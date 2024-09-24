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
import org.pentaho.reporting.engine.classic.core.metadata.parser.ReportPreProcessorMetaDataCollection;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.HashMap;

public class ReportPreProcessorRegistry {
  private static final Log logger = LogFactory.getLog( ReportPreProcessorRegistry.class );
  private static ReportPreProcessorRegistry instance;

  private HashMap<String, ReportPreProcessorMetaData> backend;
  private ResourceManager resourceManager;

  public static synchronized ReportPreProcessorRegistry getInstance() {
    if ( instance == null ) {
      instance = new ReportPreProcessorRegistry();
    }
    return instance;
  }

  private ReportPreProcessorRegistry() {
    this.resourceManager = new ResourceManager();
    this.backend = new HashMap<String, ReportPreProcessorMetaData>();
  }

  public void registerFromXml( final URL expressionMetaSource ) throws IOException {
    if ( expressionMetaSource == null ) {
      throw new NullPointerException( "Error: Could not find the report-preprocessor meta-data description file" );
    }

    try {
      final Resource resource =
          resourceManager.createDirectly( expressionMetaSource, ReportPreProcessorMetaDataCollection.class );
      final ReportPreProcessorMetaDataCollection typeCollection =
          (ReportPreProcessorMetaDataCollection) resource.getResource();
      final ReportPreProcessorMetaData[] types = typeCollection.getReportPreProcessorMetaData();
      for ( int i = 0; i < types.length; i++ ) {
        final ReportPreProcessorMetaData metaData = types[i];
        if ( metaData != null ) {
          registerReportPreProcessor( metaData );
        }
      }
    } catch ( Exception e ) {
      ReportPreProcessorRegistry.logger.error( "Failed:", e );
      throw new IOException( "Error: Could not parse the element meta-data description file" );
    }
  }

  public void registerReportPreProcessor( final ReportPreProcessorMetaData metaData ) {
    if ( metaData == null ) {
      throw new NullPointerException();
    }
    final Class type = metaData.getPreProcessorType();
    final int modifiers = type.getModifiers();
    if ( Modifier.isAbstract( modifiers ) || Modifier.isInterface( modifiers ) ) {
      throw new IllegalArgumentException( "report-preprocessor-Implementation cannot be abstract or an interface." );
    }
    this.backend.put( type.getName(), metaData );
  }

  public ReportPreProcessorMetaData[] getAllReportPreProcessorMetaDatas() {
    return backend.values().toArray( new ReportPreProcessorMetaData[backend.size()] );
  }

  public boolean isReportPreProcessorRegistered( final String identifier ) {
    if ( identifier == null ) {
      throw new NullPointerException();
    }
    return backend.containsKey( identifier );
  }

  public ReportPreProcessorMetaData getReportPreProcessorMetaData( final String identifier )
    throws MetaDataLookupException {
    if ( identifier == null ) {
      throw new NullPointerException();
    }
    final ReportPreProcessorMetaData retval = backend.get( identifier );
    if ( retval == null ) {
      throw new MetaDataLookupException( "Unable to locate metadata for report-preprocessor type " + identifier );
    }
    return retval;
  }
}
