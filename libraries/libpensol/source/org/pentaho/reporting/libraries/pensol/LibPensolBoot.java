package org.pentaho.reporting.libraries.pensol;

import org.pentaho.reporting.libraries.base.boot.AbstractBoot;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.versioning.ProjectInformation;

public class LibPensolBoot extends AbstractBoot
{
  private static LibPensolBoot instance;

  /**
   * Returns the singleton instance of the boot-class.
   *
   * @return the singleton booter.
   */
  public static synchronized LibPensolBoot getInstance()
  {
    if (instance == null)
    {
      instance = new LibPensolBoot();
    }
    return instance;
  }

  /**
   * Private constructor prevents object creation.
   */
  private LibPensolBoot()
  {
  }

  /**
   * Loads the configuration.
   *
   * @return The configuration.
   */
  protected Configuration loadConfiguration()
  {
    return createDefaultHierarchicalConfiguration
        ("/org/pentaho/reporting/libraries/pensol/libpensol.properties",
            "/libpensol.properties", true, LibPensolBoot.class);

  }

  /**
   * Performs the boot.
   */
  protected void performBoot()
  {
  }

  /**
   * Returns the project info.
   *
   * @return The project info.
   */
  protected ProjectInformation getProjectInfo()
  {
    return LibPensolInfo.getInstance();
  }
}
