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

package org.pentaho.reporting.libraries.css.keys.list;

import org.pentaho.reporting.libraries.css.values.CSSConstant;

/**
 * Creation-Date: 01.12.2005, 19:11:23
 *
 * @author Thomas Morgner
 */
public class ListStyleTypeOther {
  public static final CSSConstant NORMAL =
    new CSSConstant( "normal" );


  public static final CSSConstant ASTERISKS =
    new CSSConstant( "asterisks" );
  public static final CSSConstant FOOTNOTES =
    new CSSConstant( "footnotes" );
  //  circled-decimal  | circled-lower-latin | circled-upper-latin |
  public static final CSSConstant CIRCLED_DECIMAL =
    new CSSConstant( "circled-decimal" );
  public static final CSSConstant CIRCLED_LOWER_LATIN =
    new CSSConstant( "circled-lower-latin" );
  public static final CSSConstant CIRCLED_UPPER_LATIN =
    new CSSConstant( "circled-upper-latin" );
  // dotted-decimal | double-circled-decimal | filled-circled-decimal |
  public static final CSSConstant DOTTED_DECIMAL =
    new CSSConstant( "dotted-decimal" );
  public static final CSSConstant DOUBLE_CIRCLED_DECIMAL =
    new CSSConstant( "double-circled-decimal" );
  public static final CSSConstant FILLED_CIRCLED_DECIMAL =
    new CSSConstant( "filled-circled-decimal" );
  // parenthesised-decimal | parenthesised-lower-latin
  public static final CSSConstant PARANTHESISED_DECIMAL =
    new CSSConstant( "parenthesised-decimal" );
  public static final CSSConstant PARANTHESISED_LOWER_LATIN =
    new CSSConstant( "parenthesised-lower-latin" );

  private ListStyleTypeOther() {
  }
}
