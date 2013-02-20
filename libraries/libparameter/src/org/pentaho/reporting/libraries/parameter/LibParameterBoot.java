package org.pentaho.reporting.libraries.parameter;

import org.pentaho.reporting.libraries.base.boot.AbstractBoot;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.versioning.ProjectInformation;

public class LibParameterBoot extends AbstractBoot
{
  private static final LibParameterBoot instance = new LibParameterBoot();
  public static final String NAMESPACE = "http://reporting.pentaho.org/namespaces/parameter-definition/1.0";

  public static LibParameterBoot getInstance()
  {
    return instance;
  }

  private LibParameterBoot()
  {
  }

  protected Configuration loadConfiguration()
  {
    return createDefaultHierarchicalConfiguration
        ("/org/pentaho/reporting/libraries/resourceloader/libparameter.properties",
            "/libparameter.properties", true, LibParameterBoot.class);
  }

  protected void performBoot()
  {

  }

  protected ProjectInformation getProjectInfo()
  {
    return LibParameterInfo.getInstance();
  }
}
