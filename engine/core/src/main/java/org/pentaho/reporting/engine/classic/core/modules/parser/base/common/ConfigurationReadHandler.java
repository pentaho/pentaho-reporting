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
import org.pentaho.reporting.engine.classic.core.modules.parser.base.compat.CompatibilityMapperUtil;
import org.pentaho.reporting.libraries.base.config.ModifiableConfiguration;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.SAXException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ConfigurationReadHandler extends AbstractPropertyXmlReadHandler {
  private ModifiableConfiguration configuration;
  private HashMap fieldHandlers;

  public ConfigurationReadHandler( final ModifiableConfiguration configuration ) {
    this.configuration = configuration;
    this.fieldHandlers = new HashMap();
  }

  /**
   * Returns the handler for a child element.
   *
   * @param tagName
   *          the tag name.
   * @param attrs
   *          the attributes.
   * @return the handler or null, if the tagname is invalid.
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild( final String uri, final String tagName, final PropertyAttributes attrs )
    throws SAXException {
    if ( isSameNamespace( uri ) == false ) {
      return null;
    }

    if ( "property".equals( tagName ) ) {
      final String name = attrs.getValue( getUri(), "name" );
      if ( name == null ) {
        throw new SAXException( "Required attribute 'name' is missing." );
      }

      final PropertyStringReadHandler readHandler = new PropertyStringReadHandler();
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
      final String originalKey = (String) entry.getKey();
      final String key = CompatibilityMapperUtil.mapConfigurationKey( originalKey );
      final PropertyStringReadHandler readHandler = (PropertyStringReadHandler) entry.getValue();
      configuration.setConfigProperty( key, CompatibilityMapperUtil.mapConfigurationValue( originalKey, key,
          readHandler.getResult() ) );
    }
  }

  /**
   * Returns the object for this element.
   *
   * @return the object.
   */
  public Object getObject() {
    return configuration;
  }
}
