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

import java.awt.Color;

import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.engine.classic.core.util.beans.ColorValueConverter;

/**
 * An object-description for a <code>Color</code> object.
 *
 * @author Thomas Morgner
 */
public class ColorObjectDescription extends AbstractObjectDescription {
  private ColorValueConverter valueConverter;

  /**
   * Creates a new object description.
   */
  public ColorObjectDescription() {
    super( Color.class );
    valueConverter = new ColorValueConverter();
    setParameterDefinition( "value", String.class );
  }

  /**
   * Creates an object based on this description.
   *
   * @return The object.
   */
  public Object createObject() {
    final String value = (String) getParameter( "value" );
    if ( value == null ) {
      return null;
    }
    try {
      return valueConverter.toPropertyValue( value.trim() );
    } catch ( BeanException e ) {
      return null;
    }
  }

  /**
   * Sets the parameters of this description object to match the supplied object.
   *
   * @param o
   *          the object (should be an instance of <code>Color</code>).
   * @throws ObjectFactoryException
   *           if there is a problem while reading the properties of the given object.
   */
  public void setParameterFromObject( final Object o ) throws ObjectFactoryException {
    if ( !( o instanceof Color ) ) {
      throw new ObjectFactoryException( "Is no instance of java.awt.Color" );
    }
    final Color c = (Color) o;
    try {
      setParameter( "value", valueConverter.toAttributeValue( c ) );
    } catch ( BeanException e ) {
      throw new ObjectFactoryException( "Failed to convert color to string", e );
    }
  }
}
