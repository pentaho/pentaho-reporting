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

package org.pentaho.reporting.engine.classic.core.util.beans;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;

/**
 * A class that handles the conversion of {@link Integer} attributes to and from their {@link String} representation.
 *
 * @author Thomas Morgner
 */
public class GenericValueConverter implements ValueConverter {
  private PropertyDescriptor propertyDescriptor;
  private PropertyEditor propertyEditor;

  /**
   * Creates a new value converter.
   */
  public GenericValueConverter( final PropertyDescriptor pd ) throws IntrospectionException {
    if ( pd == null ) {
      throw new NullPointerException( "PropertyDescriptor must not be null." );
    }
    if ( pd.getPropertyEditorClass() == null ) {
      throw new IntrospectionException( "Property has no editor." );
    }
    this.propertyDescriptor = pd;
    this.propertyEditor = createPropertyEditor( pd );
  }

  private PropertyEditor createPropertyEditor( final PropertyDescriptor pi ) throws IntrospectionException {
    final Class c = pi.getPropertyEditorClass();
    try {
      return (PropertyEditor) c.newInstance();
    } catch ( Exception e ) {
      throw new IntrospectionException( "Unable to create PropertyEditor." );
    }
  }

  /**
   * Converts the attribute to a string.
   *
   * @param o
   *          the attribute ({@link Integer} expected).
   * @return A string representing the {@link Integer} value.
   * @throws BeanException
   *           if there was an error during the conversion.
   */
  public String toAttributeValue( final Object o ) throws BeanException {
    if ( o == null ) {
      throw new NullPointerException();
    }
    final Class aClass = BeanUtility.getPropertyType( propertyDescriptor );
    if ( aClass.isInstance( o ) == false ) {
      throw new BeanException( "Failed to convert object of type " + o.getClass() + ": Not a " + aClass.getName() );
    }

    propertyEditor.setValue( o );
    return propertyEditor.getAsText();
  }

  /**
   * Converts a string to a {@link Integer}.
   *
   * @param s
   *          the string.
   * @return a {@link Integer}.
   */
  public Object toPropertyValue( final String s ) {
    if ( s == null ) {
      throw new NullPointerException();
    }
    propertyEditor.setAsText( s );
    return propertyEditor.getValue();
  }
}
