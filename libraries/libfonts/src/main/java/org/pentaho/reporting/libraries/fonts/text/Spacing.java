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

package org.pentaho.reporting.libraries.fonts.text;

/**
 * Additional character spacing. This has a minimum, optimum and maximum. If the optimum is less than the minimum the
 * optimum is set to the minimum. If the optimum is greater than the maximum the optimum is set to the maximum value.
 * <p/>
 * Spacing is given in absolute values, the unit is micro-points.
 *
 * @author Thomas Morgner
 */
public class Spacing {
  public static final Spacing EMPTY_SPACING = new Spacing( 0, 0, 0 );

  private int minimum;
  private int maximum;
  private int optimum;

  public Spacing( final int minimum, final int optimum, final int maximum ) {
    if ( minimum > maximum ) {
      this.minimum = minimum;
      this.maximum = minimum;
      this.optimum = minimum;
    } else {
      this.minimum = minimum;
      this.maximum = maximum;

      if ( optimum < this.minimum ) {
        this.optimum = this.minimum;
      } else if ( optimum > this.maximum ) {
        this.optimum = this.maximum;
      } else {
        this.optimum = optimum;
      }
    }
  }

  public int getMinimum() {
    return minimum;
  }

  public int getMaximum() {
    return maximum;
  }

  public int getOptimum() {
    return optimum;
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final Spacing spacing = (Spacing) o;

    if ( maximum != spacing.maximum ) {
      return false;
    }
    if ( minimum != spacing.minimum ) {
      return false;
    }
    if ( optimum != spacing.optimum ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    int result = minimum;
    result = 29 * result + maximum;
    result = 29 * result + optimum;
    return result;
  }

  public String toString() {
    final StringBuffer b = new StringBuffer();
    b.append( getClass().getName() );
    b.append( "={minimum=" );
    b.append( minimum );
    b.append( ", optimum=" );
    b.append( optimum );
    b.append( ", maximum=" );
    b.append( maximum );
    b.append( '}' );
    return b.toString();
  }
}
