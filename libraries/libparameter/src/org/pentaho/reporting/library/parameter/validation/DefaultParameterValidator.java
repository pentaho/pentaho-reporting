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
 * Copyright (c) 2001 - 2009 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.library.parameter.validation;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.boot.ObjectFactory;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LocalizationContext;
import org.pentaho.reporting.libraries.formula.function.FunctionRegistry;
import org.pentaho.reporting.libraries.formula.operators.OperatorFactory;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.TypeRegistry;
import org.pentaho.reporting.libraries.formula.typing.coretypes.AnyType;
import org.pentaho.reporting.library.parameter.ListParameter;
import org.pentaho.reporting.library.parameter.Messages;
import org.pentaho.reporting.library.parameter.Parameter;
import org.pentaho.reporting.library.parameter.ParameterContext;
import org.pentaho.reporting.library.parameter.ParameterData;
import org.pentaho.reporting.library.parameter.ParameterDataTable;
import org.pentaho.reporting.library.parameter.ParameterDefinition;
import org.pentaho.reporting.library.parameter.ParameterException;
import org.pentaho.reporting.library.parameter.ParameterQuery;
import org.pentaho.reporting.library.parameter.ParameterValidationException;
import org.pentaho.reporting.library.parameter.ParameterValidationResult;
import org.pentaho.reporting.library.parameter.ParameterValidator;
import org.pentaho.reporting.library.parameter.ValidationMessage;

public class DefaultParameterValidator implements ParameterValidator
{
  private static class TrustedFormulaContext implements FormulaContext
  {
    private FormulaContext backend;
    private ParameterData parameterData;

    private TrustedFormulaContext(final FormulaContext backend,
                                  final ParameterData parameterData)
    {
      this.backend = backend;
      this.parameterData = parameterData;
    }

    public boolean isReferenceDirty(final Object name) throws EvaluationException
    {
      return backend.isReferenceDirty(name);
    }

    public Object resolveReference(final Object name) throws EvaluationException
    {
      if (name instanceof String)
      {
        final String text = (String) name;
        if (this.parameterData.isAvailable(text))
        {
          return this.parameterData.get(text);
        }
      }
      return backend.resolveReference(name);
    }

    public Type resolveReferenceType(final Object name) throws EvaluationException
    {
      if (name instanceof String)
      {
        final String text = (String) name;
        if (this.parameterData.isAvailable(text))
        {
          return AnyType.TYPE;
        }
      }
      return backend.resolveReferenceType(name);
    }

    public LocalizationContext getLocalizationContext()
    {
      return backend.getLocalizationContext();
    }

    public Configuration getConfiguration()
    {
      return backend.getConfiguration();
    }

    public FunctionRegistry getFunctionRegistry()
    {
      return backend.getFunctionRegistry();
    }

    public TypeRegistry getTypeRegistry()
    {
      return backend.getTypeRegistry();
    }

    public OperatorFactory getOperatorFactory()
    {
      return backend.getOperatorFactory();
    }

    public Date getCurrentDate()
    {
      return backend.getCurrentDate();
    }
  }

  private static class TrustedParameterContext implements ParameterContext
  {
    private ParameterContext context;
    private ParameterData implicitTrustedValues;
    private DefaultParameterData explicitTrustedValues;
    private ParameterData trustedValues;

    private TrustedParameterContext(final ParameterContext context,
                                    final ParameterData implicitTrustedValues)
    {
      this.explicitTrustedValues = new DefaultParameterData();
      this.context = context;
      this.implicitTrustedValues = implicitTrustedValues;
      this.trustedValues = new DefaultParameterData();
    }

    public ParameterData getParameterData()
    {
      return new CompoundParameterData(implicitTrustedValues, explicitTrustedValues, trustedValues);
    }

    public FormulaContext getFormulaContext()
    {
      return new TrustedFormulaContext(context.getFormulaContext(), getParameterData());
    }

    public DefaultParameterData getTrustedValues()
    {
      return explicitTrustedValues;
    }

    public ParameterQuery getDataFactory()
    {
      return context.getDataFactory();
    }

    public Configuration getConfiguration()
    {
      return context.getConfiguration();
    }

    public ObjectFactory getObjectFactory()
    {
      return context.getObjectFactory();
    }

    public void close()
    {
      // not needed..
    }
  }

  private static final Log logger = LogFactory.getLog(DefaultParameterValidator.class);
  private FormulaParameterEvaluator formulaParameterEvaluator;

  public DefaultParameterValidator()
  {
  }

  protected ParameterData getImplicitTrustedValues()
  {
    return new DefaultParameterData();
  }

  public ParameterValidationResult validate(ParameterValidationResult result,
                                            final ParameterDefinition parameterDefinition,
                                            final ParameterContext parameterContext)
      throws ParameterValidationException, ParameterException
  {
    if (parameterContext == null)
    {
      throw new NullPointerException();
    }
    if (parameterDefinition == null)
    {
      throw new NullPointerException();
    }

    if (result == null)
    {
      result = new DefaultParameterValidationResult();
    }

    final TrustedParameterContext trustedParameterContext =
        new TrustedParameterContext(parameterContext, getImplicitTrustedValues());
    final Parameter[] parameterDefinitionEntries = parameterDefinition.getParameterDefinitions();

    for (int i = 0; i < parameterDefinitionEntries.length; i++)
    {
      final Parameter parameterDefinitionEntry = parameterDefinitionEntries[i];
      final String parameterName = parameterDefinitionEntry.getName();
      final Object untrustedValue = parameterContext.getParameterData().get(parameterName);

      validateSingleParameter(result, trustedParameterContext, parameterDefinitionEntry, untrustedValue);
    }
    result.updateParameterValues(trustedParameterContext.getTrustedValues());
    return result;
  }

  private void validateSingleParameter(final ParameterValidationResult result,
                                       final TrustedParameterContext trustedParameterContext,
                                       final Parameter parameterDefinitionEntry,
                                       Object untrustedValue) throws ParameterException
  {
    final boolean reevaluatePossible = untrustedValue != null;

    Object defaultValue = null;
    if (untrustedValue == null)
    {
      // compute the default value
      defaultValue = parameterDefinitionEntry.getDefaultValue(trustedParameterContext);
      untrustedValue = defaultValue;
    }

    if (logger.isDebugEnabled())
    {
      logger.debug("On Validate Single Parameter: " + parameterDefinitionEntry.getName());
      logger.debug("On Validate Single Parameter: " + trustedParameterContext.getParameterData());
      logger.debug("On Validate Single Parameter: " + untrustedValue);
      logger.debug("On Validate Single Parameter: ------------------------------");
    }
    final String parameterName = parameterDefinitionEntry.getName();
    final ParameterData tempValue = new DefaultParameterData(trustedParameterContext.getTrustedValues());
    tempValue.put(parameterName, untrustedValue);

    final TrustedParameterContext tempContext = new TrustedParameterContext(trustedParameterContext, tempValue);
    final Object computedValue = formulaParameterEvaluator.computePostProcessingValue
        (result, tempContext, parameterDefinitionEntry, untrustedValue, defaultValue);

    if (isValueMissingForMandatoryParameterCheck(parameterDefinitionEntry, computedValue))
    {
      // as the post processing expression failed or returned <null>, the computed value
      // must be <null> or an error. We report an error (which stops the report processing)
      // and set the default value as current value, so that the other parameters can continue.
      trustedParameterContext.getTrustedValues().put(parameterName, null);
      result.addError(parameterName, new DefaultValidationMessage
          (Messages.getInstance().getString("DefaultParameterValidator.ParameterIsMandatory")));
      return;
    }

    if (parameterDefinitionEntry instanceof ListParameter == false)
    {
      if (computedValue != null)
      {
        final Class parameterType = parameterDefinitionEntry.getValueType();
        if (parameterType.isInstance(computedValue) == false)
        {
          logger.warn("Parameter validation error: Value cannot be matched due to invalid value type '" +
              parameterDefinitionEntry.getName() + "' with value '" + computedValue + "'");
          result.addError(parameterName, new DefaultValidationMessage
              (Messages.getInstance().getString("DefaultParameterValidator.ParameterIsInvalidType")));
          trustedParameterContext.getTrustedValues().put(parameterName, null);
          return;
        }
      }

      if (logger.isDebugEnabled())
      {
        logger.debug("On Validate Single Parameter: = " + computedValue);
        logger.debug("On Validate Single Parameter: ------------------------------");
      }
      trustedParameterContext.getTrustedValues().put(parameterName, computedValue);
      return;
    }

    final ListParameter listParameter = (ListParameter) parameterDefinitionEntry;
    final Object[] values;
    final Class parameterType;
    if (listParameter.isAllowMultiSelection())
    {
      if (computedValue == null)
      {
        if (logger.isDebugEnabled())
        {
          logger.debug("On Validate Single Parameter: = new Object[0]");
          logger.debug("On Validate Single Parameter: ------------------------------");
        }
        trustedParameterContext.getTrustedValues().put(parameterName, new Object[0]);
        return;
      }

      if (computedValue instanceof Object[] == false)
      {
        result.addError(parameterName, new DefaultValidationMessage
            (Messages.getInstance().getString("DefaultParameterValidator.ParameterIsNotAnArray")));
        trustedParameterContext.getTrustedValues().put(parameterName, null);
        if (logger.isDebugEnabled())
        {
          logger.debug("On Validate Single Parameter: = " + null);
          logger.debug("On Validate Single Parameter: ------------------------------");
        }
        return;
      }

      values = (Object[]) computedValue;
      if (listParameter.getValueType().isArray())
      {
        parameterType = listParameter.getValueType().getComponentType();
      }
      else
      {
        parameterType = listParameter.getValueType();
      }

    }
    else
    {
      values = new Object[]{computedValue};
      parameterType = listParameter.getValueType();
    }

    final ValidationMessage message = computeValidListValue
        (listParameter, trustedParameterContext, parameterType, values);
    if (message != null)
    {
      if (reevaluatePossible && listParameter.isAllowResetOnInvalidValue())
      {
        validateSingleParameter(result, trustedParameterContext, listParameter, null);
      }
      else
      {
        result.addError(parameterName, message);
        if (logger.isDebugEnabled())
        {
          logger.debug("On Validate Single Parameter: = null");
          logger.debug("On Validate Single Parameter: ------------------------------");
        }
        trustedParameterContext.getTrustedValues().put(parameterName, null);
      }
    }
    else
    {
      if (logger.isDebugEnabled())
      {
        logger.debug("On Validate Single Parameter: = " + computedValue);
        logger.debug("On Validate Single Parameter: ------------------------------");
      }
      trustedParameterContext.getTrustedValues().put(parameterName, computedValue);
    }
  }

  private ValidationMessage computeValidListValue(final ListParameter listParameter,
                                                  final ParameterContext parameterContext,
                                                  final Class parameterType,
                                                  final Object[] values) throws ParameterException
  {
    for (int i = 0; i < values.length; i++)
    {
      Object value = values[i];
      if (value != null)
      {
        if ("".equals(value))
        {
          value = null;
        }
        else if (parameterType.isInstance(value) == false)
        {
          logger.warn("Parameter validation error: Value cannot be matched due to invalid value type '" +
              listParameter.getName() + "' with value '" + value + "'");
          return new DefaultValidationMessage
              (Messages.getInstance().getString("DefaultParameterValidator.ParameterIsInvalidType"));
        }
      }

      if (listParameter.isStrictValueCheck() == false)
      {
        continue;
      }

      try
      {
        final ParameterDataTable parameterValues = listParameter.getValues(parameterContext);
        final boolean found = isValueValid(listParameter, parameterValues, value);
        if (found == false)
        {
          logger.warn("Parameter validation error: No such value in the result for '" +
              listParameter.getName() + "' with value '" + value + "'");
          return new DefaultValidationMessage
              (Messages.getInstance().getString("DefaultParameterValidator.ParameterIsInvalidValue"));
        }
      }
      catch (ParameterException e)
      {
        throw e;
      }
      catch (Throwable e)
      {
        logger.warn("Unexpected Parameter validation error", e);
        // overly broad catch, I know, but some creepy code throws ClassNotDefErrors and such around ..
        return new DefaultValidationMessage
            (Messages.getInstance().getString("DefaultParameterValidator.GlobalError"));
      }
    }
    return null;
  }

  private boolean isValueMissingForMandatoryParameterCheck(final Parameter entry,
                                                           final Object computedValue)
  {
    if (entry.isMandatory() == false)
    {
      return false;
    }
    if (computedValue == null || "".equals(computedValue))
    {
      return true;
    }

    if (entry instanceof ListParameter)
    {
      final ListParameter listParameter = (ListParameter) entry;
      if (listParameter.isAllowMultiSelection())
      {
        if (computedValue instanceof Object[] == false)
        {
          return false;
        }
        else if (Array.getLength(computedValue) == 0)
        {
          return true;
        }
      }
    }
    return false;
  }

  private boolean isValueValid(final ListParameter parameterDefinition,
                               final ParameterDataTable parameterValues,
                               final Object o)
  {
    if (parameterValues == null)
    {
      throw new NullPointerException();
    }

    final String keyColumn = parameterDefinition.getKeyColumn();

    for (int row = 0; row < parameterValues.getRowCount(); row++)
    {
      final Object keyFromData = parameterValues.getValue(keyColumn, row);
      if (o instanceof Number &&
          keyFromData instanceof Number)
      {
        final BigDecimal n1 = new BigDecimal(String.valueOf(o));
        final BigDecimal n2 = new BigDecimal(String.valueOf(keyFromData));
        if (n1.compareTo(n2) == 0)
        {
          return true;
        }
        continue;
      }
      if (o instanceof Date && keyFromData instanceof Date)
      {
        final Date d1 = (Date) o;
        final Date d2 = (Date) keyFromData;
        if (d1.getTime() == d2.getTime())
        {
          return true;
        }
        continue;
      }
      if ("".equals(keyFromData))
      {
        if (o == null)
        {
          return true;
        }
        continue;
      }
      if (ObjectUtilities.equal(keyFromData, o))
      {
        return true;
      }
    }
    return false;
  }
}
