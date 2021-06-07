package com.droar.ocr.redaction.api.common.model;

import java.io.Serializable;
import java.util.List;
import com.droar.ocr.redaction.api.common.exception.ExceptionDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The Class PostOcrExtractResponseDTO.
 *
 * @author droar
 */
@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostOcrExtractResponseDTO implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 4076431417065020453L;

  /** The list of pages for the detected doc. */
  @JsonProperty("fileInformation")
  private List<PageInfoDTO> lstPagesInfo;

  /** The error body. */
  private ExceptionDTO errorBody;
}
