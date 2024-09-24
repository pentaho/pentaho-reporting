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

package org.pentaho.reporting.engine.classic.core.modules.parser.base.common;

import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanPropertyLookupParser;
import org.xml.sax.SAXException;

import java.io.Serializable;

public class PropertyStringReadHandler extends AbstractPropertyXmlReadHandler implements Serializable {
  private class StringLookupParser extends BeanPropertyLookupParser {
    protected StringLookupParser() {
    }

    protected Object performInitialLookup( final String name ) {
      return getRootHandler().getHelperObject( name );
    }
  }

  private StringBuffer buffer;
  private StringLookupParser lookupParser;

  public PropertyStringReadHandler() {
    this.buffer = new StringBuffer( 100 );
    this.lookupParser = new StringLookupParser();
  }

  /**
   * This method is called to process the character data between element tags.
   *
   * @param ch
   *          the character buffer.
   * @param start
   *          the start index.
   * @param length
   *          the length.
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  public void characters( final char[] ch, final int start, final int length ) throws SAXException {
    buffer.append( ch, start, length );
  }

  public String getResult() {
    if ( Boolean.TRUE.equals( getRootHandler().getHelperObject( "property-expansion" ) ) ) {
      return lookupParser.translateAndLookup( buffer.toString() );
    }
    return buffer.toString();
  }

  public void startParsing( final PropertyAttributes attrs ) throws SAXException {
    super.startParsing( attrs );
  }

  public void doneParsing() throws SAXException {
    super.doneParsing();
  }

  /**
   * Returns the object for this element.
   *
   * @return the object.
   */
  public Object getObject() {
    return getResult();
  }
}
