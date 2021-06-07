package com.droar.ocr.redaction.api.common.util.tesseract;

import com.itextpdf.kernel.geom.Rectangle;

/**
 * 
 * @author droar
 *
 */
public class TextInfo {

    /**
     * Contains any text.
     */
    private String text;

    /**
     * {@link Rectangle} describing text bbox (lower-left based) expressed in points.
     */
    private Rectangle bboxRect;

    /**
     * Creates a new {@link TextInfo} instance.
     */
    public TextInfo() {
    }

    /**
     * Creates a new {@link TextInfo} instance from existing one.
     *
     * @param textInfo to create from
     */
    public TextInfo(final TextInfo textInfo) {
        this.text = textInfo.text;
        this.bboxRect = new Rectangle(textInfo.bboxRect);
    }

    /**
     * Creates a new {@link TextInfo} instance.
     *
     * @param text any text
     * @param bbox {@link Rectangle} describing text bbox
     */
    public TextInfo(final String text, final Rectangle bbox) {
        this.text = text;
        this.bboxRect = new Rectangle(bbox);
    }

    /**
     * Gets text element.
     *
     * @return String
     */
    public String getText() {
        return text;
    }

    /**
     * Sets text element.
     *
     * @param newText retrieved text
     */
    public void setText(final String newText) {
        text = newText;
    }

    /**
     * Gets bbox coordinates.
     *
     * @return {@link Rectangle} describing text bbox
     */
    public Rectangle getBboxRect() {
        return bboxRect;
    }

    /**
     * Sets text bbox.
     *
     * @param bbox {@link Rectangle} describing text bbox
     */
    public void setBboxRect(final Rectangle bbox) {
        this.bboxRect = new Rectangle(bbox);
    }
}
