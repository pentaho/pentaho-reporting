package org.pentaho.reporting.libraries.parameter.server;

import java.io.OutputStream;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.TimeZone;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.parameter.ListParameter;
import org.pentaho.reporting.libraries.parameter.Messages;
import org.pentaho.reporting.libraries.parameter.Parameter;
import org.pentaho.reporting.libraries.parameter.ParameterAttributeNames;
import org.pentaho.reporting.libraries.parameter.ParameterContext;
import org.pentaho.reporting.libraries.parameter.ParameterData;
import org.pentaho.reporting.libraries.parameter.ParameterDataTable;
import org.pentaho.reporting.libraries.parameter.ParameterDefinition;
import org.pentaho.reporting.libraries.parameter.ParameterException;
import org.pentaho.reporting.libraries.parameter.ParameterValidationResult;
import org.pentaho.reporting.libraries.parameter.ValidationMessage;
import org.pentaho.reporting.libraries.parameter.defaults.DefaultPlainParameter;
import org.pentaho.reporting.libraries.parameter.values.ConverterRegistry;
import org.pentaho.reporting.libraries.parameter.values.ValueConversionException;
import org.pentaho.reporting.libraries.parameter.values.ValueConverter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XmlParameterDocumentWriter
{
  private static Log logger = LogFactory.getLog(XmlParameterDocumentWriter.class);

  private static final String PARAMETERS = "parameters";
  private ParameterDefinition reportParameterDefinition;
  private ParameterContext parameterContext;
  private Document document;

  public XmlParameterDocumentWriter(final ParameterDefinition reportParameterDefinition,
                                    final ParameterContext parameterContext)
  {
    this.reportParameterDefinition = reportParameterDefinition;
    this.parameterContext = parameterContext;
  }

  protected Boolean isAutoSubmit()
  {
    return null;
  }

  protected boolean isAutoSubmitPreselectedInUI()
  {
    return false;
  }

  protected boolean isParameterUIVisible()
  {
    return true;
  }

  protected String getParameterLayout()
  {
    return "horizontal";
  }

  protected Map<String, Parameter> getSystemParameter()
  {
    return Collections.emptyMap();
  }


  protected boolean isGenerateDefaultDates()
  {
    return false;
  }

  protected void createAdditionalParameters()
  {
    // reporting: Add page count if needed

  }

  protected OutputParameter[] getOutputParameter()
  {
    return new OutputParameter[0];
  }

  protected Map<String, Object> computeParameterValues(final Map<String, Parameter> reportParameters,
                                                       final ParameterValidationResult vr)
  {
    final LinkedHashMap<String, Object> inputs = new LinkedHashMap<String, Object>();
    final ParameterData parameterValues = vr.getParameterValues();
    for (final String col : parameterValues.getAvailableColumns())
    {
      inputs.put(col, parameterValues.get(col));
    }

    inputs.put("showParameters", isParameterUIVisible());
    return inputs;
  }

  protected Map<String, Parameter> customizeReportParameters(final Map<String, Parameter> reportParameters,
                                                             final ParameterValidationResult vr)
  {
    // for reporting: Handle override output type
    return reportParameters;
  }

  public void write(final ParameterValidationResult vr,
                    final OutputStream out) throws Exception
  {
    this.document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    try
    {
      final Element parameters = document.createElement(PARAMETERS); //$NON-NLS-1$
      parameters.setAttribute("is-prompt-needed", String.valueOf(vr.isParameterSetValid() == false)); //$NON-NLS-1$ //$NON-NLS-2$
      parameters.setAttribute("ignore-biserver-5538", "true");

      // check if pagination is allowed and turned on

      final Boolean autoSubmitFlag = isAutoSubmit();
      if (Boolean.TRUE.equals(autoSubmitFlag))
      {
        parameters.setAttribute("autoSubmit", "true");
      }
      else if (Boolean.FALSE.equals(autoSubmitFlag))
      {
        parameters.setAttribute("autoSubmit", "false");
      }

      parameters.setAttribute("autoSubmitUI", String.valueOf(isAutoSubmitPreselectedInUI())); // NON-NLS
      parameters.setAttribute("layout", getParameterLayout());

      final Parameter[] parameterDefinitions = reportParameterDefinition.getParameterDefinitions();
      // Collect all parameter, but allow user-parameter to override system parameter.
      // It is the user's problem if the types do not match and weird errors occur, but
      // there are sensible usecases where this should be allowed.
      // System parameter must come last in the list, as this is how it was done in the original
      // version and this is how people expect it to be now.
      final LinkedHashMap<String, Parameter> collectedReportParameters = new LinkedHashMap<String, Parameter>();
      for (final Parameter parameter : parameterDefinitions)
      {
        collectedReportParameters.put(parameter.getName(), parameter);
      }
      for (final Map.Entry<String, Parameter> entry : getSystemParameter().entrySet())
      {
        if (collectedReportParameters.containsKey(entry.getKey()) == false)
        {
          collectedReportParameters.put(entry.getKey(), entry.getValue());
        }
      }

      final Map<String, Parameter> reportParameters = customizeReportParameters(collectedReportParameters, vr);
      final Map<String, Object> inputs = computeParameterValues(reportParameters, vr);

      for (final Parameter parameter : reportParameters.values())
      {
        final Object selections = inputs.get(parameter.getName());
        final ParameterContextWrapper wrapper = new ParameterContextWrapper
            (parameterContext, vr.getParameterValues());
        parameters.appendChild(createParameterElement(parameter, wrapper, selections));
      }

      if (vr.isParameterSetValid() == false)
      {
        parameters.appendChild(createErrorElements(vr));
      }

      final OutputParameter[] outputParameter = getOutputParameter();
      for (int i = 0; i < outputParameter.length; i++)
      {
        final OutputParameter outputParameterName = outputParameter[i];
        //  <output-parameter displayName="Territory" id="[Markets].[Territory]"/>
        final Element element = document.createElement("output-parameter");// NON-NLS
        element.setAttribute("displayName", outputParameterName.getDisplayName());// NON-NLS
        element.setAttribute("id", outputParameterName.getId());// NON-NLS
        parameters.appendChild(element);
      }

      createAdditionalParameters();
      document.appendChild(parameters);

      final DOMSource source = new DOMSource(document);
      final StreamResult result = new StreamResult(out);
      final Transformer transformer = TransformerFactory.newInstance().newTransformer();
      transformer.transform(source, result);
      // close parameter context
    }
    finally
    {
      document = null;
    }
  }

  private Element createErrorElements(final ParameterValidationResult vr)
  {
    final Element errors = document.createElement("errors"); //$NON-NLS-1$
    for (final String property : vr.getErrorParameterNames())
    {
      for (final ValidationMessage message : vr.getErrors(property))
      {
        final Element error = document.createElement("error"); //$NON-NLS-1$
        error.setAttribute("parameter", property);//$NON-NLS-1$
        error.setAttribute("message", message.getMessage());//$NON-NLS-1$
        errors.appendChild(error);
      }
    }
    final ValidationMessage[] globalMessages = vr.getGlobalErrors();
    for (int i = 0; i < globalMessages.length; i++)
    {
      final ValidationMessage globalMessage = globalMessages[i];
      final Element error = document.createElement("global-error"); //$NON-NLS-1$
      error.setAttribute("message", globalMessage.getMessage());//$NON-NLS-1$
      errors.appendChild(error);
    }
    return errors;
  }


  private Element createParameterElement(final Parameter parameter,
                                         final ParameterContext parameterContext,
                                         final Object selections) throws ValueConversionException, ParameterException
  {
    try
    {
      final Element parameterElement = document.createElement("parameter"); //$NON-NLS-1$
      parameterElement.setAttribute("name", parameter.getName()); //$NON-NLS-1$
      final Class valueType = parameter.getValueType();
      parameterElement.setAttribute("type", valueType.getName()); //$NON-NLS-1$
      parameterElement.setAttribute("is-mandatory", String.valueOf(parameter.isMandatory())); //$NON-NLS-1$ //$NON-NLS-2$

      final String[] namespaces = parameter.getParameterAttributeNamespaces();
      for (int i = 0; i < namespaces.length; i++)
      {
        final String namespace = namespaces[i];
        final String[] attributeNames = parameter.getParameterAttributeNames(namespace);
        for (final String attributeName : attributeNames)
        {
          final String attributeValue = parameter.getParameterAttribute
              (namespace, attributeName, parameterContext);
          // expecting: label, parameter-render-type, parameter-layout
          // but others possible as well, so we set them all
          final Element attributeElement = document.createElement("attribute"); // NON-NLS
          attributeElement.setAttribute("namespace", namespace); // NON-NLS
          attributeElement.setAttribute("name", attributeName); // NON-NLS
          attributeElement.setAttribute("value", attributeValue); // NON-NLS

          parameterElement.appendChild(attributeElement);
        }
      }

      final Class elementValueType;
      if (valueType.isArray())
      {
        elementValueType = valueType.getComponentType();
      }
      else
      {
        elementValueType = valueType;
      }

      if (Date.class.isAssignableFrom(elementValueType))
      {
        parameterElement.setAttribute("timzone-hint", computeTimeZoneHint(parameter, parameterContext));//$NON-NLS-1$
      }

      final LinkedHashSet<Object> selectionSet = new LinkedHashSet<Object>();
      if (selections != null)
      {
        if (selections.getClass().isArray())
        {
          final int length = Array.getLength(selections);
          for (int i = 0; i < length; i++)
          {
            final Object value = Array.get(selections, i);
            selectionSet.add(resolveSelectionValue(value));
          }
        }
        else
        {
          selectionSet.add(resolveSelectionValue(selections));
        }
      }
      else
      {
        final String type = parameter.getParameterAttribute
            (ParameterAttributeNames.Core.NAMESPACE, ParameterAttributeNames.Core.TYPE, parameterContext);
        if (ParameterAttributeNames.Core.TYPE_DATEPICKER.equals(type) &&
            Date.class.isAssignableFrom(valueType))
        {
          if (isGenerateDefaultDates())
          {
            selectionSet.add(new Date());
          }
        }
      }

      final LinkedHashSet handledValues = (LinkedHashSet) selectionSet.clone();

      if (parameter instanceof ListParameter)
      {
        final ListParameter listParameter = (ListParameter) parameter;
        parameterElement.setAttribute("is-multi-select", String.valueOf(listParameter.isAllowMultiSelection())); //$NON-NLS-1$ //$NON-NLS-2$
        parameterElement.setAttribute("is-strict", String.valueOf(listParameter.isStrictValueCheck())); //$NON-NLS-1$ //$NON-NLS-2$
        parameterElement.setAttribute("is-list", "true"); //$NON-NLS-1$ //$NON-NLS-2$

        final Element valuesElement = document.createElement("values"); //$NON-NLS-1$
        parameterElement.appendChild(valuesElement);

        final ParameterDataTable possibleValues = listParameter.getValues(parameterContext);
        for (int i = 0; i < possibleValues.getRowCount(); i++)
        {
          final Object key = possibleValues.getValue(listParameter.getKeyColumn(), i);
          final Object value = possibleValues.getValue(listParameter.getTextColumn(), i);

          final Element valueElement = document.createElement("value"); //$NON-NLS-1$
          valuesElement.appendChild(valueElement);

          valueElement.setAttribute("label", String.valueOf(value)); //$NON-NLS-1$ //$NON-NLS-2$
          valueElement.setAttribute("type", elementValueType.getName()); //$NON-NLS-1$

          if (key instanceof Number)
          {
            final BigDecimal bd = new BigDecimal(String.valueOf(key));
            valueElement.setAttribute("selected", String.valueOf(selectionSet.contains(bd)));//$NON-NLS-1$
            handledValues.remove(bd);
          }
          else if (key == null)
          {
            if (selections == null || selectionSet.contains(null))
            {
              valueElement.setAttribute("selected", "true");//$NON-NLS-1$
              handledValues.remove(null);
            }
          }
          else
          {
            valueElement.setAttribute("selected", String.valueOf(selectionSet.contains(key)));//$NON-NLS-1$
            handledValues.remove(key);
          }
          if (key == null)
          {
            valueElement.setAttribute("null", "true"); //$NON-NLS-1$ //$NON-NLS-2$
          }
          else
          {
            valueElement.setAttribute("null", "false"); //$NON-NLS-1$ //$NON-NLS-2$
            valueElement.setAttribute("value", convertParameterValueToString
                (parameter, parameterContext, key, elementValueType)); //$NON-NLS-1$ //$NON-NLS-2$
          }

        }

        // Only add invalid values to the selection list for non-strict parameters
        if (!listParameter.isStrictValueCheck())
        {
          for (final Object key : handledValues)
          {
            final Element valueElement = document.createElement("value"); //$NON-NLS-1$
            valuesElement.appendChild(valueElement);

            valueElement.setAttribute("label", Messages.getInstance().getString("ReportPlugin.autoParameter", String.valueOf(key))); //$NON-NLS-1$ //$NON-NLS-2$
            valueElement.setAttribute("type", elementValueType.getName()); //$NON-NLS-1$

            if (key instanceof Number)
            {
              final BigDecimal bd = new BigDecimal(String.valueOf(key));
              valueElement.setAttribute("selected", String.valueOf(selectionSet.contains(bd)));//$NON-NLS-1$
            }
            else
            {
              valueElement.setAttribute("selected", String.valueOf(selectionSet.contains(key)));//$NON-NLS-1$
            }

            if (key == null)
            {
              valueElement.setAttribute("null", "true"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            else
            {
              valueElement.setAttribute("null", "false"); //$NON-NLS-1$ //$NON-NLS-2$
              valueElement.setAttribute("value",
                  convertParameterValueToString(parameter, parameterContext, key, elementValueType)); //$NON-NLS-1$ //$NON-NLS-2$
            }

          }
        }
      }
      else if (parameter instanceof DefaultPlainParameter)
      {
        // apply defaults, this is the easy case
        parameterElement.setAttribute("is-multi-select", "false"); //$NON-NLS-1$ //$NON-NLS-2$
        parameterElement.setAttribute("is-strict", "false"); //$NON-NLS-1$ //$NON-NLS-2$
        parameterElement.setAttribute("is-list", "false"); //$NON-NLS-1$ //$NON-NLS-2$

        if (selections != null)
        {
          final Element valuesElement = document.createElement("values"); //$NON-NLS-1$
          parameterElement.appendChild(valuesElement);

          final Element valueElement = document.createElement("value"); //$NON-NLS-1$
          valuesElement.appendChild(valueElement);
          valueElement.setAttribute("type", valueType.getName()); //$NON-NLS-1$
          valueElement.setAttribute("selected", "true");//$NON-NLS-1$
          valueElement.setAttribute("null", "false"); //$NON-NLS-1$ //$NON-NLS-2$
          final String value = convertParameterValueToString(parameter, parameterContext, selections, valueType);
          valueElement.setAttribute("value", value); //$NON-NLS-1$ //$NON-NLS-2$
          valueElement.setAttribute("label", value); //$NON-NLS-1$ //$NON-NLS-2$
        }
      }
      return parameterElement;
    }
    catch (ValueConversionException be)
    {
      logger.error(Messages.getInstance().getString
          ("ReportPlugin.errorFailedToGenerateParameter", parameter.getName(), String.valueOf(selections)), be);
      throw be;
    }
  }

  private String computeTimeZoneHint(final Parameter parameter,
                                     final ParameterContext parameterContext)
  {
    // add a timezone hint ..
    final String timezoneSpec = parameter.getParameterAttribute
        (ParameterAttributeNames.Core.NAMESPACE, ParameterAttributeNames.Core.TIMEZONE, parameterContext);
    if ("client".equals(timezoneSpec))//$NON-NLS-1$
    {
      return ("");
    }
    else
    {
      final TimeZone timeZone;
      final StringBuffer value = new StringBuffer();
      if (timezoneSpec == null || "server".equals(timezoneSpec))//$NON-NLS-1$
      {
        timeZone = TimeZone.getDefault();
      }
      else if ("utc".equals(timezoneSpec))//$NON-NLS-1$
      {
        timeZone = TimeZone.getTimeZone("UTC");//$NON-NLS-1$
      }
      else
      {
        timeZone = TimeZone.getTimeZone(timezoneSpec);
      }

      final int rawOffset = timeZone.getRawOffset();
      if (rawOffset < 0)
      {
        value.append("-");
      }
      else
      {
        value.append("+");
      }

      final int seconds = Math.abs(rawOffset / 1000);
      final int minutesRaw = seconds / 60;
      final int hours = minutesRaw / 60;
      final int minutes = minutesRaw % 60;
      if (hours < 10)
      {
        value.append("0");
      }
      value.append(hours);
      if (minutes < 10)
      {
        value.append("0");
      }
      value.append(minutes);
      return value.toString();
    }
  }

  protected String convertParameterValueToString(final Parameter parameter,
                                                 final ParameterContext context,
                                                 final Object value,
                                                 final Class type) throws ValueConversionException
  {
    if (value == null)
    {
      return null;
    }

    final ValueConverter valueConverter = ConverterRegistry.getInstance().getValueConverter(type);
    if (valueConverter == null)
    {
      return String.valueOf(value);
    }

    if (Date.class.isAssignableFrom(type))
    {
      if (value instanceof Date == false)
      {
        throw new ValueConversionException(Messages.getInstance().getString("ReportPlugin.errorNonDateParameterValue"));
      }

      final String timezone = parameter.getParameterAttribute
          (ParameterAttributeNames.Core.NAMESPACE, ParameterAttributeNames.Core.TIMEZONE, context);
      final DateFormat dateFormat;
      if (timezone == null ||
          "server".equals(timezone) ||//$NON-NLS-1$
          "client".equals(timezone))//$NON-NLS-1$
      {
        // nothing needed ..
        // for server: Just print it as it is, including the server timezone.
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");//$NON-NLS-1$
      }
      else
      {
        // for convenience for the clients we send the date in the correct timezone.
        final TimeZone timeZoneObject;
        if ("utc".equals(timezone))//$NON-NLS-1$
        {
          timeZoneObject = TimeZone.getTimeZone("UTC");//$NON-NLS-1$
        }
        else
        {
          timeZoneObject = TimeZone.getTimeZone(timezone);
        }
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");//$NON-NLS-1$
        dateFormat.setTimeZone(timeZoneObject);
      }
      final Date d = (Date) value;
      return dateFormat.format(d);
    }
    if (Number.class.isAssignableFrom(type))
    {
      final ValueConverter numConverter = ConverterRegistry.getInstance().getValueConverter(BigDecimal.class);
      return numConverter.toAttributeValue(new BigDecimal(String.valueOf(value)));
    }
    return valueConverter.toAttributeValue(value);
  }

  private Object resolveSelectionValue(Object value)
  {
    // convert all numerics to BigDecimals for cross-numeric-class matching
    if (value instanceof Number)
    {
      return new BigDecimal(String.valueOf(value.toString()));
    }
    else
    {
      return value;
    }
  }

}
