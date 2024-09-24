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
* Copyright (c) 2002-2021 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.ui.datasources.table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.designtime.swing.background.CancelEvent;
import org.pentaho.reporting.libraries.designtime.swing.background.CancelListener;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;

public class ImportFromFileTask implements Runnable, CancelListener {
  private static final Log logger = LogFactory.getLog( ImportFromFileTask.class );

  private File selectedFile;
  private boolean useFirstRowAsHeader;
  private TableDataSourceEditor parent;

  public ImportFromFileTask( final File selectedFile,
                             final boolean useFirstRowAsHeader,
                             final TableDataSourceEditor parent ) {
    this.parent = parent;
    if ( selectedFile == null ) {
      throw new NullPointerException();
    }
    this.selectedFile = selectedFile;
    this.useFirstRowAsHeader = useFirstRowAsHeader;
  }

  public void run() {
    importFromFile( selectedFile, useFirstRowAsHeader );
  }

  /**
   * Requests that the thread stop processing as soon as possible.
   */
  public void cancelProcessing( final CancelEvent event ) {
    Thread.currentThread().interrupt();
  }

  private void importFromFile( final File file, final boolean firstRowIsHeader ) {
    final ByteArrayOutputStream bout = new ByteArrayOutputStream( Math.max( 8192, (int) file.length() ) );
    try {
      final InputStream fin = new FileInputStream( file );
      try {
        IOUtils.getInstance().copyStreams( new BufferedInputStream( fin ), bout );
      } finally {
        fin.close();
      }

      if ( Thread.currentThread().isInterrupted() ) {
        return;
      }

      final Workbook workbook = WorkbookFactory.create( new ByteArrayInputStream( bout.toByteArray() ) );
      int sheetIndex = 0;
      if ( workbook.getNumberOfSheets() > 1 ) {
        final SheetSelectorDialog selectorDialog = new SheetSelectorDialog( workbook, parent );
        if ( selectorDialog.performSelection() ) {
          sheetIndex = selectorDialog.getSelectedIndex();
        } else {
          return;
        }
      }

      final TypedTableModel tableModel = new TypedTableModel();
      final Sheet sheet = workbook.getSheetAt( sheetIndex );
      final Iterator rowIterator = sheet.rowIterator();

      if ( firstRowIsHeader ) {
        if ( rowIterator.hasNext() ) {
          final Row headerRow = (Row) rowIterator.next();
          final short cellCount = headerRow.getLastCellNum();
          for ( short colIdx = 0; colIdx < cellCount; colIdx++ ) {
            final Cell cell = headerRow.getCell( colIdx );
            if ( cell != null ) {
              while ( colIdx > tableModel.getColumnCount() ) {
                tableModel.addColumn( Messages.getString( "TableDataSourceEditor.Column",
                  String.valueOf( tableModel.getColumnCount() ) ), Object.class );
              }

              final RichTextString string = cell.getRichStringCellValue();
              if ( string != null ) {
                tableModel.addColumn( string.getString(), Object.class );
              } else {
                tableModel.addColumn( Messages.getString( "TableDataSourceEditor.Column", String.valueOf( colIdx ) ),
                  Object.class );
              }
            }
          }
        }
      }

      Object[] rowData = null;
      while ( rowIterator.hasNext() ) {
        final Row row = (Row) rowIterator.next();
        final short cellCount = row.getLastCellNum();
        if ( cellCount == -1 ) {
          continue;
        }
        if ( rowData == null || rowData.length != cellCount ) {
          rowData = new Object[ cellCount ];
        }

        for ( short colIdx = 0; colIdx < cellCount; colIdx++ ) {
          final Cell cell = row.getCell( colIdx );

          final Object value;
          if ( cell != null ) {
            if ( cell.getCellType() == CellType.STRING ) {
              final RichTextString string = cell.getRichStringCellValue();
              if ( string != null ) {
                value = string.getString();
              } else {
                value = null;
              }
            } else if ( cell.getCellType() == CellType.NUMERIC ) {
              final CellStyle hssfCellStyle = cell.getCellStyle();
              final short dataFormat = hssfCellStyle.getDataFormat();
              final String dataFormatString = hssfCellStyle.getDataFormatString();
              if ( isDateFormat( dataFormat, dataFormatString ) ) {
                value = cell.getDateCellValue();
              } else {
                value = cell.getNumericCellValue();
              }
            } else if ( cell.getCellType() == CellType.BOOLEAN ) {
              value = cell.getBooleanCellValue();
            } else {
              value = cell.getStringCellValue();
            }
          } else {
            value = null;
          }

          if ( value != null && "".equals( value ) == false ) {
            while ( colIdx >= tableModel.getColumnCount() ) {
              tableModel.addColumn( Messages.getString( "TableDataSourceEditor.Column",
                String.valueOf( tableModel.getColumnCount() ) ), Object.class );
            }
          }

          rowData[ colIdx ] = value;
        }

        if ( Thread.currentThread().isInterrupted() ) {
          return;
        }

        tableModel.addRow( rowData );
      }

      final int colCount = tableModel.getColumnCount();
      final int rowCount = tableModel.getRowCount();
      for ( int col = 0; col < colCount; col++ ) {
        Class type = null;
        for ( int row = 0; row < rowCount; row += 1 ) {
          final Object value = tableModel.getValueAt( row, col );
          if ( value == null ) {
            continue;
          }
          if ( type == null ) {
            type = value.getClass();
          } else if ( type != Object.class ) {
            if ( type.isInstance( value ) == false ) {
              type = Object.class;
            }
          }
        }

        if ( Thread.currentThread().isInterrupted() ) {
          return;
        }

        if ( type != null ) {
          tableModel.setColumnType( col, type );
        }
      }

      parent.importComplete( tableModel );
    } catch ( Exception e ) {
      parent.importFailed( e );
      logger.error( "Failed to import spreadsheet", e ); // NON-NLS
    }
  }


  private boolean isDateFormat( final short knownFormat, final String dataFormat ) {
    if ( "GENERAL".equalsIgnoreCase( dataFormat ) ) {
      return false;
    }
    switch( knownFormat ) {
      case 0x0e:
      case 0x0f:
      case 0x10:
      case 0x11:
      case 0x12:
      case 0x13:
      case 0x14:
      case 0x15:
      case 0x16:
      case 0x2d:
      case 0x2e:
      case 0x2f:
        return true;
    }

    boolean inFormatQuote = false;
    boolean inQuote = false;
    int maybeElapsedHour = 0;
    final char[] chars = dataFormat.toCharArray();
    for ( int i = 0; i < chars.length; i++ ) {
      final char c = chars[ i ];
      if ( c == '[' ) {
        inFormatQuote = true;
        maybeElapsedHour = 0;
      } else if ( inFormatQuote ) {
        if ( c == ']' ) {
          if ( maybeElapsedHour == 1 ) {
            // seems to contain fragments of date format strings..
            return true;
          }
        } else if ( c == 'h' && maybeElapsedHour == 0 ) {
          maybeElapsedHour = 2;
        } else if ( c == 's' && maybeElapsedHour == 0 ) {
          maybeElapsedHour = 2;
        } else if ( c == 'm' && maybeElapsedHour == 0 ) {
          maybeElapsedHour = 2;
        } else {
          maybeElapsedHour = 1;
        }
      } else if ( inQuote == false && c == '"' ) {
        inQuote = true;
      } else if ( inQuote ) {
        if ( c == '"' ) {
          inQuote = false;
        }
      } else {
        if ( c == 'm' || c == 'd' || c == 'y' || c == 'h' || c == 's' || c == 'A' || c == 'a' || c == 'P'
          || c == 'p' ) {
          return true;
        }
      }
    }
    return false;
  }
}
