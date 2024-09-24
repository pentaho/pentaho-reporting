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

package org.pentaho.reporting.engine.classic.core.wizard;

import java.io.Serializable;

import org.pentaho.reporting.libraries.xmlns.common.AttributeMap;

public class DefaultDataAttributes implements DataAttributes {
  private static class Entry implements Serializable {
    public final ConceptQueryMapper mapper;
    public final Object value;

    private Entry( final ConceptQueryMapper mapper, final Object value ) {
      this.mapper = mapper;
      this.value = value;
    }
  }

  private AttributeMap<Entry> backend;

  public DefaultDataAttributes() {
    this.backend = new AttributeMap<Entry>();
  }

  public void setMetaAttribute( final String domain,
                                final String name,
                                final ConceptQueryMapper conceptMapper,
                                final Object value ) {
    if ( name == null ) {
      throw new NullPointerException();
    }
    if ( domain == null ) {
      throw new NullPointerException();
    }
    backend.setAttribute( domain, name, new Entry( conceptMapper, value ) );
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
    if (attribute.value == null) {
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

  public void merge( final DataAttributes attributes,
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

  public void mergeReferences( final DataAttributeReferences references,
                               final DataAttributeContext context ) {
    if ( references == null ) {
      throw new NullPointerException();
    }
    if ( context == null ) {
      throw new NullPointerException();
    }
    final String[] domains = references.getMetaAttributeDomains();
    for ( int i = 0; i < domains.length; i++ ) {
      final String domain = domains[ i ];
      final String[] names = references.getMetaAttributeNames( domain );
      for ( int j = 0; j < names.length; j++ ) {
        final String name = names[ j ];
        final DataAttributeReference ref = references.getReference( domain, name );
        final Object value = ref.resolve( this, context );
        if ( value != null ) {
          ConceptQueryMapper conceptQueryMapper = ref.resolveMapper( this );
          backend.setAttribute( domain, name, new Entry( conceptQueryMapper, value ) );
        }
      }
    }
  }

  public Object clone() throws CloneNotSupportedException {
    final DefaultDataAttributes o = (DefaultDataAttributes) super.clone();
    o.backend = backend.clone();
    return o;
  }


  public boolean isEmpty() {
    return backend.getNameSpaces().length == 0;
  }

}
