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

/**
 * An object-description for a <code>Boolean</code> object.
 *
 * @author Thomas Morgner
 */
public class BooleanObjectDescription extends AbstractObjectDescription {

  /**
   * Creates a new object description.
   */
  public BooleanObjectDescription() {
    super( Boolean.class );
    setParameterDefinition( "value", String.class );
  }

  /**
   * Creates a new <code>Boolean</code> based on the settings of this description object.
   *
   * @return A <code>Boolean</code>.
   */
  public Object createObject() {
    final String o = (String) getParameter( "value" );
    return Boolean.valueOf( o.trim() );
  }

  /**
   * Sets the description object parameters to match the supplied object (which should be an instance of
   * <code>Boolean</code>.
   *
   * @param o
   *          the object.
   * @throws ObjectFactoryException
   *           if there is a problem while reading the properties of the given object.
   */
  public void setParameterFromObject( final Object o ) throws ObjectFactoryException {
    if ( !( o instanceof Boolean ) ) {
      throw new ObjectFactoryException( "The given object is no java.lang.Boolean. " );
    }
    setParameter( "value", String.valueOf( o ) );
  }

  /**
   * Tests for equality.
   *
   * @param o
   *          the object to test.
   * @return A boolean.
   */
  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( !( o instanceof AbstractObjectDescription ) ) {
      return false;
    }

    final AbstractObjectDescription abstractObjectDescription = (AbstractObjectDescription) o;

    if ( Boolean.TYPE.equals( abstractObjectDescription.getObjectClass() ) ) {
      return true;
    }
    if ( Boolean.class.equals( abstractObjectDescription.getObjectClass() ) ) {
      return true;
    }
    return false;
  }

  /**
   * Returns a hash code.
   *
   * @return A hash code.
   */
  public int hashCode() {
    return getObjectClass().hashCode();
  }

}
