/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.elementfactory;

import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.TextWrap;
import org.pentaho.reporting.engine.classic.core.style.VerticalTextAlign;
import org.pentaho.reporting.engine.classic.core.style.WhitespaceCollapse;

/**
 * The text element factory is the abstract base class for all text element factory implementations. It provides common
 * properties shared among all text elements.
 *
 * @author Thomas Morgner
 */
public abstract class TextElementFactory extends ElementFactory {
  /**
   * The name of the font used to print the text.
   */
  private String fontName;
  /**
   * A flag defining whether to use the bold font style.
   */
  private Boolean bold;
  /**
   * A flag defining whether to use the italic font style.
   */
  private Boolean italic;
  /**
   * A flag defining whether to use the underline font style.
   */
  private Boolean underline;
  /**
   * A flag defining whether to use the strikethough font style.
   */
  private Boolean strikethrough;
  /**
   * A flag defining whether to embed the font used for the element in the target document.
   */
  private Boolean embedFont;
  /**
   * Defines the font size of the used font.
   */
  private Integer fontSize;
  /**
   * Defines the lineheight. The lineheight must be >= the font size, or it is ignored.
   */
  private Float lineHeight;

  /**
   * Defines the font encoding used when writing the text.
   */
  private String encoding;

  /**
   * Defines the vertical text alignment. The vertical text alignment controls how inline elements are positioned
   * relative to each other.
   *
   * @see TextWrap
   */
  private VerticalTextAlign verticalTextAlignment;

  /**
   * The reserved literal.
   */
  private String reservedLiteral;
  /**
   * A flag defining whether to remove leading and trailing whitespaces from text lines.
   */
  private Boolean trimTextContent;
  /**
   * A flag defining, whether the text should be autmaticly wrapped in Excel.
   */
  private Boolean wrapText;

  /**
   * Defines how and when text-wrapping should occur inside a text element.
   *
   * @see TextWrap
   */
  private TextWrap wrap;

  /**
   * Defines how to treat whitespaces in the text of a text-element.
   *
   * @see WhitespaceCollapse
   */
  private WhitespaceCollapse whitespaceCollapse;
  /**
   * Defines the maximum letter spacing for a text element. This property controls how the justify-alignment distributes
   * extra space on a line.
   */
  private Float maximumLetterSpacing;
  /**
   * Defines the minimum letter spacing for a text element. This property controls how the justify-alignment distributes
   * extra space on a line and controls how much extra space is applied between the characters of a text.
   */
  private Float minimumLetterSpacing;
  /**
   * Defines the optimum letter spacing for a text element. This property controls how the justify-alignment distributes
   * extra space on a line.
   */
  private Float optimumLetterSpacing;

  /**
   * Default Constructor.
   */
  protected TextElementFactory() {
  }

  /**
   * Returns the defined vertical text alignment for this element.
   *
   * @return the vertical text alignment property or null, if the property is not defined.
   */
  public VerticalTextAlign getVerticalTextAlignment() {
    return verticalTextAlignment;
  }

  /**
   * Defines the vertical text alignment for this element.
   *
   * @param verticalTextAlignment
   *          the vertical text alignment property or null, if the property should not be defined.
   */
  public void setVerticalTextAlignment( final VerticalTextAlign verticalTextAlignment ) {
    this.verticalTextAlignment = verticalTextAlignment;
  }

  /**
   * Returns how and when text-wrapping should occur inside a text element.
   *
   * @return the defined text-wrap property.
   * @see TextWrap
   */
  public TextWrap getWrap() {
    return wrap;
  }

  /**
   * Defines how and when text-wrapping should occur inside a text element.
   *
   * @param wrap
   *          the defined text-wrap property.
   * @see TextWrap
   */
  public void setWrap( final TextWrap wrap ) {
    this.wrap = wrap;
  }

  /**
   * Returns how the layouter treats whitespaces in the text of a text-element.
   *
   * @return the white space collapse constant or null, if undefined.
   * @see WhitespaceCollapse
   */
  public WhitespaceCollapse getWhitespaceCollapse() {
    return whitespaceCollapse;
  }

  /**
   * Defines how to treat whitespaces in the text of a text-element.
   *
   * @param whitespaceCollapse
   *          the white space collapse constant or null, if undefined.
   * @see WhitespaceCollapse
   */
  public void setWhitespaceCollapse( final WhitespaceCollapse whitespaceCollapse ) {
    this.whitespaceCollapse = whitespaceCollapse;
  }

  /**
   * Returns the defined maximum letter spacing for a text element. This property controls how the justify-alignment
   * distributes extra space on a line.
   *
   * @return the maximum letter spacing or null, if undefined.
   */
  public Float getMaximumLetterSpacing() {
    return maximumLetterSpacing;
  }

  /**
   * Defines the maximum letter spacing for a text element. This property controls how the justify-alignment distributes
   * extra space on a line.
   *
   * @param maximumLetterSpacing
   *          the maximum letter spacing.
   */
  public void setMaximumLetterSpacing( final Float maximumLetterSpacing ) {
    this.maximumLetterSpacing = maximumLetterSpacing;
  }

  /**
   * Returns the defined minimum letter spacing for a text element. This property controls how the justify-alignment
   * distributes extra space on a line and controls how much extra space is applied between the characters of a text.
   *
   * @return the minimum letter spacing or null, if undefined.
   */
  public Float getMinimumLetterSpacing() {
    return minimumLetterSpacing;
  }

  /**
   * Defines the minimum letter spacing for a text element. This property controls how the justify-alignment distributes
   * extra space on a line and controls how much extra space is applied between the characters of a text.
   *
   * @param minimumLetterSpacing
   *          the minimum letter spacing or null, if undefined.
   */
  public void setMinimumLetterSpacing( final Float minimumLetterSpacing ) {
    this.minimumLetterSpacing = minimumLetterSpacing;
  }

  /**
   * Return optimum letter spacing for a text element. This property controls how the justify-alignment distributes
   * extra space on a line.
   *
   * @return the optimum letter spacing or null, if undefined.
   */
  public Float getOptimumLetterSpacing() {
    return optimumLetterSpacing;
  }

  /**
   * Defines the optimum letter spacing for a text element. This property controls how the justify-alignment distributes
   * extra space on a line.
   *
   * @param optimumLetterSpacing
   *          the optimum letter spacing or null, if undefined.
   */
  public void setOptimumLetterSpacing( final Float optimumLetterSpacing ) {
    this.optimumLetterSpacing = optimumLetterSpacing;
  }

  /**
   * Returns the font embedding flag for the new text elements. Font embedding is only used in some output targets.
   *
   * @return the font embedding flag.
   */
  public Boolean getEmbedFont() {
    return embedFont;
  }

  /**
   * Defines that the font should be embedded if possible.
   *
   * @param embedFont
   *          embedds the font if possible.
   */
  public void setEmbedFont( final Boolean embedFont ) {
    this.embedFont = embedFont;
  }

  /**
   * Returns the name of the font that should be used to print the text.
   *
   * @return the font name.
   */
  public String getFontName() {
    return fontName;
  }

  /**
   * Defines the name of the font that should be used to print the text.
   *
   * @param fontName
   *          the name of the font.
   */
  public void setFontName( final String fontName ) {
    this.fontName = fontName;
  }

  /**
   * Returns the state of the bold flag for the font. This method may return null to indicate that that value should be
   * inherited from the parents.
   *
   * @return the bold-flag.
   */
  public Boolean getBold() {
    return bold;
  }

  /**
   * Defines the state of the bold flag for the font. This value may be set to null to indicate that that value should
   * be inherited from the parents.
   *
   * @param bold
   *          the bold-flag.
   */
  public void setBold( final Boolean bold ) {
    this.bold = bold;
  }

  /**
   * Returns the state of the italic flag for the font. This method may return null to indicate that that value should
   * be inherited from the parents.
   *
   * @return the italic-flag.
   */
  public Boolean getItalic() {
    return italic;
  }

  /**
   * Defines the state of the italic flag for the font. This value may be set to null to indicate that that value should
   * be inherited from the parents.
   *
   * @param italic
   *          the italic-flag.
   */
  public void setItalic( final Boolean italic ) {
    this.italic = italic;
  }

  /**
   * Returns the state of the underline flag for the font. This method may return null to indicate that that value
   * should be inherited from the parents.
   *
   * @return the underline-flag.
   */
  public Boolean getUnderline() {
    return underline;
  }

  /**
   * Defines the state of the underline flag for the font. This value may be set to null to indicate that that value
   * should be inherited from the parents.
   *
   * @param underline
   *          the underline-flag.
   */
  public void setUnderline( final Boolean underline ) {
    this.underline = underline;
  }

  /**
   * Returns the state of the strike through flag for the font. This method may return null to indicate that that value
   * should be inherited from the parents.
   *
   * @return the strike-through-flag.
   */
  public Boolean getStrikethrough() {
    return strikethrough;
  }

  /**
   * Defines the state of the strike through flag for the font. This value may be set to null to indicate that that
   * value should be inherited from the parents.
   *
   * @param strikethrough
   *          the strikethrough-flag.
   */
  public void setStrikethrough( final Boolean strikethrough ) {
    this.strikethrough = strikethrough;
  }

  /**
   * Returns the font size in points.
   *
   * @return the font size.
   */
  public Integer getFontSize() {
    return fontSize;
  }

  /**
   * Returns the font size in points.
   *
   * @param fontSize
   *          the font size.
   */
  public void setFontSize( final Integer fontSize ) {
    this.fontSize = fontSize;
  }

  /**
   * Returns the lineheight defined for the text element. The lineheight must be greater than the font size, or this
   * value will be ignored.
   *
   * @return the line height.
   */
  public Float getLineHeight() {
    return lineHeight;
  }

  /**
   * Defines the lineheight defined for the text element. The lineheight must be greater than the font size, or this
   * value will be ignored.
   *
   * @param lineHeight
   *          the line height.
   */
  public void setLineHeight( final Float lineHeight ) {
    this.lineHeight = lineHeight;
  }

  /**
   * Returns the font encoding used to write the text. This parameter is only used by some output targets and will be
   * ignored otherwise.
   *
   * @return the font encoding.
   */
  public String getEncoding() {
    return encoding;
  }

  /**
   * Defines the font encoding used to write the text. This parameter is only used by some output targets and will be
   * ignored otherwise.
   *
   * @param encoding
   *          the font encoding.
   */
  public void setEncoding( final String encoding ) {
    this.encoding = encoding;
  }

  /**
   * Returns the defined reserved literal, which should be appended to the text to signal text, which was not completly
   * printed.
   *
   * @return the reserved literal.
   */
  public String getReservedLiteral() {
    return reservedLiteral;
  }

  /**
   * Defines the defined reserved literal, which should be appended to the text to signal text, which was not completly
   * printed.
   *
   * @param reservedLiteral
   *          the reserved literal.
   */
  public void setReservedLiteral( final String reservedLiteral ) {
    this.reservedLiteral = reservedLiteral;
  }

  /**
   * Returns, whether the text lines should be trimmed.
   *
   * @return Boolean.TRUE, if the text should be trimmed, Boolean.FALSE if the text should never be trimmed or null, if
   *         the default should be applied.
   */
  public Boolean getTrimTextContent() {
    return trimTextContent;
  }

  /**
   * Defines, whether the text lines should be trimmed.
   *
   * @param trimTextContent
   *          Boolean.TRUE, if the text should be trimmed, Boolean.FALSE if the text should never be trimmed or null, if
   *          the default should be applied.
   */
  public void setTrimTextContent( final Boolean trimTextContent ) {
    this.trimTextContent = trimTextContent;
  }

  /**
   * Returns, whether the text should be wrapped in Excel-cells.
   *
   * @return the wrap text flag.
   */
  public Boolean getWrapText() {
    return wrapText;
  }

  /**
   * Defines, whether the text should be wrapped in excel cells.
   *
   * @param wrapText
   *          the wrap text flag.
   */
  public void setWrapText( final Boolean wrapText ) {
    this.wrapText = wrapText;
  }

  /**
   * Applies the defined element style to the given stylesheet. This is a helper function to reduce the code size of the
   * implementors.
   *
   * @param style
   *          the stlyesheet.
   */
  protected void applyStyle( final ElementStyleSheet style ) {
    super.applyStyle( style );
    if ( bold != null ) {
      style.setStyleProperty( TextStyleKeys.BOLD, getBold() );
    }
    if ( embedFont != null ) {
      style.setStyleProperty( TextStyleKeys.EMBEDDED_FONT, getEmbedFont() );
    }
    if ( fontName != null ) {
      style.setStyleProperty( TextStyleKeys.FONT, getFontName() );
    }
    if ( encoding != null ) {
      style.setStyleProperty( TextStyleKeys.FONTENCODING, getEncoding() );
    }
    if ( fontSize != null ) {
      style.setStyleProperty( TextStyleKeys.FONTSIZE, getFontSize() );
    }
    if ( italic != null ) {
      style.setStyleProperty( TextStyleKeys.ITALIC, getItalic() );
    }
    if ( lineHeight != null ) {
      style.setStyleProperty( TextStyleKeys.LINEHEIGHT, getLineHeight() );
    }
    if ( strikethrough != null ) {
      style.setStyleProperty( TextStyleKeys.STRIKETHROUGH, getStrikethrough() );
    }
    if ( underline != null ) {
      style.setStyleProperty( TextStyleKeys.UNDERLINED, getUnderline() );
    }
    if ( reservedLiteral != null ) {
      style.setStyleProperty( TextStyleKeys.RESERVED_LITERAL, getReservedLiteral() );
    }
    if ( trimTextContent != null ) {
      style.setStyleProperty( TextStyleKeys.TRIM_TEXT_CONTENT, getTrimTextContent() );
    }

    if ( wrap != null ) {
      style.setStyleProperty( TextStyleKeys.TEXT_WRAP, getWrap() );
    }
    if ( maximumLetterSpacing != null ) {
      style.setStyleProperty( TextStyleKeys.X_MAX_LETTER_SPACING, getMaximumLetterSpacing() );
    }
    if ( minimumLetterSpacing != null ) {
      style.setStyleProperty( TextStyleKeys.X_MIN_LETTER_SPACING, getMinimumLetterSpacing() );
    }
    if ( optimumLetterSpacing != null ) {
      style.setStyleProperty( TextStyleKeys.X_OPTIMUM_LETTER_SPACING, getOptimumLetterSpacing() );
    }
    if ( verticalTextAlignment != null ) {
      style.setStyleProperty( TextStyleKeys.VERTICAL_TEXT_ALIGNMENT, getVerticalTextAlignment() );
    }
    if ( whitespaceCollapse != null ) {
      style.setStyleProperty( TextStyleKeys.WHITE_SPACE_COLLAPSE, getWhitespaceCollapse() );
    }
    if ( wrapText != null ) {
      style.setStyleProperty( ElementStyleKeys.EXCEL_WRAP_TEXT, getWrapText() );
    }
  }
}
