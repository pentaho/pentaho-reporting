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
 * Various MappingConstants defined in the Windows API.
 */
public interface MappingConstants {
  public static final int MM_TEXT = 1;
  public static final int MM_LOMETRIC = 2;
  public static final int MM_HIMETRIC = 3;
  public static final int MM_LOENGLISH = 4;
  public static final int MM_HIENGLISH = 5;
  public static final int MM_TWIPS = 6;
  public static final int MM_ISOTROPIC = 7;
  public static final int MM_ANISOTROPIC = 8;

  /* Min and Max Mapping Mode values */
  public static final int MM_MIN = MM_TEXT;
  public static final int MM_MAX = MM_ANISOTROPIC;
  public static final int MM_MAX_FIXEDSCALE = MM_TWIPS;
}
