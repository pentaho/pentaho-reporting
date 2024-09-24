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

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.filter.types.TextFieldType;
import org.pentaho.reporting.engine.classic.core.function.FormulaExpression;
import org.pentaho.reporting.engine.classic.core.style.FontDefinition;

/**
 * A factory to define text fields. Text fields read their content from the dataRow and try to print it as plain text
 * (using toString() if required).
 *
 * @author Thomas Morgner
 */
public class TextFieldElementFactory extends TextElementFactory {
  /**
   * The fieldname of the datarow from where to read the content.
   */
  private String fieldname;

  /**
   * The nullstring of the text element if the value in the datasource was null.
   */
  private String nullString;

  /**
   * The value-formula that computes the value for this element.
   */
  private String formula;

  /**
   * DefaultConstructor.
   */
  public TextFieldElementFactory() {
  }

  /**
   * Returns the field name from where to read the content of the element.
   *
   * @return the field name.
   */
  public String getFieldname() {
    return fieldname;
  }

  /**
   * Defines the field name from where to read the content of the element. The field name is the name of a datarow
   * column.
   *
   * @param fieldname
   *          the field name.
   */
  public void setFieldname( final String fieldname ) {
    this.fieldname = fieldname;
  }

  /**
   * Returns the formula that should be used to compute the value of the field. The formula must be valid according to
   * the OpenFormula specifications.
   *
   * @return the formula as string.
   */
  public String getFormula() {
    return formula;
  }

  /**
   * Assigns a formula to the element to compute the value for this element. If a formula is defined, it will override
   * the 'field' property.
   *
   * @param formula
   *          the formula as a string.
   */
  public void setFormula( final String formula ) {
    this.formula = formula;
  }

  /**
   * Returns the null string for the text element. The null string is used when no content is found for that element.
   *
   * @return the null string.
   */
  public String getNullString() {
    return nullString;
  }

  /**
   * Defines the null string for the text element. The null string is used when no content is found for that element.
   * The nullstring itself can be null.
   *
   * @param nullString
   *          the null string.
   */
  public void setNullString( final String nullString ) {
    this.nullString = nullString;
  }

  /**
   * Creates the text field element.
   *
   * @return the generated text field element
   * @see org.pentaho.reporting.engine.classic.core.elementfactory.ElementFactory#createElement()
   */
  public Element createElement() {
    final Element element = new Element();
    applyElementName( element );
    applyStyle( element.getStyle() );
    element.setElementType( new TextFieldType() );
    if ( getFieldname() != null ) {
      element.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.FIELD, getFieldname() );
    }
    if ( getFormula() != null ) {
      final FormulaExpression formulaExpression = new FormulaExpression();
      formulaExpression.setFormula( getFormula() );
      element.setAttributeExpression( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, formulaExpression );
    }

    element.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.NULL_VALUE, getNullString() );
    return element;
  }

  /**
   * Creates a new TextElement without any additional filtering.
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
   * @param nullString
   *          the text used when the value of this element is null
   * @param field
   *          the field in the datamodel to retrieve values from
   * @return a report element for displaying <code>String</code> objects.
   * @throws NullPointerException
   *           if bounds, name or function are null
   * @throws IllegalArgumentException
   *           if the given alignment is invalid
   * @deprecated Use a more fine-grained approach to define this element by using the element-factory directly.
   */
  public static Element createStringElement( final String name, final Rectangle2D bounds, final Color paint,
      final ElementAlignment alignment, final FontDefinition font, final String nullString, final String field ) {
    return createStringElement( name, bounds, paint, alignment, ElementAlignment.TOP, font, nullString, field );
  }

  /**
   * Creates a new TextElement without any additional filtering.
   *
   * @param name
   *          the name of the new element
   * @param bounds
   *          the bounds of the new element
   * @param paint
   *          the text color of this text element
   * @param alignment
   *          the horizontal text alignment.
   * @param valign
   *          the vertical alignment.
   * @param font
   *          the font for this element
   * @param nullString
   *          the text used when the value of this element is null
   * @param field
   *          the field in the datamodel to retrieve values from
   * @return a report element for displaying <code>String</code> objects.
   * @throws NullPointerException
   *           if bounds, name or function are null
   * @throws IllegalArgumentException
   *           if the given alignment is invalid
   * @deprecated Use a more fine-grained approach to define this element by using the element-factory directly.
   */
  public static Element createStringElement( final String name, final Rectangle2D bounds, final Color paint,
      final ElementAlignment alignment, final ElementAlignment valign, final FontDefinition font,
      final String nullString, final String field ) {
    final TextFieldElementFactory factory = new TextFieldElementFactory();
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
    factory.setFieldname( field );
    factory.setNullString( nullString );
    return factory.createElement();
  }

}
