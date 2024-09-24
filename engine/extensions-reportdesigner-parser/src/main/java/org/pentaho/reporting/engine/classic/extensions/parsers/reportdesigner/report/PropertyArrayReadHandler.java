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

package org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.report;

import org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.converter.ObjectConverterFactory;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.PropertiesReadHandler;
import org.xml.sax.SAXException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class PropertyArrayReadHandler extends PropertiesReadHandler {
  private Class componentType;
  private Object[] retval;

  public PropertyArrayReadHandler( final Class componentType ) {
    this.componentType = componentType;
  }

  /**
   * Done parsing.
   *
   * @throws SAXException if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    super.doneParsing();
    final Properties properties = getResult();
    final ArrayList list = new ArrayList();
    final Iterator entries = properties.entrySet().iterator();
    while ( entries.hasNext() ) {
      final Map.Entry o = (Map.Entry) entries.next();
      final String key = (String) o.getKey();
      final String value = (String) o.getValue();
      try {
        int index = Integer.parseInt( key );
        if ( index < 0 ) {
          throw new ParseException( "Failed to parse array index", getLocator() );
        }

        list.ensureCapacity( index );
        while ( list.size() < ( index + 1 ) ) {
          list.add( null );
        }
        list.set( index, ObjectConverterFactory.convert( componentType, value, getLocator() ) );
      } catch ( NumberFormatException nfe ) {
        throw new ParseException( "Failed to parse array index", getLocator() );
      }
    }
    retval = list.toArray();
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException if an parser error occured.
   */
  public Object getObject() {
    return retval;
  }
}
