package com.droar.ocr.redaction.api.common.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import com.droar.ocr.redaction.api.common.util.Constants;

/***
 * Generic exception handler**
 * 
 * @author droar
 *
 */
@Component
public class ExceptionHandler {

  /** The message resource. */
  @Autowired
  private ReloadableResourceBundleMessageSource messageSource;

  /**
   * Gets the http exception.
   *
   * @param status the status
   * @return the http exception
   */
  public ResponseStatusException getHttpException(HttpStatus status) {
    return new ResponseStatusException(status, resolveHttpExceptionMessage(status.value(), null));
  }

  /**
   * Gets the http exception.
   *
   * @param status the status
   * @return the http exception
   */
  public ResponseStatusException getHttpException(HttpStatus status, Integer codError) {
    return new ResponseStatusException(status, resolveHttpExceptionMessage(status.value(), codError));
  }

  /**
   * Gets the http exception.
   *
   * @param status the status
   * @param message the message
   * @return the http exception
   */
  public ResponseStatusException getHttpException(HttpStatus status, String message) {
    return new ResponseStatusException(status, getMessage(message));
  }

  /**
   * Resolve http exception message.
   *
   * @param status the status
   * @return the string
   */
  private String resolveHttpExceptionMessage(int status, Integer codError) {
    String message = "";
    switch (status) {
      case Constants.ExceptionCode.CODE_BAD_REQUEST:
        message = getMessage("http.bad_request");
        break;
      case Constants.ExceptionCode.CODE_UNAUTHORIZED:
        message = getMessage("http.unauthorized");
        break;
      case Constants.ExceptionCode.CODE_FORBIDDEN:
        message = getMessage("http.forbidden");
        break;
      case Constants.ExceptionCode.CODE_NOT_FOUND:
        message = getMessage("http.not_found");
        break;
      case Constants.ExceptionCode.CODE_INTERNAL_SERVER_ERROR:
        message = getMessage("http.internal_server_error");
        break;
      case Constants.ExceptionCode.CODE_GATEWAY_TIMEOUT:
        message = getMessage("http.gateway_timeout");
        break;
      case Constants.ExceptionCode.CODE_UNPROCESSABLE_ENTITY:
        message = getMessage("http.unprocessable_entity_1");
        break;
      default:
        message = getMessage("http.internal_server_error");
        break;
    }
    return message;
  }

  /**
   * Gets the message.
   *
   * @param message the message
   * @return the message
   */
  private String getMessage(String message) {
    return this.messageSource.getMessage(message, null, LocaleContextHolder.getLocale());
  }

}
