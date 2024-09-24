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

package org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.parser;

import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.QueryDefinitionReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.QueryDefinitionsReadHandler;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.AbstractNamedMDXDataFactory;
import org.pentaho.reporting.libraries.xmlns.parser.PropertyReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;

public abstract class AbstractNamedMDXDataSourceReadHandler extends AbstractMDXDataSourceReadHandler {
  private ArrayList<PropertyReadHandler> queries;
  private QueryDefinitionsReadHandler queryDefinitionsReadHandler;
  private PropertyReadHandler globalScriptReadHandler;

  public AbstractNamedMDXDataSourceReadHandler() {
    queries = new ArrayList<PropertyReadHandler>();
  }

  /**
   * Returns the handler for a child element.
   *
   * @param uri     the URI of the namespace of the current element.
   * @param tagName the tag name.
   * @param atts    the attributes.
   * @return the handler or null, if the tagname is invalid.
   * @throws org.xml.sax.SAXException if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild( final String uri,
                                               final String tagName,
                                               final Attributes atts ) throws SAXException {


    if ( isSameNamespace( uri ) ) {
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
    }
    return super.getHandlerForChild( uri, tagName, atts );
  }

  /**
   * Done parsing.
   *
   * @throws org.xml.sax.SAXException if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    super.doneParsing();
    final AbstractNamedMDXDataFactory dataFactory = (AbstractNamedMDXDataFactory) getDataFactory();
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
        dataFactory.setQuery( scriptedQuery.getName(), scriptedQuery.getQuery(),
          scriptedQuery.getScriptLanguage(), scriptedQuery.getScript() );
      }
    }

  }

}
