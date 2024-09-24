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

package org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sequence;

import org.pentaho.reporting.engine.classic.core.DataFactoryContext;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;

import javax.swing.table.TableModel;

public class BooleanSequence extends AbstractSequence {
  public BooleanSequence() {
  }

  public SequenceDescription getSequenceDescription() {
    return new BooleanSequenceDescription();
  }

  public TableModel produce( final DataRow parameters, final DataFactoryContext dataFactoryContext )
    throws ReportDataFactoryException {
    final Boolean trueFirst = getTypedParameter( "true-as-first", Boolean.class, Boolean.TRUE );
    final String trueText = getTypedParameter( "true-text", String.class, "true" );
    final String falseText = getTypedParameter( "false-text", String.class, "false" );

    final TypedTableModel tableModel = new TypedTableModel();
    tableModel.addColumn( "bool", Boolean.class );
    tableModel.addColumn( "int", Integer.class );
    tableModel.addColumn( "text", String.class );

    if ( Boolean.TRUE.equals( trueFirst ) ) {
      tableModel.setValueAt( Boolean.TRUE, 0, 0 );
      tableModel.setValueAt( 1, 0, 1 );
      tableModel.setValueAt( trueText, 0, 2 );

      tableModel.setValueAt( Boolean.FALSE, 1, 0 );
      tableModel.setValueAt( 0, 1, 1 );
      tableModel.setValueAt( falseText, 1, 2 );
    } else {
      tableModel.setValueAt( Boolean.FALSE, 0, 0 );
      tableModel.setValueAt( 0, 0, 1 );
      tableModel.setValueAt( falseText, 0, 2 );

      tableModel.setValueAt( Boolean.TRUE, 1, 0 );
      tableModel.setValueAt( 1, 1, 1 );
      tableModel.setValueAt( trueText, 1, 2 );
    }

    return tableModel;
  }
}
