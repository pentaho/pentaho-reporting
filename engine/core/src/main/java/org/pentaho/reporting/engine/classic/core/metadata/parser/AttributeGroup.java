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


package org.pentaho.reporting.engine.classic.core.metadata.parser;

/**
 * A internal helper object to store an attribute-group definition.
 *
 * @author Thomas Morgner
 */
public class AttributeGroup {
  private String name;
  private AttributeDefinition[] metaData;

  public AttributeGroup( final String name, final AttributeDefinition[] metaData ) {
    if ( name == null ) {
      throw new NullPointerException();
    }
    if ( metaData == null ) {
      throw new NullPointerException();
    }
    this.name = name;
    this.metaData = metaData.clone();
  }

  public String getName() {
    return name;
  }

  public AttributeDefinition[] getMetaData() {
    return (AttributeDefinition[]) metaData.clone();
  }
}
