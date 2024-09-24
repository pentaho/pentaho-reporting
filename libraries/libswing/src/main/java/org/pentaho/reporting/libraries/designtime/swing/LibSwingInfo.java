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

package org.pentaho.reporting.libraries.designtime.swing;

import org.pentaho.reporting.libraries.base.LibBaseInfo;
import org.pentaho.reporting.libraries.base.versioning.ProjectInformation;

/**
 * Details about the LibFormat project.
 *
 * @author Thomas Morgner
 */
public class LibSwingInfo extends ProjectInformation {
  /**
   * A singleton instance of the Info class.
   */
  private static LibSwingInfo instance;

  /**
   * Returns the singleton instance of the Info-Object.
   *
   * @return te info object for this library.
   */
  public static synchronized ProjectInformation getInstance() {
    if ( instance == null ) {
      instance = new LibSwingInfo();
      instance.initialize();
    }
    return instance;
  }

  /**
   * Creates a new info-object.
   */
  private LibSwingInfo() {
    super( "libswing", "LibSwing" );
  }

  /**
   * Initializes the new info-object.
   */
  private void initialize() {
    setBootClass( LibSwingBoot.class.getName() );
    setLicenseName( "LGPL" );
    setInfo( "http://reporting.pentaho.org/libswing/" );
    setCopyright( "(C)opyright 2008-2011, by Pentaho Corporation and Contributors" );

    addLibrary( LibBaseInfo.getInstance() );
  }
}
