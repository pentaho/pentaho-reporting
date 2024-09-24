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
