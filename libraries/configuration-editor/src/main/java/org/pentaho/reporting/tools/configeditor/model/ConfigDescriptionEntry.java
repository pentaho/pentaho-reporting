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
 * A config description entry provides a declaration of a single report configuration key and speicifes rules for the
 * values of that key.
 *
 * @author Thomas Morgner
 */
public abstract class ConfigDescriptionEntry {
  /**
   * A description of the given key.
   */
  private String description;
  /**
   * The fully qualified name of the key.
   */
  private String keyName;
  /**
   * a flag defining whether this is a boot time key.
   */
  private boolean global;
  /**
   * a flag defining whether this is a hidden key.
   */
  private boolean hidden;

  /**
   * Creates a new config description entry with the given name.
   *
   * @param keyName the name of the entry.
   */
  protected ConfigDescriptionEntry( final String keyName ) {
    if ( keyName == null ) {
      throw new NullPointerException();
    }
    this.keyName = keyName;
  }

  /**
   * Returns the full key name of the configuration description.
   *
   * @return the key name.
   */
  public String getKeyName() {
    return keyName;
  }

  /**
   * Returns the descrption of the configuration entry.
   *
   * @return the key description.
   */
  public String getDescription() {
    return description;
  }

  /**
   * Defines the descrption of the configuration entry.
   *
   * @param description the key description.
   */
  public void setDescription( final String description ) {
    this.description = description;
  }

  /**
   * Returns, whether the key is a global key. Global keys are read from the global report configuration and specifying
   * them in the report local configuration is useless.
   *
   * @return true, if the key is global, false otherwise.
   */
  public boolean isGlobal() {
    return global;
  }

  /**
   * Defines, whether the key is a global key. Global keys are read from the global report configuration and specifying
   * them in the report local configuration is useless.
   *
   * @param global set to true, if the key is global, false otherwise.
   */
  public void setGlobal( final boolean global ) {
    this.global = global;
  }

  /**
   * Returns, whether the key is hidden. Hidden keys will not be visible in the configuration editor.
   *
   * @return true, if the key is hidden, false otherwise
   */
  public boolean isHidden() {
    return hidden;
  }

  /**
   * Defines, whether the key is hidden. Hidden keys will not be visible in the configuration editor.
   *
   * @param hidden set to true, if the key is hidden, false otherwise
   */
  public void setHidden( final boolean hidden ) {
    this.hidden = hidden;
  }

  /**
   * Checks whether the given object is equal to this config description entry. The object will be equal, if it is also
   * an config description entry with the same name as this entry.
   *
   * @param o the other object.
   * @return true, if the config entry is equal to the given object, false otherwise.
   * @see Object#equals(Object)
   */
  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( !( o instanceof ConfigDescriptionEntry ) ) {
      return false;
    }

    final ConfigDescriptionEntry configDescriptionEntry = (ConfigDescriptionEntry) o;

    if ( !keyName.equals( configDescriptionEntry.keyName ) ) {
      return false;
    }

    return true;
  }

  /**
   * Computes an hashcode for this object.
   *
   * @return the hashcode.
   * @see Object#hashCode()
   */
  public int hashCode() {
    return keyName.hashCode();
  }
}
