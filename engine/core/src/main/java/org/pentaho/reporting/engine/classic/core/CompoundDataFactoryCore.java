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

package org.pentaho.reporting.engine.classic.core;

import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.DefaultDataFactoryCore;
import org.pentaho.reporting.engine.classic.core.metadata.ResourceReference;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class CompoundDataFactoryCore extends DefaultDataFactoryCore {
  public CompoundDataFactoryCore() {
  }

  public String[] getReferencedFields( final DataFactoryMetaData metaData, final DataFactory element,
      final String query, final DataRow parameter ) {
    DataFactory m = getDataFactoryForQuery( element, query );
    if ( m == null ) {
      return null;
    }
    DataFactoryMetaData md = m.getMetaData();
    if ( md == null ) {
      return null;
    }
    return md.getReferencedFields( m, query, parameter );
  }

  protected DataFactory getDataFactoryForQuery( final DataFactory element, final String query ) {
    if ( element instanceof CompoundDataFactorySupport == false ) {
      return null;
    }
    CompoundDataFactorySupport cdf = (CompoundDataFactorySupport) element;
    DataFactory dataFactoryForQuery = cdf.getDataFactoryForQuery( query, false );
    if ( dataFactoryForQuery == null ) {
      dataFactoryForQuery = cdf.getDataFactoryForQuery( query, true );
      if ( dataFactoryForQuery == null ) {
        return null;
      }
    }

    return dataFactoryForQuery;
  }

  public ResourceReference[] getReferencedResources( final DataFactoryMetaData metaData, final DataFactory element,
      final ResourceManager resourceManager, final String query, final DataRow parameter ) {
    DataFactory m = getDataFactoryForQuery( element, query );
    if ( m == null ) {
      return null;
    }
    DataFactoryMetaData md = m.getMetaData();
    if ( md == null ) {
      return null;
    }
    return md.getReferencedResources( m, resourceManager, query, parameter );
  }

  public Object getQueryHash( final DataFactoryMetaData dataFactoryMetaData, final DataFactory dataFactory,
      final String queryName, final DataRow parameter ) {
    DataFactory m = getDataFactoryForQuery( dataFactory, queryName );
    if ( m == null ) {
      return null;
    }
    DataFactoryMetaData md = m.getMetaData();
    if ( md == null ) {
      return null;
    }
    return md.getQueryHash( m, queryName, parameter );
  }
}
