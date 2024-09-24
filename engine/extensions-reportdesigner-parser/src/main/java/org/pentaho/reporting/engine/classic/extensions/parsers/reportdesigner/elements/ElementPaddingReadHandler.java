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

import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ElementPaddingReadHandler extends AbstractXmlReadHandler {
  private ElementStyleSheet styleSheet;

  public ElementPaddingReadHandler( final ElementStyleSheet styleSheet ) {
    this.styleSheet = styleSheet;
  }

  /**
   * Starts parsing.
   *
   * @param attrs the attributes.
   * @throws SAXException if there is a parsing error.
   */
  protected void startParsing( final Attributes attrs ) throws SAXException {
    final float top = ParserUtil.parseFloat( attrs.getValue( getUri(), "top" ), "Failed to parse attribute" );
    final float left = ParserUtil.parseFloat( attrs.getValue( getUri(), "left" ), "Failed to parse attribute" );
    final float bottom = ParserUtil.parseFloat( attrs.getValue( getUri(), "bottom" ), "Failed to parse attribute" );
    final float right = ParserUtil.parseFloat( attrs.getValue( getUri(), "right" ), "Failed to parse attribute" );

    styleSheet.setStyleProperty( ElementStyleKeys.PADDING_TOP, new Float( top ) );
    styleSheet.setStyleProperty( ElementStyleKeys.PADDING_LEFT, new Float( left ) );
    styleSheet.setStyleProperty( ElementStyleKeys.PADDING_BOTTOM, new Float( bottom ) );
    styleSheet.setStyleProperty( ElementStyleKeys.PADDING_RIGHT, new Float( right ) );
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
