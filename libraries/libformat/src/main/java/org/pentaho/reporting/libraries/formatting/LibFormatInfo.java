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
* Copyright (c) 2008 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.formatting;

import org.pentaho.reporting.libraries.base.LibBaseInfo;
import org.pentaho.reporting.libraries.base.versioning.ProjectInformation;

/**
 * Details about the LibFormat project.
 *
 * @author Thomas Morgner
 */
public class LibFormatInfo extends ProjectInformation {
  /**
   * A singleton instance of the Info class.
   */
  private static LibFormatInfo instance;

  /**
   * Returns the singleton instance of the Info-Object.
   *
   * @return te info object for this library.
   */
  public static synchronized ProjectInformation getInstance() {
    if ( instance == null ) {
      instance = new LibFormatInfo();
      instance.initialize();
    }
    return instance;
  }

  /**
   * Creates a new info-object.
   */
  private LibFormatInfo() {
    super( "libformat", "LibFormat" );
  }

  /**
   * Initializes the new info-object.
   */
  private void initialize() {
    setBootClass( LibFormatBoot.class.getName() );
    setLicenseName( "LGPL" );
    setInfo( "http://reporting.pentaho.org/libformat/" );
    setCopyright( "(C)opyright 2007-2011, by Pentaho Corporation and Contributors" );

    addLibrary( LibBaseInfo.getInstance() );
  }
}
