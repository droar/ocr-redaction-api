package com.droar.ocr.redaction.api.web.controller;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.droar.ocr.redaction.api.common.exception.CustomException;
import com.droar.ocr.redaction.api.common.exception.ExceptionDTO;
import com.droar.ocr.redaction.api.common.model.PageInfoDTO;
import com.droar.ocr.redaction.api.common.model.PostDocTextReplaceDTO;
import com.droar.ocr.redaction.api.common.model.PostDocTextReplaceResponseDTO;
import com.droar.ocr.redaction.api.common.model.PostOcrExtractDTO;
import com.droar.ocr.redaction.api.common.model.PostOcrExtractResponseDTO;
import com.droar.ocr.redaction.api.common.util.Constants;
import com.droar.ocr.redaction.api.common.util.tesseract.TextInfo;
import com.droar.ocr.redaction.api.core.service.FileService;
import com.droar.ocr.redaction.api.web.assembler.FileResourceAssembler;
import com.droar.ocr.redaction.api.web.mapper.FileMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*",
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PATCH, RequestMethod.DELETE, RequestMethod.PUT})
@RequestMapping("/files")
public class FileController {

  /** The file service. */
  @Autowired
  private FileService fileService;

  /** The file mapper. */
  @Autowired
  private FileMapper fileMapper;

  /** The file resource assembler. */
  @SuppressWarnings("unused")
  @Autowired
  private FileResourceAssembler fileResourceAssembler;

  /**
   * Extracts text from a PDF, TIF, PNG, JPG image using Tesseract
   * 
   * @param postTesseractDTO
   * @param language
   * @return
   */
  @Operation(
      summary = "Extracts files text by OCR. "
          + "IMG (JPEG, TIFF, PNG) and PDF (transformating to IMG) are permited."
          + "Returns the coordinates in Pixels (lower-left based) and the detected words",
      responses = {
          @ApiResponse(description = "Successful Operation", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
          @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
          @ApiResponse(responseCode = "401", description = "Authentication Failure", content = @Content(schema = @Schema(hidden = true)))})
  @PostMapping(value = Constants.FileController.POST_FILE_TEXT_EXTRACT,
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PostOcrExtractResponseDTO> postOcrTextExtract(PostOcrExtractDTO postOcrExtractDTO,
      @RequestHeader(value = "Accept-Language", defaultValue = Constants.Controller.DEFAULT_LANGUAGE,
          required = false) String language) {
    log.info("[API Files]: [POST] /files/text-extract: Procesing/OCR File");

    ResponseEntity<PostOcrExtractResponseDTO> responseResult = null;

    try {
      // Variable pdf from POST
      MultipartFile fileToProcess = postOcrExtractDTO.getFile();

      // Gets the OCR text, and returns it
      Map<Integer, List<TextInfo>> mpOcrText = this.fileService.extractFileTextByOCR(fileToProcess);

      if (mpOcrText == null) {
        log.error("Coudlnt extract file text from original file");
        PostOcrExtractResponseDTO errRes = new PostOcrExtractResponseDTO();
        errRes.setErrorBody(new ExceptionDTO(Constants.ExceptionCode.BAD_REQUEST, "Coudlnt extract file text for file"));
        return ResponseEntity.badRequest().body(errRes);
      } else {
        // We map entities to DTO and we add their links
        PostOcrExtractResponseDTO dtoItem = this.fileMapper.transformToDTO(mpOcrText);

        // We assemble the HATEOAS Links (in case we need them in the future)
        // PostOcrExtractResponseDTO assembledDTO = this.fileResourceAssembler.toModel(dtoItem);

        // Response ok
        responseResult = ResponseEntity.ok(dtoItem);
        mpOcrText.clear();
      }
    } catch (CustomException e) {
      PostOcrExtractResponseDTO errRes = new PostOcrExtractResponseDTO();
      errRes.setErrorBody(new ExceptionDTO(e.getExceptionCode(), e.getMessage()));
      return ResponseEntity.status(e.getExceptionCode()).body(errRes);
    }

    return responseResult;
  }

  /**
   * Edits provided word-position text inside a file Supported formats are PDF, TIF, PNG, JPG
   * 
   * @param postOcrExtractDTO
   * @param language
   * @return
   */
  @Operation(
      summary = "Replaces the text and coordinates (In Pixels) provided in the postReplaceInfo parameter, in the provided file. "
          + "Note that the coordinates are taken from lower-left based for PDF documents and upper-left on Images",
      responses = {
          @ApiResponse(description = "Successful Operation", responseCode = "200",content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
          @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
          @ApiResponse(responseCode = "401", description = "Authentication Failure", content = @Content(schema = @Schema(hidden = true)))})
  @PostMapping(value = Constants.FileController.POST_FILE_TEXT_REDACT,
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PostDocTextReplaceResponseDTO> postDocTextReplace(
      @RequestPart(value = "file", required = true) MultipartFile file,
      @RequestPart(value = "replaceInformation", required = true) PostDocTextReplaceDTO postReplaceInfo,
      @RequestHeader(value = "Accept-Language", defaultValue = Constants.Controller.DEFAULT_LANGUAGE,
          required = false) String language) {
    log.info("[API Files]: [POST] /files/text-redact: Editing file");

    ResponseEntity<PostDocTextReplaceResponseDTO> responseResult = null;

    try {
      // Gets the info to replace and the original file
      List<PageInfoDTO> lstDocPagesInfo = postReplaceInfo.getLstReplaceInfo();
      MultipartFile originalFile = file;

      // Replaces the original file with the word info-positioning
      byte[] docBytes = this.fileService.replaceFileTextByPosition(originalFile, lstDocPagesInfo);

      if (docBytes == null) {
        log.error("Coudlnt edit file text on the original file");
        PostDocTextReplaceResponseDTO errRes = new PostDocTextReplaceResponseDTO();
        errRes.setErrorBody(new ExceptionDTO(Constants.ExceptionCode.BAD_REQUEST,
            "Coudlnt edit file text on the original file"));
        return ResponseEntity.badRequest().body(errRes);
      } else {
        // We map entities to DTO and we add their links
        PostDocTextReplaceResponseDTO dtoItem = this.fileMapper.transformToDTO(docBytes);

        // We assemble the HATEOAS Links (in case we need them in the future)
        // PostOcrExtractResponseDTO assembledDTO = this.fileResourceAssembler.toModel(dtoItem);

        // Response ok
        responseResult = ResponseEntity.ok(dtoItem);
      }
    } catch (CustomException e) {
      PostDocTextReplaceResponseDTO errRes = new PostDocTextReplaceResponseDTO();
      errRes.setErrorBody(new ExceptionDTO(e.getExceptionCode(), e.getMessage()));
      return ResponseEntity.status(e.getExceptionCode()).body(errRes);
    }

    return responseResult;
  }
}
