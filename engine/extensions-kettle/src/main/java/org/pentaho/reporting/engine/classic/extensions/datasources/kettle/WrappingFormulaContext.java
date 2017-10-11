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
