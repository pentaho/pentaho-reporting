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
