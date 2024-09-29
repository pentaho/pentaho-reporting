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


package org.pentaho.reporting.engine.classic.wizard.parser;

import org.pentaho.reporting.engine.classic.wizard.model.DetailFieldDefinition;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;

public class DetailsFieldDefinitionsReadHandler extends AbstractXmlReadHandler {
  private ArrayList readHandlers;
  private DetailFieldDefinition[] result;

  public DetailsFieldDefinitionsReadHandler() {
    readHandlers = new ArrayList();
  }

  protected XmlReadHandler getHandlerForChild( final String uri,
                                               final String tagName,
                                               final Attributes atts ) throws SAXException {
    if ( isSameNamespace( uri ) == false ) {
      return null;
    }
    if ( "detail-field".equals( tagName ) ) {
      final DetailsFieldDefinitionReadHandler readHandler = new DetailsFieldDefinitionReadHandler();
      readHandlers.add( readHandler );
      return readHandler;
    }
    return null;
  }

  protected void doneParsing() throws SAXException {
    result = new DetailFieldDefinition[ readHandlers.size() ];
    for ( int i = 0; i < readHandlers.size(); i++ ) {
      final DetailsFieldDefinitionReadHandler handler = (DetailsFieldDefinitionReadHandler) readHandlers.get( i );
      result[ i ] = (DetailFieldDefinition) handler.getObject();
    }
  }

  public Object getObject() throws SAXException {
    return getDetailFieldDefinitions();
  }

  public DetailFieldDefinition[] getDetailFieldDefinitions() {
    return (DetailFieldDefinition[]) result.clone();
  }

}
