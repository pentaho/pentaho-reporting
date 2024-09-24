/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.reporting.engine.classic.demo;

import org.pentaho.reporting.engine.classic.core.ClassicEngineInfo;
import org.pentaho.reporting.libraries.base.versioning.ProjectInformation;

/**
 * Creation-Date: 02.12.2006, 19:20:18
 *
 * @author Thomas Morgner
 */
public class ClassicEngineDemoInfo extends ProjectInformation
{
  private static ClassicEngineDemoInfo info;

  /**
   * Constructs an empty project info object.
   */
  private ClassicEngineDemoInfo()
  {
    super("classic-demo", "Pentaho Reporting Engine Demo");
  }

  private void initialize()
  {
    setInfo("http://reporting.pentaho.org");
    setCopyright("(C)opyright 2000-2011, by Pentaho Corp. and Contributors");

    addLibrary(ClassicEngineInfo.getInstance());

    setBootClass(ClassicEngineDemoBoot.class.getName());
  }


  public static synchronized ProjectInformation getInstance()
  {
    if (info == null)
    {
      info = new ClassicEngineDemoInfo();
      info.initialize();
    }
    return info;
  }
}
