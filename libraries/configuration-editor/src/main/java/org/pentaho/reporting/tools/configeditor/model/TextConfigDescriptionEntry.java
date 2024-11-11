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

/**
 * The text config description entry represents an configuration key, where users may enter free-form text.
 *
 * @author Thomas Morgner
 */
public class TextConfigDescriptionEntry extends ConfigDescriptionEntry {
  /**
   * Creates a new text description entry for the given configuration key.
   *
   * @param keyName the keyname of this entry.
   */
  public TextConfigDescriptionEntry( final String keyName ) {
    super( keyName );
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
    return true;
  }

  public int hashCode() {
    return super.hashCode();
  }
}
