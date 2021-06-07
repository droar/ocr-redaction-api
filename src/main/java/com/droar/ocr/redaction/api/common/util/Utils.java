package com.droar.ocr.redaction.api.common.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;
import com.droar.ocr.redaction.api.common.exception.CustomException;
import com.droar.ocr.redaction.api.common.model.PageInfoDTO;
import com.droar.ocr.redaction.api.common.model.TextInfoDTO;
import com.droar.ocr.redaction.api.common.util.tesseract.TextInfo;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.pdfcleanup.PdfCleanUpLocation;
import com.itextpdf.pdfcleanup.PdfCleanUpTool;
import boofcv.abst.denoise.FactoryImageDenoise;
import boofcv.abst.denoise.WaveletDenoiseFilter;
import boofcv.alg.enhance.EnhanceImageOps;
import boofcv.alg.filter.binary.GThresholdImageOps;
import boofcv.alg.misc.ImageStatistics;
import boofcv.gui.binary.VisualizeBinaryData;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.GrayU8;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author droar
 *
 */
@Slf4j
public class Utils {

  /**
   * The Constant to convert pixels to points.
   */
  private static final float PX_TO_PT = 3F / 4F;

  /**
   * Gets a document mime type
   * 
   * @param document
   * @return
   */
  public static String getFileMimeType(MultipartFile document) {
    String documentType = "";
    // The mimeType of the document
    if (document != null) {
      try (InputStream io = document.getInputStream()) {
        documentType = new Tika().detect(io);
      } catch (IOException e) {
        log.error("Document is not of a valid mime type.");
      }
    }

    return documentType;
  }


  /**
   * Redact document (PDF as of now) with the text replaces (coordinates)
   *
   * @param originalFile the original file
   * @param lstDocPagesInfo the lst doc pages info
   * @return the byte[] of the endfile.
   * @throws IOException
   * @throws FileNotFoundException
   */
  public static byte[] redactDocumentReplaces(File originalFile, List<PageInfoDTO> lstDocPagesInfo)
      throws IOException {
    byte[] resultBytes = null;
    
    // Temporary out file
    String tempPath = originalFile.getPath().substring(0, originalFile.getPath().lastIndexOf(File.separator) + 1);
    File outFile = new File(tempPath.concat("TMP_").concat(originalFile.getName()));

    try (PdfReader reader = new PdfReader(originalFile.getPath()) ; 
        PdfWriter writer = new PdfWriter(outFile) ; 
        PdfDocument pdf = new PdfDocument(reader, writer)) {
      // Does PDF Edition
      List<PdfCleanUpLocation> cleanUpLocations = new ArrayList<>();
      
      for (PageInfoDTO pageInfo : lstDocPagesInfo) {
        Integer pageNum = Integer.parseInt(pageInfo.getPageNumber());
        List<TextInfoDTO> lstReplaces = pageInfo.getTextInformation();

        for (TextInfoDTO textInfo : lstReplaces) {
          // edit image - example use defaults to add text at point 25x, 25y
          // You could add some more to choose typeface, size, attributes, color, style and more
          Float posX = Float.parseFloat(textInfo.getPosX());
          Float posY = Float.parseFloat(textInfo.getPosY());
          Float posXR = Float.parseFloat(textInfo.getPosXR());
          Float posYR = Float.parseFloat(textInfo.getPosYR());

          // We calculate width and height, needed to paint
          Float width = posXR - posX;
          Float height = posYR - posY;

          // The arguments of the PdfCleanUpLocation constructor: the number of page to be cleaned up,
          // a Rectangle defining the area on the page we want to clean up,
          // a color which will be used while filling the cleaned area.
          PdfCleanUpLocation location = new PdfCleanUpLocation(pageNum, new Rectangle(posX, posY, width, height), ColorConstants.BLACK);
          cleanUpLocations.add(location);
        }
      }
      PdfCleanUpTool cleaner = new PdfCleanUpTool(pdf, cleanUpLocations);
      cleaner.cleanUp();
      lstDocPagesInfo.clear();
    } catch (Exception e) {
      log.error("Error happened when redacting DOCUMENT file: " + e.getMessage());
      throw new CustomException(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Error happened when redacting DOCUMENT file: " + e.getMessage());
    }
    
    // We extract the bytes
    resultBytes = FileUtils.readFileToByteArray(outFile);
    
    // We dispose of the file
    FileUtils.deleteQuietly(outFile);

    return resultBytes;
  }

  /**
   * Redact IMG (PNG, TIFF, JPEG) with the text replaces (coordinates)
   *
   * @param originalFile the original file
   * @param lstDocPagesInfo the lst doc pages info
   * @return the byte[] of the endfile.
   * @throws IOException
   * @throws FileNotFoundException
   */
  public static byte[] redactImageReplaces(File originalFile, String documentType, List<PageInfoDTO> lstDocPagesInfo) throws IOException {
    byte[] resultBytes = null;
    
    // Temporary out file
    String tempPath = originalFile.getPath().substring(0, originalFile.getPath().lastIndexOf(File.separator) + 1);
    File outFile = new File(tempPath.concat("TMP_").concat(originalFile.getName()));
    
    // If we have blank extension we cant do nothing
    if (StringUtils.isNotBlank(documentType)) {
      
      // Tiff has diferent treatment (its multipage image file)
      if (documentType.equalsIgnoreCase(Constants.MimeType.TIFF)) {
        Utils.doWriteMultImageReplaces(originalFile, lstDocPagesInfo, outFile);
      } else {
        // We need the extension type for saving it later
        String extensionType = documentType.substring((documentType.lastIndexOf("/") + 1), documentType.length());
        Utils.doWriteSingleImageReplaces(originalFile, extensionType, lstDocPagesInfo, outFile);
      }
      lstDocPagesInfo.clear();
    }
    
    // We extract the bytes
    resultBytes = FileUtils.readFileToByteArray(outFile);
    
    // We dispose of the file
    FileUtils.deleteQuietly(outFile);

    return resultBytes;
  }
  
  /**
   * We have to reset the coordinates to the original PDF dpi (asuming 72 as of now).
   * 
   * @param pt
   * @return
   */
  public static List<TextInfo> reescaleFinalCoordinates(List<TextInfo> lstTextInfo, Float dpiOriMult) {
    if (CollectionUtils.isNotEmpty(lstTextInfo)) {
      for (TextInfo txtInfo : lstTextInfo) {
        if (txtInfo.getBboxRect() != null)
          txtInfo.setBboxRect(new Rectangle(txtInfo.getBboxRect().getX() * dpiOriMult, txtInfo.getBboxRect().getY() * dpiOriMult, txtInfo.getBboxRect().getWidth() * dpiOriMult, txtInfo.getBboxRect().getHeight() * dpiOriMult));
      }
    }
    return lstTextInfo;
  }

  /**
   * Converts points to pixels.
   */
  public static float toPixel(float pt) {
    return pt / PX_TO_PT;
  }
    
  // Private methods beyond this point (used by this util class)
  
  /**
   * Single image file writer (jpeg, png)
   * 
   * @param originalFile
   * @param documentType
   * @param lstDocPagesInfo
   * @param fos
   * @throws IOException
   */
  private static void doWriteSingleImageReplaces(File originalFile, String extensionType, List<PageInfoDTO> lstDocPagesInfo,
      File outFile) throws IOException {
    
    try (FileOutputStream fos = new FileOutputStream(outFile)) {
      // Does IMG Edition
      BufferedImage in = ImageIO.read(originalFile);
      
      PageInfoDTO pageInfoDTO = lstDocPagesInfo.stream().findFirst().orElse(null); // We only have 1 page, its an image
      
      if (pageInfoDTO != null) {
        // We do the image replaces
        Utils.doImageReplaces(pageInfoDTO.getTextInformation(), in);
      }

      // Use ImageIO to save a PNG
      ImageIO.write(in, extensionType, fos);
    } catch (Exception e) {
      log.error("Error happened when redacting IMG file: " + e.getMessage());
      throw new CustomException(HttpStatus.SC_INTERNAL_SERVER_ERROR,
          "Error happened when redacting IMG file: " + e.getMessage());
    }
  }
  
  /**
   * Tiff file writer/replacer
   * 
   * @param originalFile
   * @param documentType
   * @param lstDocPagesInfo
   * @param fos
   * @throws IOException
   */
  private static void doWriteMultImageReplaces(File originalFile, List<PageInfoDTO> lstDocPagesInfo, File outFile)
      throws IOException {
    try (FileOutputStream fos = new FileOutputStream(outFile);
        ImageInputStream is = ImageIO.createImageInputStream(originalFile)) {
      // Does IMG Edition
      ImageReader reader = ImageIO.getImageReaders(is).next();

      // We are just looking for the first reader compatible:
      reader.setInput(is);

      // Obtain a TIFF writer
      ImageWriter writer = ImageIO.getImageWriter(reader);

      try (ImageOutputStream output = ImageIO.createImageOutputStream(fos)) {
        writer.setOutput(output);

        ImageWriteParam params = writer.getDefaultWriteParam();

        // Compression mode and type
        params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        params.setCompressionType("Deflate");
        writer.prepareWriteSequence(null); // We have to call it before iterating

        for (PageInfoDTO pageInfo : lstDocPagesInfo) {

          if (StringUtils.isNotBlank(pageInfo.getPageNumber()) && CollectionUtils.isNotEmpty(pageInfo.getTextInformation())) {
            Integer numbCalcPage = Integer.valueOf(pageInfo.getPageNumber()) - 1; // pageNumber is 1 based, we have to
                                                                                  // rest it
            BufferedImage in = reader.read(numbCalcPage);

            // We do the image replaces
            Utils.doImageReplaces(pageInfo.getTextInformation(), in);

            // Use ImageIO to save a PNG
            writer.writeToSequence(new IIOImage(in, null, null), params);
          }
        }

        // We're done
        writer.endWriteSequence();
      }

      // We dispose after writing
      writer.dispose();

    } catch (Exception e) {
      log.error("Error happened when redacting IMG file: " + e.getMessage());
      throw new CustomException(HttpStatus.SC_INTERNAL_SERVER_ERROR,
          "Error happened when redacting IMG file: " + e.getMessage());
    }
  }

  /**
   * @param lstDocPagesInfo
   * @param in
   */
  private static void doImageReplaces(List<TextInfoDTO> lstReplaces, BufferedImage in) {
    Graphics2D g2d = in.createGraphics();
    g2d.setColor(Color.BLACK);

    for (TextInfoDTO textInfo : lstReplaces) {
      // edit image
      Float posX = Float.parseFloat(textInfo.getPosX());
      Float posXR = Float.parseFloat(textInfo.getPosXR());
      Float posY = in.getHeight() - Float.parseFloat(textInfo.getPosYR()); // Images are upper-left coordinated, we
                                                                           // have to rest the HEIGHT, and invert it
      Float posYR = in.getHeight() - Float.parseFloat(textInfo.getPosY());

      // We calculate width and height, needed to paint
      Float width = posXR - posX;
      Float height = posYR - posY;

      // We fill or 'redact'
      g2d.fillRect(posX.intValue(), posY.intValue(), width.intValue(), height.intValue());
    }
    
    // release the drawing context
    g2d.dispose();
  }
  
  /**
   * Adjusts the image quality for OCR Processing 
   * 
   * @param img
   * @return
   */
  public static BufferedImage adjustImgThresold(BufferedImage img) {
    // convert into a usable format for thresolding
    GrayF32 input = ConvertBufferedImage.convertFromSingle(img, null, GrayF32.class);
    GrayU8 binary = new GrayU8(input.width,input.height);
    
    // Adaptive / thresold
    GThresholdImageOps.threshold(input, binary, GThresholdImageOps.computeOtsu2(input, 0, 255), false);

    img = VisualizeBinaryData.renderBinary(binary, false, null);
    return img;
  }
  
  /**
   * Adjusts the image sharpness for OCR Processing 
   * 
   * @param img
   * @return
   */
  public static BufferedImage adjustImgSharpness(BufferedImage img) {
    GrayU8 gray = ConvertBufferedImage.convertFrom(img, (GrayU8) null);
    GrayU8 adjusted = gray.createSameShape();
    EnhanceImageOps.sharpen8(gray, adjusted);
    
    img = ConvertBufferedImage.convertTo(adjusted,null);
    return img;
  }
  
  /**
   * Adjusts the image sharpness for OCR Processing 
   * 
   * @param img
   * @return
   */
  public static BufferedImage reduceImageNoise(BufferedImage img) {
 // load the input image, declare data structures, create a noisy image
    GrayF32 noisy = ConvertBufferedImage.convertFrom(img,(GrayF32)null);
    GrayF32 denoised = noisy.createSameShape();

    // How many levels in wavelet transform
    int numLevels = 4;
    // Create the noise removal algorithm
    WaveletDenoiseFilter<GrayF32> denoiser = FactoryImageDenoise.waveletBayes(GrayF32.class,numLevels,0,255);

    // remove noise from the image
    denoiser.process(noisy,denoised);

    img = ConvertBufferedImage.convertTo(denoised,null);
    return img;
  }
  
  /**
   * Histogram improvement
   * 
   * @param img
   * @return
   */
  public static BufferedImage adjunstHistogram(BufferedImage img) {
    GrayU8 gray = ConvertBufferedImage.convertFrom(img,(GrayU8)null);
    GrayU8 adjusted = gray.createSameShape();

    int histogram [] = new int[256];
    int transform [] = new int[256];

    ImageStatistics.histogram(gray,0, histogram);
    EnhanceImageOps.equalize(histogram, transform);
    EnhanceImageOps.applyTransform(gray, transform, adjusted);
    EnhanceImageOps.equalizeLocal(gray, 50, adjusted, 256, null);
    
    img = ConvertBufferedImage.convertTo(adjusted,null);

    return img;
  }
 
}
