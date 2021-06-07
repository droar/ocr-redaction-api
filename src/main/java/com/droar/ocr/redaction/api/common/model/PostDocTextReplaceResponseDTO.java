package com.droar.ocr.redaction.api.common.model;

import com.droar.ocr.redaction.api.common.exception.ExceptionDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The Class PostDocTextReplaceResponseDTO.
 *
 * @author droar
 */
@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostDocTextReplaceResponseDTO {

  /** The file. */
  private byte[] fileBytes;
  
  /** The error body. */
  private ExceptionDTO errorBody;
}
