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

package org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.elements;

import org.pentaho.reporting.engine.classic.core.style.BorderStyle;
import org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.converter.ColorConverter;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.awt.*;

public class BorderEdgeReadHandler extends AbstractXmlReadHandler {
  private BorderStyle borderType;
  private float width;
  private Color color;

  public BorderEdgeReadHandler() {
  }

  /**
   * Starts parsing.
   *
   * @param attrs the attributes.
   * @throws SAXException if there is a parsing error.
   */
  protected void startParsing( final Attributes attrs ) throws SAXException {
    width = ParserUtil.parseFloat( attrs.getValue( getUri(), "width" ), "Failed to parse width", getLocator() );
    color = ColorConverter.getObject( attrs.getValue( getUri(), "color" ) );
    borderType = parseBorderStyle( attrs.getValue( getUri(), "borderType" ) );
  }

  private BorderStyle parseBorderStyle( String s ) {
    if ( "HIDDEN".equals( s ) ) {
      return BorderStyle.HIDDEN;
    }
    if ( "DOTTED".equals( s ) ) {
      return BorderStyle.DOTTED;
    }
    if ( "DASHED".equals( s ) ) {
      return BorderStyle.DASHED;
    }
    if ( "SOLID".equals( s ) ) {
      return BorderStyle.SOLID;
    }
    if ( "DOUBLE".equals( s ) ) {
      return BorderStyle.DOUBLE;
    }
    if ( "DOT_DASH".equals( s ) ) {
      return BorderStyle.DOT_DASH;
    }
    if ( "DOT_DOT_DASH".equals( s ) ) {
      return BorderStyle.DOT_DOT_DASH;
    }
    if ( "WAVE".equals( s ) ) {
      return BorderStyle.WAVE;
    }
    if ( "GROOVE".equals( s ) ) {
      return BorderStyle.GROOVE;
    }
    if ( "RIDGE".equals( s ) ) {
      return BorderStyle.RIDGE;
    }
    if ( "INSET".equals( s ) ) {
      return BorderStyle.INSET;
    }
    if ( "OUTSET".equals( s ) ) {
      return BorderStyle.OUTSET;
    }
    return BorderStyle.NONE;
  }

  public BorderStyle getBorderType() {
    return borderType;
  }

  public float getWidth() {
    return width;
  }

  public Color getColor() {
    return color;
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException if an parser error occured.
   */
  public Object getObject() throws SAXException {
    return null;
  }
}
