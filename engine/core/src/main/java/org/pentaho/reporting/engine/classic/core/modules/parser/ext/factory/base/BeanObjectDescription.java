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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * An object-description for a bean object. This object description is very dangerous, if the bean contains properties
 * with undefined types.
 *
 * @author Thomas Morgner
 */
public class BeanObjectDescription extends AbstractObjectDescription {

  private static final Log logger = LogFactory.getLog( BeanObjectDescription.class );
  private HashSet ignoredParameters;
  /**
   * @noinspection InstanceVariableMayNotBeInitializedByReadObject
   */
  private transient HashMap properties;

  /**
   * Creates a new object description.
   *
   * @param className
   *          the class.
   */
  public BeanObjectDescription( final Class className ) {
    this( className, true );
  }

  /**
   * Creates a new object description.
   *
   * @param className
   *          the class.
   * @param init
   *          set to true, to autmaoticly initialise the object description. If set to false, the initialisation is
   *          elsewhere.
   */
  public BeanObjectDescription( final Class className, final boolean init ) {
    super( className );
    // now create some method descriptions ..
    this.ignoredParameters = new HashSet();
    readBeanDescription( className, init );
  }

  private boolean isValidMethod( final Method method, final int parCount ) {
    if ( method == null ) {
      return false;
    }
    if ( !Modifier.isPublic( method.getModifiers() ) ) {
      return false;
    }
    if ( Modifier.isStatic( method.getModifiers() ) ) {
      return false;
    }
    if ( method.getParameterTypes().length != parCount ) {
      return false;
    }
    return true;
  }

  /**
   * Creates an object based on this description.
   *
   * @return The object.
   */
  public Object createObject() {
    try {
      final Object o = getObjectClass().newInstance();
      // now add the various parameters ...

      final Iterator it = getParameterNames();
      while ( it.hasNext() ) {
        final String name = (String) it.next();

        if ( isParameterIgnored( name ) ) {
          continue;
        }

        final Method method = findSetMethod( name );
        final Object parameterValue = getParameter( name );
        if ( parameterValue != null ) {
          method.invoke( o, new Object[] { parameterValue } );
        }
      }
      return o;
    } catch ( Exception e ) {
      BeanObjectDescription.logger.error( "Unable to invoke bean method", e );
    }
    return null;
  }

  /**
   * Finds a set method in the bean.
   *
   * @param parameterName
   *          the parameter name.
   * @return The method.
   */
  private Method findSetMethod( final String parameterName ) {
    final PropertyDescriptor descriptor = (PropertyDescriptor) this.properties.get( parameterName );
    return descriptor.getWriteMethod();
  }

  /**
   * Finds a get method in the bean.
   *
   * @param parameterName
   *          the paramater name.
   * @return The method.
   */
  private Method findGetMethod( final String parameterName ) {
    final PropertyDescriptor descriptor = (PropertyDescriptor) this.properties.get( parameterName );
    return descriptor.getReadMethod();
  }

  /**
   * Sets the parameters in the description to match the supplied object.
   *
   * @param o
   *          the object (<code>null</code> not allowed).
   * @throws ObjectFactoryException
   *           if there is a problem.
   */
  public void setParameterFromObject( final Object o ) throws ObjectFactoryException {
    if ( o == null ) {
      throw new NullPointerException( "Given object is null" );
    }
    final Class c = getObjectClass();
    if ( !c.isInstance( o ) ) {
      throw new ObjectFactoryException( "Object is no instance of " + c + "(is " + o.getClass() + ')' );
    }

    final Iterator it = getParameterNames();
    while ( it.hasNext() ) {
      final String propertyName = (String) it.next();

      if ( isParameterIgnored( propertyName ) ) {
        continue;
      }

      try {
        final Method method = findGetMethod( propertyName );
        final Object retval = method.invoke( o, (Object[]) null );
        if ( retval != null ) {
          setParameter( propertyName, retval );
        }
      } catch ( Exception e ) {
        BeanObjectDescription.logger.info( "Exception on method invokation.", e );
      }

    }
  }

  /**
   * Adds a parameter to the ignored parameters.
   *
   * @param parameter
   *          the parameter.
   */
  protected void ignoreParameter( final String parameter ) {
    this.ignoredParameters.add( parameter );
  }

  /**
   * Returns a flag that indicates whether or not the specified parameter is ignored.
   *
   * @param parameter
   *          the parameter.
   * @return The flag.
   */
  protected boolean isParameterIgnored( final String parameter ) {
    return this.ignoredParameters.contains( parameter );
  }

  private void readObject( final ObjectInputStream in ) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    readBeanDescription( getObjectClass(), false );
  }

  private void readBeanDescription( final Class className, final boolean init ) {
    try {
      this.properties = new HashMap();

      final BeanInfo bi = Introspector.getBeanInfo( className );
      final PropertyDescriptor[] propertyDescriptors = bi.getPropertyDescriptors();
      for ( int i = 0; i < propertyDescriptors.length; i++ ) {
        final PropertyDescriptor propertyDescriptor = propertyDescriptors[i];
        final Method readMethod = propertyDescriptor.getReadMethod();
        final Method writeMethod = propertyDescriptor.getWriteMethod();
        if ( isValidMethod( readMethod, 0 ) && isValidMethod( writeMethod, 1 ) ) {
          final String name = propertyDescriptor.getName();
          this.properties.put( name, propertyDescriptor );
          if ( init ) {
            super.setParameterDefinition( name, propertyDescriptor.getPropertyType() );
          }
        }
      }
    } catch ( IntrospectionException e ) {
      BeanObjectDescription.logger.error( "Unable to build bean description", e );
    }
  }
}
