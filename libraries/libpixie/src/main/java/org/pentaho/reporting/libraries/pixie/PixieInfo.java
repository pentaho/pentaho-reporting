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
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.libraries.pixie;

import org.pentaho.reporting.libraries.base.LibBaseInfo;
import org.pentaho.reporting.libraries.base.versioning.ProjectInformation;

public class PixieInfo extends ProjectInformation {
  private static PixieInfo singleton;

  /**
   * Returns the single instance of this class.
   *
   * @return The single instance of information about the JCommon library.
   */
  public static synchronized PixieInfo getInstance() {
    if ( singleton == null ) {
      singleton = new PixieInfo();
    }
    return singleton;
  }

  /**
   * Creates a new instance. (Must be public so that we can instantiate the library-info using Class.newInstance(..)).
   */
  public PixieInfo() {
    super( "libpixie", "Pixie" );
    setInfo( "http://reporting.pentaho.org/pixie/" );
    setCopyright( "(C)opyright 2000-2011, by Pentaho Corporation, Object Refinery Limited and Contributors" );
    setLicenseName( "LGPL" );

    addLibrary( LibBaseInfo.getInstance() );
  }
}
