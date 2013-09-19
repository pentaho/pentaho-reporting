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
 * Copyright (c) 2009 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.reporting.ui.datasources.mondrian;

import java.awt.Window;
import javax.swing.JDialog;
import javax.swing.JFrame;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.designtime.DataFactoryChangeRecorder;
import org.pentaho.reporting.engine.classic.core.designtime.DataSourcePlugin;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryRegistry;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.LegacyBandedMDXDataFactory;

public class LegacyMdxDataSourcePlugin implements DataSourcePlugin
{
  public LegacyMdxDataSourcePlugin()
  {
  }

  public DataFactory performEdit(final DesignTimeContext context,
                                 final DataFactory input,
                                 final String queryName,
                                 final DataFactoryChangeRecorder changeRecorder)
  {
    final LegacyMdxDataSourceEditor editor;
    final Window window = context.getParentWindow();
    if (window instanceof JDialog)
    {
      editor = new LegacyMdxDataSourceEditor(context, (JDialog) window);
    }
    else if (window instanceof JFrame)
    {
      editor = new LegacyMdxDataSourceEditor(context, (JFrame) window);
    }
    else
    {
      editor = new LegacyMdxDataSourceEditor(context);
    }
    return editor.performConfiguration((LegacyBandedMDXDataFactory)input, queryName);
  }

  public boolean canHandle(final DataFactory dataFactory)
  {
    return dataFactory instanceof LegacyBandedMDXDataFactory;
  }

  public DataFactoryMetaData getMetaData()
  {
    return DataFactoryRegistry.getInstance().getMetaData(LegacyBandedMDXDataFactory.class.getName());
  }
}