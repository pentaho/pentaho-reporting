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
