/*!
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

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
