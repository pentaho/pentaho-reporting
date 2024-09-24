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
* Copyright (c) 2000 - 2017 Hitachi Vantara and Contributors...
* All rights reserved.
*/

package org.pentaho.reporting.engine.classic.extensions.datasources.pmd.types;

import org.pentaho.metadata.model.concept.security.Security;
import org.pentaho.metadata.model.concept.security.SecurityOwner;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class SecurityWrapper extends Security {
  private Security backend;

  public SecurityWrapper( final Security backend ) {
    this.backend = backend;
  }

  public int hashCode() {
    return backend.getOwnerAclMap().hashCode();
  }

  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append( "SecurityWrapper" );
    // maps are not order guaranteed, so I'm sorting by keys before printing the map
    sb.append( "{ownerAclMap=" ).append( new TreeMap<SecurityOwner, Integer>( backend.getOwnerAclMap() ) );
    sb.append( '}' );
    return sb.toString();
  }

  @Override
  public boolean equals( final Object object ) {
    if ( object == null ) {
      return false;
    }
    if ( object instanceof Security == false ) {
      return false;
    }
    return backend.equals( object );
  }

  public Map<SecurityOwner, Integer> getOwnerAclMap() {
    return backend.getOwnerAclMap();
  }

  public int getOwnerRights( final SecurityOwner owner ) {
    return backend.getOwnerRights( owner );
  }

  public Set<SecurityOwner> getOwners() {
    return backend.getOwners();
  }

  public void putOwnerRights( final SecurityOwner owner, final int rights ) {
    backend.putOwnerRights( owner, rights );
  }

  public void removeOwnerRights( final SecurityOwner owner ) {
    backend.removeOwnerRights( owner );
  }

  public void setOwnerAclMap( final Map<SecurityOwner, Integer> ownerAclMap ) {
    backend.setOwnerAclMap( ownerAclMap );
  }
}
