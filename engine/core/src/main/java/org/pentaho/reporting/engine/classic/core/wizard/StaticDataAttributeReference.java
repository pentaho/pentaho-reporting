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
