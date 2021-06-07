package com.droar.ocr.redaction.api.core.service;

import java.io.File;
import java.util.List;
import java.util.Map;
import com.droar.ocr.redaction.api.common.util.tesseract.TextInfo;

public interface OCRService {

  /**
   * Does PDF OCR Text extraction
   * 
   * @param inFile
   * @return
   */
  public Map<Integer, List<TextInfo>> doPdfOcrExtraction(File inFile);
  
  /**
   * Does Image OCR Text extraction
   * This method is called from @doPdfOcrExtraction (after rendering the PDF to images)
   * 
   * Note that this method is SYNCRONIZED, meaning it will run thread-safe due to
   * the actual tesseract engine not being multi thread safe.
   * 
   * @param inFile
   * @return
   */
  public Map<Integer, List<TextInfo>> doImageOcrExtraction(File inImageFile);
}
