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

package org.pentaho.reporting.engine.classic.core.metadata.parser;

import java.util.ArrayList;

import org.pentaho.reporting.engine.classic.core.metadata.builder.StyleMetaDataBuilder;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class StyleGroupReadHandler extends AbstractXmlReadHandler {
  private ArrayList<StyleReadHandler> styleHandlers;
  private String name;
  private GlobalMetaDefinition styleGroups;
  private StyleGroup group;
  private String bundle;

  public StyleGroupReadHandler( final GlobalMetaDefinition styleGroups ) {
    this.styleGroups = styleGroups;
    this.styleHandlers = new ArrayList<StyleReadHandler>();
  }

  /**
   * Starts parsing.
   *
   * @param attrs
   *          the attributes.
   * @throws SAXException
   *           if there is a parsing error.
   */
  protected void startParsing( final Attributes attrs ) throws SAXException {
    name = attrs.getValue( getUri(), "name" ); // NON-NLS
    if ( name == null ) {
      throw new ParseException( "Attribute 'name' is undefined", getLocator() );
    }

    bundle = attrs.getValue( getUri(), "bundle-name" ); // NON-NLS
  }

  /**
   * Returns the handler for a child element.
   *
   * @param uri
   *          the URI of the namespace of the current element.
   * @param tagName
   *          the tag name.
   * @param atts
   *          the attributes.
   * @return the handler or null, if the tagname is invalid.
   * @throws SAXException
   *           if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild( final String uri, final String tagName, final Attributes atts )
    throws SAXException {
    if ( getUri().equals( uri ) == false ) {
      return null;
    }
    if ( "style".equals( tagName ) ) { // NON-NLS
      final StyleReadHandler handler = new StyleReadHandler( bundle );
      styleHandlers.add( handler );
      return handler;
    }
    return null;
  }

  /**
   * Done parsing.
   *
   * @throws SAXException
   *           if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    final ArrayList<StyleMetaDataBuilder> styles = new ArrayList<StyleMetaDataBuilder>();
    for ( final StyleReadHandler styleHandler : styleHandlers ) {
      if ( styleHandler.getBuilder().getKey() == null ) {
        throw new IllegalStateException();
      }
      styles.add( styleHandler.getBuilder().clone() );
    }
    group = new StyleGroup( name, styles );
    styleGroups.addStyleGroup( group );
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException
   *           if an parser error occured.
   */
  public Object getObject() throws SAXException {
    return group;
  }
}
