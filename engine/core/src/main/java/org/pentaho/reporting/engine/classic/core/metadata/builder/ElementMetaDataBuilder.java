/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

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
