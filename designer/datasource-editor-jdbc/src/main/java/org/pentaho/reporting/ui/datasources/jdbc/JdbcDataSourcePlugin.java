/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2008 - 2017 Hitachi Vantara, .  All rights reserved.
 */

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