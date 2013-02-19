package org.pentaho.reporting.library.parameter.server;

import java.io.IOException;
import java.lang.reflect.Array;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.library.parameter.ListParameter;
import org.pentaho.reporting.library.parameter.Messages;
import org.pentaho.reporting.library.parameter.Parameter;
import org.pentaho.reporting.library.parameter.ParameterAttributeNames;
import org.pentaho.reporting.library.parameter.ParameterContext;
import org.pentaho.reporting.library.parameter.ParameterData;
import org.pentaho.reporting.library.parameter.ParameterDefinition;
import org.pentaho.reporting.library.parameter.ParameterException;
import org.pentaho.reporting.library.parameter.ParameterValidationException;
import org.pentaho.reporting.library.parameter.ParameterValidationResult;
import org.pentaho.reporting.library.parameter.ParameterValidator;
import org.pentaho.reporting.library.parameter.defaults.DefaultParameterData;
import org.pentaho.reporting.library.parameter.validation.DefaultParameterValidationResult;
import org.pentaho.reporting.library.parameter.validation.DefaultValidationMessage;
import org.pentaho.reporting.library.parameter.values.ConverterRegistry;
import org.pentaho.reporting.library.parameter.values.ValueConversionException;
import org.pentaho.reporting.library.parameter.values.ValueConverter;

public class DefaultHttpParameterParser implements HttpParameterParser
{
  private static final Log logger = LogFactory.getLog(DefaultHttpParameterParser.class);
  private HashMap<String, Object> values;

  public DefaultHttpParameterParser()
  {
    values = new HashMap<String, Object>();
  }

  public void setUntrustedServletValues(final Map<String, String[]> parameters)
  {
    values.clear();
    values.putAll(parameters);
  }

  public void setUntrustedRawValues(final Map<String, Object> parameters)
  {
    values.clear();
    values.putAll(parameters);
  }


  /**
   * Apply inputs (if any) to corresponding report parameters, care is taken when
   * checking parameter types to perform any necessary casting and conversion.
   *
   * @param report           The report to retrieve parameter definitions and values from.
   * @param context          a ParameterContext for which the parameters will be under
   * @param validationResult the validation result that will hold the warnings. If null, a new one will be created.
   * @return the validation result containing any parameter validation errors.
   * @throws java.io.IOException if the report of this component could not be parsed.
   * @throws org.pentaho.reporting.libraries.resourceloader.ResourceException
   *                             if the report of this component could not be parsed.
   */
  public ParameterValidationResult applyInputsToReportParameters(final ParameterDefinition report,
                                                                 final ParameterContext context,
                                                                 final ParameterData parameterValues,
                                                                 final Map<String, Object> inputs,
                                                                 ParameterValidationResult validationResult)
      throws IOException, ResourceException
  {
    if (validationResult == null)
    {
      validationResult = new DefaultParameterValidationResult();
    }
    // apply inputs to report
    final Parameter[] params = report.getParameterDefinitions();
    for (final Parameter param : params)
    {
      final String paramName = param.getName();
      try
      {
        final Object rawValue = inputs.get(paramName);
        final Object computedParameter = computeParameterValue(context, param, rawValue);
        parameterValues.put(param.getName(), computedParameter);

        if (logger.isInfoEnabled())
        {
          logger.info(Messages.getInstance().formatMessage("ReportPlugin.infoParameterValues",  //$NON-NLS-1$
              new Object[]{paramName, String.valueOf(rawValue), String.valueOf(computedParameter)}));
        }
      }
      catch (Exception e)
      {
        if (logger.isWarnEnabled())
        {
          logger.warn(Messages.getInstance().getString("ReportPlugin.logErrorParametrization"), e); //$NON-NLS-1$
        }
        validationResult.addError(paramName, new DefaultValidationMessage(e.getMessage()));
      }
    }
    return validationResult;
  }

  public Object computeParameterValue(final ParameterContext parameterContext,
                                      final Parameter parameterDefinition,
                                      final Object value) throws ParameterException
  {
    if (value == null)
    {
      // there are still buggy report definitions out there ...
      return null;
    }

    final Class valueType = parameterDefinition.getValueType();
    final boolean allowMultiSelect = isAllowMultiSelect(parameterDefinition);
    if (allowMultiSelect && Collection.class.isInstance(value))
    {
      final Collection c = (Collection) value;
      final Class componentType;
      if (valueType.isArray())
      {
        componentType = valueType.getComponentType();
      }
      else
      {
        componentType = valueType;
      }

      final int length = c.size();
      final Object[] sourceArray = c.toArray();
      final Object array = Array.newInstance(componentType, length);
      for (int i = 0; i < length; i++)
      {
        Array.set(array, i, convert(parameterContext, parameterDefinition, componentType, sourceArray[i]));
      }
      return array;
    }
    else if (value.getClass().isArray())
    {
      final Class componentType;
      if (valueType.isArray())
      {
        componentType = valueType.getComponentType();
      }
      else
      {
        componentType = valueType;
      }

      final int length = Array.getLength(value);
      final Object array = Array.newInstance(componentType, length);
      for (int i = 0; i < length; i++)
      {
        Array.set(array, i, convert(parameterContext, parameterDefinition, componentType, Array.get(value, i)));
      }
      return array;
    }
    else if (allowMultiSelect)
    {
      // if the parameter allows multi selections, wrap this single input in an array
      // and re-call addParameter with it
      final Object[] array = new Object[1];
      array[0] = value;
      return computeParameterValue(parameterContext, parameterDefinition, array);
    }
    else
    {
      return convert(parameterContext, parameterDefinition, parameterDefinition.getValueType(), value);
    }
  }

  protected boolean isAllowMultiSelect(final Parameter parameter)
  {
    if (parameter instanceof ListParameter)
    {
      final ListParameter listParameter = (ListParameter) parameter;
      return listParameter.isAllowMultiSelection();
    }
    return false;
  }


  protected Object convert(final ParameterContext context,
                           final Parameter parameter,
                           final Class targetType, final Object rawValue) throws ParameterException
  {
    if (targetType == null)
    {
      throw new NullPointerException();
    }

    if (rawValue == null)
    {
      return null;
    }
    if (targetType.isInstance(rawValue))
    {
      return rawValue;
    }

    final String valueAsString = String.valueOf(rawValue);
    if (StringUtils.isEmpty(valueAsString))
    {
      // none of the converters accept empty strings as valid input. So we can return null instead.
      return null;
    }

    if (targetType.equals(Timestamp.class))
    {
      try
      {
        final Date date = parseDate(parameter, context, valueAsString);
        return new Timestamp(date.getTime());
      }
      catch (ParseException pe)
      {
        // ignore, we try to parse it as real date now ..
      }
    }
    else if (targetType.equals(Time.class))
    {
      try
      {
        final Date date = parseDate(parameter, context, valueAsString);
        return new Time(date.getTime());
      }
      catch (ParseException pe)
      {
        // ignore, we try to parse it as real date now ..
      }
    }
    else if (targetType.equals(java.sql.Date.class))
    {
      try
      {
        final Date date = parseDate(parameter, context, valueAsString);
        return new java.sql.Date(date.getTime());
      }
      catch (ParseException pe)
      {
        // ignore, we try to parse it as real date now ..
      }
    }
    else if (targetType.equals(Date.class))
    {
      try
      {
        final Date date = parseDate(parameter, context, valueAsString);
        return new Date(date.getTime());
      }
      catch (ParseException pe)
      {
        // ignore, we try to parse it as real date now ..
      }
    }

    final String dataFormat = parameter.getParameterAttribute(ParameterAttributeNames.Core.NAMESPACE,
        ParameterAttributeNames.Core.DATA_FORMAT, context);
    if (dataFormat != null)
    {
      try
      {
        if (Number.class.isAssignableFrom(targetType))
        {
          final Locale locale = context.getFormulaContext().getLocalizationContext().getLocale();
          final DecimalFormat format = new DecimalFormat(dataFormat, new DecimalFormatSymbols(locale));
          format.setParseBigDecimal(true);
          final Number number = format.parse(valueAsString);
          final String asText = ConverterRegistry.toAttributeValue(number);
          return ConverterRegistry.toPropertyValue(asText, targetType);
        }
        else if (Date.class.isAssignableFrom(targetType))
        {
          final Locale locale = context.getFormulaContext().getLocalizationContext().getLocale();
          final SimpleDateFormat format = new SimpleDateFormat(dataFormat, new DateFormatSymbols(locale));
          format.setLenient(false);
          final Date number = format.parse(valueAsString);
          final String asText = ConverterRegistry.toAttributeValue(number);
          return ConverterRegistry.toPropertyValue(asText, targetType);
        }
      }
      catch (Exception e)
      {
        // again, ignore it .
      }
    }

    final ValueConverter valueConverter = ConverterRegistry.getInstance().getValueConverter(targetType);
    if (valueConverter != null)
    {
      try
      {
        return valueConverter.toPropertyValue(valueAsString);
      }
      catch (ValueConversionException e)
      {
        throw new ParameterException(Messages.getInstance().getString
            ("ReportPlugin.unableToConvertParameter", parameter.getName(), valueAsString)); //$NON-NLS-1$
      }
    }
    return rawValue;
  }

  protected Date parseDate(final Parameter parameterEntry,
                           final ParameterContext context,
                           final String value) throws ParseException
  {
    try
    {
      return parseDateStrict(parameterEntry, context, value);
    }
    catch (ParseException pe)
    {
      //
    }

    try
    {
      // parse the legacy format that we used in 3.5.0-GA.
      final Long dateAsLong = Long.parseLong(value);
      return new Date(dateAsLong);
    }
    catch (NumberFormatException nfe)
    {
      // ignored
    }

    try
    {
      final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd"); // NON-NLS
      return simpleDateFormat.parse(value);
    }
    catch (ParseException pe)
    {
      //
    }
    throw new ParseException("Unable to parse Date", 0);
  }

  protected Date parseDateStrict(final Parameter parameterEntry,
                                 final ParameterContext context,
                                 final String value) throws ParseException
  {
    final String timezoneSpec = parameterEntry.getParameterAttribute
        (ParameterAttributeNames.Core.NAMESPACE, ParameterAttributeNames.Core.TIMEZONE, context);
    if (timezoneSpec == null ||
        "server".equals(timezoneSpec)) // NON-NLS
    {
      final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"); // NON-NLS
      return simpleDateFormat.parse(value);
    }
    else if ("utc".equals(timezoneSpec)) // NON-NLS
    {
      final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"); // NON-NLS
      simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // NON-NLS
      return simpleDateFormat.parse(value);
    }
    else if ("client".equals(timezoneSpec)) // NON-NLS
    {
      try
      {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"); // NON-NLS
        return simpleDateFormat.parse(value);
      }
      catch (ParseException pe)
      {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"); // NON-NLS
        return simpleDateFormat.parse(value);
      }
    }
    else
    {
      final TimeZone timeZone = TimeZone.getTimeZone(timezoneSpec);
      // this never returns null, but if the timezone is not understood, we end up with GMT/UTC.
      final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"); // NON-NLS
      simpleDateFormat.setTimeZone(timeZone);
      return simpleDateFormat.parse(value);
    }
  }

  public ParameterValidationResult validate(final ParameterValidationResult result,
                                            final ParameterDefinition parameterDefinition,
                                            final ParameterContext parameterContext)
      throws ParameterValidationException, ParameterException
  {
    final ParameterValidator validator = parameterDefinition.getValidator();

    final DefaultParameterData parameterData = new DefaultParameterData();
    for (final Parameter param: parameterDefinition.getParameterDefinitions())
    {
      final String paramName = param.getName();
      final Object rawValue = values.get(paramName);
      final Object computedParameter = computeParameterValue(parameterContext, param, rawValue);
      parameterData.put(param.getName(), computedParameter);
    }
    final ParameterContextWrapper wrapper = new ParameterContextWrapper(parameterContext, parameterData);
    return validator.validate(result, parameterDefinition, wrapper);
  }
}
