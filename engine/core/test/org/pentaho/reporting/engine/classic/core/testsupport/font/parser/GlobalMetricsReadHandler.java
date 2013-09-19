package org.pentaho.reporting.engine.classic.core.testsupport.font.parser;

import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class GlobalMetricsReadHandler extends AbstractXmlReadHandler
{
  private long ascent;
  private long descent;
  private long leading;
  private long xheight;
  private long overlinePosition;
  private long underlinePosition;
  private long strikethroughPosition;
  private long maxAscent;
  private long maxDescent;
  private long maxHeight;
  private long maxCharAdvance;
  private long italicAngle;
  private boolean uniformFontMetrics;

  public GlobalMetricsReadHandler()
  {
  }

  protected void startParsing(final Attributes attrs) throws SAXException
  {
    ascent = parseLongOrDie("ascent", attrs);
    descent = parseLongOrDie("descent", attrs);
    leading = parseLongOrDie("leading", attrs);
    xheight = parseLongOrDie("x-height", attrs);
    overlinePosition = parseLongOrDie("overline-position", attrs);
    underlinePosition = parseLongOrDie("underline-position", attrs);
    strikethroughPosition = parseLongOrDie("strike-through-position", attrs);
    maxAscent = parseLongOrDie("max-ascent", attrs);
    maxDescent = parseLongOrDie("max-descent", attrs);
    maxHeight = parseLongOrDie("max-height", attrs);
    maxCharAdvance= parseLongOrDie("max-char-advance", attrs);
    italicAngle = parseLongOrDie("italic-angle", attrs);
    uniformFontMetrics = "true".equals(attrs.getValue(getUri(), "uniform"));
  }

  private long parseLongOrDie(final String attrName,
                              final Attributes attrs) throws ParseException
  {
    final String value = attrs.getValue(getUri(), attrName);
    if (StringUtils.isEmpty(value))
    {
      throw new ParseException("Attribute '" + attrName + "' is missing.", getLocator());
    }
    try
    {
      return Long.parseLong(value);
    }
    catch (Exception e)
    {
      throw new ParseException("Attribute '" + attrName + "' with value '" + value + "'is invalid.", getLocator());
    }
  }

  public long getAscent()
  {
    return ascent;
  }

  public long getDescent()
  {
    return descent;
  }

  public long getItalicAngle()
  {
    return italicAngle;
  }

  public long getLeading()
  {
    return leading;
  }

  public long getMaxAscent()
  {
    return maxAscent;
  }

  public long getMaxCharAdvance()
  {
    return maxCharAdvance;
  }

  public long getMaxDescent()
  {
    return maxDescent;
  }

  public long getMaxHeight()
  {
    return maxHeight;
  }

  public long getOverlinePosition()
  {
    return overlinePosition;
  }

  public long getStrikethroughPosition()
  {
    return strikethroughPosition;
  }

  public long getUnderlinePosition()
  {
    return underlinePosition;
  }

  public boolean isUniformFontMetrics()
  {
    return uniformFontMetrics;
  }

  public long getXheight()
  {
    return xheight;
  }

  public Object getObject() throws SAXException
  {
    return null;
  }
}
