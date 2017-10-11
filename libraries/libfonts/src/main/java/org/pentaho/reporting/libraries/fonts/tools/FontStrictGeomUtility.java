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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

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
