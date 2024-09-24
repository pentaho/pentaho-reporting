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
