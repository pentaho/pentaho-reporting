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


package org.pentaho.reporting.engine.classic.core.imagemap;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;

public class DefaultImageMapEntry extends AbstractImageMapEntry {
  private static final float[] EMPTY_COORDS = new float[0];

  public DefaultImageMapEntry() {
  }

  public String getAreaType() {
    return "default";
  }

  public float[] getAreaCoordinates() {
    return EMPTY_COORDS;
  }

  public Shape getShape() {
    return new Rectangle2D.Double();
  }
}
