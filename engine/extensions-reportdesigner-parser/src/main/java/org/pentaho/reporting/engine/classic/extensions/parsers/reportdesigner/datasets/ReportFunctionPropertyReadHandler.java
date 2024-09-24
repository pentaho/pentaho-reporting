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

import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanUtility;
import org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.converter.ObjectConverterFactory;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.StringReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ReportFunctionPropertyReadHandler extends StringReadHandler {
  private String propertyName;
  private BeanUtility beanUtility;
  private boolean array;
  private ArrayList properties;

  public ReportFunctionPropertyReadHandler( final BeanUtility beanUtility ) {
    if ( beanUtility == null ) {
      throw new NullPointerException( "No current beanUtility" );
    }

    this.beanUtility = beanUtility;
    this.properties = new ArrayList();
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

    // yes, this is how the report designer parses this property, so we have to follow that strange road too
    array = ( attrs.getValue( getUri(), "array" ) != null );
  }

  /**
   * Done parsing.
   *
   * @throws SAXException if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    super.doneParsing();
    try {
      final Class propertyType = beanUtility.getPropertyType( propertyName );
      if ( array == false ) {
        final String value = getResult();
        final Object o = ObjectConverterFactory.convert( propertyType, value, getLocator() );
        beanUtility.setProperty( propertyName, o );
      } else {
        final Object[] value = (Object[]) Array.newInstance( propertyType, properties.size() );
        for ( int i = 0; i < properties.size(); i++ ) {
          final ReportFunctionPropertyArrayReadHandler handler =
            (ReportFunctionPropertyArrayReadHandler) properties.get( i );
          value[ i ] = handler.getObject();
        }
        beanUtility.setProperty( propertyName, value );
      }
    } catch ( BeanException e ) {
      throw new ParseException( "Failed to set property", getLocator() );
    }
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException if an parser error occured.
   */
  public Object getObject() {
    return null;
  }
}
