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

package org.pentaho.reporting.engine.classic.extensions.datasources.xpath.parser;

import org.pentaho.reporting.libraries.xmlns.parser.PropertyReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class XPathQueryReadHandler extends PropertyReadHandler {
  private boolean legacyMode;

  public XPathQueryReadHandler() {
  }

  /**
   * Starts parsing.
   *
   * @param attrs the attributes.
   * @throws org.xml.sax.SAXException if there is a parsing error.
   */
  protected void startParsing( final Attributes attrs ) throws SAXException {
    super.startParsing( attrs );
    if ( "false".equals( attrs.getValue( getUri(), "legacy-mode" ) ) ) {
      this.legacyMode = false;
    } else {
      this.legacyMode = true;
    }
  }

  public boolean isLegacyMode() {
    return legacyMode;
  }
}
