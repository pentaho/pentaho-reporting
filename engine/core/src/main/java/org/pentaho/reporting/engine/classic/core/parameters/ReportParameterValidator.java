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
