/*
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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.parser.ext.readhandlers;

import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportParserUtil;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.libraries.xmlns.parser.IgnoreAnyChildReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.RootXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.SAXException;

import java.util.HashMap;

public class StyleReadHandler extends CompoundObjectReadHandler {

  private HashMap<String, ElementStyleSheet> styleSheetCollection;
  private ElementStyleSheet styleSheet;
  private boolean createStyle;

  public StyleReadHandler() {
    this( null );
  }

  public StyleReadHandler( final ElementStyleSheet styleSheet ) {
    super( new ElementStyleSheetObjectDescription() );
    this.styleSheet = styleSheet;
    this.createStyle = ( styleSheet == null );
  }

  /**
   * Initialises the handler.
   *
   * @param rootHandler
   *          the root handler.
   * @param tagName
   *          the tag name.
   */
  public void init( final RootXmlReadHandler rootHandler, final String uri, final String tagName ) throws SAXException {
    super.init( rootHandler, uri, tagName );
    styleSheetCollection =
        (HashMap<String, ElementStyleSheet>) rootHandler.getHelperObject( ReportParserUtil.HELPER_OBJ_LEGACY_STYLES );
  }

  /**
   * Starts parsing.
   *
   * @param attrs
   *          the attributes.
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  protected void startParsing( final PropertyAttributes attrs ) throws SAXException {
    if ( createStyle ) {
      final String name = attrs.getValue( getUri(), "name" );
      if ( name == null ) {
        throw new ParseException( "Required attribute 'name' is missing.", getLocator() );
      }
      styleSheet = new ElementStyleSheet();
      styleSheetCollection.put( name, styleSheet );
    }

    final ElementStyleSheetObjectDescription objectDescription =
        (ElementStyleSheetObjectDescription) getObjectDescription();
    objectDescription.init( getRootHandler(), styleSheet );
  }

  /**
   * Returns the handler for a child element.
   *
   * @param tagName
   *          the tag name.
   * @param atts
   *          the attributes.
   * @return the handler or null, if the tagname is invalid.
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild( final String uri, final String tagName, final PropertyAttributes atts )
    throws SAXException {
    if ( isSameNamespace( uri ) == false ) {
      return null;
    }

    if ( "extends".equals( tagName ) ) {
      return new StyleExtendsReadHandler( styleSheetCollection, styleSheet );
    }

    if ( "basic-key".equals( tagName ) ) {
      final String name = atts.getValue( getUri(), "name" );
      if ( ElementStyleKeys.isLegacyKey( name ) ) {
        return new IgnoreAnyChildReadHandler();
      }
      return handleBasicObject( atts );
    } else if ( "compound-key".equals( tagName ) ) {
      final String name = atts.getValue( getUri(), "name" );
      if ( ElementStyleKeys.isLegacyKey( name ) ) {
        return new IgnoreAnyChildReadHandler();
      }
      return handleCompoundObject( atts );
    }
    return null;
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   */
  public Object getObject() {
    return styleSheet;
  }
}
