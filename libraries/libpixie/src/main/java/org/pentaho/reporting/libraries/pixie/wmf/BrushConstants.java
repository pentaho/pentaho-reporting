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
