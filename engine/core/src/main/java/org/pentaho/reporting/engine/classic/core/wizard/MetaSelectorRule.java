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
