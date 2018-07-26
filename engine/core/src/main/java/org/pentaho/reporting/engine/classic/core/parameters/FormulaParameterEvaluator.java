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
 * Copyright (c) 2002-2018 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.parameters;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ReportEnvironmentDataRow;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.function.FormulaExpression;
import org.pentaho.reporting.engine.classic.core.util.ReportParameterValues;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.engine.classic.core.util.beans.ConverterRegistry;
import org.pentaho.reporting.engine.classic.core.util.beans.ValueConverter;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.formula.ErrorValue;

import java.util.Locale;

import static org.pentaho.reporting.engine.classic.core.parameters.ParameterUtils.getLocale;

public class FormulaParameterEvaluator {
  private static final Log logger = LogFactory.getLog( FormulaParameterEvaluator.class );

  private FormulaParameterEvaluator() {
  }

  /**
   * @param result
   * @param parameterDefinition
   * @param parameterContext
   * @return
   * @deprecated This method is unsafe. Do not use it or you may open up your application to all kinds of security
   *             risks. The method will be removed in the next release.
   */
  public static ReportParameterValues evaluate( final ValidationResult result,
      final ReportParameterDefinition parameterDefinition, final ParameterContext parameterContext ) {

    final ReportParameterValues parameterValues = new ReportParameterValues();
    parameterValues.putAll( parameterContext.getParameterData() );
    final ParameterDefinitionEntry[] entries = parameterDefinition.getParameterDefinitions();
    for ( int i = 0; i < entries.length; i++ ) {
      final ParameterDefinitionEntry entry = entries[i];
      try {
        final Object o =
            computePostProcessingValue( result, parameterContext, parameterValues, entry, parameterValues.get( entry
                .getName() ), entry.getDefaultValue( parameterContext ) );
        parameterValues.put( entry.getName(), o );
      } catch ( ReportProcessingException e ) {
        if ( logger.isDebugEnabled() ) {
          logger.debug( "Unable to compute default value for parameter '" + entry.getName() + "'", e );
        }
        if ( result != null ) {
          final Locale locale = getLocale( parameterContext.getReportEnvironment() );
          result.addError( entry.getName(), new ValidationMessage( Messages.getInstance( locale ).formatMessage(
              "FormulaParameterEvaluator.PostProcessingInitFailed", e.getLocalizedMessage() ) ) );
        }
      }

    }

    return parameterValues;
  }

  public static Object computePostProcessingValue( final ValidationResult result,
      final ParameterContext parameterContext, final ReportParameterValues parameterValues,
      final ParameterDefinitionEntry entry, final Object untrustedValue, final Object defaultValue )
    throws ReportProcessingException {
    final ReportEnvironmentDataRow envDataRow = new ReportEnvironmentDataRow( parameterContext.getReportEnvironment() );
    final String formula =
        entry.getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
            ParameterAttributeNames.Core.POST_PROCESSOR_FORMULA, parameterContext );
    if ( StringUtils.isEmpty( formula, true ) ) {
      return untrustedValue;
    }

    final ParameterExpressionRuntime runtime =
        new ParameterExpressionRuntime( parameterContext, new CompoundDataRow( envDataRow, parameterValues ) );
    return computeValue( runtime, formula, result, entry, defaultValue );
  }

  public static Object computeValue( final ExpressionRuntime runtime, final String formula,
      final ValidationResult result, final ParameterDefinitionEntry entry, final Object defaultValue ) {
    final FormulaExpression fe = new FormulaExpression();
    fe.setFormula( formula );
    fe.setRuntime( runtime );
    final Object value = fe.getValue();
    final Locale locale = getLocale( runtime.getProcessingContext().getEnvironment() );
    if ( value == null ) {
      final Exception error = fe.getFormulaError();
      if ( error != null ) {
        if ( result != null ) {
          result.addError( entry.getName(), new ValidationMessage( Messages.getInstance( locale ).formatMessage(
              "FormulaParameterEvaluator.PostProcessingFormulaFailed", error.getLocalizedMessage() ) ) );
        }

        if ( logger.isDebugEnabled() ) {
          logger.debug( "Unable to compute default value for parameter '" + entry.getName() + "'", error );
        }
        // if the value is a hard error, we return <null> instead of the default value.
        // This way, a mandatory parameter will not continue in case of eval-errors.
        return null;
      }

      return defaultValue;
    } else if ( entry.getValueType().isInstance( value ) ) {
      return value;
    } else if ( value instanceof ErrorValue ) {
      final ErrorValue errorValue = (ErrorValue) value;
      if ( result != null ) {
        result.addError( entry.getName(),
            new ValidationMessage( Messages.getInstance( locale ).formatMessage(
                "FormulaParameterEvaluator.PostProcessingFormulaFailed",
                errorValue.getErrorMessage( Locale.getDefault() ) ) ) );
      }
      // if the value is a hard error, we return <null> instead of the default value.
      // This way, a mandatory parameter will not continue in case of eval-errors.
      return null;
    } else {
      final ValueConverter valueConverter = ConverterRegistry.getInstance().getValueConverter( entry.getValueType() );
      if ( valueConverter != null ) {
        // try to convert it; if this conversion fails we resort to String.valueOf,
        // but it will take care of converting dates and number subtypes correctly ..
        String textValue;
        try {
          textValue = ConverterRegistry.toAttributeValue( value );
        } catch ( BeanException be ) {
          textValue = String.valueOf( value );
        }

        try {
          return ConverterRegistry.toPropertyValue( textValue, entry.getValueType() );
        } catch ( BeanException e ) {
          if ( logger.isDebugEnabled() ) {
            logger.debug( "Unable to convert computed default value for parameter '" + entry.getName() + "'", e );
          }
          if ( result != null ) {
            result.addError( entry.getName(), new ValidationMessage( Messages.getInstance( locale ).getString(
                "FormulaParameterEvaluator.ErrorConvertingValue" ) ) );
            result.addError( entry.getName(), new ValidationMessage(
                "The post-processing result cannot be converted into the target-type." ) );
          }
          return null;
        }
      }
      return defaultValue;
    }

  }
}
