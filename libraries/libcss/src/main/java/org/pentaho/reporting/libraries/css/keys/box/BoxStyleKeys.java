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

package org.pentaho.reporting.libraries.css.keys.box;

import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.model.StyleKeyRegistry;

/**
 * http://www.w3.org/TR/css3-box/
 * <p/>
 * Creation-Date: 27.10.2005, 21:12:57
 *
 * @author Thomas Morgner
 */
public class BoxStyleKeys {
  // /** This complicates the rendering process, without giving any real gain. */
  public static final StyleKey BOX_SIZING =
    StyleKeyRegistry.getRegistry().createKey
      ( "box-sizing", false, false, StyleKey.All_ELEMENTS );
  /**
   * These values may be auto. If specified, this overrides the width.
   */
  public static final StyleKey BOX_WIDTH =
    StyleKeyRegistry.getRegistry().createKey
      ( "box-width", false, false, StyleKey.All_ELEMENTS );

  /**
   * These values may be auto. If specified, this overrides the height.
   */
  public static final StyleKey BOX_HEIGHT =
    StyleKeyRegistry.getRegistry().createKey
      ( "box-height", false, false, StyleKey.All_ELEMENTS );

  /**
   * The 'display-model' property determines the algorithm with which an element lays out its children.
   */
  public static final StyleKey DISPLAY_MODEL =
    StyleKeyRegistry.getRegistry().createKey
      ( "display-model", false, false, StyleKey.All_ELEMENTS );

  /**
   * 'display-role' specifies what role an element has in its parent's algorithm.
   */
  public static final StyleKey DISPLAY_ROLE =
    StyleKeyRegistry.getRegistry().createKey
      ( "display-role", false, false, StyleKey.All_ELEMENTS );

  /**
   * Set the padding around the content of a box. Padding is between content and border. Background expands over the
   * padding up to the border.
   * <p/>
   * Values given may not be negative. If percentages are given, all paddings are relative to the <strong>width</strong>
   * of the parent (if the flow is horizontal, else the height is used).
   */
  public static final StyleKey PADDING_TOP =
    StyleKeyRegistry.getRegistry().createKey
      ( "padding-top", false, false, StyleKey.All_ELEMENTS | StyleKey.MARGINS );

  public static final StyleKey PADDING_LEFT =
    StyleKeyRegistry.getRegistry().createKey
      ( "padding-left", false, false, StyleKey.All_ELEMENTS | StyleKey.MARGINS );

  public static final StyleKey PADDING_BOTTOM =
    StyleKeyRegistry.getRegistry().createKey
      ( "padding-bottom", false, false, StyleKey.All_ELEMENTS | StyleKey.MARGINS );

  public static final StyleKey PADDING_RIGHT =
    StyleKeyRegistry.getRegistry().createKey
      ( "padding-right", false, false, StyleKey.All_ELEMENTS | StyleKey.MARGINS );

  /** The border is defined in the border module. */

  /**
   * These properties set the thickness of the margin. The value may be negative, but the UA may impose a lower bound.
   * <p/>
   * 'Margin' is a shorthand to set top, right, bottom and left together. If four values are given, they set top, right,
   * bottom and left in that order. If left is omitted, it is the same as right. If bottom is omitted, it is the same as
   * top. If right is omitted it is the same as top.
   * <p/>
   * The meaning of 'auto' on 'margin-left' '-right', '-top' and '-bottom' is as follows:
   * <p/>
   * on floating and inline-level elements, 'auto' is equal to 0 on positioned elements: see the positioning module[ref]
   * on normal-flow elements, if the containing block is horizontal: o 'auto' on 'margin-top' and 'margin-bottom' is
   * equal to 0 o on 'margin-right' and 'margin-left': see equation (1) below on normal-flow elements, if the containing
   * block is vertical : o 'auto' on 'margin-right' and 'margin-left' is equal to 0 o on 'margin-top' and
   * 'margin-bottom': see equation (1) below
   * <p/>
   * Margins must satisfy certain constraints, which means that the computed value may be different from the specified
   * value. See equation (1) below.
   * <p/>
   * Note that in a horizontal flow, percentages on 'margin-top' and 'margin-bottom' are relative to the width of the
   * containing block, not the height (and in vertical flow, 'margin-left' and 'margin-right' are relative to the
   * height, not the width).
   * <p/>
   * Note that 'margin-top' and 'margin-bottom' do not apply to non-replaced, inline elements (in horizontal flow); see
   * [CSS3LINE].
   */
  public static final StyleKey MARGIN_TOP =
    StyleKeyRegistry.getRegistry().createKey
      ( "margin-top", false, false, StyleKey.All_ELEMENTS | StyleKey.MARGINS );

  public static final StyleKey MARGIN_LEFT =
    StyleKeyRegistry.getRegistry().createKey
      ( "margin-left", false, false, StyleKey.All_ELEMENTS | StyleKey.MARGINS );

  public static final StyleKey MARGIN_BOTTOM =
    StyleKeyRegistry.getRegistry().createKey
      ( "margin-bottom", false, false, StyleKey.All_ELEMENTS | StyleKey.MARGINS );

  public static final StyleKey MARGIN_RIGHT =
    StyleKeyRegistry.getRegistry().createKey
      ( "margin-right", false, false, StyleKey.All_ELEMENTS | StyleKey.MARGINS );

  /**
   * These values may be auto.
   */
  public static final StyleKey WIDTH =
    StyleKeyRegistry.getRegistry().createKey
      ( "width", false, false, StyleKey.All_ELEMENTS | StyleKey.MARGINS );

  /**
   * These values may be auto.
   */
  public static final StyleKey HEIGHT =
    StyleKeyRegistry.getRegistry().createKey
      ( "height", false, false, StyleKey.All_ELEMENTS | StyleKey.MARGINS );

  // The Box-Sizing property is not implemented here.

  public static final StyleKey MAX_WIDTH =
    StyleKeyRegistry.getRegistry().createKey
      ( "max-width", false, false, StyleKey.All_ELEMENTS | StyleKey.MARGINS );

  public static final StyleKey MAX_HEIGHT =
    StyleKeyRegistry.getRegistry().createKey
      ( "max-height", false, false, StyleKey.All_ELEMENTS | StyleKey.MARGINS );

  public static final StyleKey MIN_WIDTH =
    StyleKeyRegistry.getRegistry().createKey
      ( "min-width", false, false, StyleKey.All_ELEMENTS | StyleKey.MARGINS );

  public static final StyleKey MIN_HEIGHT =
    StyleKeyRegistry.getRegistry().createKey
      ( "min-height", false, false, StyleKey.All_ELEMENTS | StyleKey.MARGINS );


  public static final StyleKey FIT =
    StyleKeyRegistry.getRegistry().createKey
      ( "fit", false, true, StyleKey.All_ELEMENTS | StyleKey.MARGINS );

  /**
   * A pair of values.
   */
  public static final StyleKey FIT_POSITION =
    StyleKeyRegistry.getRegistry().createKey
      ( "fit-position", false, true, StyleKey.All_ELEMENTS | StyleKey.MARGINS );

  /**
   * We alter the semantics a bit. When crop is computed, absolute values are used. Inset-Rect is voodoo, so ignore it
   * for now.
   */
  public static final StyleKey CROP =
    StyleKeyRegistry.getRegistry().createKey
      ( "crop", false, false, StyleKey.All_ELEMENTS | StyleKey.MARGINS );

  public static final StyleKey FLOAT =
    StyleKeyRegistry.getRegistry().createKey
      ( "float", false, false, StyleKey.All_ELEMENTS );
  //  /**
  //   * Optional: (The old idea from 1996; allow shapes).
  //   * Do not implement it for now, assume 'box'
  //   */
  //  public static final StyleKey FLOAT_TYPE =
  //          StyleKeyRegistry.getRegistry().createKey
  //                  ("float-type", false, false, false);

  public static final StyleKey CLEAR =
    StyleKeyRegistry.getRegistry().createKey
      ( "clear", false, false, StyleKey.All_ELEMENTS );

  //  public static final StyleKey X_CLEAR_LEFT =
  //          StyleKeyRegistry.getRegistry().createKey
  //                  ("-x-pentaho-css-clear-left", true, false, false);
  //  public static final StyleKey X_CLEAR_RIGHT =
  //          StyleKeyRegistry.getRegistry().createKey
  //                  ("-x-pentaho-css-clear-right", true, false, false);
  //  public static final StyleKey X_CLEAR_AFTER_LEFT =
  //          StyleKeyRegistry.getRegistry().createKey
  //                  ("-x-pentaho-css-clear-after-left", true, false, false);
  //  public static final StyleKey X_CLEAR_AFTER_RIGHT =
  //          StyleKeyRegistry.getRegistry().createKey
  //                  ("-x-pentaho-css-clear-after-right", true, false, false);

  public static final StyleKey CLEAR_AFTER =
    StyleKeyRegistry.getRegistry().createKey
      ( "clear-after", false, false, StyleKey.All_ELEMENTS );

  public static final StyleKey FLOAT_DISPLACE =
    StyleKeyRegistry.getRegistry().createKey
      ( "float-displace", false, true, StyleKey.All_ELEMENTS );

  public static final StyleKey INDENT_EDGE_RESET =
    StyleKeyRegistry.getRegistry().createKey
      ( "indent-edge-reset", false, false, StyleKey.All_ELEMENTS );

  /**
   * The plain overflow behaviour can be constructed using these both properties.
   */
  public static final StyleKey OVERFLOW_X =
    StyleKeyRegistry.getRegistry().createKey
      ( "overflow-x", false, false, StyleKey.BLOCK_ELEMENTS );

  public static final StyleKey OVERFLOW_Y =
    StyleKeyRegistry.getRegistry().createKey
      ( "overflow-y", false, false, StyleKey.BLOCK_ELEMENTS );
  // todo...

  public static final StyleKey OVERFLOW_CLIP =
    StyleKeyRegistry.getRegistry().createKey
      ( "overflow-clip", false, false, StyleKey.BLOCK_ELEMENTS );


  public static final StyleKey VISIBILITY =
    StyleKeyRegistry.getRegistry().createKey
      ( "visibility", false, true, StyleKey.All_ELEMENTS );

  //  /**
  //   * Page 664 of the OpenOffice file format specs:
  //   * (This is a similiar effect as the table-cell alignment)
  //   * <p/>
  //   * Possible values: top, middle, bottom, justify
  //   * (Not yet used?)
  //   */
  //  public static final StyleKey BOX_VERTICAL_ALIGN =
  //      StyleKeyRegistry.getRegistry().createKey
  //          ("-x-pentaho-css-box-vertical-align",
  //              false, true, StyleKey.All_ELEMENTS);

  private BoxStyleKeys() {
  }

}
