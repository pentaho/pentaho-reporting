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

import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.DocWriter;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Image;
import com.lowagie.text.List;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.rtf.RtfBasicElement;
import com.lowagie.text.rtf.RtfExtendedElement;
import com.lowagie.text.rtf.document.RtfDocument;
import com.lowagie.text.rtf.style.RtfColor;
import com.lowagie.text.rtf.style.RtfParagraphStyle;
import com.lowagie.text.rtf.text.RtfParagraph;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * The PatchRtfCell wraps a Cell, but can also be added directly to a Table. The PatchRtfCell is an extension of Cell,
 * that supports a multitude of different borderstyles.
 *
 * @author Mark Hall (Mark.Hall@mail.room3b.eu)
 * @author Steffen Stundzig
 * @author Benoit Wiart
 * @author Thomas Bickel (tmb99@inode.at)
 * @version $Id: PatchRtfCell.java 3580 2008-08-06 15:52:00Z howard_s $
 * @see com.lowagie.text.rtf.table.RtfBorder
 */
@SuppressWarnings( "HardCodedStringLiteral" )
public class PatchRtfCell extends Cell implements RtfExtendedElement {

  /**
   * This cell is not merged
   */
  private static final int MERGE_NONE = 0;
  /**
   * This cell is the parent cell of a vertical merge operation
   */
  private static final int MERGE_VERT_PARENT = 1;
  /**
   * This cell is a child cell of a vertical merge operation
   */
  private static final int MERGE_VERT_CHILD = 2;

  /**
   * The parent PatchRtfRow of this PatchRtfCell
   */
  private PatchRtfRow parentRow = null;
  /**
   * The content of this PatchRtfCell
   */
  private ArrayList<RtfBasicElement> content = null;
  /**
   * The right margin of this PatchRtfCell
   */
  private int cellRight = 0;
  /**
   * The width of this PatchRtfCell
   */
  private int cellWidth = 0;
  /**
   * The borders of this PatchRtfCell
   */
  private PatchRtfBorderGroup borders = null;

  /**
   * The background color of this PatchRtfCell
   */
  private RtfColor backgroundColor = null;
  /**
   * The padding of this PatchRtfCell
   */
  private int cellPadding = 0;
  /**
   * The merge type of this PatchRtfCell
   */
  private int mergeType = MERGE_NONE;
  /**
   * The RtfDocument this PatchRtfCell belongs to
   */
  private RtfDocument document = null;
  /**
   * Whether this PatchRtfCell is in a header
   */
  private boolean inHeader = false;
  /**
   * Whether this PatchRtfCell is a placeholder for a removed table cell.
   */
  private boolean deleted = false;

  /**
   * Whether to use generic padding or individual padding values (cellPaddingLeft, cellPaddingTop, cellPaddingBottom,
   * cellPaddingRight)
   */
  private boolean usePadding = false;
  /*
   * Cell padding left
   */
  private float cellPaddingLeft = 0;
  /*
   * Cell padding top
   */
  private float cellPaddingTop = 0;
  /*
   * Cell padding bottom
   */
  private float cellPaddingBottom = 0;
  /*
   * Cell padding right
   */
  private float cellPaddingRight = 0;

  private float minimumHeight = 0;

  /**
   * Constructs an empty PatchRtfCell
   */
  public PatchRtfCell() {
    super();
    this.borders = new PatchRtfBorderGroup();
    verticalAlignment = ALIGN_MIDDLE;
  }

  /**
   * Constructs a PatchRtfCell based upon a String
   *
   * @param content
   *          The String to base the PatchRtfCell on
   */
  public PatchRtfCell( String content ) {
    super( content );
    this.borders = new PatchRtfBorderGroup();
    verticalAlignment = ALIGN_MIDDLE;
  }

  /**
   * Constructs a PatchRtfCell based upon an Element
   *
   * @param element
   *          The Element to base the PatchRtfCell on
   * @throws BadElementException
   *           If the Element is not valid
   */
  public PatchRtfCell( Element element ) throws BadElementException {
    super( element );
    this.borders = new PatchRtfBorderGroup();
    verticalAlignment = ALIGN_MIDDLE;
  }

  /**
   * Constructs a deleted PatchRtfCell.
   *
   * @param deleted
   *          Whether this PatchRtfCell is actually deleted.
   */
  protected PatchRtfCell( boolean deleted ) {
    super();
    this.deleted = deleted;
    verticalAlignment = ALIGN_MIDDLE;
  }

  /**
   * Constructs a PatchRtfCell based on a Cell.
   *
   * @param doc
   *          The RtfDocument this PatchRtfCell belongs to
   * @param row
   *          The PatchRtfRow this PatchRtfCell lies in
   * @param cell
   *          The Cell to base this PatchRtfCell on
   */
  protected PatchRtfCell( RtfDocument doc, PatchRtfRow row, Cell cell ) {
    this.document = doc;
    this.parentRow = row;
    importCell( cell );
  }

  /**
   * Constructs a PatchRtfCell based on a Cell.
   *
   * @param doc
   *          The RtfDocument this PatchRtfCell belongs to
   * @param row
   *          The PatchRtfRow this PatchRtfCell lies in
   * @param cell
   *          The PdfPCell to base this PatchRtfCell on
   * @since 2.1.3
   */
  protected PatchRtfCell( RtfDocument doc, PatchRtfRow row, PdfPCell cell ) {
    this.document = doc;
    this.parentRow = row;
    importCell( cell );
  }

  /**
   * Imports the Cell properties into the PatchRtfCell
   *
   * @param cell
   *          The Cell to import
   */
  private void importCell( Cell cell ) {
    this.content = new ArrayList<RtfBasicElement>();

    if ( cell == null ) {
      this.borders =
          new PatchRtfBorderGroup( this.document, PatchRtfBorder.CELL_BORDER, this.parentRow.getParentTable()
              .getBorders() );
      return;
    }

    if ( cell instanceof PatchRtfCell ) {
      PatchRtfCell rtfCell = (PatchRtfCell) cell;
      this.minimumHeight = rtfCell.minimumHeight;
    }

    this.colspan = cell.getColspan();
    this.rowspan = cell.getRowspan();
    if ( cell.getRowspan() > 1 ) {
      this.mergeType = MERGE_VERT_PARENT;
    }
    if ( cell instanceof PatchRtfCell ) {
      this.borders =
          new PatchRtfBorderGroup( this.document, PatchRtfBorder.CELL_BORDER, ( (PatchRtfCell) cell ).getBorders() );
    } else {
      this.borders =
          new PatchRtfBorderGroup( this.document, PatchRtfBorder.CELL_BORDER, cell.getBorder(), cell.getBorderWidth(),
              cell.getBorderColor() );
    }
    this.verticalAlignment = cell.getVerticalAlignment();
    if ( cell.getBackgroundColor() == null ) {
      this.backgroundColor = new RtfColor( this.document, 255, 255, 255 );
    } else {
      this.backgroundColor = new RtfColor( this.document, cell.getBackgroundColor() );
    }

    this.cellPadding = (int) this.parentRow.getParentTable().getCellPadding();

    Iterator cellIterator = cell.getElements();
    Paragraph container = null;
    while ( cellIterator.hasNext() ) {
      try {
        Element element = (Element) cellIterator.next();
        // should we wrap it in a paragraph
        if ( !( element instanceof Paragraph ) && !( element instanceof List ) ) {
          if ( container != null ) {
            container.add( element );
          } else {
            container = new Paragraph();
            container.setAlignment( cell.getHorizontalAlignment() );
            container.add( element );
          }
        } else {
          if ( container != null ) {
            RtfBasicElement[] rtfElements = this.document.getMapper().mapElement( container );
            for ( int i = 0; i < rtfElements.length; i++ ) {
              rtfElements[i].setInTable( true );
              this.content.add( rtfElements[i] );
            }
            container = null;
          }
          // if horizontal alignment is undefined overwrite
          // with that of enclosing cell
          if ( element instanceof Paragraph && ( (Paragraph) element ).getAlignment() == Element.ALIGN_UNDEFINED ) {
            ( (Paragraph) element ).setAlignment( cell.getHorizontalAlignment() );
          }

          RtfBasicElement[] rtfElements = this.document.getMapper().mapElement( element );
          for ( int i = 0; i < rtfElements.length; i++ ) {
            rtfElements[i].setInTable( true );
            this.content.add( rtfElements[i] );
          }
        }
      } catch ( DocumentException de ) {
        de.printStackTrace();
      }
    }
    if ( container != null ) {
      try {
        RtfBasicElement[] rtfElements = this.document.getMapper().mapElement( container );
        for ( int i = 0; i < rtfElements.length; i++ ) {
          rtfElements[i].setInTable( true );
          this.content.add( rtfElements[i] );
        }
      } catch ( DocumentException de ) {
        de.printStackTrace();
      }
    }
  }

  /**
   * Imports the Cell properties into the PatchRtfCell
   *
   * @param cell
   *          The PdfPCell to import
   * @since 2.1.3
   */
  private void importCell( PdfPCell cell ) {
    this.content = new ArrayList<RtfBasicElement>();

    if ( cell == null ) {
      this.borders =
          new PatchRtfBorderGroup( this.document, PatchRtfBorder.CELL_BORDER, this.parentRow.getParentTable()
              .getBorders() );
      return;
    }

    // padding
    this.cellPadding = (int) this.parentRow.getParentTable().getCellPadding();
    this.cellPaddingBottom = cell.getPaddingBottom();
    this.cellPaddingTop = cell.getPaddingTop();
    this.cellPaddingRight = cell.getPaddingRight();
    this.cellPaddingLeft = cell.getPaddingLeft();

    // BORDERS
    this.borders =
        new PatchRtfBorderGroup( this.document, PatchRtfBorder.CELL_BORDER, cell.getBorder(), cell.getBorderWidth(),
            cell.getBorderColor() );

    // border colors
    this.border = cell.getBorder();
    this.borderColor = cell.getBorderColor();
    this.borderColorBottom = cell.getBorderColorBottom();
    this.borderColorTop = cell.getBorderColorTop();
    this.borderColorLeft = cell.getBorderColorLeft();
    this.borderColorRight = cell.getBorderColorRight();

    // border widths
    this.borderWidth = cell.getBorderWidth();
    this.borderWidthBottom = cell.getBorderWidthBottom();
    this.borderWidthTop = cell.getBorderWidthTop();
    this.borderWidthLeft = cell.getBorderWidthLeft();
    this.borderWidthRight = cell.getBorderWidthRight();

    this.colspan = cell.getColspan();
    this.rowspan = 1; // cell.getRowspan();
    // if(cell.getRowspan() > 1) {
    // this.mergeType = MERGE_VERT_PARENT;
    // }

    this.verticalAlignment = cell.getVerticalAlignment();

    if ( cell.getBackgroundColor() == null ) {
      this.backgroundColor = new RtfColor( this.document, 255, 255, 255 );
    } else {
      this.backgroundColor = new RtfColor( this.document, cell.getBackgroundColor() );
    }

    // does it have column composite info?
    java.util.List compositeElements = cell.getCompositeElements();
    if ( compositeElements != null ) {
      Iterator cellIterator = compositeElements.iterator();
      // does it have column info?
      Paragraph container = null;
      while ( cellIterator.hasNext() ) {
        try {
          Element element = (Element) cellIterator.next();
          // should we wrap it in a paragraph
          if ( !( element instanceof Paragraph ) && !( element instanceof List ) ) {
            if ( container != null ) {
              container.add( element );
            } else {
              container = new Paragraph();
              container.setAlignment( cell.getHorizontalAlignment() );
              container.add( element );
            }
          } else {
            if ( container != null ) {
              RtfBasicElement[] rtfElements = this.document.getMapper().mapElement( container );
              for ( int i = 0; i < rtfElements.length; i++ ) {
                rtfElements[i].setInTable( true );
                this.content.add( rtfElements[i] );
              }
              container = null;
            }
            // if horizontal alignment is undefined overwrite
            // with that of enclosing cell
            if ( element instanceof Paragraph && ( (Paragraph) element ).getAlignment() == Element.ALIGN_UNDEFINED ) {
              ( (Paragraph) element ).setAlignment( cell.getHorizontalAlignment() );
            }

            RtfBasicElement[] rtfElements = this.document.getMapper().mapElement( element );
            for ( int i = 0; i < rtfElements.length; i++ ) {
              rtfElements[i].setInTable( true );
              this.content.add( rtfElements[i] );
            }
          }
        } catch ( DocumentException de ) {
          de.printStackTrace();
        }
      }
      if ( container != null ) {
        try {
          RtfBasicElement[] rtfElements = this.document.getMapper().mapElement( container );
          for ( int i = 0; i < rtfElements.length; i++ ) {
            rtfElements[i].setInTable( true );
            this.content.add( rtfElements[i] );
          }
        } catch ( DocumentException de ) {
          de.printStackTrace();
        }
      }
    }

    // does it have image info?

    Image img = cell.getImage();
    if ( img != null ) {
      try {
        RtfBasicElement[] rtfElements = this.document.getMapper().mapElement( img );
        for ( int i = 0; i < rtfElements.length; i++ ) {
          rtfElements[i].setInTable( true );
          this.content.add( rtfElements[i] );
        }
      } catch ( DocumentException e ) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    // does it have phrase info?
    Phrase phrase = cell.getPhrase();
    if ( phrase != null ) {
      try {
        RtfBasicElement[] rtfElements = this.document.getMapper().mapElement( phrase );
        for ( int i = 0; i < rtfElements.length; i++ ) {
          rtfElements[i].setInTable( true );
          this.content.add( rtfElements[i] );
        }
      } catch ( DocumentException e ) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    // does it have table info?
    PdfPTable table = cell.getTable();
    if ( table != null ) {
      this.add( table );
      // try {
      // RtfBasicElement[] rtfElements = this.document.getMapper().mapElement(table);
      // for (int i = 0; i < rtfElements.length; i++) {
      // rtfElements[i].setInTable(true);
      // this.content.add(rtfElements[i]);
      // }
      // } catch (DocumentException e) {
      // // TODO Auto-generated catch block
      // e.printStackTrace();
      // }
    }

  }

  /**
   * Write the cell definition part of this PatchRtfCell
   */
  public void writeDefinition( final OutputStream result ) throws IOException {
    if ( this.mergeType == MERGE_VERT_PARENT ) {
      result.write( DocWriter.getISOBytes( "\\clvmgf" ) );
    } else if ( this.mergeType == MERGE_VERT_CHILD ) {
      result.write( DocWriter.getISOBytes( "\\clvmrg" ) );
    }
    switch ( verticalAlignment ) {
      case Element.ALIGN_BOTTOM:
        result.write( DocWriter.getISOBytes( "\\clvertalb" ) );
        break;
      case Element.ALIGN_CENTER:
      case Element.ALIGN_MIDDLE:
        result.write( DocWriter.getISOBytes( "\\clvertalc" ) );
        break;
      case Element.ALIGN_TOP:
        result.write( DocWriter.getISOBytes( "\\clvertalt" ) );
        break;
    }
    this.borders.writeContent( result );

    if ( this.backgroundColor != null ) {
      result.write( DocWriter.getISOBytes( "\\clcbpat" ) );
      result.write( intToByteArray( this.backgroundColor.getColorNumber() ) );
    }
    this.document.outputDebugLinebreak( result );

    result.write( DocWriter.getISOBytes( "\\clftsWidth3" ) );
    this.document.outputDebugLinebreak( result );

    result.write( DocWriter.getISOBytes( "\\clwWidth" ) );
    result.write( intToByteArray( this.cellWidth ) );
    this.document.outputDebugLinebreak( result );

    if ( this.cellPadding > 0 ) {
      result.write( DocWriter.getISOBytes( "\\clpadl" ) );
      result.write( intToByteArray( this.cellPadding / 2 ) );
      result.write( DocWriter.getISOBytes( "\\clpadt" ) );
      result.write( intToByteArray( this.cellPadding / 2 ) );
      result.write( DocWriter.getISOBytes( "\\clpadr" ) );
      result.write( intToByteArray( this.cellPadding / 2 ) );
      result.write( DocWriter.getISOBytes( "\\clpadb" ) );
      result.write( intToByteArray( this.cellPadding / 2 ) );
      result.write( DocWriter.getISOBytes( "\\clpadfl3" ) );
      result.write( DocWriter.getISOBytes( "\\clpadft3" ) );
      result.write( DocWriter.getISOBytes( "\\clpadfr3" ) );
      result.write( DocWriter.getISOBytes( "\\clpadfb3" ) );
    }
    result.write( DocWriter.getISOBytes( "\\cellx" ) );
    result.write( intToByteArray( this.cellRight ) );
  }

  /**
   * Write the content of this PatchRtfCell
   */
  public void writeContent( final OutputStream result ) throws IOException {
    if ( this.content.size() == 0 ) {
      result.write( RtfParagraph.PARAGRAPH_DEFAULTS );
      if ( this.parentRow.getParentTable().getTableFitToPage() ) {
        result.write( RtfParagraphStyle.KEEP_TOGETHER_WITH_NEXT );
      }
      result.write( RtfParagraph.IN_TABLE );
    } else {
      for ( int i = 0; i < this.content.size(); i++ ) {
        RtfBasicElement rtfElement = this.content.get( i );
        if ( rtfElement instanceof RtfParagraph ) {
          ( (RtfParagraph) rtfElement ).setKeepTogetherWithNext( this.parentRow.getParentTable().getTableFitToPage() );
        }
        rtfElement.writeContent( result );
        if ( rtfElement instanceof RtfParagraph && i < ( this.content.size() - 1 ) ) {
          result.write( RtfParagraph.PARAGRAPH );
        }
      }
    }
    result.write( DocWriter.getISOBytes( "\\cell" ) );
  }

  /**
   * Sets the right margin of this cell. Used in merge operations
   *
   * @param cellRight
   *          The right margin to use
   */
  protected void setCellRight( int cellRight ) {
    this.cellRight = cellRight;
  }

  /**
   * Gets the right margin of this PatchRtfCell
   *
   * @return The right margin of this PatchRtfCell.
   */
  protected int getCellRight() {
    return this.cellRight;
  }

  /**
   * Sets the cell width of this PatchRtfCell. Used in merge operations.
   *
   * @param cellWidth
   *          The cell width to use
   */
  protected void setCellWidth( int cellWidth ) {
    this.cellWidth = cellWidth;
  }

  /**
   * Gets the cell width of this PatchRtfCell
   *
   * @return The cell width of this PatchRtfCell
   */
  protected int getCellWidth() {
    return this.cellWidth;
  }

  /**
   * Gets the cell padding of this PatchRtfCell
   *
   * @return The cell padding of this PatchRtfCell
   */
  protected int getCellpadding() {
    return this.cellPadding;
  }

  /**
   * Gets the borders of this PatchRtfCell
   *
   * @return The borders of this PatchRtfCell
   */
  protected PatchRtfBorderGroup getBorders() {
    return this.borders;
  }

  /**
   * Set the borders of this PatchRtfCell
   *
   * @param borderGroup
   *          The PatchRtfBorderGroup to use as borders
   */
  public void setBorders( PatchRtfBorderGroup borderGroup ) {
    this.borders = new PatchRtfBorderGroup( this.document, PatchRtfBorder.CELL_BORDER, borderGroup );
  }

  /**
   * Get the background color of this PatchRtfCell
   *
   * @return The background color of this PatchRtfCell
   */
  protected RtfColor getRtfBackgroundColor() {
    return this.backgroundColor;
  }

  /**
   * Merge this cell into the parent cell.
   *
   * @param mergeParent
   *          The PatchRtfCell to merge with
   */
  protected void setCellMergeChild( PatchRtfCell mergeParent ) {
    this.mergeType = MERGE_VERT_CHILD;
    this.cellWidth = mergeParent.getCellWidth();
    this.cellRight = mergeParent.getCellRight();
    this.cellPadding = mergeParent.getCellpadding();
    this.borders = mergeParent.getBorders();
    this.verticalAlignment = mergeParent.getVerticalAlignment();
    this.backgroundColor = mergeParent.getRtfBackgroundColor();
  }

  /**
   * Sets the RtfDocument this PatchRtfCell belongs to
   *
   * @param doc
   *          The RtfDocument to use
   */
  public void setRtfDocument( RtfDocument doc ) {
    this.document = doc;
  }

  /**
   * Unused
   *
   * @param inTable
   */
  public void setInTable( boolean inTable ) {
  }

  /**
   * Sets whether this PatchRtfCell is in a header
   *
   * @param inHeader
   *          <code>True</code> if this PatchRtfCell is in a header, <code>false</code> otherwise
   */
  public void setInHeader( boolean inHeader ) {
    this.inHeader = inHeader;
    for ( int i = 0; i < this.content.size(); i++ ) {
      this.content.get( i ).setInHeader( inHeader );
    }
  }

  /**
   * Gets whether this <code>PatchRtfCell</code> is in a header
   *
   * @return <code>True</code> if this <code>PatchRtfCell</code> is in a header, <code>false</code> otherwise
   * @since 2.1.0
   */
  public boolean isInHeader() {
    return this.inHeader;
  }

  /**
   * Transforms an integer into its String representation and then returns the bytes of that string.
   *
   * @param i
   *          The integer to convert
   * @return A byte array representing the integer
   */
  private byte[] intToByteArray( int i ) {
    return DocWriter.getISOBytes( Integer.toString( i ) );
  }

  /**
   * Checks whether this PatchRtfCell is a placeholder for a table cell that has been removed due to col/row spanning.
   *
   * @return <code>True</code> if this PatchRtfCell is deleted, <code>false</code> otherwise.
   */
  public boolean isDeleted() {
    return this.deleted;
  }

  public float getMinimumHeight() {
    return minimumHeight;
  }

  public void setMinimumHeight( final float minimumHeight ) {
    this.minimumHeight = minimumHeight;
  }
}
