package org.pentaho.reporting.library.parameter;

import org.pentaho.reporting.libraries.base.LibBaseInfo;
import org.pentaho.reporting.libraries.base.versioning.DependencyInformation;
import org.pentaho.reporting.libraries.base.versioning.ProjectInformation;

public class LibParameterInfo extends ProjectInformation
{
  private static LibParameterInfo instance;

  public static LibParameterInfo getInstance()
  {
    if (instance == null)
    {
      instance = new LibParameterInfo();
      instance.initialize();
    }
    return instance;
  }

  /**
   * Constructs an empty project info object.
   */
  public LibParameterInfo()
  {
    super("libparameter", "LibParameter");
  }

  private void initialize()
  {
    setLicenseName("LGPL");

    setInfo("http://reporting.pentaho.org/");
    setCopyright("(C)opyright 2013, by Pentaho Corporation and Contributors");

    setBootClass(LibParameterInfo.class.getName());

    addLibrary(LibBaseInfo.getInstance());
  }
}
