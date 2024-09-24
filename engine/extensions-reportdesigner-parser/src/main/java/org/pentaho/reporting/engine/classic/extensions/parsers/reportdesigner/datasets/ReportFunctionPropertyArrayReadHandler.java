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

package org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.datasets;

import org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.converter.ObjectConverterFactory;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.StringReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ReportFunctionPropertyArrayReadHandler extends StringReadHandler {
  private Class componentType;
  private Object value;
  private String propertyName;

  public ReportFunctionPropertyArrayReadHandler( final Class componentType ) {
    this.componentType = componentType;
  }

  /**
   * Starts parsing.
   *
   * @param attrs the attributes.
   * @throws SAXException if there is a parsing error.
   */
  protected void startParsing( final Attributes attrs ) throws SAXException {
    super.startParsing( attrs );
    propertyName = attrs.getValue( getUri(), "name" );
    if ( propertyName == null ) {
      throw new ParseException( "Required attribute 'name' is null.", getLocator() );
    }
  }

  /**
   * Done parsing.
   *
   * @throws SAXException if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    super.doneParsing();
    value = ObjectConverterFactory.convert( componentType, getResult(), getLocator() );
  }

  public String getPropertyName() {
    return propertyName;
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException if an parser error occured.
   */
  public Object getObject() {
    return value;
  }
}
