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
