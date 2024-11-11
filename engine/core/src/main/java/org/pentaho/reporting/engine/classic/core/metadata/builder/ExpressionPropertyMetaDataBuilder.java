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


package org.pentaho.reporting.engine.classic.core.metadata.builder;

import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.metadata.DefaultExpressionPropertyCore;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionPropertyCore;
import org.pentaho.reporting.engine.classic.core.metadata.propertyeditors.SharedPropertyDescriptorProxy;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.ExpressionPropertyWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.UserDefinedExpressionPropertyReadHandler;

import java.beans.PropertyEditor;

public class ExpressionPropertyMetaDataBuilder extends MetaDataBuilder<ExpressionPropertyMetaDataBuilder> {
  private boolean mandatory;
  private boolean computed;
  private String valueRole;
  private Class<? extends PropertyEditor> editor;
  private ExpressionPropertyCore core;
  private SharedPropertyDescriptorProxy descriptor;
  private Class<? extends Expression> expression;
  private Class<? extends UserDefinedExpressionPropertyReadHandler> propertyReadHandler;
  private Class<? extends ExpressionPropertyWriteHandler> propertyWriteHandler;
  private boolean designTime;

  public ExpressionPropertyMetaDataBuilder() {
    this.core = new DefaultExpressionPropertyCore();
  }

  protected ExpressionPropertyMetaDataBuilder self() {
    return this;
  }

  public ExpressionPropertyMetaDataBuilder readHandler( Class<? extends UserDefinedExpressionPropertyReadHandler> handler ) {
    this.propertyReadHandler = handler;
    return self();
  }

  public ExpressionPropertyMetaDataBuilder writeHandler( Class<? extends ExpressionPropertyWriteHandler> handler ) {
    this.propertyWriteHandler = handler;
    return self();
  }

  public ExpressionPropertyMetaDataBuilder descriptor( SharedPropertyDescriptorProxy descriptor ) {
    this.descriptor = descriptor;
    return self();
  }

  public ExpressionPropertyMetaDataBuilder descriptorFromParent( Class<? extends Expression> expression ) {
    this.expression = expression;
    return self();
  }

  public ExpressionPropertyMetaDataBuilder mandatory( final boolean mandatory ) {
    this.mandatory = mandatory;
    return self();
  }

  public ExpressionPropertyMetaDataBuilder computed( final boolean computed ) {
    this.computed = computed;
    return self();
  }

  public ExpressionPropertyMetaDataBuilder valueRole( final String valueRole ) {
    this.valueRole = valueRole;
    return self();
  }

  public ExpressionPropertyMetaDataBuilder editor( final Class<? extends PropertyEditor> propertyEditorClass ) {
    this.editor = propertyEditorClass;
    return self();
  }

  public ExpressionPropertyMetaDataBuilder core( final ExpressionPropertyCore expressionPropertyCore ) {
    this.core = expressionPropertyCore;
    return self();
  }

  public ExpressionPropertyMetaDataBuilder designTime( boolean v ) {
    this.designTime = v;
    return self();
  }

  public boolean isMandatory() {
    return mandatory;
  }

  public boolean isComputed() {
    return computed;
  }

  public String getValueRole() {
    return valueRole;
  }

  public boolean isDesignTime() {
    return designTime;
  }

  public Class<? extends PropertyEditor> getEditor() {
    return editor;
  }

  public ExpressionPropertyCore getCore() {
    return core;
  }

  public SharedPropertyDescriptorProxy getDescriptor() {
    if ( descriptor != null ) {
      return descriptor;
    }
    if ( expression != null && getName() != null ) {
      return new SharedPropertyDescriptorProxy( expression, getName() );
    }
    return null;
  }

  public Class<? extends UserDefinedExpressionPropertyReadHandler> getPropertyReadHandler() {
    return propertyReadHandler;
  }

  public Class<? extends ExpressionPropertyWriteHandler> getPropertyWriteHandler() {
    return propertyWriteHandler;
  }
}
