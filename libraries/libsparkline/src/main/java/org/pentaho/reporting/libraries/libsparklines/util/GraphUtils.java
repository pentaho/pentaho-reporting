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


package org.pentaho.reporting.libraries.libsparklines.util;

/**
 * A utility class that computes the scale factor for a given number array to scale the numbers into a predefined
 * height.
 *
 * @author Larry Ogrodnek <larry@cheesesteak.net>
 * @version $Revision: 1.3 $ $Date: 2007-01-15 04:49:21 $
 */
public final class GraphUtils {
  /**
   * Utility class constructor prevents object creation.
   */
  private GraphUtils() {
  }

  /**
   * Computes the scale factor to scale the given numeric data into the target height.
   *
   * @param data   the numeric data.
   * @param height the target height of the graph.
   * @return the scale factor.
   */
  public static float getDivisor( final Number[] data, final int height ) {
    if ( data == null ) {
      throw new NullPointerException( "Data array must not be null." );
    }

    if ( height < 1 ) {
      throw new IndexOutOfBoundsException( "Height must be greater or equal to 1" );
    }

    float max = Float.MIN_VALUE;
    float min = Float.MAX_VALUE;

    for ( int index = 0; index < data.length; index++ ) {
      final Number i = data[ index ];
      if ( i == null ) {
        continue;
      }

      final float numValue = i.floatValue();
      if ( numValue < min ) {
        min = numValue;
      }
      if ( numValue > max ) {
        max = numValue;
      }
    }

    if ( max <= min ) {
      return 1.0f;
    }
    if ( height == 1 ) {
      return 0;
    }
    return ( max - min ) / ( height - 1 );
  }

  public static float getAxe( final Number[] data ) {
    if ( data == null ) {
      throw new NullPointerException( "Data array must not be null." );
    }

    float max = Float.MIN_VALUE;
    float min = Float.MAX_VALUE;

    for ( int index = 0; index < data.length; index++ ) {
      final Number i = data[ index ];
      if ( i == null ) {
        continue;
      }

      final float numValue = i.floatValue();
      if ( index == 0 ) {
        max = min = numValue;
      } else {
        if ( numValue > max ) {
          max = numValue;
        }
        if ( numValue < min ) {
          min = numValue;
        }
      }
    }

    if ( min >= 0 ) {
      return 0;
    }
    if ( max <= 0 ) {
      return -1;
    }

    final float distance = max - min;

    final float axe = distance / 2f;
    final float delta = max - axe;
    return axe + delta;
  }
}


