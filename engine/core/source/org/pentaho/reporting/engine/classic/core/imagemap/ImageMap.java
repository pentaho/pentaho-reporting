package org.pentaho.reporting.engine.classic.core.imagemap;

import java.io.Serializable;
import java.util.ArrayList;

import org.pentaho.reporting.libraries.xmlns.common.AttributeMap;

public class ImageMap implements Serializable
{
  private AttributeMap<String> attributes;
  private ArrayList<ImageMapEntry> mapEntries;

  public ImageMap ()
  {
    attributes = new AttributeMap<String>();
    mapEntries = new ArrayList<ImageMapEntry>();
  }

  public void addMapEntry (final ImageMapEntry mapEntry)
  {
    if (mapEntry == null)
    {
      throw new NullPointerException();
    }
    mapEntries.add(mapEntry);
  }

  public ImageMapEntry[] getMapEntries()
  {
    return mapEntries.toArray(new ImageMapEntry[this.mapEntries.size()]);
  }

  public int getMapEntryCount()
  {
    return mapEntries.size();
  }

  public ImageMapEntry getMapEntry(final int index)
  {
    return mapEntries.get(index);
  }

  public void setAttribute(final String namespace, final String attribute, final String value)
  {
    attributes.setAttribute(namespace, attribute, value);
  }

  public String getAttribute(final String namespace, final String attribute)
  {
    return attributes.getAttribute(namespace, attribute);
  }

  public String[] getNames(final String namespace)
  {
    return attributes.getNames(namespace);
  }

  public String[] getNameSpaces()
  {
    return attributes.getNameSpaces();
  }

  public ImageMapEntry[] getEntriesForPoint(float x, float y)
  {
    final ArrayList<ImageMapEntry> list = new ArrayList<ImageMapEntry>();
    for (int i = 0; i < mapEntries.size(); i++)
    {
      final ImageMapEntry entry = mapEntries.get(i);
      if (entry.contains(x,y))
      {
        list.add(entry);
      }
    }
    return list.toArray(new ImageMapEntry[list.size()]);
  }
}
