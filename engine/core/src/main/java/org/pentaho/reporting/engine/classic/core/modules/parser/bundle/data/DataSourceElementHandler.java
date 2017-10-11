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

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.data;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class DataSourceElementHandler extends AbstractXmlReadHandler {
  private DataFactory dataFactory;
  private String query;
  private int queryLimit;
  private int queryTimeout;

  public DataSourceElementHandler() {
  }

  /**
   * Starts parsing.
   *
   * @param attrs
   *          the attributes.
   * @throws SAXException
   *           if there is a parsing error.
   */
  protected void startParsing( final Attributes attrs ) throws SAXException {
    query = attrs.getValue( getUri(), "report-query" );
    final String queryLimitText = attrs.getValue( getUri(), "limit" );
    if ( queryLimitText == null ) {
      queryLimit = 0;
    } else {
      queryLimit = ParserUtil.parseInt( queryLimitText, 0 );
    }

    final String queryTimeoutText = attrs.getValue( getUri(), "timeout" );
    if ( queryTimeoutText == null ) {
      queryTimeout = 0;
    } else {
      queryTimeout = ParserUtil.parseInt( queryTimeoutText, 0 );
    }

    final String href = attrs.getValue( getUri(), "ref" );
    if ( href != null ) {
      try {
        dataFactory = (DataFactory) this.performExternalParsing( href, DataFactory.class );
      } catch ( ResourceLoadingException e ) {
        throw new ParseException( "Failed to load data-source definition: " + href, e, getLocator() );
      }
    }
  }

  public DataFactory getDataFactory() {
    return dataFactory;
  }

  public String getQuery() {
    return query;
  }

  public int getQueryLimit() {
    return queryLimit;
  }

  public int getQueryTimeout() {
    return queryTimeout;
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException
   *           if an parser error occured.
   */
  public Object getObject() throws SAXException {
    return null;
  }
}
