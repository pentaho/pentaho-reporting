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
