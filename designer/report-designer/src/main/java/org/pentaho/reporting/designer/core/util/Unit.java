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

package org.pentaho.reporting.designer.core.util;

/**
 * User: Martin Date: 07.02.2006 Time: 20:14:38
 */
public enum Unit {
  POINTS( 1 ),
  PICA( 12 ),
  MM( 72. / ( 2.54 * 10 ) ),
  CM( 72. / 2.54 ),
  INCH( 72 );

  private double dotsPerUnit;

  private Unit( final double dotsPerUnit ) {
    this.dotsPerUnit = dotsPerUnit;
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

  public double getTickSize( final double sf ) {
    switch( this ) {
      case POINTS: {
        if ( sf < 0.2 ) {
          return 200;
        } else if ( sf < 0.5 ) {
          return 100;
        } else if ( sf < 2 ) {
          return 50;
        } else if ( sf < 3 ) {
          return 20;
        } else {
          return 10;
        }
      }
      case INCH: {
        if ( sf < 0.2 ) {
          return 2;
        } else if ( sf < 0.5 ) {
          return 1;
        } else if ( sf < 2 ) {
          return 0.5;
        } else if ( sf < 3 ) {
          return 0.2;
        } else {
          return 0.1;
        }
      }
      case CM: {
        if ( sf < 0.2 ) {
          return 5;
        } else if ( sf < 0.5 ) {
          return 2;
        } else if ( sf < 2 ) {
          return 1;
        } else if ( sf < 4 ) {
          return 0.5;
        } else {
          return 0.2;
        }
      }
      case MM: {
        if ( sf < 0.5 ) {
          return 50;
        } else if ( sf < 1 ) {
          return 20;
        } else if ( sf < 3 ) {
          return 10;
        } else if ( sf < 4 ) {
          return 5;
        } else {
          return 2;
        }
      }
      case PICA: {
        if ( sf < 0.2 ) {
          return 20;
        } else if ( sf < 0.5 ) {
          return 10;
        } else if ( sf < 2 ) {
          return 5;
        } else if ( sf < 3 ) {
          return 2;
        } else {
          return 1;
        }
      }
    }

    return 1;

  }
}
