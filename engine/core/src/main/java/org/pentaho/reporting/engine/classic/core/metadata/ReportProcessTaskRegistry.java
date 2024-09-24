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

import org.pentaho.reporting.engine.classic.core.ReportProcessTask;
import org.pentaho.reporting.engine.classic.core.metadata.parser.ReportProcessTaskMetaDataCollection;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

public class ReportProcessTaskRegistry {
  private ConcurrentHashMap<String, ReportProcessTaskMetaData> exportTypes;
  private static ReportProcessTaskRegistry processTaskRegistry;
  private ResourceManager resourceManager;

  public static synchronized ReportProcessTaskRegistry getInstance() {
    if ( processTaskRegistry == null ) {
      processTaskRegistry = new ReportProcessTaskRegistry();
    }
    return processTaskRegistry;
  }

  private ReportProcessTaskRegistry() {
    this.resourceManager = new ResourceManager();
    this.exportTypes = new ConcurrentHashMap<String, ReportProcessTaskMetaData>();
  }

  public void registerFromXml( final URL expressionMetaSource ) throws IOException {
    if ( expressionMetaSource == null ) {
      throw new NullPointerException( "Error: Could not find the report-preprocessor meta-data description file" );
    }

    try {
      final Resource resource =
          resourceManager.createDirectly( expressionMetaSource, ReportProcessTaskMetaDataCollection.class );
      final ReportProcessTaskMetaDataCollection typeCollection =
          (ReportProcessTaskMetaDataCollection) resource.getResource();
      final ReportProcessTaskMetaData[] types = typeCollection.getMetaData();
      for ( int i = 0; i < types.length; i++ ) {
        final ReportProcessTaskMetaData metaData = types[i];
        if ( metaData != null ) {
          registerExportType( metaData );
        }
      }
    } catch ( Exception e ) {
      throw new IOException( "Error: Could not parse the element meta-data description file", e );
    }
  }

  public void registerExportType( final ReportProcessTaskMetaData exportTask ) {
    if ( exportTask == null ) {
      throw new NullPointerException();
    }

    this.exportTypes.put( exportTask.getName(), exportTask );
  }

  public ReportProcessTaskMetaData[] getAll() {
    return this.exportTypes.values().toArray( new ReportProcessTaskMetaData[this.exportTypes.size()] );
  }

  public String[] getExportTypes() {
    return exportTypes.keySet().toArray( new String[exportTypes.size()] );
  }

  public boolean isExportTypeRegistered( final String exportType ) {
    return exportTypes.containsKey( exportType );
  }

  public ReportProcessTask createProcessTask( final String exportType ) {
    final ReportProcessTaskMetaData c = exportTypes.get( exportType );
    if ( c == null ) {
      throw new IllegalArgumentException();
    }
    return c.create();
  }

  public ReportProcessTask createProcessTaskByAlias( final String exportType ) {
    for ( final ReportProcessTaskMetaData c : exportTypes.values() ) {
      final String[] alias = c.getAlias();
      if ( Arrays.asList( alias ).contains( exportType ) ) {
        return c.create();
      }
    }
    throw new IllegalArgumentException();
  }

}
