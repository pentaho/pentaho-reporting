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

package org.pentaho.reporting.designer.core.model;

import org.pentaho.reporting.engine.classic.core.util.geom.StrictBounds;

import java.util.ArrayList;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class CachedLayoutData {
  private static final StrictBounds[] EMPTY_BOUNDS = new StrictBounds[ 0 ];

  private boolean conflictsInTableMode;
  private int elementType;
  private long layoutAge;
  private long x;
  private long y;
  private long width;
  private long height;
  private long paddingX;
  private long paddingY;

  private ArrayList<StrictBounds> additionalBounds;

  protected CachedLayoutData() {
    layoutAge = -1;
  }

  public boolean isConflictsInTableMode() {
    return conflictsInTableMode;
  }

  public void setConflictsInTableMode( final boolean conflictsInTableMode ) {
    this.conflictsInTableMode = conflictsInTableMode;
  }

  public int getElementType() {
    return elementType;
  }

  public void setElementType( final int elementType ) {
    this.elementType = elementType;
  }

  public long getLayoutAge() {
    return layoutAge;
  }

  public void setLayoutAge( final long layoutAge ) {
    this.layoutAge = layoutAge;
  }

  public long getX() {
    return x;
  }

  public void setX( final long x ) {
    this.x = x;
  }

  public long getY() {
    return y;
  }

  public void setY( final long y ) {
    this.y = y;
  }

  public long getWidth() {
    return width;
  }

  public void setWidth( final long width ) {
    this.width = width;
  }

  public long getHeight() {
    return height;
  }

  public void setHeight( final long height ) {
    this.height = height;
  }

  public long getPaddingX() {
    return paddingX;
  }

  public void setPaddingX( final long paddingX ) {
    this.paddingX = paddingX;
  }

  public long getPaddingY() {
    return paddingY;
  }

  public void setPaddingY( final long paddingY ) {
    this.paddingY = paddingY;
  }

  public void clearAdditionalBounds() {
    if ( additionalBounds == null ) {
      return;
    }
    additionalBounds.clear();
  }

  public StrictBounds[] getAdditionalBounds() {
    if ( additionalBounds == null ) {
      return EMPTY_BOUNDS;
    }
    return additionalBounds.toArray( new StrictBounds[ additionalBounds.size() ] );
  }

  public void addAdditionalBounds( final StrictBounds bounds ) {
    if ( bounds == null ) {
      throw new NullPointerException();
    }
    if ( additionalBounds == null ) {
      additionalBounds = new ArrayList<StrictBounds>();
    }
    additionalBounds.add( bounds );
  }

}
