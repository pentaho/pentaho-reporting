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


package org.pentaho.reporting.libraries.resourceloader;

import org.pentaho.reporting.libraries.base.LibBaseInfo;
import org.pentaho.reporting.libraries.base.versioning.DependencyInformation;
import org.pentaho.reporting.libraries.base.versioning.ProjectInformation;

public class LibLoaderInfo extends ProjectInformation {
  private static LibLoaderInfo instance;

  public static LibLoaderInfo getInstance() {
    if ( instance == null ) {
      instance = new LibLoaderInfo();
      instance.initialize();
    }
    return instance;
  }

  /**
   * Constructs an empty project info object.
   */
  public LibLoaderInfo() {
    super( "libloader", "LibLoader" );
  }

  private void initialize() {
    setLicenseName( "LGPL" );

    setInfo( "http://reporting.pentaho.org/libloader/" );
    setCopyright( "(C)opyright 2006-2011, by Pentaho Corporation and Contributors" );

    setBootClass( LibLoaderBoot.class.getName() );

    addLibrary( LibBaseInfo.getInstance() );
    addOptionalLibrary( "org.pentaho.reporting.libraries.pixie.PixieInfo" );
    addOptionalLibrary( new DependencyInformation( "EHCache", "1.2rc1", "Apache Licence 2.0",
      "http://ehcache.sourceforge.net/" ) );
    addOptionalLibrary( new DependencyInformation( "Batik", "1.17", "Apache Software License",
      "http://xml.apache.org/batik" ) );
  }
}
