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
 * Copyright (c) 2001 - 2014 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
 */
package org.pentaho.reporting.engine.classic.core.util;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ReportElement;

public class RotationUtils
{
  public static final float NO_ROTATION = 0; // degrees
  public static final float FULL_ROTATION = 360; // degrees

  public static final String ROTATE_LEFT = "left"; //$NON-NLS-1$
  public static final String ROTATE_RIGHT = "right"; //$NON-NLS-1$

  public static final String ROTATE_NONE = "none"; //$NON-NLS-1$
  public static final String ROTATE_NULL = "null"; //$NON-NLS-1$

  public static float getRotation(final ReportElement e) {

    return ( e == null ) ? NO_ROTATION :
        getRotation( String.valueOf( e.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.ROTATION ) ) );
  }

  public static float getRotation( final String r ) {

    if ( r != null && !ROTATE_NONE.equalsIgnoreCase( r ) && !ROTATE_NULL.equalsIgnoreCase( r ) ) {

      if ( ROTATE_LEFT.equalsIgnoreCase( r ) ) {
        return Float.valueOf( -90 );

      } else if ( ROTATE_RIGHT.equalsIgnoreCase (r ) ) {
        return Float.valueOf( 90 );

      } else if ( isValidNumber( r ) ) {
        // Check if rotation is needed by validating the rotation angle value
        return Float.valueOf( r ).floatValue() % FULL_ROTATION; // remainder
      }
    }
    return NO_ROTATION;
  }

  /*
   * xls throws exception if angle out of range [-90,90]
   */
  public static float getRotationDegreesInXlsAcceptedRange( final String r ) {

    final float rotation = getRotation( r );

    // Accepted range is [-90,90] degrees
    if ( rotation >= -90 && rotation <= 90 ) {
      return rotation;
    } else if ( rotation >= 270 && rotation < 360 ) {
      return rotation - 360;
    } else if ( rotation > -360 && rotation <= -270 ) {
      return rotation + 360;
    } else {
      // if unsupported degree value, cell content will not be rotated and will be exported as an image
      return NO_ROTATION;
    }
  }

  public static boolean isValidNumber( String value ) {
    return value != null && value.matches( "[-+]?[0-9]*\\.?[0-9]+" ); // Is a number - int, float, double
  }
}
