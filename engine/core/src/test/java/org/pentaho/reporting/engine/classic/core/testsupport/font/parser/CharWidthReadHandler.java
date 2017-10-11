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

package org.pentaho.reporting.engine.classic.core.testsupport.font.parser;

import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class CharWidthReadHandler extends AbstractXmlReadHandler {
  private int codepoint;
  private int value;

  public CharWidthReadHandler() {
  }

  public int getCodepoint() {
    return codepoint;
  }

  public int getValue() {
    return value;
  }

  protected void startParsing( final Attributes attrs ) throws SAXException {
    codepoint = parseOrDie( "codepoint", attrs );
    value = parseOrDie( "value", attrs );
  }

  public Object getObject() throws SAXException {
    return null;
  }

  private int parseOrDie( final String attrName, final Attributes attrs ) throws ParseException {
    final String value = attrs.getValue( getUri(), attrName );
    if ( StringUtils.isEmpty( value ) ) {
      throw new ParseException( "Attribute '" + attrName + "' is missing.", getLocator() );
    }
    try {
      return Integer.parseInt( value );
    } catch ( Exception e ) {
      throw new ParseException( "Attribute '" + attrName + "' with value '" + value + "'is invalid.", getLocator() );
    }
  }
}
