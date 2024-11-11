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


package org.pentaho.reporting.engine.classic.wizard.model;

import org.pentaho.reporting.engine.classic.core.util.ObjectStreamResolveException;

import java.io.ObjectStreamException;
import java.io.Serializable;

public final class LengthUnit implements Serializable {
  public static final LengthUnit POINTS = new LengthUnit( "pt", 1 );
  public static final LengthUnit PERCENTAGE = new LengthUnit( "%", -1 );
  public static final LengthUnit PICA = new LengthUnit( "pc", 12 );
  public static final LengthUnit MM = new LengthUnit( "mm", 72 / ( 2.54 * 10 ) );
  public static final LengthUnit CM = new LengthUnit( "cm", 72.0 / 2.54 );
  public static final LengthUnit INCH = new LengthUnit( "in", 72 );

  private double dotsPerUnit;
  private String name;

  private LengthUnit( final String name, final double dotsPerUnit ) {
    this.name = name;
    this.dotsPerUnit = dotsPerUnit;
  }

  public String getName() {
    return name;
  }

  public double getDotsPerUnit() {
    return dotsPerUnit;
  }

  public double convertFromPoints( final double points ) {
    return points / dotsPerUnit;
  }

  public double convertToPoints( final double unit ) {
    return unit * dotsPerUnit;
  }

  public static LengthUnit[] values() {
    return new LengthUnit[] { INCH, CM, MM, PICA, PERCENTAGE, POINTS };
  }

  /**
   * Replaces the automatically generated instance with one of the enumeration instances.
   *
   * @return the resolved element
   * @throws ObjectStreamException if the element could not be resolved.
   */
  private Object readResolve()
    throws ObjectStreamException {
    if ( this.dotsPerUnit == INCH.dotsPerUnit ) {
      return INCH;
    }
    if ( this.dotsPerUnit == CM.dotsPerUnit ) {
      return CM;
    }
    if ( this.dotsPerUnit == MM.dotsPerUnit ) {
      return MM;
    }
    if ( this.dotsPerUnit == PICA.dotsPerUnit ) {
      return PICA;
    }
    if ( this.dotsPerUnit == PERCENTAGE.dotsPerUnit ) {
      return PERCENTAGE;
    }
    if ( this.dotsPerUnit == POINTS.dotsPerUnit ) {
      return POINTS;
    }
    // unknown element alignment...
    throw new ObjectStreamResolveException();
  }

}
