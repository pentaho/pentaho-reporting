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

package org.pentaho.reporting.libraries.css.keys.content;

import org.pentaho.reporting.libraries.css.values.CSSStringType;
import org.pentaho.reporting.libraries.css.values.CSSStringValue;

/**
 * This class holds a sample of well-known quoting characters. These values are non-normative and there are no
 * CSS-constants defined for them.
 *
 * @author Thomas Morgner
 */
public class QuotingValues {

  public static final CSSStringValue QUOTATION_MARK =
    new CSSStringValue( CSSStringType.STRING, "\"" );
  public static final CSSStringValue APOSTROPHE =
    new CSSStringValue( CSSStringType.STRING, "\u0027" );
  public static final CSSStringValue SINGLE_LEFT_POINTING_ANGLE_QUOTATION_MARK =
    new CSSStringValue( CSSStringType.STRING, "\u2039" );
  public static final CSSStringValue SINGLE_RIGHT_POINTING_ANGLE_QUOTATION_MARK =
    new CSSStringValue( CSSStringType.STRING, "\u203A" );
  public static final CSSStringValue DOUBLE_LEFT_POINTING_ANGLE_QUOTATION_MARK =
    new CSSStringValue( CSSStringType.STRING, "\u00AB" );
  public static final CSSStringValue DOUBLE_RIGHT_POINTING_ANGLE_QUOTATION_MARK =
    new CSSStringValue( CSSStringType.STRING, "\u00BB" );
  public static final CSSStringValue SINGLE_LEFT_QUOTATION_MARK =
    new CSSStringValue( CSSStringType.STRING, "\u2018" );
  public static final CSSStringValue SINGLE_RIGHT_QUOTATION_MARK =
    new CSSStringValue( CSSStringType.STRING, "\u2019" );
  public static final CSSStringValue DOUBLE_LEFT_QUOTATION_MARK =
    new CSSStringValue( CSSStringType.STRING, "\u201C" );
  public static final CSSStringValue DOUBLE_RIGHT_QUOTATION_MARK =
    new CSSStringValue( CSSStringType.STRING, "\u201D" );
  public static final CSSStringValue DOUBLE_LOW9_QUOTATION_MARK =
    new CSSStringValue( CSSStringType.STRING, "\u201E" );

  private QuotingValues() {
  }
}
