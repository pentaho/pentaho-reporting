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


package org.pentaho.reporting.ui.datasources.kettle;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.designtime.DataFactoryChangeRecorder;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryRegistry;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.EmbeddedKettleDataFactoryEditor;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleDataFactory;

import javax.swing.*;
import java.awt.*;

public class EmbeddedTransformationDataSourcePlugin extends KettleDataSourcePlugin
  implements EmbeddedKettleDataFactoryEditor {
  private String metaDataId;

  public EmbeddedTransformationDataSourcePlugin() {
  }

  public void configure( final String metaDataId ) {
    this.metaDataId = metaDataId;
  }


  public DataFactory performEdit( final DesignTimeContext context,
                                  final DataFactory dataFactory,
                                  final String queryName,
                                  final DataFactoryChangeRecorder changeRecorder ) {

    try {
      KettleDataFactory factory;
      if ( dataFactory == null ) {
        factory = new KettleDataFactory();
      } else {
        factory = (KettleDataFactory) dataFactory;
      }
      factory.setMetadata( getMetaData() );

      final KettleDataSourceDialog editor = createEmbeddedKettleDataSourceDialog( context );
      return editor.performConfiguration( context, factory, queryName );
    } catch ( KettleException e ) {
      context.error( e );
      return dataFactory;
    }

  }

  protected KettleDataSourceDialog createEmbeddedKettleDataSourceDialog( final DesignTimeContext context ) {
    final KettleDataSourceDialog editor;
    final Window window = context.getParentWindow();
    if ( window instanceof JDialog ) {
      editor = new EmbeddedKettleDataSourceDialog( context, (JDialog) window, metaDataId );
    } else if ( window instanceof JFrame ) {
      editor = new EmbeddedKettleDataSourceDialog( context, (JFrame) window, metaDataId );
    } else {
      editor = new EmbeddedKettleDataSourceDialog( context, metaDataId );
    }
    return editor;
  }

  public DataFactoryMetaData getMetaData() {
    return DataFactoryRegistry.getInstance().getMetaData( metaDataId );
  }
}
