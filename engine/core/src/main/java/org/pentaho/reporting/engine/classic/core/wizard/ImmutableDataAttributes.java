/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.wizard;

import java.io.Serializable;

import org.pentaho.reporting.libraries.xmlns.common.AttributeMap;

public final class ImmutableDataAttributes implements DataAttributes {
  public static final ImmutableDataAttributes EMPTY = new ImmutableDataAttributes();

  private static class Entry implements Serializable {
    public final ConceptQueryMapper mapper;
    public final Object value;

    private Entry( final ConceptQueryMapper mapper, final Object value ) {
      this.mapper = mapper;
      this.value = value;
    }

    public boolean equals( final Object o ) {
      if ( this == o ) {
        return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
        return false;
      }

      final Entry entry = (Entry) o;

      if ( mapper != null ? !mapper.equals( entry.mapper ) : entry.mapper != null ) {
        return false;
      }
      if ( value != null ? !value.equals( entry.value ) : entry.value != null ) {
        return false;
      }

      return true;
    }

    public int hashCode() {
      int result = mapper != null ? mapper.hashCode() : 0;
      result = 31 * result + ( value != null ? value.hashCode() : 0 );
      return result;
    }
  }

  private AttributeMap<Entry> backend;

  private ImmutableDataAttributes() {
    this.backend = new AttributeMap<Entry>();
  }

  private ImmutableDataAttributes( DataAttributes source, DataAttributeContext context ) {
    this.backend = new AttributeMap<Entry>();
    merge( source, context );
  }

  public ImmutableDataAttributes( AttributeMap<Object> data ) {
    this.backend = new AttributeMap<Entry>();
    for ( AttributeMap.DualKey k : data.keySet() ) {
      final Object value = data.getAttribute( k.namespace, k.name );
      this.backend.setAttribute( k.namespace, k.name, new Entry( DefaultConceptQueryMapper.INSTANCE, value ) );
    }
  }

  public static ImmutableDataAttributes create( DataAttributes source, DataAttributeContext context ) {
    if ( source instanceof ImmutableDataAttributes ) {
      return (ImmutableDataAttributes) source;
    } else {
      return new ImmutableDataAttributes( source, context );
    }
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

  public Object getMetaAttribute( final String domain,
                                  final String name,
                                  final Class type,
                                  final DataAttributeContext context ) {
    if ( domain == null ) {
      throw new NullPointerException();
    }
    if ( name == null ) {
      throw new NullPointerException();
    }
    if ( context == null ) {
      throw new NullPointerException();
    }
    return getMetaAttribute( domain, name, type, context, null );
  }

  public Object getMetaAttribute( final String domain,
                                  final String name,
                                  final Class type,
                                  final DataAttributeContext context,
                                  final Object defaultValue ) {
    if ( domain == null ) {
      throw new NullPointerException();
    }
    if ( name == null ) {
      throw new NullPointerException();
    }
    if ( context == null ) {
      throw new NullPointerException();
    }

    final Entry attribute = backend.getAttribute( domain, name );
    if ( attribute == null ) {
      return defaultValue;
    }
    if ( attribute.value == null ) {
      return defaultValue;
    }
    final ConceptQueryMapper mapper = attribute.mapper;
    return mapper.getValue( attribute.value, type, context );
  }

  public ConceptQueryMapper getMetaAttributeMapper( final String domain, final String name ) {
    if ( domain == null ) {
      throw new NullPointerException();
    }
    if ( name == null ) {
      throw new NullPointerException();
    }
    final Entry attribute = backend.getAttribute( domain, name );
    if ( attribute == null ) {
      return DefaultConceptQueryMapper.INSTANCE;
    }

    return attribute.mapper;
  }

  private void merge( final DataAttributes attributes,
                      final DataAttributeContext context ) {
    if ( attributes == null ) {
      throw new NullPointerException();
    }
    if ( context == null ) {
      throw new NullPointerException();
    }

    final String[] domains = attributes.getMetaAttributeDomains();
    for ( int i = 0; i < domains.length; i++ ) {
      final String domain = domains[ i ];
      final String[] names = attributes.getMetaAttributeNames( domain );
      for ( int j = 0; j < names.length; j++ ) {
        final String name = names[ j ];
        final Object value = attributes.getMetaAttribute( domain, name, null, context );
        if ( value != null ) {
          ConceptQueryMapper mapper = attributes.getMetaAttributeMapper( domain, name );
          backend.setAttribute( domain, name, new Entry( mapper, value ) );
        }
      }
    }
  }

  public ImmutableDataAttributes clone() throws CloneNotSupportedException {
    final ImmutableDataAttributes o = (ImmutableDataAttributes) super.clone();
    o.backend = backend.clone();
    return o;
  }

  public boolean isEmpty() {
    return backend.getNameSpaces().length == 0;
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final ImmutableDataAttributes that = (ImmutableDataAttributes) o;

    if ( !backend.equals( that.backend ) ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    return backend.hashCode();
  }
}
