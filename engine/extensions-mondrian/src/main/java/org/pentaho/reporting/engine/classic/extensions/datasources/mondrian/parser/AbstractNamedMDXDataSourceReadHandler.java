/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.parser;

import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.QueryDefinitionReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.QueryDefinitionsReadHandler;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.AbstractNamedMDXDataFactory;
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
    if ( isSameNamespace( uri ) == false ) {
      return null;
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
