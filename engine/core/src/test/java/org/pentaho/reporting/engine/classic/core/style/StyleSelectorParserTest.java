/*!
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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

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
