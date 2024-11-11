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


package org.pentaho.reporting.ui.datasources.jdbc;

import java.awt.Window;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.designtime.DataFactoryChangeRecorder;
import org.pentaho.reporting.engine.classic.core.designtime.DataSourcePlugin;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryRegistry;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.SQLReportDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.SimpleSQLReportDataFactory;
import org.pentaho.reporting.ui.datasources.jdbc.ui.SimpleJdbcDataSourceDialog;

/**
 * @author David Kincade
 */
public class SimpleJdbcDataSourcePlugin implements DataSourcePlugin {
  public SimpleJdbcDataSourcePlugin() {
  }

  public DataFactoryMetaData getMetaData() {
    return DataFactoryRegistry.getInstance().getMetaData( SimpleSQLReportDataFactory.class.getName() );
  }

  public boolean canHandle( final DataFactory dataFactory ) {
    return dataFactory instanceof SimpleSQLReportDataFactory && dataFactory instanceof SQLReportDataFactory == false;
  }

  public DataFactory performEdit( final DesignTimeContext context, final DataFactory input, final String queryName,
      final DataFactoryChangeRecorder changeRecorder ) {
    final SimpleJdbcDataSourceDialog editor = createEditor( context );
    return editor.performConfiguration( (SimpleSQLReportDataFactory) input );
  }

  /**
   * package-local visibility for testing purposes
   */
  SimpleJdbcDataSourceDialog createEditor( DesignTimeContext context ) {
    final Window window = context.getParentWindow();
    if ( window instanceof JDialog ) {
      return new SimpleJdbcDataSourceDialog( context, (JDialog) window );
    } else if ( window instanceof JFrame ) {
      return new SimpleJdbcDataSourceDialog( context, (JFrame) window );
    } else {
      return new SimpleJdbcDataSourceDialog( context );
    }
  }

  public JComponent getPropertyPanel( final DataFactory dataFactory, final DesignTimeContext context ) {
    return null;
  }
}
