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

import java.awt.geom.Line2D;

/**
 * An object-description for a <code>Line2D</code> object.
 *
 * @author Thomas Morgner
 */
public class Line2DObjectDescription extends AbstractObjectDescription {

  /**
   * Creates a new object description.
   */
  public Line2DObjectDescription() {
    super( Line2D.class );
    setParameterDefinition( "x1", Float.class );
    setParameterDefinition( "x2", Float.class );
    setParameterDefinition( "y1", Float.class );
    setParameterDefinition( "y2", Float.class );
  }

  /**
   * Creates an object based on this description.
   *
   * @return The object.
   */
  public Object createObject() {
    final Line2D line = new Line2D.Float();

    final float x1 = getFloatParameter( "x1" );
    final float x2 = getFloatParameter( "x2" );
    final float y1 = getFloatParameter( "y1" );
    final float y2 = getFloatParameter( "y2" );
    line.setLine( x1, y1, x2, y2 );
    return line;
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
   *          the object (should be an instance of <code>Line2D</code>).
   * @throws ObjectFactoryException
   *           if the object is not an instance of <code>Line2D</code>.
   */
  public void setParameterFromObject( final Object o ) throws ObjectFactoryException {
    if ( !( o instanceof Line2D ) ) {
      throw new ObjectFactoryException( "The given object is no java.awt.geom.Line2D." );
    }
    final Line2D line = (Line2D) o;
    final float x1 = (float) line.getX1();
    final float x2 = (float) line.getX2();
    final float y1 = (float) line.getY1();
    final float y2 = (float) line.getY2();

    setParameter( "x1", new Float( x1 ) );
    setParameter( "x2", new Float( x2 ) );
    setParameter( "y1", new Float( y1 ) );
    setParameter( "y2", new Float( y2 ) );
  }
}
