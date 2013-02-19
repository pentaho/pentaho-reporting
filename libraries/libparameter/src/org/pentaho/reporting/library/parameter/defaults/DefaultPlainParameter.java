package org.pentaho.reporting.library.parameter.defaults;

import java.io.IOException;
import java.util.HashSet;

import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;
import org.pentaho.reporting.library.parameter.LibParameterBoot;
import org.pentaho.reporting.library.parameter.values.ConverterRegistry;
import org.pentaho.reporting.library.parameter.values.ValueConversionException;

public class DefaultPlainParameter extends AbstractParameter
{
  public DefaultPlainParameter(final String name, final Class valueType)
  {
    super(name, valueType);
  }

  public void toXml(final XmlWriter writer) throws IOException
  {
    if (StringUtils.isEmpty(getName()))
    {
      throw new IOException("Cannot write a unnamed parameter entry.");
    }

    final AttributeList paramAttrs = new AttributeList();
    paramAttrs.setAttribute(LibParameterBoot.NAMESPACE, "name", getName());// NON-NLS
    paramAttrs.setAttribute(LibParameterBoot.NAMESPACE, "mandatory", String.valueOf(isMandatory()));// NON-NLS
    paramAttrs.setAttribute(LibParameterBoot.NAMESPACE, "type", getValueType().getName());// NON-NLS

    final Object defaultValue = getDefaultValue();
    if (defaultValue != null)
    {
      try
      {
        final String valAsString = ConverterRegistry.toAttributeValue(defaultValue);
        if (StringUtils.isEmpty(valAsString) == false)
        {
          paramAttrs.setAttribute(LibParameterBoot.NAMESPACE, "default-value", valAsString);// NON-NLS
        }
      }
      catch (ValueConversionException e)
      {
        throw new IOException("Unable to convert parameter " +
            "default-value to string for parameter '" + getName() + '\'');
      }
    }

    final String[] namespaces = getParameterAttributeNamespaces();
    if (namespaces.length == 0)
    {
      writer.writeTag(LibParameterBoot.NAMESPACE, "plain-parameter", paramAttrs, XmlWriterSupport.CLOSE);// NON-NLS
    }
    else
    {
      writer.writeTag(LibParameterBoot.NAMESPACE, "plain-parameter", paramAttrs, XmlWriterSupport.OPEN);// NON-NLS
      for (int j = 0; j < namespaces.length; j++)
      {
        final String namespace = namespaces[j];
        final String[] names = getParameterAttributeNames(namespace);
        for (int k = 0; k < names.length; k++)
        {
          final String name = names[k];
          final String value = getParameterAttribute(namespace, name);
          if (StringUtils.isEmpty(value))
          {
            continue;
          }
          final AttributeList attrsAttr = new AttributeList();
          attrsAttr.setAttribute(LibParameterBoot.NAMESPACE, "namespace", namespace);// NON-NLS
          attrsAttr.setAttribute(LibParameterBoot.NAMESPACE, "name", name);// NON-NLS
          writer.writeTag(LibParameterBoot.NAMESPACE, "attribute", attrsAttr, XmlWriterSupport.OPEN);// NON-NLS
          writer.writeTextNormalized(value, false);
          writer.writeCloseTag();
        }
      }
      writer.writeCloseTag();
    }
  }


}
