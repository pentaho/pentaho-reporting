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
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.style.BorderStyle;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.FontSmooth;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.libraries.base.util.FloatDimension;

/**
 * The class element factory is the base class for all Element Factories. Element factories can be used to create
 * predefined element types. The properties allow null values, if a property is null, it will not be defined. Undefined
 * properties can inherit their values from the element's parent bands or the default-stylesheet. Whether a property is
 * inheritable from the parent is defined in the style-key itself.
 *
 * @author Thomas Morgner
 */
public abstract class ElementFactory {
  /**
   * The name of the new element.
   */
  private String name;

  /**
   * The elements minimum width. A number between 0 and -100 specifies the width as relative size given in percent of
   * the parent's width or height (depending on the layout model of the band that contains this element).
   */
  private Float minimumWidth;
  /**
   * The elements minimum height. A number between 0 and -100 specifies the width as relative size given in percent of
   * the parent's width or height (depending on the layout model of the band that contains this element).
   */
  private Float minimumHeight;

  /**
   * The elements maximum width. The maximum width cannot have relative values.
   */
  private Float maximumWidth;
  /**
   * The elements maximum height. The maximum height cannot have relative values.
   */
  private Float maximumHeight;

  /**
   * The elements preferred width. A number between 0 and -100 specifies the width as relative size given in percent of
   * the parent's width or height (depending on the layout model of the band that contains this element).
   */
  private Float width;
  /**
   * The elements preferred height. A number between 0 and -100 specifies the width as relative size given in percent of
   * the parent's width or height (depending on the layout model of the band that contains this element).
   */
  private Float height;

  /**
   * The elements absolute horizontal position for the canvas-layout. The position is relative to the parents upper left
   * corner of the content-area. A number between 0 and -100 specifies the width as relative size given in percent of
   * the parent's width or height (depending on the layout model of the band that contains this element).
   */
  private Float x;

  /**
   * The elements absolute vertical position for the canvas-layout. The position is relative to the parents upper left
   * corner of the content-area. A number between 0 and -100 specifies the width as relative size given in percent of
   * the parent's width or height (depending on the layout model of the band that contains this element).
   */
  private Float y;

  /**
   * The elements dynamic content height flag.
   */
  private Boolean dynamicHeight;

  /**
   * The elements visible flag.
   */
  private Boolean visible;

  /**
   * Defines whether the layouter will try to avoid to generate pagebreaks inside this element. If the element does not
   * fit on the current page, it will be moved to the next page. Only if this next page does not have enough space to
   * hold this element, a pagebreak will be generated inside the element.
   */
  private Boolean avoidPagebreaks;

  /**
   * Defines, whether text contained in this element will overflow horizontally. This will generate overlapping text in
   * the pageable outputs without increasing the total size of the element. Activating this property may cause rendering
   * artifacts.
   */
  private Boolean overflowX;

  /**
   * Defines, whether text contained in this element will overflow vertically. This will generate overlapping text in
   * the pageable outputs without increasing the total size of the element. Activating this property may cause rendering
   * artifacts.
   */
  private Boolean overflowY;

  /**
   * Defines the number of widow-lines in this element. This avoids pagebreaks inside the first number of lines of a
   * paragraph. If a pagebreak would occur inside the widow-segment, the whole box will be shifted to the next page.
   */
  private Integer widows;
  /**
   * Defines the number of orphan-lines in this element. This avoids pagebreaks inside the last number of lines of a
   * paragraph. If a pagebreak would occur inside the orphan-segment, the whole number of orphan lines or the whole box
   * will be shifted to the next page.
   */
  private Integer orphans;

  private Boolean widowOrphanOptOut;

  /**
   * The background color of the box.
   */
  private Color backgroundColor;

  /**
   * Defines the global padding of this box. Padding defines the empty area between the border and the content of an
   * element. This property defines a short-hand property for all other padding properties. Paddings cannot have
   * relative sizes.
   */
  private Float padding;

  /**
   * Defines the top padding of this box. Padding defines the empty area between the border and the content of an
   * element. Paddings cannot have relative sizes.
   */
  private Float paddingTop;
  /**
   * Defines the left padding of this box. Padding defines the empty area between the border and the content of an
   * element. Paddings cannot have relative sizes.
   */
  private Float paddingLeft;
  /**
   * Defines the bottom padding of this box. Padding defines the empty area between the border and the content of an
   * element. Paddings cannot have relative sizes.
   */
  private Float paddingBottom;
  /**
   * Defines the right padding of this box. Padding defines the empty area between the border and the content of an
   * element. Paddings cannot have relative sizes.
   */
  private Float paddingRight;

  /**
   * Defines the global border width of this box. A border width of zero effectively disables the border. A number
   * between 0 and -100 specifies the width as relative size given in percent of the parent's width or height (depending
   * on the layout model of the band that contains this element). This property defines a short-hand property for all
   * other border-width properties.
   */
  private Float borderWidth;
  /**
   * Defines the top border width of this box. A border width of zero effectively disables the border. A number between
   * 0 and -100 specifies the width as relative size given in percent of the parent's width or height (depending on the
   * layout model of the band that contains this element).
   */
  private Float borderTopWidth;
  /**
   * Defines the left border width of this box. A border width of zero effectively disables the border. A number between
   * 0 and -100 specifies the width as relative size given in percent of the parent's width or height (depending on the
   * layout model of the band that contains this element).
   */
  private Float borderLeftWidth;
  /**
   * Defines the bottom border width of this box. A border width of zero effectively disables the border. A number
   * between 0 and -100 specifies the width as relative size given in percent of the parent's width or height (depending
   * on the layout model of the band that contains this element).
   */
  private Float borderBottomWidth;
  /**
   * Defines the right border width of this box. A border width of zero effectively disables the border. A number
   * between 0 and -100 specifies the width as relative size given in percent of the parent's width or height (depending
   * on the layout model of the band that contains this element).
   */
  private Float borderRightWidth;
  /**
   * Defines the break border width of this box. A border width of zero effectively disables the border. A number
   * between 0 and -100 specifies the width as relative size given in percent of the parent's width or height (depending
   * on the layout model of the band that contains this element).
   * <p/>
   * The break border is applied to all inner border-edges of elements that got split on a pagebreak.
   */
  private Float borderBreakWidth;

  /**
   * Defines the global border-style for the element. If the border-style is set to NONE or undefined, the border-size
   * property will be ignored and no border is rendered. This property defines a short-hand property for all other
   * border-style properties.
   */
  private BorderStyle borderStyle;
  /**
   * Defines the top border-style for the element. If the border-style is set to NONE or undefined, the border-size
   * property will be ignored and no border is rendered.
   */
  private BorderStyle borderTopStyle;
  /**
   * Defines the left border-style for the element. If the border-style is set to NONE or undefined, the border-size
   * property will be ignored and no border is rendered.
   */
  private BorderStyle borderLeftStyle;
  /**
   * Defines the bottom border-style for the element. If the border-style is set to NONE or undefined, the border-size
   * property will be ignored and no border is rendered.
   */
  private BorderStyle borderBottomStyle;
  /**
   * Defines the right border-style for the element. If the border-style is set to NONE or undefined, the border-size
   * property will be ignored and no border is rendered.
   */
  private BorderStyle borderRightStyle;
  /**
   * Defines the break border-style for the element. If the border-style is set to NONE or undefined, the border-size
   * property will be ignored and no border is rendered.
   * <p/>
   * The break border is applied to all inner border-edges of elements that got split on a pagebreak.
   */
  private BorderStyle borderBreakStyle;

  /**
   * Defines the global border-color. This property defines a short-hand property for all other border-color properties.
   */
  private Color borderColor;

  /**
   * Defines the top border-color.
   */
  private Color borderTopColor;
  /**
   * Defines the left border-color.
   */
  private Color borderLeftColor;
  /**
   * Defines the bottom border-color.
   */
  private Color borderBottomColor;
  /**
   * Defines the right border-color.
   */
  private Color borderRightColor;
  /**
   * Defines the break border-color.
   */
  private Color borderBreakColor;

  /**
   * Defines the global width of the border-radius for this element. If the border radius has a non-zero width and
   * height, the element's border will have rounded corners. This property is a short-hand property for all other
   * border-radius properties.
   * <p/>
   * Split borders cannot have rounded-corners.
   */
  private Float borderRadiusWidth;

  /**
   * Defines the global height of the border-radius for this element. If the border radius has a non-zero width and
   * height, the element's border will have rounded corners. This property is a short-hand property for all other
   * border-radius properties.
   * <p/>
   * Split borders cannot have rounded-corners.
   */
  private Float borderRadiusHeight;

  /**
   * Defines the width of the bottom-left border-radius for this element. If the border radius has a non-zero width and
   * height, the element's border will have a rounded bottom-left corner.
   */
  private Float borderBottomLeftRadiusWidth;
  /**
   * Defines the width of the bottom-right border-radius for this element. If the border radius has a non-zero width and
   * height, the element's border will have a rounded bottom-right corner.
   */
  private Float borderBottomRightRadiusWidth;
  /**
   * Defines the width of the top-left border-radius for this element. If the border radius has a non-zero width and
   * height, the element's border will have a rounded top-left corner.
   */
  private Float borderTopLeftRadiusWidth;
  /**
   * Defines the width of the top-right border-radius for this element. If the border radius has a non-zero width and
   * height, the element's border will have a rounded top-right corner.
   */
  private Float borderTopRightRadiusWidth;

  /**
   * Defines the height of the bottom-left border-radius for this element. If the border radius has a non-zero width and
   * height, the element's border will have a rounded bottom-left corner.
   */
  private Float borderBottomLeftRadiusHeight;
  /**
   * Defines the height of the bottom-right border-radius for this element. If the border radius has a non-zero width
   * and height, the element's border will have a rounded bottom-right corner.
   */
  private Float borderBottomRightRadiusHeight;
  /**
   * Defines the height of the top-left border-radius for this element. If the border radius has a non-zero width and
   * height, the element's border will have a rounded top-left corner.
   */
  private Float borderTopLeftRadiusHeight;
  /**
   * Defines the height of the top-right border-radius for this element. If the border radius has a non-zero width and
   * height, the element's border will have a rounded top-right corner.
   */
  private Float borderTopRightRadiusHeight;

  /**
   * Defines the text color.
   */
  private Color color;

  /**
   * Defines the vertical alignment of the content.
   */
  private ElementAlignment verticalAlignment;

  /**
   * Defines the horizontal alignment of the content.
   */
  private ElementAlignment horizontalAlignment;

  /**
   * The URL for an hyperlink that contains this element as content.
   */
  private String hRefTarget;

  /**
   * The target-window for the hyperlink defined for this element.
   */
  private String hRefWindow;

  /**
   * The href-title for the hyperlink defined for this element.
   */
  private String hRefTitle;

  /**
   * Defines, whether font smoothing (also known as text-aliasing) is applied to the element. If the font-smooth
   * property is undefined or auto, the actual value will be comptued depending on the element's font size.
   */
  private FontSmooth fontSmooth;
  private Boolean useMinChunkWidth;

  /**
   * Default Constructor.
   */
  protected ElementFactory() {
  }

  /**
   * Returns the defined global padding of this box. Padding defines the empty area between the border and the content
   * of an element. This property defines a short-hand property for all other padding properties. Paddings cannot have
   * relative sizes.
   *
   * @return the padding or null, if none is defined here.
   */
  public Float getPadding() {
    return padding;
  }

  /**
   * Defines the global padding of this box. Padding defines the empty area between the border and the content of an
   * element. This property defines a short-hand property for all other padding properties. Paddings cannot have
   * relative sizes.
   *
   * @param padding
   *          the padding or null, if the default should be used.
   */
  public void setPadding( final Float padding ) {
    this.padding = padding;
  }

  /**
   * Returns the defined global border width of this box. A border width of zero effectively disables the border. A
   * number between 0 and -100 specifies the width as relative size given in percent of the parent's width or height
   * (depending on the layout model of the band that contains this element). This property defines a short-hand property
   * for all other border-width properties.
   *
   * @return the defined border-width or null, if none is defined here.
   */
  public Float getBorderWidth() {
    return borderWidth;
  }

  /**
   * Defines the global border width of this box. A border width of zero effectively disables the border. A number
   * between 0 and -100 specifies the width as relative size given in percent of the parent's width or height (depending
   * on the layout model of the band that contains this element). This property defines a short-hand property for all
   * other border-width properties.
   *
   * @param borderWidth
   *          the defined border width or null, if the default should be used.
   */
  public void setBorderWidth( final Float borderWidth ) {
    this.borderWidth = borderWidth;
  }

  /**
   * Returns the defined global border-style for the element. If the border-style is set to NONE or undefined, the
   * border-size property will be ignored and no border is rendered. This property defines a short-hand property for all
   * other border-style properties.
   *
   * @return the defined border-style or null, if the default should be used.
   */
  public BorderStyle getBorderStyle() {
    return borderStyle;
  }

  /**
   * Defines the global border-style for the element. If the border-style is set to NONE or undefined, the border-size
   * property will be ignored and no border is rendered. This property defines a short-hand property for all other
   * border-style properties.
   *
   * @param borderStyle
   *          the defined border-style or null, if none is defined here.
   */
  public void setBorderStyle( final BorderStyle borderStyle ) {
    this.borderStyle = borderStyle;
  }

  /**
   * Returns the defined global border-color. This property defines a short-hand property for all other border-color
   * properties.
   *
   * @return the border-color for all borders or null, if no global color is defined.
   */
  public Color getBorderColor() {
    return borderColor;
  }

  /**
   * Defines the global border-color. This property defines a short-hand property for all other border-color properties.
   *
   * @param borderColor
   *          the defined color for all borders or null, if no global color is defined.
   */
  public void setBorderColor( final Color borderColor ) {
    this.borderColor = borderColor;
  }

  /**
   * Returns the defined global border-radius for this element. If the border radius has a non-zero width and height,
   * the element's border will have rounded corners. This property is a short-hand property for all other border-radius
   * properties.
   *
   * @return the defined border-radius for all corners of this element or null, if no global default is defined here.
   */
  public Dimension2D getBorderRadius() {
    if ( borderRadiusWidth == null || borderRadiusHeight == null ) {
      return null;
    }
    return new FloatDimension( borderRadiusWidth.floatValue(), borderRadiusHeight.floatValue() );
  }

  /**
   * Defines the global border-radius for this element. If the border radius has a non-zero width and height, the
   * element's border will have rounded corners. This property is a short-hand property for all other border-radius
   * properties.
   * <p/>
   *
   * @param borderRadius
   *          the defined border-radius for all corners of this element or null, if no global default should be defined
   *          here.
   */
  public void setBorderRadius( final Dimension2D borderRadius ) {
    if ( borderRadius == null ) {
      this.borderRadiusWidth = null;
      this.borderRadiusHeight = null;
    } else {
      this.borderRadiusWidth = new Float( borderRadius.getWidth() );
      this.borderRadiusHeight = new Float( borderRadius.getHeight() );
    }
  }

  /**
   * Returns the defined global width of the border-radius for this element. If the border radius has a non-zero width
   * and height, the element's border will have rounded corners. This property is a short-hand property for all other
   * border-radius properties.
   *
   * @return the defined width of the border-radius for all corners of this element or null, if no global default is
   *         defined here.
   */
  public Float getBorderRadiusWidth() {
    return borderRadiusWidth;
  }

  /**
   * Defines the global width of the border-radius for this element. If the border radius has a non-zero width and
   * height, the element's border will have rounded corners. This property is a short-hand property for all other
   * border-radius properties.
   * <p/>
   *
   * @param borderRadiusWidth
   *          the defined width of the border-radius for all corners of this element or null, if no global default
   *          should be defined here.
   */
  public void setBorderRadiusWidth( final Float borderRadiusWidth ) {
    this.borderRadiusWidth = borderRadiusWidth;
  }

  /**
   * Returns the defined global height of the border-radius for this element. If the border radius has a non-zero width
   * and height, the element's border will have rounded corners. This property is a short-hand property for all other
   * border-radius properties.
   *
   * @return the defined height of the border-radius for all corners of this element or null, if no global default is
   *         defined here.
   */
  public Float getBorderRadiusHeight() {
    return borderRadiusHeight;
  }

  /**
   * Defines the global height of the border-radius for this element. If the border radius has a non-zero width and
   * height, the element's border will have rounded corners. This property is a short-hand property for all other
   * border-radius properties.
   * <p/>
   *
   * @param borderRadiusHeight
   *          the defined height of the border-radius for all corners of this element or null, if no global default
   *          should be defined here.
   */
  public void setBorderRadiusHeight( final Float borderRadiusHeight ) {
    this.borderRadiusHeight = borderRadiusHeight;
  }

  /**
   * Returns the defined bottom-left border-radius for this element. If the border radius has a non-zero width and
   * height, the element's border will have a rounded bottom-left corner.
   *
   * @return the defined border-radius for the bottom-left corner of this element or null, if this property is
   *         undefined.
   */
  public Dimension2D getBorderBottomLeftRadius() {
    if ( borderBottomLeftRadiusWidth == null || borderBottomLeftRadiusHeight == null ) {
      return null;
    }
    return new FloatDimension( borderBottomLeftRadiusWidth.floatValue(), borderBottomLeftRadiusHeight.floatValue() );
  }

  /**
   * Defines the bottom-left border-radius for this element. If the border radius has a non-zero width and height, the
   * element's border will have a rounded bottom-left corner.
   *
   * @param borderRadius
   *          the defined border-radius for the bottom-left corner of this element or null, if this property should be
   *          undefined.
   */
  public void setBorderBottomLeftRadius( final Dimension2D borderRadius ) {
    if ( borderRadius == null ) {
      this.borderBottomLeftRadiusWidth = null;
      this.borderBottomLeftRadiusHeight = null;
    } else {
      this.borderBottomLeftRadiusWidth = new Float( borderRadius.getWidth() );
      this.borderBottomLeftRadiusHeight = new Float( borderRadius.getHeight() );
    }
  }

  /**
   * Returns width of the defined bottom-left border-radius for this element. If the border radius has a non-zero width
   * and height, the element's border will have a rounded bottom-left corner.
   *
   * @return the defined width of the border-radius for the bottom-left corner of this element or null, if this property
   *         is undefined.
   */
  public Float getBorderBottomLeftRadiusWidth() {
    return borderBottomLeftRadiusWidth;
  }

  /**
   * Defines width of the bottom-left border-radius for this element. If the border radius has a non-zero width and
   * height, the element's border will have a rounded bottom-left corner.
   *
   * @param borderBottomLeftRadiusWidth
   *          the width of the defined border-radius for the bottom-left corner of this element or null, if this
   *          property should be undefined.
   */
  public void setBorderBottomLeftRadiusWidth( final Float borderBottomLeftRadiusWidth ) {
    this.borderBottomLeftRadiusWidth = borderBottomLeftRadiusWidth;
  }

  /**
   * Returns height of the defined bottom-left border-radius for this element. If the border radius has a non-zero width
   * and height, the element's border will have a rounded bottom-left corner.
   *
   * @return the defined height of the border-radius for the bottom-left corner of this element or null, if this
   *         property is undefined.
   */
  public Float getBorderBottomLeftRadiusHeight() {
    return borderBottomLeftRadiusHeight;
  }

  /**
   * Defines height of the bottom-left border-radius for this element. If the border radius has a non-zero width and
   * height, the element's border will have a rounded bottom-left corner.
   *
   * @param borderBottomLeftRadiusHeight
   *          the height of the defined border-radius for the bottom-left corner of this element or null, if this
   *          property should be undefined.
   */
  public void setBorderBottomLeftRadiusHeight( final Float borderBottomLeftRadiusHeight ) {
    this.borderBottomLeftRadiusHeight = borderBottomLeftRadiusHeight;
  }

  /**
   * Returns the defined bottom-right border-radius for this element. If the border radius has a non-zero width and
   * height, the element's border will have a rounded bottom-right corner.
   *
   * @return the defined border-radius for the bottom-right corner of this element or null, if this property is
   *         undefined.
   */
  public Dimension2D getBorderBottomRightRadius() {
    if ( borderBottomRightRadiusWidth == null || borderBottomRightRadiusHeight == null ) {
      return null;
    }
    return new FloatDimension( borderBottomRightRadiusWidth.floatValue(), borderBottomRightRadiusHeight.floatValue() );
  }

  /**
   * Defines the bottom-right border-radius for this element. If the border radius has a non-zero width and height, the
   * element's border will have a rounded bottom-right corner.
   *
   * @param borderRadius
   *          the defined border-radius for the bottom-right corner of this element or null, if this property should be
   *          undefined.
   */
  public void setBorderBottomRightRadius( final Dimension2D borderRadius ) {
    if ( borderRadius == null ) {
      this.borderBottomRightRadiusWidth = null;
      this.borderBottomRightRadiusHeight = null;
    } else {
      this.borderBottomRightRadiusWidth = new Float( borderRadius.getWidth() );
      this.borderBottomRightRadiusHeight = new Float( borderRadius.getHeight() );
    }
  }

  /**
   * Returns the width of the defined bottom-right border-radius for this element. If the border radius has a non-zero
   * width and height, the element's border will have a rounded bottom-right corner.
   *
   * @return the width of the defined border-radius for the bottom-right corner of this element or null, if this
   *         property is undefined.
   */
  public Float getBorderBottomRightRadiusWidth() {
    return borderBottomRightRadiusWidth;
  }

  /**
   * Defines the width of the bottom-right border-radius for this element. If the border radius has a non-zero width and
   * height, the element's border will have a rounded bottom-right corner.
   *
   * @param borderBottomRightRadiusWidth
   *          the width of the defined border-radius for the bottom-right corner of this element or null, if this
   *          property should be undefined.
   */
  public void setBorderBottomRightRadiusWidth( final Float borderBottomRightRadiusWidth ) {
    this.borderBottomRightRadiusWidth = borderBottomRightRadiusWidth;
  }

  /**
   * Returns the height of the defined bottom-right border-radius for this element. If the border radius has a non-zero
   * width and height, the element's border will have a rounded bottom-right corner.
   *
   * @return the height of the defined border-radius for the bottom-right corner of this element or null, if this
   *         property is undefined.
   */
  public Float getBorderBottomRightRadiusHeight() {
    return borderBottomRightRadiusHeight;
  }

  /**
   * Defines the height of the bottom-right border-radius for this element. If the border radius has a non-zero width
   * and height, the element's border will have a rounded bottom-right corner.
   *
   * @param borderBottomRightRadiusHeight
   *          the height of the defined border-radius for the bottom-right corner of this element or null, if this
   *          property should be undefined.
   */
  public void setBorderBottomRightRadiusHeight( final Float borderBottomRightRadiusHeight ) {
    this.borderBottomRightRadiusHeight = borderBottomRightRadiusHeight;
  }

  /**
   * Returns the defined top-left border-radius for this element. If the border radius has a non-zero width and height,
   * the element's border will have a rounded top-left corner.
   *
   * @return the defined border-radius for the top-left corner of this element or null, if this property is undefined.
   */
  public Dimension2D getBorderTopLeftRadius() {
    if ( borderTopLeftRadiusWidth == null || borderTopLeftRadiusHeight == null ) {
      return null;
    }
    return new FloatDimension( borderTopLeftRadiusWidth.floatValue(), borderTopLeftRadiusHeight.floatValue() );
  }

  /**
   * Defines the top-left border-radius for this element. If the border radius has a non-zero width and height, the
   * element's border will have a rounded top-left corner.
   *
   * @param borderRadius
   *          the defined border-radius for the top-left corner of this element or null, if this property should be
   *          undefined.
   */
  public void setBorderTopLeftRadius( final Dimension2D borderRadius ) {
    if ( borderRadius == null ) {
      this.borderTopLeftRadiusWidth = null;
      this.borderTopLeftRadiusHeight = null;
    } else {
      this.borderTopLeftRadiusWidth = new Float( borderRadius.getWidth() );
      this.borderTopLeftRadiusHeight = new Float( borderRadius.getHeight() );
    }
  }

  /**
   * Returns the width of the defined top-left border-radius for this element. If the border radius has a non-zero width
   * and height, the element's border will have a rounded top-left corner.
   *
   * @return the width of the defined border-radius for the top-left corner of this element or null, if this property is
   *         undefined.
   */
  public Float getBorderTopLeftRadiusWidth() {
    return borderTopLeftRadiusWidth;
  }

  /**
   * Defines the width of the top-left border-radius for this element. If the border radius has a non-zero width and
   * height, the element's border will have a rounded top-left corner.
   *
   * @param borderTopLeftRadiusWidth
   *          the width of the defined border-radius for the top-left corner of this element or null, if this property
   *          should be undefined.
   */
  public void setBorderTopLeftRadiusWidth( final Float borderTopLeftRadiusWidth ) {
    this.borderTopLeftRadiusWidth = borderTopLeftRadiusWidth;
  }

  /**
   * Returns the height of the defined top-left border-radius for this element. If the border radius has a non-zero
   * width and height, the element's border will have a rounded top-left corner.
   *
   * @return the height of the defined border-radius for the top-left corner of this element or null, if this property
   *         is undefined.
   */
  public Float getBorderTopLeftRadiusHeight() {
    return borderTopLeftRadiusHeight;
  }

  /**
   * Defines the height of the top-left border-radius for this element. If the border radius has a non-zero width and
   * height, the element's border will have a rounded top-left corner.
   *
   * @param borderTopLeftRadiusHeight
   *          the height of the defined border-radius for the top-left corner of this element or null, if this property
   *          should be undefined.
   */
  public void setBorderTopLeftRadiusHeight( final Float borderTopLeftRadiusHeight ) {
    this.borderTopLeftRadiusHeight = borderTopLeftRadiusHeight;
  }

  /**
   * Returns the defined top-right border-radius for this element. If the border radius has a non-zero width and height,
   * the element's border will have a rounded top-right corner.
   *
   * @return the defined border-radius for the top-right corner of this element or null, if this property is undefined.
   */
  public Dimension2D getBorderTopRightRadius() {
    if ( borderTopRightRadiusWidth == null || borderTopRightRadiusHeight == null ) {
      return null;
    }
    return new FloatDimension( borderTopRightRadiusWidth.floatValue(), borderTopRightRadiusHeight.floatValue() );
  }

  /**
   * Defines the top-right border-radius for this element. If the border radius has a non-zero width and height, the
   * element's border will have a rounded top-right corner.
   *
   * @param borderRadius
   *          the defined border-radius for the top-right corner of this element or null, if this property should be
   *          undefined.
   */
  public void setBorderTopRightRadius( final Dimension2D borderRadius ) {
    if ( borderRadius == null ) {
      this.borderTopRightRadiusWidth = null;
      this.borderTopRightRadiusHeight = null;
    } else {
      this.borderTopRightRadiusWidth = new Float( borderRadius.getWidth() );
      this.borderTopRightRadiusHeight = new Float( borderRadius.getHeight() );
    }
  }

  /**
   * Returns the width of the defined top-right border-radius for this element. If the border radius has a non-zero
   * width and height, the element's border will have a rounded top-right corner.
   *
   * @return the width of the defined border-radius for the top-right corner of this element or null, if this property
   *         is undefined.
   */
  public Float getBorderTopRightRadiusWidth() {
    return borderTopRightRadiusWidth;
  }

  /**
   * Defines the width of the top-right border-radius for this element. If the border radius has a non-zero width and
   * height, the element's border will have a rounded top-right corner.
   *
   * @param borderTopRightRadiusWidth
   *          the width of the defined border-radius for the top-right corner of this element or null, if this property
   *          should be undefined.
   */
  public void setBorderTopRightRadiusWidth( final Float borderTopRightRadiusWidth ) {
    this.borderTopRightRadiusWidth = borderTopRightRadiusWidth;
  }

  /**
   * Returns the height of the defined top-right border-radius for this element. If the border radius has a non-zero
   * width and height, the element's border will have a rounded top-right corner.
   *
   * @return the height of the defined border-radius for the top-right corner of this element or null, if this property
   *         is undefined.
   */
  public Float getBorderTopRightRadiusHeight() {
    return borderTopRightRadiusHeight;
  }

  /**
   * Defines the height of the top-right border-radius for this element. If the border radius has a non-zero width and
   * height, the element's border will have a rounded top-right corner.
   *
   * @param borderTopRightRadiusHeight
   *          the height of the defined border-radius for the top-right corner of this element or null, if this property
   *          should be undefined.
   */
  public void setBorderTopRightRadiusHeight( final Float borderTopRightRadiusHeight ) {
    this.borderTopRightRadiusHeight = borderTopRightRadiusHeight;
  }

  /**
   * Returns the text color for the new element.
   *
   * @return the text color.
   */
  public Color getColor() {
    return color;
  }

  /**
   * Defines the text color for the new element.
   *
   * @param color
   *          the text color.
   */
  public void setColor( final Color color ) {
    this.color = color;
  }

  /**
   * Returns the vertical alignment for the content of this element.
   *
   * @return the vertical alignment.
   */
  public ElementAlignment getVerticalAlignment() {
    return verticalAlignment;
  }

  /**
   * Defines the vertical alignment for the content of this element.
   *
   * @param verticalAlignment
   *          the vertical alignment.
   */
  public void setVerticalAlignment( final ElementAlignment verticalAlignment ) {
    this.verticalAlignment = verticalAlignment;
  }

  /**
   * Returns the horizontal alignment for the content of this element.
   *
   * @return the horizontal alignment.
   */
  public ElementAlignment getHorizontalAlignment() {
    return horizontalAlignment;
  }

  /**
   * Defines the horizontal alignment for the content of this element.
   *
   * @param horizontalAlignment
   *          the vertical alignment.
   */
  public void setHorizontalAlignment( final ElementAlignment horizontalAlignment ) {
    this.horizontalAlignment = horizontalAlignment;
  }

  /**
   * Returns the defined font smoothing (also known as text-aliasing) is applied to the element. If the font-smooth
   * property is undefined or auto, the actual value will be comptued depending on the element's font size.
   *
   * @return the font-smooth constant or null, if this property is undefined.
   */
  public FontSmooth getFontSmooth() {
    return fontSmooth;
  }

  /**
   * Defines, whether font smoothing (also known as text-aliasing) is applied to the element. If the font-smooth
   * property is undefined or auto, the actual value will be comptued depending on the element's font size.
   *
   * @param fontSmooth
   *          the font-smooth constant or null, if this property should be left undefined.
   */
  public void setFontSmooth( final FontSmooth fontSmooth ) {
    this.fontSmooth = fontSmooth;
  }

  /**
   * Returns the defined top border width of this box. A border width of zero effectively disables the border. A number
   * between 0 and -100 specifies the width as relative size given in percent of the parent's width or height (depending
   * on the layout model of the band that contains this element).
   *
   * @return the border width for the top edge or null if the property should be left undefined.
   */
  public Float getBorderTopWidth() {
    return borderTopWidth;
  }

  /**
   * Defines the top border width of this box. A border width of zero effectively disables the border. A number between
   * 0 and -100 specifies the width as relative size given in percent of the parent's width or height (depending on the
   * layout model of the band that contains this element).
   *
   * @param borderTopWidth
   *          the border width for the top edge or null if the property should be left undefined.
   */
  public void setBorderTopWidth( final Float borderTopWidth ) {
    this.borderTopWidth = borderTopWidth;
  }

  /**
   * Returns the defined left border width of this box. A border width of zero effectively disables the border. A number
   * between 0 and -100 specifies the width as relative size given in percent of the parent's width or height (depending
   * on the layout model of the band that contains this element).
   *
   * @return the border width for the left edge or null if the property should be left undefined.
   */
  public Float getBorderLeftWidth() {
    return borderLeftWidth;
  }

  /**
   * Defines the left border width of this box. A border width of zero effectively disables the border. A number between
   * 0 and -100 specifies the width as relative size given in percent of the parent's width or height (depending on the
   * layout model of the band that contains this element).
   *
   * @param borderLeftWidth
   *          the border width for the left edge or null if the property should be left undefined.
   */
  public void setBorderLeftWidth( final Float borderLeftWidth ) {
    this.borderLeftWidth = borderLeftWidth;
  }

  /**
   * Returns the defined left border width of this box. A border width of zero effectively disables the border. A number
   * between 0 and -100 specifies the width as relative size given in percent of the parent's width or height (depending
   * on the layout model of the band that contains this element).
   *
   * @return the border width for the left edge or null if the property should be left undefined.
   */
  public Float getBorderBottomWidth() {
    return borderBottomWidth;
  }

  /**
   * Defines the bottom border width of this box. A border width of zero effectively disables the border. A number
   * between 0 and -100 specifies the width as relative size given in percent of the parent's width or height (depending
   * on the layout model of the band that contains this element).
   *
   * @param borderBottomWidth
   *          the border width for the bottom edge or null if the property should be left undefined.
   */
  public void setBorderBottomWidth( final Float borderBottomWidth ) {
    this.borderBottomWidth = borderBottomWidth;
  }

  /**
   * Returns the defined right border width of this box. A border width of zero effectively disables the border. A
   * number between 0 and -100 specifies the width as relative size given in percent of the parent's width or height
   * (depending on the layout model of the band that contains this element).
   *
   * @return the border width for the right edge or null if the property should be left undefined.
   */
  public Float getBorderRightWidth() {
    return borderRightWidth;
  }

  /**
   * Defines the right border width of this box. A border width of zero effectively disables the border. A number
   * between 0 and -100 specifies the width as relative size given in percent of the parent's width or height (depending
   * on the layout model of the band that contains this element).
   *
   * @param borderRightWidth
   *          the border width for the right edge or null if the property should be left undefined.
   */
  public void setBorderRightWidth( final Float borderRightWidth ) {
    this.borderRightWidth = borderRightWidth;
  }

  /**
   * Returns the defined break border width of this box. A border width of zero effectively disables the border. A
   * number between 0 and -100 specifies the width as relative size given in percent of the parent's width or height
   * (depending on the layout model of the band that contains this element).
   * <p/>
   * The break border is applied to all inner border-edges of elements that got split on a pagebreak.
   *
   * @return the width of the break edge of the border or null, if not defined.
   */
  public Float getBorderBreakWidth() {
    return borderBreakWidth;
  }

  /**
   * Defines the break border width of this box. A border width of zero effectively disables the border. A number
   * between 0 and -100 specifies the width as relative size given in percent of the parent's width or height (depending
   * on the layout model of the band that contains this element).
   * <p/>
   * The break border is applied to all inner border-edges of elements that got split on a pagebreak.
   *
   * @param borderBreakWidth
   *          the width of the break edge of the border or null, if not defined.
   */
  public void setBorderBreakWidth( final Float borderBreakWidth ) {
    this.borderBreakWidth = borderBreakWidth;
  }

  /**
   * Returns the defined top border-style for the element. If the border-style is set to NONE or undefined, the
   * border-size property will be ignored and no border is rendered.
   *
   * @return the border style for the top edge or null, if the style should remain undefined.
   */
  public BorderStyle getBorderTopStyle() {
    return borderTopStyle;
  }

  /**
   * Defines the top border-style for the element. If the border-style is set to NONE or undefined, the border-size
   * property will be ignored and no border is rendered.
   *
   * @param borderTopStyle
   *          the border style for the top edge or null, if the style should remain undefined.
   */
  public void setBorderTopStyle( final BorderStyle borderTopStyle ) {
    this.borderTopStyle = borderTopStyle;
  }

  /**
   * Returns the defined left border-style for the element. If the border-style is set to NONE or undefined, the
   * border-size property will be ignored and no border is rendered.
   *
   * @return the border style for the left edge or null, if the style should remain undefined.
   */
  public BorderStyle getBorderLeftStyle() {
    return borderLeftStyle;
  }

  /**
   * Defines the left border-style for the element. If the border-style is set to NONE or undefined, the border-size
   * property will be ignored and no border is rendered.
   *
   * @param borderLeftStyle
   *          the border style for the left edge or null, if the style should remain undefined.
   */
  public void setBorderLeftStyle( final BorderStyle borderLeftStyle ) {
    this.borderLeftStyle = borderLeftStyle;
  }

  /**
   * Returns the defined bottom border-style for the element. If the border-style is set to NONE or undefined, the
   * border-size property will be ignored and no border is rendered.
   *
   * @return the border style for the bottom edge or null, if the style should remain undefined.
   */
  public BorderStyle getBorderBottomStyle() {
    return borderBottomStyle;
  }

  /**
   * Defines the bottom border-style for the element. If the border-style is set to NONE or undefined, the border-size
   * property will be ignored and no border is rendered.
   *
   * @param borderBottomStyle
   *          the border style for the bottom edge or null, if the style should remain undefined.
   */
  public void setBorderBottomStyle( final BorderStyle borderBottomStyle ) {
    this.borderBottomStyle = borderBottomStyle;
  }

  /**
   * Returns the defined right border-style for the element. If the border-style is set to NONE or undefined, the
   * border-size property will be ignored and no border is rendered.
   *
   * @return the border style for the right edge or null, if the style should remain undefined.
   */
  public BorderStyle getBorderRightStyle() {
    return borderRightStyle;
  }

  /**
   * Defines the right border-style for the element. If the border-style is set to NONE or undefined, the border-size
   * property will be ignored and no border is rendered.
   *
   * @param borderRightStyle
   *          the border style for the right edge or null, if the style should remain undefined.
   */
  public void setBorderRightStyle( final BorderStyle borderRightStyle ) {
    this.borderRightStyle = borderRightStyle;
  }

  /**
   * Returns the defined break border-style for the element. If the border-style is set to NONE or undefined, the
   * border-size property will be ignored and no border is rendered.
   *
   * @return the border style for the break edge or null, if the style should remain undefined.
   */
  public BorderStyle getBorderBreakStyle() {
    return borderBreakStyle;
  }

  /**
   * Defines the break border-style for the element. If the border-style is set to NONE or undefined, the border-size
   * property will be ignored and no border is rendered.
   *
   * @param borderBreakStyle
   *          the border style for the break edge or null, if the style should remain undefined.
   */
  public void setBorderBreakStyle( final BorderStyle borderBreakStyle ) {
    this.borderBreakStyle = borderBreakStyle;
  }

  /**
   * Returns the defined top border-color.
   *
   * @return the color for the top edge or null, if the value should be left undefined here.
   */
  public Color getBorderTopColor() {
    return borderTopColor;
  }

  /**
   * Defines the top border-color.
   *
   * @param borderTopColor
   *          the color for the top edge or null, if the value should be left undefined here.
   */
  public void setBorderTopColor( final Color borderTopColor ) {
    this.borderTopColor = borderTopColor;
  }

  /**
   * Returns the defined left border-color.
   *
   * @return the color for the left edge or null, if the value should be left undefined here.
   */
  public Color getBorderLeftColor() {
    return borderLeftColor;
  }

  /**
   * Defines the left border-color.
   *
   * @param borderLeftColor
   *          the color for the left edge or null, if the value should be left undefined here.
   */
  public void setBorderLeftColor( final Color borderLeftColor ) {
    this.borderLeftColor = borderLeftColor;
  }

  /**
   * Returns the defined bottom border-color.
   *
   * @return the color for the bottom edge or null, if the value should be left undefined here.
   */
  public Color getBorderBottomColor() {
    return borderBottomColor;
  }

  /**
   * Defines the bottom border-color.
   *
   * @param borderBottomColor
   *          the color for the bottom edge or null, if the value should be left undefined here.
   */
  public void setBorderBottomColor( final Color borderBottomColor ) {
    this.borderBottomColor = borderBottomColor;
  }

  /**
   * Returns the defined right border-color.
   *
   * @return the color for the right edge or null, if the value should be left undefined here.
   */
  public Color getBorderRightColor() {
    return borderRightColor;
  }

  /**
   * Defines the right border-color.
   *
   * @param borderRightColor
   *          the color for the right edge or null, if the value should be left undefined here.
   */
  public void setBorderRightColor( final Color borderRightColor ) {
    this.borderRightColor = borderRightColor;
  }

  /**
   * Returns the defined break border-color.
   *
   * @return the color for the break edge or null, if the value should be left undefined here.
   */
  public Color getBorderBreakColor() {
    return borderBreakColor;
  }

  /**
   * Defines the break border-color.
   *
   * @param borderBreakColor
   *          the color for the break edge or null, if the value should be left undefined here.
   */
  public void setBorderBreakColor( final Color borderBreakColor ) {
    this.borderBreakColor = borderBreakColor;
  }

  /**
   * Returns the defined top padding of this box. Padding defines the empty area between the border and the content of
   * an element. Paddings cannot have relative sizes.
   *
   * @return the padding or null, if the padding remains undefined here.
   */
  public Float getPaddingTop() {
    return paddingTop;
  }

  /**
   * Defines the top padding of this box. Padding defines the empty area between the border and the content of an
   * element. Paddings cannot have relative sizes.
   *
   * @param paddingTop
   *          the padding or null, if the padding remains undefined here.
   */
  public void setPaddingTop( final Float paddingTop ) {
    this.paddingTop = paddingTop;
  }

  /**
   * Returns the defined left padding of this box. Padding defines the empty area between the border and the content of
   * an element. Paddings cannot have relative sizes.
   *
   * @return the padding or null, if the padding remains undefined here.
   */
  public Float getPaddingLeft() {
    return paddingLeft;
  }

  /**
   * Defines the left padding of this box. Padding defines the empty area between the border and the content of an
   * element. Paddings cannot have relative sizes.
   *
   * @param paddingLeft
   *          the padding or null, if the padding remains undefined here.
   */
  public void setPaddingLeft( final Float paddingLeft ) {
    this.paddingLeft = paddingLeft;
  }

  /**
   * Returns the defined bottom padding of this box. Padding defines the empty area between the border and the content
   * of an element. Paddings cannot have relative sizes.
   *
   * @return the padding or null, if the padding remains undefined here.
   */
  public Float getPaddingBottom() {
    return paddingBottom;
  }

  /**
   * Defines the bottom padding of this box. Padding defines the empty area between the border and the content of an
   * element. Paddings cannot have relative sizes.
   *
   * @param paddingBottom
   *          the padding or null, if the padding remains undefined here.
   */
  public void setPaddingBottom( final Float paddingBottom ) {
    this.paddingBottom = paddingBottom;
  }

  /**
   * Returns the defined right padding of this box. Padding defines the empty area between the border and the content of
   * an element. Paddings cannot have relative sizes.
   *
   * @return the padding or null, if the padding remains undefined here.
   */
  public Float getPaddingRight() {
    return paddingRight;
  }

  /**
   * Defines the right padding of this box. Padding defines the empty area between the border and the content of an
   * element. Paddings cannot have relative sizes.
   *
   * @param paddingRight
   *          the padding or null, if the padding remains undefined here.
   */
  public void setPaddingRight( final Float paddingRight ) {
    this.paddingRight = paddingRight;
  }

  /**
   * Returns the defined number of widow-lines in this element. This avoids pagebreaks inside the first number of lines
   * of a paragraph. If a pagebreak would occur inside the widow-segment, the whole box will be shifted to the next
   * page.
   *
   * @return the number of widow-lines that control the pagebreak inside the paragraph.
   */
  public Integer getWidows() {
    return widows;
  }

  /**
   * Defines the number of widow-lines in this element. This avoids pagebreaks inside the first number of lines of a
   * paragraph. If a pagebreak would occur inside the widow-segment, the whole box will be shifted to the next page.
   *
   * @param widows
   *          the number of widow-lines that control the pagebreak inside the paragraph.
   */
  public void setWidows( final Integer widows ) {
    this.widows = widows;
  }

  /**
   * Returns the defined number of orphan-lines in this element. This avoids pagebreaks inside the last number of lines
   * of a paragraph. If a pagebreak would occur inside the orphan-segment, the whole number of orphan lines or the whole
   * box will be shifted to the next page.
   *
   * @return the number of orphan-lines that control the pagebreak inside the paragraph.
   */
  public Integer getOrphans() {
    return orphans;
  }

  /**
   * Defines the number of orphan-lines in this element. This avoids pagebreaks inside the last number of lines of a
   * paragraph. If a pagebreak would occur inside the orphan-segment, the whole number of orphan lines or the whole box
   * will be shifted to the next page.
   *
   * @param orphans
   *          the number of orphan-lines that control the pagebreak inside the paragraph.
   */
  public void setOrphans( final Integer orphans ) {
    this.orphans = orphans;
  }

  public Boolean getWidowOrphanOptOut() {
    return widowOrphanOptOut;
  }

  public void setWidowOrphanOptOut( final Boolean widowOrphanOptOut ) {
    this.widowOrphanOptOut = widowOrphanOptOut;
  }

  /**
   * Returns the defined background color of the box.
   *
   * @return the background color or null, if undefined.
   */
  public Color getBackgroundColor() {
    return backgroundColor;
  }

  /**
   * Defines the background color of the box.
   *
   * @param backgroundColor
   *          the background color or null, if undefined.
   */
  public void setBackgroundColor( final Color backgroundColor ) {
    this.backgroundColor = backgroundColor;
  }

  /**
   * Returns, whether text contained in this element will overflow vertically. This will generate overlapping text in
   * the pageable outputs without increasing the total size of the element. Activating this property may cause rendering
   * artifacts.
   *
   * @return a boolean whether text can overflow the box boundaries or null, if this property is left undefined.
   */
  public Boolean getOverflowY() {
    return overflowY;
  }

  /**
   * Defines, whether text contained in this element will overflow vertically. This will generate overlapping text in
   * the pageable outputs without increasing the total size of the element. Activating this property may cause rendering
   * artifacts.
   *
   * @param overflowY
   *          defines, whether text can overflow the box boundaries or null, to leave this property undefined.
   */
  public void setOverflowY( final Boolean overflowY ) {
    this.overflowY = overflowY;
  }

  /**
   * Returns, whether text contained in this element will overflow horizontally. This will generate overlapping text in
   * the pageable outputs without increasing the total size of the element. Activating this property may cause rendering
   * artifacts.
   *
   * @return a boolean whether text can overflow the box boundaries or null, if this property is left undefined.
   */
  public Boolean getOverflowX() {
    return overflowX;
  }

  /**
   * Defines, whether text contained in this element will overflow horizontally. This will generate overlapping text in
   * the pageable outputs without increasing the total size of the element. Activating this property may cause rendering
   * artifacts.
   *
   * @param overflowX
   *          defines, whether text can overflow the box boundaries or null, to leave this property undefined.
   */
  public void setOverflowX( final Boolean overflowX ) {
    this.overflowX = overflowX;
  }

  /**
   * Returns whether the layouter will try to avoid to generate pagebreaks inside this element. If the element does not
   * fit on the current page, it will be moved to the next page. Only if this next page does not have enough space to
   * hold this element, a pagebreak will be generated inside the element.
   *
   * @return a boolean defining whether pagebreaks are allowed inside the box or null, if this property has been left
   *         undefined.
   */
  public Boolean getAvoidPagebreaks() {
    return avoidPagebreaks;
  }

  /**
   * Defines whether the layouter will try to avoid to generate pagebreaks inside this element. If the element does not
   * fit on the current page, it will be moved to the next page. Only if this next page does not have enough space to
   * hold this element, a pagebreak will be generated inside the element.
   *
   * @param avoidPagebreaks
   *          a boolean defining whether pagebreaks are allowed inside the box or null, if this property should be left
   *          undefined.
   */
  public void setAvoidPagebreaks( final Boolean avoidPagebreaks ) {
    this.avoidPagebreaks = avoidPagebreaks;
  }

  /**
   * Returns the link target for this element. The link-target usually specifies the URL for a hyper-link.
   *
   * @return the link target.
   */
  public String getHRefTarget() {
    return hRefTarget;
  }

  /**
   * Defines the link target for the element. The link-target usually specifies the URL for a hyper-link.
   *
   * @param hRefTarget
   *          the link target.
   */
  public void setHRefTarget( final String hRefTarget ) {
    this.hRefTarget = hRefTarget;
  }

  /**
   * Returns the name of the new element.
   *
   * @return the name of the element.
   */
  public String getName() {
    return name;
  }

  /**
   * Defines the name of the element. If the name is null, the default (anonymous) name will be used.
   *
   * @param name
   *          the element name.
   */
  public void setName( final String name ) {
    this.name = name;
  }

  /**
   * Returns the element's minimum size.
   *
   * @return the element's minimum size.
   * @see ElementFactory#getMinimumWidth()
   * @see ElementFactory#getMinimumHeight()
   */
  public Dimension2D getMinimumSize() {
    if ( minimumWidth == null && minimumHeight == null ) {
      return null;
    }
    if ( minimumWidth == null ) {
      return new FloatDimension( 0, minimumHeight.floatValue() );
    }
    if ( minimumHeight == null ) {
      return new FloatDimension( minimumWidth.floatValue(), 0 );
    }
    return new FloatDimension( minimumWidth.floatValue(), minimumHeight.floatValue() );
  }

  /**
   * Defines the element's minimum size.
   *
   * @param minimumSize
   *          the element's minimum size.
   * @see ElementFactory#setMinimumWidth(Float)
   * @see ElementFactory#setMinimumHeight(Float)
   */
  public void setMinimumSize( final Dimension2D minimumSize ) {
    if ( minimumSize == null ) {
      this.minimumWidth = null;
      this.minimumHeight = null;
    } else {
      this.minimumWidth = new Float( minimumSize.getWidth() );
      this.minimumHeight = new Float( minimumSize.getHeight() );
    }
  }

  /**
   * Returns the element's maximum size.
   *
   * @return the element's maximum size.
   * @see ElementFactory#getMinimumWidth()
   * @see ElementFactory#getMinimumHeight()
   */
  public Dimension2D getMaximumSize() {
    if ( maximumWidth == null && maximumHeight == null ) {
      return null;
    }
    if ( maximumWidth == null ) {
      return new FloatDimension( 0, maximumHeight.floatValue() );
    }
    if ( maximumHeight == null ) {
      return new FloatDimension( maximumWidth.floatValue(), 0 );
    }
    return new FloatDimension( maximumWidth.floatValue(), maximumHeight.floatValue() );
  }

  /**
   * Defines the element's maximum size.
   *
   * @param maximumSize
   *          the element's maximum size.
   * @see ElementFactory#setMaximumWidth(Float)
   * @see ElementFactory#setMaximumHeight(Float)
   */
  public void setMaximumSize( final Dimension2D maximumSize ) {
    if ( maximumSize == null ) {
      this.maximumWidth = null;
      this.maximumHeight = null;
    } else {
      this.maximumWidth = new Float( maximumSize.getWidth() );
      this.maximumHeight = new Float( maximumSize.getHeight() );
    }
  }

  /**
   * Returns the element's preferred size.
   *
   * @return the element's preferred size.
   * @see ElementFactory#getWidth()
   * @see ElementFactory#getHeight()
   */
  public Dimension2D getPreferredSize() {
    if ( width == null && height == null ) {
      return null;
    }
    if ( width == null ) {
      return new FloatDimension( 0, height.floatValue() );
    }
    if ( height == null ) {
      return new FloatDimension( width.floatValue(), 0 );
    }
    return new FloatDimension( width.floatValue(), height.floatValue() );
  }

  /**
   * Returns the element's preferred size.
   *
   * @param preferredSize
   *          the element's preferred size.
   * @see ElementFactory#setWidth(Float)
   * @see ElementFactory#setHeight(Float)
   */
  public void setPreferredSize( final Dimension2D preferredSize ) {
    if ( preferredSize == null ) {
      this.width = null;
      this.height = null;
    } else {
      this.width = new Float( preferredSize.getWidth() );
      this.height = new Float( preferredSize.getHeight() );
    }
  }

  /**
   * Returns the element's absolute position. This property is only used if the band containing this element uses a
   * canvas-layout strategy.
   *
   * @return the element's absolute position.
   * @see ElementFactory#getX()
   * @see ElementFactory#getY()
   */
  public Point2D getAbsolutePosition() {
    if ( x == null && y == null ) {
      return null;
    }
    if ( x == null ) {
      return new Point2D.Float( 0, y.floatValue() );
    }
    if ( y == null ) {
      return new Point2D.Float( x.floatValue(), 0 );
    }
    return new Point2D.Float( x.floatValue(), y.floatValue() );
  }

  /**
   * Returns the element's absolute position. This property is only used if the band containing this element uses a
   * canvas-layout strategy.
   *
   * @param absolutePosition
   *          the element's absolute position.
   * @see ElementFactory#setX(Float)
   * @see ElementFactory#setY(Float)
   */
  public void setAbsolutePosition( final Point2D absolutePosition ) {
    if ( absolutePosition == null ) {
      this.x = null;
      this.y = null;
    } else {
      this.x = new Float( absolutePosition.getX() );
      this.y = new Float( absolutePosition.getY() );
    }
  }

  /**
   * Returns whether the element's height should be adjusted automaticly.
   *
   * @return the state of the dynamic feature or null, if the feature is undefined.
   */
  public Boolean getDynamicHeight() {
    return dynamicHeight;
  }

  /**
   * Defines whether the element's height should be adjusted automaticly.
   *
   * @param dynamicHeight
   *          the new value of the elements dynamic height feature.
   */
  public void setDynamicHeight( final Boolean dynamicHeight ) {
    this.dynamicHeight = dynamicHeight;
  }

  /**
   * Returns, whether the layout for the element is cachable. If you intend to modify the element's properties from
   * within a function, you should mark the element as non-cachable, or the layout may look funny.
   * <p/>
   * This property is no longer used. This method will be removed in version 1.0.
   *
   * @return the layout-cachable flag.
   * @deprecated The layout cachable flag is no longer used.
   */
  public Boolean getLayoutCachable() {
    return null;
  }

  /**
   * Returns, whether the layout for the element is cachable. If you intend to modify the element's properties from
   * within a function, you should mark the element as non-cachable, or the layout may look funny. Set this value to
   * <code>null</code> if this value should be inherited from the parent.
   * <p/>
   * This property is no longer used. This method will be removed in version 1.0.
   *
   * @param layoutCachable
   *          the layout-cachable flag.
   * @deprecated The layout cachable flag is no longer used.
   */
  public void setLayoutCachable( final Boolean layoutCachable ) {
    // does nothing.
  }

  /**
   * Returns, whether the element will be visible.
   *
   * @return the visibility of the element.
   */
  public Boolean getVisible() {
    return visible;
  }

  /**
   * Defines, whether the element will be visible.
   *
   * @param visible
   *          the visibility flag of the element.
   */
  public void setVisible( final Boolean visible ) {
    this.visible = visible;
  }

  /**
   * Applies the defined name to the created element.
   *
   * @param e
   *          the element which was created.
   */
  protected void applyElementName( final Element e ) {
    if ( getName() != null ) {
      e.setName( getName() );
    }
  }

  /**
   * Returns the 'window' parameter for hyperlink references. This property will only make sense in HTML-exports, as all
   * other export targets that support Hyperlinks will open up in a new window anyway.
   *
   * @return the href-window string.
   */
  public String getHRefWindow() {
    return hRefWindow;
  }

  /**
   * Defines the 'window' parameter for hyperlink references. This property will only make sense in HTML-exports, as all
   * other export targets that support Hyperlinks will open up in a new window anyway.
   *
   * @param hRefWindow
   *          the href-window string.
   */
  public void setHRefWindow( final String hRefWindow ) {
    this.hRefWindow = hRefWindow;
  }

  /**
   * Returns the elements minimum width. A number between 0 and -100 specifies the width as relative size given in
   * percent of the parent's width or height (depending on the layout model of the band that contains this element).
   *
   * @return the minimum width or null, if no minimum width is defined.
   */
  public Float getMinimumWidth() {
    return minimumWidth;
  }

  /**
   * Defines the elements minimum width. A number between 0 and -100 specifies the width as relative size given in
   * percent of the parent's width or height (depending on the layout model of the band that contains this element).
   *
   * @param minimumWidth
   *          the minimum width or null, to leave this property undefined.
   */
  public void setMinimumWidth( final Float minimumWidth ) {
    this.minimumWidth = minimumWidth;
  }

  /**
   * Returns the elements minimum height. A number between 0 and -100 specifies the width as relative size given in
   * percent of the parent's width or height (depending on the layout model of the band that contains this element).
   *
   * @return the minimum height or null, if no minimum height is defined.
   */
  public Float getMinimumHeight() {
    return minimumHeight;
  }

  /**
   * Defines the elements minimum height. A number between 0 and -100 specifies the width as relative size given in
   * percent of the parent's width or height (depending on the layout model of the band that contains this element).
   *
   * @param minimumHeight
   *          the minimum height or null, to leave this property undefined.
   */
  public void setMinimumHeight( final Float minimumHeight ) {
    this.minimumHeight = minimumHeight;
  }

  /**
   * Returns the elements maximum width. The maximum width cannot have relative values.
   *
   * @return the maximum width or null, if no maximum width is defined.
   */
  public Float getMaximumWidth() {
    return maximumWidth;
  }

  /**
   * Defines the elements maximum width. The maximum width cannot have relative values.
   *
   * @param maximumWidth
   *          the maximum width or null, if no maximum width should be defined.
   */
  public void setMaximumWidth( final Float maximumWidth ) {
    this.maximumWidth = maximumWidth;
  }

  /**
   * Returns the elements maximum height. The maximum height cannot have relative values.
   *
   * @return the maximum height or null, if no maximum height is defined.
   */
  public Float getMaximumHeight() {
    return maximumHeight;
  }

  /**
   * Defines the elements maximum height. The maximum height cannot have relative values.
   *
   * @param maximumHeight
   *          the maximum width or null, if no maximum height should be defined.
   */
  public void setMaximumHeight( final Float maximumHeight ) {
    this.maximumHeight = maximumHeight;
  }

  /**
   * Returns the elements defined preferred width. A number between 0 and -100 specifies the width as relative size
   * given in percent of the parent's width or height (depending on the layout model of the band that contains this
   * element).
   *
   * @return the preferred width or null, if left undefined.
   */
  public Float getWidth() {
    return width;
  }

  /**
   * Defines the elements preferred width. A number between 0 and -100 specifies the width as relative size given in
   * percent of the parent's width or height (depending on the layout model of the band that contains this element).
   *
   * @param width
   *          the preferred width or null, if left undefined.
   */
  public void setWidth( final Float width ) {
    this.width = width;
  }

  /**
   * Returns the elements defined preferred height. A number between 0 and -100 specifies the width as relative size
   * given in percent of the parent's width or height (depending on the layout model of the band that contains this
   * element).
   *
   * @return the preferred height or null, if left undefined.
   */
  public Float getHeight() {
    return height;
  }

  /**
   * Defines the elements preferred height. A number between 0 and -100 specifies the width as relative size given in
   * percent of the parent's width or height (depending on the layout model of the band that contains this element).
   *
   * @param height
   *          the preferred height or null, if left undefined.
   */
  public void setHeight( final Float height ) {
    this.height = height;
  }

  /**
   * Returns the elements defined absolute horizontal position for the canvas-layout. The position is relative to the
   * parents upper left corner of the content-area. A number between 0 and -100 specifies the width as relative size
   * given in percent of the parent's width or height (depending on the layout model of the band that contains this
   * element).
   *
   * @return the elements horizontal position or null, if not defined.
   */
  public Float getX() {
    return x;
  }

  /**
   * Defines the elements absolute horizontal position for the canvas-layout. The position is relative to the parents
   * upper left corner of the content-area. A number between 0 and -100 specifies the width as relative size given in
   * percent of the parent's width or height (depending on the layout model of the band that contains this element).
   *
   * @param x
   *          the elements horizontal position or null, if not defined.
   */
  public void setX( final Float x ) {
    this.x = x;
  }

  /**
   * Returns the elements defined absolute vertical position for the canvas-layout. The position is relative to the
   * parents upper left corner of the content-area. A number between 0 and -100 specifies the width as relative size
   * given in percent of the parent's width or height (depending on the layout model of the band that contains this
   * element).
   *
   * @return the elements vertical position or null, if not defined.
   */
  public Float getY() {
    return y;
  }

  /**
   * Defines the elements absolute vertical position for the canvas-layout. The position is relative to the parents
   * upper left corner of the content-area. A number between 0 and -100 specifies the width as relative size given in
   * percent of the parent's width or height (depending on the layout model of the band that contains this element).
   *
   * @param y
   *          the elements vertical position or null, if not defined.
   */
  public void setY( final Float y ) {
    this.y = y;
  }

  /**
   * Returns the defined HREF-Title. This title is only valid during the HTML export.
   *
   * @return the href-title as string.
   */
  public String getHRefTitle() {
    return hRefTitle;
  }

  /**
   * Defines the defined HREF-Title. This title is only valid during the HTML export.
   *
   * @param hRefTitle
   *          the href-title as string.
   */
  public void setHRefTitle( final String hRefTitle ) {
    this.hRefTitle = hRefTitle;
  }

  /**
   * Applies the style definition to the elements stylesheet.
   *
   * @param style
   *          the element stylesheet which should receive the style definition.
   */
  protected void applyStyle( final ElementStyleSheet style ) {
    if ( getUseMinChunkWidth() != null ) {
      style.setStyleProperty( ElementStyleKeys.USE_MIN_CHUNKWIDTH, getUseMinChunkWidth() );
    }
    if ( getX() != null ) {
      style.setStyleProperty( ElementStyleKeys.POS_X, getX() );
    }
    if ( getY() != null ) {
      style.setStyleProperty( ElementStyleKeys.POS_Y, getY() );
    }
    if ( getDynamicHeight() != null ) {
      style.setStyleProperty( ElementStyleKeys.DYNAMIC_HEIGHT, getDynamicHeight() );
    }
    if ( getMaximumWidth() != null ) {
      style.setStyleProperty( ElementStyleKeys.MAX_WIDTH, getMaximumWidth() );
    }
    if ( getMaximumHeight() != null ) {
      style.setStyleProperty( ElementStyleKeys.MAX_HEIGHT, getMaximumHeight() );
    }
    if ( getMinimumWidth() != null ) {
      style.setStyleProperty( ElementStyleKeys.MIN_WIDTH, getMinimumWidth() );
    }
    if ( getMinimumHeight() != null ) {
      style.setStyleProperty( ElementStyleKeys.MIN_HEIGHT, getMinimumHeight() );
    }
    if ( getWidth() != null ) {
      style.setStyleProperty( ElementStyleKeys.WIDTH, getWidth() );
    }
    if ( getHeight() != null ) {
      style.setStyleProperty( ElementStyleKeys.HEIGHT, getHeight() );
    }
    if ( getVisible() != null ) {
      style.setStyleProperty( ElementStyleKeys.VISIBLE, getVisible() );
    }
    if ( getHRefTarget() != null ) {
      style.setStyleProperty( ElementStyleKeys.HREF_TARGET, getHRefTarget() );
    }
    if ( getHRefWindow() != null ) {
      style.setStyleProperty( ElementStyleKeys.HREF_WINDOW, getHRefWindow() );
    }
    if ( getHRefTitle() != null ) {
      style.setStyleProperty( ElementStyleKeys.HREF_TITLE, getHRefTitle() );
    }
    if ( getFontSmooth() != null ) {
      style.setStyleProperty( TextStyleKeys.FONT_SMOOTH, getFontSmooth() );
    }

    if ( getBorderColor() != null ) {
      style.setStyleProperty( ElementStyleKeys.BORDER_TOP_COLOR, getBorderColor() );
      style.setStyleProperty( ElementStyleKeys.BORDER_LEFT_COLOR, getBorderColor() );
      style.setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_COLOR, getBorderColor() );
      style.setStyleProperty( ElementStyleKeys.BORDER_RIGHT_COLOR, getBorderColor() );
      style.setStyleProperty( ElementStyleKeys.BORDER_BREAK_COLOR, getBorderColor() );
    }
    if ( getBorderWidth() != null ) {
      style.setStyleProperty( ElementStyleKeys.BORDER_TOP_WIDTH, getBorderWidth() );
      style.setStyleProperty( ElementStyleKeys.BORDER_LEFT_WIDTH, getBorderWidth() );
      style.setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_WIDTH, getBorderWidth() );
      style.setStyleProperty( ElementStyleKeys.BORDER_RIGHT_WIDTH, getBorderWidth() );
      style.setStyleProperty( ElementStyleKeys.BORDER_BREAK_WIDTH, getBorderWidth() );
    }
    if ( getBorderStyle() != null ) {
      style.setStyleProperty( ElementStyleKeys.BORDER_TOP_STYLE, getBorderStyle() );
      style.setStyleProperty( ElementStyleKeys.BORDER_LEFT_STYLE, getBorderStyle() );
      style.setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_STYLE, getBorderStyle() );
      style.setStyleProperty( ElementStyleKeys.BORDER_RIGHT_STYLE, getBorderStyle() );
      style.setStyleProperty( ElementStyleKeys.BORDER_BREAK_STYLE, getBorderStyle() );
    }
    if ( getPadding() != null ) {
      style.setStyleProperty( ElementStyleKeys.PADDING_TOP, getPadding() );
      style.setStyleProperty( ElementStyleKeys.PADDING_LEFT, getPadding() );
      style.setStyleProperty( ElementStyleKeys.PADDING_BOTTOM, getPadding() );
      style.setStyleProperty( ElementStyleKeys.PADDING_RIGHT, getPadding() );
    }

    if ( borderTopColor != null ) {
      style.setStyleProperty( ElementStyleKeys.BORDER_TOP_COLOR, getBorderTopColor() );
    }
    if ( borderLeftColor != null ) {
      style.setStyleProperty( ElementStyleKeys.BORDER_LEFT_COLOR, getBorderLeftColor() );
    }
    if ( borderBottomColor != null ) {
      style.setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_COLOR, getBorderBottomColor() );
    }
    if ( borderRightColor != null ) {
      style.setStyleProperty( ElementStyleKeys.BORDER_RIGHT_COLOR, getBorderRightColor() );
    }
    if ( borderBreakColor != null ) {
      style.setStyleProperty( ElementStyleKeys.BORDER_BREAK_COLOR, getBorderBreakColor() );
    }
    if ( borderTopWidth != null ) {
      style.setStyleProperty( ElementStyleKeys.BORDER_TOP_WIDTH, getBorderTopWidth() );
    }
    if ( borderLeftWidth != null ) {
      style.setStyleProperty( ElementStyleKeys.BORDER_LEFT_WIDTH, getBorderLeftWidth() );
    }
    if ( borderBottomWidth != null ) {
      style.setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_WIDTH, getBorderBottomWidth() );
    }
    if ( borderRightWidth != null ) {
      style.setStyleProperty( ElementStyleKeys.BORDER_RIGHT_WIDTH, getBorderRightWidth() );
    }
    if ( borderBreakWidth != null ) {
      style.setStyleProperty( ElementStyleKeys.BORDER_BREAK_WIDTH, getBorderBreakWidth() );
    }
    if ( borderTopStyle != null ) {
      style.setStyleProperty( ElementStyleKeys.BORDER_TOP_STYLE, getBorderTopStyle() );
    }
    if ( borderLeftStyle != null ) {
      style.setStyleProperty( ElementStyleKeys.BORDER_LEFT_STYLE, getBorderLeftStyle() );
    }
    if ( borderBottomStyle != null ) {
      style.setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_STYLE, getBorderBottomStyle() );
    }
    if ( borderRightStyle != null ) {
      style.setStyleProperty( ElementStyleKeys.BORDER_RIGHT_STYLE, getBorderRightStyle() );
    }
    if ( borderBreakStyle != null ) {
      style.setStyleProperty( ElementStyleKeys.BORDER_BREAK_STYLE, getBorderBreakStyle() );
    }
    if ( paddingTop != null ) {
      style.setStyleProperty( ElementStyleKeys.PADDING_TOP, getPaddingTop() );
    }
    if ( paddingLeft != null ) {
      style.setStyleProperty( ElementStyleKeys.PADDING_LEFT, getPaddingLeft() );
    }
    if ( paddingBottom != null ) {
      style.setStyleProperty( ElementStyleKeys.PADDING_BOTTOM, getPaddingBottom() );
    }
    if ( paddingRight != null ) {
      style.setStyleProperty( ElementStyleKeys.PADDING_RIGHT, getPaddingRight() );
    }

    if ( backgroundColor != null ) {
      style.setStyleProperty( ElementStyleKeys.BACKGROUND_COLOR, getBackgroundColor() );
    }
    if ( avoidPagebreaks != null ) {
      style.setStyleProperty( ElementStyleKeys.AVOID_PAGEBREAK_INSIDE, getAvoidPagebreaks() );
    }
    if ( orphans != null ) {
      style.setStyleProperty( ElementStyleKeys.ORPHANS, getOrphans() );
    }
    if ( widows != null ) {
      style.setStyleProperty( ElementStyleKeys.WIDOWS, getWidows() );
    }
    if ( widowOrphanOptOut != null ) {
      style.setStyleProperty( ElementStyleKeys.WIDOW_ORPHAN_OPT_OUT, getWidowOrphanOptOut() );
    }
    if ( overflowX != null ) {
      style.setStyleProperty( ElementStyleKeys.OVERFLOW_X, getOverflowX() );
    }
    if ( overflowY != null ) {
      style.setStyleProperty( ElementStyleKeys.OVERFLOW_Y, getOverflowY() );
    }
    if ( fontSmooth != null ) {
      style.setStyleProperty( TextStyleKeys.FONT_SMOOTH, getFontSmooth() );
    }
    if ( horizontalAlignment != null ) {
      style.setStyleProperty( ElementStyleKeys.ALIGNMENT, getHorizontalAlignment() );
    }
    if ( verticalAlignment != null ) {
      style.setStyleProperty( ElementStyleKeys.VALIGNMENT, getVerticalAlignment() );
    }
    if ( color != null ) {
      style.setStyleProperty( ElementStyleKeys.PAINT, getColor() );
    }

    if ( borderRadiusWidth != null ) {
      style.setStyleProperty( ElementStyleKeys.BORDER_TOP_LEFT_RADIUS_WIDTH, getBorderRadiusWidth() );
      style.setStyleProperty( ElementStyleKeys.BORDER_TOP_RIGHT_RADIUS_WIDTH, getBorderRadiusWidth() );
      style.setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_LEFT_RADIUS_WIDTH, getBorderRadiusWidth() );
      style.setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_RIGHT_RADIUS_WIDTH, getBorderRadiusWidth() );
    }
    if ( borderRadiusHeight != null ) {
      style.setStyleProperty( ElementStyleKeys.BORDER_TOP_LEFT_RADIUS_HEIGHT, getBorderRadiusHeight() );
      style.setStyleProperty( ElementStyleKeys.BORDER_TOP_RIGHT_RADIUS_HEIGHT, getBorderRadiusHeight() );
      style.setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_LEFT_RADIUS_HEIGHT, getBorderRadiusHeight() );
      style.setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_RIGHT_RADIUS_HEIGHT, getBorderRadiusHeight() );
    }
    if ( borderTopLeftRadiusWidth != null ) {
      style.setStyleProperty( ElementStyleKeys.BORDER_TOP_LEFT_RADIUS_WIDTH, getBorderTopLeftRadiusWidth() );
    }
    if ( borderTopLeftRadiusHeight != null ) {
      style.setStyleProperty( ElementStyleKeys.BORDER_TOP_LEFT_RADIUS_HEIGHT, getBorderTopLeftRadiusHeight() );
    }
    if ( borderTopRightRadiusWidth != null ) {
      style.setStyleProperty( ElementStyleKeys.BORDER_TOP_RIGHT_RADIUS_WIDTH, getBorderTopRightRadiusWidth() );
    }
    if ( borderTopRightRadiusHeight != null ) {
      style.setStyleProperty( ElementStyleKeys.BORDER_TOP_RIGHT_RADIUS_HEIGHT, getBorderTopRightRadiusHeight() );
    }
    if ( borderBottomLeftRadiusWidth != null ) {
      style.setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_LEFT_RADIUS_WIDTH, getBorderBottomLeftRadiusWidth() );
    }
    if ( borderBottomLeftRadiusHeight != null ) {
      style.setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_LEFT_RADIUS_HEIGHT, getBorderBottomLeftRadiusHeight() );
    }
    if ( borderBottomRightRadiusWidth != null ) {
      style.setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_RIGHT_RADIUS_WIDTH, getBorderBottomRightRadiusWidth() );
    }
    if ( borderBottomRightRadiusHeight != null ) {
      style.setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_RIGHT_RADIUS_HEIGHT, getBorderBottomRightRadiusHeight() );
    }
  }

  /**
   * Creates a new instance of the element. Override this method to return a concrete subclass of the element.
   *
   * @return the newly generated instance of the element.
   */
  public abstract Element createElement();

  /**
   * Converts the given primitive boolean into a Boolean object.
   *
   * @param b
   *          the primitive value.
   * @return Boolean.TRUE or Boolean.FALSE.
   */
  protected static Boolean getBooleanValue( final boolean b ) {
    if ( b ) {
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }

  public void setUseMinChunkWidth( final Boolean useMinChunkWidth ) {
    this.useMinChunkWidth = useMinChunkWidth;
  }

  public Boolean getUseMinChunkWidth() {
    return useMinChunkWidth;
  }
}
