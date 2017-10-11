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
