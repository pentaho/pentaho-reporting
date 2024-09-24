/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.reporting.engine.classic.core.elementfactory;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.filter.types.LabelType;
import org.pentaho.reporting.engine.classic.core.style.FontDefinition;

/**
 * A factory to define LabelElements. LabelElements are considered immutable and should not be modified once they are
 * created. The label expects plain text.
 *
 * @author Thomas Morgner
 */
public class LabelElementFactory extends TextElementFactory {
  /**
   * The label text.
   */
  private String text;

  private String excelFormula;

  /**
   * DefaultConstructor.
   */
  public LabelElementFactory() {
  }

  /**
   * Returns the label text.
   *
   * @return the text of the label.
   */
  public String getText() {
    return text;
  }

  /**
   * Defines the text of the label.
   *
   * @param text
   *          the plain text of the label.
   */
  public void setText( final String text ) {
    this.text = text;
  }

  public String getExcelFormula() {
    return excelFormula;
  }

  public void setExcelFormula( final String excelFormula ) {
    this.excelFormula = excelFormula;
  }

  /**
   * Creates the label element.
   *
   * @return the generated label.
   * @throws IllegalStateException
   *           if the text is not defined.
   * @see org.pentaho.reporting.engine.classic.core.elementfactory.ElementFactory#createElement()
   */
  public Element createElement() {
    final Element element = new Element();
    element.setElementType( new LabelType() );
    applyElementName( element );
    applyStyle( element.getStyle() );

    element.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, getText() );
    element.setAttribute( AttributeNames.Excel.NAMESPACE, AttributeNames.Excel.FIELD_FORMULA, getExcelFormula() );
    return element;
  }

  /**
   * Creates a new TextElement containing a label.
   *
   * @param name
   *          the name of the new element
   * @param bounds
   *          the bounds of the new element
   * @param paint
   *          the text color of this text element
   * @param alignment
   *          the horizontal text alignment.
   * @param font
   *          the font for this element
   * @param labeltext
   *          the text to display
   * @return a report element for displaying a label (static text).
   * @throws NullPointerException
   *           if bounds, name, format or field are null
   * @throws IllegalArgumentException
   *           if the given alignment is invalid
   * @deprecated Use a more fine-grained approach to define this element by using the element-factory directly.
   */
  public static Element createLabelElement( final String name, final Rectangle2D bounds, final Color paint,
      final ElementAlignment alignment, final FontDefinition font, final String labeltext ) {
    return createLabelElement( name, bounds, paint, alignment, ElementAlignment.TOP, font, labeltext );
  }

  /**
   * Creates a new Text Element containing a label.
   *
   * @param name
   *          the name of the new element.
   * @param bounds
   *          the bounds of the new element.
   * @param paint
   *          the text color of this text element.
   * @param alignment
   *          the horizontal alignment.
   * @param valign
   *          the vertical alignment.
   * @param font
   *          the font for this element.
   * @param labeltext
   *          the text to display.
   * @return a report element for displaying a label (static text).
   * @throws NullPointerException
   *           if bounds, name, format or field are <code>null</code>.
   * @throws IllegalArgumentException
   *           if the given alignment is invalid.
   * @deprecated Use a more fine-grained approach to define this element by using the element-factory directly.
   */
  public static Element
    createLabelElement( final String name, final Rectangle2D bounds, final Color paint,
        final ElementAlignment alignment, final ElementAlignment valign, final FontDefinition font,
        final String labeltext ) {
    final LabelElementFactory factory = new LabelElementFactory();
    factory.setX( new Float( bounds.getX() ) );
    factory.setY( new Float( bounds.getY() ) );
    factory.setMinimumWidth( new Float( bounds.getWidth() ) );
    factory.setMinimumHeight( new Float( bounds.getHeight() ) );
    factory.setName( name );
    factory.setColor( paint );
    factory.setHorizontalAlignment( alignment );
    factory.setVerticalAlignment( valign );

    if ( font != null ) {
      factory.setFontName( font.getFontName() );
      factory.setFontSize( new Integer( font.getFontSize() ) );
      factory.setBold( ElementFactory.getBooleanValue( font.isBold() ) );
      factory.setItalic( ElementFactory.getBooleanValue( font.isItalic() ) );
      factory.setEncoding( font.getFontEncoding( null ) );
      factory.setUnderline( ElementFactory.getBooleanValue( font.isUnderline() ) );
      factory.setStrikethrough( ElementFactory.getBooleanValue( font.isStrikeThrough() ) );
      factory.setEmbedFont( ElementFactory.getBooleanValue( font.isEmbeddedFont() ) );
    }
    factory.setText( labeltext );
    return factory.createElement();
  }

}
