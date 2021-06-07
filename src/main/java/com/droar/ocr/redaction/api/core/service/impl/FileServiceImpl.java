package com.droar.ocr.redaction.api.core.service.impl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.droar.ocr.redaction.api.common.exception.CustomException;
import com.droar.ocr.redaction.api.common.model.PageInfoDTO;
import com.droar.ocr.redaction.api.common.util.Utils;
import com.droar.ocr.redaction.api.common.util.Constants.MimeType.AcceptedMimeTypes;
import com.droar.ocr.redaction.api.common.util.tesseract.TextInfo;
import com.droar.ocr.redaction.api.core.service.FileService;
import com.droar.ocr.redaction.api.core.service.OCRService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FileServiceImpl implements FileService {

  /** The message source. */
  @Autowired
  private ReloadableResourceBundleMessageSource messageSource;

  /** The ocr service. */
  @Autowired
  private OCRService ocrService;

  /** The tmp work dir. */
  @Value("${tmp.dir}")
  private String tmpWorkDir;

  @Override
  public Map<Integer, List<TextInfo>> extractFileTextByOCR(MultipartFile fileToProcess) {
    // The extracted OCR text
    Map<Integer, List<TextInfo>> mpOcrResults = null;
    
    // file path
    Path tmpFolder = Path.of(tmpWorkDir.concat(UUID.randomUUID().toString()));

    try {
      // We have to validate if this format is valid for OCRing
      String fileType = Utils.getFileMimeType(fileToProcess);
 
      Files.createDirectories(tmpFolder);
      
      // We transfer the file
      File inFile = new File(tmpFolder.toString().concat(File.separator.concat(fileToProcess.getOriginalFilename())));
      fileToProcess.transferTo(inFile);

      log.info("***  Entering OCR process on file, detected type is : " + fileType + " ***");

      if (AcceptedMimeTypes.DOCUMENT.containsType(fileType)) {
        // Does PDF Extraction
        mpOcrResults = this.ocrService.doPdfOcrExtraction(inFile);
      } else if (AcceptedMimeTypes.IMAGE.containsType(fileType)) {
        // Does IMG Extraction
        mpOcrResults = this.ocrService.doImageOcrExtraction(inFile);
      }

    } catch (Exception e) {
      log.error("Error trying to extract the text file with OCR : " + e.getMessage());
      throw new CustomException(HttpStatus.SC_INTERNAL_SERVER_ERROR,
          this.messageSource.getMessage("msg.error.extract", null, LocaleContextHolder.getLocale()) + ", reason: " + e.getMessage());
    } finally {
      // We delete the temp file
      FileUtils.deleteQuietly(tmpFolder.toFile());
    }

    // Returns the resultant text.
    return mpOcrResults;
  }

  @Override
  public byte[] replaceFileTextByPosition(MultipartFile originalFile, List<PageInfoDTO> lstFilePagesInfo) {
    // Replaced doc
    byte[] resultBytes = null;
    
    // Tmp work folder
    Path tmpFolder = Path.of(tmpWorkDir.concat(UUID.randomUUID().toString()));

    try {
      // We have to validate if this format is valid for OCRing
      String fileType = Utils.getFileMimeType(originalFile);

      // file path
      Files.createDirectories(tmpFolder);
      
      // We transfer the file
      File file = new File(tmpFolder.toString().concat(File.separator.concat(originalFile.getOriginalFilename())));
      originalFile.transferTo(file);

      log.info("***  Redacting File, detected type is : " + fileType + " ***");

      // PDF and image replaces
      if (AcceptedMimeTypes.DOCUMENT.containsType(fileType)) {
        resultBytes = Utils.redactDocumentReplaces(file, lstFilePagesInfo);
      } else if (AcceptedMimeTypes.IMAGE.containsType(fileType)) {
        resultBytes = Utils.redactImageReplaces(file, fileType, lstFilePagesInfo);
      }
      
      log.info("***  Finished redaction process! ***");
    } catch (Exception e) {
      log.error("Error trying to edit the file text : " + e.getMessage());
      throw new CustomException(HttpStatus.SC_INTERNAL_SERVER_ERROR,
          this.messageSource.getMessage("msg.error.edit", null, LocaleContextHolder.getLocale()) + ", reason: " + e.getMessage());
    } finally {
      // We delete the temp file
      FileUtils.deleteQuietly(tmpFolder.toFile());
    }

    // Returns the resultant text.
    return resultBytes;
  }
}
