package com.droar.ocr.redaction.api.common.model;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The Class PageInfoDTO.
 *
 * @author droar
 */
@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageInfoDTO implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 4076431417065020453L;

  /** The page number. */
  @JsonProperty("pageNumber")
  private String pageNumber;
  
  /** The to replace text. */
  @JsonProperty("textInformation")
  private List<TextInfoDTO> textInformation;
  
}
