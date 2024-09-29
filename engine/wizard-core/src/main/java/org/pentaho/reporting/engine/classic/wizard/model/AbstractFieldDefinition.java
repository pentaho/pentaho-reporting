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


package org.pentaho.reporting.engine.classic.wizard.model;

public abstract class AbstractFieldDefinition extends AbstractElementFormatDefinition implements FieldDefinition {
  private String nullString;
  private String displayName;
  private String dataFormat;
  private Class aggregationFunction;
  private String field;
  private String fieldAggregation;
  private Class fieldTypeHint;
  private Length width;

  protected AbstractFieldDefinition() {
  }

  protected AbstractFieldDefinition( final String field ) {
    this.field = field;
  }

  public String getField() {
    return field;
  }

  public void setField( final String field ) {
    this.field = field;
  }

  public String getNullString() {
    return nullString;
  }

  public void setNullString( final String nullString ) {
    this.nullString = nullString;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName( final String displayName ) {
    this.displayName = displayName;
  }

  public String getDataFormat() {
    return dataFormat;
  }

  public void setDataFormat( final String dataFormat ) {
    this.dataFormat = dataFormat;
  }

  public Class getAggregationFunction() {
    return aggregationFunction;
  }

  public void setAggregationFunction( final Class aggregationFunction ) {
    this.aggregationFunction = aggregationFunction;
  }

  public Class getFieldTypeHint() {
    return fieldTypeHint;
  }

  public void setFieldTypeHint( final Class fieldTypeHint ) {
    this.fieldTypeHint = fieldTypeHint;
  }

  public Length getWidth() {
    return width;
  }

  public void setWidth( final Length width ) {
    this.width = width;
  }

  public String getFieldAggregation() {
    return fieldAggregation;
  }

  public void setFieldAggregation( String fieldAggregation ) {
    this.fieldAggregation = fieldAggregation;
  }

}
