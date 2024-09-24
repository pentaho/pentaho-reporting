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

package org.pentaho.reporting.engine.classic.core.modules.parser.base.common;

import org.pentaho.reporting.libraries.xmlns.parser.PropertiesReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.PropertyReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class PasswordPropertiesReadHandler extends PropertiesReadHandler {
  public PasswordPropertiesReadHandler() {
  }

  public PasswordPropertiesReadHandler( final String propertyTagName ) {
    super( propertyTagName );
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
  protected XmlReadHandler getHandlerForChild( final String uri, final String tagName, final Attributes atts )
    throws SAXException {
    if ( isSameNamespace( uri ) == false ) {
      return null;
    }
    if ( tagName.equals( getPropertyTagName() ) ) {
      final String attrName = atts.getValue( "name" );
      final PropertyReadHandler prh;
      if ( attrName != null && attrName.toLowerCase().contains( "password" ) ) {
        prh = new PasswordPropertyReadHandler();
      } else {
        prh = new PropertyReadHandler();
      }
      getPropertyHandlers().add( prh );
      return prh;
    }
    return null;
  }

}
