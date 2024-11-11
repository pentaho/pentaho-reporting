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


package org.pentaho.reporting.engine.classic.core.wizard;

import org.pentaho.reporting.libraries.xmlns.common.AttributeMap;

public class DefaultDataAttributeReferences implements DataAttributeReferences {
  private AttributeMap<DataAttributeReference> backend;

  public DefaultDataAttributeReferences() {
    this.backend = new AttributeMap<DataAttributeReference>();
  }

  public void setReference( final String domain, final String name, final DataAttributeReference value ) {
    if ( domain == null ) {
      throw new NullPointerException();
    }
    if ( name == null ) {
      throw new NullPointerException();
    }
    backend.setAttribute( domain, name, value );
  }

  public String[] getMetaAttributeDomains() {
    return backend.getNameSpaces();
  }

  public String[] getMetaAttributeNames( final String domainName ) {
    if ( domainName == null ) {
      throw new NullPointerException();
    }
    return backend.getNames( domainName );
  }

  public DataAttributeReference getReference( final String domain, final String name ) {
    if ( domain == null ) {
      throw new NullPointerException();
    }
    if ( name == null ) {
      throw new NullPointerException();
    }

    return backend.getAttribute( domain, name );
  }

  public void merge( final DataAttributeReferences attributes ) {
    if ( attributes == null ) {
      throw new NullPointerException();
    }

    final String[] domains = attributes.getMetaAttributeDomains();
    for ( int i = 0; i < domains.length; i++ ) {
      final String domain = domains[i];
      final String[] names = attributes.getMetaAttributeNames( domain );
      for ( int j = 0; j < names.length; j++ ) {
        final String name = names[j];
        final DataAttributeReference value = attributes.getReference( domain, name );
        if ( value != null ) {
          backend.setAttribute( domain, name, value );
        }
      }
    }
  }
}
