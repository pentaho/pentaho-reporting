/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.metadata;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.metadata.parser.ElementTypeCollection;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

public final class ElementTypeRegistry {
  private static final Log logger = LogFactory.getLog( ElementTypeRegistry.class );
  private HashMap<String, String> namespaceMapping;
  private HashMap<String, DefaultElementMetaData> backend;
  private static ElementTypeRegistry instance;
  private ResourceManager resourceManager;

  public static synchronized ElementTypeRegistry getInstance() {
    if ( instance == null ) {
      instance = new ElementTypeRegistry();
    }
    return instance;
  }

  ElementTypeRegistry() {
    this.resourceManager = new ResourceManager();
    this.backend = new HashMap<String, DefaultElementMetaData>();
    this.namespaceMapping = new HashMap<String, String>();
  }

  public void registerFromXml( final URL metaDataSource ) throws IOException {
    if ( metaDataSource == null ) {
      throw new NullPointerException( "Error: Could not find the element meta-data description file" );
    }

    try {
      final Resource resource = resourceManager.createDirectly( metaDataSource, ElementTypeCollection.class );
      final ElementTypeCollection typeCollection = (ElementTypeCollection) resource.getResource();
      final ElementMetaData[] types = typeCollection.getElementTypes();
      for ( int i = 0; i < types.length; i++ ) {
        final ElementMetaData metaData = types[i];
        registerElement( metaData );
      }
    } catch ( Exception e ) {
      logger.debug( "Error: Could not parse the element meta-data description file: " + metaDataSource, e );
      throw new IOException( "Error: Could not parse the element meta-data description file: " + metaDataSource );
    }
  }

  public void registerElement( final ElementMetaData metaData ) {
    if ( metaData == null ) {
      throw new NullPointerException();
    }
    this.backend.put( metaData.getName(), new DefaultElementMetaData( metaData ) );
  }

  public AttributeRegistry getAttributeRegistry( final ElementType identifier ) {
    return getAttributeRegistry( identifier.getMetaData().getName() );
  }

  public AttributeRegistry getAttributeRegistry( final String identifier ) {
    if ( identifier == null ) {
      throw new NullPointerException();
    }
    final DefaultElementMetaData retval = backend.get( identifier );
    if ( retval == null ) {
      throw new MetaDataLookupException( "There is no meta-data defined for type '" + identifier + '\'' );
    }

    return new DefaultAttributeRegistry( retval );
  }

  public ElementMetaData[] getAllElementTypes() {
    return backend.values().toArray( new ElementMetaData[backend.size()] );
  }

  public boolean isElementTypeRegistered( final String identifier ) {
    if ( identifier == null ) {
      throw new NullPointerException();
    }
    return backend.containsKey( identifier );
  }

  public ElementMetaData getElementType( final String identifier ) throws MetaDataLookupException {
    if ( identifier == null ) {
      throw new NullPointerException();
    }
    final ElementMetaData retval = backend.get( identifier );
    if ( retval == null ) {
      throw new MetaDataLookupException( "There is no meta-data defined for type '" + identifier + '\'' );
    }
    return retval;
  }

  public String getNamespacePrefix( final String namespaceUri ) {
    if ( namespaceUri == null ) {
      throw new NullPointerException();
    }
    return namespaceMapping.get( namespaceUri );
  }

  public void registerNamespacePrefix( final String namespaceUri, final String namespacePrefix ) {
    if ( namespacePrefix == null ) {
      throw new NullPointerException();
    }
    if ( namespaceUri == null ) {
      throw new NullPointerException();
    }
    namespaceMapping.put( namespaceUri, namespacePrefix );
  }
}
