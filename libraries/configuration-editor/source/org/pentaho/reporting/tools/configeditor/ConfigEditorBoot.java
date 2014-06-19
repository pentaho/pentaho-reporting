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
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package org.pentaho.reporting.tools.configeditor;

import org.pentaho.reporting.libraries.base.boot.AbstractBoot;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.versioning.ProjectInformation;

public class ConfigEditorBoot extends AbstractBoot
{
  public static final String NAMESPACE =
      "http://jfreereport.sourceforge.net/namespaces/config-description"; //$NON-NLS-1$

  public static final String BUNDLE_NAME = "org.pentaho.reporting.tools.configeditor.messages"; //$NON-NLS-1$

  private static ConfigEditorBoot instance;

  public static synchronized ConfigEditorBoot getInstance()
  {
    if (instance == null)
    {
      instance = new ConfigEditorBoot();
    }
    return instance;
  }

  private ConfigEditorBoot()
  {
  }

  protected Configuration loadConfiguration()
  {
    return createDefaultHierarchicalConfiguration
        ("/org/pentaho/reporting/tools/configeditor/config-editor.properties",
            "/config-editor.properties", true, ConfigEditorBoot.class);
  }

  protected void performBoot()
  {
  }

  protected ProjectInformation getProjectInfo()
  {
    return ConfigEditorInfo.getInstance();
  }

}
