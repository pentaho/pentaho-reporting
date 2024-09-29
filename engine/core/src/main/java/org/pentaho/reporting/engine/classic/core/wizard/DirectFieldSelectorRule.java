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

import org.pentaho.reporting.engine.classic.core.MetaAttributeNames;

public class DirectFieldSelectorRule implements DataSchemaRule {
  private String field;
  private DataAttributes attributes;
  private DataAttributeReferences references;

  public DirectFieldSelectorRule( final String field, final DataAttributes attributes,
      final DataAttributeReferences references ) {
    if ( field == null ) {
      throw new NullPointerException();
    }
    if ( attributes == null ) {
      throw new NullPointerException();
    }
    if ( references == null ) {
      throw new NullPointerException();
    }

    this.field = field;
    this.attributes = attributes;
    this.references = references;
  }

  public String getFieldName() {
    return field;
  }

  public void setFieldName( final String fieldName ) {
    if ( fieldName == null ) {
      throw new NullPointerException();
    }

    this.field = fieldName;
  }

  public DataAttributes getStaticAttributes() {
    return attributes;
  }

  public DataAttributeReferences getMappedAttributes() {
    return references;
  }

  public boolean isMatch( final DataAttributes dataAttributes, final DataAttributeContext context ) {
    final Object name =
        dataAttributes.getMetaAttribute( MetaAttributeNames.Core.NAMESPACE, MetaAttributeNames.Core.NAME, String.class,
            context );
    return field.equals( name );
  }
}
