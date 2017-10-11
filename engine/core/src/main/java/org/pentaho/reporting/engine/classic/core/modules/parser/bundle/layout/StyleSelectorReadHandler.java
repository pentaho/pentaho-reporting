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
