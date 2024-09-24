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

package org.pentaho.reporting.engine.classic.core.elementfactory;

import java.awt.Color;
import java.awt.Stroke;

import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;

/**
 * The drawable field element factory can be used to create elements that display <code>Drawable</code> elements.
 * <p/>
 * A drawable field expects the named datasource to contain Drawable objects.
 * <p/>
 * Once the desired properties are set, the factory can be reused to create similiar elements.
 *
 * @author Thomas Morgner
 */
public abstract class AbstractContentElementFactory extends ElementFactory {
  /**
   * The image element scaling property.
   */
  private Boolean scale;
  /**
   * The Keep-Aspect-Ratio property for the generated image element.
   */
  private Boolean keepAspectRatio;

  /**
   * The shape fill-color.
   */
  private Color fillColor;

  /**
   * The shape's stroke.
   */
  private Stroke stroke;
  /**
   * Defines, whether the shape should be filled.
   */
  private Boolean shouldFill;
  /**
   * Defines, whether the shape outline should be drawn.
   */
  private Boolean shouldDraw;

  /**
   * DefaultConstructor.
   */
  protected AbstractContentElementFactory() {
  }

  /**
   * Returns, whether the image content should be scaled to fit the complete image element bounds.
   *
   * @return the scale flag of the image element.
   */
  public Boolean getScale() {
    return scale;
  }

  /**
   * Defines, whether the image content should be scaled to fit the complete image element bounds.
   *
   * @param scale
   *          the scale flag of the image element.
   */
  public void setScale( final Boolean scale ) {
    this.scale = scale;
  }

  /**
   * Returns whether the generated image element should preserve the original aspect ratio of the image content during
   * scaling. This property has no effect if the image content is not scaled.
   *
   * @return the keep aspect ratio flag.
   */
  public Boolean getKeepAspectRatio() {
    return keepAspectRatio;
  }

  /**
   * Defines whether the generated image element should preserve the original aspect ratio of the image content during
   * scaling. This property has no effect if the image content is not scaled.
   *
   * @param keepAspectRatio
   *          whether to keep the aspect ratio of the image content during the scaling.
   */
  public void setKeepAspectRatio( final Boolean keepAspectRatio ) {
    this.keepAspectRatio = keepAspectRatio;
  }

  /**
   * Returns the shapes stroke. The stroke is used to draw the outline of the shape.
   *
   * @return the stoke.
   */
  public Stroke getStroke() {
    return stroke;
  }

  /**
   * Defines the shapes stroke. The stroke is used to draw the outline of the shape.
   *
   * @param stroke
   *          the stoke.
   */
  public void setStroke( final Stroke stroke ) {
    this.stroke = stroke;
  }

  /**
   * Return whether to fill the shape on report generation.
   *
   * @return the should fill flag.
   */
  public Boolean getShouldFill() {
    return shouldFill;
  }

  /**
   * Defines wether to fill the shape on report generation.
   *
   * @param shouldFill
   *          the fill flag.
   */
  public void setShouldFill( final Boolean shouldFill ) {
    this.shouldFill = shouldFill;
  }

  /**
   * Returns whether to draw the shape outline on report generation.
   *
   * @return the draw shape flag.
   */
  public Boolean getShouldDraw() {
    return shouldDraw;
  }

  /**
   * Defines whether to draw the shape outline on report generation.
   *
   * @param shouldDraw
   *          the draw shape flag.
   */
  public void setShouldDraw( final Boolean shouldDraw ) {
    this.shouldDraw = shouldDraw;
  }

  public Color getFillColor() {
    return fillColor;
  }

  public void setFillColor( final Color fillColor ) {
    this.fillColor = fillColor;
  }

  /**
   * Applies the style definition to the elements stylesheet.
   *
   * @param style
   *          the element stylesheet which should receive the style definition.
   */
  protected void applyStyle( final ElementStyleSheet style ) {
    super.applyStyle( style );
    if ( keepAspectRatio != null ) {
      style.setStyleProperty( ElementStyleKeys.KEEP_ASPECT_RATIO, getKeepAspectRatio() );
    }
    if ( scale != null ) {
      style.setStyleProperty( ElementStyleKeys.SCALE, getScale() );
    }
    if ( fillColor != null ) {
      style.setStyleProperty( ElementStyleKeys.FILL_COLOR, getFillColor() );
    }
    if ( stroke != null ) {
      style.setStyleProperty( ElementStyleKeys.STROKE, getStroke() );
    }
    if ( shouldDraw != null ) {
      style.setStyleProperty( ElementStyleKeys.DRAW_SHAPE, getShouldDraw() );
    }
    if ( shouldFill != null ) {
      style.setStyleProperty( ElementStyleKeys.FILL_SHAPE, getShouldFill() );
    }
  }
}
