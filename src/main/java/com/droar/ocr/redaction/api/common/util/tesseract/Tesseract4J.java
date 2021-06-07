package com.droar.ocr.redaction.api.common.util.tesseract;

import java.io.File;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import lombok.Data;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;


/**
 * The Class Tesseract4J.
 */
@Data
@Component
public class Tesseract4J {

  @Value("${tesseract.ocr.langs}")
  private String arrOcrLangs;
  
  @Value("${tesseract.ocr.seg}")
  private Integer ocrSegMode;
  
  @Value("${tesseract.ocr.engine}")
  private Integer ocrEngineMode;
  
  @Value("${tesseract.ocr.hocr.enable}")
  private Boolean ocrHocrMode;

  @Value("${tesseract.data.folder}")
  private String tessDataFolder;

  /**
   * Do ocr.
   *
   * @param inImageFile the in image file
   * @return the string
   * @throws TesseractException 
   */
  public String doOcr(Tesseract ocrEngine, File inImageFile) throws TesseractException {
    String ocrResults = "";
    
    if (ocrEngine != null && inImageFile != null) {
      ocrEngine.setHocr(this.ocrHocrMode);
      ocrEngine.setTessVariable("tessedit_do_invert", "0"); // Stated by tesseract, it improves performance
      ocrEngine.setPageSegMode(this.ocrSegMode); // Automatic page segmentation with OSD - rotation detection
      ocrEngine.setOcrEngineMode(this.ocrEngineMode);
      ocrEngine.setDatapath(this.tessDataFolder);
      ocrEngine.setLanguage(this.arrOcrLangs); // if needed change properties file
      ocrEngine.setTessVariable("user_defined_dpi", "300"); // Default user defined dpi
      ocrEngine.setTessVariable("preserve_interword_spaces", "1"); // Default user defined dpi
      ocrResults = ocrEngine.doOCR(inImageFile);
    }
    
    return ocrResults;
  }
  
}
