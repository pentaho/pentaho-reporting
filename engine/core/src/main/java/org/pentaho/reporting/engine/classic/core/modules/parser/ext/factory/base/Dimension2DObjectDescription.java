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

package org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base;

import org.pentaho.reporting.libraries.base.util.FloatDimension;

import java.awt.geom.Dimension2D;

/**
 * An object-description for a <code>Dimension2D</code> object.
 *
 * @author Thomas Morgner
 */
public class Dimension2DObjectDescription extends AbstractObjectDescription {

  /**
   * Creates a new object description.
   */
  public Dimension2DObjectDescription() {
    super( Dimension2D.class );
    setParameterDefinition( "width", Float.class );
    setParameterDefinition( "height", Float.class );
  }

  /**
   * Creates an object based on the description.
   *
   * @return The object.
   */
  public Object createObject() {
    final Dimension2D dim = new FloatDimension();

    final float width = getFloatParameter( "width" );
    final float height = getFloatParameter( "height" );
    dim.setSize( width, height );
    return dim;
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
   *          the object (should be an instance of <code>Dimension2D</code>).
   * @throws ObjectFactoryException
   *           if the object is not an instance of <code>Point2D</code>.
   */
  public void setParameterFromObject( final Object o ) throws ObjectFactoryException {
    if ( !( o instanceof Dimension2D ) ) {
      throw new ObjectFactoryException( "The given object is no java.awt.geom.Dimension2D." );
    }

    final Dimension2D dim = (Dimension2D) o;
    final float width = (float) dim.getWidth();
    final float height = (float) dim.getHeight();

    setParameter( "width", new Float( width ) );
    setParameter( "height", new Float( height ) );
  }
}
