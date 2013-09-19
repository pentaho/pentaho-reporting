package org.pentaho.reporting.engine.classic.core.imagemap;

import org.pentaho.reporting.libraries.xmlns.common.AttributeMap;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

public abstract class AbstractImageMapEntry implements ImageMapEntry
{
  private AttributeMap attributeMap;

  protected AbstractImageMapEntry()
  {
    attributeMap = new AttributeMap();
  }

  public String getAttribute(final String namespace, final String name)
  {
    return (String) attributeMap.getAttribute(namespace, name);
  }

  public void setAttribute(final String namespace, final String name, final String value)
  {
    attributeMap.setAttribute(namespace, name, value);
  }

  public String[] getNames(final String namespace)
  {
    return attributeMap.getNames(namespace);
  }

  public String[] getNameSpaces()
  {
    return attributeMap.getNameSpaces();
  }

  public Object getFirstAttribute(final String name)
  {
    return attributeMap.getFirstAttribute(name);
  }

  public boolean contains(final float x, final float y)
  {
    return getShape().contains(x, y);
  }

  public String toString()
  {
    final StringBuffer stringBuilder = new StringBuffer();
    stringBuilder.append("<area type=\"");
    stringBuilder.append(getAreaType());
    stringBuilder.append("\" coords=\"");
    final float[] coords = getAreaCoordinates();
    for (int i = 0; i < coords.length; i++)
    {
      final float coord = coords[i];
      if (i > 0)
      {
        stringBuilder.append(",");
        stringBuilder.append(coord);
      }
    }
    stringBuilder.append("\"");
    final String[] namespaces = attributeMap.getNameSpaces();
    for (int i = 0; i < namespaces.length; i++)
    {
      final String namespace = namespaces[i];
      final String[] names = attributeMap.getNames(namespace);
      for (int j = 0; j < names.length; j++)
      {
        final String name = names[j];
        stringBuilder.append(" ");
        stringBuilder.append(XmlWriterSupport.normalize(name, true));
        stringBuilder.append("=");
        stringBuilder.append(XmlWriterSupport.normalize(String.valueOf(attributeMap.getAttribute(namespace, name)), true));
      }
    }
    stringBuilder.append("/>");
    return stringBuilder.toString();
  }
}
