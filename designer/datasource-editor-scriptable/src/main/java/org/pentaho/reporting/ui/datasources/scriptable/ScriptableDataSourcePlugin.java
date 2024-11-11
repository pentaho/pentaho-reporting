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


package org.pentaho.reporting.ui.datasources.scriptable;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.designtime.DataFactoryChangeRecorder;
import org.pentaho.reporting.engine.classic.core.designtime.DataSourcePlugin;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryRegistry;
import org.pentaho.reporting.engine.classic.extensions.datasources.scriptable.ScriptableDataFactory;

import javax.swing.*;
import java.awt.*;

/**
 * @author David Kincade
 */
public class ScriptableDataSourcePlugin implements DataSourcePlugin {
  public ScriptableDataSourcePlugin() {
  }

  public boolean canHandle( final DataFactory dataFactory ) {
    return dataFactory instanceof ScriptableDataFactory;
  }

  public DataFactory performEdit( final DesignTimeContext context,
                                  final DataFactory input,
                                  final String queryName,
                                  final DataFactoryChangeRecorder changeRecorder ) {
    final ScriptableDataSourceEditor editor;
    final Window window = context.getParentWindow();
    if ( window instanceof JDialog ) {
      editor = new ScriptableDataSourceEditor( context, (JDialog) window );
    } else if ( window instanceof JFrame ) {
      editor = new ScriptableDataSourceEditor( context, (JFrame) window );
    } else {
      editor = new ScriptableDataSourceEditor( context );
    }
    return editor.performConfiguration( (ScriptableDataFactory) input, queryName );
  }

  public DataFactoryMetaData getMetaData() {
    return DataFactoryRegistry.getInstance().getMetaData( ScriptableDataFactory.class.getName() );
  }

}
