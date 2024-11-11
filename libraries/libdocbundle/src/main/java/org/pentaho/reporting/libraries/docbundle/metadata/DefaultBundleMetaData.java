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


package org.pentaho.reporting.libraries.docbundle.metadata;

import org.pentaho.reporting.libraries.xmlns.common.AttributeMap;

import java.io.Serializable;
import java.util.Map;

public class DefaultBundleMetaData implements BundleMetaData, Serializable, Cloneable {
  private AttributeMap<Object> attributeMap;
  private static final long serialVersionUID = -3626983495621673501L;

  public DefaultBundleMetaData() {
    attributeMap = new AttributeMap<Object>();
  }

  public DefaultBundleMetaData( final BundleMetaData metaData ) {
    this();
    if ( metaData != null ) {
      final String[] namespaces = metaData.getNamespaces();
      for ( int namespaceIdx = 0; namespaceIdx < namespaces.length; namespaceIdx++ ) {
        final String namespace = namespaces[ namespaceIdx ];
        final String[] names = metaData.getNames( namespace );
        for ( int namesIdx = 0; namesIdx < names.length; namesIdx++ ) {
          final String name = names[ namesIdx ];
          attributeMap.setAttribute( namespace, name, metaData.getBundleAttribute( namespace, name ) );
        }
      }
    }
  }

  public void putBundleAttribute( final String namespace, final String attributeName, final Object value ) {
    if ( namespace == null ) {
      throw new NullPointerException();
    }
    if ( attributeName == null ) {
      throw new NullPointerException();
    }

    attributeMap.setAttribute( namespace, attributeName, value );
  }

  public Object getBundleAttribute( final String namespace, final String attributeName ) {
    if ( namespace == null ) {
      throw new NullPointerException();
    }
    if ( attributeName == null ) {
      throw new NullPointerException();
    }
    return attributeMap.getAttribute( namespace, attributeName );
  }

  public String[] getNamespaces() {
    return attributeMap.getNameSpaces();
  }

  public String[] getNames( final String namespace ) {
    if ( namespace == null ) {
      throw new NullPointerException();
    }

    final Map<String, Object> map = attributeMap.getAttributes( namespace );
    return map.keySet().toArray( new String[ map.size() ] );
  }

  public Object clone() throws CloneNotSupportedException {
    final DefaultBundleMetaData metaData = (DefaultBundleMetaData) super.clone();
    metaData.attributeMap = (AttributeMap<Object>) attributeMap.clone();
    return metaData;
  }
}
