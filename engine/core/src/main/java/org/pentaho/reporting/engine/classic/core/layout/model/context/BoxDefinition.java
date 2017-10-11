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

package org.pentaho.reporting.engine.classic.core.layout.model.context;

import org.pentaho.reporting.engine.classic.core.layout.model.Border;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderLength;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;

/**
 * A box definition. The paddings or maximum height/width cannot be percentages or AUTO.
 *
 * @author Thomas Morgner
 */
public final class BoxDefinition {
  public static final BoxDefinition EMPTY = new BoxDefinition().lock();
  public static final RenderLength DEFAULT_PREFERRED_WIDTH = RenderLength.createFromRaw( -100 );

  private Boolean empty;
  private long paddingTop;
  private long paddingLeft;
  private long paddingBottom;
  private long paddingRight;
  private Border border;
  private RenderLength preferredHeight;
  private RenderLength preferredWidth;
  private RenderLength minimumHeight;
  private RenderLength minimumWidth;
  private RenderLength marginTop;
  private RenderLength marginBottom;
  private RenderLength marginLeft;
  private RenderLength marginRight;
  private RenderLength maximumHeight;
  private RenderLength maximumWidth;
  private RenderLength fixedPosition;
  private boolean locked;
  private boolean sizeSpecifiesBorderBox;

  public BoxDefinition() {
    border = Border.EMPTY_BORDER;
    preferredWidth = RenderLength.AUTO;
    preferredHeight = RenderLength.AUTO;
    minimumHeight = RenderLength.EMPTY;
    minimumWidth = RenderLength.EMPTY;
    marginTop = RenderLength.EMPTY;
    marginLeft = RenderLength.EMPTY;
    marginBottom = RenderLength.EMPTY;
    marginRight = RenderLength.EMPTY;
    maximumWidth = RenderLength.AUTO;
    maximumHeight = RenderLength.AUTO;
    fixedPosition = RenderLength.AUTO;
    sizeSpecifiesBorderBox = true;
  }

  public void setSizeSpecifiesBorderBox( final boolean sizeSpecifiesBorderBox ) {
    this.sizeSpecifiesBorderBox = sizeSpecifiesBorderBox;
  }

  public boolean isSizeSpecifiesBorderBox() {
    return sizeSpecifiesBorderBox;
  }

  public boolean isLocked() {
    return locked;
  }

  public BoxDefinition lock() {
    locked = true;
    return this;
  }

  public BoxDefinition derive() {
    final BoxDefinition retval = new BoxDefinition();
    retval.border = border;
    retval.preferredWidth = preferredWidth;
    retval.preferredHeight = preferredHeight;
    retval.minimumHeight = minimumHeight;
    retval.minimumWidth = minimumWidth;
    retval.marginTop = marginTop;
    retval.marginLeft = marginLeft;
    retval.marginBottom = marginBottom;
    retval.marginRight = marginRight;
    retval.maximumWidth = maximumWidth;
    retval.maximumHeight = maximumHeight;
    retval.fixedPosition = fixedPosition;
    retval.locked = locked;
    retval.empty = empty;
    return retval;
  }

  public RenderLength getFixedPosition() {
    return fixedPosition;
  }

  public void setFixedPosition( final RenderLength fixedPosition ) {
    if ( locked ) {
      throw new IllegalStateException();
    }

    if ( fixedPosition == null ) {
      throw new NullPointerException();
    }

    this.fixedPosition = fixedPosition;
    this.empty = null;
  }

  public Border getBorder() {
    return border;
  }

  public void setBorder( final Border border ) {
    if ( locked ) {
      throw new IllegalStateException();
    }

    if ( border == null ) {
      throw new NullPointerException();
    }

    this.border = border;
    this.empty = null;
  }

  public long getPaddingTop() {
    return paddingTop;
  }

  public void setPaddingTop( final long paddingTop ) {
    if ( locked ) {
      throw new IllegalStateException();
    }

    this.paddingTop = paddingTop;
    this.empty = null;
  }

  public long getPaddingLeft() {
    return paddingLeft;
  }

  public void setPaddingLeft( final long paddingLeft ) {
    if ( locked ) {
      throw new IllegalStateException();
    }

    this.paddingLeft = paddingLeft;
    this.empty = null;
  }

  public long getPaddingBottom() {
    return paddingBottom;
  }

  public void setPaddingBottom( final long paddingBottom ) {
    if ( locked ) {
      throw new IllegalStateException();
    }

    this.paddingBottom = paddingBottom;
    this.empty = null;
  }

  public long getPaddingRight() {
    return paddingRight;
  }

  public void setPaddingRight( final long paddingRight ) {
    if ( locked ) {
      throw new IllegalStateException();
    }
    this.paddingRight = paddingRight;
    this.empty = null;
  }

  public RenderLength getPreferredHeight() {
    return preferredHeight;
  }

  public void setPreferredHeight( final RenderLength preferredHeight ) {
    if ( locked ) {
      throw new IllegalStateException();
    }
    if ( preferredHeight == null ) {
      throw new NullPointerException();
    }

    this.preferredHeight = preferredHeight;
  }

  public RenderLength getPreferredWidth() {
    return preferredWidth;
  }

  public void setPreferredWidth( final RenderLength preferredWidth ) {
    if ( locked ) {
      throw new IllegalStateException();
    }
    if ( preferredWidth == null ) {
      throw new NullPointerException();
    }

    this.preferredWidth = preferredWidth;
  }

  public RenderLength getMinimumHeight() {
    return minimumHeight;
  }

  public void setMinimumHeight( final RenderLength minimumHeight ) {
    if ( locked ) {
      throw new IllegalStateException();
    }
    if ( minimumHeight == null ) {
      throw new NullPointerException();
    }
    this.minimumHeight = minimumHeight;
  }

  public RenderLength getMinimumWidth() {
    return minimumWidth;
  }

  public void setMinimumWidth( final RenderLength minimumWidth ) {
    if ( locked ) {
      throw new IllegalStateException();
    }
    if ( minimumWidth == null ) {
      throw new NullPointerException();
    }
    this.minimumWidth = minimumWidth;
  }

  public RenderLength getMaximumHeight() {
    return maximumHeight;
  }

  public void setMaximumHeight( final RenderLength maximumHeight ) {
    if ( locked ) {
      throw new IllegalStateException();
    }
    if ( maximumHeight == null ) {
      throw new NullPointerException();
    }

    this.maximumHeight = maximumHeight;
  }

  public RenderLength getMaximumWidth() {
    return maximumWidth;
  }

  public void setMaximumWidth( final RenderLength maximumWidth ) {
    if ( locked ) {
      throw new IllegalStateException();
    }
    if ( maximumWidth == null ) {
      throw new NullPointerException();
    }

    this.maximumWidth = maximumWidth;
  }

  public RenderLength getMarginTop() {
    return marginTop;
  }

  public void setMarginTop( final RenderLength marginTop ) {
    if ( locked ) {
      throw new IllegalStateException();
    }
    if ( marginTop == null ) {
      throw new NullPointerException();
    }

    this.marginTop = marginTop;
    this.empty = null;
  }

  public RenderLength getMarginBottom() {
    return marginBottom;
  }

  public void setMarginBottom( final RenderLength marginBottom ) {
    if ( locked ) {
      throw new IllegalStateException();
    }
    if ( marginBottom == null ) {
      throw new NullPointerException();
    }

    this.marginBottom = marginBottom;
    this.empty = null;
  }

  public RenderLength getMarginLeft() {
    return marginLeft;
  }

  public void setMarginLeft( final RenderLength marginLeft ) {
    if ( locked ) {
      throw new IllegalStateException();
    }
    if ( marginLeft == null ) {
      throw new NullPointerException();
    }
    this.marginLeft = marginLeft;
    this.empty = null;
  }

  public RenderLength getMarginRight() {
    return marginRight;
  }

  public void setMarginRight( final RenderLength marginRight ) {
    if ( locked ) {
      throw new IllegalStateException();
    }
    if ( marginRight == null ) {
      throw new NullPointerException();
    }
    this.marginRight = marginRight;
    this.empty = null;
  }

  public boolean isEmpty() {
    if ( empty != null ) {
      return empty.booleanValue();
    }

    if ( paddingTop != 0 ) {
      empty = Boolean.FALSE;
      return false;
    }
    if ( paddingLeft != 0 ) {
      empty = Boolean.FALSE;
      return false;
    }
    if ( paddingBottom != 0 ) {
      empty = Boolean.FALSE;
      return false;
    }
    if ( paddingRight != 0 ) {
      empty = Boolean.FALSE;
      return false;
    }

    if ( fixedPosition != null && RenderLength.AUTO.equals( fixedPosition ) == false ) {
      empty = Boolean.FALSE;
      return false;
    }

    if ( border.isEmpty() == false ) {
      empty = Boolean.FALSE;
      return false;
    }

    empty = Boolean.TRUE;
    return true;
  }

  /**
   * Split the box definition for the given major axis. A horizontal axis will perform vertical splits (resulting in a
   * left and right box definition) and a given vertical axis will split the box into a top and bottom box.
   *
   * @param axis
   *          the axis on which to split the box.
   * @return the two new box definitions, never null.
   */
  public BoxDefinition[] split( final int axis ) {
    if ( axis == RenderNode.HORIZONTAL_AXIS ) {
      return splitVertically();
    }
    return splitHorizontally();
  }

  private BoxDefinition[] splitVertically() {
    final Border[] borders = border.splitVertically( null );
    final BoxDefinition first = new BoxDefinition();
    first.marginTop = marginTop;
    first.marginLeft = marginLeft;
    first.marginBottom = marginBottom;
    first.marginRight = RenderLength.EMPTY;
    first.paddingBottom = paddingBottom;
    first.paddingTop = paddingTop;
    first.paddingLeft = paddingLeft;
    first.paddingRight = 0;
    first.border = borders[0];
    first.preferredHeight = preferredHeight;
    first.preferredWidth = preferredWidth;
    first.minimumHeight = minimumHeight;
    first.minimumWidth = minimumWidth;
    first.maximumHeight = maximumHeight;
    first.maximumWidth = maximumWidth;
    first.fixedPosition = fixedPosition;

    final BoxDefinition second = new BoxDefinition();
    second.marginTop = marginTop;
    second.marginLeft = RenderLength.EMPTY;
    second.marginBottom = marginBottom;
    second.marginRight = marginRight;
    second.paddingBottom = paddingBottom;
    second.paddingTop = paddingTop;
    second.paddingLeft = 0;
    second.paddingRight = paddingRight;
    second.border = borders[1];
    second.preferredHeight = preferredHeight;
    second.preferredWidth = preferredWidth;
    second.minimumHeight = minimumHeight;
    second.minimumWidth = minimumWidth;
    second.maximumHeight = maximumHeight;
    second.maximumWidth = maximumWidth;
    second.fixedPosition = RenderLength.AUTO;

    final BoxDefinition[] boxes = new BoxDefinition[2];
    boxes[0] = first;
    boxes[1] = second;
    return boxes;
  }

  private BoxDefinition[] splitHorizontally() {
    final Border[] borders = border.splitHorizontally( null );

    final BoxDefinition first = new BoxDefinition();
    first.marginTop = marginTop;
    first.marginLeft = marginLeft;
    first.marginBottom = RenderLength.EMPTY;
    first.marginRight = marginRight;
    first.paddingBottom = 0;
    first.paddingTop = paddingTop;
    first.paddingLeft = paddingLeft;
    first.paddingRight = paddingRight;
    first.border = borders[0];
    first.preferredHeight = preferredHeight;
    first.preferredWidth = preferredWidth;
    first.minimumHeight = minimumHeight;
    first.minimumWidth = minimumWidth;
    first.maximumHeight = maximumHeight;
    first.maximumWidth = maximumWidth;
    first.fixedPosition = fixedPosition;

    final BoxDefinition second = new BoxDefinition();
    second.marginTop = RenderLength.EMPTY;
    second.marginLeft = marginLeft;
    second.marginBottom = marginBottom;
    second.marginRight = marginRight;
    second.paddingBottom = paddingBottom;
    second.paddingTop = 0;
    second.paddingLeft = paddingLeft;
    second.paddingRight = paddingRight;
    second.border = borders[1];
    second.preferredHeight = preferredHeight;
    second.preferredWidth = preferredWidth;
    second.minimumHeight = minimumHeight;
    second.minimumWidth = minimumWidth;
    second.maximumHeight = maximumHeight;
    second.maximumWidth = maximumWidth;
    second.fixedPosition = fixedPosition;

    final BoxDefinition[] boxes = new BoxDefinition[2];
    boxes[0] = first;
    boxes[1] = second;
    return boxes;
  }

  public String toString() {
    return "BoxDefinition{" + "minimumHeight=" + minimumHeight + ", minimumWidth=" + minimumWidth
        + ", preferredHeight=" + preferredHeight + ", preferredWidth=" + preferredWidth + ", maximumHeight="
        + maximumHeight + ", maximumWidth=" + maximumWidth + ", marginTop=" + marginTop + ", marginBottom="
        + marginBottom + ", marginLeft=" + marginLeft + ", marginRight=" + marginRight + ", paddingTop=" + paddingTop
        + ", paddingLeft=" + paddingLeft + ", paddingBottom=" + paddingBottom + ", paddingRight=" + paddingRight
        + ", border=" + border + '}';
  }
}
