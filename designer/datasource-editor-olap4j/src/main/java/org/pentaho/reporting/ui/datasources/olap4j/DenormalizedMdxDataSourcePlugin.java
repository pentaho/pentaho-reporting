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
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.DenormalizedMDXDataFactory;

import javax.swing.*;
import java.awt.*;

public class DenormalizedMdxDataSourcePlugin implements DataSourcePlugin {
  public DenormalizedMdxDataSourcePlugin() {
  }

  public DataFactory performEdit( final DesignTimeContext context,
                                  final DataFactory input,
                                  final String queryName,
                                  final DataFactoryChangeRecorder changeRecorder ) {
    final DenormalizedMdxDataSourceEditor editor;
    final Window window = context.getParentWindow();
    if ( window instanceof JDialog ) {
      editor = new DenormalizedMdxDataSourceEditor( context, (JDialog) window );
    } else if ( window instanceof JFrame ) {
      editor = new DenormalizedMdxDataSourceEditor( context, (JFrame) window );
    } else {
      editor = new DenormalizedMdxDataSourceEditor( context );
    }
    return editor.performConfiguration( (AbstractNamedMDXDataFactory) input, queryName );
  }

  public boolean canHandle( final DataFactory dataFactory ) {
    return dataFactory instanceof DenormalizedMDXDataFactory;
  }

  public DataFactoryMetaData getMetaData() {
    return DataFactoryRegistry.getInstance().getMetaData( DenormalizedMDXDataFactory.class.getName() );
  }
}
