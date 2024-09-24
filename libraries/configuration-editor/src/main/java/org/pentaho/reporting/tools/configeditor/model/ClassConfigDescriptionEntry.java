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

package org.pentaho.reporting.tools.configeditor.model;

/**
 * A config description entry that describes class name configurations. The specified class in the configuration is
 * forced to be a subclass of the specified base class.
 *
 * @author Thomas Morgner
 */
public class ClassConfigDescriptionEntry extends ConfigDescriptionEntry {
  /**
   * The base class for the configuration value.
   */
  private Class baseClass;

  /**
   * Creates a new config description entry.
   *
   * @param keyName the full name of the key.
   */
  public ClassConfigDescriptionEntry( final String keyName ) {
    super( keyName );
    baseClass = Object.class;
  }

  /**
   * Returns the base class used to verify the configuration values.
   *
   * @return the base class or Object.class if not specified otherwise.
   */
  public Class getBaseClass() {
    return baseClass;
  }

  /**
   * Defines the base class for this configuration entry.
   *
   * @param baseClass the base class, never null.
   */
  public void setBaseClass( final Class baseClass ) {
    if ( baseClass == null ) {
      throw new NullPointerException();
    }
    this.baseClass = baseClass;
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }
    if ( !super.equals( o ) ) {
      return false;
    }

    final ClassConfigDescriptionEntry that = (ClassConfigDescriptionEntry) o;
    if ( baseClass != null ? !baseClass.equals( that.baseClass ) : that.baseClass != null ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    int result = super.hashCode();
    result = 29 * result + ( baseClass != null ? baseClass.hashCode() : 0 );
    return result;
  }
}
