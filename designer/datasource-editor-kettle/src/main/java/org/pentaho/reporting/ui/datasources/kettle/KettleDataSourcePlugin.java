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
import org.pentaho.reporting.engine.classic.core.designtime.DataSourcePlugin;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryRegistry;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleDataFactory;

import javax.swing.*;
import java.awt.*;

/**
 * @author David Kincade
 */
public class KettleDataSourcePlugin implements DataSourcePlugin {
  public KettleDataSourcePlugin() {
  }

  public boolean canHandle( final DataFactory dataFactory ) {
    return dataFactory instanceof KettleDataFactory;
  }

  public DataFactory performEdit( final DesignTimeContext context,
                                  final DataFactory input,
                                  final String queryName,
                                  final DataFactoryChangeRecorder changeRecorder ) {
    try {
      final KettleDataSourceDialog editor = createKettleDataSourceDialog( context );
      return editor.performConfiguration( context, (KettleDataFactory) input, queryName );
    } catch ( KettleException e ) {
      context.error( e );
      return input;
    }
  }

  protected KettleDataSourceDialog createKettleDataSourceDialog( final DesignTimeContext context ) {
    final KettleDataSourceDialog editor;
    final Window window = context.getParentWindow();
    if ( window instanceof JDialog ) {
      editor = new KettleDataSourceDialog( context, (JDialog) window );
    } else if ( window instanceof JFrame ) {
      editor = new KettleDataSourceDialog( context, (JFrame) window );
    } else {
      editor = new KettleDataSourceDialog( context );
    }
    return editor;
  }

  public DataFactoryMetaData getMetaData() {
    return DataFactoryRegistry.getInstance().getMetaData( KettleDataFactory.class.getName() );
  }

}
