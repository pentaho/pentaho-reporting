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
 * The Display-Role property describes the role an element plays in the parent algorithm. Seeing that property as
 * general 'LayoutManagerConstraint' might be apropriate.
 * <p/>
 * The RUBY_* properties are required for Japanese and other Asian font support and not yet used.
 */
public class DisplayRole {
  /**
   * The element is not rendered. The rendering is the same as if the element had been removed from the document tree,
   * except for possible effects on counters (see [generated] or [paged]).
   * <p/>
   * Note that :before and :after pseudo elements of this element are also not rendered, see [generated].)
   */
  public static final CSSConstant NONE = new CSSConstant( "none" );

  /**
   * The element is rendered as a rectangular block. See Collapsing margins for its position relative to earlier boxes
   * in the same flow. In paged media [ref] or inside another element that has two or more columns, the box may be split
   * into several smaller boxes.
   */
  public static final CSSConstant BLOCK = new CSSConstant( "block" );

  /**
   * The element is rendered inside a line box. It may be split into several boxes because of line breaking and bidi
   * processing (see the Text module).
   */
  public static final CSSConstant INLINE = new CSSConstant( "inline" );

  /**
   * The element is rendered the same as if it had display-role 'block', but in addition a marker is generated (see
   * 'list-style').
   */
  public static final CSSConstant LIST_ITEM = new CSSConstant( "list-item" );

  /**
   * The effect depends on what comes after the element. If the next element (in the depth-first, left to right tree
   * traversal, so not necessarily a sibling) has a 'display-model' of 'block-inside', the current element will be
   * rendered as if it had display-role 'inline' and was the first child of that block element. Otherwise this element
   * will be rendered as if it had display-role 'block'. [Does this explain Ian's tests?]
   */
  public static final CSSConstant RUN_IN = new CSSConstant( "run-in" );

  /**
   * The effect depends on the intrinsic size of this element and on what comes after it. If the next element has a
   * 'display-role' of 'block', and the intrinsic width of the compact element is less than or equal to the left margin
   * of that block (resp. the right margin, if the block's 'direction' is 'rtl'), then the compact element is rendered
   * in the left (right) margin of the block at its intrinsic size and baseline aligned with the first line box of the
   * block. [Do we need a different alignment depending on script?] In all other cases the compact element is rendered
   * as if its display-role was 'block'.
   */
  public static final CSSConstant COMPACT = new CSSConstant( "compact" );

  /**
   * See the Tables module [CSS3TBL].
   */
  public static final CSSConstant TABLE_ROW = new CSSConstant( "table-row" );
  public static final CSSConstant TABLE_CELL = new CSSConstant( "table-cell" );
  public static final CSSConstant TABLE_ROW_GROUP = new CSSConstant(
    "table-row-group" );
  public static final CSSConstant TABLE_HEADER_GROUP = new CSSConstant(
    "table-header-group" );

  public static final CSSConstant TABLE_FOOTER_GROUP = new CSSConstant(
    "table-footer-group" );
  public static final CSSConstant TABLE_COLUMN = new CSSConstant(
    "table-column" );
  public static final CSSConstant TABLE_COLUMN_GROUP = new CSSConstant(
    "table-column-group" );
  public static final CSSConstant TABLE_CAPTION = new CSSConstant(
    "table-caption" );

  /**
   * Ruby is not yet used.
   */
  public static final CSSConstant RUBY_TEXT = new CSSConstant( "ruby-text" );
  public static final CSSConstant RUBY_BASE = new CSSConstant( "ruby-base" );
  public static final CSSConstant RUBY_BASE_GROUP = new CSSConstant(
    "ruby-base-group" );
  public static final CSSConstant RUBY_TEXT_GROUP
    = new CSSConstant( "ruby-text-group" );

  /**
   * A Pentaho-reporting compatibility setting. Enables the absolute positioning mode.
   */
  public static final CSSConstant CANVAS = new CSSConstant( "canvas" );


  private DisplayRole() {
  }
}
