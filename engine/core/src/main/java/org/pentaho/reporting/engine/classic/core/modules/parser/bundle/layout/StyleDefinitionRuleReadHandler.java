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


package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout;

import org.pentaho.reporting.engine.classic.core.style.css.ElementStyleRule;
import org.pentaho.reporting.engine.classic.core.style.css.selector.CSSSelector;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;
import java.util.List;

public class StyleDefinitionRuleReadHandler extends AbstractXmlReadHandler {
  private ElementStyleRule styleRule;
  private ArrayList<StyleSelectorReadHandler> selectorReadHandlers;

  public StyleDefinitionRuleReadHandler() {
    selectorReadHandlers = new ArrayList<StyleSelectorReadHandler>();
    styleRule = new ElementStyleRule();
  }

  protected XmlReadHandler getHandlerForChild( final String uri, final String tagName, final Attributes atts )
    throws SAXException {
    if ( isSameNamespace( uri ) ) {
      if ( "selector".equals( tagName ) ) {
        final StyleSelectorReadHandler readHandler = new StyleSelectorReadHandler();
        selectorReadHandlers.add( readHandler );
        return readHandler;
      }
      if ( "styles".equals( tagName ) ) {
        return new ElementStyleReadHandler( styleRule );
      }
    }
    return null;
  }

  protected void doneParsing() throws SAXException {
    for ( int i = 0; i < selectorReadHandlers.size(); i++ ) {
      final StyleSelectorReadHandler readHandler = selectorReadHandlers.get( i );
      final List<CSSSelector> object = readHandler.getObject();
      for ( int j = 0; j < object.size(); j++ ) {
        final CSSSelector cssSelector = object.get( j );
        styleRule.addSelector( cssSelector );
      }
    }
  }

  public ElementStyleRule getObject() throws SAXException {
    return styleRule;
  }
}
