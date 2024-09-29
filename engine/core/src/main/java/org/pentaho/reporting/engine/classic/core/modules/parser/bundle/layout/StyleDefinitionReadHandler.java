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


package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout;

import org.pentaho.reporting.engine.classic.core.style.css.ElementStyleDefinition;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;

public class StyleDefinitionReadHandler extends AbstractXmlReadHandler {
  private ArrayList<StyleDefinitionReadHandler> styleDefinitionReadHandlers;
  private ArrayList<StyleDefinitionRuleReadHandler> styleRuleReadHandler;
  private ElementStyleDefinition result;

  public StyleDefinitionReadHandler() {
    styleDefinitionReadHandlers = new ArrayList<StyleDefinitionReadHandler>();
    styleRuleReadHandler = new ArrayList<StyleDefinitionRuleReadHandler>();
    result = new ElementStyleDefinition();
  }

  protected XmlReadHandler getHandlerForChild( final String uri, final String tagName, final Attributes atts )
    throws SAXException {
    if ( isSameNamespace( uri ) ) {
      if ( "rule".equals( tagName ) ) {
        final StyleDefinitionRuleReadHandler readHandler = new StyleDefinitionRuleReadHandler();
        styleRuleReadHandler.add( readHandler );
        return readHandler;
      }

      if ( "style-definition".equals( tagName ) ) {
        final StyleDefinitionReadHandler readHandler = new StyleDefinitionReadHandler();
        styleDefinitionReadHandlers.add( readHandler );
        return readHandler;
      }
    }
    return null;
  }

  protected void doneParsing() throws SAXException {
    for ( int i = 0; i < styleDefinitionReadHandlers.size(); i++ ) {
      final StyleDefinitionReadHandler definitionReadHandler = styleDefinitionReadHandlers.get( i );
      result.addStyleSheet( definitionReadHandler.getObject() );
    }
    for ( int i = 0; i < styleRuleReadHandler.size(); i++ ) {
      final StyleDefinitionRuleReadHandler ruleReadHandler = styleRuleReadHandler.get( i );
      result.addRule( ruleReadHandler.getObject() );
    }
  }

  public ElementStyleDefinition getObject() throws SAXException {
    return result;
  }
}
