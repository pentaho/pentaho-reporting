package org.pentaho.reporting.engine.classic.extensions.drilldown;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.extensions.drilldown.parser.DrillDownProfileCollection;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class DrillDownProfileMetaData
{
  private static final Log logger = LogFactory.getLog(DrillDownProfileMetaData.class);
  private static DrillDownProfileMetaData instance;
  private ResourceManager resourceManager;

  public static synchronized DrillDownProfileMetaData getInstance()
  {
    if (instance == null)
    {
      instance = new DrillDownProfileMetaData();
    }
    return instance;
  }

  private HashMap<String, DrillDownProfile> drillDownProfiles;

  private DrillDownProfileMetaData()
  {
    this.resourceManager = new ResourceManager();
    drillDownProfiles = new HashMap<String, DrillDownProfile>();
  }

  public void registerProfile(final DrillDownProfile profile)
  {
    drillDownProfiles.put(profile.getName(), profile);
  }

  public void registerFromXml(final URL expressionMetaSource) throws IOException
  {
    if (expressionMetaSource == null)
    {
      throw new NullPointerException("Error: Could not find the expression meta-data description file");
    }
    try
    {
      final Resource resource = resourceManager.createDirectly(expressionMetaSource,
          DrillDownProfileCollection.class);
      final DrillDownProfileCollection typeCollection = (DrillDownProfileCollection) resource.getResource();
      final DrillDownProfile[] types = typeCollection.getData();
      for (int i = 0; i < types.length; i++)
      {
        final DrillDownProfile metaData = types[i];
        if (metaData != null)
        {
          registerProfile(metaData);
        }
      }
    }
    catch (Exception e)
    {
      DrillDownProfileMetaData.logger.error("Failed:", e);
      throw new IOException("Error: Could not parse the element meta-data description file");
    }
  }

  public DrillDownProfile getDrillDownProfile(final String configIndicator)
  {
    return drillDownProfiles.get(configIndicator);
  }

  public DrillDownProfile[] getDrillDownProfiles()
  {
    return drillDownProfiles.values().toArray(new DrillDownProfile[drillDownProfiles.size()]);
  }

  public String[] getDrillDownProfileNames()
  {
    return drillDownProfiles.keySet().toArray(new String[drillDownProfiles.size()]);
  }

  public DrillDownProfile[] getDrillDownProfileByGroup(final String group)
  {
    final ArrayList<DrillDownProfile> profiles = new ArrayList<DrillDownProfile>();
    for (final DrillDownProfile profile : drillDownProfiles.values())
    {
      if (ObjectUtilities.equal(profile.getAttribute("group"), group))
      {
        profiles.add(profile);
      }
    }
    return profiles.toArray(new DrillDownProfile[profiles.size()]);
  }
}
