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


package org.pentaho.reporting.ui.datasources.olap4j;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.designtime.DataFactoryChangeRecorder;
import org.pentaho.reporting.engine.classic.core.designtime.DataSourcePlugin;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryRegistry;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.AbstractNamedMDXDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.LegacyBandedMDXDataFactory;

import javax.swing.*;
import java.awt.*;

public class LegacyMdxDataSourcePlugin implements DataSourcePlugin {
  public LegacyMdxDataSourcePlugin() {
  }

  public DataFactory performEdit( final DesignTimeContext context,
                                  final DataFactory input,
                                  final String queryName,
                                  final DataFactoryChangeRecorder changeRecorder ) {
    final LegacyMdxDataSourceEditor editor;
    final Window window = context.getParentWindow();
    if ( window instanceof JDialog ) {
      editor = new LegacyMdxDataSourceEditor( context, (JDialog) window );
    } else if ( window instanceof JFrame ) {
      editor = new LegacyMdxDataSourceEditor( context, (JFrame) window );
    } else {
      editor = new LegacyMdxDataSourceEditor( context );
    }
    return editor.performConfiguration( (AbstractNamedMDXDataFactory) input, queryName );
  }

  public boolean canHandle( final DataFactory dataFactory ) {
    return dataFactory instanceof LegacyBandedMDXDataFactory;
  }

  public DataFactoryMetaData getMetaData() {
    return DataFactoryRegistry.getInstance().getMetaData( LegacyBandedMDXDataFactory.class.getName() );
  }
}
