package org.pentaho.reporting.engine.classic.core.imagemap;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;

public class DefaultImageMapEntry extends AbstractImageMapEntry
{
  private static final float[] EMPTY_COORDS = new float[0];

  public DefaultImageMapEntry()
  {
  }

  public String getAreaType()
  {
    return "default";
  }

  public float[] getAreaCoordinates()
  {
    return EMPTY_COORDS;
  }

  public Shape getShape()
  {
    return new Rectangle2D.Double();
  }
}
