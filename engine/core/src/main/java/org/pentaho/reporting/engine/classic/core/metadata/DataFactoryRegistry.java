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
