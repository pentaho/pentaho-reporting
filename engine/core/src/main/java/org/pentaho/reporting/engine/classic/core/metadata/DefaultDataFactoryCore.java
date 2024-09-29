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
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactoryMetaProvider;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class DefaultDataFactoryCore implements DataFactoryCore {
  private static final Log logger = LogFactory.getLog( DefaultDataFactoryCore.class );

  public DefaultDataFactoryCore() {
  }

  public String[] getReferencedFields( final DataFactoryMetaData metaData, final DataFactory element,
      final String query, final DataRow parameter ) {
    if ( element instanceof DataFactoryMetaProvider ) {
      try {
        DataFactoryMetaProvider p = (DataFactoryMetaProvider) element;
        return p.getReferencedFields( query, parameter );
      } catch ( final ReportDataFactoryException e ) {
        logger.info( "Unable to compute design-time data: Referenced fields", e );
      }
    }
    return null;
  }

  public ResourceReference[] getReferencedResources( final DataFactoryMetaData metaData, final DataFactory element,
      final ResourceManager resourceManager, final String query, final DataRow parameter ) {
    return new ResourceReference[0];
  }

  public String getDisplayConnectionName( final DataFactoryMetaData metaData, final DataFactory dataFactory ) {
    if ( dataFactory instanceof DataFactoryMetaProvider ) {
      DataFactoryMetaProvider p = (DataFactoryMetaProvider) dataFactory;
      return p.getDisplayConnectionName();
    }
    return null;
  }

  public Object getQueryHash( final DataFactoryMetaData dataFactoryMetaData, final DataFactory dataFactory,
      final String queryName, final DataRow parameter ) {
    if ( dataFactory instanceof DataFactoryMetaProvider ) {
      try {
        DataFactoryMetaProvider p = (DataFactoryMetaProvider) dataFactory;
        return p.getReferencedFields( queryName, parameter );
      } catch ( final ReportDataFactoryException e ) {
        logger.info( "Unable to compute design-time data: Query Hash", e );
      }
    }
    return null;
  }
}
