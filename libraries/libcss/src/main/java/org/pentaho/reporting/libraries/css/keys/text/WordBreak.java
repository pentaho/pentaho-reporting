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
 * Creation-Date: 30.10.2005, 20:06:54
 *
 * @author Thomas Morgner
 */
public class WordBreak {
  public static final CSSConstant NORMAL = new CSSConstant( "normal" );
  public static final CSSConstant KEEP_ALL = new CSSConstant( "keep-all" );
  public static final CSSConstant LOOSE = new CSSConstant( "loose" );
  public static final CSSConstant BREAK_STRICT = new CSSConstant( "break-strict" );
  public static final CSSConstant BREAK_ALL = new CSSConstant( "break-all" );

  private WordBreak() {
  }
}
