package org.pentaho.reporting.libraries.pensol;

import org.pentaho.reporting.libraries.base.LibBaseInfo;
import org.pentaho.reporting.libraries.base.versioning.ProjectInformation;

public class LibPensolInfo extends ProjectInformation
{
  private static LibPensolInfo instance;

  /**
   * Returns the singleton instance of the ProjectInformation-class.
   *
   * @return the singleton ProjectInformation.
   */
  public static synchronized ProjectInformation getInstance()
  {
    if (instance == null)
    {
      instance = new LibPensolInfo();
      instance.initialize();
    }
    return instance;
  }

  /**
   * Constructs an empty project info object.
   */
  private LibPensolInfo()
  {
    super("libpensol", "LibPenSol");
  }

  /**
   * Initialized the project info object.
   */
  private void initialize()
  {
    setLicenseName("LGPL");

    setInfo("http://reporting.pentaho.org/libpensol/");
    setCopyright("(C)opyright 2010, by Pentaho Corporation and Contributors");

    setBootClass(LibPensolBoot.class.getName());
    addLibrary(LibBaseInfo.getInstance());
  }
}
