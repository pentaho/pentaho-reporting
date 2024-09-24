/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.ui.datasources.cda;

import java.awt.Window;
import javax.swing.JDialog;
import javax.swing.JFrame;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.designtime.DataFactoryChangeRecorder;
import org.pentaho.reporting.engine.classic.core.designtime.DataSourcePlugin;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryRegistry;
import org.pentaho.reporting.engine.classic.extensions.datasources.cda.CdaDataFactory;

public class CdaDataSourcePlugin implements DataSourcePlugin
{
  public CdaDataSourcePlugin()
  {
  }

  public DataFactory performEdit(final DesignTimeContext context,
                                 final DataFactory input,
                                 final String queryName,
                                 final DataFactoryChangeRecorder changeRecorder)
  {
    final CdaDataSourceEditor editor;
    final Window window = context.getParentWindow();
    if (window instanceof JDialog)
    {
      editor = new CdaDataSourceEditor(context, (JDialog) window);
    }
    else if (window instanceof JFrame)
    {
      editor = new CdaDataSourceEditor(context, (JFrame) window);
    }
    else
    {
      editor = new CdaDataSourceEditor(context);
    }
    return editor.performConfiguration((CdaDataFactory) input, queryName);
  }

  public boolean canHandle(final DataFactory dataFactory)
  {
    return dataFactory instanceof CdaDataFactory;
  }

  public DataFactoryMetaData getMetaData()
  {
    return DataFactoryRegistry.getInstance().getMetaData(CdaDataFactory.class.getName());
  }
}
