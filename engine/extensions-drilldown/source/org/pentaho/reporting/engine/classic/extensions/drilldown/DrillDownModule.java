package org.pentaho.reporting.engine.classic.extensions.drilldown;

import java.net.URL;

import org.pentaho.reporting.libraries.base.boot.AbstractModule;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.SubSystem;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

public class DrillDownModule extends AbstractModule
{
  public static final String DRILLDOWN_PROFILE_NAMESPACE =
      "http://reporting.pentaho.org/namespaces/engine/classic/drilldown-profile/1.0";

  public DrillDownModule() throws ModuleInitializeException
  {
    loadModuleInfo();
  }

  /**
   * Initializes the module. Use this method to perform all initial setup operations.
   * This method is called only once in a modules lifetime. If the initializing cannot
   * be completed, throw a ModuleInitializeException to indicate the error,. The module
   * will not be available to the system.
   *
   * @param subSystem the subSystem.
   * @throws org.pentaho.reporting.libraries.base.boot.ModuleInitializeException
   *          if an error ocurred while initializing the module.
   */
  public void initialize(final SubSystem subSystem) throws ModuleInitializeException
  {
    final URL expressionMetaSource = ObjectUtilities.getResource
        ("org/pentaho/reporting/engine/classic/extensions/drilldown/drilldown-profile.xml", DrillDownModule.class);
    if (expressionMetaSource == null)
    {
      throw new ModuleInitializeException("Error: Could not find the drilldown meta-data description file");
    }
    register(expressionMetaSource);

    final URL customizedMetaSource = ObjectUtilities.getResource("drilldown-profile.xml", DrillDownModule.class);
    if (customizedMetaSource != null)
    {
      register(customizedMetaSource);
    }
  }

  private void register(final URL expressionMetaSource)
      throws ModuleInitializeException
  {
    if (expressionMetaSource == null)
    {
      return;
    }
    try
    {
      DrillDownProfileMetaData.getInstance().registerFromXml(expressionMetaSource);
    }
    catch (Exception e)
    {
      throw new ModuleInitializeException("Error: Could not parse the drilldown meta-data description file", e);
    }
  }
}
