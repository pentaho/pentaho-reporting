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


package org.pentaho.reporting.engine.classic.core.imagemap;

import java.awt.Shape;
import java.awt.geom.GeneralPath;

public class PolygonImageMapEntry extends AbstractImageMapEntry {
  private float[] coordinates;
  private GeneralPath polygon;

  public PolygonImageMapEntry( final float[] coordinates ) {
    if ( coordinates == null ) {
      throw new NullPointerException();
    }
    if ( coordinates.length == 0 ) {
      throw new IllegalArgumentException();
    }
    this.coordinates = coordinates.clone();
  }

  public String getAreaType() {
    return "poly";
  }

  public float[] getAreaCoordinates() {
    return (float[]) coordinates.clone();
  }

  public Shape getShape() {
    if ( polygon == null ) {
      polygon = new GeneralPath();

      for ( int i = 0; i < coordinates.length; i += 2 ) {
        float coordinateX = coordinates[i];
        float coordinateY = coordinates[i + 1];
        if ( i == 0 ) {
          polygon.moveTo( coordinateX, coordinateY );
        } else {
          polygon.lineTo( coordinateX, coordinateY );
        }
      }
      polygon.closePath();
    }
    return polygon;
  }
}
