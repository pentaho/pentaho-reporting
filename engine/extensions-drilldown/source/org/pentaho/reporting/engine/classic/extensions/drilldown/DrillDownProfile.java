package org.pentaho.reporting.engine.classic.extensions.drilldown;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.pentaho.reporting.engine.classic.core.metadata.AbstractMetaData;

public class DrillDownProfile extends AbstractMetaData
{
  private Class linkCustomizerType;
  private HashMap<String,String> attributes;

  public DrillDownProfile(final String name,
                          final String bundleLocation,
                          final String keyPrefix,
                          final boolean expert,
                          final boolean preferred,
                          final boolean hidden,
                          final boolean deprecated,
                          final Class linkCustomizerType,
                          final Map<String,String> attributes,
                          final boolean experimental,
                          final int compatibilityLevel)
  {
    super(name, bundleLocation, keyPrefix, expert, preferred, hidden, deprecated, experimental, compatibilityLevel);
    this.linkCustomizerType = linkCustomizerType;
    this.attributes = new HashMap<String,String>(attributes);
  }

  public DrillDownProfile(final Class linkCustomizerType)
  {
    this("", "org.pentaho.reporting.engine.classic.extensions.drilldown.drilldown-profile",
        "", false, false, false, false, linkCustomizerType, new HashMap<String,String>(), false, -1);
  }

  public Class getLinkCustomizerType()
  {
    return linkCustomizerType;
  }

  public String getAttribute(final String name)
  {
    return attributes.get(name);
  }

  public String[] getAttributes()
  {
    return attributes.keySet().toArray(new String[attributes.size()]);
  }

  public String getGroupDisplayName(final Locale locale)
  {
    return getBundle(locale).getString("drilldown-profile-group." + getAttribute("group") + ".display-name");//NON-NLS
  }
}
