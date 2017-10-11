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

import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.InvalidReportStateException;
import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.filter.types.AutoLayoutBoxType;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.style.SimpleStyleSheet;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.ResolverStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.VerticalTextAlign;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

import java.io.Serializable;

/**
 * A static properties collection. That one is static; once computed it does not change anymore. It does not (under no
 * thinkable circumstances) depend on the given content. It may depend on static content of the parent.
 *
 * @author Thomas Morgner
 */
public final class NodeLayoutProperties implements Serializable, Cloneable {
  private static final Float ZERO = new Float( 0 );
  public static final InstanceID SIMPLE_NODE_ID = new InstanceID();
  public static final NodeLayoutProperties GENERIC_PROPERTIES = new NodeLayoutProperties();
  // ComputedMetrics:

  // Fully static properties ...
  private VerticalTextAlign verticalTextAlign;
  private ElementAlignment verticalAlignment;
  private int majorAxis;
  private int minorAxis;
  private StyleSheet styleSheet;

  /**
   * The instance id identifies the original report-element that was used to create this render-element. There is no
   * guarantee of uniqueness in this id, as the same element can be printed multiple times.
   */
  private InstanceID instanceId;
  private Float posY;
  private Float posX;
  private ReportAttributeMap<Object> attributes;
  private ElementType elementType;
  private boolean visible;

  private NodeLayoutProperties() {
    this( SimpleStyleSheet.EMPTY_STYLE, ReportAttributeMap.emptyMap(), NodeLayoutProperties.SIMPLE_NODE_ID,
        AutoLayoutBoxType.INSTANCE );
  }

  public NodeLayoutProperties( final StyleSheet styleSheet, final ReportAttributeMap<Object> attributes,
      final InstanceID instanceID, final ElementType elementType ) {
    this( RenderNode.VERTICAL_AXIS, RenderNode.HORIZONTAL_AXIS, styleSheet, attributes, instanceID, elementType );
  }

  public NodeLayoutProperties( final int majorAxis, final int minorAxis, final StyleSheet styleSheet,
      final ReportAttributeMap<Object> attributes, final InstanceID instanceID, final ElementType elementType ) {
    if ( instanceID == null ) {
      throw new NullPointerException();
    }
    if ( styleSheet == null ) {
      throw new NullPointerException();
    }
    if ( attributes == null ) {
      throw new NullPointerException();
    }

    if ( styleSheet instanceof ResolverStyleSheet ) {
      throw new IllegalStateException();
    }

    if ( styleSheet instanceof ElementStyleSheet ) {
      throw new InvalidReportStateException( "Cannot have render-nodes with mutable style" );
    }
    if ( styleSheet.getStyleProperty( TextStyleKeys.FONTSIZE ) == null ) {
      throw new InvalidReportStateException( "Every resolved style must have a non-null font-size" );
    }
    if ( styleSheet.getStyleProperty( ElementStyleKeys.STROKE ) == null ) {
      throw new InvalidReportStateException( "Every resolved style must have a non-null stroke" );
    }

    this.majorAxis = majorAxis;
    this.minorAxis = minorAxis;
    this.instanceId = instanceID;
    this.styleSheet = styleSheet;
    this.attributes = attributes;
    this.elementType = elementType;

    final Object vTextAlign = styleSheet.getStyleProperty( TextStyleKeys.VERTICAL_TEXT_ALIGNMENT );
    if ( vTextAlign != null ) {
      verticalTextAlign = (VerticalTextAlign) vTextAlign;
    } else {
      verticalTextAlign = VerticalTextAlign.BASELINE;
    }
    this.visible = true;
  }

  public void setVisible( final boolean visible ) {
    this.visible = visible;
  }

  public boolean isVisible() {
    return visible;
  }

  public VerticalTextAlign getVerticalTextAlign() {
    return verticalTextAlign;
  }

  public StyleSheet getStyleSheet() {
    return styleSheet;
  }

  public InstanceID getInstanceId() {
    return instanceId;
  }

  public int getMajorAxis() {
    return majorAxis;
  }

  public int getMinorAxis() {
    return minorAxis;
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  public ElementAlignment getVerticalAlignment() {
    if ( verticalAlignment == null ) {
      this.verticalAlignment = (ElementAlignment) styleSheet.getStyleProperty( ElementStyleKeys.VALIGNMENT );
    }
    return verticalAlignment;
  }

  public double getPosY() {
    if ( posY == null ) {
      final Object o = styleSheet.getStyleProperty( ElementStyleKeys.POS_Y );
      if ( o == null ) {
        posY = ZERO;
      } else {
        posY = (Float) o;
      }
    }
    return posY.doubleValue();
  }

  public double getPosX() {
    if ( posX == null ) {
      final Object o = styleSheet.getStyleProperty( ElementStyleKeys.POS_X );
      if ( o == null ) {
        posX = ZERO;
      } else {
        posX = (Float) o;
      }
    }
    return posX.doubleValue();
  }

  public ReportAttributeMap<Object> getAttributes() {
    return attributes;
  }

  public ElementType getElementType() {
    return elementType;
  }

  public String toString() {
    return "NodeLayoutProperties{" + "verticalAlignment=" + getVerticalAlignment() + ", majorAxis=" + majorAxis
        + ", minorAxis=" + minorAxis + '}';
  }
}
