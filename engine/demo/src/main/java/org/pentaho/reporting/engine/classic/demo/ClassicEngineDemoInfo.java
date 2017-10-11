/*
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

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
