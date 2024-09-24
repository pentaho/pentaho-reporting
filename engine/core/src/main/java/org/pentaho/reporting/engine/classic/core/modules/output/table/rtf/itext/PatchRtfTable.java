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

import com.lowagie.text.Element;
import com.lowagie.text.Row;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.PdfPRow;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.rtf.RtfElement;
import com.lowagie.text.rtf.document.RtfDocument;
import com.lowagie.text.rtf.style.RtfFont;
import com.lowagie.text.rtf.text.RtfParagraph;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * The PatchRtfTable wraps a Table. INTERNAL USE ONLY
 *
 * @author Mark Hall (Mark.Hall@mail.room3b.eu)
 * @author Steffen Stundzig
 * @author Benoit Wiart
 * @author Thomas Bickel (tmb99@inode.at)
 * @version $Id: PatchRtfTable.java 3533 2008-07-07 21:27:13Z Howard_s $
 */
public class PatchRtfTable extends RtfElement {

  /**
   * The rows of this PatchRtfTable
   */
  private ArrayList<PatchRtfRow> rows = null;
  /**
   * The percentage of the page width that this PatchRtfTable covers
   */
  private float tableWidthPercent = 80;
  /**
   * An array with the proportional widths of the cells in each row
   */
  private float[] proportionalWidths = null;
  /**
   * The cell padding
   */
  private float cellPadding = 0;
  /**
   * The cell spacing
   */
  private float cellSpacing = 0;

  /**
   * The border style of this PatchRtfTable
   */
  private PatchRtfBorderGroup borders = null;
  /**
   * The alignment of this PatchRtfTable
   */
  private int alignment = Element.ALIGN_CENTER;
  /**
   * Whether the cells in this PatchRtfTable must fit in a page
   */
  private boolean cellsFitToPage = false;
  /**
   * Whether the whole PatchRtfTable must fit in a page
   */
  private boolean tableFitToPage = false;
  /**
   * The number of header rows in this PatchRtfTable
   */
  private int headerRows = 0;
  /**
   * The offset from the previous text
   */
  private int offset = -1;

  /**
   * Constructs a PatchRtfTable based on a Table for a RtfDocument.
   *
   * @param doc
   *          The RtfDocument this PatchRtfTable belongs to
   * @param table
   *          The Table that this PatchRtfTable wraps
   */
  public PatchRtfTable( RtfDocument doc, Table table ) {
    super( doc );
    table.complete();
    importTable( table );
  }

  /**
   * Constructs a PatchRtfTable based on a PdfTable for a RtfDocument.
   *
   * @param doc
   *          The RtfDocument this PatchRtfTable belongs to
   * @param table
   *          The PdfPTable that this PatchRtfTable wraps
   * @since 2.1.3
   */
  public PatchRtfTable( RtfDocument doc, PdfPTable table ) {
    super( doc );
    importTable( table );
  }

  /**
   * Imports the rows and settings from the Table into this PatchRtfTable.
   *
   * @param table
   *          The source Table
   */
  private void importTable( Table table ) {
    this.rows = new ArrayList<PatchRtfRow>();
    this.tableWidthPercent = table.getWidth();
    this.proportionalWidths = table.getProportionalWidths();
    this.cellPadding = (float) ( table.getPadding() * TWIPS_FACTOR );
    this.cellSpacing = (float) ( table.getSpacing() * TWIPS_FACTOR );
    this.borders =
        new PatchRtfBorderGroup( this.document, PatchRtfBorder.ROW_BORDER, table.getBorder(), table.getBorderWidth(),
            table.getBorderColor() );
    this.alignment = table.getAlignment();

    int i = 0;
    Iterator rowIterator = table.iterator();
    while ( rowIterator.hasNext() ) {
      this.rows.add( new PatchRtfRow( this.document, this, (Row) rowIterator.next(), i ) );
      // avoid out of memory exception
      rowIterator.remove();
      i++;
    }
    for ( i = 0; i < this.rows.size(); i++ ) {
      this.rows.get( i ).handleCellSpanning();
      this.rows.get( i ).cleanRow();
    }
    this.headerRows = table.getLastHeaderRow();
    this.cellsFitToPage = table.isCellsFitPage();
    this.tableFitToPage = table.isTableFitsPage();
    if ( !Float.isNaN( table.getOffset() ) ) {
      this.offset = (int) ( table.getOffset() * 2 );
    }
  }

  /**
   * Imports the rows and settings from the Table into this PatchRtfTable.
   *
   * @param table
   *          The source PdfPTable
   * @since 2.1.3
   */
  private void importTable( PdfPTable table ) {
    this.rows = new ArrayList<PatchRtfRow>();
    this.tableWidthPercent = table.getWidthPercentage();
    // this.tableWidthPercent = table.getWidth();
    this.proportionalWidths = table.getAbsoluteWidths();
    // this.proportionalWidths = table.getProportionalWidths();
    this.cellPadding = (float) ( table.spacingAfter() * TWIPS_FACTOR );
    // this.cellPadding = (float) (table.getPadding() * TWIPS_FACTOR);
    this.cellSpacing = (float) ( table.spacingAfter() * TWIPS_FACTOR );
    // this.cellSpacing = (float) (table.getSpacing() * TWIPS_FACTOR);
    // this.borders = new PatchRtfBorderGroup(this.document, PatchRtfBorder.ROW_BORDER, table.getBorder(),
    // table.getBorderWidth(), table.getBorderColor());
    // this.borders = new PatchRtfBorderGroup(this.document, PatchRtfBorder.ROW_BORDER, table.getBorder(),
    // table.getBorderWidth(), table.getBorderColor());
    this.alignment = table.getHorizontalAlignment();
    // this.alignment = table.getAlignment();

    int i = 0;
    Iterator rowIterator = table.getRows().iterator();
    // Iterator rowIterator = table.iterator();
    while ( rowIterator.hasNext() ) {
      this.rows.add( new PatchRtfRow( this.document, this, (PdfPRow) rowIterator.next(), i ) );
      i++;
    }
    for ( i = 0; i < this.rows.size(); i++ ) {
      this.rows.get( i ).handleCellSpanning();
      this.rows.get( i ).cleanRow();
    }

    this.headerRows = table.getHeaderRows();
    // this.headerRows = table.getLastHeaderRow();
    this.cellsFitToPage = table.getKeepTogether();
    // this.cellsFitToPage = table.isCellsFitPage();
    this.tableFitToPage = table.getKeepTogether();
    // this.tableFitToPage = table.isTableFitsPage();
    // if(!Float.isNaN(table.getOffset())) {
    // this.offset = (int) (table.getOffset() * 2);
    // }
    // if(!Float.isNaN(table.getOffset())) {
    // this.offset = (int) (table.getOffset() * 2);
    // }
  }

  /**
   * Writes the content of this PatchRtfTable
   */
  public void writeContent( final OutputStream result ) throws IOException {
    if ( !inHeader ) {
      if ( this.offset != -1 ) {
        result.write( RtfFont.FONT_SIZE );
        result.write( intToByteArray( this.offset ) );
      }
      result.write( RtfParagraph.PARAGRAPH );
    }

    final Iterator<PatchRtfRow> iterator = this.rows.iterator();
    while ( iterator.hasNext() ) {
      final RtfElement re = iterator.next();
      re.writeContent( result );
      iterator.remove();
    }

    result.write( RtfParagraph.PARAGRAPH_DEFAULTS );
  }

  /**
   * Gets the alignment of this PatchRtfTable
   *
   * @return The alignment of this PatchRtfTable.
   */
  protected int getAlignment() {
    return alignment;
  }

  /**
   * Gets the borders of this PatchRtfTable
   *
   * @return The borders of this PatchRtfTable.
   */
  protected PatchRtfBorderGroup getBorders() {
    return this.borders;
  }

  /**
   * Gets the cell padding of this PatchRtfTable
   *
   * @return The cell padding of this PatchRtfTable.
   */
  protected float getCellPadding() {
    return cellPadding;
  }

  /**
   * Gets the cell spacing of this PatchRtfTable
   *
   * @return The cell spacing of this PatchRtfTable.
   */
  protected float getCellSpacing() {
    return cellSpacing;
  }

  /**
   * Gets the proportional cell widths of this PatchRtfTable
   *
   * @return The proportional widths of this PatchRtfTable.
   */
  protected float[] getProportionalWidths() {
    return proportionalWidths.clone();
  }

  /**
   * Gets the percentage of the page width this PatchRtfTable covers
   *
   * @return The percentage of the page width.
   */
  protected float getTableWidthPercent() {
    return tableWidthPercent;
  }

  /**
   * Gets the rows of this PatchRtfTable
   *
   * @return The rows of this PatchRtfTable
   */
  protected ArrayList getRows() {
    return this.rows;
  }

  /**
   * Gets the cellsFitToPage setting of this PatchRtfTable.
   *
   * @return The cellsFitToPage setting of this PatchRtfTable.
   */
  protected boolean getCellsFitToPage() {
    return this.cellsFitToPage;
  }

  /**
   * Gets the tableFitToPage setting of this PatchRtfTable.
   *
   * @return The tableFitToPage setting of this PatchRtfTable.
   */
  protected boolean getTableFitToPage() {
    return this.tableFitToPage;
  }

  /**
   * Gets the number of header rows of this PatchRtfTable
   *
   * @return The number of header rows
   */
  protected int getHeaderRows() {
    return this.headerRows;
  }
}
