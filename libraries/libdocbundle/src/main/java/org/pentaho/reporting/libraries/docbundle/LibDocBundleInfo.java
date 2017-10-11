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

package org.pentaho.reporting.libraries.docbundle;

import org.pentaho.reporting.libraries.base.versioning.ProjectInformation;
import org.pentaho.reporting.libraries.repository.LibRepositoryInfo;
import org.pentaho.reporting.libraries.resourceloader.LibLoaderInfo;
import org.pentaho.reporting.libraries.xmlns.LibXmlInfo;

/**
 * Details about the LibDocBundle project.
 *
 * @author Thomas Morgner
 */
public final class LibDocBundleInfo extends ProjectInformation {
  /**
   * A singleton instance of the Info class.
   */
  private static LibDocBundleInfo instance;

  /**
   * Returns the singleton instance of the Info-Object.
   *
   * @return te info object for this library.
   */
  public static ProjectInformation getInstance() {
    if ( instance == null ) {
      instance = new LibDocBundleInfo();
      instance.initialize();
    }
    return instance;
  }

  private LibDocBundleInfo() {
    super( "libdocbundle", "LibDocBundle" );
  }

  private void initialize() {
    setBootClass( LibDocBundleBoot.class.getName() );
    setLicenseName( "LGPL" );
    setInfo( "http://reporting.pentaho.org/libdocbundle/" );
    setCopyright( "(C)opyright 2007-2011, by Pentaho Corporation and Contributors" );

    addLibrary( LibLoaderInfo.getInstance() );
    addLibrary( LibXmlInfo.getInstance() );
    addLibrary( LibRepositoryInfo.getInstance() );
  }
}
