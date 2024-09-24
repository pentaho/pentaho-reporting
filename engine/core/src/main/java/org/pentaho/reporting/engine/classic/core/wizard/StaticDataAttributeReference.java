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

public class StaticDataAttributeReference implements DataAttributeReference {
  private String name;
  private String domain;
  private Class type;
  private ConceptQueryMapper queryMapper;

  public StaticDataAttributeReference( final String domain, final String name ) {
    this( domain, name, Object.class, DefaultConceptQueryMapper.INSTANCE );
  }

  public StaticDataAttributeReference( final String domain, final String name, final Class type,
      final ConceptQueryMapper queryMapper ) {
    if ( domain == null ) {
      throw new NullPointerException();
    }
    if ( name == null ) {
      throw new NullPointerException();
    }
    if ( queryMapper == null ) {
      throw new NullPointerException();
    }
    this.domain = domain;
    this.name = name;
    this.type = type;
    this.queryMapper = queryMapper;
  }

  public String getDomain() {
    return domain;
  }

  public String getName() {
    return name;
  }

  public Object resolve( final DataAttributes attributes, final DataAttributeContext context ) {
    if ( attributes == null ) {
      throw new NullPointerException();
    }
    if ( context == null ) {
      throw new NullPointerException();
    }
    final Object value = attributes.getMetaAttribute( domain, name, null, context );
    return queryMapper.getValue( value, type, context );
  }

  public ConceptQueryMapper resolveMapper( DataAttributes attributes ) {
    return queryMapper;
  }
}
