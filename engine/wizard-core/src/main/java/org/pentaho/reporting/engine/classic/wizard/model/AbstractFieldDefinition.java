/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

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
