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
* Copyright (c) 2008 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

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
