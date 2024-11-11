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


package org.pentaho.reporting.engine.classic.core.metadata;

public class DefaultAttributeRegistry implements AttributeRegistry {
  private DefaultElementMetaData elementMetaData;

  public DefaultAttributeRegistry( final DefaultElementMetaData elementMetaData ) {
    if ( elementMetaData == null ) {
      throw new NullPointerException();
    }
    this.elementMetaData = elementMetaData;
  }

  public void putAttributeDescription( final AttributeMetaData metaData ) {
    this.elementMetaData.setAttributeDescription( metaData.getNameSpace(), metaData.getName(), metaData );
  }

  public AttributeMetaData getAttributeDescription( final String namespace, final String name ) {
    return elementMetaData.getAttributeDescription( namespace, name );
  }
}
