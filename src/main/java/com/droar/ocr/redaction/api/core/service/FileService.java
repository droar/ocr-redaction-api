package com.droar.ocr.redaction.api.core.service;

import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;
import com.droar.ocr.redaction.api.common.model.PageInfoDTO;
import com.droar.ocr.redaction.api.common.util.tesseract.TextInfo;

public interface FileService {

  /**
   * Gets the tesseract OCR text
   * 
   * On PDF: Transforms to Images and then applies the OCR to each page.
   * On PNG, TIF, JPG: Process the image and returns the extracted text and positions
   * 
   * @param fileToProcess
   * @returns the Map page/word text info
   */
  public Map<Integer, List<TextInfo>> extractFileTextByOCR(MultipartFile fileToProcess);
  
  /**
   * Replaces the file with the word/positioning information sent
   * 
   * On PDF: Will whitebox positioning and will put the word on top of it (stamp).
   * On PNG, TIF, JPG: Will blackbox positioning (future plans on whitebox + put word too)
   * 
   * @param originalFile
   * @param lstFilePagesInfo
   * @returns the replaced file
   */
  public byte[] replaceFileTextByPosition(MultipartFile originalFile, List<PageInfoDTO> lstFilePagesInfo);
}
