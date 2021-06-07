package com.droar.ocr.redaction.api.core.service.impl;

import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import javax.imageio.ImageIO;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpStatus;
import org.apache.pdfbox.debugger.ui.ImageUtil;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.droar.ocr.redaction.api.common.exception.CustomException;
import com.droar.ocr.redaction.api.common.util.Utils;
import com.droar.ocr.redaction.api.common.util.tesseract.Tesseract4J;
import com.droar.ocr.redaction.api.common.util.tesseract.TesseractUtils;
import com.droar.ocr.redaction.api.common.util.tesseract.TextInfo;
import com.droar.ocr.redaction.api.common.util.tesseract.TextPositioning;
import com.droar.ocr.redaction.api.core.service.OCRService;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;


/**
 * The Class TesseractUtils.
 */
@Slf4j
@Service
public class OCRServiceImpl implements OCRService {

  /** The Constant C_DEFAULT_PDF_DPI. */
  private static final int C_DEFAULT_PDF_DPI = 72;

  @Value("${tesseract.pdf.img.render.dpi:150}") // Default value in case null = 150 dpi
  private Integer dpisToRender;
  
  @Value("${tesseract.parallelism.level:0}") // Default value in case null = 2
  private Integer parallelism;

  @Autowired
  private Tesseract4J ocrInstance;

  @Override
  public Map<Integer, List<TextInfo>> doPdfOcrExtraction(File inFile) {
    Map<Integer, List<TextInfo>> mpOcrResults = new HashMap<>();

    try (PDDocument pdfDocument = PDDocument.load(inFile, MemoryUsageSetting.setupTempFileOnly())) {
      // PDFRenderer class to be Instantiated i.e. creating it's object
      PDFRenderer pdfRenderer = new PDFRenderer(pdfDocument);
      
      Map<RenderingHints.Key, Object> renderOptions = new HashMap<>();
      
      renderOptions.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
      renderOptions.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
      renderOptions.put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
      renderOptions.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

      pdfRenderer.setRenderingHints(new RenderingHints(renderOptions));
      
      // The image work path
      String imgWorkPath = inFile.getPath().substring(0, inFile.getPath().lastIndexOf(File.separator));

      log.info("[DOCUMENT OCR] - *** Starting OCR on the provided PDF. -" + pdfDocument.getNumberOfPages() + "- pages will be rendered and processed *** -"); 
      
      log.info("[Rendering] - Rendering a total of " + pdfDocument.getNumberOfPages() + " pages (rendering at: " + dpisToRender + " DPI)");
      
      List<Pair<Integer, File>> lstToOCR = new ArrayList<>();
      
      for (Integer i = 0; i < pdfDocument.getNumberOfPages(); i++) { 
        // Physical page num 
        Integer phyPageNum = i + 1; 
        
        // Rendering an image from the PDF document using BufferedImage class -> Scale is set to xdpi 
        // better quality, can be higher. Recomended for PDF 
        BufferedImage originalImage = pdfRenderer.renderImageWithDPI(i, this.dpisToRender, ImageType.BINARY); 
        
        // We have to adjust the rotation in case the original document is rotated
        Integer rotationAdjust = (360 - pdfDocument.getPage(i).getRotation());
        originalImage = Utils.adjustImgThresold(ImageUtil.getRotatedImage(originalImage, rotationAdjust));
        
        // Writing the extracted image to a new file 
        File toRenderImage = new File(imgWorkPath + File.separator + inFile.getName().concat("_") + (phyPageNum) + ".png"); 
        ImageIO.write(originalImage, "png", toRenderImage); 
        originalImage.flush(); 
         
        lstToOCR.add(Pair.of(phyPageNum, toRenderImage));
      } 
      
      // Start Time
      LocalDateTime startTime = LocalDateTime.now();
      
      log.info("*** Initializing ForkJoin : parallelism level of: - " + this.parallelism + " - detected from prop. variable ****");
      ForkJoinPool forkPool = (this.parallelism > 0 ? new ForkJoinPool(this.parallelism) : new ForkJoinPool());
      
      // We instantiate a paralel fork with the env defined property (if any)
      forkPool.submit(() -> lstToOCR.parallelStream().forEach(ocrPair -> {  
        
        try { 
          
          //G ets the pair values for process
          Integer phyPageNum = ocrPair.getKey();
          File toRenderImage = ocrPair.getValue();
          
          // For each image/page we do OCR 
          log.info("[OCR] - OCR on Page: " + (phyPageNum) + " of: " + pdfDocument.getNumberOfPages());
          String hocrText = (ocrInstance.doOcr(new Tesseract(), toRenderImage)); 
           
          // If we have results we will parse them by word 
          if (StringUtils.isNotBlank(hocrText)) { 
            Map<Integer, List<TextInfo>> mpParcedHocr = TesseractUtils.parseHocrFile(hocrText, TextPositioning.BY_WORDS);
   
            // We are asuming original DPI of PDFS to 72, might change and recalculation will be needed
            Integer originalDPI = C_DEFAULT_PDF_DPI;
            
            // we are hovering a page, single img, it will always have 1 page OCR
            List<TextInfo> lstRescaledTxt = Utils.reescaleFinalCoordinates(mpParcedHocr.get(1), ((float) originalDPI / this.dpisToRender));
            mpOcrResults.put((phyPageNum), lstRescaledTxt);
            
            // We clear the work map
            mpParcedHocr.clear();
          } 
          
          // We delete the render to work image, we dont need it anymore 
          FileUtils.deleteQuietly(toRenderImage); 
        }catch (TesseractException | IOException e){ 
          log.error("Error has happened when extracting text with OCR: " + e.getMessage());
          throw new CustomException(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Error has happened when extracting text with OCR: " + e.getMessage());
        } 
      })).join();
      
      // We shutdown the pool for now
      forkPool.shutdown();
      
      // We delete the ocrList
      lstToOCR.clear();
      
      // End Time
      LocalDateTime endTime = LocalDateTime.now();
      
      log.info("[DOCUMENT OCR] - *** OCR Process ended! The OCR process took : " + (startTime.until(endTime, ChronoUnit.SECONDS)) + " second(s) *** -");
      
    } catch (Exception e) {
      log.error("Error has happened when extracting text with OCR: " + e.getMessage());
      throw new CustomException(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Error has happened when extracting text with OCR: " + e.getMessage());
    }

    return mpOcrResults;
  }

  @Override
  public Map<Integer, List<TextInfo>> doImageOcrExtraction(File inImageFile) {
    Map<Integer, List<TextInfo>> mpOcrResults = new HashMap<>();

    try {
      log.info("[IMAGE OCR] - *** Starting OCR on the provided IMG *** -");
      
      // Start time
      LocalDateTime startTime = LocalDateTime.now();

      // For each image/page we do OCR
      String hocrText = (ocrInstance.doOcr(new Tesseract(), inImageFile));
      
      // If we have results we will parse them by word
      if(StringUtils.isNotBlank(hocrText)) mpOcrResults = TesseractUtils.parseHocrFile(hocrText, TextPositioning.BY_WORDS);
      
      // End Time
      LocalDateTime endTime = LocalDateTime.now();
      
      log.info("[IMAGE OCR] - *** OCR Process ended! The OCR process took : " + (startTime.until(endTime, ChronoUnit.SECONDS)) + " second(s) *** -");
    } catch (Exception e) {
      log.error("Error has happened when extracting IMG text with OCR: " + e.getMessage());
      throw new CustomException(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Error has happened when extracting IMG text with OCR: " + e.getMessage());
    }

    return mpOcrResults;
  }
}

