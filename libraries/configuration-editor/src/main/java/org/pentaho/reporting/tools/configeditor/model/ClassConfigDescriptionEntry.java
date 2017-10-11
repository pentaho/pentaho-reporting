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
