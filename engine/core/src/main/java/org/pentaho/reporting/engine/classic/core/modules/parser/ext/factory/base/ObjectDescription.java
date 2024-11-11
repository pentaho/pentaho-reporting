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

import org.pentaho.reporting.libraries.base.config.Configuration;

import java.io.Serializable;
import java.util.Iterator;

/**
 * An interface for object descriptions.
 *
 * @author Thomas Morgner
 */
public interface ObjectDescription extends Serializable {

  /**
   * Returns a parameter definition. If the parameter is invalid, this function returns null.
   *
   * @param name
   *          the definition name.
   * @return The parameter class or null, if the parameter is not defined.
   */
  public Class getParameterDefinition( String name );

  /**
   * Sets the value of a parameter.
   *
   * @param name
   *          the parameter name.
   * @param value
   *          the parameter value.
   */
  public void setParameter( String name, Object value );

  /**
   * Returns the value of a parameter.
   *
   * @param name
   *          the parameter name.
   * @return The value.
   */
  public Object getParameter( String name );

  /**
   * Returns an iterator the provides access to the parameter names. This returns all _known_ parameter names, the
   * object description may accept additional parameters.
   *
   * @return The iterator.
   */
  public Iterator getParameterNames();

  /**
   * Returns the object class.
   *
   * @return The Class.
   */
  public Class getObjectClass();

  /**
   * Creates an object based on the description.
   *
   * @return The object.
   */
  public Object createObject();

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
  public ObjectDescription getUnconfiguredInstance();

  /**
   * Returns a cloned instance of the object description. The contents of the parameter objects collection are cloned
   * too, so that any already defined parameter value is copied to the new instance.
   * <p/>
   * Parameter definitions are not cloned, as they are considered read-only.
   *
   * @return A cloned instance.
   */
  public ObjectDescription getInstance();

  /**
   * Sets the parameters of this description object to match the supplied object.
   *
   * @param o
   *          the object.
   * @throws ObjectFactoryException
   *           if there is a problem while reading the properties of the given object.
   */
  public void setParameterFromObject( Object o ) throws ObjectFactoryException;

  /**
   * Configures this factory. The configuration contains several keys and their defined values. The given reference to
   * the configuration object will remain valid until the report parsing or writing ends.
   * <p/>
   * The configuration contents may change during the reporting.
   *
   * @param config
   *          the configuration, never null
   */
  public void configure( Configuration config );

  /**
   * Compares whether two object descriptions are equal.
   *
   * @param o
   *          the other object.
   * @return true, if both object desciptions describe the same object, false otherwise.
   */
  public boolean equals( Object o );

  /**
   * Computes the hashCode for this ClassFactory. As equals() must be implemented, a corresponding hashCode() should be
   * implemented as well.
   *
   * @return the hashcode.
   */
  public int hashCode();

}
