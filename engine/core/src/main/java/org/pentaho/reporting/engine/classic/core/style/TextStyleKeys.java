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
 * Copyright (c) 2001 - 2016 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.style;

public class TextStyleKeys {
  private TextStyleKeys() {
  }

  /**
   * This property declares whether and how white space inside the element is collapsed. Values have the following
   * meanings, which must be interpreted according to the white space processing rules:
   * <p/>
   * <ul>
   * <li>collapse</li>
   * <p>
   * This value directs user agents to collapse sequences of white space into a single character (or in some cases, no
   * character).
   * </p>
   * <li>preserve</li>
   * <p>
   * This value prevents user agents from collapsing sequences of white space. Line breaks are preserved.
   * </p>
   * <li>preserve-breaks</li>
   * <p>
   * This value collapses white space as for 'collapse', but preserves line breaks.
   * </p>
   * <li>discard</li>
   * <p>
   * This value directs user agents to "discard" all white space in the element.
   * </p>
   * </ul>
   */
  public static final StyleKey WHITE_SPACE_COLLAPSE = StyleKey.getStyleKey( "whitespace-collapse",
      WhitespaceCollapse.class, false, true );
  /**
   * 'none' or 'wrap'
   */
  public static final StyleKey TEXT_WRAP = StyleKey.getStyleKey( "text-wrap", TextWrap.class, false, true );

  public static final StyleKey X_MIN_LETTER_SPACING = StyleKey.getStyleKey( "min-letter-spacing", Float.class );
  public static final StyleKey X_OPTIMUM_LETTER_SPACING = StyleKey.getStyleKey( "optimum-letter-spacing", Float.class );
  public static final StyleKey X_MAX_LETTER_SPACING = StyleKey.getStyleKey( "max-letter-spacing", Float.class );
  public static final StyleKey WORD_SPACING = StyleKey.getStyleKey( "word-spacing", Float.class );

  public static final StyleKey FONT_SMOOTH = StyleKey.getStyleKey( "font-smooth", FontSmooth.class );
  public static final StyleKey VERTICAL_TEXT_ALIGNMENT = StyleKey.getStyleKey( "vertical-text-alignment",
      VerticalTextAlign.class );
  public static final StyleKey WORDBREAK = StyleKey.getStyleKey( "word-break", Boolean.class );

  /**
   * A key for the 'font family' used to draw element text.
   */
  public static final StyleKey FONT = StyleKey.getStyleKey( "font", String.class );

  /**
   * A key for the 'font size' used to draw element text.
   */
  public static final StyleKey FONTSIZE = StyleKey.getStyleKey( "font-size", Integer.class );

  /**
   * A key for the 'font size' used to draw element text.
   */
  public static final StyleKey LINEHEIGHT = StyleKey.getStyleKey( "line-height", Float.class );

  /**
   * A key for an element's 'bold' flag.
   */
  public static final StyleKey BOLD = StyleKey.getStyleKey( "font-bold", Boolean.class );

  /**
   * A key for an element's 'italic' flag.
   */
  public static final StyleKey ITALIC = StyleKey.getStyleKey( "font-italic", Boolean.class );

  /**
   * A key for an element's 'underlined' flag.
   */
  public static final StyleKey UNDERLINED = StyleKey.getStyleKey( "font-underline", Boolean.class );

  /**
   * A key for an element's 'strikethrough' flag.
   */
  public static final StyleKey STRIKETHROUGH = StyleKey.getStyleKey( "font-strikethrough", Boolean.class );

  /**
   * A key for an element's 'embedd' flag.
   */
  public static final StyleKey EMBEDDED_FONT = StyleKey.getStyleKey( "font-embedded", Boolean.class );

  /**
   * A key for an element's 'embedd' flag.
   */
  public static final StyleKey FONTENCODING = StyleKey.getStyleKey( "font-encoding", String.class );

  /**
   * The string that is used to end a text if not all text fits into the element. In typography, this string is better
   * known as ellipsis.
   */
  public static final StyleKey RESERVED_LITERAL = StyleKey.getStyleKey( "reserved-literal", String.class );

  /**
   * The Layout Cacheable stylekey. Set this stylekey to false, to define that the element is not cachable. This key
   * defaults to true.
   */
  public static final StyleKey TRIM_TEXT_CONTENT = StyleKey.getStyleKey( "trim-text-content", Boolean.class );

  public static final StyleKey TEXT_INDENT = StyleKey.getStyleKey( "text-indent", Float.class );

  public static final StyleKey FIRST_LINE_INDENT = StyleKey.getStyleKey( "first-line-indent", Float.class );

  public static final StyleKey DIRECTION = StyleKey.getStyleKey( "direction", TextDirection.class );

  public static final StyleKey TEXT_ROTATION = StyleKey.getStyleKey( "rotation", TextRotation.class );
}
