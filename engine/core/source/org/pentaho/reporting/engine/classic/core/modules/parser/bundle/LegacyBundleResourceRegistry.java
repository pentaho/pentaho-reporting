package org.pentaho.reporting.engine.classic.core.modules.parser.bundle;

import java.util.LinkedHashSet;

public class LegacyBundleResourceRegistry
{
  private static final LegacyBundleResourceRegistry INSTANCE = new LegacyBundleResourceRegistry();
  private LinkedHashSet<String> registeredFiles;

  public static LegacyBundleResourceRegistry getInstance()
  {
    return INSTANCE;
  }

  private LegacyBundleResourceRegistry()
  {
    this.registeredFiles = new LinkedHashSet<String>();
  }

  public synchronized void register (final String name)
  {
    this.registeredFiles.add(name);
  }

  public synchronized String[] getRegisteredFiles ()
  {
    return this.registeredFiles.toArray(new String[registeredFiles.size()]);
  }
}
