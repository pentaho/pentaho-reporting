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


package org.pentaho.reporting.engine.classic.core.style;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.style.css.StyleSheetParserUtil;
import org.pentaho.reporting.engine.classic.core.style.css.namespaces.NamespaceCollection;
import org.pentaho.reporting.engine.classic.core.style.css.selector.CSSSelector;
import org.w3c.css.sac.SelectorList;

public class StyleSelectorParserTest extends TestCase {
  public StyleSelectorParserTest() {
  }

  public StyleSelectorParserTest( final String name ) {
    super( name );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testParsing() throws Exception {
    final NamespaceCollection namespaceCollection = StyleSheetParserUtil.getInstance().getNamespaceCollection();
    final SelectorList selectorList =
        StyleSheetParserUtil.getInstance().parseSelector( namespaceCollection, "h1.test[x-lang=\"fr'\\\"\"]" );
    for ( int i = 0; i < selectorList.getLength(); i += 1 ) {
      CSSSelector item = (CSSSelector) selectorList.item( i );
      System.out.println( item.print( namespaceCollection ) );
    }
  }

  public void testParsingClass() throws Exception {
    final NamespaceCollection namespaceCollection = StyleSheetParserUtil.getInstance().getNamespaceCollection();
    final SelectorList selectorList =
        StyleSheetParserUtil.getInstance().parseSelector( namespaceCollection, ".\\aa  test" );
    for ( int i = 0; i < selectorList.getLength(); i += 1 ) {
      CSSSelector item = (CSSSelector) selectorList.item( i );
      System.out.println( item.print( namespaceCollection ) );
    }
  }

}
