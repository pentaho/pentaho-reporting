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

package org.pentaho.reporting.libraries.css.keys.hyperlinks;

import org.pentaho.reporting.libraries.css.values.CSSConstant;

/**
 * Creation-Date: 24.11.2005, 17:26:17
 *
 * @author Thomas Morgner
 */
public class TargetName {
  public static final CSSConstant CURRENT =
    new CSSConstant( "current" );
  public static final CSSConstant ROOT =
    new CSSConstant( "root" );
  public static final CSSConstant PARENT =
    new CSSConstant( "parent" );
  public static final CSSConstant NEW =
    new CSSConstant( "new" );
  public static final CSSConstant MODAL =
    new CSSConstant( "modal" );

  private TargetName() {
  }
}
