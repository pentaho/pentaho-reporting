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

import java.awt.Color;

import org.pentaho.reporting.engine.classic.core.layout.model.Border;
import org.pentaho.reporting.engine.classic.core.layout.model.BorderCorner;
import org.pentaho.reporting.engine.classic.core.layout.model.BorderEdge;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderLength;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.BorderStyle;
import org.pentaho.reporting.engine.classic.core.style.BoxSizing;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.base.util.LFUMap;

public final class BoxDefinitionFactory {
  private static class CacheKey {
    private Object instanceId;
    private String styleClass;

    protected CacheKey() {
    }

    protected CacheKey( final Object instanceId, final String styleClass ) {
      if ( instanceId == null ) {
        throw new NullPointerException();
      }
      if ( styleClass == null ) {
        throw new NullPointerException();
      }
      this.instanceId = instanceId;
      this.styleClass = styleClass;
    }

    protected void reuse( final Object instanceId, final String styleClass ) {
      if ( instanceId == null ) {
        throw new NullPointerException();
      }
      if ( styleClass == null ) {
        throw new NullPointerException();
      }
      this.instanceId = instanceId;
      this.styleClass = styleClass;
    }

    public Object getInstanceId() {
      return instanceId;
    }

    public void setInstanceId( final Object instanceId ) {
      this.instanceId = instanceId;
    }

    public String getStyleClass() {
      return styleClass;
    }

    public void setStyleClass( final String styleClass ) {
      this.styleClass = styleClass;
    }

    public boolean equals( final Object o ) {
      if ( this == o ) {
        return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
        return false;
      }

      final CacheKey cacheKey = (CacheKey) o;

      if ( !instanceId.equals( cacheKey.instanceId ) ) {
        return false;
      }
      if ( !styleClass.equals( cacheKey.styleClass ) ) {
        return false;
      }

      return true;
    }

    public int hashCode() {
      int result = instanceId.hashCode();
      result = 31 * result + styleClass.hashCode();
      return result;
    }

    public String toString() {
      return "CacheKey{" + "instanceId=" + instanceId + ", styleClass='" + styleClass + '\'' + '}';
    }
  }

  private static class CacheCarrier {
    private long changeTracker;
    private BoxDefinition boxDefinition;

    protected CacheCarrier( final long changeTracker, final BoxDefinition border ) {
      this.changeTracker = changeTracker;
      this.boxDefinition = border;
    }

    protected void update( final long changeTracker, final BoxDefinition border ) {
      this.changeTracker = changeTracker;
      this.boxDefinition = border;
    }

    public long getChangeTracker() {
      return changeTracker;
    }

    public BoxDefinition getBoxDefinition() {
      return boxDefinition;
    }
  }

  private LFUMap<CacheKey, CacheCarrier> cache;
  private CacheKey cacheKey;

  public BoxDefinitionFactory() {
    this.cacheKey = new CacheKey();
    this.cache = new LFUMap<CacheKey, CacheCarrier>( 500 );
  }

  public BoxDefinition getBoxDefinition( final StyleSheet es ) {
    final InstanceID id = es.getId();
    cacheKey.reuse( id, es.getClass().getName() );
    final CacheCarrier cc = cache.get( cacheKey );
    if ( cc == null ) {
      final BoxDefinition boxDefinition = createBoxDefinition( es );
      cache.put( new CacheKey( id, es.getClass().getName() ), new CacheCarrier( es.getChangeTracker(), boxDefinition ) );
      return boxDefinition;
    }

    if ( cc.getChangeTracker() != es.getChangeTracker() ) {
      final BoxDefinition boxDefinition = createBoxDefinition( es );
      cc.update( es.getChangeTracker(), boxDefinition );
      return boxDefinition;
    }

    return cc.getBoxDefinition();
  }

  private BoxDefinition createBoxDefinition( final StyleSheet style ) {
    final BoxDefinition box = new BoxDefinition();
    box.setPreferredWidth( produceFromStyle( style, ElementStyleKeys.WIDTH, RenderLength.AUTO ) );
    box.setPreferredHeight( produceFromStyle( style, ElementStyleKeys.HEIGHT, RenderLength.AUTO ) );
    box.setMinimumWidth( produceFromStyle( style, ElementStyleKeys.MIN_WIDTH, RenderLength.EMPTY ) );
    box.setMinimumHeight( produceFromStyle( style, ElementStyleKeys.MIN_HEIGHT, RenderLength.EMPTY ) );
    box.setSizeSpecifiesBorderBox( BoxSizing.BORDER_BOX.equals( style.getStyleProperty( ElementStyleKeys.BOX_SIZING ) ) );

    box.setMaximumWidth( produceFromStyle( style, ElementStyleKeys.MAX_WIDTH, RenderLength.AUTO ) );
    box.setMaximumHeight( produceFromStyle( style, ElementStyleKeys.MAX_HEIGHT, RenderLength.AUTO ) );
    box.setFixedPosition( produceFromStyle( style, BandStyleKeys.FIXED_POSITION, RenderLength.AUTO ) );

    box.setPaddingTop( Math.max( 0, StrictGeomUtility.toInternalValue( style.getDoubleStyleProperty(
        ElementStyleKeys.PADDING_TOP, 0 ) ) ) );
    box.setPaddingLeft( Math.max( 0, StrictGeomUtility.toInternalValue( style.getDoubleStyleProperty(
        ElementStyleKeys.PADDING_LEFT, 0 ) ) ) );
    box.setPaddingBottom( Math.max( 0, StrictGeomUtility.toInternalValue( style.getDoubleStyleProperty(
        ElementStyleKeys.PADDING_BOTTOM, 0 ) ) ) );
    box.setPaddingRight( Math.max( 0, StrictGeomUtility.toInternalValue( style.getDoubleStyleProperty(
        ElementStyleKeys.PADDING_RIGHT, 0 ) ) ) );

    final BorderEdge edgeTop =
        createEdge( style, ElementStyleKeys.BORDER_TOP_STYLE, ElementStyleKeys.BORDER_TOP_COLOR,
            ElementStyleKeys.BORDER_TOP_WIDTH );
    final BorderEdge edgeLeft =
        createEdge( style, ElementStyleKeys.BORDER_LEFT_STYLE, ElementStyleKeys.BORDER_LEFT_COLOR,
            ElementStyleKeys.BORDER_LEFT_WIDTH );
    final BorderEdge edgeBottom =
        createEdge( style, ElementStyleKeys.BORDER_BOTTOM_STYLE, ElementStyleKeys.BORDER_BOTTOM_COLOR,
            ElementStyleKeys.BORDER_BOTTOM_WIDTH );
    final BorderEdge edgeRight =
        createEdge( style, ElementStyleKeys.BORDER_RIGHT_STYLE, ElementStyleKeys.BORDER_RIGHT_COLOR,
            ElementStyleKeys.BORDER_RIGHT_WIDTH );
    final BorderEdge edgeBreak =
        createEdge( style, ElementStyleKeys.BORDER_BREAK_STYLE, ElementStyleKeys.BORDER_BREAK_COLOR,
            ElementStyleKeys.BORDER_BREAK_WIDTH );

    if ( BorderEdge.EMPTY.equals( edgeBottom ) && BorderEdge.EMPTY.equals( edgeLeft )
        && BorderEdge.EMPTY.equals( edgeBreak ) && BorderEdge.EMPTY.equals( edgeRight )
        && BorderEdge.EMPTY.equals( edgeTop ) ) {
      box.setBorder( Border.EMPTY_BORDER );
    } else {
      final BorderCorner topLeftCorner =
          createCorner( style, ElementStyleKeys.BORDER_TOP_LEFT_RADIUS_WIDTH,
              ElementStyleKeys.BORDER_TOP_LEFT_RADIUS_HEIGHT );
      final BorderCorner topRightCorner =
          createCorner( style, ElementStyleKeys.BORDER_TOP_RIGHT_RADIUS_WIDTH,
              ElementStyleKeys.BORDER_TOP_RIGHT_RADIUS_HEIGHT );
      final BorderCorner bottmLeftCorner =
          createCorner( style, ElementStyleKeys.BORDER_BOTTOM_LEFT_RADIUS_WIDTH,
              ElementStyleKeys.BORDER_BOTTOM_LEFT_RADIUS_HEIGHT );
      final BorderCorner bottomRightCorner =
          createCorner( style, ElementStyleKeys.BORDER_BOTTOM_RIGHT_RADIUS_WIDTH,
              ElementStyleKeys.BORDER_BOTTOM_RIGHT_RADIUS_HEIGHT );
      box.setBorder( new Border( edgeTop, edgeLeft, edgeBottom, edgeRight, edgeBreak, topLeftCorner, topRightCorner,
          bottmLeftCorner, bottomRightCorner ) );
    }
    box.lock();
    return box;
  }

  private BorderCorner createCorner( final StyleSheet style, final StyleKey radiusKeyX, final StyleKey radiusKeyY ) {
    final float dimX = (float) style.getDoubleStyleProperty( radiusKeyX, 0 );
    final float dimY = (float) style.getDoubleStyleProperty( radiusKeyY, 0 );
    if ( dimX <= 0 || dimY <= 0 ) {
      return BorderCorner.EMPTY;
    }
    return new BorderCorner( StrictGeomUtility.toInternalValue( dimX ), StrictGeomUtility.toInternalValue( dimY ) );
  }

  private BorderEdge createEdge( final StyleSheet style, final StyleKey borderStyleKey, final StyleKey borderColorKey,
      final StyleKey borderWidthKey ) {
    final BorderStyle styleRight = (BorderStyle) style.getStyleProperty( borderStyleKey );
    if ( styleRight == null || BorderStyle.NONE.equals( styleRight ) ) {
      return BorderEdge.EMPTY;
    }

    final Color color = (Color) style.getStyleProperty( borderColorKey );
    final double width = style.getDoubleStyleProperty( borderWidthKey, 0 );
    if ( color == null || width <= 0 ) {
      return BorderEdge.EMPTY;
    }

    return new BorderEdge( styleRight, color, StrictGeomUtility.toInternalValue( width ) );
  }

  private RenderLength produceFromStyle( final StyleSheet styleSheet, final StyleKey key, final RenderLength retval ) {
    final Float value = (Float) styleSheet.getStyleProperty( key );
    if ( value == null ) {
      return retval;
    }
    return RenderLength.createFromRaw( value.doubleValue() );
  }

}
