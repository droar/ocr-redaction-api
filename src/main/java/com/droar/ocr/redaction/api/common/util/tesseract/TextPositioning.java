package com.droar.ocr.redaction.api.common.util.tesseract;

/**
 * Enumeration of the possible types of text positioning.
 * It is used when there is possibility in selected Reader to process
 * the text by lines or by words and to return coordinates for the
 * selected type of item.
 * For tesseract this value makes sense only if selected
 * {@link OutputFormat} is {@link OutputFormat#HOCR}.
 */
public enum TextPositioning {
    /**
     * Text will be located by lines retrieved from hocr file.
     * (default value)
     */
    BY_LINES,
    /**
     * Text will be located by words retrieved from hocr file.
     */
    BY_WORDS,
    /**
     * Similar to BY_WORDS mode, but top and bottom of word BBox are inherited from line.
     */
    BY_WORDS_AND_LINES,
}
