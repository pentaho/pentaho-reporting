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

import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.metadata.DefaultReportPreProcessorPropertyCore;
import org.pentaho.reporting.engine.classic.core.metadata.ReportPreProcessorPropertyCore;
import org.pentaho.reporting.engine.classic.core.metadata.propertyeditors.SharedPropertyDescriptorProxy;

import java.beans.PropertyEditor;

public class ReportPreProcessorPropertyMetaDataBuilder extends
    MetaDataBuilder<ReportPreProcessorPropertyMetaDataBuilder> {
  private boolean mandatory;
  private boolean computed;
  private String valueRole;
  private Class<? extends PropertyEditor> editor;
  private ReportPreProcessorPropertyCore core;
  private SharedPropertyDescriptorProxy descriptor;
  private Class<? extends Expression> expression;

  public ReportPreProcessorPropertyMetaDataBuilder() {
    this.core = new DefaultReportPreProcessorPropertyCore();
  }

  protected ReportPreProcessorPropertyMetaDataBuilder self() {
    return this;
  }

  public ReportPreProcessorPropertyMetaDataBuilder descriptor( SharedPropertyDescriptorProxy descriptor ) {
    this.descriptor = descriptor;
    return self();
  }

  public ReportPreProcessorPropertyMetaDataBuilder descriptorFromParent( Class<? extends Expression> expression ) {
    this.expression = expression;
    return self();
  }

  public ReportPreProcessorPropertyMetaDataBuilder mandatory( final boolean mandatory ) {
    this.mandatory = mandatory;
    return self();
  }

  public ReportPreProcessorPropertyMetaDataBuilder computed( final boolean computed ) {
    this.computed = computed;
    return self();
  }

  public ReportPreProcessorPropertyMetaDataBuilder valueRole( final String valueRole ) {
    this.valueRole = valueRole;
    return self();
  }

  public ReportPreProcessorPropertyMetaDataBuilder editor( final Class<? extends PropertyEditor> propertyEditorClass ) {
    this.editor = propertyEditorClass;
    return self();
  }

  public ReportPreProcessorPropertyMetaDataBuilder core( final ReportPreProcessorPropertyCore expressionPropertyCore ) {
    this.core = expressionPropertyCore;
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

  public Class<? extends PropertyEditor> getEditor() {
    return editor;
  }

  public ReportPreProcessorPropertyCore getCore() {
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
}
