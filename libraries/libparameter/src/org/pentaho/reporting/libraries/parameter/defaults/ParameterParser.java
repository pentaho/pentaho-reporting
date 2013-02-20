package org.pentaho.reporting.libraries.parameter.defaults;

import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.writer.CharacterEntityParser;
import org.pentaho.reporting.libraries.parameter.ListParameter;
import org.pentaho.reporting.libraries.parameter.Parameter;
import org.pentaho.reporting.libraries.parameter.ParameterDefinition;
import org.pentaho.reporting.libraries.parameter.values.ConverterRegistry;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

@SuppressWarnings("HardCodedStringLiteral")
public class ParameterParser
{
  /**
   * An instance of the XML character entity parser.
   */
  private static final CharacterEntityParser XML_ENTITIES =
      CharacterEntityParser.createXMLEntityParser();

  public ParameterParser()
  {
  }

  public ParameterDefinition parseDefinition(final Element node) throws ParseException
  {
    final DefaultParameterDefinition retval = new DefaultParameterDefinition();
    final NodeList childNodes = node.getChildNodes();
    for (int i = 0; i < childNodes.getLength(); i += 1)
    {
      final Node childNode = childNodes.item(i);
      if (childNode.getNodeType() != Node.ELEMENT_NODE)
      {
        continue;
      }
      final Element childElement = (Element) childNode;
      final String tagName = childElement.getTagName();
      if ("plain-parameter".equals(tagName))
      {
        retval.addParameter(parsePlainParameter(childElement));
      }
      else if ("list-parameter".equals(tagName))
      {
        retval.addParameter(parseListParameter(childElement));
      }
    }

    return retval;
  }

  public Parameter parsePlainParameter(final Element node) throws ParseException
  {
    final DefaultPlainParameter param = new DefaultPlainParameter("<construct-me>", Object.class);
    fillPlainParameter(param, node);
    return param;
  }

  protected void fillPlainParameter(final DefaultPlainParameter parameter, final Element node) throws ParseException
  {
    fillParameter(parameter, node);
  }

  protected void fillParameter(final AbstractParameter parameter, final Element node) throws ParseException
  {
    try
    {
      final String name = node.getAttributeNS(node.getNamespaceURI(), "name");
      final boolean mandatory = ParserUtil.parseBoolean(node.getAttributeNS(node.getNamespaceURI(), "mandatory"), false);
      final String type = node.getAttributeNS(node.getNamespaceURI(), "type");
      final String defaultValue = node.getAttributeNS(node.getNamespaceURI(), "default-value");

      if (StringUtils.isEmpty(name))
      {
        throw new ParseException("Mandatory attribute 'name' is missing.");
      }
      final Class typeClass = Class.forName(type, false, ObjectUtilities.getClassLoader(getClass()));
      parameter.setValueType(typeClass);
      parameter.setName(name);
      parameter.setMandatory(mandatory);

      if (StringUtils.isEmpty(defaultValue) == false)
      {
        parameter.setDefaultValue(ConverterRegistry.toPropertyValue(defaultValue, typeClass));
      }

      final NodeList attributeNodes = node.getElementsByTagName("attribute");
      for (int i = 0; i < attributeNodes.getLength(); i += 1)
      {
        final Node attrNode = attributeNodes.item(i);
        if (attrNode.getNodeType() != Node.ELEMENT_NODE)
        {
          continue;
        }

        final Element attrElement = (Element) attrNode;
        final String attrNamespace = attrElement.getAttributeNS(attrElement.getNamespaceURI(), "namespace");
        final String attrName = attrElement.getAttributeNS(attrElement.getNamespaceURI(), "name");
        final String attrValue = getText(attrElement);
        parameter.setParameterAttribute(attrNamespace, attrName, attrValue);
      }
    }
    catch (ClassNotFoundException e)
    {
      throw new ParseException("Attribute 'type' contained an invalid data type.", e);
    }
  }


  /**
   * extracts all text-elements of a particular element and returns an single string containing the contents of all
   * textelements and all character entity nodes. If a node is not known to the parser, its string value will be
   * delivered as <code>&entityname;</code>.
   *
   * @param e the element which is direct parent of all to be extracted textnodes.
   * @return the extracted String
   */
  protected static String getText(final Element e)
  {
    final NodeList nl = e.getChildNodes();
    final StringBuilder result = new StringBuilder(100);
    for (int i = 0; i < nl.getLength(); i++)
    {
      final Node n = nl.item(i);
      if (n.getNodeType() == Node.TEXT_NODE ||
          n.getNodeType() == Node.CDATA_SECTION_NODE)
      {
        final Text text = (Text) n;
        result.append(text.getData());
      }
      else if (n.getNodeType() == Node.ENTITY_REFERENCE_NODE)
      {
        result.append(XML_ENTITIES.decodeEntities('&' + n.getNodeName() + ';'));
      }
    }
    return XML_ENTITIES.decodeEntities(result.toString());
  }

  public ListParameter parseListParameter(final Element node) throws ParseException
  {
    final DefaultListParameter param = new DefaultListParameter("<construct-me>", Object.class, "?", "?");
    fillListParameter(param, node);
    return param;
  }

  protected void fillListParameter(final DefaultListParameter param, final Element node) throws ParseException
  {
    fillParameter(param, node);
    final String query = node.getAttributeNS(node.getNamespaceURI(), "query");
    final String keyColumn = node.getAttributeNS(node.getNamespaceURI(), "key-column");
    final String textColumn = node.getAttributeNS(node.getNamespaceURI(), "text-column");
    final boolean strictValues = ParserUtil.parseBoolean(node.getAttributeNS(node.getNamespaceURI(), "strict-values"), false);
    final boolean allowMultiSelection = ParserUtil.parseBoolean(node.getAttributeNS(node.getNamespaceURI(), "allow-multi-selection"), false);
    final boolean allowResetOnInvalidValue = ParserUtil.parseBoolean(node.getAttributeNS(node.getNamespaceURI(), "allow-reset-on-invalid-value"), false);

    if (StringUtils.isEmpty(query))
    {
      throw new ParseException("Mandatory attribute 'query' is missing.");
    }
    if (StringUtils.isEmpty(keyColumn))
    {
      throw new ParseException("Mandatory attribute 'key-column' is missing.");
    }
    if (StringUtils.isEmpty(textColumn))
    {
      throw new ParseException("Mandatory attribute 'text-column' is missing.");
    }

    param.setKeyColumn(keyColumn);
    param.setTextColumn(textColumn);
    param.setStrictValueCheck(strictValues);
    param.setAllowMultiSelection(allowMultiSelection);
    param.setAllowResetOnInvalidValue(allowResetOnInvalidValue);
  }
}
