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

package org.pentaho.reporting.engine.classic.core.modules.output.table.base;

import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.layout.model.BorderCorner;
import org.pentaho.reporting.engine.classic.core.layout.model.BorderEdge;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.libraries.xmlns.common.AttributeMap;

import java.awt.Color;
import java.util.ArrayList;

public class CellBackground {
  private ReportAttributeMap<Object> attributes;
  private ArrayList<Color> collectedColors;
  private Color backgroundColor;
  private ArrayList<String> anchors;

  private BorderEdge top;
  private BorderEdge left;
  private BorderEdge bottom;
  private BorderEdge right;

  private BorderCorner topLeft;
  private BorderCorner topRight;
  private BorderCorner bottomLeft;
  private BorderCorner bottomRight;
  private boolean origin;
  private ElementType elementType;
  private transient Integer hashCode;
  private static final String[] EMPTY_ANCHORS = new String[ 0 ];

  public CellBackground() {
    this.top = BorderEdge.EMPTY;
    this.left = BorderEdge.EMPTY;
    this.bottom = BorderEdge.EMPTY;
    this.right = BorderEdge.EMPTY;
    this.attributes = new ReportAttributeMap<Object>();

    this.topLeft = new BorderCorner( 0, 0 );
    this.topRight = new BorderCorner( 0, 0 );
    this.bottomLeft = new BorderCorner( 0, 0 );
    this.bottomRight = new BorderCorner( 0, 0 );
  }

  public boolean isOrigin() {
    return origin;
  }

  public void setOrigin( final boolean origin ) {
    this.origin = origin;
    this.hashCode = null;
  }

  public void addAttributes( final ReportAttributeMap<Object> attrs ) {
    if ( attrs == null ) {
      throw new NullPointerException();
    }

    for ( final AttributeMap.DualKey key : attrs.keySet() ) {
      final String namespace = key.namespace;
      final String name = key.name;
      final Object value = attrs.getAttribute( namespace, name );
      if ( value != null ) {
        this.attributes.setAttribute( namespace, name, value );
      }
    }
    this.hashCode = null;
  }

  public ReportAttributeMap<Object> getAttributes() {
    return attributes;
  }

  public void addBackground( final Color color ) {
    if ( color == null ) {
      return;
    }
    if ( color.getAlpha() == 0 ) {
      // fully transparent ..
      return;
    }

    // common special case: Only one background color defined ..
    if ( backgroundColor == null ) {
      backgroundColor = color;
      this.hashCode = null;
      return;
    }

    if ( color.getAlpha() == 255 ) {
      // fully opaque, so
      backgroundColor = color;
      if ( collectedColors != null ) {
        collectedColors.clear();
      }
      this.hashCode = null;
      return;
    }

    if ( collectedColors == null ) {
      collectedColors = new ArrayList<Color>();
    }
    if ( collectedColors.isEmpty() ) {
      collectedColors.add( backgroundColor );
    }

    collectedColors.add( color );
    this.hashCode = null;
  }

  public Color getBackgroundColor() {
    if ( backgroundColor != null && backgroundColor.getAlpha() == 255 ) {
      return backgroundColor;
    }

    if ( collectedColors == null ) {
      return backgroundColor;
    }
    if ( collectedColors.isEmpty() == false ) {
      Color retval = null;
      final int colorCount = collectedColors.size();
      for ( int i = 0; i < colorCount; i++ ) {
        final Color c = collectedColors.get( i );
        if ( retval == null ) {
          retval = c;
        } else {
          retval = addColor( retval, c );
        }
      }
      return retval;
    }
    return backgroundColor;
  }

  /**
   * Adds two colors, the result is the mixed color of the base color and the paint color.
   *
   * @param base  the base color
   * @param paint the overlay color
   * @return the merged colors.
   */
  private static Color addColor( final Color base, final Color paint ) {
    if ( paint.getAlpha() == 255 ) {
      return paint;
    }
    if ( paint.getAlpha() == 0 ) {
      return base;
    }

    final double baseAlpha = ( base.getAlpha() / 255.0 );
    final double paintAlpha = ( paint.getAlpha() / 255.0 );
    final double effectiveAlpha = 1.0 - baseAlpha * paintAlpha;

    final double deltaAlpha = 1.0 - effectiveAlpha;
    final int red = (int) ( base.getRed() * deltaAlpha + paint.getRed() * effectiveAlpha );
    final int green = (int) ( base.getGreen() * deltaAlpha + paint.getGreen() * effectiveAlpha );
    final int blue = (int) ( base.getBlue() * deltaAlpha + paint.getBlue() * effectiveAlpha );
    return new Color( red, green, blue, (int) ( effectiveAlpha * 255.0 ) );
  }

  public BorderEdge getTop() {
    return top;
  }

  public void setTop( final BorderEdge top ) {
    if ( top == null ) {
      throw new NullPointerException();
    }
    this.top = top;
    this.hashCode = null;
  }

  public BorderEdge getLeft() {
    return left;
  }

  public void setLeft( final BorderEdge left ) {
    if ( left == null ) {
      throw new NullPointerException();
    }
    this.left = left;
    this.hashCode = null;
  }

  public BorderEdge getBottom() {
    return bottom;
  }

  public void setBottom( final BorderEdge edge ) {
    if ( edge == null ) {
      throw new NullPointerException();
    }
    this.bottom = edge;
    this.hashCode = null;
  }

  public BorderEdge getRight() {
    return right;
  }

  public void setRight( final BorderEdge edge ) {
    if ( edge == null ) {
      throw new NullPointerException();
    }
    this.right = edge;
    this.hashCode = null;
  }

  public void setTopLeft( final BorderCorner topLeft ) {
    if ( topLeft == null ) {
      throw new NullPointerException();
    }
    this.topLeft = topLeft;
    this.hashCode = null;
  }

  public void setTopRight( final BorderCorner topRight ) {
    if ( topRight == null ) {
      throw new NullPointerException();
    }
    this.topRight = topRight;
    this.hashCode = null;
  }

  public void setBottomLeft( final BorderCorner bottomLeft ) {
    if ( bottomLeft == null ) {
      throw new NullPointerException();
    }
    this.bottomLeft = bottomLeft;
    this.hashCode = null;
  }

  public void setBottomRight( final BorderCorner bottomRight ) {
    if ( bottomRight == null ) {
      throw new NullPointerException();
    }
    this.bottomRight = bottomRight;
    this.hashCode = null;
  }

  public BorderCorner getTopLeft() {
    return topLeft;
  }

  public BorderCorner getTopRight() {
    return topRight;
  }

  public BorderCorner getBottomLeft() {
    return bottomLeft;
  }

  public BorderCorner getBottomRight() {
    return bottomRight;
  }

  public void addAnchor( final String anchor ) {
    if ( anchor == null ) {
      return;
    }
    if ( anchors == null ) {
      anchors = new ArrayList<String>();
    }
    anchors.add( anchor );
    this.hashCode = null;
  }

  public String[] getAnchors() {
    if ( anchors == null ) {
      return EMPTY_ANCHORS;
    }
    return anchors.toArray( new String[ anchors.size() ] );
  }

  public void addElementType( final ElementType type ) {
    if ( type == null ) {
      throw new NullPointerException();
    }
    this.elementType = type;
    this.hashCode = null;
  }

  public ElementType getElementType() {
    return elementType;
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final CellBackground that = (CellBackground) o;

    if ( origin != that.origin ) {
      return false;
    }
    if ( anchors != null ? !anchors.equals( that.anchors ) : that.anchors != null ) {
      return false;
    }
    if ( attributes != null ? !attributes.equals( that.attributes ) : that.attributes != null ) {
      return false;
    }
    if ( backgroundColor != null ? !backgroundColor.equals( that.backgroundColor ) : that.backgroundColor != null ) {
      return false;
    }
    if ( bottom != null ? !bottom.equals( that.bottom ) : that.bottom != null ) {
      return false;
    }
    if ( bottomLeft != null ? !bottomLeft.equals( that.bottomLeft ) : that.bottomLeft != null ) {
      return false;
    }
    if ( bottomRight != null ? !bottomRight.equals( that.bottomRight ) : that.bottomRight != null ) {
      return false;
    }
    if ( collectedColors != null ? !collectedColors.equals( that.collectedColors ) : that.collectedColors != null ) {
      return false;
    }
    if ( elementType != null ? !elementType.equals( that.elementType ) : that.elementType != null ) {
      return false;
    }
    if ( left != null ? !left.equals( that.left ) : that.left != null ) {
      return false;
    }
    if ( right != null ? !right.equals( that.right ) : that.right != null ) {
      return false;
    }
    if ( top != null ? !top.equals( that.top ) : that.top != null ) {
      return false;
    }
    if ( topLeft != null ? !topLeft.equals( that.topLeft ) : that.topLeft != null ) {
      return false;
    }
    if ( topRight != null ? !topRight.equals( that.topRight ) : that.topRight != null ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    if ( hashCode == null ) {
      int result = attributes != null ? attributes.hashCode() : 0;
      result = 31 * result + ( collectedColors != null ? collectedColors.hashCode() : 0 );
      result = 31 * result + ( backgroundColor != null ? backgroundColor.hashCode() : 0 );
      result = 31 * result + ( anchors != null ? anchors.hashCode() : 0 );
      result = 31 * result + ( top != null ? top.hashCode() : 0 );
      result = 31 * result + ( left != null ? left.hashCode() : 0 );
      result = 31 * result + ( bottom != null ? bottom.hashCode() : 0 );
      result = 31 * result + ( right != null ? right.hashCode() : 0 );
      result = 31 * result + ( topLeft != null ? topLeft.hashCode() : 0 );
      result = 31 * result + ( topRight != null ? topRight.hashCode() : 0 );
      result = 31 * result + ( bottomLeft != null ? bottomLeft.hashCode() : 0 );
      result = 31 * result + ( bottomRight != null ? bottomRight.hashCode() : 0 );
      result = 31 * result + ( origin ? 1 : 0 );
      result = 31 * result + ( elementType != null ? elementType.hashCode() : 0 );
      hashCode = result;
    }
    return hashCode;
  }
}
