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


package org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base;

import java.awt.geom.Point2D;

/**
 * An object-description for a <code>Point2D</code> object.
 *
 * @author Thomas Morgner
 */
public class Point2DObjectDescription extends AbstractObjectDescription {

  /**
   * Creates a new object description.
   */
  public Point2DObjectDescription() {
    super( Point2D.class );
    setParameterDefinition( "x", Float.class );
    setParameterDefinition( "y", Float.class );
  }

  /**
   * Creates an object based on this description.
   *
   * @return The object.
   */
  public Object createObject() {
    final Point2D point = new Point2D.Float();

    final float x = getFloatParameter( "x" );
    final float y = getFloatParameter( "y" );
    point.setLocation( x, y );
    return point;
  }

  /**
   * Returns a parameter value as a float.
   *
   * @param param
   *          the parameter name.
   * @return The float value.
   */
  private float getFloatParameter( final String param ) {
    final Float p = (Float) getParameter( param );
    if ( p == null ) {
      return 0;
    }
    return p.floatValue();
  }

  /**
   * Sets the parameters of this description object to match the supplied object.
   *
   * @param o
   *          the object (should be an instance of <code>Point2D</code>).
   * @throws ObjectFactoryException
   *           if the object is not an instance of <code>Point2D</code>.
   */
  public void setParameterFromObject( final Object o ) throws ObjectFactoryException {
    if ( !( o instanceof Point2D ) ) {
      throw new ObjectFactoryException( "The given object is no java.awt.geom.Point2D." );
    }

    final Point2D point = (Point2D) o;
    final float x = (float) point.getX();
    final float y = (float) point.getY();

    setParameter( "x", new Float( x ) );
    setParameter( "y", new Float( y ) );
  }
}
