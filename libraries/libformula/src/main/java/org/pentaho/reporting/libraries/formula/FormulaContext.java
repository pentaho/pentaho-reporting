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

package org.pentaho.reporting.libraries.formula;

import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.formula.function.FunctionRegistry;
import org.pentaho.reporting.libraries.formula.operators.OperatorFactory;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.TypeRegistry;

import java.util.Date;

/**
 * The formula-context connects the formula functions with the outside world. The context can be used to resolve
 * external references, to query the configuration or to retrieve information about the formula-evaluation system.
 *
 * @author Thomas Morgner
 */
public interface FormulaContext {
  /**
   * Checks whether the external object referenced by <code>name</code> has changed.
   *
   * @param name the name that identifies the reference.
   * @return true, if the reference has changed, false otherwise.
   * @throws EvaluationException if an error occurs.
   */
  public boolean isReferenceDirty( Object name ) throws EvaluationException;

  /**
   * Resolves the given reference. How the name is interpreted by the outside system is an implementation detail.
   *
   * @param name the name that identifies the reference.
   * @return the resolved object.
   * @throws EvaluationException if an error occurs.
   */
  public Object resolveReference( Object name ) throws EvaluationException;

  /**
   * Queries the type of the given reference. How the name is interpreted by the outside system is an implementation
   * detail. This return a LibFormula type object matching the type of the object that would be returned by
   * resolveReference.
   *
   * @param name the name that identifies the reference.
   * @return the type of the resolved object.
   * @throws EvaluationException if an error occurs.
   */
  public Type resolveReferenceType( Object name ) throws EvaluationException;

  /**
   * Returns the localization context of this formula. The localization context can be used to query locale specific
   * configuration settings.
   *
   * @return the localization context.
   */
  public LocalizationContext getLocalizationContext();

  /**
   * Returns the local configuration of the formula.
   *
   * @return the local configuration.
   */
  public Configuration getConfiguration();

  /**
   * Returns the function registry. The function registry grants access to all formula-function implementations.
   *
   * @return the function registry.
   */
  public FunctionRegistry getFunctionRegistry();

  /**
   * Returns the type registry. The type registry contains all type information and allows to convert values between
   * different types.
   *
   * @return the function registry.
   */
  public TypeRegistry getTypeRegistry();

  /**
   * Returns the operator registry. The Operator-registry contains all operator-implementations.
   *
   * @return the operator registry.
   */
  public OperatorFactory getOperatorFactory();

  /**
   * Returns the current date.
   *
   * @return the date.
   */
  public Date getCurrentDate();
}
