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


package org.pentaho.reporting.libraries.fonts.tools;

/**
 * This class is the heart of the alternative geometrics toolkit. It performs the neccessary conversions from and to the
 * AWT classes to the Strict-classes.
 *
 * @author Thomas Morgner
 */
public class FontStrictGeomUtility {
  /**
   * This is the correction factor used to convert points into 'Micro-Points'.
   */
  private static final double CORRECTION_FACTOR = 1000.0d;

  /**
   * Hidden, non usable constructor.
   */
  private FontStrictGeomUtility() {
  }

  /**
   * Converts the given AWT value into a strict value.
   *
   * @param value the AWT point value.
   * @return the internal micro point value.
   */
  public static long toInternalValue( final double value ) {
    return (long) ( value * FontStrictGeomUtility.CORRECTION_FACTOR );
  }

  /**
   * Converts the given micro point value into an AWT value.
   *
   * @param value the micro point point value.
   * @return the AWT point value.
   */
  public static double toExternalValue( final long value ) {
    return ( value / FontStrictGeomUtility.CORRECTION_FACTOR );
  }

  public static long multiply( final long x, final long y ) {
    return (long) ( ( x * y ) / FontStrictGeomUtility.CORRECTION_FACTOR );
  }
}
