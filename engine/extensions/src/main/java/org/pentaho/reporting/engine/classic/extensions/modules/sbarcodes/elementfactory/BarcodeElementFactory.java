/*!
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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions.modules.sbarcodes.elementfactory;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.elementfactory.AbstractContentElementFactory;
import org.pentaho.reporting.engine.classic.core.function.FormulaExpression;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.extensions.modules.sbarcodes.SimpleBarcodesAttributeNames;
import org.pentaho.reporting.engine.classic.extensions.modules.sbarcodes.SimpleBarcodesType;

/**
 * The Barcode element factory can be used to create Barcode of following symbologies: 3of9, 3of9ext, code39, code39ext,
 * usd3, usd3ext, usd-3, usd-3ext, codabar, code27, usd4, 2of7, monarch, nw7, usd-4, nw-7, ean13, ean-13, upca, upc-a,
 * isbn, bookland, code128, code128a, code128b, code128c, uccean128, 2of5, std2of5, int2of5, postnet.
 * <p/>
 * The barcode underlying barcode library used is <a href="http://barbecue.sourceforge.net/">Barbecue</a>, so have a
 * look to their documentation to check the barcode limitations.
 *
 * @author Cedric Pronzato
 */
public class BarcodeElementFactory extends AbstractContentElementFactory {
  private Object content;
  private String fieldname;
  private String formula;
  private Object nullValue;

  private String type;
  private Integer barHeight;
  private Integer barWidth;
  private Boolean checksum = Boolean.FALSE;
  private Boolean showText = Boolean.TRUE;

  private String fontName;
  private Integer fontSize;
  private Boolean bold;
  private Boolean italic;

  public BarcodeElementFactory() {
  }

  public String getFormula() {
    return formula;
  }

  public void setFormula( String formula ) {
    this.formula = formula;
  }

  public Object getContent() {
    return content;
  }

  public void setContent( Object content ) {
    this.content = content;
  }

  public String getFieldname() {
    return fieldname;
  }

  public void setFieldname( String fieldname ) {
    this.fieldname = fieldname;
  }

  public Object getNullValue() {
    return nullValue;
  }

  public void setNullValue( Object nullValue ) {
    this.nullValue = nullValue;
  }

  public String getType() {
    return type;
  }

  public void setType( String type ) {
    this.type = type;
  }

  public Integer getBarHeight() {
    return barHeight;
  }

  public void setBarHeight( Integer barHeight ) {
    this.barHeight = barHeight;
  }

  public Integer getBarWidth() {
    return barWidth;
  }

  public void setBarWidth( Integer barWidth ) {
    this.barWidth = barWidth;
  }

  public Boolean getChecksum() {
    return checksum;
  }

  public void setChecksum( Boolean checksum ) {
    this.checksum = checksum;
  }

  public Boolean getShowText() {
    return showText;
  }

  public void setShowText( Boolean showText ) {
    this.showText = showText;
  }

  public String getFontName() {
    return fontName;
  }

  public void setFontName( String fontName ) {
    this.fontName = fontName;
  }

  public Integer getFontSize() {
    return fontSize;
  }

  public void setFontSize( Integer fontSize ) {
    this.fontSize = fontSize;
  }

  public Boolean getBold() {
    return bold;
  }

  public void setBold( Boolean bold ) {
    this.bold = bold;
  }

  public Boolean getItalic() {
    return italic;
  }

  public void setItalic( Boolean italic ) {
    this.italic = italic;
  }

  /**
   * Applies the style definition to the elements stylesheet.
   *
   * @param style
   *          the element stylesheet which should receive the style definition.
   */
  protected void applyStyle( ElementStyleSheet style ) {
    // background color, paint color,
    super.applyStyle( style );
    // set text styles
    if ( fontName != null ) {
      style.setStyleProperty( TextStyleKeys.FONT, getFontName() );
    }
    if ( fontSize != null ) {
      style.setStyleProperty( TextStyleKeys.FONTSIZE, getFontSize() );
    }
    if ( bold != null ) {
      style.setStyleProperty( TextStyleKeys.BOLD, getBold() );
    }
    if ( italic != null ) {
      style.setStyleProperty( TextStyleKeys.ITALIC, getItalic() );
    }
  }

  /**
   * Creates a new instance of the element. Override this method to return a concrete subclass of the element.
   *
   * @return the newly generated instance of the element.
   */
  public Element createElement() {
    final Element element = new Element();
    applyElementName( element );
    applyStyle( element.getStyle() );

    element.setElementType( new SimpleBarcodesType() );
    if ( getContent() != null ) {
      element.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, getContent() );
    }
    if ( getFieldname() != null ) {
      element.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.FIELD, getFieldname() );
    }
    if ( getFormula() != null ) {
      final FormulaExpression formulaExpression = new FormulaExpression();
      formulaExpression.setFormula( getFormula() );
      element.setAttributeExpression( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, formulaExpression );
    }

    if ( getType() != null ) {
      element.setAttribute( SimpleBarcodesAttributeNames.NAMESPACE, SimpleBarcodesAttributeNames.TYPE_ATTRIBUTE,
          getType() );
    }
    if ( getChecksum() != null ) {
      element.setAttribute( SimpleBarcodesAttributeNames.NAMESPACE, SimpleBarcodesAttributeNames.CHECKSUM_ATTRIBUTE,
          getChecksum() );
    }
    if ( barWidth != null ) {
      element.setAttribute( SimpleBarcodesAttributeNames.NAMESPACE, SimpleBarcodesAttributeNames.BAR_WIDTH_ATTRIBUTE,
          barWidth );
    }
    if ( barHeight != null ) {
      element.setAttribute( SimpleBarcodesAttributeNames.NAMESPACE, SimpleBarcodesAttributeNames.BAR_HEIGHT_ATTRIBUTE,
          barHeight );
    }
    if ( showText != null ) {
      element.setAttribute( SimpleBarcodesAttributeNames.NAMESPACE, SimpleBarcodesAttributeNames.SHOW_TEXT_ATTRIBUTE,
          showText );
    }

    return element;
  }

}
