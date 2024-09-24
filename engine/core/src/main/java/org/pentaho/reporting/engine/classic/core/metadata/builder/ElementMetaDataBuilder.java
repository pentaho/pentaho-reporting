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

package org.pentaho.reporting.engine.classic.core.metadata.builder;

import org.pentaho.reporting.engine.classic.core.metadata.AttributeMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.metadata.StyleMetaData;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.libraries.xmlns.common.AttributeMap;

import java.util.LinkedHashMap;
import java.util.Map;

public class ElementMetaDataBuilder extends MetaDataBuilder<ElementMetaDataBuilder> {
  private ElementMetaData.TypeClassification reportElementType;
  private AttributeMap<AttributeMetaData> attributes;
  private LinkedHashMap<StyleKey, StyleMetaData> styles;
  private Class<? extends ElementType> elementType;
  private Class<?> contentType;
  private String namespace;

  public ElementMetaDataBuilder() {
    this.styles = new LinkedHashMap<StyleKey, StyleMetaData>();
    this.attributes = new AttributeMap<AttributeMetaData>();
    this.reportElementType = ElementMetaData.TypeClassification.DATA;
    this.contentType = Object.class;
  }

  protected ElementMetaDataBuilder self() {
    return this;
  }

  public ElementMetaDataBuilder namespace( final String namespace ) {
    this.namespace = namespace;
    return self();
  }

  public ElementMetaDataBuilder typeClassification( final ElementMetaData.TypeClassification t ) {
    this.reportElementType = t;
    return this;
  }

  public ElementMetaDataBuilder contentType( final Class<?> contentType ) {
    this.contentType = contentType;
    return this;
  }

  public ElementMetaDataBuilder elementType( final Class<? extends ElementType> t ) {
    this.elementType = t;
    return this;
  }

  public ElementMetaDataBuilder attributes( final AttributeMap<AttributeMetaData> attrs ) {
    this.attributes.putAll( attrs );
    return this;
  }

  public ElementMetaDataBuilder attribute( final AttributeMetaData attrs ) {
    this.attributes.setAttribute( attrs.getNameSpace(), attrs.getName(), attrs );
    return this;
  }

  public ElementMetaDataBuilder styles( final Map<StyleKey, StyleMetaData> styles ) {
    this.styles.putAll( styles );
    return this;
  }

  public ElementMetaDataBuilder style( final StyleMetaData styles ) {
    this.styles.put( styles.getStyleKey(), styles );
    return this;
  }

  public Map<StyleKey, StyleMetaData> stylesRef() {
    return styles;
  }

  public ElementMetaData.TypeClassification getReportElementType() {
    return reportElementType;
  }

  public AttributeMap<AttributeMetaData> getAttributes() {
    return attributes.clone();
  }

  public AttributeMap<AttributeMetaData> attributesRef() {
    return attributes;
  }

  public Map<StyleKey, StyleMetaData> getStyles() {
    return (Map<StyleKey, StyleMetaData>) styles.clone();
  }

  public Class<? extends ElementType> getElementType() {
    return elementType;
  }

  public Class<?> getContentType() {
    return contentType;
  }

  public String getNamespace() {
    return namespace;
  }
}
