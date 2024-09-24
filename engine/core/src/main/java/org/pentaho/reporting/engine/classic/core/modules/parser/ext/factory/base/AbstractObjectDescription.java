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

import org.pentaho.reporting.libraries.base.config.Configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

/**
 * An abstract base class for object descriptions.
 *
 * @author Thomas Morgner.
 */
public abstract class AbstractObjectDescription implements ObjectDescription, Cloneable {

  /**
   * The class.
   */
  private Class className;

  /**
   * Storage for parameters.
   */
  private HashMap parameters;

  /**
   * Storage for parameter definitions.
   */
  private HashMap parameterDefs;

  /**
   * The configuration for the object description.
   */
  private Configuration config;

  /**
   * Creates a new object description.
   *
   * @param className
   *          the class.
   */
  protected AbstractObjectDescription( final Class className ) {
    this.className = className;
    this.parameters = new HashMap();
    this.parameterDefs = new HashMap();
  }

  public Object clone() throws CloneNotSupportedException {
    final AbstractObjectDescription od = (AbstractObjectDescription) super.clone();
    od.parameterDefs = (HashMap) parameterDefs.clone();
    od.parameters = (HashMap) parameters.clone();
    return od;
  }

  /**
   * Returns a parameter class.
   *
   * @param name
   *          the parameter definition.
   * @return The class.
   */
  public Class getParameterDefinition( final String name ) {
    return (Class) this.parameterDefs.get( name );
  }

  /**
   * Sets the class for a parameter.
   *
   * @param name
   *          the parameter name.
   * @param obj
   *          the parameter class.
   */
  public void setParameterDefinition( final String name, final Class obj ) {
    if ( obj == null ) {
      this.parameterDefs.remove( name );
    } else {
      this.parameterDefs.put( name, obj );
    }
  }

  /**
   * Converts primitives to corresponding object class.
   *
   * @param obj
   *          the class.
   * @return The class.
   */
  public static Class convertPrimitiveClass( final Class obj ) {
    if ( !obj.isPrimitive() ) {
      return obj;
    }
    if ( obj == Boolean.TYPE ) {
      return Boolean.class;
    }
    if ( obj == Byte.TYPE ) {
      return Byte.class;
    }
    if ( obj == Character.TYPE ) {
      return Character.class;
    }
    if ( obj == Short.TYPE ) {
      return Short.class;
    }
    if ( obj == Integer.TYPE ) {
      return Integer.class;
    }
    if ( obj == Long.TYPE ) {
      return Long.class;
    }
    if ( obj == Float.TYPE ) {
      return Float.class;
    }
    if ( obj == Double.TYPE ) {
      return Double.class;
    }
    throw new IllegalArgumentException( "Class 'void' is not allowed here" );
  }

  /**
   * Sets a parameter.
   *
   * @param name
   *          the name.
   * @param value
   *          the value.
   */
  public void setParameter( final String name, final Object value ) {
    if ( getParameterDefinition( name ) == null ) {
      throw new IllegalArgumentException( "No such Parameter defined: " + name + " in class " + getObjectClass() );
    }
    if ( value == null ) {
      parameters.remove( name );
      return;
    }

    final Class parameterClass = AbstractObjectDescription.convertPrimitiveClass( getParameterDefinition( name ) );
    if ( !parameterClass.isAssignableFrom( value.getClass() ) ) {
      throw new ClassCastException( "In Object " + getObjectClass() + ": Value is not assignable: " + value.getClass()
          + " is not assignable from " + parameterClass );
    }
    this.parameters.put( name, value );
  }

  /**
   * Returns an iterator for the parameter names.
   *
   * @return The iterator.
   */
  public synchronized Iterator getParameterNames() {
    final ArrayList parameterNames = new ArrayList( this.parameterDefs.keySet() );
    Collections.sort( parameterNames );
    return parameterNames.iterator();
  }

  /**
   * Returns an iterator for the parameter names.
   *
   * @return The iterator.
   */
  protected Iterator getDefinedParameterNames() {
    final ArrayList parameterNames = new ArrayList( this.parameters.keySet() );
    return parameterNames.iterator();
  }

  /**
   * Returns a parameter value.
   *
   * @param name
   *          the parameter name.
   * @return The parameter value.
   */
  public Object getParameter( final String name ) {
    return this.parameters.get( name );
  }

  /**
   * Returns the class for the object.
   *
   * @return The class.
   */
  public Class getObjectClass() {
    return this.className;
  }

  /**
   * Returns a cloned instance of the object description. The contents of the parameter objects collection are cloned
   * too, so that any already defined parameter value is copied to the new instance.
   * <p/>
   * Parameter definitions are not cloned, as they are considered read-only.
   * <p/>
   * The newly instantiated object description is not configured. If it need to be configured, then you have to call
   * configure on it.
   *
   * @return A cloned instance.
   */
  public ObjectDescription getInstance() {
    try {
      final AbstractObjectDescription c = (AbstractObjectDescription) super.clone();
      c.parameters = (HashMap) this.parameters.clone();
      return c;
    } catch ( Exception e ) {
      throw new IllegalStateException( "Should not happen: Error on clone" );
    }
  }

  /**
   * Returns a cloned instance of the object description. The contents of the parameter objects collection are cloned
   * too, so that any already defined parameter value is copied to the new instance.
   * <p/>
   * Parameter definitions are not cloned, as they are considered read-only.
   * <p/>
   * The newly instantiated object description is not configured. If it need to be configured, then you have to call
   * configure on it.
   *
   * @return A cloned instance.
   */
  public ObjectDescription getUnconfiguredInstance() {
    try {
      final AbstractObjectDescription c = (AbstractObjectDescription) super.clone();
      c.parameters = (HashMap) this.parameters.clone();
      c.config = null;
      return c;
    } catch ( Exception e ) {
      throw new IllegalStateException( "Should not happen: Error on clone" );
    }
  }

  /**
   * Configures this factory. The configuration contains several keys and their defined values. The given reference to
   * the configuration object will remain valid until the report parsing or writing ends.
   * <p/>
   * The configuration contents may change during the reporting.
   *
   * @param config
   *          the configuration, never null
   */
  public void configure( final Configuration config ) {
    if ( config == null ) {
      throw new NullPointerException( "The given configuration is null" );
    }
    this.config = config;
  }

  /**
   * Returns the configuration for that object description.
   *
   * @return the configuration or null, if not yet set.
   */
  public Configuration getConfig() {
    return this.config;
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

    if ( !this.className.equals( abstractObjectDescription.className ) ) {
      return false;
    }

    return true;
  }

  /**
   * Returns a hash code for the object.
   *
   * @return The hash code.
   */
  public int hashCode() {
    return this.className.hashCode();
  }
}
