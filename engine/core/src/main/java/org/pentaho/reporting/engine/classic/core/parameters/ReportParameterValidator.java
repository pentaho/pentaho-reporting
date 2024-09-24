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

package org.pentaho.reporting.engine.classic.core.parameters;

import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;

import java.io.Serializable;

/**
 * The report parameter validator is responsible for validating the values provided by the user. The parameters must be
 * valid before the reporting can start. Validation can happen any time in the parametrization process, but will be
 * always executed by the report processor and make the report-processing fail if the validator fails.
 *
 * @author Thomas Morgner
 */
public interface ReportParameterValidator extends Serializable {
  /**
   * Validates the parameter set.
   *
   * @param result
   *          the validation result, null to create a new one.
   * @param parameterDefinition
   *          the parameter definitions.
   * @param parameterContext
   *          the parameter context
   * @return the validation result, never null.
   */
  public ValidationResult validate( final ValidationResult result, final ReportParameterDefinition parameterDefinition,
      final ParameterContext parameterContext ) throws ReportDataFactoryException, ReportProcessingException;
}
