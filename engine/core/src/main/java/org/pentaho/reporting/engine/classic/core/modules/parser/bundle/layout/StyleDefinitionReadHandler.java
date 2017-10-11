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
