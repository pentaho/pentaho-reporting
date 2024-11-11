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


package org.pentaho.reporting.libraries.repository;

import org.pentaho.reporting.libraries.base.LibBaseInfo;
import org.pentaho.reporting.libraries.base.versioning.ProjectInformation;

/**
 * The LibRepositoryInfo class contains all dependency information and some common information like version, license and
 * contributors about the library itself.
 *
 * @author Thomas Morgner
 */
public class LibRepositoryInfo extends ProjectInformation {
  private static LibRepositoryInfo instance;

  /**
   * Returns the singleton instance of the ProjectInformation-class.
   *
   * @return the singleton ProjectInformation.
   */
  public static synchronized ProjectInformation getInstance() {
    if ( instance == null ) {
      instance = new LibRepositoryInfo();
      instance.initialize();
    }
    return instance;
  }

  /**
   * Constructs an empty project info object.
   */
  private LibRepositoryInfo() {
    super( "librepository", "LibRepository" );
  }

  /**
   * Initialized the project info object.
   */
  private void initialize() {
    setLicenseName( "LGPL" );

    setInfo( "http://reporting.pentaho.org/librepository/" );
    setCopyright( "(C)opyright 2006-2011, by Pentaho Corporation and Contributors" );

    setBootClass( LibRepositoryBoot.class.getName() );
    addLibrary( LibBaseInfo.getInstance() );
  }
}
