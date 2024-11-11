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


package org.pentaho.reporting.ui.datasources.sequence;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.designtime.DataFactoryChangeRecorder;
import org.pentaho.reporting.engine.classic.core.designtime.DataSourcePlugin;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryRegistry;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sequence.SequenceDataFactory;

import java.awt.*;

public class SequenceDataSourcePlugin implements DataSourcePlugin {
  public SequenceDataSourcePlugin() {
  }

  public DataFactory performEdit( final DesignTimeContext context,
                                  final DataFactory input,
                                  final String selectedQueryName, final DataFactoryChangeRecorder changeRecorder ) {
    final SequenceDataSourceEditor editor;
    final Window parentWindow = context.getParentWindow();

    if ( parentWindow instanceof Dialog ) {
      editor = new SequenceDataSourceEditor( (Dialog) parentWindow );
    } else if ( parentWindow instanceof Frame ) {
      editor = new SequenceDataSourceEditor( (Frame) parentWindow );
    } else {
      editor = new SequenceDataSourceEditor();
    }
    return editor.performConfiguration( context, (SequenceDataFactory) input, selectedQueryName );
  }

  public boolean canHandle( final DataFactory dataFactory ) {
    return dataFactory instanceof SequenceDataFactory;
  }

  public DataFactoryMetaData getMetaData() {
    return DataFactoryRegistry.getInstance().getMetaData( SequenceDataFactory.class.getName() );
  }
}
