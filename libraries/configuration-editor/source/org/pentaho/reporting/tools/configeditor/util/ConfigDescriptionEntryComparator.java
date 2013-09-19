package org.pentaho.reporting.tools.configeditor.util;

import java.util.Comparator;

import org.pentaho.reporting.tools.configeditor.model.ConfigDescriptionEntry;

public class ConfigDescriptionEntryComparator implements Comparator<ConfigDescriptionEntry>
{
  public ConfigDescriptionEntryComparator()
  {
  }

  public int compare(final ConfigDescriptionEntry e1, final ConfigDescriptionEntry e2)
  {
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
