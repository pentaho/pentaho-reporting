/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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
