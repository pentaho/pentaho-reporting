package org.pentaho.reporting.library.parameter;

import org.pentaho.reporting.libraries.base.boot.AbstractBoot;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.versioning.ProjectInformation;

public class LibParameterBoot extends AbstractBoot
{
  public LibParameterBoot()
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
    return null;
  }
}
