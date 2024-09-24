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
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.SAXException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ParserConfigurationReadHandler extends AbstractPropertyXmlReadHandler {
  private HashMap fieldHandlers;

  public ParserConfigurationReadHandler() {
    this.fieldHandlers = new HashMap();
  }

  /**
   * Returns the handler for a child element.
   *
   * @param tagName
   *          the tag name.
   * @param atts
   *          the attributes.
   * @return the handler or null, if the tagname is invalid.
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild( final String uri, final String tagName, final PropertyAttributes atts )
    throws SAXException {
    if ( getUri().equals( uri ) == false ) {
      return null;
    }

    if ( "property".equals( tagName ) ) {
      final String name = atts.getValue( getUri(), "name" );
      if ( name == null ) {
        throw new ParseException( "Required attribute 'name' is missing.", getLocator() );
      }

      final PropertyReferenceReadHandler readHandler = new PropertyReferenceReadHandler();
      fieldHandlers.put( name, readHandler );
      return readHandler;
    }
    return null;
  }

  /**
   * Done parsing.
   *
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    final Iterator it = fieldHandlers.entrySet().iterator();
    while ( it.hasNext() ) {
      final Map.Entry entry = (Map.Entry) it.next();
      final String key = (String) entry.getKey();
      if ( key.startsWith( "::" ) ) {
        throw new ParseException( "The key value '" + key
            + "' is invalid. Internal keys (starting with '::') cannot be redefined.", getLocator() );
      }
      final PropertyReferenceReadHandler readHandler = (PropertyReferenceReadHandler) entry.getValue();
      getRootHandler().setHelperObject( key, readHandler.getObject() );
    }
  }

  /**
   * Returns the object for this element.
   *
   * @return the object.
   */
  public Object getObject() {
    return null;
  }
}
