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


package org.pentaho.reporting.engine.classic.core.util.beans;

/**
 * A value converter is an object that can transform an object into a string or vice versa.
 *
 * @author Thomas Morgner
 */
public interface ValueConverter {
  /**
   * Converts an object to an attribute value.
   *
   * @param o
   *          the object, never null.
   * @return the attribute value.
   * @throws BeanException
   *           if there was an error during the conversion.
   */
  public String toAttributeValue( Object o ) throws BeanException;

  /**
   * Converts a string to a property value.
   *
   * @param s
   *          the string, never null.
   * @return a property value.
   * @throws BeanException
   *           if there was an error during the conversion.
   */
  public Object toPropertyValue( String s ) throws BeanException;
}
