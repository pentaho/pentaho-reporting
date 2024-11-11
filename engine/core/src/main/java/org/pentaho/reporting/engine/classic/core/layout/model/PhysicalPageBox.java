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


package org.pentaho.reporting.engine.classic.core.layout.model;

import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;

import java.awt.print.PageFormat;

/**
 * Defines the properties of a single physical page. In a later version, this box may receive physical page header and
 * footer or may even support the full CSS-pagebox model.
 */
public final class PhysicalPageBox implements Cloneable {
  private long width;
  private long height;
  private long imageableX;
  private long imageableY;
  private long imageableWidth;
  private long imageableHeight;
  private long globalX;
  private long globalY;
  private int orientation;

  public PhysicalPageBox( final PageFormat pageFormat, final long globalX, final long globalY ) {
    this.width = StrictGeomUtility.toInternalValue( pageFormat.getWidth() );
    this.height = StrictGeomUtility.toInternalValue( pageFormat.getHeight() );
    this.imageableX = StrictGeomUtility.toInternalValue( pageFormat.getImageableX() );
    this.imageableY = StrictGeomUtility.toInternalValue( pageFormat.getImageableY() );
    this.imageableWidth = StrictGeomUtility.toInternalValue( pageFormat.getImageableWidth() );
    this.imageableHeight = StrictGeomUtility.toInternalValue( pageFormat.getImageableHeight() );
    this.globalX = globalX;
    this.globalY = globalY;
    this.orientation = pageFormat.getOrientation();
  }

  public int getOrientation() {
    return orientation;
  }

  public long getImageableX() {
    return imageableX;
  }

  public long getImageableY() {
    return imageableY;
  }

  public long getImageableWidth() {
    return imageableWidth;
  }

  public long getImageableHeight() {
    return imageableHeight;
  }

  public long getGlobalX() {
    return globalX;
  }

  public long getGlobalY() {
    return globalY;
  }

  public long getWidth() {
    return width;
  }

  public long getHeight() {
    return height;
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }
}
