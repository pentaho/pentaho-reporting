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
 * Creation-Date: 27.10.2005, 22:21:37
 *
 * @author Thomas Morgner
 */
public class FloatDisplace {
  /**
   * Line boxes should be shortened and moved so as to avoid floats. The margin, border, padding and background of the
   * element are not affected by floats. (This is the behavior as described in CSS2.)
   */
  public static final CSSConstant LINE = new CSSConstant( "line" );
  /**
   * The distance between the margin edge of the floats and the start of the line box is set to the distance between the
   * active reference indent edge (see the 'indent-edge-reset' property) and the content edge of the block box. This
   * ensures that relative indents are preserved in the presence of floats.
   */
  public static final CSSConstant INDENT = new CSSConstant( "indent" );
  /**
   * The containing block's width as used by the horizontal formatting model is reduced by the width of the floats
   * intruding upon its content area (not taking into account floating descendants or floating elements that appear
   * later in the document tree). The block is then flowed in this reduced containing block width.
   * <p/>
   * If the effective containing block width is, by the algorithm given above, reduced to a value smaller than the sum
   * of the margin-left, border-width-left, padding-left, width, padding-right, border-width-right, and margin-right
   * values (treating any 'auto' values as zero) then the margin-top of the block is increased until the effective
   * containing block width is no longer so constrained or until all floats have been cleared, whichever occurs first.
   */
  public static final CSSConstant BLOCK = new CSSConstant( "block" );
  /**
   * As for the 'block' value, but the determination of intrusions that adjust the width of the block is done separately
   * on each page on which the block appears. Thus, the block may be narrowed on the first page due to one or more
   * intrusions, but may expand (or contract) its width on subsequent pages with different intrusions. The computed
   * value of the 'width' property for this case is...?
   */
  public static final CSSConstant BLOCK_WITHIN_PAGE =
    new CSSConstant( "block-within-page" );

  private FloatDisplace() {
  }
}
