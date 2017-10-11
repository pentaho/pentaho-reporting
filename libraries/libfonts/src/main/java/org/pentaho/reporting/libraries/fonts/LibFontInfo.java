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

package org.pentaho.reporting.libraries.fonts;

import org.pentaho.reporting.libraries.base.LibBaseInfo;
import org.pentaho.reporting.libraries.base.versioning.ProjectInformation;
import org.pentaho.reporting.libraries.resourceloader.LibLoaderInfo;

/**
 * Creation-Date: 06.11.2005, 18:24:57
 *
 * @author Thomas Morgner
 */
public class LibFontInfo extends ProjectInformation {
  private static LibFontInfo instance;

  public static synchronized LibFontInfo getInstance() {
    if ( instance == null ) {
      instance = new LibFontInfo();
      instance.initialize();
    }
    return instance;
  }

  public LibFontInfo() {
    super( "libfonts", "LibFonts" );
  }

  private void initialize() {
    setLicenseName( "LGPL" );
    setInfo( "http://reporting.pentaho.org/libfonts/" );
    setCopyright( "(C)opyright 2006-2011, by Pentaho Corporation and Contributors" );

    setBootClass( "org.pentaho.reporting.libraries.fonts.LibFontBoot" );

    addLibrary( LibBaseInfo.getInstance() );
    addLibrary( LibLoaderInfo.getInstance() );
  }
}
