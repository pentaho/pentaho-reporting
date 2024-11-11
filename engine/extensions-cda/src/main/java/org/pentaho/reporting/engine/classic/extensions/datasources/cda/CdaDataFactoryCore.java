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


package org.pentaho.reporting.engine.classic.extensions.datasources.cda;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ParameterMapping;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.DefaultDataFactoryCore;

import java.util.ArrayList;
import java.util.LinkedHashSet;

/**
 * Core methods to allow caching of the CDA queries.
 * <p/>
 * Created by Andre Simoes (andre.simoes (at) xpand-it (dot) com).
 */
public class CdaDataFactoryCore extends DefaultDataFactoryCore {
  public String[] getReferencedFields( final DataFactoryMetaData metaData,
                                       final DataFactory element,
                                       final String query,
                                       final DataRow parameter ) {
    final CdaDataFactory df = (CdaDataFactory) element;
    final LinkedHashSet<String> list = new LinkedHashSet<String>();

    final ParameterMapping[] params = df.getQueryEntry( query ).getParameters();
    for ( int i = 0; i < params.length; i++ ) {
      String paramName = params[ i ].getName();
      list.add( paramName );
    }

    return list.toArray( new String[ list.size() ] );
  }

  public Object getQueryHash( final DataFactoryMetaData dataFactoryMetaData,
                              final DataFactory dataFactory,
                              final String queryName,
                              final DataRow parameter ) {
    final CdaDataFactory df = (CdaDataFactory) dataFactory;
    final ArrayList<Object> list = new ArrayList<Object>();

    list.add( df.getPath() );
    list.add( df.getQueryEntry( queryName ) );
    list.add( df.getBaseUrl() );
    list.add( df.getUsername() );

    return list;
  }
}
