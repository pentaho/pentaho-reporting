/*
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
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.libraries.pixie.wmf;

/**
 * The BrushConstants were defined in the Windows-API and are used do define the appearance of Wmf-Brushes.
 */
public interface BrushConstants {
  /* Brush Styles */
  public static final int BS_SOLID = 0;
  public static final int BS_NULL = 1;
  public static final int BS_HOLLOW = BS_NULL;
  public static final int BS_HATCHED = 2;
  public static final int BS_PATTERN = 3;
  public static final int BS_INDEXED = 4;
  public static final int BS_DIBPATTERN = 5;
  public static final int BS_DIBPATTERNPT = 6;
  public static final int BS_PATTERN8X8 = 7;
  public static final int BS_DIBPATTERN8X8 = 8;
  public static final int BS_MONOPATTERN = 9;

  /* Hatch Style: -----. */
  public static final int HS_HORIZONTAL = 0;
  /* Hatch Style: |||||. */
  public static final int HS_VERTICAL = 1;
  /* Hatch Style: \\\\\\\ . */
  public static final int HS_FDIAGONAL = 2;
  /* Hatch Style: //////// . */
  public static final int HS_BDIAGONAL = 3;
  /* Hatch Style: +++++++ . */
  public static final int HS_CROSS = 4;
  /* Hatch Style: XXXXXXX . */
  public static final int HS_DIAGCROSS = 5;

  public static final int TRANSPARENT = 1;
  public static final int OPAQUE = 2;
}
