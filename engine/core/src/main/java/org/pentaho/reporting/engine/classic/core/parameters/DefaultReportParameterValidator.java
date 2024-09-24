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
 * Copyright (c) 2001 - 2020 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.parameters;

import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.PerformanceTags;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.ReportEnvironment;
import org.pentaho.reporting.engine.classic.core.ReportEnvironmentDataRow;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.states.PerformanceMonitorContext;
import org.pentaho.reporting.engine.classic.core.util.ReportParameterValues;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.PerformanceLoggingStopWatch;
import org.pentaho.reporting.libraries.docbundle.DocumentMetaData;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Locale;

import static org.pentaho.reporting.engine.classic.core.parameters.ParameterUtils.getLocale;

public class DefaultReportParameterValidator implements ReportParameterValidator {
  private static class TrustedParameterContext implements ParameterContext {
    private ParameterContext context;
    private ReportEnvironmentDataRow environmentDataRow;
    private ReportParameterValues trustedValues;

    private TrustedParameterContext( final ParameterContext context ) {
      this.context = context;
      this.environmentDataRow = new ReportEnvironmentDataRow( context.getReportEnvironment() );
      this.trustedValues = new ReportParameterValues();
    }

    public DataRow getParameterData() {
      return new CompoundDataRow( environmentDataRow, trustedValues );
    }

    public ReportParameterValues getTrustedValues() {
      return trustedValues;
    }

    public DocumentMetaData getDocumentMetaData() {
      return context.getDocumentMetaData();
    }

    public ReportEnvironment getReportEnvironment() {
      return context.getReportEnvironment();
    }

    public DataFactory getDataFactory() {
      return context.getDataFactory();
    }

    public ResourceBundleFactory getResourceBundleFactory() {
      return context.getResourceBundleFactory();
    }

    public ResourceKey getContentBase() {
      return context.getContentBase();
    }

    public ResourceManager getResourceManager() {
      return context.getResourceManager();
    }

    public Configuration getConfiguration() {
      return context.getConfiguration();
    }

    public void close() throws ReportDataFactoryException {
      // not needed..
    }

    public PerformanceMonitorContext getPerformanceMonitorContext() {
      return context.getPerformanceMonitorContext();
    }
  }

  private static final Log logger = LogFactory.getLog( DefaultReportParameterValidator.class );

  public DefaultReportParameterValidator() {
  }

  public ValidationResult validate( ValidationResult result, final ReportParameterDefinition parameterDefinition,
      final ParameterContext parameterContext ) throws ReportProcessingException {
    if ( parameterContext == null ) {
      throw new NullPointerException();
    }
    if ( parameterDefinition == null ) {
      throw new NullPointerException();
    }

    if ( result == null ) {
      result = new ValidationResult();
    }

    PerformanceLoggingStopWatch sw =
        parameterContext.getPerformanceMonitorContext().createStopWatch( PerformanceTags.REPORT_PARAMETER );
    try {
      sw.start();

      final TrustedParameterContext trustedParameterContext = new TrustedParameterContext( parameterContext );
      final ParameterDefinitionEntry[] parameterDefinitionEntries = parameterDefinition.getParameterDefinitions();

      for ( int i = 0; i < parameterDefinitionEntries.length; i++ ) {
        final ParameterDefinitionEntry parameterDefinitionEntry = parameterDefinitionEntries[i];
        final String parameterName = parameterDefinitionEntry.getName();
        final Object untrustedValue = parameterContext.getParameterData().get( parameterName );

        validateSingleParameter( result, trustedParameterContext, parameterDefinitionEntry, untrustedValue );
      }
      result.setParameterValues( trustedParameterContext.getTrustedValues() );
      return result;
    } finally {
      sw.close();
    }
  }

  private void validateSingleParameter( final ValidationResult result,
      final TrustedParameterContext trustedParameterContext, final ParameterDefinitionEntry parameterDefinitionEntry,
      Object untrustedValue ) throws ReportProcessingException {
    final boolean reevaluatePossible = untrustedValue != null;

    Object defaultValue = null;
    if ( untrustedValue == null ) {
      // compute the default value
      defaultValue = parameterDefinitionEntry.getDefaultValue( trustedParameterContext );
      untrustedValue = defaultValue;
    }

    if ( logger.isDebugEnabled() ) {
      logger.debug( "On Validate Single Parameter: " + parameterDefinitionEntry.getName() );
      logger.debug( "On Validate Single Parameter: " + trustedParameterContext.getParameterData() );
      logger.debug( "On Validate Single Parameter: " + untrustedValue );
      logger.debug( "On Validate Single Parameter: ------------------------------" );
    }
    final String parameterName = parameterDefinitionEntry.getName();
    final ReportParameterValues tempValue = new ReportParameterValues( trustedParameterContext.getTrustedValues() );
    tempValue.put( parameterName, untrustedValue );
    final Object computedValue =
        FormulaParameterEvaluator.computePostProcessingValue( result, trustedParameterContext, tempValue,
            parameterDefinitionEntry, untrustedValue, defaultValue );

    final Locale locale = getLocale( trustedParameterContext.getReportEnvironment() );

    if ( isValueMissingForMandatoryParameterCheck( parameterDefinitionEntry, computedValue ) ) {
      // as the post processing expression failed or returned <null>, the computed value
      // must be <null> or an error. We report an error (which stops the report processing)
      // and set the default value as current value, so that the other parameters can continue.
      trustedParameterContext.getTrustedValues().put( parameterName, null );
      result.addError( parameterName, new ValidationMessage( Messages.getInstance( locale ).getString(
          "DefaultReportParameterValidator.ParameterIsMandatory" ) ) );
      return;
    }

    if ( parameterDefinitionEntry instanceof ListParameter == false ) {
      if ( computedValue != null ) {
        final Class parameterType = parameterDefinitionEntry.getValueType();
        if ( parameterType.isInstance( computedValue ) == false ) {
          logger.warn( "Parameter validation error: Value cannot be matched due to invalid value type '"
              + parameterDefinitionEntry.getName() + "' with value '" + computedValue + "'" );
          result.addError( parameterName, new ValidationMessage( Messages.getInstance( locale ).getString(
              "DefaultReportParameterValidator.ParameterIsInvalidType" ) ) );
          trustedParameterContext.getTrustedValues().put( parameterName, null );
          return;
        }
      }

      if ( logger.isDebugEnabled() ) {
        logger.debug( "On Validate Single Parameter: = " + computedValue );
        logger.debug( "On Validate Single Parameter: ------------------------------" );
      }
      trustedParameterContext.getTrustedValues().put( parameterName, computedValue );
      return;
    }

    final ListParameter listParameter = (ListParameter) parameterDefinitionEntry;
    final Object[] values;
    final Class parameterType;
    if ( listParameter.isAllowMultiSelection() ) {
      if ( computedValue == null ) {
        if ( logger.isDebugEnabled() ) {
          logger.debug( "On Validate Single Parameter: = new Object[0]" );
          logger.debug( "On Validate Single Parameter: ------------------------------" );
        }
        trustedParameterContext.getTrustedValues().put( parameterName, new Object[0] );
        return;
      }

      if ( computedValue instanceof Object[] == false ) {
        result.addError( parameterName, new ValidationMessage( Messages.getInstance( locale ).getString(
            "DefaultReportParameterValidator.ParameterIsNotAnArray" ) ) );
        trustedParameterContext.getTrustedValues().put( parameterName, null );
        if ( logger.isDebugEnabled() ) {
          logger.debug( "On Validate Single Parameter: = " + null );
          logger.debug( "On Validate Single Parameter: ------------------------------" );
        }
        return;
      }

      values = (Object[]) computedValue;
      if ( listParameter.getValueType().isArray() ) {
        parameterType = listParameter.getValueType().getComponentType();
      } else {
        parameterType = listParameter.getValueType();
      }

    } else {
      values = new Object[] { computedValue };
      parameterType = listParameter.getValueType();
    }

    final ValidationMessage message =
        computeValidListValue( listParameter, trustedParameterContext, parameterType, values, locale );
    if ( message != null ) {
      if ( reevaluatePossible
          && "true".equals( listParameter.getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
              ParameterAttributeNames.Core.RE_EVALUATE_ON_FAILED_VALUES, trustedParameterContext ) ) ) {
        validateSingleParameter( result, trustedParameterContext, listParameter, null );
      } else {
        result.addError( parameterName, message );
        if ( logger.isDebugEnabled() ) {
          logger.debug( "On Validate Single Parameter: = null" );
          logger.debug( "On Validate Single Parameter: ------------------------------" );
        }
        trustedParameterContext.getTrustedValues().put( parameterName, null );
      }
    } else {
      if ( logger.isDebugEnabled() ) {
        logger.debug( "On Validate Single Parameter: = " + computedValue );
        logger.debug( "On Validate Single Parameter: ------------------------------" );
      }
      trustedParameterContext.getTrustedValues().put( parameterName, computedValue );
    }
  }

  private ValidationMessage computeValidListValue( final ListParameter listParameter,
      final ParameterContext parameterContext, final Class parameterType, final Object[] values, final Locale locale )
    throws ReportDataFactoryException {
    for ( int i = 0; i < values.length; i++ ) {
      Object value = values[i];
      if ( value != null ) {
        if ( "".equals( value ) ) {
          value = null;
        } else if ( parameterType.isInstance( value ) == false ) {
          logger.warn( "Parameter validation error: Value cannot be matched due to invalid value type '"
              + listParameter.getName() + "' with value '" + value + "'" );
          return new ValidationMessage( Messages.getInstance( locale ).getString(
              "DefaultReportParameterValidator.ParameterIsInvalidType" ) );
        }
      }

      if ( listParameter.isStrictValueCheck() == false ) {
        continue;
      }

      try {
        final ParameterValues parameterValues = listParameter.getValues( parameterContext );
        final boolean found = isValueValid( parameterValues, value );
        if ( found == false ) {
          logger.warn( "Parameter validation error: No such value in the result for '" + listParameter.getName()
              + "' with value '" + value + "'" );
          return new ValidationMessage( Messages.getInstance( locale ).getString(
              "DefaultReportParameterValidator.ParameterIsInvalidValue" ) );
        }
      } catch ( ReportDataFactoryException e ) {
        throw e;
      } catch ( Throwable e ) {
        logger.warn( "Unexpected Parameter validation error", e );
        // overly broad catch, I know, but some creepy code throws ClassNotDefErrors and such around ..
        return new ValidationMessage( Messages.getInstance( locale ).getString( "DefaultReportParameterValidator.GlobalError" ) );
      }
    }
    return null;
  }

  @VisibleForTesting
  boolean isValueMissingForMandatoryParameterCheck( final ParameterDefinitionEntry entry, final Object computedValue ) {
    if ( entry.isMandatory() == false ) {
      return false;
    }
    if ( computedValue == null ) {
      return true;
    }

    if ( entry instanceof ListParameter ) {
      final ListParameter listParameter = (ListParameter) entry;
      if ( listParameter.isAllowMultiSelection() ) {
        if ( computedValue instanceof Object[] == false ) {
          return false;
        } else if ( Array.getLength( computedValue ) == 0 ) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean isValueValid( final ParameterValues parameterValues, final Object o ) {
    if ( parameterValues == null ) {
      throw new NullPointerException();
    }

    for ( int row = 0; row < parameterValues.getRowCount(); row++ ) {
      final Object keyFromData = parameterValues.getKeyValue( row );
      if ( o instanceof Number && keyFromData instanceof Number ) {
        final BigDecimal n1 = new BigDecimal( String.valueOf( o ) );
        final BigDecimal n2 = new BigDecimal( String.valueOf( keyFromData ) );
        if ( n1.compareTo( n2 ) == 0 ) {
          return true;
        }
        continue;
      }
      if ( o instanceof Date && keyFromData instanceof Date ) {
        final Date d1 = (Date) o;
        final Date d2 = (Date) keyFromData;
        if ( d1.getTime() == d2.getTime() ) {
          return true;
        }
        continue;
      }
      if ( "".equals( keyFromData ) ) {
        if ( o == null ) {
          return true;
        }
        continue;
      }
      if ( ObjectUtilities.equal( keyFromData, o ) ) {
        return true;
      }
    }
    return false;
  }
}
