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

import java.util.Arrays;

/**
 * The enumeration config description entry represents an configuration key, where users may select a valid value from a
 * predefined list of elements. Such an key will not allow free-form text.
 *
 * @author Thomas Morgner
 */
public class EnumConfigDescriptionEntry extends ConfigDescriptionEntry {
  /**
   * The list of available options in this entry.
   */
  private String[] options;
  private static final String[] EMPTY_STRINGS = new String[ 0 ];

  /**
   * Creates a new enumeration description entry for the given configuration key.
   *
   * @param keyName the keyname of this entry.
   */
  public EnumConfigDescriptionEntry( final String keyName ) {
    super( keyName );
    this.options = EnumConfigDescriptionEntry.EMPTY_STRINGS;
  }

  /**
   * Returns all options from this entry as array.
   *
   * @return the options as array.
   */
  public String[] getOptions() {
    return options.clone();
  }

  /**
   * Defines all options for this entry.
   *
   * @param options the selectable values for this entry.
   */
  public void setOptions( final String[] options ) {
    this.options = options.clone();
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

    final EnumConfigDescriptionEntry that = (EnumConfigDescriptionEntry) o;

    if ( !Arrays.equals( options, that.options ) ) {
      return false;
    }

    return true;
  }


  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + Arrays.hashCode( options );
    return result;
  }
}
