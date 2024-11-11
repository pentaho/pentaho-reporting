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


package org.pentaho.reporting.libraries.base.config;

/**
 * The extended configuration provides methods to make using the configuration easier.
 *
 * @author Thomas Morgner
 */
public interface ExtendedConfiguration extends Configuration {
  /**
   * Checks, whether a given property is defined.
   *
   * @param name the name of the property
   * @return true, if the property is defined, false otherwise.
   */
  public boolean isPropertySet( String name );

  /**
   * Returns a given property as int value. Zero is returned if the property value is no number or the property is not
   * set.
   *
   * @param name the name of the property
   * @return the parsed number value or zero
   */
  public int getIntProperty( String name );

  /**
   * Returns a given property as int value. The specified default value is returned if the property value is no number
   * or the property is not set.
   *
   * @param name         the name of the property
   * @param defaultValue the value to be returned if the property is no integer value
   * @return the parsed number value or the specified default value
   */
  public int getIntProperty( String name, int defaultValue );

  /**
   * Returns the boolean value of a given configuration property. The boolean value true is returned, if the contained
   * string is equal to 'true'.
   *
   * @param name the name of the property
   * @return the boolean value of the property.
   */
  public boolean getBoolProperty( String name );

  /**
   * Returns the boolean value of a given configuration property. The boolean value true is returned, if the contained
   * string is equal to 'true'. If the property is not set, the default value is returned.
   *
   * @param name         the name of the property
   * @param defaultValue the default value to be returned if the property is not set
   * @return the boolean value of the property.
   */
  public boolean getBoolProperty( String name, boolean defaultValue );
}
