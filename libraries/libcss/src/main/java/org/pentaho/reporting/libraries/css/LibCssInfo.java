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
