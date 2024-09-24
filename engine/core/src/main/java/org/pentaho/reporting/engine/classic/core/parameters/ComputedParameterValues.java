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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.parameters;

import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.function.FormulaExpression;
import org.pentaho.reporting.engine.classic.core.util.IntegerCache;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.StringUtils;

import javax.swing.table.TableModel;
import java.util.HashMap;

public class ComputedParameterValues extends DefaultParameterValues {
  private static class TableDataRow implements DataRow {
    private TableModel data;
    private int currentRow;
    private String[] columnNames;
    private HashMap<String, Integer> nameindex;
    private String valueColumn;
    private Object value;

    private TableDataRow( final TableModel data, final String valueColumn ) {
      if ( data == null ) {
        throw new NullPointerException();
      }
      this.data = data;
      this.columnNames = new String[data.getColumnCount()];
      this.nameindex = new HashMap<String, Integer>();
      this.valueColumn = valueColumn;
      for ( int i = 0; i < columnNames.length; i++ ) {
        final String name = data.getColumnName( i );
        columnNames[i] = name;
        nameindex.put( name, IntegerCache.getInteger( i ) );
      }

      this.currentRow = -1;
    }

    public int getCurrentRow() {
      return currentRow;
    }

    public void setCurrentRow( final int currentRow ) {
      if ( currentRow < -1 || currentRow >= data.getRowCount() ) {
        throw new IndexOutOfBoundsException();
      }

      this.currentRow = currentRow;
    }

    /**
     * Returns the value of the function, expression or column using its specific name. The given name is translated
     * into a valid column number and the the column is queried. For functions and expressions, the
     * <code>getValue()</code> method is called and for columns from the tablemodel the tablemodel method
     * <code>getValueAt(row, column)</code> gets called.
     *
     * @param col
     *          the item index.
     * @return the value.
     */
    public Object get( final String col ) {
      if ( col == null ) {
        throw new NullPointerException();
      }

      if ( currentRow >= 0 ) {
        final Integer o = (Integer) nameindex.get( col );
        if ( o == null ) {
          if ( ObjectUtilities.equal( col, valueColumn ) ) {
            return getValue();
          }
          return null;
        }
        return data.getValueAt( currentRow, o.intValue() );
      }
      return null;
    }

    public String[] getColumnNames() {
      if ( StringUtils.isEmpty( valueColumn ) ) {
        return (String[]) columnNames.clone();
      }

      final String[] columnNames = new String[this.columnNames.length + 1];
      System.arraycopy( this.columnNames, 0, columnNames, 0, this.columnNames.length );
      columnNames[this.columnNames.length] = valueColumn;
      return columnNames;
    }

    /**
     * Checks whether the value contained in the column has changed since the last advance-operation.
     *
     * @param name
     *          the name of the column.
     * @return true, if the value has changed, false otherwise.
     */
    public boolean isChanged( final String name ) {
      return false;
    }

    public TableModel getData() {
      return data;
    }

    public Object getValue() {
      return value;
    }

    public void setValue( final Object value ) {
      this.value = value;
    }
  }

  private static class ComputedParameterExpressionRuntime extends ParameterExpressionRuntime {
    private TableDataRow dataRow;

    private ComputedParameterExpressionRuntime( final ParameterContext parameterContext, final TableDataRow dataRow )
      throws ReportProcessingException {
      super( parameterContext, dataRow );
      this.dataRow = dataRow;
    }

    /**
     * Returns the report configuration that was used to initiate this processing run.
     *
     * @return the report configuration.
     */
    public Configuration getConfiguration() {
      return getProcessingContext().getConfiguration();
    }

    /**
     * Returns the resource-bundle factory of current processing context.
     *
     * @return the current resource-bundle factory.
     */
    public ResourceBundleFactory getResourceBundleFactory() {
      return getProcessingContext().getResourceBundleFactory();
    }

    /**
     * Grants access to the tablemodel was granted using report properties, now direct.
     *
     * @return the current tablemodel used in the report.
     */
    public TableModel getData() {
      return dataRow.getData();
    }

    /**
     * Returns the number of the row in the tablemodel that is currently being processed.
     *
     * @return the current row number.
     */
    public int getCurrentRow() {
      return dataRow.getCurrentRow();
    }

    public void setCurrentRow( final int currentRow ) {
      this.dataRow.setCurrentRow( currentRow );
    }

    public Object getValue() {
      return dataRow.getValue();
    }

    public void setValue( final Object value ) {
      dataRow.setValue( value );
    }
  }

  private FormulaExpression formula;
  private ComputedParameterExpressionRuntime expressionRuntime;

  public ComputedParameterValues( final TableModel parent, final String keyColumn, final String valueColumn,
      final String valueFormula, final ParameterContext context ) throws ReportProcessingException {
    super( parent, keyColumn, valueColumn );
    if ( valueFormula == null ) {
      throw new NullPointerException();
    }
    if ( context == null ) {
      throw new NullPointerException();
    }

    this.expressionRuntime = new ComputedParameterExpressionRuntime( context, new TableDataRow( parent, valueColumn ) );

    final FormulaExpression expression = new FormulaExpression();
    expression.setFormula( valueFormula );
    this.formula = expression;
  }

  public Object getTextValue( final int row ) {
    expressionRuntime.setCurrentRow( row );
    expressionRuntime.setValue( super.getTextValue( row ) );
    formula.setRuntime( expressionRuntime );
    try {
      return formula.getValue();
    } finally {
      formula.setRuntime( null );
    }
  }
}
