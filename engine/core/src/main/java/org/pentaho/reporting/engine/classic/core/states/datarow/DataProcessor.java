/*!
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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.states.datarow;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.states.crosstab.CrosstabSpecification;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import javax.swing.table.TableModel;
import java.util.Arrays;

public class DataProcessor implements Cloneable {
  private static final Log logger = LogFactory.getLog( DataProcessor.class );

  private PaddingController paddingDataRow;
  private ReportDataRow reportDataRow;
  private int paddingCount;
  private int cursor;
  private boolean prepadding;

  public DataProcessor() {
    cursor = 0;
  }

  public ReportDataRow getReportDataRow() {
    return reportDataRow;
  }

  public void clearReportDataRow( final MasterDataRowChangeHandler changeHandler ) {
    if ( this.reportDataRow == null ) {
      throw new IllegalStateException();
    }
    final MasterDataRowChangeEvent event = changeHandler.getReusableEvent();
    event.reuse( MasterDataRowChangeEvent.COLUMN_REMOVED, "", "" );
    final int dataColCount = this.reportDataRow.getColumnCount();
    for ( int i = dataColCount - 1; i >= 0; i-- ) {
      final String columnName = this.reportDataRow.getColumnName( i );
      if ( columnName != null ) {
        event.setColumnName( columnName );
        changeHandler.dataRowChanged( event );
      }
    }
    this.reportDataRow = null;
  }

  public void setReportDataRow( final ReportDataRow reportDataRow, final MasterDataRowChangeHandler changeHandler ) {
    if ( reportDataRow == null ) {
      throw new NullPointerException();
    }

    if ( this.reportDataRow != null ) {
      throw new IllegalStateException();
    }

    this.reportDataRow = reportDataRow;
    final MasterDataRowChangeEvent event = changeHandler.getReusableEvent();
    event.reuse( MasterDataRowChangeEvent.COLUMN_ADDED, "", null );
    final boolean readable = reportDataRow.isReadable();
    final int dataColCount = reportDataRow.getColumnCount();
    for ( int i = 0; i < dataColCount; i++ ) {
      final String columnName = reportDataRow.getColumnName( i );
      if ( columnName != null ) {
        event.setColumnName( columnName );
        if ( readable ) {
          event.setColumnValue( reportDataRow.get( i ) );
        } else {
          event.setColumnValue( null );
        }
        changeHandler.dataRowChanged( event );
      }
    }
  }

  public int getCursor() {
    return cursor;
  }

  public int getRawDataCursor() {
    return reportDataRow.getCursor();
  }

  public TableModel getRawData() {
    return reportDataRow.getReportData();
  }

  public DataProcessor advance( final boolean deepTraversingOnly, final FastGlobalView globalView ) {
    if ( deepTraversingOnly ) {
      return this;
    }

    final DataProcessor dataRow = derive();
    dataRow.cursor += 1;
    if ( paddingDataRow != null ) {
      dataRow.paddingDataRow = paddingDataRow.advance();

      if ( paddingCount > 0 ) {
        if ( logger.isDebugEnabled() ) {
          logger.debug( "Padding = " + dataRow.paddingCount + "; Cursor = " + dataRow.cursor );
        }
        dataRow.paddingCount -= 1;
        dataRow.paddingDataRow.activate( globalView );
        dataRow.paddingDataRow.refreshPaddedRow( globalView, dataRow.reportDataRow );
      } else if ( dataRow.prepadding ) {
        dataRow.paddingDataRow.activate( globalView );
        dataRow.paddingDataRow.refreshRow( globalView, reportDataRow );
        refreshData( globalView, reportDataRow );
        if ( logger.isDebugEnabled() ) {
          logger.debug( "Pre-Padding finished; Using dataset at cursor = " + dataRow.cursor + "; padding = "
              + dataRow.paddingCount );
        }
        dataRow.prepadding = false;
      } else {
        final ReportDataRow tempReportDataRow = reportDataRow.advance();
        final FastGlobalView tempGlobalView = globalView.derive();
        refreshData( tempGlobalView, tempReportDataRow );
        final Object[] tempRowKey = dataRow.paddingDataRow.createRowKey( tempGlobalView );
        final Object[] oldRowKey = paddingDataRow.createRowKey( globalView );
        // if the row key has changed ...
        if ( ObjectUtilities.equalArray( tempRowKey, oldRowKey ) == false ) {
          if ( logger.isDebugEnabled() ) {
            logger.debug( "TempRowKey=" + Arrays.asList( tempRowKey ) + "; RowKey=" + Arrays.asList( oldRowKey )
                + " Cursor=" + dataRow.cursor );
          }
          // .. check whether the current row has processed all column dimensions ...
          if ( dataRow.paddingDataRow.getCurrentCursorPosition() < dataRow.paddingDataRow.getCrosstabColumnCount() ) {
            // post padding mode. Ignore the advance, mark the next few advances as paddings until we completed all
            // column dimensions
            dataRow.paddingCount =
                dataRow.paddingDataRow.getCrosstabColumnCount() - dataRow.paddingDataRow.getCurrentCursorPosition() - 1;
            if ( logger.isDebugEnabled() ) {
              logger.debug( "RowKey Changed - Need post-padding; Cursor = " + dataRow.cursor + "; padding = "
                  + dataRow.paddingCount );
            }
            dataRow.paddingDataRow.activate( globalView );
            dataRow.paddingDataRow.refreshPaddedRow( globalView, dataRow.reportDataRow );
          } else {
            dataRow.paddingDataRow = dataRow.paddingDataRow.resetRowCursor();
            dataRow.paddingCount = dataRow.paddingDataRow.getPrePaddingRows( tempGlobalView );
            dataRow.reportDataRow = tempReportDataRow;
            if ( dataRow.paddingCount > 0 ) {
              if ( logger.isDebugEnabled() ) {
                logger.debug( "RowKey Changed, but detected need for Pre-Padding = " + dataRow.paddingCount
                    + "; Cursor = " + dataRow.cursor );
              }
              dataRow.paddingCount -= 1;
              dataRow.paddingDataRow.activate( globalView );
              dataRow.paddingDataRow.refreshPaddedRow( globalView, dataRow.reportDataRow );
              dataRow.prepadding = true;
            } else {
              if ( logger.isDebugEnabled() ) {
                logger.debug( "RowKey Changed; Advance; Cursor = " + dataRow.cursor + "; padding = "
                    + dataRow.paddingCount );
              }
              dataRow.paddingDataRow.activate( globalView );
              dataRow.paddingDataRow.refreshRow( globalView, tempReportDataRow );
              // refreshData(globalView, tempReportDataRow);
            }
          }
        } else {
          final Object[] tempColKey = dataRow.paddingDataRow.createColumnKey( tempGlobalView );
          final Object[] oldColKey = dataRow.paddingDataRow.createColumnKey( globalView );
          if ( ObjectUtilities.equalArray( tempColKey, oldColKey ) ) {
            if ( logger.isDebugEnabled() ) {
              logger.debug( "Row- and Column-Key still the same, staying in current crosstab-position = "
                  + dataRow.paddingCount + "; Cursor = " + dataRow.cursor );
            }
            // undo the advance for the padding data-row, but advance the report-data row
            dataRow.paddingDataRow = paddingDataRow;
            dataRow.reportDataRow = tempReportDataRow;
            dataRow.paddingDataRow.activate( globalView );
            dataRow.paddingDataRow.refreshRow( globalView, tempReportDataRow );
            // refreshData(globalView, tempReportDataRow);
          } else {
            // rowkey is still the same, so check for pre-paddings ...
            dataRow.paddingCount = dataRow.paddingDataRow.getPrePaddingRows( tempGlobalView );
            if ( dataRow.paddingCount > 0 ) {
              if ( logger.isDebugEnabled() ) {
                logger.debug( "RowKey same, but detected need for Padding = " + dataRow.paddingCount + "; Cursor = "
                    + dataRow.cursor );
              }
              dataRow.paddingCount -= 1;
              dataRow.paddingDataRow.activate( globalView );
              dataRow.paddingDataRow.refreshPaddedRow( globalView, dataRow.reportDataRow );
            } else {
              if ( logger.isDebugEnabled() ) {
                logger.debug( "RowKey Same; Advance; Cursor = " + dataRow.cursor + "; padding = "
                    + dataRow.paddingCount );
              }
              dataRow.reportDataRow = tempReportDataRow;
              dataRow.paddingDataRow.activate( globalView );
              dataRow.paddingDataRow.refreshRow( globalView, tempReportDataRow );
              // refreshData(globalView, tempReportDataRow);
            }
          }
        }
      }
    } else if ( reportDataRow != null ) {
      dataRow.reportDataRow = reportDataRow.advance();
      refreshData( globalView, dataRow.reportDataRow );
    }
    return dataRow;
  }

  public boolean isAdvanceable( final DataRow globalView ) {
    if ( paddingDataRow != null ) {
      if ( paddingCount > 0 ) {
        return true;
      }

      if ( paddingDataRow.getPrePaddingRows( globalView ) > 0 ) {
        return true;
      }
    }
    if ( reportDataRow != null ) {
      if ( reportDataRow.isAdvanceable() ) {
        return true;
      }

      // at the end of the report, we should be also at the end of the columns ...
      if ( paddingDataRow != null ) {
        final int colsToGo =
            ( paddingDataRow.getCrosstabColumnCount() - paddingDataRow.getCurrentCursorPosition() ) - 1;
        if ( colsToGo > 0 ) {
          return true;
        }
      }
    }
    return false;
  }

  private static void refreshData( final MasterDataRowChangeHandler changeHandler, final ReportDataRow reportDataRow ) {
    if ( reportDataRow == null ) {
      return;
    }

    logger.debug( "Refreshing data" );
    final MasterDataRowChangeEvent event = changeHandler.getReusableEvent();
    event.reuse( MasterDataRowChangeEvent.COLUMN_UPDATED, "", null );
    final int dataColCount = reportDataRow.getColumnCount();
    final boolean readable = reportDataRow.isReadable();
    for ( int i = 0; i < dataColCount; i++ ) {
      final String columnName = reportDataRow.getColumnName( i );
      if ( columnName != null ) {
        event.setColumnName( columnName );
        if ( readable ) {
          event.setColumnValue( reportDataRow.get( i ) );
        } else {
          event.setColumnValue( null );
        }
        changeHandler.dataRowChanged( event );
      }
    }
  }

  public DataProcessor startCrosstabMode( final CrosstabSpecification crosstabSpecification,
      final FastGlobalView globalView ) {
    logger.debug( "Starting crosstab mode" );

    final DataProcessor dataRow = derive();
    dataRow.paddingDataRow = new PaddingController( crosstabSpecification );
    final int prePaddingRows = dataRow.paddingDataRow.getPrePaddingRows( globalView );
    if ( prePaddingRows > 0 ) {
      // The current position of the first data-row of this crosstab does point to the first computed column.
      // this means, we have to insert one or more artificial rows now.
      dataRow.paddingCount = prePaddingRows - 1;
      if ( logger.isDebugEnabled() ) {
        logger.debug( "Starting crosstab mode: cursor=" + dataRow.cursor + " pre-padding=" + dataRow.paddingCount );
      }
      dataRow.paddingDataRow.activate( globalView );
      dataRow.paddingDataRow.refreshPaddedRow( globalView, dataRow.reportDataRow );
      dataRow.prepadding = true;
    } else {
      if ( logger.isDebugEnabled() ) {
        logger.debug( "Starting crosstab mode: cursor=" + dataRow.cursor + " pre-padding=" + dataRow.paddingCount );
      }
    }
    return dataRow;
  }

  public void refresh( final FastGlobalView globalView ) {
    if ( paddingDataRow != null && reportDataRow != null ) {
      paddingDataRow.activate( globalView );
      if ( paddingCount > 0 ) {
        paddingDataRow.refreshPaddedRow( globalView, reportDataRow );
      } else {
        paddingDataRow.refreshRow( globalView, reportDataRow );
      }
    } else if ( reportDataRow != null ) {
      refreshData( globalView, reportDataRow );
    }
  }

  public DataProcessor endCrosstabMode() {
    logger.debug( "Ending crosstab mode" );

    final DataProcessor retval = derive();
    retval.paddingDataRow = null;
    return retval;
  }

  public DataProcessor resetRowCursor() {
    if ( paddingDataRow != null ) {
      final DataProcessor dataRow = derive();
      dataRow.paddingDataRow = dataRow.paddingDataRow.resetRowCursor();
      return dataRow;
    }
    return this;
  }

  public DataProcessor derive() {
    return this.clone();
  }

  public DataProcessor clone() {
    try {
      final DataProcessor clone = (DataProcessor) super.clone();
      if ( paddingDataRow != null ) {
        clone.paddingDataRow = this.paddingDataRow.clone();
      }
      return clone;
    } catch ( CloneNotSupportedException e ) {
      throw new IllegalStateException();
    }
  }

  public CrosstabSpecification getCrosstabSpecification() {
    if ( paddingDataRow == null ) {
      return null;
    }
    return paddingDataRow.getCrosstabSpecification();
  }

  public boolean isCrosstabActive() {
    return paddingDataRow != null;
  }

  public boolean isSameState( final DataProcessor processor ) {
    if ( processor.prepadding != prepadding ) {
      return false;
    }
    if ( processor.cursor != cursor ) {
      return false;
    }
    if ( processor.paddingCount != paddingCount ) {
      return false;
    }
    return true;
  }
}
