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

package org.pentaho.reporting.engine.classic.core.modules.parser.data.sql;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.ConnectionProvider;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.SQLReportDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.DataFactoryReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.QueryDefinitionReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.QueryDefinitionsReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.PropertyReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;

public class SQLDataSourceReadHandler extends AbstractXmlReadHandler implements DataFactoryReadHandler {
  private ConnectionReadHandler connectionProviderReadHandler;
  private ArrayList<PropertyReadHandler> queries;
  private ConfigReadHandler configReadHandler;
  private DataFactory dataFactory;
  private QueryDefinitionsReadHandler queryDefinitionsReadHandler;
  private PropertyReadHandler globalScriptReadHandler;

  public SQLDataSourceReadHandler() {
    queries = new ArrayList<PropertyReadHandler>();
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
    final ConnectionReadHandlerFactory factory = ConnectionReadHandlerFactory.getInstance();
    final XmlReadHandler handler = factory.getHandler( uri, tagName );
    if ( handler instanceof ConnectionReadHandler ) {
      connectionProviderReadHandler = (ConnectionReadHandler) handler;
      return connectionProviderReadHandler;
    }

    if ( isSameNamespace( uri ) == false ) {
      return null;
    }

    if ( "config".equals( tagName ) ) {
      configReadHandler = new ConfigReadHandler();
      return configReadHandler;
    }
    if ( "query".equals( tagName ) ) {
      final PropertyReadHandler queryReadHandler = new PropertyReadHandler();
      queries.add( queryReadHandler );
      return queryReadHandler;
    }

    if ( "global-script".equals( tagName ) ) {
      globalScriptReadHandler = new PropertyReadHandler( "language", true );
      return globalScriptReadHandler;
    }

    if ( "query-definitions".equals( tagName ) ) {
      queryDefinitionsReadHandler = new QueryDefinitionsReadHandler();
      return queryDefinitionsReadHandler;
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
    ConnectionProvider provider = null;
    if ( connectionProviderReadHandler != null ) {
      provider = (ConnectionProvider) connectionProviderReadHandler.getObject();
    }
    if ( provider == null ) {
      provider = (ConnectionProvider) getRootHandler().getHelperObject( "connection-provider" );
    }
    if ( provider == null ) {
      throw new SAXException( "Unable to create SQL Factory: No connection provider specified or recognized." );
    }

    final SQLReportDataFactory dataFactory = new SQLReportDataFactory( provider );
    for ( int i = 0; i < queries.size(); i++ ) {
      final PropertyReadHandler handler = queries.get( i );
      dataFactory.setQuery( handler.getName(), handler.getResult(), null, null );
    }

    if ( globalScriptReadHandler != null ) {
      dataFactory.setGlobalScript( globalScriptReadHandler.getResult() );
      dataFactory.setGlobalScriptLanguage( globalScriptReadHandler.getName() );
    }

    if ( queryDefinitionsReadHandler != null ) {
      final ArrayList<QueryDefinitionReadHandler> scriptedQueries = queryDefinitionsReadHandler.getScriptedQueries();
      for ( final QueryDefinitionReadHandler scriptedQuery : scriptedQueries ) {
        dataFactory.setQuery( scriptedQuery.getName(), scriptedQuery.getQuery(), scriptedQuery.getScriptLanguage(),
            scriptedQuery.getScript() );
      }
    }

    this.dataFactory = dataFactory;
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
