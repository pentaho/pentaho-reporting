package org.pentaho.reporting.engine.classic.core.imagemap;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;

public class CircleImageMapEntry extends AbstractImageMapEntry
{
  private float radius;
  private float x;
  private float y;

  public CircleImageMapEntry(final float x, final float y, final float radius)
  {
    this.x = x;
    this.y = y;
    this.radius = radius;
  }

  public String getAreaType()
  {
    return "circle";
  }

  public float[] getAreaCoordinates()
  {
    return new float[]{x, y, radius};
  }

  public Shape getShape()
  {
    return new Ellipse2D.Float(x,y,2*radius, 2*radius);
  }
}
