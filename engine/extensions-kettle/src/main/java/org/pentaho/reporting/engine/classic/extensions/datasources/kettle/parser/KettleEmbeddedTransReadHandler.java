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


package org.pentaho.reporting.engine.classic.extensions.datasources.kettle.parser;

import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.EmbeddedKettleTransformationProducer;
import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.StringReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class KettleEmbeddedTransReadHandler extends AbstractKettleTransformationProducerReadHandler {
  private String pluginId;
  private StringReadHandler resourceReadHandler;
  private String name;

  public KettleEmbeddedTransReadHandler() {
  }

  protected void startParsing( final Attributes attrs ) throws SAXException {
    // note: We do not call super here
    pluginId = attrs.getValue( getUri(), "plugin-id" );
    name = attrs.getValue( getUri(), "name" );
  }


  public String getName() {
    return name;
  }

  protected XmlReadHandler getHandlerForChild( final String uri,
                                               final String tagName,
                                               final Attributes atts ) throws SAXException {
    if ( getUri().equals( uri ) && "resource".equals( tagName ) ) {
      resourceReadHandler = new StringReadHandler();
      return resourceReadHandler;
    }
    return super.getHandlerForChild( uri, tagName, atts );
  }

  public EmbeddedKettleTransformationProducer getObject() throws SAXException {
    if ( resourceReadHandler == null ) {
      throw new ParseException( "Missing tag 'resource'", getLocator() );
    }

    final String resourceName = resourceReadHandler.getResult();

    try {
      byte[] bytes = loadDataFromBundle( resourceName );
      EmbeddedKettleTransformationProducer kprod = new EmbeddedKettleTransformationProducer( getDefinedArgumentNames(),
        getDefinedVariableNames(),
        pluginId,
        bytes );
      kprod.setStopOnError( isStopOnError() );
      return kprod;
    } catch ( final Exception e ) {
      throw new ParseException( "Could not load resource " + resourceName, e, getLocator() );
    }

  }

  private byte[] loadDataFromBundle( final String href ) throws ResourceKeyCreationException, ResourceLoadingException {
    final ResourceKey key = getRootHandler().getSource();
    final ResourceManager manager = getRootHandler().getResourceManager();
    final ResourceKey derivedKey = manager.deriveKey( key, href );
    final ResourceData data = manager.load( derivedKey );
    return data.getResource( manager );
  }
}
