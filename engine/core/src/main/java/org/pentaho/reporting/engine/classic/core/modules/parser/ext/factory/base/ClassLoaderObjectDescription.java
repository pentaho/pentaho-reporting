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

import org.pentaho.reporting.engine.classic.core.modules.parser.base.compat.CompatibilityMapperUtil;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

/**
 * An object-description for a class loader.
 *
 * @author Thomas Morgner
 */
public class ClassLoaderObjectDescription extends AbstractObjectDescription {
  private static final Class[] EMPTY_PARAMS = new Class[0];

  /**
   * Creates a new object description.
   */
  public ClassLoaderObjectDescription() {
    super( Object.class );
    setParameterDefinition( "class", String.class );
  }

  /**
   * Creates an object based on this object description.
   *
   * @return The object.
   */
  public Object createObject() {
    try {
      final String o = (String) getParameter( "class" );
      return ObjectUtilities.loadAndInstantiate( CompatibilityMapperUtil.mapClassName( o ), getClass(), null );
    } catch ( Exception e ) {
      return null;
    }
  }

  /**
   * Sets the parameters of the object description to match the supplied object.
   *
   * @param o
   *          the object.
   * @throws ObjectFactoryException
   *           if there is a problem while reading the properties of the given object.
   */
  public void setParameterFromObject( final Object o ) throws ObjectFactoryException {
    if ( o == null ) {
      throw new ObjectFactoryException( "The Object is null." );
    }
    try {
      final Constructor c = o.getClass().getConstructor( ClassLoaderObjectDescription.EMPTY_PARAMS );
      if ( !Modifier.isPublic( c.getModifiers() ) ) {
        throw new ObjectFactoryException( "The given object has no public default constructor. [" + o.getClass() + ']' );
      }
      setParameter( "class", o.getClass().getName() );
    } catch ( Exception e ) {
      throw new ObjectFactoryException( "The given object has no default constructor. [" + o.getClass() + ']', e );
    }
  }
}
