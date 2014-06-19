/*!
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
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

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
