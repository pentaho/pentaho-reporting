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

import org.pentaho.reporting.engine.classic.core.metadata.AttributeCore;

import java.beans.PropertyEditor;

public class AttributeMetaDataBuilder extends MetaDataBuilder<AttributeMetaDataBuilder> {

  private String valueRole;
  private boolean bulk;
  private String namespace;
  private String namespacePrefix;
  private Class<?> targetClass;
  private boolean mandatory;
  private boolean computed;
  private boolean transientFlag;
  private boolean designTime;
  private AttributeCore core;

  private Class<? extends PropertyEditor> propertyEditor;

  public AttributeMetaDataBuilder() {
  }

  public AttributeMetaDataBuilder propertyEditor( Class<? extends PropertyEditor> propertyEditor ) {
    this.propertyEditor = propertyEditor;
    return self();
  }

  public AttributeMetaDataBuilder valueRole( String valueRole ) {
    this.valueRole = valueRole;
    return self();
  }

  public AttributeMetaDataBuilder core( AttributeCore core ) {
    this.core = core;
    return self();
  }

  public AttributeMetaDataBuilder bulk( boolean v ) {
    this.bulk = v;
    return self();
  }

  public AttributeMetaDataBuilder designTime( boolean v ) {
    this.designTime = v;
    return self();
  }

  public AttributeMetaDataBuilder computed( boolean v ) {
    this.computed = v;
    return self();
  }

  public AttributeMetaDataBuilder mandatory( boolean v ) {
    this.mandatory = v;
    return self();
  }

  public AttributeMetaDataBuilder transientFlag( boolean v ) {
    this.transientFlag = v;
    return self();
  }

  public AttributeMetaDataBuilder namespace( final String namespace ) {
    this.namespace = namespace;
    return self();
  }

  public AttributeMetaDataBuilder namespacePrefix( final String namespace ) {
    this.namespacePrefix = namespace;
    return self();
  }

  public AttributeMetaDataBuilder targetClass( final Class<?> targetClass ) {
    this.targetClass = targetClass;
    return self();
  }

  public String getValueRole() {
    return valueRole;
  }

  public boolean isBulk() {
    return bulk;
  }

  public String getNamespace() {
    return namespace;
  }

  public Class<?> getTargetClass() {
    return targetClass;
  }

  public boolean isMandatory() {
    return mandatory;
  }

  public boolean isComputed() {
    return computed;
  }

  public boolean isTransientFlag() {
    return transientFlag;
  }

  public boolean isDesignTime() {
    return designTime;
  }

  public AttributeCore getCore() {
    return core;
  }

  public String getNamespacePrefix() {
    return namespacePrefix;
  }

  public Class<? extends PropertyEditor> getPropertyEditor() {
    return propertyEditor;
  }

  protected AttributeMetaDataBuilder self() {
    return this;
  }
}
