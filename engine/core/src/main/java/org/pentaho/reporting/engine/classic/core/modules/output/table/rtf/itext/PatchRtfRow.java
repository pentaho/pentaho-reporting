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

package org.pentaho.reporting.engine.classic.core.modules.output.table.rtf.itext;

import com.lowagie.text.Cell;
import com.lowagie.text.DocWriter;
import com.lowagie.text.Element;
import com.lowagie.text.Row;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPRow;
import com.lowagie.text.rtf.RtfElement;
import com.lowagie.text.rtf.document.RtfDocument;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * The PatchRtfRow wraps one Row for a PatchRtfTable. INTERNAL USE ONLY
 *
 * @author Mark Hall (Mark.Hall@mail.room3b.eu)
 * @author Steffen Stundzig
 * @author Lorenz Maierhofer
 * @author Thomas Bickel (tmb99@inode.at)
 * @version $Id: PatchRtfRow.java 3735 2009-02-26 01:44:03Z xlv $
 */
@SuppressWarnings( "HardCodedStringLiteral" )
public class PatchRtfRow extends RtfElement {

  /**
   * Constant for the PatchRtfRow beginning
   */
  private static final byte[] ROW_BEGIN = DocWriter.getISOBytes( "\\trowd" );
  private static final byte[] ROW_HEIGHT = DocWriter.getISOBytes( "\\trrh" );
  /**
   * Constant for the PatchRtfRow width style
   */
  private static final byte[] ROW_WIDTH_STYLE = DocWriter.getISOBytes( "\\trftsWidth3" );
  /**
   * Constant for the PatchRtfRow width
   */
  private static final byte[] ROW_WIDTH = DocWriter.getISOBytes( "\\trwWidth" );
  /**
   * Constant to specify that this PatchRtfRow are not to be broken across pages
   */
  private static final byte[] ROW_KEEP_TOGETHER = DocWriter.getISOBytes( "\\trkeep" );
  /**
   * Constant to specify that this is a header PatchRtfRow
   */
  private static final byte[] ROW_HEADER_ROW = DocWriter.getISOBytes( "\\trhdr" );
  /**
   * Constant for left alignment of this PatchRtfRow
   */
  private static final byte[] ROW_ALIGN_LEFT = DocWriter.getISOBytes( "\\trql" );
  /**
   * Constant for right alignment of this PatchRtfRow
   */
  private static final byte[] ROW_ALIGN_RIGHT = DocWriter.getISOBytes( "\\trqr" );
  /**
   * Constant for center alignment of this PatchRtfRow
   */
  private static final byte[] ROW_ALIGN_CENTER = DocWriter.getISOBytes( "\\trqc" );
  /**
   * Constant for justified alignment of this PatchRtfRow
   */
  private static final byte[] ROW_ALIGN_JUSTIFIED = DocWriter.getISOBytes( "\\trqj" );
  /**
   * Constant for the graph style of this PatchRtfRow
   */
  private static final byte[] ROW_GRAPH = DocWriter.getISOBytes( "\\trgaph10" );
  /**
   * Constant for the cell left spacing
   */
  private static final byte[] ROW_CELL_SPACING_LEFT = DocWriter.getISOBytes( "\\trspdl" );
  /**
   * Constant for the cell top spacing
   */
  private static final byte[] ROW_CELL_SPACING_TOP = DocWriter.getISOBytes( "\\trspdt" );
  /**
   * Constant for the cell right spacing
   */
  private static final byte[] ROW_CELL_SPACING_RIGHT = DocWriter.getISOBytes( "\\trspdr" );
  /**
   * Constant for the cell bottom spacing
   */
  private static final byte[] ROW_CELL_SPACING_BOTTOM = DocWriter.getISOBytes( "\\trspdb" );
  /**
   * Constant for the cell left spacing style
   */
  private static final byte[] ROW_CELL_SPACING_LEFT_STYLE = DocWriter.getISOBytes( "\\trspdfl3" );
  /**
   * Constant for the cell top spacing style
   */
  private static final byte[] ROW_CELL_SPACING_TOP_STYLE = DocWriter.getISOBytes( "\\trspdft3" );
  /**
   * Constant for the cell right spacing style
   */
  private static final byte[] ROW_CELL_SPACING_RIGHT_STYLE = DocWriter.getISOBytes( "\\trspdfr3" );
  /**
   * Constant for the cell bottom spacing style
   */
  private static final byte[] ROW_CELL_SPACING_BOTTOM_STYLE = DocWriter.getISOBytes( "\\trspdfb3" );
  /**
   * Constant for the cell left padding
   */
  private static final byte[] ROW_CELL_PADDING_LEFT = DocWriter.getISOBytes( "\\trpaddl" );
  /**
   * Constant for the cell right padding
   */
  private static final byte[] ROW_CELL_PADDING_RIGHT = DocWriter.getISOBytes( "\\trpaddr" );
  /**
   * Constant for the cell left padding style
   */
  private static final byte[] ROW_CELL_PADDING_LEFT_STYLE = DocWriter.getISOBytes( "\\trpaddfl3" );
  /**
   * Constant for the cell right padding style
   */
  private static final byte[] ROW_CELL_PADDING_RIGHT_STYLE = DocWriter.getISOBytes( "\\trpaddfr3" );
  /**
   * Constant for the end of a row
   */
  private static final byte[] ROW_END = DocWriter.getISOBytes( "\\row" );

  /**
   * The PatchRtfTable this PatchRtfRow belongs to
   */
  private PatchRtfTable parentTable = null;
  /**
   * The cells of this PatchRtfRow
   */
  private ArrayList<PatchRtfCell> cells = null;
  /**
   * The width of this row
   */
  private int width = 0;
  /**
   * The row number
   */
  private int rowNumber = 0;

  /**
   * Constructs a PatchRtfRow for a Row.
   *
   * @param doc
   *          The RtfDocument this PatchRtfRow belongs to
   * @param PatchRtfTable
   *          The PatchRtfTable this PatchRtfRow belongs to
   * @param row
   *          The Row this PatchRtfRow is based on
   * @param rowNumber
   *          The number of this row
   */
  protected PatchRtfRow( RtfDocument doc, PatchRtfTable PatchRtfTable, Row row, int rowNumber ) {
    super( doc );
    this.parentTable = PatchRtfTable;
    this.rowNumber = rowNumber;
    importRow( row );
  }

  /**
   * Constructs a PatchRtfRow for a Row.
   *
   * @param doc
   *          The RtfDocument this PatchRtfRow belongs to
   * @param PatchRtfTable
   *          The PatchRtfTable this PatchRtfRow belongs to
   * @param row
   *          The Row this PatchRtfRow is based on
   * @param rowNumber
   *          The number of this row
   * @since 2.1.3
   */
  protected PatchRtfRow( RtfDocument doc, PatchRtfTable PatchRtfTable, PdfPRow row, int rowNumber ) {
    super( doc );
    this.parentTable = PatchRtfTable;
    this.rowNumber = rowNumber;
    importRow( row );
  }

  /**
   * Imports a Row and copies all settings
   *
   * @param row
   *          The Row to import
   */
  private void importRow( Row row ) {
    this.cells = new ArrayList<PatchRtfCell>();
    this.width =
        this.document.getDocumentHeader().getPageSetting().getPageWidth()
            - this.document.getDocumentHeader().getPageSetting().getMarginLeft()
            - this.document.getDocumentHeader().getPageSetting().getMarginRight();
    this.width = (int) ( this.width * this.parentTable.getTableWidthPercent() / 100 );

    int cellRight = 0;
    int cellWidth = 0;
    for ( int i = 0; i < row.getColumns(); i++ ) {
      cellWidth = (int) ( this.width * this.parentTable.getProportionalWidths()[i] / 100 );
      cellRight = cellRight + cellWidth;

      Cell cell = (Cell) row.getCell( i );
      PatchRtfCell rtfCell = new PatchRtfCell( this.document, this, cell );
      rtfCell.setCellRight( cellRight );
      rtfCell.setCellWidth( cellWidth );
      this.cells.add( rtfCell );
    }
  }

  /**
   * Imports a PdfPRow and copies all settings
   *
   * @param row
   *          The PdfPRow to import
   * @since 2.1.3
   */
  private void importRow( PdfPRow row ) {
    this.cells = new ArrayList<PatchRtfCell>();
    this.width =
        this.document.getDocumentHeader().getPageSetting().getPageWidth()
            - this.document.getDocumentHeader().getPageSetting().getMarginLeft()
            - this.document.getDocumentHeader().getPageSetting().getMarginRight();
    this.width = (int) ( this.width * this.parentTable.getTableWidthPercent() / 100 );

    int cellRight = 0;
    int cellWidth = 0;
    PdfPCell[] cells = row.getCells();
    for ( int i = 0; i < cells.length; i++ ) {
      cellWidth = (int) ( this.width * this.parentTable.getProportionalWidths()[i] / 100 );
      cellRight = cellRight + cellWidth;

      PdfPCell cell = cells[i];
      PatchRtfCell rtfCell = new PatchRtfCell( this.document, this, cell );
      rtfCell.setCellRight( cellRight );
      rtfCell.setCellWidth( cellWidth );
      this.cells.add( rtfCell );
    }
  }

  /**
   * Performs a second pass over all cells to handle cell row/column spanning.
   */
  protected void handleCellSpanning() {
    PatchRtfCell deletedCell = new PatchRtfCell( true );
    for ( int i = 0; i < this.cells.size(); i++ ) {
      PatchRtfCell rtfCell = this.cells.get( i );
      if ( rtfCell.getColspan() > 1 ) {
        int cSpan = rtfCell.getColspan();
        for ( int j = i + 1; j < i + cSpan; j++ ) {
          if ( j < this.cells.size() ) {
            PatchRtfCell rtfCellMerge = this.cells.get( j );
            rtfCell.setCellRight( rtfCell.getCellRight() + rtfCellMerge.getCellWidth() );
            rtfCell.setCellWidth( rtfCell.getCellWidth() + rtfCellMerge.getCellWidth() );
            this.cells.set( j, deletedCell );
          }
        }
      }
      if ( rtfCell.getRowspan() > 1 ) {
        ArrayList rows = this.parentTable.getRows();
        for ( int j = 1; j < rtfCell.getRowspan(); j++ ) {
          PatchRtfRow mergeRow = (PatchRtfRow) rows.get( this.rowNumber + j );
          if ( this.rowNumber + j < rows.size() ) {
            PatchRtfCell rtfCellMerge = mergeRow.getCells().get( i );
            rtfCellMerge.setCellMergeChild( rtfCell );
          }
          if ( rtfCell.getColspan() > 1 ) {
            int cSpan = rtfCell.getColspan();
            for ( int k = i + 1; k < i + cSpan; k++ ) {
              if ( k < mergeRow.getCells().size() ) {
                mergeRow.getCells().set( k, deletedCell );
              }
            }
          }
        }
      }
    }
  }

  /**
   * Cleans the deleted PatchRtfCells from the total PatchRtfCells.
   */
  protected void cleanRow() {
    int i = 0;
    while ( i < this.cells.size() ) {
      if ( ( this.cells.get( i ) ).isDeleted() ) {
        this.cells.remove( i );
      } else {
        i++;
      }
    }
  }

  /**
   * Writes the row definition/settings.
   *
   * @param result
   *          The <code>OutputStream</code> to write the definitions to.
   */
  private void writeRowDefinition( final OutputStream result ) throws IOException {
    result.write( ROW_BEGIN );
    this.document.outputDebugLinebreak( result );
    result.write( ROW_WIDTH_STYLE );
    result.write( ROW_WIDTH );
    result.write( intToByteArray( this.width ) );
    if ( this.parentTable.getCellsFitToPage() ) {
      result.write( ROW_KEEP_TOGETHER );
    }

    float minHeightInTwips = 0;
    for ( int i = 0; i < this.cells.size(); i++ ) {
      PatchRtfCell rtfCell = this.cells.get( i );
      minHeightInTwips = Math.max( minHeightInTwips, rtfCell.getMinimumHeight() );
    }
    result.write( ROW_HEIGHT );
    result.write( intToByteArray( (int) ( minHeightInTwips * 20 ) ) );

    if ( this.rowNumber <= this.parentTable.getHeaderRows() ) {
      result.write( ROW_HEADER_ROW );
    }
    switch ( this.parentTable.getAlignment() ) {
      case Element.ALIGN_LEFT:
        result.write( ROW_ALIGN_LEFT );
        break;
      case Element.ALIGN_RIGHT:
        result.write( ROW_ALIGN_RIGHT );
        break;
      case Element.ALIGN_CENTER:
        result.write( ROW_ALIGN_CENTER );
        break;
      case Element.ALIGN_JUSTIFIED:
      case Element.ALIGN_JUSTIFIED_ALL:
        result.write( ROW_ALIGN_JUSTIFIED );
        break;
    }
    result.write( ROW_GRAPH );
    PatchRtfBorderGroup borders = this.parentTable.getBorders();
    if ( borders != null ) {
      borders.writeContent( result );
    }

    if ( this.parentTable.getCellSpacing() > 0 ) {
      result.write( ROW_CELL_SPACING_LEFT );
      result.write( intToByteArray( (int) ( this.parentTable.getCellSpacing() / 2 ) ) );
      result.write( ROW_CELL_SPACING_LEFT_STYLE );
      result.write( ROW_CELL_SPACING_TOP );
      result.write( intToByteArray( (int) ( this.parentTable.getCellSpacing() / 2 ) ) );
      result.write( ROW_CELL_SPACING_TOP_STYLE );
      result.write( ROW_CELL_SPACING_RIGHT );
      result.write( intToByteArray( (int) ( this.parentTable.getCellSpacing() / 2 ) ) );
      result.write( ROW_CELL_SPACING_RIGHT_STYLE );
      result.write( ROW_CELL_SPACING_BOTTOM );
      result.write( intToByteArray( (int) ( this.parentTable.getCellSpacing() / 2 ) ) );
      result.write( ROW_CELL_SPACING_BOTTOM_STYLE );
    }

    result.write( ROW_CELL_PADDING_LEFT );
    result.write( intToByteArray( (int) ( this.parentTable.getCellPadding() / 2 ) ) );
    result.write( ROW_CELL_PADDING_RIGHT );
    result.write( intToByteArray( (int) ( this.parentTable.getCellPadding() / 2 ) ) );
    result.write( ROW_CELL_PADDING_LEFT_STYLE );
    result.write( ROW_CELL_PADDING_RIGHT_STYLE );

    this.document.outputDebugLinebreak( result );

    for ( int i = 0; i < this.cells.size(); i++ ) {
      PatchRtfCell rtfCell = this.cells.get( i );
      rtfCell.writeDefinition( result );
    }
  }

  /**
   * Writes the content of this PatchRtfRow
   */
  public void writeContent( final OutputStream result ) throws IOException {
    writeRowDefinition( result );

    for ( int i = 0; i < this.cells.size(); i++ ) {
      PatchRtfCell rtfCell = this.cells.get( i );
      rtfCell.writeContent( result );
    }

    result.write( DELIMITER );

    if ( this.document.getDocumentSettings().isOutputTableRowDefinitionAfter() ) {
      writeRowDefinition( result );
    }

    result.write( ROW_END );
    this.document.outputDebugLinebreak( result );
  }

  /**
   * Gets the parent PatchRtfTable of this PatchRtfRow
   *
   * @return The parent PatchRtfTable of this PatchRtfRow
   */
  protected PatchRtfTable getParentTable() {
    return this.parentTable;
  }

  /**
   * Gets the cells of this PatchRtfRow
   *
   * @return The cells of this PatchRtfRow
   */
  protected ArrayList<PatchRtfCell> getCells() {
    return this.cells;
  }
}
