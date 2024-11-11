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


package org.pentaho.reporting.designer.core.editor.groups;

import org.pentaho.reporting.engine.classic.core.util.InstanceID;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class GroupDataEntry {
  private static final String[] EMPTY_FIELDS = new String[ 0 ];

  private String[] fields;
  private String name;
  private InstanceID instanceID;

  public GroupDataEntry( final InstanceID instanceID, final String name, final String[] fields ) {
    this.instanceID = instanceID;
    this.name = name;
    this.fields = fields.clone();
  }

  public InstanceID getInstanceID() {
    return instanceID;
  }

  public String[] getFields() {
    return fields.clone();
  }

  public String getName() {
    return name;
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final GroupDataEntry that = (GroupDataEntry) o;

    if ( instanceID != null ? !instanceID.equals( that.instanceID ) : that.instanceID != null ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    return ( instanceID != null ? instanceID.hashCode() : 0 );
  }

  /**
   * @noinspection MagicCharacter
   */
  public String getFieldsAsText() {
    final StringBuffer buffer = new StringBuffer( 100 );
    buffer.append( '[' );
    final String[] strings = getFields();
    for ( int i = 0; i < strings.length; i++ ) {
      if ( i != 0 ) {
        buffer.append( ", " );
      }
      final String string = strings[ i ];
      buffer.append( string );
    }
    buffer.append( ']' );
    return ( buffer.toString() );
  }
}
