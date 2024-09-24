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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

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
