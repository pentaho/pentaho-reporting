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

package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian;

import mondrian.spi.CatalogLocator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.FileSystemException;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.libraries.base.boot.ObjectFactoryException;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.File;
import java.util.ArrayList;

public class DefaultCubeFileProvider implements CubeFileProvider {
  private static final Log logger = LogFactory.getLog( DefaultCubeFileProvider.class );
  private String mondrianCubeFile;
  private String cubeConnectionName;

  public DefaultCubeFileProvider() {
  }

  public DefaultCubeFileProvider( final String mondrianCubeFile ) {
    this( mondrianCubeFile, null );
  }

  public DefaultCubeFileProvider( final String mondrianCubeFile,
                                  final String cubeConnectionName ) {
    this.mondrianCubeFile = mondrianCubeFile;
    this.cubeConnectionName = cubeConnectionName;
  }

  public String getDesignTimeFile() {
    return getMondrianCubeFile();
  }

  public void setDesignTimeFile( final String name ) {
    setMondrianCubeFile( name );
  }

  public String getMondrianCubeFile() {
    return mondrianCubeFile;
  }

  public void setMondrianCubeFile( final String mondrianCubeFile ) {
    this.mondrianCubeFile = mondrianCubeFile;
  }

  public String getCubeFile( final ResourceManager resourceManager,
                             final ResourceKey contextKey ) throws ReportDataFactoryException {
    if ( mondrianCubeFile == null ) {
      throw new ReportDataFactoryException( "No schema file defined." );
    }

    try {
      final CatalogLocator locator = ClassicEngineBoot.getInstance().getObjectFactory().get( CatalogLocator.class );
      final String mappedCatalog = locator.locate( mondrianCubeFile );
      if ( StringUtils.isEmpty( mappedCatalog ) == false ) {
        return mappedCatalog;
      }
    } catch ( ObjectFactoryException e ) {
      if ( logger.isTraceEnabled() ) {
        logger.trace( "No catalog-locator defined", e ); // NON-NLS
      }
    }

    final File cubeAsFile = new File( mondrianCubeFile );
    if ( cubeAsFile.isFile() ) {
      return mondrianCubeFile;
    }

    try {
      return SchemaResolver.resolveSchema( resourceManager, contextKey, mondrianCubeFile );
    } catch ( FileSystemException e ) {
      return mondrianCubeFile;
    }
  }

  public String getCubeConnectionName() {
    return cubeConnectionName;
  }

  public void setCubeConnectionName( final String cubeConnectionName ) {
    this.cubeConnectionName = cubeConnectionName;
  }

  public Object getConnectionHash() {
    final ArrayList<Object> hash = new ArrayList<Object>();
    hash.add( getClass().getName() );
    hash.add( getMondrianCubeFile() );
    hash.add( getCubeConnectionName() );
    return hash;
  }
}
