/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.reporting.engine.classic.core.metadata.parser;

import org.pentaho.reporting.engine.classic.core.metadata.AttributeMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.DefaultAttributeMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.builder.AttributeMetaDataBuilder;

public class AttributeDefinition {
  private AttributeMetaDataBuilder builder;

  public AttributeDefinition( final AttributeMetaDataBuilder builder ) {
    this.builder = builder.clone();
  }

  public String getNamespace() {
    return builder.getNamespace();
  }

  public String getName() {
    return builder.getName();
  }

  public AttributeMetaData build() {
    return new DefaultAttributeMetaData( builder );
  }
}
