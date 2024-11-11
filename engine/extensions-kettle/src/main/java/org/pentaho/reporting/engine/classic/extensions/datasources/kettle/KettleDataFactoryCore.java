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


package org.pentaho.reporting.engine.classic.extensions.datasources.kettle;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.DefaultDataFactoryCore;

import java.util.Locale;

public class KettleDataFactoryCore extends DefaultDataFactoryCore {
  private static final long serialVersionUID = -8347624479657990545L;

  public KettleDataFactoryCore() {
  }

  public String[] getReferencedFields( final DataFactoryMetaData metaData,
                                       final DataFactory element,
                                       final String query,
                                       final DataRow parameter ) {
    final KettleDataFactory kettleDataFactory = (KettleDataFactory) element;
    final KettleTransformationProducer transformationProducer = kettleDataFactory.getQuery( query );
    if ( transformationProducer == null ) {
      return null;
    }

    try {
      return transformationProducer.getReferencedFields();
    } catch ( Exception e ) {
      return null;
    }
  }

  public Object getQueryHash( final DataFactoryMetaData dataFactoryMetaData,
                              final DataFactory dataFactory,
                              final String queryName,
                              final DataRow parameter ) {
    final KettleDataFactory kettleDataFactory = (KettleDataFactory) dataFactory;
    return kettleDataFactory.getQueryHash( queryName );
  }

  public String getDisplayConnectionName( final DataFactoryMetaData metaData, final DataFactory dataFactory ) {
    KettleDataFactory df = (KettleDataFactory) dataFactory;
    if ( df.getMetaData() != null ) {
      return df.getMetaData().getDisplayName( Locale.getDefault() );
    }
    return metaData.getDisplayName( Locale.getDefault() );

  }
}
