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
