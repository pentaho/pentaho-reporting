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

package org.pentaho.reporting.ui.datasources.table;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.designtime.DataFactoryChangeRecorder;
import org.pentaho.reporting.engine.classic.core.designtime.DataSourcePlugin;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryRegistry;

import javax.swing.*;
import java.awt.*;

public class TableDataSourcePlugin implements DataSourcePlugin {

  public TableDataSourcePlugin() {
  }

  public DataFactory performEdit( final DesignTimeContext designTimeContext,
                                  final DataFactory anInput, final String queryName,
                                  final DataFactoryChangeRecorder changeRecorder ) {
    final TableDataSourceEditor editor;
    final Window parentWindow = designTimeContext.getParentWindow();

    if ( parentWindow instanceof JDialog ) {
      editor = new TableDataSourceEditor( (JDialog) parentWindow );
    } else if ( parentWindow instanceof JFrame ) {
      editor = new TableDataSourceEditor( (JFrame) parentWindow );
    } else {
      editor = new TableDataSourceEditor();
    }
    return editor.performConfiguration( designTimeContext, (TableDataFactory) anInput, queryName );
  }

  public boolean canHandle( final DataFactory aDataFactory ) {
    return aDataFactory instanceof TableDataFactory;
  }

  public DataFactoryMetaData getMetaData() {
    return DataFactoryRegistry.getInstance().getMetaData( TableDataFactory.class.getName() );
  }
}
