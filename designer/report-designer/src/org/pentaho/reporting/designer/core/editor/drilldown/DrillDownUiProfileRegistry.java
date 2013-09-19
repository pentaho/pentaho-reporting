package org.pentaho.reporting.designer.core.editor.drilldown;

import java.util.HashMap;
import java.util.Iterator;

import org.pentaho.reporting.designer.core.ReportDesignerBoot;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.DebugLog;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * Todo: Document me!
 * <p/>
 * Date: 05.08.2010
 * Time: 13:25:17
 *
 * @author Thomas Morgner.
 */
public class DrillDownUiProfileRegistry
{
  private static DrillDownUiProfileRegistry instance;
  private HashMap<String, DrillDownUiProfile> registry;
  private static final String PREFIX = "org.pentaho.reporting.designer.core.editor.drilldown.profiles.";

  public static synchronized DrillDownUiProfileRegistry getInstance()
  {
    if (instance == null)
    {
      instance = new DrillDownUiProfileRegistry();
      instance.initialize();
    }
    return instance;
  }

  private DrillDownUiProfileRegistry()
  {
    this.registry = new HashMap<String, DrillDownUiProfile>();
  }

  public void addProfile(final String name, final DrillDownUiProfile profile)
  {
    if (profile == null)
    {
      throw new NullPointerException();
    }
    registry.put(name, profile);
  }

  public void initialize()
  {
    final Configuration configuration = ReportDesignerBoot.getInstance().getGlobalConfig();
    final Iterator keys = configuration.findPropertyKeys(PREFIX);//NON-NLS
    while (keys.hasNext())
    {
      final String key = (String) keys.next();
      final String name = key.substring(PREFIX.length());
      final String className = configuration.getConfigProperty(key);
      if (className == null)
      {
        DebugLog.log("No such profile: " + key);//NON-NLS
        continue;
      }
      final DrillDownUiProfile profile = (DrillDownUiProfile) ObjectUtilities.loadAndInstantiate
          (className, DrillDownEditor.class, DrillDownUiProfile.class);
      if (profile == null)
      {
        DebugLog.log("Invalid profile: " + key);//NON-NLS
        continue;
      }
      addProfile(name, profile);
    }
  }

  public DrillDownUiProfile getProfile(String name)
  {
    return registry.get(name);
  }

  public DrillDownUiProfile[] getProfiles()
  {
    return registry.values().toArray(new DrillDownUiProfile[registry.size()]);
  }

  public int getProfileCount()
  {
    return registry.size();
  }
}
