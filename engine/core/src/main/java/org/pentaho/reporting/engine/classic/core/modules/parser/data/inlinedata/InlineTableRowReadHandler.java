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

package org.pentaho.reporting.engine.classic.core.modules.parser.data.inlinedata;

import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.IgnoreAnyChildReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;

public class InlineTableRowReadHandler extends AbstractXmlReadHandler {
  private ArrayList data;
  private Class[] types;
  private int columnCount;

  public InlineTableRowReadHandler( final Class[] types ) {
    if ( types == null ) {
      throw new NullPointerException( "Type-array must not be null." );
    }
    this.types = (Class[]) types.clone();
    this.data = new ArrayList();
  }

  /**
   * Returns the handler for a child element.
   *
   * @param uri
   *          the URI of the namespace of the current element.
   * @param tagName
   *          the tag name.
   * @param atts
   *          the attributes.
   * @return the handler or null, if the tagname is invalid.
   * @throws SAXException
   *           if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild( final String uri, final String tagName, final Attributes atts )
    throws SAXException {
    if ( isSameNamespace( uri ) == false ) {
      return null;
    }

    if ( "data".equals( tagName ) ) {
      if ( columnCount >= types.length ) {
        return new IgnoreAnyChildReadHandler();
      }

      final InlineTableDataReadHandler dataReadHandler = new InlineTableDataReadHandler( types[columnCount] );
      columnCount += 1;
      data.add( dataReadHandler );
      return dataReadHandler;
    }

    return null;
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException
   *           if an parser error occured.
   */
  public Object getObject() throws SAXException {
    final Object[] result = new Object[types.length];

    final int size = Math.min( data.size(), types.length );
    for ( int i = 0; i < size; i++ ) {
      final InlineTableDataReadHandler handler = (InlineTableDataReadHandler) data.get( i );
      result[i] = handler.getObject();
    }

    return result;
  }
}
