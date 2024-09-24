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

import java.awt.geom.RoundRectangle2D;

/**
 * An object-description for a <code>Rectangle2D</code> object.
 *
 * @author Thomas Morgner
 */
public class RoundRectangle2DObjectDescription extends AbstractObjectDescription {

  /**
   * Creates a new object description.
   */
  public RoundRectangle2DObjectDescription() {
    super( RoundRectangle2D.class );
    setParameterDefinition( "width", Float.class );
    setParameterDefinition( "height", Float.class );
    setParameterDefinition( "x", Float.class );
    setParameterDefinition( "y", Float.class );
    setParameterDefinition( "arcWidth", Float.class );
    setParameterDefinition( "arcHeight", Float.class );
  }

  /**
   * Creates an object based on this description.
   *
   * @return The object.
   */
  public Object createObject() {
    final RoundRectangle2D rect = new RoundRectangle2D.Float();
    final float w = getFloatParameter( "width" );
    final float h = getFloatParameter( "height" );
    final float x = getFloatParameter( "x" );
    final float y = getFloatParameter( "y" );
    final float aw = getFloatParameter( "arcWidth" );
    final float ah = getFloatParameter( "arcHeight" );
    rect.setRoundRect( x, y, w, h, aw, ah );

    return rect;
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
   *          the object (should be an instance of <code>Rectangle2D</code>).
   * @throws ObjectFactoryException
   *           if the object is not an instance of <code>Rectangle2D</code>.
   */
  public void setParameterFromObject( final Object o ) throws ObjectFactoryException {
    if ( !( o instanceof RoundRectangle2D ) ) {
      throw new ObjectFactoryException( "The given object is no java.awt.geom.Rectangle2D." );
    }

    final RoundRectangle2D rect = (RoundRectangle2D) o;
    final float x = (float) rect.getX();
    final float y = (float) rect.getY();
    final float w = (float) rect.getWidth();
    final float h = (float) rect.getHeight();
    final float aw = (float) rect.getArcWidth();
    final float ah = (float) rect.getArcHeight();

    setParameter( "x", new Float( x ) );
    setParameter( "y", new Float( y ) );
    setParameter( "width", new Float( w ) );
    setParameter( "height", new Float( h ) );
    setParameter( "arcWidth", new Float( aw ) );
    setParameter( "arcHeight", new Float( ah ) );
  }

}
