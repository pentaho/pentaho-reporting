/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions.datasources.kettle;

import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.step.RowListener;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;

import javax.swing.table.TableModel;
import java.math.BigDecimal;
import java.util.Date;

public class TableProducer implements RowListener {
  private TypedTableModel tableModel;
  private int rowsWritten;
  private RowMetaInterface rowMeta;
  private int queryLimit;
  private boolean stopOnError;

  private boolean firstCall;
  private boolean error;

  public TableProducer( final RowMetaInterface rowMeta, final int queryLimit, final boolean stopOnError ) {
    this.rowMeta = rowMeta;
    this.queryLimit = queryLimit;
    this.stopOnError = stopOnError;
    this.firstCall = true;
  }

  /**
   * This method is called when a row is written to another step (even if there is no next step)
   *
   * @param rowMeta the metadata of the row
   * @param row     the data of the row
   * @throws org.pentaho.di.core.exception.KettleStepException an exception that can be thrown to hard stop the step
   */
  public void rowWrittenEvent( final RowMetaInterface rowMeta, final Object[] row ) throws KettleStepException {
    if ( firstCall ) {
      this.tableModel = createTableModel( rowMeta );
      firstCall = false;
    }

    if ( queryLimit > 0 && rowsWritten > queryLimit ) {
      return;
    }

    try {
      rowsWritten += 1;

      final int count = tableModel.getColumnCount();
      final Object dataRow[] = new Object[ count ];
      for ( int columnNo = 0; columnNo < count; columnNo++ ) {
        final ValueMetaInterface valueMeta = rowMeta.getValueMeta( columnNo );

        switch( valueMeta.getType() ) {
          case ValueMetaInterface.TYPE_BIGNUMBER:
            dataRow[ columnNo ] = rowMeta.getBigNumber( row, columnNo );
            break;
          case ValueMetaInterface.TYPE_BOOLEAN:
            dataRow[ columnNo ] = rowMeta.getBoolean( row, columnNo );
            break;
          case ValueMetaInterface.TYPE_DATE:
            dataRow[ columnNo ] = rowMeta.getDate( row, columnNo );
            break;
          case ValueMetaInterface.TYPE_INTEGER:
            dataRow[ columnNo ] = rowMeta.getInteger( row, columnNo );
            break;
          case ValueMetaInterface.TYPE_NONE:
            dataRow[ columnNo ] = rowMeta.getString( row, columnNo );
            break;
          case ValueMetaInterface.TYPE_NUMBER:
            dataRow[ columnNo ] = rowMeta.getNumber( row, columnNo );
            break;
          case ValueMetaInterface.TYPE_STRING:
            dataRow[ columnNo ] = rowMeta.getString( row, columnNo );
            break;
          case ValueMetaInterface.TYPE_BINARY:
            dataRow[ columnNo ] = rowMeta.getBinary( row, columnNo );
            break;
          default:
            dataRow[ columnNo ] = rowMeta.getString( row, columnNo );
        }
      }
      tableModel.addRow( dataRow );
    } catch ( final KettleValueException kve ) {
      throw new KettleStepException( kve );
    } catch ( final Exception e ) {
      throw new KettleStepException( e );
    }
  }

  protected TypedTableModel createTableModel( final RowMetaInterface rowMeta ) {
    final int colCount = rowMeta.size();
    final String fieldNames[] = new String[ colCount ];
    final Class<?> fieldTypes[] = new Class<?>[ colCount ];
    for ( int columnNo = 0; columnNo < colCount; columnNo++ ) {
      final ValueMetaInterface valueMeta = rowMeta.getValueMeta( columnNo );
      fieldNames[ columnNo ] = valueMeta.getName();

      switch( valueMeta.getType() ) {
        case ValueMetaInterface.TYPE_BIGNUMBER:
          fieldTypes[ columnNo ] = BigDecimal.class;
          break;
        case ValueMetaInterface.TYPE_BOOLEAN:
          fieldTypes[ columnNo ] = Boolean.class;
          break;
        case ValueMetaInterface.TYPE_DATE:
          fieldTypes[ columnNo ] = Date.class;
          break;
        case ValueMetaInterface.TYPE_INTEGER:
          fieldTypes[ columnNo ] = Integer.class;
          break;
        case ValueMetaInterface.TYPE_NONE:
          fieldTypes[ columnNo ] = String.class;
          break;
        case ValueMetaInterface.TYPE_NUMBER:
          fieldTypes[ columnNo ] = Double.class;
          break;
        case ValueMetaInterface.TYPE_STRING:
          fieldTypes[ columnNo ] = String.class;
          break;
        case ValueMetaInterface.TYPE_BINARY:
          fieldTypes[ columnNo ] = byte[].class;
          break;
        default:
          fieldTypes[ columnNo ] = String.class;
      }

    }
    return new TypedTableModel( fieldNames, fieldTypes );
  }

  /**
   * This method is called when a row is read from another step
   *
   * @param rowMeta the metadata of the row
   * @param row     the data of the row
   * @throws org.pentaho.di.core.exception.KettleStepException an exception that can be thrown to hard stop the step
   */
  public void rowReadEvent( final RowMetaInterface rowMeta, final Object[] row ) throws KettleStepException {
  }

  /**
   * This method is called when the error handling of a row is writing a row to the error stream.
   *
   * @param rowMeta the metadata of the row
   * @param row     the data of the row
   * @throws org.pentaho.di.core.exception.KettleStepException an exception that can be thrown to hard stop the step
   */
  public void errorRowWrittenEvent( final RowMetaInterface rowMeta, final Object[] row ) throws KettleStepException {
    if ( stopOnError ) {
      throw new KettleStepException( "Aborting transformation due to error detected" );
    }
    error = true;
  }

  public TableModel getTableModel() throws ReportDataFactoryException {
    if ( stopOnError && error ) {
      throw new ReportDataFactoryException( "Transformation produced an error." );
    }

    if ( tableModel == null ) {
      return createTableModel( rowMeta );
    }
    return tableModel;
  }
}
