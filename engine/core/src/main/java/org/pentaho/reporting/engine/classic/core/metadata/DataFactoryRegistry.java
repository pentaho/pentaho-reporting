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


package org.pentaho.reporting.engine.classic.core.metadata;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.metadata.parser.DataFactoryMetaDataCollection;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashMap;

public class DataFactoryRegistry {
  private static final Log logger = LogFactory.getLog( DataFactoryRegistry.class );
  private static DataFactoryRegistry instance;

  private LinkedHashMap<String, DataFactoryMetaData> backend;
  private ResourceManager resourceManager;

  public static synchronized DataFactoryRegistry getInstance() {
    if ( instance == null ) {
      instance = new DataFactoryRegistry();
    }
    return instance;
  }

  private DataFactoryRegistry() {
    this.resourceManager = new ResourceManager();
    this.backend = new LinkedHashMap<String, DataFactoryMetaData>();
  }

  public void registerFromXml( final URL dataFactoryMetaSource ) throws IOException {
    if ( dataFactoryMetaSource == null ) {
      throw new NullPointerException( "Error: Could not find the data-factory meta-data description file" );
    }

    try {
      final Resource resource =
          resourceManager.createDirectly( dataFactoryMetaSource, DataFactoryMetaDataCollection.class );
      final DataFactoryMetaDataCollection typeCollection = (DataFactoryMetaDataCollection) resource.getResource();
      final DataFactoryMetaData[] types = typeCollection.getFactoryMetaData();
      for ( int i = 0; i < types.length; i++ ) {
        final DataFactoryMetaData metaData = types[i];
        if ( metaData != null ) {
          register( metaData );
        }
      }
    } catch ( Exception e ) {
      DataFactoryRegistry.logger.error( "Failed:", e );
      throw new IOException( "Error: Could not parse the element meta-data description file" );
    }
  }

  public void unregister( final DataFactoryMetaData metaData ) {
    if ( metaData == null ) {
      throw new NullPointerException();
    }
    this.backend.remove( metaData.getName() );
  }

  public void register( final DataFactoryMetaData metaData ) {
    if ( metaData == null ) {
      throw new NullPointerException();
    }
    this.backend.put( metaData.getName(), metaData );
  }

  public DataFactoryMetaData[] getAll() {
    return backend.values().toArray( new DataFactoryMetaData[backend.size()] );
  }

  public boolean isRegistered( final String identifier ) {
    if ( identifier == null ) {
      throw new NullPointerException();
    }
    return backend.containsKey( identifier );
  }

  public DataFactoryMetaData getMetaData( final String identifier ) throws MetaDataLookupException {
    if ( identifier == null ) {
      throw new NullPointerException();
    }
    final DataFactoryMetaData retval = backend.get( identifier );
    if ( retval == null ) {
      throw new MetaDataLookupException( "Unable to locate metadata for data-factory type " + identifier );
    }
    return retval;
  }
}
