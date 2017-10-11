/*
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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.function;

import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchema;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LocalizationContext;
import org.pentaho.reporting.libraries.formula.function.FunctionRegistry;
import org.pentaho.reporting.libraries.formula.operators.OperatorFactory;
import org.pentaho.reporting.libraries.formula.typing.DefaultTypeRegistry;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.TypeRegistry;
import org.pentaho.reporting.libraries.formula.typing.coretypes.AnyType;

import javax.swing.table.TableModel;
import java.util.Date;

/**
 * The report formula context is a FormulaContext implementation that connects the formula evaluator with the current
 * data-row of the report process.
 * <p/>
 * This is an internal class used by the FormulaExpression and FormulaFunction. It has no sensible usages outside of
 * that scope.
 *
 * @author Thomas Morgner
 */
public class ReportFormulaContext implements FormulaContext {
  /**
   * The formula context provided from the LibFormula implementation.
   */
  private FormulaContext backend;
  /**
   * The export-type as returned by the output-processor.
   */
  private ProcessingContext processingContext;
  private ExpressionRuntime runtime;
  private DefaultTypeRegistry typeRegistry;

  /**
   * Creates a new ReportFormulaContext using the given FormulaContext as backend. All data is read from the data-row.
   *
   * @param backend
   *          the formula-context backend.
   * @param runtime
   *          the ExpressionRuntime
   */
  public ReportFormulaContext( final FormulaContext backend, final ExpressionRuntime runtime ) {
    if ( runtime == null ) {
      throw new NullPointerException( "Runtime is null." );
    }
    if ( backend == null ) {
      throw new NullPointerException( "Backend-FormulaContext is null" );
    }

    this.runtime = runtime;
    this.backend = backend;
    this.typeRegistry = new DefaultTypeRegistry();
    this.typeRegistry.initialize( this );
    this.processingContext = runtime.getProcessingContext();
  }

  public DataSchema getDataSchema() {
    return runtime.getDataSchema();
  }

  /**
   * Returns the localization context of this formula. The localization context can be used to query locale specific
   * configuration settings.
   *
   * @return the localization context.
   */
  public LocalizationContext getLocalizationContext() {
    return backend.getLocalizationContext();
  }

  /**
   * Returns the local configuration of the formula.
   *
   * @return the local configuration.
   */
  public Configuration getConfiguration() {
    return backend.getConfiguration();
  }

  /**
   * Returns the function registry. The function registry grants access to all formula-function implementations.
   *
   * @return the function registry.
   */
  public FunctionRegistry getFunctionRegistry() {
    return backend.getFunctionRegistry();
  }

  /**
   * Returns the type registry. The type registry contains all type information and allows to convert values between
   * different types.
   *
   * @return the function registry.
   */
  public TypeRegistry getTypeRegistry() {
    return typeRegistry;
  }

  /**
   * Returns the operator registry. The Operator-registry contains all operator-implementations.
   *
   * @return the operator registry.
   */
  public OperatorFactory getOperatorFactory() {
    return backend.getOperatorFactory();
  }

  /**
   * Checks whether the external object referenced by <code>name</code> has changed. This forwards the call to the
   * data-row and checks, whether the value has changed since the last call to advance().
   *
   * @param name
   *          the name that identifies the reference.
   * @return true, if the reference has changed, false otherwise.
   * @throws EvaluationException
   *           if an error occurs.
   */
  public boolean isReferenceDirty( final Object name ) throws EvaluationException {
    return runtime.getDataRow().isChanged( (String) name );
  }

  /**
   * Resolves the given reference. How the name is interpreted by the outside system is an implementation detail. This
   * method always returns AnyType, as we do not interpret the values returned from the data-row.
   *
   * @param name
   *          the name that identifies the reference.
   * @return the resolved object.
   */
  public Type resolveReferenceType( final Object name ) {
    return AnyType.TYPE;
  }

  /**
   * Queries the type of the given reference. How the name is interpreted by the outside system is an implementation
   * detail. This return a LibFormula type object matching the type of the object that would be returned by
   * resolveReference.
   *
   * @param name
   *          the name that identifies the reference.
   * @return the type of the resolved object.
   * @throws EvaluationException
   *           if an error occurs.
   */
  public Object resolveReference( final Object name ) throws EvaluationException {
    if ( name == null ) {
      throw new NullPointerException();
    }
    return runtime.getDataRow().get( String.valueOf( name ) );
  }

  /**
   * Returns the current data-row.
   *
   * @return the current datarow.
   */
  public DataRow getDataRow() {
    return runtime.getDataRow();
  }

  /**
   * Invalidates the formula context.
   */
  public void close() {
    this.runtime = null;
    this.processingContext = null;
  }

  /**
   * Return the export type of the current report processing run.
   *
   * @return the current export type.
   */
  public String getExportType() {
    return processingContext.getExportDescriptor();
  }

  public ProcessingContext getProcessingContext() {
    return processingContext;
  }

  public boolean isResultSetEmpty() {
    final TableModel data = runtime.getData();
    return data == null || data.getRowCount() == 0 || data.getColumnCount() == 0;
  }

  public ExpressionRuntime getRuntime() {
    return runtime;
  }

  public Date getCurrentDate() {
    try {
      final Object date = resolveReference( "report.date" );
      if ( date instanceof Date ) {
        return (Date) date;
      }
    } catch ( EvaluationException e ) {
      // ignore
    }
    return new Date();
  }
}
