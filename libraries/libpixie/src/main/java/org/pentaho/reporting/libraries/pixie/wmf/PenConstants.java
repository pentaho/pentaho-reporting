/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


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
