package com.droar.ocr.redaction.api.common.util;

import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;

/**
 * Constants to be used for the whole project.
 *
 * @author droar
 */
public class Constants {

  /**
   * Instantiates a new constants.
   */
  private Constants() {
    // private Constructor
  }

  /** The Constant TIMEOUT. */
  public static final int QUERY_TIMEOUT = 1000;

  /** The Constant EMBEDDED. */
  public static final String JSON_EMBEDDED = "_embedded";

  /** The Constant JSON_ITEMS. */
  public static final String JSON_ITEMS = "items";
  
  /**
   * The Class Controller.
   */
  public static class Controller {

    /**
     * Instantiates a new controller.
     */
    private Controller() {
      // private Constructor
    }

    /** The Constant DEFAULT_LANGUAGE. */
    public static final String DEFAULT_LANGUAGE = "es";

    /** The Constant DEFAULT_LIMIT. */
    public static final String DEFAULT_LIMIT = "20";

    /** The Constant DEFAULT_OFFSET. */
    public static final String DEFAULT_OFFSET = "0";
  }

  /**
   * The Class CommonExpressions.
   */
  public static class CommonExpressions {

    /**
     * Instantiates a new CommonExpressions.
     */
    private CommonExpressions() {
      // private Constructor
    }

    /** The Constant YES_CAPITAL_CHAR. */
    public static final String YES_CAPITAL_CHAR = "S";

    /** The Constant NO_CAPITAL_CHAR. */
    public static final String NO_CAPITAL_CHAR = "N";

    /** The Constant LANG_ES. */
    public static final String LANG_ES = "es";

    /** The Constant LANG_EU. */
    public static final String LANG_EU = "eu";

    /** The Constant ZERO_STRING. */
    public static final String ZERO_STRING = "0";

  }


  /**
   * Exception codes.
   */
  public static class ExceptionCode {

    /**
     * Instantiates a new exceptionCode.
     */
    private ExceptionCode() {
      // private Constructor
    }

    /** The Constant CODE_BAD_REQUEST. */
    public static final int CODE_BAD_REQUEST = 400;

    /** The Constant CODE_UNAUTHORIZED. */
    public static final int CODE_UNAUTHORIZED = 401;

    /** The Constant CODE_FORBIDDEN. */
    public static final int CODE_FORBIDDEN = 403;

    /** The Constant CODE_NOT_FOUND. */
    public static final int CODE_NOT_FOUND = 404;

    /** The Constant CODE_UNPROCESSABLE_ENTITY. */
    public static final int CODE_UNPROCESSABLE_ENTITY = 422;

    /** The Constant CODE_INTERNAL_SERVER_ERROR. */
    public static final int CODE_INTERNAL_SERVER_ERROR = 500;

    /** The Constant CODE_GATEWAY_TIMEOUT. */
    public static final int CODE_GATEWAY_TIMEOUT = 504;

    /** The Constant BAD_REQUEST. */
    public static final int BAD_REQUEST = 1;

    /** The Constant NOT_FOUND. */
    public static final int NOT_FOUND = 2;

    /** The Constant FORBIDDEN. */
    public static final int FORBIDDEN = 3;

    /** The Constant INTERNAL_SERVER_ERROR. */
    public static final int INTERNAL_SERVER_ERROR = 4;

    /** The Constant UNAUTHORIZED. */
    public static final int UNAUTHORIZED = 5;

    /** The Constant UNAUTHORIZED. */
    public static final int GATEWAY_TIMEOUT = 6;

  }

  /**
   * The Class FileController.
   */
  public static class FileController {

    /**
     * Instantiates a new file controller.
     */
    private FileController() {
      // private Constructor
    }

    /** The Constant POST_OCR_TEXT_EXTRACT. */
    public static final String POST_FILE_TEXT_EXTRACT = "/text-extract";

    /** The Constant POST_FILE_TEXT_REDACT. */
    public static final String POST_FILE_TEXT_REDACT = "/text-redact";
  }

  /**
   * 
   * The mimeTypes Constants
   *
   */
  public static class MimeType {
    // Enum all of the column types supported
    public enum AcceptedMimeTypes {
      DOCUMENT(PDF), IMAGE(JPEG, TIFF, PNG);

      private final String[] types;

      private AcceptedMimeTypes(String... t) {
        types = t;
      }

      // Checks if passed mime type is accepted
      public boolean containsType(String typeToCheck) {
        Boolean contains = Boolean.FALSE;
        if (StringUtils.isNotBlank(typeToCheck)) {
          contains = Arrays.stream(types).anyMatch(typeToCheck::equalsIgnoreCase);
        }
        return contains;
      }

    }

    /** The Constant PDF. */
    public static final String PDF = "application/pdf";

    /** The Constant JPEG. */
    public static final String JPEG = "image/jpeg";

    /** The Constant PNG. */
    public static final String PNG = "image/png";

    /** The Constant TIFF. */
    public static final String TIFF = "image/tiff";
  }
}
