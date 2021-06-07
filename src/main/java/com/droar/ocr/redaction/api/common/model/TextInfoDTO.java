package com.droar.ocr.redaction.api.common.model;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The Class PostTesseract.
 *
 * @author droar
 */
@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TextInfoDTO implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 4076431417065020453L;

  /** The txt. */
  @JsonProperty("wordText")
  private String wordText;

  /** The pos X. */
  @JsonProperty("posX")
  private String posX;

  /** The pos Y. */
  @JsonProperty("posY")
  private String posY;

  /** The pos X Right. */
  @JsonProperty("posXR")
  private String posXR;

  /** The pos Y Right. */
  @JsonProperty("posYR")
  private String posYR;
  
}
