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

/**
 * An object-description for a <code>String</code> object.
 *
 * @author Thomas Morgner
 */
public class StringObjectDescription extends AbstractObjectDescription {

  /**
   * Creates a new object description.
   */
  public StringObjectDescription() {
    super( String.class );
    setParameterDefinition( "value", String.class );
  }

  /**
   * Creates an object based on this description.
   *
   * @return The object.
   */
  public Object createObject() {
    final String o = (String) getParameter( "value" );
    return String.valueOf( o );
  }

  /**
   * Sets the parameters of this description object to match the supplied object.
   *
   * @param o
   *          the object (should be an instance of <code>String</code>).
   * @throws ObjectFactoryException
   *           if the object is not an instance of <code>String</code>.
   */
  public void setParameterFromObject( final Object o ) throws ObjectFactoryException {
    if ( !( o instanceof String ) ) {
      throw new ObjectFactoryException( "The given object is no java.lang.String." );
    }

    setParameter( "value", String.valueOf( o ) );
  }
}
