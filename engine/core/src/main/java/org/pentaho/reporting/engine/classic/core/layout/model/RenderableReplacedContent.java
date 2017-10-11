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

import java.awt.Dimension;

import org.pentaho.reporting.engine.classic.core.ImageContainer;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.factory.drawable.DrawableWrapper;

public final class RenderableReplacedContent {
  private static final long PHYSICAL_LIMIT = (long) StrictMath.pow( 2, 52 );

  private transient Object contentCached;
  private Object content;
  private ResourceKey source;
  private long contentWidth;
  private long contentHeight;
  private boolean keepAspectRatio;

  private RenderLength requestedWidth;
  private RenderLength requestedHeight;
  private RenderLength minimumWidth;
  private RenderLength minimumHeight;
  private RenderLength maximumWidth;
  private RenderLength maximumHeight;

  public RenderableReplacedContent( final StyleSheet styleSheet, final Object content, final ResourceKey source,
      final OutputProcessorMetaData metaData ) {
    this.content = content;
    this.source = source;
    this.keepAspectRatio = styleSheet.getBooleanStyleProperty( ElementStyleKeys.KEEP_ASPECT_RATIO );

    minimumWidth = RenderLength.createFromRaw( styleSheet.getDoubleStyleProperty( ElementStyleKeys.MIN_WIDTH, 0 ) );
    minimumHeight = RenderLength.createFromRaw( styleSheet.getDoubleStyleProperty( ElementStyleKeys.MIN_HEIGHT, 0 ) );
    maximumWidth =
        RenderLength.createFromRaw( styleSheet.getDoubleStyleProperty( ElementStyleKeys.MAX_WIDTH, PHYSICAL_LIMIT ) );
    maximumHeight =
        RenderLength.createFromRaw( styleSheet.getDoubleStyleProperty( ElementStyleKeys.MAX_HEIGHT, PHYSICAL_LIMIT ) );

    final Float prefWidth = (Float) styleSheet.getStyleProperty( ElementStyleKeys.WIDTH, null );
    if ( prefWidth != null ) {
      requestedWidth = RenderLength.createFromRaw( prefWidth.doubleValue() );
    } else {
      requestedWidth = RenderLength.AUTO;
    }

    final Float prefHeight = (Float) styleSheet.getStyleProperty( ElementStyleKeys.HEIGHT, null );
    if ( prefHeight != null ) {
      requestedHeight = RenderLength.createFromRaw( prefHeight.doubleValue() );
    } else {
      requestedHeight = RenderLength.AUTO;
    }

    if ( content instanceof ImageContainer ) {
      final boolean imageResolutionMapping =
          metaData.isFeatureSupported( OutputProcessorFeature.IMAGE_RESOLUTION_MAPPING );
      final double displayResolution = metaData.getNumericFeatureValue( OutputProcessorFeature.DEVICE_RESOLUTION );
      final double correctionFactorPxToPoint = 72.0 / displayResolution;

      final ImageContainer ir = (ImageContainer) content;
      final double scaleX = ir.getScaleX();
      final double scaleY = ir.getScaleY();
      if ( imageResolutionMapping ) {
        contentWidth = StrictGeomUtility.toInternalValue( ir.getImageWidth() * scaleX * correctionFactorPxToPoint );
        contentHeight = StrictGeomUtility.toInternalValue( ir.getImageHeight() * scaleY * correctionFactorPxToPoint );
      } else {
        contentWidth = StrictGeomUtility.toInternalValue( ir.getImageWidth() * scaleX );
        contentHeight = StrictGeomUtility.toInternalValue( ir.getImageHeight() * scaleY );
      }
    } else if ( content instanceof DrawableWrapper ) {
      final DrawableWrapper edr = (DrawableWrapper) content;
      final Dimension preferredSize = edr.getPreferredSize();
      if ( preferredSize != null ) {
        contentWidth = StrictGeomUtility.toInternalValue( preferredSize.getWidth() );
        contentHeight = StrictGeomUtility.toInternalValue( preferredSize.getHeight() );
      }
    } else {
      throw new IllegalStateException( "Unexpected item: " + content );
    }
  }

  public ResourceKey getSource() {
    return source;
  }

  public Object getRawObject() {
    return content;
  }

  public RenderLength getMinimumWidth() {
    return minimumWidth;
  }

  public RenderLength getMinimumHeight() {
    return minimumHeight;
  }

  public RenderLength getMaximumWidth() {
    return maximumWidth;
  }

  public RenderLength getMaximumHeight() {
    return maximumHeight;
  }

  public long getContentWidth() {
    return contentWidth;
  }

  public long getContentHeight() {
    return contentHeight;
  }

  public RenderLength getRequestedWidth() {
    return requestedWidth;
  }

  public RenderLength getRequestedHeight() {
    return requestedHeight;
  }

  @Deprecated
  public boolean isImageResolutionMapping() {
    return false;
  }

  public Object getContentCached() {
    return contentCached;
  }

  public void setContentCached( final Object contentCached ) {
    this.contentCached = contentCached;
  }

  public boolean isKeepAspectRatio() {
    return keepAspectRatio;
  }

  public String toString() {
    return "RenderableReplacedContent{" + ", source=" + source + ", contentWidth=" + contentWidth + ", contentHeight="
        + contentHeight + ", requestedWidth=" + requestedWidth + ", requestedHeight=" + requestedHeight
        + ", minimumWidth=" + minimumWidth + ", minimumHeight=" + minimumHeight + ", maximumWidth=" + maximumWidth
        + ", maximumHeight=" + maximumHeight + '}';
  }
}
