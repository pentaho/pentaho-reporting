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

package org.pentaho.reporting.libraries.formula.lvalues;

import org.pentaho.reporting.libraries.base.util.ObjectTable;
import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.typing.ArrayCallback;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.AnyType;

/**
 * Creation-Date: 05.11.2006, 13:34:01
 *
 * @author Thomas Morgner
 * @author Cedric Pronzato
 */
public class DefaultDataTable extends ObjectTable implements DataTable {

  private static class DefaultArrayCallback implements ArrayCallback {
    private DefaultDataTable table;
    private TypeValuePair[][] backend;
    private int rowCount;
    private int columnCount;

    private DefaultArrayCallback( final DefaultDataTable table ) {
      this.table = table;
      this.rowCount = table.getRowCount();
      this.columnCount = table.getColumnCount();
      this.backend = new TypeValuePair[ rowCount ][ columnCount ];
    }

    public LValue getRaw( final int row, final int column ) {
      return table.getValueAt( row, column );
    }

    public Object getValue( final int row, final int column ) throws EvaluationException {
      final TypeValuePair value = get( row, column );
      return value.getValue();
    }

    private TypeValuePair get( final int row, final int column )
      throws EvaluationException {
      if ( row < 0 || row >= rowCount ) {
        throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ILLEGAL_ARRAY_VALUE );
      }
      if ( column < 0 || column >= columnCount ) {
        throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ILLEGAL_ARRAY_VALUE );
      }
      try {
        TypeValuePair value = backend[ row ][ column ];
        if ( value == null ) {
          value = getRaw( row, column ).evaluate();
          backend[ row ][ column ] = value;
        }
        return value;
      } catch ( IndexOutOfBoundsException ioe ) {
        throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ILLEGAL_ARRAY_VALUE );
      }
    }

    public Type getType( final int row, final int column ) throws EvaluationException {
      return get( row, column ).getType();
    }

    public int getColumnCount() {
      return table.getColumnCount();
    }

    public int getRowCount() {
      return table.getRowCount();
    }
  }

  private transient Boolean constant;
  private static final LValue[] EMPTY_LVALUES = new LValue[ 0 ];
  private static final long serialVersionUID = 4942690291611203409L;
  private ParsePosition parsePosition;

  /**
   * Creates a new table.
   */
  public DefaultDataTable() {
    setData( new LValue[ 0 ][ 0 ], 0 );
  }

  public DefaultDataTable( final LValue[][] array ) {
    if ( array != null && array.length > 0 ) {
      final int colCount = array[ 0 ].length;
      setData( array, colCount );
    } else {
      setData( new LValue[ 0 ][ 0 ], 0 );
    }
  }

  public ParsePosition getParsePosition() {
    return parsePosition;
  }

  public void setParsePosition( final ParsePosition parsePosition ) {
    this.parsePosition = parsePosition;
  }

  public String getColumnName( int column ) {
    final StringBuffer result = new StringBuffer( 10 );
    for (; column >= 0; column = column / 26 - 1 ) {
      final int colChar = (char) ( column % 26 ) + 'A';
      result.append( colChar );
    }
    return result.toString();
  }

  /**
   * Sets the object for a cell in the table.  The table is expanded if necessary.
   *
   * @param row    the row index (zero-based).
   * @param column the column index (zero-based).
   * @param object the object.
   */
  public void setObject( final int row, final int column, final LValue object ) {
    super.setObject( row, column, object );
  }

  public LValue getValueAt( final int row, final int column ) {
    return (LValue) getObject( row, column );
  }

  public void initialize( final FormulaContext context ) throws EvaluationException {
    final int rows = getRowCount();
    final int cols = getColumnCount();
    for ( int row = 0; row < rows; row++ ) {
      for ( int col = 0; col < cols; col++ ) {
        final LValue value = getValueAt( row, col );
        if ( value != null ) {
          value.initialize( context );
        }
      }
    }
  }

  public TypeValuePair evaluate() throws EvaluationException {
    int colCount = -1;
    final LValue[][] array = (LValue[][]) getData();
    for ( int i = 0; i < array.length; i++ ) {
      final LValue[] row = array[ i ];
      if ( colCount > 0 && row.length != colCount ) {
        // error, different column count is not allowed
        throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ILLEGAL_ARRAY_VALUE );
      } else {
        colCount = row.length;
      }
    }
    return new TypeValuePair( AnyType.ANY_ARRAY, new DefaultArrayCallback( this ) );
  }

  public ArrayCallback getAsArray() {
    return new DefaultArrayCallback( this );
  }

  public Object clone() throws CloneNotSupportedException {
    final DefaultDataTable table = (DefaultDataTable) super.clone();
    final Object[][] data = getData();
    final Object[][] targetData = (Object[][]) data.clone();
    for ( int i = 0; i < targetData.length; i++ ) {
      final Object[] objects = targetData[ i ];
      if ( objects == null ) {
        continue;
      }

      targetData[ i ] = (Object[]) objects.clone();
      for ( int j = 0; j < objects.length; j++ ) {
        final LValue object = (LValue) objects[ j ];
        if ( object == null ) {
          continue;
        }
        objects[ j ] = object.clone();
      }
    }

    table.setData( targetData, getColumnCount() );
    return table;
  }

  /**
   * Querying the value type is only valid *after* the value has been evaluated.
   *
   * @return
   */
  public Type getValueType() {
    return AnyType.ANY_ARRAY;
  }

  /**
   * Returns any dependent lvalues (parameters and operands, mostly).
   *
   * @return
   */
  public LValue[] getChildValues() {
    // too expensive ...
    return EMPTY_LVALUES;
  }

  /**
   * Checks whether the LValue is constant. Constant lvalues always return the same value.
   *
   * @return
   */
  public boolean isConstant() {
    if ( constant == null ) {
      if ( computeConstantValue() ) {
        constant = Boolean.TRUE;
      } else {
        constant = Boolean.FALSE;
      }
    }

    return Boolean.TRUE.equals( constant );
  }

  private boolean computeConstantValue() {
    final int rows = getRowCount();
    final int cols = getColumnCount();
    for ( int row = 0; row < rows; row++ ) {
      for ( int col = 0; col < cols; col++ ) {
        final LValue value = getValueAt( row, col );
        if ( value.isConstant() == false ) {
          return false;
        }
      }
    }
    return true;
  }

  public String toString() {
    final StringBuffer b = new StringBuffer();
    final int rowcount = getRowCount();
    final int colcount = getColumnCount();
    b.append( "{" );
    for ( int row = 0; row < rowcount; row += 1 ) {
      if ( row > 0 ) {
        b.append( "|" );
      }

      for ( int col = 0; col < colcount; col += 1 ) {
        if ( col > 0 ) {
          b.append( ";" );
        }
        b.append( getValueAt( row, col ) );
      }
    }
    b.append( "}" );
    return b.toString();
  }
}
