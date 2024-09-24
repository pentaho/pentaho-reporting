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
