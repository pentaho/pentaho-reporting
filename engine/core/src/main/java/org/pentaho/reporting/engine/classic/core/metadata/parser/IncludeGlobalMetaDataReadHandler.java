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

import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class IncludeGlobalMetaDataReadHandler extends AbstractXmlReadHandler {
  private GlobalMetaDefinition result;
  private GlobalMetaDefinition globalMetaDefinition;

  public IncludeGlobalMetaDataReadHandler( final GlobalMetaDefinition globalMetaDefinition ) {
    this.globalMetaDefinition = globalMetaDefinition;
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
    final String href = attrs.getValue( getUri(), "src" );
    if ( href == null ) {
      throw new ParseException( "Required attribute 'src' is missing", getLocator() );
    }

    final ResourceManager resourceManager = getRootHandler().getResourceManager();
    final ResourceKey context = getRootHandler().getContext();
    try {
      final ResourceKey resourceKey = resourceManager.deriveKey( context, href );
      final Resource resource = resourceManager.create( resourceKey, null, GlobalMetaDefinition.class );
      result = (GlobalMetaDefinition) resource.getResource();
    } catch ( ResourceException e ) {
      throw new ParseException( "Failed to parse included global definitions", e, getLocator() );
    }

    if ( globalMetaDefinition != null ) {
      globalMetaDefinition.merge( result );
    }
  }

  public GlobalMetaDefinition getResult() {
    return result;
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException
   *           if an parser error occured.
   */
  public Object getObject() throws SAXException {
    return result;
  }
}
