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


package org.pentaho.reporting.designer.core.model.lineal;

import java.io.Serializable;

/**
 * User: Martin Date: 26.01.2006 Time: 10:44:44
 */
public class GuideLine implements Serializable {
  private double position;
  private boolean active;

  public GuideLine( final double position, final boolean active ) {
    this.position = position;
    this.active = active;
  }

  public GuideLine updateActive( final boolean active ) {
    return new GuideLine( position, active );
  }

  public double getPosition() {
    return position;
  }

  public boolean isActive() {
    return active;
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( !( o instanceof GuideLine ) ) {
      return false;
    }

    final GuideLine guideLine = (GuideLine) o;

    if ( Double.compare( guideLine.position, position ) != 0 ) {
      return false;
    }

    return true;
  }

  public String externalize() {
    return "(" + active + ',' + position + ')';
  }

  public int hashCode() {
    final long temp = position != +0.0d ? Double.doubleToLongBits( position ) : 0L;
    return (int) ( temp ^ ( temp >>> 32 ) );
  }
}
