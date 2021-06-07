package com.droar.ocr.redaction.api.common.exception;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @author droar
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CustomException extends RuntimeException implements Serializable {
  /**
  * 
  */
  private static final long serialVersionUID = -5616443777678335597L;

  /** The exception code. */
  private Integer exceptionCode;

  /** The error message. */
  private String errorMessage;

  public CustomException(Integer exceptionCode, String errorMessage) {
    super(errorMessage);
    this.exceptionCode = exceptionCode;
  }
}
