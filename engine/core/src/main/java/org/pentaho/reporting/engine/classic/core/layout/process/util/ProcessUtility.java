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

package org.pentaho.reporting.engine.classic.core.layout.process.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import org.pentaho.reporting.engine.classic.core.layout.model.BorderEdge;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.PageAreaBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderLength;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableReplacedContent;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableReplacedContentBox;
import org.pentaho.reporting.engine.classic.core.layout.model.context.BoxDefinition;
import org.pentaho.reporting.engine.classic.core.layout.process.ComputeStaticPropertiesProcessStep;
import org.pentaho.reporting.engine.classic.core.style.BorderStyle;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.ShapeDrawable;
import org.pentaho.reporting.engine.classic.core.util.StrokeUtility;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.resourceloader.factory.drawable.DrawableWrapper;

/**
 * Creation-Date: 16.07.2007, 13:42:43
 *
 * @author Thomas Morgner
 */
public class ProcessUtility {
  private ProcessUtility() {
  }

  /**
   * Returns the computed block-context width. This width is a content-size width - so it excludes paddings and borders.
   * (See CSS3-BOX 4.2; http://www.w3.org/TR/css3-box/#containing)
   *
   * @param box
   *          the box for which the block-context width should be computed.
   * @return the block context width.
   */
  public static long computeBlockContextWidth( final RenderNode box ) {
    final RenderBox parentBlockContext = box.getParent();
    if ( parentBlockContext == null ) {
      // page cannot have borders ...
      final int type = box.getNodeType();
      if ( ( type & LayoutNodeTypes.MASK_BOX_PAGEAREA ) == LayoutNodeTypes.MASK_BOX_PAGEAREA ) {
        final PageAreaBox pageAreaBox = (PageAreaBox) box;
        final LogicalPageBox pageBox = pageAreaBox.getLogicalPage();
        if ( pageBox == null ) {
          return 0;
        }
        return pageBox.getPageWidth();
      }
      if ( ( type & LayoutNodeTypes.TYPE_BOX_LOGICALPAGE ) == LayoutNodeTypes.TYPE_BOX_LOGICALPAGE ) {
        final LogicalPageBox logicalPage = (LogicalPageBox) box;
        return logicalPage.getPageWidth();
      }
      return 0;
    }

    // A row or inline-box never establishes a context on its own; it always transfers the parent's block-render
    // context.
    // The parent's computed width is used as block context ..
    return parentBlockContext.getCachedWidth();
  }

  public static boolean isContent( final RenderBox element, final boolean ellipseAsBackground,
      final boolean shapesAsContent ) {

    // For legacy reasons: A single ReplacedContent in a paragraph means, we may have a old-style border and
    // background definition.
    if ( element.getNodeType() == LayoutNodeTypes.TYPE_BOX_CONTENT ) {
      final RenderableReplacedContentBox contentBox = (RenderableReplacedContentBox) element;
      final RenderableReplacedContent rpc = contentBox.getContent();
      final Object rawContentObject = rpc.getRawObject();
      if ( rawContentObject instanceof DrawableWrapper == false ) {
        return true;
      }
      final DrawableWrapper wrapper = (DrawableWrapper) rawContentObject;
      final Object rawbackend = wrapper.getBackend();
      if ( rawbackend instanceof ShapeDrawable == false ) {
        return true;
      }
      final ShapeDrawable drawable = (ShapeDrawable) rawbackend;
      final Shape rawObject = drawable.getShape();
      final StyleSheet styleSheet = element.getStyleSheet();
      if ( shapesAsContent == false ) {
        return false;
      }

      if ( rawObject instanceof Line2D ) {
        if ( hasBorderEdge( styleSheet ) ) {
          final Line2D line = (Line2D) rawObject;
          if ( line.getY1() == line.getY2() ) {
            return false;
          } else if ( line.getX1() == line.getX2() ) {
            return false;
          }
        }
      } else if ( rawObject instanceof Rectangle2D ) {
        return false;
      } else if ( ellipseAsBackground && rawObject instanceof Ellipse2D ) {
        return false;
      } else if ( rawObject instanceof RoundRectangle2D ) {
        return false;
      }
      return true;
    }

    RenderNode child = element.getFirstChild();
    while ( child != null ) {
      final int type = child.getNodeType();
      if ( ( type & LayoutNodeTypes.MASK_BOX_INLINE ) == LayoutNodeTypes.MASK_BOX_INLINE ) {
        return true;
      }
      if ( type == LayoutNodeTypes.TYPE_NODE_TEXT ) {
        return true;
      }
      child = child.getNext();
    }
    return false;
  }

  public static boolean hasBorderEdge( final StyleSheet style ) {
    final Stroke s = (Stroke) style.getStyleProperty( ElementStyleKeys.STROKE );
    if ( s instanceof BasicStroke == false ) {
      return false;
    }
    final BorderStyle borderStyle = StrokeUtility.translateStrokeStyle( s );
    if ( BorderStyle.NONE.equals( borderStyle ) ) {
      return false;
    }
    return true;
  }

  public static BorderEdge produceBorderEdge( final StyleSheet style ) {
    final Stroke s = (Stroke) style.getStyleProperty( ElementStyleKeys.STROKE );
    if ( s instanceof BasicStroke == false ) {
      return null;
    }
    final BasicStroke bs = (BasicStroke) s;
    final BorderStyle borderStyle = StrokeUtility.translateStrokeStyle( s );
    if ( BorderStyle.NONE.equals( borderStyle ) ) {
      return null;
    }

    final Color c = (Color) style.getStyleProperty( ElementStyleKeys.PAINT );
    return new BorderEdge( borderStyle, c, StrictGeomUtility.toInternalValue( bs.getLineWidth() ) );
  }

  public static long computeLength( final long min, final long max, final long pref ) {
    if ( pref > max ) {
      if ( max < min ) {
        return min;
      }
      return max;
    }

    if ( pref < min ) {
      if ( max < min ) {
        return max;
      }
      return min;
    }

    if ( max < pref ) {
      return max;
    }
    return pref;
  }

  public static long resolveComputedWidth( final RenderBox box ) {
    final long bcw = ProcessUtility.computeBlockContextWidth( box );
    final BoxDefinition boxDef = box.getBoxDefinition();
    final RenderLength minLength = boxDef.getMinimumWidth();
    final RenderLength prefLength = boxDef.getPreferredWidth();
    final RenderLength maxLength = boxDef.getMaximumWidth();

    final long min = minLength.resolve( bcw, 0 );
    final long pref = prefLength.resolve( bcw, 0 );
    final long max = maxLength.resolve( bcw, ComputeStaticPropertiesProcessStep.MAX_AUTO );
    return ProcessUtility.computeLength( min, max, pref );
  }

}
