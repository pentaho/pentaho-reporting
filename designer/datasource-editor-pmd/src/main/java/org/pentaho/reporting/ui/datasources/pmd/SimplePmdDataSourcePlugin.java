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


package org.pentaho.reporting.ui.datasources.pmd;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.designtime.DataFactoryChangeRecorder;
import org.pentaho.reporting.engine.classic.core.designtime.DataSourcePlugin;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryRegistry;
import org.pentaho.reporting.engine.classic.extensions.datasources.pmd.SimplePmdDataFactory;

import javax.swing.*;
import java.awt.*;

public class SimplePmdDataSourcePlugin implements DataSourcePlugin {
  public SimplePmdDataSourcePlugin() {
  }

  public DataFactory performEdit( final DesignTimeContext context,
                                  final DataFactory input,
                                  final String queryName,
                                  final DataFactoryChangeRecorder changeRecorder ) {
    final SimplePmdDataSourceEditor editor;
    final Window window = context.getParentWindow();
    if ( window instanceof JDialog ) {
      editor = new SimplePmdDataSourceEditor( context, (JDialog) window );
    } else if ( window instanceof JFrame ) {
      editor = new SimplePmdDataSourceEditor( context, (JFrame) window );
    } else {
      editor = new SimplePmdDataSourceEditor( context );
    }
    return editor.performConfiguration( (SimplePmdDataFactory) input );
  }

  public boolean canHandle( final DataFactory dataFactory ) {
    return dataFactory != null && SimplePmdDataFactory.class.equals( dataFactory.getClass() );
  }

  public DataFactoryMetaData getMetaData() {
    return DataFactoryRegistry.getInstance().getMetaData( SimplePmdDataFactory.class.getName() );
  }
}
