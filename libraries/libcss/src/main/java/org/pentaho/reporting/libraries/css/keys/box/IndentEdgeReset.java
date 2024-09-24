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
 * Creation-Date: 30.10.2005, 18:27:41
 *
 * @author Thomas Morgner
 */
public class IndentEdgeReset {
  public static final CSSConstant NONE =
    new CSSConstant( "none" );
  public static final CSSConstant MARGIN_EDGE =
    new CSSConstant( "margin-edge" );
  public static final CSSConstant BORDER_EDGE =
    new CSSConstant( "border-edge" );
  public static final CSSConstant PADDING_EDGE =
    new CSSConstant( "padding-edge" );
  public static final CSSConstant CONTENT_EDGE =
    new CSSConstant( "content-edge" );

  private IndentEdgeReset() {
  }

}
