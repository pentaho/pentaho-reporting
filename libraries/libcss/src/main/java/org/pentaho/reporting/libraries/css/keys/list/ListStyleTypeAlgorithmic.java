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
 * Creation-Date: 01.12.2005, 18:42:51
 *
 * @author Thomas Morgner
 */
public class ListStyleTypeAlgorithmic {
  // armenian | cjk-ideographic | ethiopic-numeric | georgian |
  // hebrew | japanese-formal | japanese-informal |   lower-armenian |
  // lower-roman | simp-chinese-formal | simp-chinese-informal |
  // syriac | tamil | trad-chinese-formal | trad-chinese-informal |
  // upper-armenian | upper-roman

  public static final CSSConstant ARMENIAN =
    new CSSConstant( "armenian" );
  public static final CSSConstant CJK_IDEOGRAPHIC =
    new CSSConstant( "cjk-ideographic" );
  public static final CSSConstant ETHIOPIC_NUMERIC =
    new CSSConstant( "ethiopic-numeric" );
  public static final CSSConstant GEORGIAN =
    new CSSConstant( "georgian" );
  public static final CSSConstant HEBREW =
    new CSSConstant( "hebrew" );
  public static final CSSConstant JAPANESE_FORMAL =
    new CSSConstant( "japanese-formal" );
  public static final CSSConstant JAPANESE_INFORMAL =
    new CSSConstant( "japanese-informal" );
  public static final CSSConstant LOWER_ARMENIAN =
    new CSSConstant( "lower-armenian" );
  public static final CSSConstant LOWER_ROMAN =
    new CSSConstant( "lower-roman" );
  public static final CSSConstant SIMP_CHINESE_FORMAL =
    new CSSConstant( "simp-chinese-formal" );
  public static final CSSConstant SIMP_CHINESE_INFORMAL =
    new CSSConstant( "simp-chinese-informal" );
  public static final CSSConstant TRAD_CHINESE_FORMAL =
    new CSSConstant( "trad-chinese-formal" );
  public static final CSSConstant TRAD_CHINESE_INFORMAL =
    new CSSConstant( "trad-chinese-informal" );
  public static final CSSConstant UPPER_ARMENIAN =
    new CSSConstant( "upper-armenian" );
  public static final CSSConstant UPPER_ROMAN =
    new CSSConstant( "upper-roman" );
  public static final CSSConstant SYRIAC =
    new CSSConstant( "syriac" );
  public static final CSSConstant TAMIL =
    new CSSConstant( "tamil" );

  private ListStyleTypeAlgorithmic() {
  }
}
