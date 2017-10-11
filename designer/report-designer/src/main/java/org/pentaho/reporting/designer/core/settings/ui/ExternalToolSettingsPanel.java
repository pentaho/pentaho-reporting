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
import java.awt.*;

/**
 * User: Martin Date: 02.03.2006 Time: 07:37:12
 */
public class ExternalToolSettingsPanel extends JPanel implements SettingsPlugin {
  private ToolSettingsPanel toolSettingsPanelPDF;
  private ToolSettingsPanel toolSettingsPanelRTF;
  private ToolSettingsPanel toolSettingsPanelXLS;
  private ToolSettingsPanel toolSettingsPanelCSV;

  public ExternalToolSettingsPanel() {
    final JTabbedPane tabbedPane = new JTabbedPane();

    toolSettingsPanelPDF = new ToolSettingsPanel();
    toolSettingsPanelRTF = new ToolSettingsPanel();
    toolSettingsPanelXLS = new ToolSettingsPanel();
    toolSettingsPanelCSV = new ToolSettingsPanel();

    tabbedPane.addTab( SettingsMessages.getInstance().getString( "ExternalToolSettingsPanel.Tab.PDF" ),
      toolSettingsPanelPDF );
    tabbedPane.addTab( SettingsMessages.getInstance().getString( "ExternalToolSettingsPanel.Tab.RTF" ),
      toolSettingsPanelRTF );
    tabbedPane.addTab( SettingsMessages.getInstance().getString( "ExternalToolSettingsPanel.Tab.XLS" ),
      toolSettingsPanelXLS );
    tabbedPane.addTab( SettingsMessages.getInstance().getString( "ExternalToolSettingsPanel.Tab.CSV" ),
      toolSettingsPanelCSV );

    setLayout( new BorderLayout() );
    add( tabbedPane, BorderLayout.CENTER );

    reset();
  }

  public JComponent getComponent() {
    return this;
  }

  public Icon getIcon() {
    return IconLoader.getInstance().getExternalToolsIcon32();
  }

  public String getTitle() {
    return SettingsMessages.getInstance().getString( "SettingsDialog.ExternalTool" );
  }

  public ValidationResult validate( final ValidationResult validationResult ) {
    toolSettingsPanelPDF.validate( validationResult );
    toolSettingsPanelRTF.validate( validationResult );
    toolSettingsPanelXLS.validate( validationResult );
    toolSettingsPanelCSV.validate( validationResult );
    return validationResult;
  }

  public void apply() {
    ExternalToolSettings.getInstance().setUseDefaultPDFViewer(
      toolSettingsPanelPDF.isUseDefaultApplication() );
    ExternalToolSettings.getInstance().setCustomPDFViewerExecutable(
      toolSettingsPanelPDF.getCustomExecutable() );
    ExternalToolSettings.getInstance().setCustomPDFViewerParameters(
      toolSettingsPanelPDF.getCustomExecutableParameters() );

    ExternalToolSettings.getInstance().setUseDefaultRTFViewer(
      toolSettingsPanelRTF.isUseDefaultApplication() );
    ExternalToolSettings.getInstance().setCustomRTFViewerExecutable(
      toolSettingsPanelRTF.getCustomExecutable() );
    ExternalToolSettings.getInstance().setCustomRTFViewerParameters(
      toolSettingsPanelRTF.getCustomExecutableParameters() );

    ExternalToolSettings.getInstance().setUseDefaultXLSViewer(
      toolSettingsPanelXLS.isUseDefaultApplication() );
    ExternalToolSettings.getInstance().setCustomXLSViewerExecutable(
      toolSettingsPanelXLS.getCustomExecutable() );
    ExternalToolSettings.getInstance().setCustomXLSViewerParameters(
      toolSettingsPanelXLS.getCustomExecutableParameters() );

    ExternalToolSettings.getInstance().setUseDefaultCSVViewer(
      toolSettingsPanelCSV.isUseDefaultApplication() );
    ExternalToolSettings.getInstance().setCustomCSVViewerExecutable(
      toolSettingsPanelCSV.getCustomExecutable() );
    ExternalToolSettings.getInstance().setCustomCSVViewerParameters(
      toolSettingsPanelCSV.getCustomExecutableParameters() );
  }


  public void reset() {
    toolSettingsPanelPDF.setUseDefaultApplication(
      ExternalToolSettings.getInstance().isUseDefaultPDFViewer() );
    toolSettingsPanelPDF.setCustomExecutable(
      ExternalToolSettings.getInstance().getCustomPDFViewerExecutable() );
    toolSettingsPanelPDF.setCustomExecutableParameters(
      ExternalToolSettings.getInstance().getCustomPDFViewerParameters() );

    toolSettingsPanelRTF.setUseDefaultApplication(
      ExternalToolSettings.getInstance().isUseDefaultRTFViewer() );
    toolSettingsPanelRTF.setCustomExecutable(
      ExternalToolSettings.getInstance().getCustomRTFViewerExecutable() );
    toolSettingsPanelRTF.setCustomExecutableParameters(
      ExternalToolSettings.getInstance().getCustomRTFViewerParameters() );

    toolSettingsPanelXLS.setUseDefaultApplication(
      ExternalToolSettings.getInstance().isUseDefaultXLSViewer() );
    toolSettingsPanelXLS.setCustomExecutable(
      ExternalToolSettings.getInstance().getCustomXLSViewerExecutable() );
    toolSettingsPanelXLS.setCustomExecutableParameters(
      ExternalToolSettings.getInstance().getCustomXLSViewerParameters() );

    toolSettingsPanelCSV.setUseDefaultApplication(
      ExternalToolSettings.getInstance().isUseDefaultCSVViewer() );
    toolSettingsPanelCSV.setCustomExecutable(
      ExternalToolSettings.getInstance().getCustomCSVViewerExecutable() );
    toolSettingsPanelCSV.setCustomExecutableParameters(
      ExternalToolSettings.getInstance().getCustomCSVViewerParameters() );

  }

}
