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

import java.awt.BasicStroke;

/**
 * An object-description for a <code>BasicStroke</code> object.
 *
 * @author Thomas Morgner
 */
public class BasicStrokeObjectDescription extends AbstractObjectDescription {

  /**
   * Creates a new object description.
   */
  public BasicStrokeObjectDescription() {
    super( BasicStroke.class );
    setParameterDefinition( "value", String.class );
    setParameterDefinition( "width", Float.class );
    setParameterDefinition( "dashes", Float[].class );
  }

  /**
   * Returns a parameter as a float.
   *
   * @param param
   *          the parameter name.
   * @return The float value.
   */
  private float getFloatParameter( final String param ) {
    final String p = (String) getParameter( param );
    if ( p == null ) {
      return 0;
    }
    try {
      return Float.parseFloat( p );
    } catch ( Exception e ) {
      return 0;
    }
  }

  /**
   * Creates a new <code>BasicStroke</code> object based on this description.
   *
   * @return The <code>BasicStroke</code> object.
   */
  public Object createObject() {

    final float width = getFloatParameter( "value" );
    if ( width > 0 ) {
      return new BasicStroke( width );
    }

    final Float realWidth = (Float) getParameter( "width" );
    final Float[] dashes = (Float[]) getParameter( "dashes" );
    if ( realWidth == null || dashes == null ) {
      return null;
    }
    final float[] dashesPrimitive = new float[dashes.length];
    for ( int i = 0; i < dashes.length; i++ ) {
      final Float dash = dashes[i];
      dashesPrimitive[i] = dash.floatValue();
    }
    return new BasicStroke( realWidth.floatValue(), BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f,
        dashesPrimitive, 0.0f );
  }

  /**
   * Sets the parameters for this description to match the supplied object.
   *
   * @param o
   *          the object (instance of <code>BasicStroke</code> required).
   * @throws ObjectFactoryException
   *           if the supplied object is not an instance of <code>BasicStroke</code>.
   */
  public void setParameterFromObject( final Object o ) throws ObjectFactoryException {
    if ( !( o instanceof BasicStroke ) ) {
      throw new ObjectFactoryException( "Expected object of type BasicStroke" );
    }
    final BasicStroke bs = (BasicStroke) o;
    setParameter( "value", String.valueOf( bs.getLineWidth() ) );
  }
}
