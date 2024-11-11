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

import org.pentaho.reporting.engine.classic.core.style.css.CSSParseException;
import org.pentaho.reporting.engine.classic.core.style.css.StyleSheetParserUtil;
import org.pentaho.reporting.engine.classic.core.style.css.namespaces.NamespaceCollection;
import org.pentaho.reporting.engine.classic.core.style.css.selector.CSSSelector;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.StringReadHandler;
import org.w3c.css.sac.SelectorList;
import org.xml.sax.SAXException;

import java.util.ArrayList;
import java.util.List;

public class StyleSelectorReadHandler extends StringReadHandler {
  private ArrayList<CSSSelector> selector;

  public StyleSelectorReadHandler() {
    selector = new ArrayList<CSSSelector>();
  }

  protected void doneParsing() throws SAXException {
    super.doneParsing();
    try {
      final NamespaceCollection namespaceCollection = StyleSheetParserUtil.getInstance().getNamespaceCollection();
      final SelectorList selectorList =
          StyleSheetParserUtil.getInstance().parseSelector( namespaceCollection, getResult() );
      for ( int i = 0; i < selectorList.getLength(); i += 1 ) {
        selector.add( (CSSSelector) selectorList.item( i ) );
      }
    } catch ( CSSParseException e ) {
      throw new ParseException( "Failed to parse selector", e, getLocator() );
    }
  }

  public List<CSSSelector> getObject() {
    return selector;
  }
}
