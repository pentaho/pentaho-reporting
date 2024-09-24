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

package org.pentaho.reporting.libraries.formula.typing;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.lvalues.LValue;
import org.pentaho.reporting.libraries.formula.lvalues.StaticValue;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;

/**
 * An array call-back that copies all contents of the source into a private buffer.
 *
 * @author Thomas Morgner
 */
public class StaticArrayCallback implements ArrayCallback {
  private TypeValuePair[][] backend;
  private int rowCount;
  private int colCount;

  public StaticArrayCallback( final ArrayCallback source ) throws EvaluationException {
    rowCount = source.getRowCount();
    colCount = source.getColumnCount();
    backend = new TypeValuePair[ rowCount ][ colCount ];
    for ( int row = 0; row < rowCount; row += 1 ) {
      for ( int col = 0; col < colCount; col += 1 ) {
        final Type type = source.getType( row, col );
        final Object value = source.getValue( row, col );
        backend[ row ][ col ] = new TypeValuePair( type, value );
      }
    }
  }

  public LValue getRaw( final int row, final int column ) throws EvaluationException {
    return new StaticValue( getValue( row, column ), getType( row, column ) );
  }

  public Object getValue( final int row, final int column ) throws EvaluationException {
    return backend[ row ][ column ].getValue();
  }

  public Type getType( final int row, final int column ) throws EvaluationException {
    return backend[ row ][ column ].getType();
  }

  public int getColumnCount() {
    return colCount;
  }

  public int getRowCount() {
    return rowCount;
  }
}
