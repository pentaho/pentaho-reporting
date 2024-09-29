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

public class GlobalRule implements DataSchemaRule {
  private DataAttributes attributes;
  private DataAttributeReferences references;

  public GlobalRule( final DataAttributes attributes, final DataAttributeReferences references ) {
    if ( attributes == null ) {
      throw new NullPointerException();
    }
    if ( references == null ) {
      throw new NullPointerException();
    }
    this.attributes = attributes;
    this.references = references;
  }

  public DataAttributes getStaticAttributes() {
    return attributes;
  }

  public DataAttributeReferences getMappedAttributes() {
    return references;
  }

  public boolean isMatch( final DataAttributes dataAttributes, final DataAttributeContext context ) {
    return true;
  }
}
