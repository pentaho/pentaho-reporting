/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.libraries.css.keys.border;

import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.model.StyleKeyRegistry;

/**
 * http://www.w3.org/TR/css3-border/
 * <p/>
 * Creation-Date: 27.10.2005, 21:40:14
 * <p/>
 * Border-breaks are specified using single values. The CSS3 specification does not define explicit properties for the
 * break-borders, but using the composite definition is ugly.
 *
 * @author Thomas Morgner
 */
public class BorderStyleKeys {
  private BorderStyleKeys() {
  }

  public static final StyleKey BACKGROUND_COLOR =
    StyleKeyRegistry.getRegistry().createKey
      ( "background-color", false, false, StyleKey.All_ELEMENTS );

  /**
   * This expects a list of images. How to handle that?
   */
  public static final StyleKey BACKGROUND_IMAGE =
    StyleKeyRegistry.getRegistry().createKey
      ( "background-image", false, false, StyleKey.All_ELEMENTS );

  public static final StyleKey BACKGROUND_REPEAT =
    StyleKeyRegistry.getRegistry().createKey
      ( "background-repeat", false, false, StyleKey.All_ELEMENTS );

  /**
   * BackgroundAttachment needs scrolling, and thus we do not implement this style-attribute yet.
   */
  public static final StyleKey BACKGROUND_ATTACHMENT =
    StyleKeyRegistry.getRegistry().createKey
      ( "background-attachment", false, false, StyleKey.All_ELEMENTS );

  /**
   * The position is always specified in numeric values. The constants are mapped by the parser.
   */
  public static final StyleKey BACKGROUND_POSITION =
    StyleKeyRegistry.getRegistry().createKey
      ( "background-position", false, false, StyleKey.All_ELEMENTS );

  public static final StyleKey BACKGROUND_ORIGIN =
    StyleKeyRegistry.getRegistry().createKey
      ( "background-origin", false, false, StyleKey.All_ELEMENTS );

  public static final StyleKey BACKGROUND_CLIP =
    StyleKeyRegistry.getRegistry().createKey
      ( "background-clip", false, false, StyleKey.All_ELEMENTS );

  public static final StyleKey BACKGROUND_SIZE =
    StyleKeyRegistry.getRegistry().createKey
      ( "background-size", false, false, StyleKey.All_ELEMENTS );

  public static final StyleKey BACKGROUND_BREAK =
    StyleKeyRegistry.getRegistry().createKey
      ( "background-break", false, false, StyleKey.All_ELEMENTS );


  public static final StyleKey BORDER_IMAGE =
    StyleKeyRegistry.getRegistry().createKey
      ( "border-image", false, false, StyleKey.All_ELEMENTS );

  /**
   * Set the border around the content and padding of a box. Padding is between content and border. Background expands
   * over the padding up to the border.
   * <p/>
   * Values given may not be negative. If percentages are given, all paddings are relative to the <strong>width</strong>
   * of the parent (if the flow is horizontal, else the height is used).
   */
  public static final StyleKey BORDER_TOP_WIDTH =
    StyleKeyRegistry.getRegistry().createKey
      ( "border-top-width", false, false, StyleKey.All_ELEMENTS );

  public static final StyleKey BORDER_LEFT_WIDTH =
    StyleKeyRegistry.getRegistry().createKey
      ( "border-left-width", false, false, StyleKey.All_ELEMENTS );

  public static final StyleKey BORDER_BOTTOM_WIDTH =
    StyleKeyRegistry.getRegistry().createKey
      ( "border-bottom-width", false, false, StyleKey.All_ELEMENTS );

  public static final StyleKey BORDER_RIGHT_WIDTH =
    StyleKeyRegistry.getRegistry().createKey
      ( "border-right-width", false, false, StyleKey.All_ELEMENTS );

  public static final StyleKey BORDER_TOP_COLOR =
    StyleKeyRegistry.getRegistry().createKey
      ( "border-top-color", false, false, StyleKey.All_ELEMENTS );

  public static final StyleKey BORDER_LEFT_COLOR =
    StyleKeyRegistry.getRegistry().createKey
      ( "border-left-color", false, false, StyleKey.All_ELEMENTS );

  public static final StyleKey BORDER_BOTTOM_COLOR =
    StyleKeyRegistry.getRegistry().createKey
      ( "border-bottom-color", false, false, StyleKey.All_ELEMENTS );

  public static final StyleKey BORDER_RIGHT_COLOR =
    StyleKeyRegistry.getRegistry().createKey
      ( "border-right-color", false, false, StyleKey.All_ELEMENTS );

  public static final StyleKey BORDER_TOP_STYLE =
    StyleKeyRegistry.getRegistry().createKey
      ( "border-top-style", false, false, StyleKey.All_ELEMENTS );

  public static final StyleKey BORDER_LEFT_STYLE =
    StyleKeyRegistry.getRegistry().createKey
      ( "border-left-style", false, false, StyleKey.All_ELEMENTS );

  public static final StyleKey BORDER_BOTTOM_STYLE =
    StyleKeyRegistry.getRegistry().createKey
      ( "border-bottom-style", false, false, StyleKey.All_ELEMENTS );

  public static final StyleKey BORDER_RIGHT_STYLE =
    StyleKeyRegistry.getRegistry().createKey
      ( "border-right-style", false, false, StyleKey.All_ELEMENTS );

  public static final StyleKey BORDER_BOTTOM_RIGHT_RADIUS =
    StyleKeyRegistry.getRegistry().createKey
      ( "border-bottom-right-radius", false, false, StyleKey.All_ELEMENTS );

  public static final StyleKey BORDER_TOP_LEFT_RADIUS =
    StyleKeyRegistry.getRegistry().createKey
      ( "border-top-left-radius", false, false, StyleKey.All_ELEMENTS );

  public static final StyleKey BORDER_BOTTOM_LEFT_RADIUS =
    StyleKeyRegistry.getRegistry().createKey
      ( "border-bottom-left-radius", false, false, StyleKey.All_ELEMENTS );

  public static final StyleKey BORDER_TOP_RIGHT_RADIUS =
    StyleKeyRegistry.getRegistry().createKey
      ( "border-top-right-radius", false, false, StyleKey.All_ELEMENTS );

  public static final StyleKey BORDER_BREAK_WIDTH =
    StyleKeyRegistry.getRegistry().createKey
      ( "border-break-width", false, false, StyleKey.All_ELEMENTS );

  public static final StyleKey BORDER_BREAK_COLOR =
    StyleKeyRegistry.getRegistry().createKey
      ( "border-break-color", false, false, StyleKey.All_ELEMENTS );

  public static final StyleKey BORDER_BREAK_STYLE =
    StyleKeyRegistry.getRegistry().createKey
      ( "border-break-style", false, false, StyleKey.All_ELEMENTS );

  public static final StyleKey BOX_SHADOW =
    StyleKeyRegistry.getRegistry().createKey
      ( "box-shadow", false, false, StyleKey.All_ELEMENTS );

}
