package org.pentaho.reporting.engine.classic.core.testsupport.font.parser;

import java.util.ArrayList;

import org.pentaho.reporting.engine.classic.core.testsupport.font.LocalFontFamily;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class FontFamilyReadHandler extends AbstractXmlReadHandler
{
  private String name;
  private ArrayList<FontRecordReadHandler> fontRecords;
  
  public FontFamilyReadHandler()
  {
    fontRecords = new ArrayList<FontRecordReadHandler>();
  }

  protected void startParsing(final Attributes attrs) throws SAXException
  {
    name = attrs.getValue(getUri(), "name");
  }

  protected XmlReadHandler getHandlerForChild(final String uri,
                                              final String tagName,
                                              final Attributes atts) throws SAXException
  {
    if (isSameNamespace(uri))
    {
      final FontRecordReadHandler recordReadHandler = new FontRecordReadHandler();
      fontRecords.add(recordReadHandler);
      return recordReadHandler;
    }
    return null;
  }

  public LocalFontFamily getObject() throws SAXException
  {
    final LocalFontFamily fontFamily = new LocalFontFamily(name);
    for (int i = 0; i < fontRecords.size(); i++)
    {
      final FontRecordReadHandler record = fontRecords.get(i);
      fontFamily.setFontRecord(record.isBold(), record.isItalics(), record.getSource());
    }
    return fontFamily;
  }
}
