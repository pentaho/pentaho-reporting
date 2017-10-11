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
