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


package org.pentaho.reporting.engine.classic.core.testsupport;

import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.function.GenericExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;

import javax.swing.table.TableModel;

public class DebugExpressionRuntime extends GenericExpressionRuntime {
  public DebugExpressionRuntime() {
  }

  public DebugExpressionRuntime( final TableModel data, final int currentRow, final ProcessingContext processingContext ) {
    super( data, currentRow, processingContext );
  }

  public DebugExpressionRuntime( final DataRow dataRow, final TableModel data, final int currentRow,
      final ProcessingContext processingContext ) {
    super( dataRow, data, currentRow, processingContext );
  }
}
