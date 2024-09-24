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

public class MetaSelectorRule implements DataSchemaRule {
  private MetaSelector[] selectors;
  private DataAttributes attributes;
  private DataAttributeReferences references;

  public MetaSelectorRule( final MetaSelector[] selectors, final DataAttributes attributes,
      final DataAttributeReferences references ) {
    if ( selectors == null ) {
      throw new NullPointerException();
    }
    if ( attributes == null ) {
      throw new NullPointerException();
    }
    if ( references == null ) {
      throw new NullPointerException();
    }
    this.attributes = attributes;
    this.references = references;
    this.selectors = (MetaSelector[]) selectors.clone();
  }

  public DataAttributes getStaticAttributes() {
    return attributes;
  }

  public MetaSelector[] getSelectors() {
    return (MetaSelector[]) selectors.clone();
  }

  public DataAttributeReferences getMappedAttributes() {
    return references;
  }

  public boolean isMatch( final DataAttributes dataAttributes, final DataAttributeContext context ) {
    if ( context == null ) {
      throw new NullPointerException();
    }
    if ( dataAttributes == null ) {
      throw new NullPointerException();
    }
    for ( int i = 0; i < selectors.length; i++ ) {
      final MetaSelector selector = selectors[i];
      final String domain = selector.getDomain();
      final String name = selector.getName();
      final Object value = selector.getValue();
      if ( value == null ) {
        if ( dataAttributes.getMetaAttribute( domain, name, null, context ) == null ) {
          return false;
        }
      } else {
        final Object attrValue = dataAttributes.getMetaAttribute( domain, name, null, context );
        if ( value.equals( attrValue ) == false ) {
          return false;
        }
      }
    }
    return true;
  }
}
