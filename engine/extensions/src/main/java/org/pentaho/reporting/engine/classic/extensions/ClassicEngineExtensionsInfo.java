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

package org.pentaho.reporting.engine.classic.extensions;

import org.pentaho.reporting.engine.classic.core.ClassicEngineInfo;
import org.pentaho.reporting.libraries.base.versioning.ProjectInformation;

/**
 * Creation-Date: 21.05.2007, 15:26:09
 *
 * @author Thomas Morgner
 */
public class ClassicEngineExtensionsInfo extends ProjectInformation {
  private static ClassicEngineExtensionsInfo info;

  /**
   * Constructs an empty project info object.
   */
  private ClassicEngineExtensionsInfo() {
    super( "classic-extensions", "Pentaho Reporting Classic Extensions" );
  }

  private void initialize() {
    setInfo( "http://reporting.pentaho.org/" );
    setCopyright( "(C)opyright 2000-2011, by Pentaho Corp. and Contributors" );
    setLicenseName( "LGPL" );
    addLibrary( ClassicEngineInfo.getInstance() );
  }

  public static synchronized ProjectInformation getInstance() {
    if ( info == null ) {
      info = new ClassicEngineExtensionsInfo();
      info.initialize();
    }
    return info;
  }
}
