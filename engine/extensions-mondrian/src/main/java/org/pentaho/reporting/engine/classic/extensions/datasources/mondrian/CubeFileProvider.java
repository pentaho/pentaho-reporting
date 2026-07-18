/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian;

import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.Serializable;

public interface CubeFileProvider extends Serializable {
  public String getDesignTimeFile();

  public void setDesignTimeFile( String file );

  public String getCubeConnectionName();

  public void setCubeConnectionName( String cubeConnectionName );

  public String getCubeFile( final ResourceManager resourceManager, final ResourceKey contextKey )
    throws ReportDataFactoryException;

  public Object getConnectionHash();
}
