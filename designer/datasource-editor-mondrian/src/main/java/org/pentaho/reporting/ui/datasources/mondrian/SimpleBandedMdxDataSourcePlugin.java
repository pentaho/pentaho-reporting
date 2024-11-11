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


package org.pentaho.reporting.ui.datasources.mondrian;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.designtime.DataFactoryChangeRecorder;
import org.pentaho.reporting.engine.classic.core.designtime.DataSourcePlugin;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryRegistry;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.SimpleBandedMDXDataFactory;

import javax.swing.*;
import java.awt.*;

public class SimpleBandedMdxDataSourcePlugin implements DataSourcePlugin {
  public SimpleBandedMdxDataSourcePlugin() {
  }

  public DataFactory performEdit( final DesignTimeContext context,
                                  final DataFactory input,
                                  final String queryName,
                                  final DataFactoryChangeRecorder changeRecorder ) {
    final SimpleBandedMdxDataSourceEditor editor;
    final Window window = context.getParentWindow();
    if ( window instanceof JDialog ) {
      editor = new SimpleBandedMdxDataSourceEditor( context, (JDialog) window );
    } else if ( window instanceof JFrame ) {
      editor = new SimpleBandedMdxDataSourceEditor( context, (JFrame) window );
    } else {
      editor = new SimpleBandedMdxDataSourceEditor( context );
    }
    return editor.performConfiguration( (SimpleBandedMDXDataFactory) input );
  }

  public boolean canHandle( final DataFactory dataFactory ) {
    return dataFactory instanceof SimpleBandedMDXDataFactory;
  }

  public DataFactoryMetaData getMetaData() {
    return DataFactoryRegistry.getInstance().getMetaData( SimpleBandedMDXDataFactory.class.getName() );
  }
}
