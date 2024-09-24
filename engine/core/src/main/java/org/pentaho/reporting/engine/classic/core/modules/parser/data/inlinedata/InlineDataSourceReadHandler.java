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

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.DataFactoryReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;

/**
 * Creation-Date: 07.04.2006, 17:47:53
 *
 * @author Thomas Morgner
 */
public class InlineDataSourceReadHandler extends AbstractXmlReadHandler implements DataFactoryReadHandler {
  private ArrayList queries;
  private DataFactory dataFactory;

  public InlineDataSourceReadHandler() {
    queries = new ArrayList();
  }

  /**
   * Returns the handler for a child element.
   *
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

    if ( "inline-table".equals( tagName ) ) {
      final InlineTableReadHandler queryReadHandler = new InlineTableReadHandler();
      queries.add( queryReadHandler );
      return queryReadHandler;
    }
    return null;
  }

  /**
   * Done parsing.
   *
   * @throws SAXException
   *           if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    final TableDataFactory srdf = new TableDataFactory();
    for ( int i = 0; i < queries.size(); i++ ) {
      final InlineTableReadHandler handler = (InlineTableReadHandler) queries.get( i );
      srdf.addTable( handler.getName(), handler.getData() );
    }

    dataFactory = srdf;
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException
   *           if there is a parsing error.
   */
  public Object getObject() throws SAXException {
    return dataFactory;
  }

  public DataFactory getDataFactory() {
    return dataFactory;
  }
}
