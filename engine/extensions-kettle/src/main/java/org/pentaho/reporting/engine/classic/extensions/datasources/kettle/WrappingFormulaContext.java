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

package org.pentaho.reporting.engine.classic.extensions.datasources.kettle;

import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LocalizationContext;
import org.pentaho.reporting.libraries.formula.function.FunctionRegistry;
import org.pentaho.reporting.libraries.formula.operators.OperatorFactory;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.TypeRegistry;
import org.pentaho.reporting.libraries.formula.typing.coretypes.AnyType;

import java.util.Date;

public class WrappingFormulaContext implements FormulaContext {
  private DataRow dataRow;
  private FormulaContext parent;

  public WrappingFormulaContext( final FormulaContext parent, final DataRow dataRow ) {
    this.parent = parent;
    this.dataRow = dataRow;
  }

  public boolean isReferenceDirty( final Object name ) throws EvaluationException {
    return dataRow.isChanged( String.valueOf( name ) );
  }

  public Object resolveReference( final Object name ) throws EvaluationException {
    return dataRow.get( String.valueOf( name ) );
  }

  public Type resolveReferenceType( final Object name ) throws EvaluationException {
    return AnyType.TYPE;
  }

  public LocalizationContext getLocalizationContext() {
    return parent.getLocalizationContext();
  }

  public Configuration getConfiguration() {
    return parent.getConfiguration();
  }

  public FunctionRegistry getFunctionRegistry() {
    return parent.getFunctionRegistry();
  }

  public TypeRegistry getTypeRegistry() {
    return parent.getTypeRegistry();
  }

  public OperatorFactory getOperatorFactory() {
    return parent.getOperatorFactory();
  }

  public Date getCurrentDate() {
    return parent.getCurrentDate();
  }
}
