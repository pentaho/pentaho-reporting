/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.libraries.pixie.wmf;

/**
 * The TextConstants were defined in the Windows-API and are used do define the appearance of Wmf-Text.
 *
 * @noinspection SuspiciousNameCombination
 */
public interface TextConstants {
  public static final int TA_NOUPDATECP = 0x0000; //bin 00.00000000
  public static final int TA_UPDATECP = 0x0001; //bin 00.00000001

  public static final int TA_LEFT = 0x0000; //bin 00.00000000
  public static final int TA_RIGHT = 0x0002; //bin 00.00000010
  public static final int TA_CENTER = 0x0006; //bin 00.00000110

  public static final int TA_TOP = 0x0000; //bin 00.00000000
  public static final int TA_BOTTOM = 0x0008; //bin 00.00001000
  public static final int TA_BASELINE = 0x0018; //bin 00.00011000
  public static final int TA_RTLREADING = 0x0100; //bin 01.00000000
  public static final int TA_MASK = ( TA_BASELINE + TA_CENTER + TA_UPDATECP + TA_RTLREADING );

  public static final int VTA_BASELINE = TA_BASELINE;
  public static final int VTA_LEFT = TA_BOTTOM;
  public static final int VTA_RIGHT = TA_TOP;
  public static final int VTA_CENTER = TA_CENTER;
  public static final int VTA_BOTTOM = TA_RIGHT;
  public static final int VTA_TOP = TA_LEFT;

}
