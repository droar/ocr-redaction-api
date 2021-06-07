package com.droar.ocr.redaction.api.common.model;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The Class PostDocTextReplaceDTO.
 *
 * @author droar
 */
@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostDocTextReplaceDTO implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = -6649469455279051828L;
  
  /** The list of pages for the detected doc. */
  @JsonProperty(value = "replaceInformation")
  private List<PageInfoDTO> lstReplaceInfo;
}
