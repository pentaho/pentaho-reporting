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
