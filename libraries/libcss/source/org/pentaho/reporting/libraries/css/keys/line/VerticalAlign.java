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
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package org.pentaho.reporting.libraries.css.keys.line;

import org.pentaho.reporting.libraries.css.values.CSSConstant;

/**
 * Creation-Date: 24.11.2005, 17:08:01
 *
 * @author Thomas Morgner
 */
public class VerticalAlign
{
  public static final CSSConstant USE_SCRIPT =
      new CSSConstant("use-script");
  public static final CSSConstant BASELINE =
      new CSSConstant("baseline");
  public static final CSSConstant SUB =
      new CSSConstant("sub");
  public static final CSSConstant SUPER =
      new CSSConstant("super");

  public static final CSSConstant TOP =
      new CSSConstant("top");
  public static final CSSConstant TEXT_TOP =
      new CSSConstant("text-top");
  public static final CSSConstant CENTRAL =
      new CSSConstant("central");
  public static final CSSConstant MIDDLE =
      new CSSConstant("middle");
  public static final CSSConstant BOTTOM =
      new CSSConstant("bottom");
  public static final CSSConstant TEXT_BOTTOM =
      new CSSConstant("text-bottom");

  private VerticalAlign()
  {
  }
}
