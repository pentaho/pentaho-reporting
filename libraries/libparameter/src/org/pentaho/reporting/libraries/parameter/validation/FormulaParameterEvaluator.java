package org.pentaho.reporting.libraries.parameter.validation;

import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.formula.ErrorValue;
import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.Formula;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.parser.ParseException;
import org.pentaho.reporting.libraries.parameter.Messages;
import org.pentaho.reporting.libraries.parameter.Parameter;
import org.pentaho.reporting.libraries.parameter.ParameterAttributeNames;
import org.pentaho.reporting.libraries.parameter.ParameterContext;
import org.pentaho.reporting.libraries.parameter.ParameterException;
import org.pentaho.reporting.libraries.parameter.ParameterValidationResult;
import org.pentaho.reporting.libraries.parameter.values.ConverterRegistry;
import org.pentaho.reporting.libraries.parameter.values.ValueConverter;

public class FormulaParameterEvaluator
{
  private static final Log logger = LogFactory.getLog(FormulaParameterEvaluator.class);

  public FormulaParameterEvaluator()
  {
  }

  public Object computeDefaultValue(final ParameterContext context,
                                    final Parameter parameter,
                                    final Object defaultValue) throws ParameterException
  {
    final String formula = parameter.getParameterAttribute
        (ParameterAttributeNames.Core.NAMESPACE, ParameterAttributeNames.Core.DEFAULT_VALUE_FORMULA, context);
    if (StringUtils.isEmpty(formula, false) == false)
    {
      // evaluate
      try
      {
        final Object value = computeValue(context, formula, null, parameter, defaultValue);
        if (value == null)
        {
          return defaultValue;
        }
        return value;
      }
      catch (ParseException e)
      {
        throw new ParameterException("Unable to compute default value for parameter '" + parameter.getName() + '"', e);
      }
      catch (EvaluationException e)
      {
        throw new ParameterException("Unable to compute default value for parameter '" + parameter.getName() + '"', e);
      }
    }
    return defaultValue;
  }

  public Object computePostProcessingValue(final ParameterValidationResult result,
                                           final ParameterContext parameterContext,
                                           final Parameter entry,
                                           final Object untrustedValue,
                                           final Object defaultValue) throws ParameterException
  {
    final String formula = entry.getParameterAttribute
        (ParameterAttributeNames.Core.NAMESPACE, ParameterAttributeNames.Core.POST_PROCESSOR_FORMULA,
            parameterContext);
    if (StringUtils.isEmpty(formula, true))
    {
      return untrustedValue;
    }

    try
    {
      return computeValue(parameterContext, formula, result, entry, defaultValue);
    }
    catch (EvaluationException e)
    {
      if (result != null)
      {
        result.addError(entry.getName(),
            new DefaultValidationMessage(Messages.getInstance().formatMessage
                ("FormulaParameterEvaluator.PostProcessingFormulaFailed", e.getLocalizedMessage())));
      }
    }
    catch (ParseException e)
    {
      if (result != null)
      {
        result.addError(entry.getName(),
            new DefaultValidationMessage(Messages.getInstance().formatMessage
                ("FormulaParameterEvaluator.PostProcessingFormulaFailed", e.getLocalizedMessage())));
      }
    }
    return null;
  }

  protected String getDefaultFormulaNamespace()
  {
    return "report";
  }

  private Formula parseFormula(final ParameterContext parameterContext,
                               String formula) throws ParseException, EvaluationException
  {
    if (formula == null)
    {
      return null;
    }

    final String formulaNamespace;
    final String formulaExpression;
    if (formula.endsWith(";"))
    {
      logger.warn("A formula with a trailing semicolon is not valid. Auto-correcting the formula.");
      formula = formula.substring(0, formula.length() - 1);
    }

    if (formula.length() > 0 && formula.charAt(0) == '=')
    {
      formulaNamespace = getDefaultFormulaNamespace();
      formulaExpression = formula.substring(1);
    }
    else
    {
      final int separator = formula.indexOf(':');
      if (separator <= 0 || ((separator + 1) == formula.length()))
      {
        // error: invalid formula.
        formulaNamespace = null;
        formulaExpression = null;
      }
      else
      {
        formulaNamespace = formula.substring(0, separator);
        formulaExpression = formula.substring(separator + 1);
      }
    }
    if (formulaExpression == null)
    {
      return null;
    }
    final Formula compileFormula = new Formula(formulaExpression);
    compileFormula.initialize(createFormulaContext(parameterContext, formulaNamespace));
    return compileFormula;
  }

  protected FormulaContext createFormulaContext(final ParameterContext parameterContext,
                                                final String formulaNamespace)
  {
    return parameterContext.getFormulaContext();
  }

  protected Object computeValue(final ParameterContext parameterContext,
                                final String formula,
                                final ParameterValidationResult result,
                                final Parameter entry,
                                final Object defaultValue)
      throws EvaluationException, ParseException
  {
    final Formula compiledFormula = parseFormula(parameterContext, formula);
    final Object value = compiledFormula.evaluate();
    if (value == null)
    {
      return defaultValue;
    }
    else if (entry.getValueType().isInstance(value))
    {
      return value;
    }
    else if (value instanceof ErrorValue)
    {
      final ErrorValue errorValue = (ErrorValue) value;
      if (result != null)
      {
        result.addError(entry.getName(),
            new DefaultValidationMessage(Messages.getInstance().formatMessage
                ("FormulaParameterEvaluator.PostProcessingFormulaFailed",
                    errorValue.getErrorMessage(Locale.getDefault()))));
      }
      // if the value is a hard error, we return <null> instead of the default value.
      // This way, a mandatory parameter will not continue in case of eval-errors.
      return null;
    }
    else
    {
      return convertValue(entry, value, defaultValue, result);
    }
  }

  protected Object convertValue(final Parameter entry,
                                final Object value,
                                final Object defaultValue,
                                final ParameterValidationResult result)
  {
    final ValueConverter valueConverter = ConverterRegistry.getInstance().getValueConverter(entry.getValueType());
    if (valueConverter != null)
    {
      // try to convert it; if this conversion fails we resort to String.valueOf,
      // but it will take care of converting dates and number subtypes correctly  ..
      String textValue;
      try
      {
        textValue = ConverterRegistry.toAttributeValue(value);
      }
      catch (Exception be)
      {
        textValue = String.valueOf(value);
      }

      try
      {
        return ConverterRegistry.toPropertyValue(textValue, entry.getValueType());
      }
      catch (Exception e)
      {
        if (logger.isDebugEnabled())
        {
          logger.debug("Unable to convert computed default value for parameter '" + entry.getName() + "'", e);
        }
        if (result != null)
        {
          result.addError(entry.getName(),
              new DefaultValidationMessage(Messages.getInstance().getString
                  ("FormulaParameterEvaluator.ErrorConvertingValue")));
          result.addError(entry.getName(),
              new DefaultValidationMessage("The post-processing result cannot be converted into the target-type."));
        }
        return null;
      }
    }
    return defaultValue;

  }
}
