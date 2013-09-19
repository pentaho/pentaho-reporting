package org.pentaho.reporting.engine.classic.core.testsupport.font.parser;

import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class KerningReadHandler extends AbstractXmlReadHandler
{
  private int codepoint;
  private int prev;
  private int value;

  public KerningReadHandler()
  {
  }

  protected void startParsing(final Attributes attrs) throws SAXException
  {
    codepoint = parseOrDie("codepoint", attrs);
    prev = parseOrDie("prev", attrs);
    value = parseOrDie("value", attrs);
  }

  public int getCodepoint()
  {
    return codepoint;
  }

  public int getPrev()
  {
    return prev;
  }

  public int getValue()
  {
    return value;
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
