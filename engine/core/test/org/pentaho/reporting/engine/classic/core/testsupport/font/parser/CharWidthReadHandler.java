package org.pentaho.reporting.engine.classic.core.testsupport.font.parser;

import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class CharWidthReadHandler extends AbstractXmlReadHandler
{
  private int codepoint;
  private int value;

  public CharWidthReadHandler()
  {
  }

  public int getCodepoint()
  {
    return codepoint;
  }

  public int getValue()
  {
    return value;
  }

  protected void startParsing(final Attributes attrs) throws SAXException
  {
    codepoint = parseOrDie("codepoint", attrs);
    value = parseOrDie("value", attrs);
  }

  public Object getObject() throws SAXException
  {
    return null;
  }

  private int parseOrDie(final String attrName,
                         final Attributes attrs) throws ParseException
  {
    final String value = attrs.getValue(getUri(), attrName);
    if (StringUtils.isEmpty(value))
    {
      throw new ParseException("Attribute '" + attrName + "' is missing.", getLocator());
    }
    try
    {
      return Integer.parseInt(value);
    }
    catch (Exception e)
    {
      throw new ParseException("Attribute '" + attrName + "' with value '" + value + "'is invalid.", getLocator());
    }
  }
}
