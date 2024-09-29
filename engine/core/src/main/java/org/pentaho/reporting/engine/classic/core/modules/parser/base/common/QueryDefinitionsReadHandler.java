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


package org.pentaho.reporting.engine.classic.core.modules.parser.base.common;

import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;

public class QueryDefinitionsReadHandler extends AbstractXmlReadHandler {
  private ArrayList<QueryDefinitionReadHandler> scriptedQueries;

  public QueryDefinitionsReadHandler() {
    scriptedQueries = new ArrayList<QueryDefinitionReadHandler>();
  }

  protected XmlReadHandler getHandlerForChild( final String uri, final String tagName, final Attributes atts )
    throws SAXException {
    if ( isSameNamespace( uri ) == false ) {
      return null;
    }
    if ( "query".equals( tagName ) ) {
      final QueryDefinitionReadHandler queryReadHandler = new QueryDefinitionReadHandler();
      scriptedQueries.add( queryReadHandler );
      return queryReadHandler;
    }
    return null;
  }

  public Object getObject() throws SAXException {
    return getScriptedQueries();
  }

  public ArrayList<QueryDefinitionReadHandler> getScriptedQueries() {
    return scriptedQueries;
  }
}
