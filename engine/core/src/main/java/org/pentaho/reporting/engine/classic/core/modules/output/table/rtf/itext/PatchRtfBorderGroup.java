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

package org.pentaho.reporting.engine.classic.core.modules.output.table.rtf.itext;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.lowagie.text.Rectangle;
import com.lowagie.text.rtf.RtfElement;
import com.lowagie.text.rtf.document.RtfDocument;

/**
 * The PatchRtfBorderGroup represents a collection of RtfBorders to use in a PatchRtfCell or PatchRtfTable.
 *
 * @author Mark Hall (Mark.Hall@mail.room3b.eu)
 * @author Thomas Bickel (tmb99@inode.at)
 * @version $Id: PatchRtfBorderGroup.java 3427 2008-05-24 18:32:31Z xlv $
 */
public class PatchRtfBorderGroup extends RtfElement {
  /**
   * The type of borders this PatchRtfBorderGroup contains. PatchRtfBorder.ROW_BORDER or PatchRtfBorder.CELL_BORDER
   */
  private int borderType = PatchRtfBorder.ROW_BORDER;
  /**
   * The borders in this PatchRtfBorderGroup
   */
  private HashMap<Integer, PatchRtfBorder> borders = null;

  /**
   * Constructs an empty PatchRtfBorderGroup.
   */
  public PatchRtfBorderGroup() {
    super( null );
    this.borders = new HashMap<Integer, PatchRtfBorder>();
  }

  /**
   * Constructs a PatchRtfBorderGroup with on border style for multiple borders.
   *
   * @param bordersToAdd
   *          The borders to add (Rectangle.LEFT, Rectangle.RIGHT, Rectangle.TOP, Rectangle.BOTTOM, Rectangle.BOX)
   * @param borderStyle
   *          The style of border to add (from PatchRtfBorder)
   * @param borderWidth
   *          The border width to use
   * @param borderColor
   *          The border color to use
   */
  public PatchRtfBorderGroup( int bordersToAdd, int borderStyle, float borderWidth, Color borderColor ) {
    super( null );
    this.borders = new HashMap<Integer, PatchRtfBorder>();
    addBorder( bordersToAdd, borderStyle, borderWidth, borderColor );
  }

  /**
   * Constructs a PatchRtfBorderGroup based on another PatchRtfBorderGroup.
   *
   * @param doc
   *          The RtfDocument this PatchRtfBorderGroup belongs to
   * @param borderType
   *          The type of borders this PatchRtfBorderGroup contains
   * @param borderGroup
   *          The PatchRtfBorderGroup to use as a base
   */
  protected PatchRtfBorderGroup( RtfDocument doc, int borderType, PatchRtfBorderGroup borderGroup ) {
    super( doc );
    this.borders = new HashMap<Integer, PatchRtfBorder>();
    this.borderType = borderType;
    if ( borderGroup != null ) {
      for ( Map.Entry<Integer, PatchRtfBorder> entry : borderGroup.getBorders().entrySet() ) {
        this.borders.put( entry.getKey(), new PatchRtfBorder( this.document, this.borderType, entry.getValue() ) );
      }
    }
  }

  /**
   * Constructs a PatchRtfBorderGroup with certain borders
   *
   * @param doc
   *          The RtfDocument this PatchRtfBorderGroup belongs to
   * @param borderType
   *          The type of borders this PatchRtfBorderGroup contains
   * @param bordersToUse
   *          The borders to add (Rectangle.LEFT, Rectangle.RIGHT, Rectangle.TOP, Rectangle.BOTTOM, Rectangle.BOX)
   * @param borderWidth
   *          The border width to use
   * @param borderColor
   *          The border color to use
   */
  protected PatchRtfBorderGroup( RtfDocument doc, int borderType, int bordersToUse, float borderWidth, Color borderColor ) {
    super( doc );
    this.borderType = borderType;
    this.borders = new HashMap<Integer, PatchRtfBorder>();
    addBorder( bordersToUse, PatchRtfBorder.BORDER_SINGLE, borderWidth, borderColor );
  }

  /**
   * Sets a border in the Hashtable of borders
   *
   * @param borderPosition
   *          The position of this PatchRtfBorder
   * @param borderStyle
   *          The type of borders this PatchRtfBorderGroup contains
   * @param borderWidth
   *          The border width to use
   * @param borderColor
   *          The border color to use
   */
  private void setBorder( int borderPosition, int borderStyle, float borderWidth, Color borderColor ) {
    PatchRtfBorder border =
        new PatchRtfBorder( this.document, this.borderType, borderPosition, borderStyle, borderWidth, borderColor );
    this.borders.put( borderPosition, border );
  }

  /**
   * Adds borders to the PatchRtfBorderGroup
   *
   * @param bordersToAdd
   *          The borders to add (Rectangle.LEFT, Rectangle.RIGHT, Rectangle.TOP, Rectangle.BOTTOM, Rectangle.BOX)
   * @param borderStyle
   *          The style of border to add (from PatchRtfBorder)
   * @param borderWidth
   *          The border width to use
   * @param borderColor
   *          The border color to use
   */
  public void addBorder( int bordersToAdd, int borderStyle, float borderWidth, Color borderColor ) {
    if ( ( bordersToAdd & Rectangle.LEFT ) == Rectangle.LEFT ) {
      setBorder( PatchRtfBorder.LEFT_BORDER, borderStyle, borderWidth, borderColor );
    }
    if ( ( bordersToAdd & Rectangle.TOP ) == Rectangle.TOP ) {
      setBorder( PatchRtfBorder.TOP_BORDER, borderStyle, borderWidth, borderColor );
    }
    if ( ( bordersToAdd & Rectangle.RIGHT ) == Rectangle.RIGHT ) {
      setBorder( PatchRtfBorder.RIGHT_BORDER, borderStyle, borderWidth, borderColor );
    }
    if ( ( bordersToAdd & Rectangle.BOTTOM ) == Rectangle.BOTTOM ) {
      setBorder( PatchRtfBorder.BOTTOM_BORDER, borderStyle, borderWidth, borderColor );
    }
    if ( ( bordersToAdd & Rectangle.BOX ) == Rectangle.BOX && this.borderType == PatchRtfBorder.ROW_BORDER ) {
      setBorder( PatchRtfBorder.VERTICAL_BORDER, borderStyle, borderWidth, borderColor );
      setBorder( PatchRtfBorder.HORIZONTAL_BORDER, borderStyle, borderWidth, borderColor );
    }
  }

  /**
   * Removes borders from the list of borders
   *
   * @param bordersToRemove
   *          The borders to remove (from Rectangle)
   */
  public void removeBorder( int bordersToRemove ) {
    if ( ( bordersToRemove & Rectangle.LEFT ) == Rectangle.LEFT ) {
      this.borders.remove( new Integer( PatchRtfBorder.LEFT_BORDER ) );
    }
    if ( ( bordersToRemove & Rectangle.TOP ) == Rectangle.TOP ) {
      this.borders.remove( new Integer( PatchRtfBorder.TOP_BORDER ) );
    }
    if ( ( bordersToRemove & Rectangle.RIGHT ) == Rectangle.RIGHT ) {
      this.borders.remove( new Integer( PatchRtfBorder.RIGHT_BORDER ) );
    }
    if ( ( bordersToRemove & Rectangle.BOTTOM ) == Rectangle.BOTTOM ) {
      this.borders.remove( new Integer( PatchRtfBorder.BOTTOM_BORDER ) );
    }
    if ( ( bordersToRemove & Rectangle.BOX ) == Rectangle.BOX && this.borderType == PatchRtfBorder.ROW_BORDER ) {
      this.borders.remove( new Integer( PatchRtfBorder.VERTICAL_BORDER ) );
      this.borders.remove( new Integer( PatchRtfBorder.HORIZONTAL_BORDER ) );
    }
  }

  /**
   * Writes the borders of this PatchRtfBorderGroup
   */
  public void writeContent( final OutputStream result ) throws IOException {
    Iterator<PatchRtfBorder> it = this.borders.values().iterator();
    while ( it.hasNext() ) {
      it.next().writeContent( result );
    }
  }

  /**
   * Gets the RtfBorders of this PatchRtfBorderGroup
   *
   * @return The RtfBorders of this PatchRtfBorderGroup
   */
  protected HashMap<Integer, PatchRtfBorder> getBorders() {
    return this.borders;
  }
}
