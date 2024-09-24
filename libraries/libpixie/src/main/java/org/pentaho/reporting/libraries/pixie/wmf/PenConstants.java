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
 * The PenConstants were defined in the Windows-API and are used do define the appearance of Wmf-Pens.
 */
public interface PenConstants {
  /* Pen Styles */
  public static final int PS_SOLID = 0;
  public static final int PS_DASH = 1;       /* -------  */
  public static final int PS_DOT = 2;       /* .......  */
  public static final int PS_DASHDOT = 3;       /* _._._._  */
  public static final int PS_DASHDOTDOT = 4;       /* _.._.._  */
  public static final int PS_NULL = 5;
  public static final int PS_INSIDEFRAME = 6;
  public static final int PS_USERSTYLE = 7;
  public static final int PS_ALTERNATE = 8;
  public static final int PS_STYLE_MASK = 0x0000000F;

  public static final int PS_ENDCAP_ROUND = 0x00000000;
  public static final int PS_ENDCAP_SQUARE = 0x00000100;
  public static final int PS_ENDCAP_FLAT = 0x00000200;
  public static final int PS_ENDCAP_MASK = 0x00000F00;

  public static final int PS_JOIN_ROUND = 0x00000000;
  public static final int PS_JOIN_BEVEL = 0x00001000;
  public static final int PS_JOIN_MITER = 0x00002000;
  public static final int PS_JOIN_MASK = 0x0000F000;

  public static final int PS_COSMETIC = 0x00000000;
  public static final int PS_GEOMETRIC = 0x00010000;
  public static final int PS_TYPE_MASK = 0x000F0000;


}
