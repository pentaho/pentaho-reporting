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
import java.math.BigDecimal;

public class NumberSequence extends AbstractSequence {
  public NumberSequence() {
  }

  public SequenceDescription getSequenceDescription() {
    return new NumberSequenceDescription();
  }

  public TableModel produce( final DataRow parameters, final DataFactoryContext dataFactoryContext )
    throws ReportDataFactoryException {
    final Integer limit = getTypedParameter( "limit", Integer.class, 100 );
    final BigDecimal stepSize = getTypedParameter( "step", BigDecimal.class, new BigDecimal( 1 ) );
    final BigDecimal start = getTypedParameter( "start", BigDecimal.class, new BigDecimal( 1 ) );
    final Boolean ascending = getTypedParameter( "ascending", Boolean.class, true );

    final TypedTableModel tableModel = new TypedTableModel();
    tableModel.addColumn( "number", BigDecimal.class );
    BigDecimal value;
    if ( Boolean.TRUE.equals( ascending ) ) {
      value = start;
    } else {
      value = start.add( stepSize.multiply( new BigDecimal( limit ) ) );
    }

    for ( int i = 0; i < limit; i++ ) {
      tableModel.setValueAt( value, i, 0 );
      value = value.add( stepSize );
    }
    return tableModel;
  }
}
