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
