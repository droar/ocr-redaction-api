package com.droar.ocr.redaction.api.common.util.tesseract;

/**
 * The Class Tesseract4LogMessageConstant.
 */
public class Tesseract4LogMessageConstant {
  
  /** The Constant TESSERACT_FAILED. */
  public static final String TESSERACT_FAILED = "Tesseract failed: {0}";
  
  /** The Constant COMMAND_FAILED. */
  public static final String COMMAND_FAILED = "Command failed: {0}";
  
  /** The Constant CANNOT_READ_FILE. */
  public static final String CANNOT_READ_FILE = "Cannot read file {0}: {1}";
  
  /** The Constant CANNOT_OCR_INPUT_FILE. */
  public static final String CANNOT_OCR_INPUT_FILE = "Cannot ocr input file: {1}";
  
  /** The Constant CANNOT_USE_USER_WORDS. */
  public static final String CANNOT_USE_USER_WORDS = "Cannot use custom user words: {0}";
  
  /** The Constant CANNOT_RETRIEVE_PAGES_FROM_IMAGE. */
  public static final String CANNOT_RETRIEVE_PAGES_FROM_IMAGE = "Cannot get pages from image {0}: {1}";
  
  /** The Constant PAGE_NUMBER_IS_INCORRECT. */
  public static final String PAGE_NUMBER_IS_INCORRECT = "Provided number of page ({0}) is incorrect for {1}";
  
  /** The Constant CANNOT_DELETE_FILE. */
  public static final String CANNOT_DELETE_FILE = "File {0} cannot be deleted: {1}";
  
  /** The Constant CANNOT_PROCESS_IMAGE. */
  public static final String CANNOT_PROCESS_IMAGE = "Cannot process " + "image: {0}";
  
  /** The Constant CANNOT_WRITE_TO_FILE. */
  public static final String CANNOT_WRITE_TO_FILE = "Cannot write to file {0}: {1}";
  
  /** The Constant CREATED_TEMPORARY_FILE. */
  public static final String CREATED_TEMPORARY_FILE = "Created temp file {0}";
  
  /** The Constant CANNOT_BINARIZE_IMAGE. */
  public static final String CANNOT_BINARIZE_IMAGE = "Cannot binarize image with depth {0}";
  
  /** The Constant CANNOT_CREATE_BUFFERED_IMAGE. */
  public static final String CANNOT_CREATE_BUFFERED_IMAGE = "Cannot create a buffered image from the input image: {0}";
  
  /** The Constant START_OCR_FOR_IMAGES. */
  public static final String START_OCR_FOR_IMAGES = "Starting ocr for {0} image(s)";
  
  /** The Constant CANNOT_READ_INPUT_IMAGE. */
  public static final String CANNOT_READ_INPUT_IMAGE = "Cannot read input image {0}";
  
  /** The Constant CANNOT_GET_TEMPORARY_DIRECTORY. */
  public static final String CANNOT_GET_TEMPORARY_DIRECTORY = "Cannot get " + "temporary directory: {0}";
  
  /** The Constant CANNOT_PARSE_NODE_BBOX. */
  public static final String CANNOT_PARSE_NODE_BBOX = "Cannot parse node BBox, defaults to 0, 0, 0, 0. Node: {0}";
  
  /** The Constant CANNOT_READ_IMAGE_METADATA. */
  public static final String CANNOT_READ_IMAGE_METADATA = "Cannot read image metadata {0}";
  
  /** The Constant UNSUPPORTED_EXIF_ORIENTATION_VALUE. */
  public static final String UNSUPPORTED_EXIF_ORIENTATION_VALUE =
      "Unsuppoted EXIF Orientation value {0}. 1 is used by default";


  /**
   * Instantiates a new tesseract 4 log message constant.
   */
  private Tesseract4LogMessageConstant() {}
}
