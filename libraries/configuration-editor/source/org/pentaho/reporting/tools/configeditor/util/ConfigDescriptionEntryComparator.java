package org.pentaho.reporting.tools.configeditor.util;

import java.util.Comparator;

import org.pentaho.reporting.tools.configeditor.model.ConfigDescriptionEntry;

public class ConfigDescriptionEntryComparator implements Comparator
{
  public ConfigDescriptionEntryComparator()
  {
  }

  public int compare(final Object o1, final Object o2)
  {
    final ConfigDescriptionEntry e1 = (ConfigDescriptionEntry) o1;
    final ConfigDescriptionEntry e2 = (ConfigDescriptionEntry) o2;
    if (e1 == null)
    {
      return 1;
    }
    if (e2 == null)
    {
      return -1;
    }
    if (e1 == e2)
    {
      return 0;
    }
    return e1.getKeyName().compareTo(e2.getKeyName());
  }
}
