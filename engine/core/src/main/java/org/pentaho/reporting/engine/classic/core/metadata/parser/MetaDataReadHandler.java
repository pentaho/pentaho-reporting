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
 * Copyright (c) 2001 - 2017 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.metadata.parser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaDataParser;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @noinspection HardCodedStringLiteral
 */
public class MetaDataReadHandler extends AbstractXmlReadHandler {

  private static final Log logger = LogFactory.getLog( MetaDataReadHandler.class );
  private ArrayList<ElementReadHandler> elements;
  private GlobalMetaDefinition globalMetaDefinition;
  private ElementTypeCollection typeCollection;

  public MetaDataReadHandler() {
    globalMetaDefinition = new GlobalMetaDefinition();
    elements = new ArrayList<ElementReadHandler>();
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
    super.startParsing( attrs );

    final ResourceManager resourceManager = getRootHandler().getResourceManager();
    final ResourceKey context = getRootHandler().getContext();

    final Configuration configuration = ClassicEngineBoot.getInstance().getGlobalConfig();
    final Iterator keys = configuration.findPropertyKeys( ElementMetaDataParser.GLOBAL_INCLUDES_PREFIX );
    while ( keys.hasNext() ) {
      final String key = (String) keys.next();
      final String href = configuration.getConfigProperty( key );
      if ( StringUtils.isEmpty( href, true ) ) {
        continue;
      }

      try {
        final ResourceKey resourceKey = resourceManager.deriveKey( context, href );
        final Resource resource = resourceManager.create( resourceKey, null, GlobalMetaDefinition.class );
        globalMetaDefinition.merge( (GlobalMetaDefinition) resource.getResource() );
      } catch ( ResourceException e ) {
        logger.warn( "Failed to parse included global definitions: " + getLocator(), e );
      }
    }

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
    if ( "attribute-group".equals( tagName ) ) {
      return new AttributeGroupReadHandler( globalMetaDefinition );
    }
    if ( "style-group".equals( tagName ) ) {
      return new StyleGroupReadHandler( globalMetaDefinition );
    }
    if ( "include-globals".equals( tagName ) ) {
      return new IncludeGlobalMetaDataReadHandler( globalMetaDefinition );
    }
    if ( "element".equals( tagName ) ) {
      final ElementReadHandler readHandler = new ElementReadHandler( globalMetaDefinition );
      elements.add( readHandler );
      return readHandler;
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
    final ElementMetaData[] result = new ElementMetaData[elements.size()];
    for ( int i = 0; i < elements.size(); i++ ) {
      final ElementReadHandler handler = elements.get( i );
      result[i] = (ElementMetaData) handler.getObject();
    }

    typeCollection = new ElementTypeCollection( result );
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException
   *           if an parser error occured.
   */
  public Object getObject() throws SAXException {
    return typeCollection;
  }
}
