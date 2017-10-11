/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

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
