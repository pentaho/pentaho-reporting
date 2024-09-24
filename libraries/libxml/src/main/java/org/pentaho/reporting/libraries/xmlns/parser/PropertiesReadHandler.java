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

package org.pentaho.reporting.libraries.xmlns.parser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;
import java.util.Properties;

/**
 * A read handler that creates property-structures (name-value-pairs) and returns the properties as java.util.Properties
 * collection.
 *
 * @author Thomas Morgner
 */
public class PropertiesReadHandler extends AbstractXmlReadHandler {
  private ArrayList<PropertyReadHandler> propertyHandlers;
  private String propertyTagName;
  private Properties result;

  /**
   * Creates a properties read-handler using "property" as child-tagname.
   */
  public PropertiesReadHandler() {
    this( "property" );
  }

  /**
   * Creates a properties read-handler using the specified propertyTagName as child-tagname.
   *
   * @param propertyTagName the tag name for the child elements that define the properties.
   */
  public PropertiesReadHandler( final String propertyTagName ) {
    if ( propertyTagName == null ) {
      throw new NullPointerException();
    }
    this.propertyHandlers = new ArrayList<PropertyReadHandler>();
    this.propertyTagName = propertyTagName;
  }

  protected ArrayList<PropertyReadHandler> getPropertyHandlers() {
    return propertyHandlers;
  }

  protected String getPropertyTagName() {
    return propertyTagName;
  }

  /**
   * Returns the handler for a child element.
   *
   * @param tagName the tag name.
   * @param atts    the attributes.
   * @return the handler or null, if the tagname is invalid.
   * @throws SAXException if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild( final String uri,
                                               final String tagName,
                                               final Attributes atts )
    throws SAXException {
    if ( isSameNamespace( uri ) == false ) {
      return null;
    }
    if ( tagName.equals( propertyTagName ) ) {
      final PropertyReadHandler prh = new PropertyReadHandler();
      propertyHandlers.add( prh );
      return prh;
    }
    return null;
  }

  /**
   * Done parsing.
   *
   * @throws SAXException if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    result = new Properties();
    for ( int i = 0; i < propertyHandlers.size(); i++ ) {
      final PropertyReadHandler handler = propertyHandlers.get( i );
      result.setProperty( handler.getName(), handler.getResult() );
    }
  }

  /**
   * Returns the resulting properties collection, never null.
   *
   * @return the properties.
   */
  public Properties getResult() {
    return result;
  }

  /**
   * Returns the resulting properties collection, never null.
   *
   * @return the properties.
   * @throws SAXException if there is a parsing error.
   */
  public Object getObject() throws SAXException {
    return result;
  }
}
