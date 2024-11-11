/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.wizard.parser;

import org.pentaho.reporting.engine.classic.wizard.model.GroupDefinition;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;

public class GroupDefinitionsReadHandler extends AbstractXmlReadHandler {
  private ArrayList readHandlers;
  private GroupDefinition[] result;

  public GroupDefinitionsReadHandler() {
    readHandlers = new ArrayList();
  }

  protected XmlReadHandler getHandlerForChild( final String uri,
                                               final String tagName,
                                               final Attributes atts ) throws SAXException {
    if ( isSameNamespace( uri ) == false ) {
      return null;
    }
    if ( "group-definition".equals( tagName ) ) {
      final GroupDefinitionReadHandler readHandler = new GroupDefinitionReadHandler();
      readHandlers.add( readHandler );
      return readHandler;
    }
    return null;
  }

  protected void doneParsing() throws SAXException {
    result = new GroupDefinition[ readHandlers.size() ];
    for ( int i = 0; i < readHandlers.size(); i++ ) {
      final GroupDefinitionReadHandler handler = (GroupDefinitionReadHandler) readHandlers.get( i );
      result[ i ] = (GroupDefinition) handler.getObject();
    }
  }

  public Object getObject() throws SAXException {
    return getGroupDefinitions();
  }

  public GroupDefinition[] getGroupDefinitions() {
    return (GroupDefinition[]) result.clone();
  }
}
