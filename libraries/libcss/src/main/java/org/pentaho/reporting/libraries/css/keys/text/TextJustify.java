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

package org.pentaho.reporting.libraries.css.keys.text;

import org.pentaho.reporting.libraries.css.values.CSSConstant;

/**
 * Creation-Date: 28.11.2005, 19:55:17
 *
 * @author Thomas Morgner
 */
public class TextJustify {
  public static final CSSConstant INTER_WORD = new CSSConstant( "inter-word" );
  public static final CSSConstant INTER_IDEOGRAPH = new CSSConstant( "inter-ideograph" );
  public static final CSSConstant INTER_CHARACTER = new CSSConstant( "inter-character" );
  public static final CSSConstant INTER_CLUSTER = new CSSConstant( "inter-cluster" );
  public static final CSSConstant KASHIDA = new CSSConstant( "kashida" );
  public static final CSSConstant SIZE = new CSSConstant( "size" );

  private TextJustify() {
  }
}
