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

package org.pentaho.reporting.tools.configeditor;

import org.pentaho.reporting.libraries.base.versioning.ProjectInformation;
import org.pentaho.reporting.libraries.xmlns.LibXmlInfo;

public class ConfigEditorInfo extends ProjectInformation {
  private static ConfigEditorInfo instance;

  public static synchronized ConfigEditorInfo getInstance() {
    if ( instance == null ) {
      instance = new ConfigEditorInfo();
      instance.initialize();
    }
    return instance;
  }

  public ConfigEditorInfo() {
    super( "configuration-editor", "Report Designer Configuration Editor" );
  }

  private void initialize() {

    setLicenseName( "LGPL" );

    setInfo( "http://reporting.pentaho.org/config-editor/" );
    setCopyright( "(C)opyright 2007-2011, by Pentaho Corporation and Contributors" );

    setBootClass( "org.pentaho.reporting.tools.configeditor.ConfigEditorBoot" );

    addLibrary( LibXmlInfo.getInstance() );
  }
}
