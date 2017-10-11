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

package org.pentaho.reporting.designer.core.settings.ui;

import org.pentaho.reporting.designer.core.settings.ExternalToolSettings;
import org.pentaho.reporting.designer.core.settings.SettingsMessages;
import org.pentaho.reporting.designer.core.util.IconLoader;

import javax.swing.*;

/**
 * User: Martin Date: 02.03.2006 Time: 07:37:12
 */
public class BrowserSettingsPanel extends ToolSettingsPanel implements SettingsPlugin {
  public BrowserSettingsPanel() {
    reset();
  }

  protected String getCustomApplicationTranslation() {
    return SettingsMessages.getInstance().getString( "BrowserSettingsPanel.customExecutableRadioButton" );
  }

  protected String getDefaultApplicationTranslation() {
    return SettingsMessages.getInstance().getString( "BrowserSettingsPanel.defaultBrowserRadioButton" );
  }

  public void apply() {
    ExternalToolSettings.getInstance().setUseDefaultBrowser( isUseDefaultApplication() );
    ExternalToolSettings.getInstance().setCustomBrowserExecutable( getCustomExecutable() );
    ExternalToolSettings.getInstance().setCustomBrowserParameters( getCustomExecutableParameters() );
  }

  public void reset() {
    setUseDefaultApplication( ExternalToolSettings.getInstance().isUseDefaultBrowser() );
    setCustomExecutable( ExternalToolSettings.getInstance().getCustomBrowserExecutable() );
    setCustomExecutableParameters( ExternalToolSettings.getInstance().getCustomBrowserParameters() );
  }

  public JComponent getComponent() {
    return this;
  }

  public Icon getIcon() {
    return IconLoader.getInstance().getBrowserIcon32();
  }

  public String getTitle() {
    return SettingsMessages.getInstance().getString( "SettingsDialog.Browser" );
  }
}
