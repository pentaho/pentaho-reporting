/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout;

import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextRotation;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class RotationStyleReadHandler extends AbstractXmlReadHandler implements StyleReadHandler {

  private ElementStyleSheet styleSheet;

  public RotationStyleReadHandler() {
  }

  public ElementStyleSheet getStyleSheet() {
    return styleSheet;
  }

  public void setStyleSheet( final ElementStyleSheet styleSheet ) {
    this.styleSheet = styleSheet;
  }


  protected void startParsing( final Attributes attrs ) throws SAXException {
    final String rotation = attrs.getValue( getUri(), "rotation" );
    if ( rotation != null ) {
      styleSheet.setStyleProperty( TextStyleKeys.TEXT_ROTATION, parseTextRotation( rotation ) );
    }
  }

  private TextRotation parseTextRotation( final String attr ) {

    if ( TextRotation.D_90.toString().equalsIgnoreCase( attr ) ) {
      return TextRotation.D_90;
    }

    if ( TextRotation.D_270.toString().equalsIgnoreCase( attr ) ) {
      return TextRotation.D_270;
    }

    return null;
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException if an parser error occured.
   */
  public Object getObject() throws SAXException {
    return styleSheet;
  }
}
