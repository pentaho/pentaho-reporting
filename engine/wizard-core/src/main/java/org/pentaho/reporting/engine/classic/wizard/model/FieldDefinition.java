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

import java.io.Serializable;

public interface FieldDefinition extends Serializable, Cloneable {
  public String getNullString();

  public void setNullString( String nullString );

  public String getDisplayName();

  public void setDisplayName( String name );

  public String getField();

  public void setField( String field );

  public String getFieldAggregation();

  public void setFieldAggregation( String fieldAggregation );

  public Class getFieldTypeHint();

  public void setFieldTypeHint( Class c );

  public Class getAggregationFunction();

  public void setAggregationFunction( Class c );

  public String getDataFormat();

  public void setDataFormat( String name );

  public Length getWidth();

  public void setWidth( Length length );

  public Object clone() throws CloneNotSupportedException;
}
