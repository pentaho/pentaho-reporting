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

package org.pentaho.reporting.engine.classic.core.metadata;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.metadata.builder.ElementMetaDataBuilder;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.libraries.xmlns.common.AttributeMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DefaultElementMetaData extends AbstractMetaData implements ElementMetaData {
  private static final Log logger = LogFactory.getLog( DefaultElementMetaData.class );

  private transient AttributeMetaData[] attributesAsArray;
  private transient StyleMetaData[] stylesArray;
  private AttributeMap<AttributeMetaData> attributes;
  private Map<StyleKey, StyleMetaData> styles;
  private Class<? extends ElementType> elementType;
  private TypeClassification reportElementType;
  private Class<?> contentType;
  private String namespace;

  public DefaultElementMetaData( final String name, final String bundleLocation, final String keyPrefix,
      final String namespace, final boolean expert, final boolean preferred, final boolean hidden,
      final boolean deprecated, final TypeClassification reportElementType,
      final AttributeMap<AttributeMetaData> attributes, final Map<StyleKey, StyleMetaData> styles,
      final Class<? extends ElementType> elementType, final Class<?> contentType, final MaturityLevel maturityLevel,
      final int compatibilityLevel ) {
    super( name, bundleLocation, keyPrefix, expert, preferred, hidden, deprecated, maturityLevel, compatibilityLevel );
    if ( styles == null ) {
      throw new NullPointerException();
    }
    if ( attributes == null ) {
      throw new NullPointerException();
    }
    if ( elementType == null ) {
      throw new NullPointerException();
    }
    if ( contentType == null ) {
      throw new NullPointerException();
    }
    if ( namespace == null ) {
      throw new NullPointerException();
    }

    this.contentType = contentType;
    this.reportElementType = reportElementType;
    this.attributes = attributes.clone();
    this.styles = new HashMap<StyleKey, StyleMetaData>( styles );
    this.elementType = elementType;
    this.namespace = namespace;
  }

  public DefaultElementMetaData( final ElementMetaDataBuilder builder ) {
    super( builder );
    this.contentType = builder.getContentType();
    this.reportElementType = builder.getReportElementType();
    this.attributes = builder.getAttributes();
    this.styles = builder.getStyles();
    this.elementType = builder.getElementType();
    this.namespace = builder.getNamespace();

    if ( this.namespace == null ) {
      throw new IllegalArgumentException();
    }
  }

  public DefaultElementMetaData( final ElementMetaData metaData ) {
    super( metaData );
    this.contentType = metaData.getContentType();
    this.reportElementType = metaData.getReportElementType();
    this.elementType = metaData.getElementType();
    this.namespace = metaData.getNamespace();
    if ( this.namespace == null ) {
      throw new IllegalArgumentException();
    }

    this.styles = new HashMap<StyleKey, StyleMetaData>();
    final StyleMetaData[] styleDescriptions = metaData.getStyleDescriptions();
    for ( int i = 0; i < styleDescriptions.length; i++ ) {
      final StyleMetaData styleMetaData = styleDescriptions[i];
      this.styles.put( styleMetaData.getStyleKey(), styleMetaData );
    }

    this.attributes = new AttributeMap<AttributeMetaData>();
    final AttributeMetaData[] attributeDescriptions = metaData.getAttributeDescriptions();
    for ( int i = 0; i < attributeDescriptions.length; i++ ) {
      final AttributeMetaData attributeDescription = attributeDescriptions[i];
      this.attributes.setAttribute( attributeDescription.getNameSpace(), attributeDescription.getName(),
          attributeDescription );
    }
  }

  public AttributeMetaData[] getAttributeDescriptions() {
    if ( attributesAsArray == null ) {
      final ArrayList<AttributeMetaData> buffer = new ArrayList<AttributeMetaData>();
      final String[] namespaces = attributes.getNameSpaces();
      for ( int i = 0; i < namespaces.length; i++ ) {
        final String namespace = namespaces[i];
        final Map<String, AttributeMetaData> attrsNs = attributes.getAttributes( namespace );
        final Iterator<Map.Entry<String, AttributeMetaData>> it = attrsNs.entrySet().iterator();
        while ( it.hasNext() ) {
          final Map.Entry<String, AttributeMetaData> entry = it.next();
          final AttributeMetaData exp = entry.getValue();
          buffer.add( exp );
        }
      }
      attributesAsArray = buffer.toArray( new AttributeMetaData[buffer.size()] );
    }
    return attributesAsArray.clone();
  }

  public StyleMetaData[] getStyleDescriptions() {
    if ( stylesArray == null ) {
      stylesArray = styles.values().toArray( new StyleMetaData[styles.size()] );
    }
    return stylesArray;
  }

  public AttributeMetaData getAttributeDescription( final String namespace, final String name ) {
    if ( namespace == null ) {
      throw new NullPointerException();
    }
    if ( name == null ) {
      throw new NullPointerException();
    }

    final AttributeMetaData attribute = attributes.getAttribute( namespace, name );
    if ( attribute == null ) {
      if ( logger.isDebugEnabled() ) {
        logger.debug( String.format( "No metadata defined for attribute [%s:%s] on element-type %s", namespace, name,
            getName() ) );
      }
    }
    return attribute;
  }

  public void setAttributeDescription( final String namespace, final String name, final AttributeMetaData metaData ) {
    if ( namespace == null ) {
      throw new NullPointerException();
    }
    if ( name == null ) {
      throw new NullPointerException();
    }
    if ( metaData == null ) {
      throw new NullPointerException();
    }

    attributes.setAttribute( namespace, name, metaData );
  }

  public StyleMetaData getStyleDescription( final StyleKey name ) {
    if ( name == null ) {
      throw new NullPointerException();
    }

    return styles.get( name );
  }

  public ElementType create() throws InstantiationException {
    try {
      return elementType.newInstance();
    } catch ( IllegalAccessException e ) {
      throw new InstantiationException( "Unable to instantiate " + elementType + ": IllegalAccessException caught" );
    }
  }

  public boolean isContainerElement() {
    return reportElementType != TypeClassification.DATA;
  }

  public TypeClassification getReportElementType() {
    return reportElementType;
  }

  public Class<?> getContentType() {
    return contentType;
  }

  public Class<? extends ElementType> getElementType() {
    return elementType;
  }

  public String getNamespace() {
    return namespace;
  }
}
