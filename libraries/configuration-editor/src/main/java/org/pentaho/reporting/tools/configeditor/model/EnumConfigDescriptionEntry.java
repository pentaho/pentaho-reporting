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
