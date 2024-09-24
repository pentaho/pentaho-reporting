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

import org.pentaho.reporting.libraries.css.values.CSSConstant;

/**
 * The DisplayModel selects the algorithm used to layout an element. This is equal to selecting a LayoutManager in
 * Java.
 * <p/>
 * Creation-Date: 27.10.2005, 21:03:12
 *
 * @author Thomas Morgner
 */
public class DisplayModel {
  /**
   * If this is not an inline-level element, the effect is the same as for 'block-inside'. Otherwise the element's
   * inline-level children and text sequences that come before the first block-level child are rendered as additional
   * inline boxes for the line boxes of the containing block. Ditto for the text sequences and inline-level children
   * after the last block-level child. The other children and text sequences are rendered as for 'block-inside'.
   */
  public static final CSSConstant INLINE_INSIDE = new CSSConstant(
    "inline-inside" );

  /**
   * Child elements are rendered as described for their 'display-role'. Sequences of inline-level elements and anonymous
   * inline elements (ignoring elements with a display-role of 'none') are rendered as one or more line boxes. (How many
   * line boxes depends on the line-breaking rules, see the Text module [[!CSS3-text].)
   */
  public static final CSSConstant BLOCK_INSIDE =
    new CSSConstant( "block-inside" );

  /**
   * See the table module
   */
  public static final CSSConstant TABLE = new CSSConstant( "table" );
  /**
   * Not yet used.
   */
  public static final CSSConstant RUBY = new CSSConstant( "ruby" );

  /**
   * A Pentaho-reporting compatibility setting. Enables the absolute (canvas) positioning mode.
   */
  public static final CSSConstant CANVAS = new CSSConstant( "canvas" );

  private DisplayModel() {
  }

}
