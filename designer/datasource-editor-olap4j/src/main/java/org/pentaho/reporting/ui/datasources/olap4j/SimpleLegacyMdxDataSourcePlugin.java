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


package org.pentaho.reporting.ui.datasources.olap4j;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.designtime.DataFactoryChangeRecorder;
import org.pentaho.reporting.engine.classic.core.designtime.DataSourcePlugin;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryRegistry;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.AbstractMDXDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.SimpleLegacyBandedMDXDataFactory;

import javax.swing.*;
import java.awt.*;

public class SimpleLegacyMdxDataSourcePlugin implements DataSourcePlugin {
  public SimpleLegacyMdxDataSourcePlugin() {
  }

  public DataFactory performEdit( final DesignTimeContext context,
                                  final DataFactory input,
                                  final String queryName,
                                  final DataFactoryChangeRecorder changeRecorder ) {
    final SimpleLegacyMdxDataSourceEditor editor;
    final Window window = context.getParentWindow();
    if ( window instanceof JDialog ) {
      editor = new SimpleLegacyMdxDataSourceEditor( context, (JDialog) window );
    } else if ( window instanceof JFrame ) {
      editor = new SimpleLegacyMdxDataSourceEditor( context, (JFrame) window );
    } else {
      editor = new SimpleLegacyMdxDataSourceEditor( context );
    }
    return editor.performConfiguration( (AbstractMDXDataFactory) input );
  }

  public boolean canHandle( final DataFactory dataFactory ) {
    return dataFactory instanceof SimpleLegacyBandedMDXDataFactory;
  }

  public DataFactoryMetaData getMetaData() {
    return DataFactoryRegistry.getInstance().getMetaData( SimpleLegacyBandedMDXDataFactory.class.getName() );
  }
}
