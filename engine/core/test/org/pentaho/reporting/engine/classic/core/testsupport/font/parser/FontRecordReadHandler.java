package org.pentaho.reporting.engine.classic.core.testsupport.font.parser;

import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class FontRecordReadHandler extends AbstractXmlReadHandler
{
  private boolean bold;
  private boolean italics;
  private String source;
  
  public FontRecordReadHandler()
  {
  }

  protected void startParsing(final Attributes attrs) throws SAXException
  {
    source = attrs.getValue(getUri(), "source");
    bold = "true".equals(attrs.getValue(getUri(), "bold"));
    italics = "true".equals(attrs.getValue(getUri(), "italics"));
  }

  public Object getObject() throws SAXException
  {
    return this;
  }

  public boolean isBold()
  {
    return bold;
  }

  public boolean isItalics()
  {
    return italics;
  }

  public String getSource()
  {
    return source;
  }
}
