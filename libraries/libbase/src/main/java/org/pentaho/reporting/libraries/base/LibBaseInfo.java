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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.libraries.base;

import org.pentaho.reporting.libraries.base.versioning.ProjectInformation;

/**
 * The project information for LibBase.
 *
 * @author Thomas Morgner
 * @noinspection UseOfSystemOutOrSystemErr
 */
public final class LibBaseInfo extends ProjectInformation {
  /**
   * A singleton variable for the info-class.
   */
  private static LibBaseInfo info;

  /**
   * Returns a singleton instance of the LibBase project information structure.
   *
   * @return the LibBase project information.
   */
  public static synchronized ProjectInformation getInstance() {
    if ( info == null ) {
      info = new LibBaseInfo();
      info.initialize();
    }
    return info;
  }

  /**
   * Private constructor to prevent object creation.
   */
  private LibBaseInfo() {
    super( "libbase", "LibBase" );
  }

  /**
   * Initializes the project info.
   */
  private void initialize() {
    setBootClass( LibBaseBoot.class.getName() );
    setLicenseName( "LGPL" );
    setInfo( "http://reporting.pentaho.org/libbase/" );
    setCopyright( "(C)opyright 2007-2011, by Pentaho Corporation and Contributors" );
  }

  /**
   * The main method can be used to check the version of the code.
   *
   * @param args not used.
   */
  public static void main( final String[] args ) {
    System.out.println( getInstance().getVersion() );
  }
}
