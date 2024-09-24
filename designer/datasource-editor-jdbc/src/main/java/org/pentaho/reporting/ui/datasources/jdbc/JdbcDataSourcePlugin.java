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

package org.pentaho.reporting.ui.datasources.jdbc;

import java.awt.Window;
import javax.swing.JDialog;
import javax.swing.JFrame;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.designtime.DataFactoryChangeRecorder;
import org.pentaho.reporting.engine.classic.core.designtime.DataSourcePlugin;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryRegistry;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.SQLReportDataFactory;
import org.pentaho.reporting.ui.datasources.jdbc.ui.JdbcDataSourceDialog;

/**
 * @author David Kincade
 */
public class JdbcDataSourcePlugin implements DataSourcePlugin
{
  public JdbcDataSourcePlugin()
  {
  }

  public DataFactoryMetaData getMetaData()
  {
    return DataFactoryRegistry.getInstance().getMetaData(SQLReportDataFactory.class.getName());
  }

  public boolean canHandle(final DataFactory dataFactory)
  {
    return dataFactory instanceof SQLReportDataFactory;
  }

  public DataFactory performEdit(final DesignTimeContext context,
                                 final DataFactory input,
                                 String queryName,
                                 final DataFactoryChangeRecorder changeRecorder)
  {
    final JdbcDataSourceDialog editor;
    final Window window = context.getParentWindow();
    if (window instanceof JDialog)
    {
      editor = new JdbcDataSourceDialog(context, (JDialog) window);
    }
    else if (window instanceof JFrame)
    {
      editor = new JdbcDataSourceDialog(context, (JFrame) window);
    }
    else
    {
      editor = new JdbcDataSourceDialog(context);
    }
    return editor.performConfiguration((SQLReportDataFactory) input, queryName);
  }
}