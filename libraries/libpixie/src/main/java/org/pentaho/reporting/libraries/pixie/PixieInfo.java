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
