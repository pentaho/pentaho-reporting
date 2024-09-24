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
import org.pentaho.reporting.engine.classic.core.filter.types.MessageType;
import org.pentaho.reporting.engine.classic.core.style.FontDefinition;

/**
 * The message format factory can be used to create formatted text elements using the format defined for
 * {@link java.text.MessageFormat}. These text elements have special abilities to format numeric values and dates based
 * on the MessageFormat string.
 * <p/>
 * Once the desired properties are set, the factory can be reused to create similar text elements.
 *
 * @author J&ouml;rg Schaible
 */
public class MessageFieldElementFactory extends TextElementFactory {
  /**
   * The message format instance used to format the text element.
   */
  private String formatString;
  /**
   * The nullstring of the text element if the value in the datasource was null.
   */
  private String nullString;

  private String messageNullString;

  /**
   * Creates a new message field element factory.
   */
  public MessageFieldElementFactory() {
  }

  /**
   * Returns the format string of the used message format.
   *
   * @return the formatstring of the number format instance.
   */
  public String getFormatString() {
    return formatString;
  }

  /**
   * Defines the format string of the used message format. The format string should contain a format for the element 0.
   * This method will replace the message format instance of this factory.
   *
   * @param formatString
   *          the formatstring of the message format instance.
   */
  public void setFormatString( final String formatString ) {
    this.formatString = formatString;
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

  public String getMessageNullString() {
    return messageNullString;
  }

  public void setMessageNullString( final String messageNullString ) {
    this.messageNullString = messageNullString;
  }

  /**
   * Creates the message text element based on the defined settings. Undefined properties will not be set in the
   * generated element.
   *
   * @return the generated numeric text element
   * @see org.pentaho.reporting.engine.classic.core.elementfactory.ElementFactory#createElement()
   */
  public Element createElement() {
    final Element element = new Element();
    applyElementName( element );
    applyStyle( element.getStyle() );

    element.setElementType( new MessageType() );
    element.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.NULL_VALUE, getNullString() );
    element
        .setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.MESSAGE_NULL_VALUE, getMessageNullString() );
    element.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, getFormatString() );
    return element;
  }

  /**
   * Creates a new TextElement containing a message filter structure.
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
   * @param format
   *          the format string used in this message element
   * @return a report element for displaying <code>Number</code> objects.
   * @throws NullPointerException
   *           if bounds, name or function are null
   * @throws IllegalArgumentException
   *           if the given alignment is invalid
   * @deprecated Use a more fine-grained approach to define this element by using the element-factory directly.
   */
  public static Element createMessageElement( final String name, final Rectangle2D bounds, final Color paint,
      final ElementAlignment alignment, final FontDefinition font, final String nullString, final String format ) {
    return createMessageElement( name, bounds, paint, alignment, ElementAlignment.TOP, font, nullString, format );
  }

  /**
   * Creates a new TextElement containing a message filter structure.
   *
   * @param name
   *          the name of the new element.
   * @param bounds
   *          the bounds of the new element.
   * @param color
   *          the text color of this text element.
   * @param alignment
   *          the horizontal text alignment.
   * @param valign
   *          the vertical alignment.
   * @param font
   *          the font for this element.
   * @param nullString
   *          the text used when the value of this element is null.
   * @param formatString
   *          the MessageFormat used in this number element.
   * @return a report element for displaying <code>Number</code> objects.
   * @throws NullPointerException
   *           if bounds, name or function are null
   * @throws IllegalArgumentException
   *           if the given alignment is invalid
   * @deprecated Use a more fine-grained approach to define this element by using the element-factory directly.
   */
  public static Element createMessageElement( final String name, final Rectangle2D bounds, final Color color,
      final ElementAlignment alignment, final ElementAlignment valign, final FontDefinition font,
      final String nullString, final String formatString ) {

    final MessageFieldElementFactory factory = new MessageFieldElementFactory();
    factory.setX( new Float( bounds.getX() ) );
    factory.setY( new Float( bounds.getY() ) );
    factory.setMinimumWidth( new Float( bounds.getWidth() ) );
    factory.setMinimumHeight( new Float( bounds.getHeight() ) );
    factory.setName( name );
    factory.setColor( color );
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
    factory.setNullString( nullString );
    factory.setFormatString( formatString );
    return factory.createElement();
  }
}
