/*
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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

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
