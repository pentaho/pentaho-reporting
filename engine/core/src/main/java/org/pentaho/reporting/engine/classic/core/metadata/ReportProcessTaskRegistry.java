/*!
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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

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
