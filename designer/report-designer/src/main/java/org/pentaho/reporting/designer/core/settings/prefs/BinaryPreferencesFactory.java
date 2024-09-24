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

package org.pentaho.reporting.designer.core.settings.prefs;

import org.pentaho.reporting.designer.core.ReportDesignerBoot;
import org.pentaho.reporting.libraries.base.config.Configuration;

import java.io.File;
import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class BinaryPreferencesFactory implements PreferencesFactory {
  private BinaryPreferences userRoot;
  private BinaryPreferences systemRoot;

  public BinaryPreferencesFactory() {
    final Configuration configuration = ReportDesignerBoot.getInstance().getGlobalConfig();
    final String homeDirectory = configuration.getConfigProperty( "user.home", "." );//NON-NLS


    final String root =
      homeDirectory + File.separatorChar + ".pentaho" + File.separatorChar + "report-designer";//NON-NLS
    userRoot = new BinaryPreferences( root + File.separatorChar + "user" );//NON-NLS
    systemRoot = new BinaryPreferences( root + File.separatorChar + "system" );//NON-NLS
  }

  public Preferences systemRoot() {
    return systemRoot;
  }

  public Preferences userRoot() {
    return userRoot;
  }
}
