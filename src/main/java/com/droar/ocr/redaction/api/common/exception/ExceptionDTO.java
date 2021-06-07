package com.droar.ocr.redaction.api.common.exception;

import java.io.Serializable;
import lombok.Builder;
import lombok.Getter;

/**
 * 
 * @author droar
 *
 */
@Getter
@Builder
public class ExceptionDTO implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -5102870025771819990L;
  
  /** The error code. */
  private Integer errorCode;
  
  /** The message. */
  private String message;

  /**
   * Instantiates a new exception response.
   *
   * @param codigo the codigo
   * @param mensaje the mensaje
   */
  public ExceptionDTO(int codigo, String mensaje) {
    this.errorCode = codigo;
    this.message = mensaje;
  }

  /**
   * Instantiates a new exception response.
   */
  public ExceptionDTO() {}

}
