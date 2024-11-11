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


package org.pentaho.reporting.ui.datasources.reflection;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.designtime.DataFactoryChangeRecorder;
import org.pentaho.reporting.engine.classic.core.designtime.DataSourcePlugin;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryRegistry;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.NamedStaticDataFactory;

import javax.swing.*;
import java.awt.*;


public class ReflectionDataSourcePlugin implements DataSourcePlugin {
  public ReflectionDataSourcePlugin() {
  }

  public DataFactory performEdit( final DesignTimeContext aContext,
                                  final DataFactory anInput,
                                  final String queryName,
                                  final DataFactoryChangeRecorder changeRecorder ) {
    final ReflectionDataSourceEditor theEditor;
    final Window parentWindow = aContext.getParentWindow();

    if ( parentWindow instanceof JDialog ) {
      theEditor = new ReflectionDataSourceEditor( (JDialog) parentWindow );
    } else if ( parentWindow instanceof JFrame ) {
      theEditor = new ReflectionDataSourceEditor( (JFrame) parentWindow );
    } else {
      theEditor = new ReflectionDataSourceEditor();
    }
    return theEditor.performConfiguration( (NamedStaticDataFactory) anInput, queryName );
  }

  public boolean canHandle( final DataFactory dataFactory ) {
    return dataFactory instanceof NamedStaticDataFactory;
  }

  public DataFactoryMetaData getMetaData() {
    return DataFactoryRegistry.getInstance().getMetaData( NamedStaticDataFactory.class.getName() );
  }
}
