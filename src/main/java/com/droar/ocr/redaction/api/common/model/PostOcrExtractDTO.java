package com.droar.ocr.redaction.api.common.model;

import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The Class PostOcrExtractDTO.
 *
 * @author droar
 */
@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostOcrExtractDTO {

  /** The file. */
  private MultipartFile file;

}
