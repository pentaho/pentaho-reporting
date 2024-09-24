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

package org.pentaho.reporting.libraries.css;

import org.pentaho.reporting.libraries.base.LibBaseInfo;
import org.pentaho.reporting.libraries.base.versioning.ProjectInformation;
import org.pentaho.reporting.libraries.resourceloader.LibLoaderInfo;

/**
 * Creation-Date: 27.07.2007, 12:58:12
 *
 * @author Thomas Morgner
 */
public class LibCssInfo extends ProjectInformation {
  private static LibCssInfo info;

  /**
   * Constructs an empty project info object.
   */
  private LibCssInfo() {
    super( "libcss", "LibCSS" );
  }

  private void initialize() {
    setInfo( "http://reporting.pentaho.org/libcss/" );
    setCopyright( "(C)opyright 2007-2011, by Pentaho Corporation and Contributors" );


    addLibrary( LibBaseInfo.getInstance() );
    addLibrary( LibLoaderInfo.getInstance() );

    setBootClass( LibCssBoot.class.getName() );
  }

  /**
   * Returns the singleton instance of the boot-class.
   *
   * @return the singleton booter.
   */
  public static synchronized ProjectInformation getInstance() {
    if ( info == null ) {
      info = new LibCssInfo();
      info.initialize();
    }
    return info;
  }
}
