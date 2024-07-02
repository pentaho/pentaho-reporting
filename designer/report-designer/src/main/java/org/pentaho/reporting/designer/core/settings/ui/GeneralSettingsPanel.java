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
* Copyright (c) 2024 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.designer.core.settings.ui;

import org.pentaho.reporting.designer.core.settings.DateFormatModel;
import org.pentaho.reporting.designer.core.settings.NumberFormatModel;
import org.pentaho.reporting.designer.core.settings.SettingsMessages;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.engine.classic.core.metadata.MaturityLevel;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * User: Martin Date: 01.03.2006 Time: 18:15:58
 */
public class GeneralSettingsPanel extends JPanel implements SettingsPlugin {
  private class LookAndFeelModel extends DefaultComboBoxModel {

    private LookAndFeelInfo lnfs[];
    private int selectedIndex;

    public LookAndFeelModel() {
      lnfs = UIManager.getInstalledLookAndFeels();
      selectedIndex = -1;
    }

    public int getSize() {
      return lnfs.length;
    }

    public Object getElementAt( final int index ) {
      // display value
      return lnfs[ index ].getName();
    }

    public Object getSelectedItem() {
      if ( selectedIndex >= 0 ) {
        return lnfs[ selectedIndex ].getName();
      }
      return null;
    }

    public void setSelectedItem( final Object anObject ) {
      // based on classname (or if that fails, try based on LNF name)
      for ( int i = 0; anObject != null && i < lnfs.length; i++ ) {
        final LookAndFeelInfo lnf = lnfs[ i ];
        if ( lnf.getClassName().equalsIgnoreCase( anObject.toString() ) ) {
          selectedIndex = i;
          return;
        }
      }
      for ( int i = 0; anObject != null && i < lnfs.length; i++ ) {
        final LookAndFeelInfo lnf = lnfs[ i ];
        if ( lnf.getName().equalsIgnoreCase( anObject.toString() ) ) {
          selectedIndex = i;
          return;
        }
      }
      selectedIndex = -1;
    }
  }

  private class EditFormatAction extends AbstractAction {
    private EditFormatAction() {
      putValue( Action.NAME, SettingsMessages.getInstance().getString( "GeneralSettingsPanel.EditFormat.Name" ) );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      final Window window = SwingUtilities.getWindowAncestor( GeneralSettingsPanel.this );
      final FormatEditorDialog dialog;
      if ( window instanceof Dialog ) {
        dialog = new FormatEditorDialog( (Dialog) window );
      } else if ( window instanceof Frame ) {
        dialog = new FormatEditorDialog( (Frame) window );
      } else {
        dialog = new FormatEditorDialog();
      }

      final FormatEditorDialog.Result o = dialog.editArray( dateFormats, numberFormats );
      if ( o == null ) {
        return;
      }
      dateFormats = o.getDateFormats();
      numberFormats = o.getNumberFormats();
    }
  }

  private JCheckBox showExpertItems;
  private JCheckBox openLastReport;
  private JCheckBox showDeprecatedItems;
  private JCheckBox showDebugItems;
  private JCheckBox experimentalFeatures;
  private LookAndFeelModel lookAndFeelModel;
  private JTextField dateFormatField;
  private JTextField timeFormatField;
  private JTextField datetimeFormatField;
  private JCheckBox showIndexColumns;
  private String[] dateFormats;
  private String[] numberFormats;
  private DateFormatModel dateFormatModel;
  private NumberFormatModel numberFormatModel;

  public GeneralSettingsPanel() {
    setLayout( new BorderLayout() );

    dateFormatModel = new DateFormatModel();
    numberFormatModel = new NumberFormatModel();

    dateFormats = new String[ 0 ];
    numberFormats = new String[ 0 ];

    final WorkspaceSettings workspaceSettings = WorkspaceSettings.getInstance();

    dateFormatField = new JTextField();
    dateFormatField.setColumns( 50 );
    timeFormatField = new JTextField();
    timeFormatField.setColumns( 50 );
    datetimeFormatField = new JTextField();
    datetimeFormatField.setColumns( 50 );

    lookAndFeelModel = new LookAndFeelModel();
    lookAndFeelModel.setSelectedItem( workspaceSettings.getLNF() );

    experimentalFeatures = new JCheckBox
      ( SettingsMessages.getInstance().getString( "GeneralSettingsPanel.EnableCommunityFeatures" ) );
    experimentalFeatures.setSelected( workspaceSettings.isExperimentalFeaturesVisible() );

    showIndexColumns = new JCheckBox
      ( SettingsMessages.getInstance().getString( "GeneralSettingsPanel.ShowIndexColumns" ) );
    showIndexColumns.setSelected( workspaceSettings.isShowIndexColumns() );

    showDeprecatedItems = new JCheckBox
      ( SettingsMessages.getInstance().getString( "GeneralSettingsPanel.ShowDeprecatedItems" ) );
    showDeprecatedItems.setSelected( workspaceSettings.isShowDeprecatedItems() );
    showExpertItems = new JCheckBox
      ( SettingsMessages.getInstance().getString( "GeneralSettingsPanel.ShowExpertItems" ) );
    showExpertItems.setSelected( workspaceSettings.isShowExpertItems() );

    showDebugItems = new JCheckBox
      ( SettingsMessages.getInstance().getString( "GeneralSettingsPanel.ShowDebugItems" ) );
    showDebugItems.setSelected( workspaceSettings.isDebugFeaturesVisible() );


    openLastReport = new JCheckBox
      ( SettingsMessages.getInstance().getString( "GeneralSettingsPanel.OpenLastReport" ) );
    openLastReport.setSelected( workspaceSettings.isShowExpertItems() );

    final JPanel contentPanel = new JPanel();
    contentPanel.setLayout( new GridBagLayout() );
    final GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    contentPanel.add( createLookandFeelPanel(), c );

    c.gridx = 0;
    c.gridy = 2;
    c.weightx = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    contentPanel.add( createDataFormatSettings(), c );

    c.gridx = 0;
    c.gridy = 3;
    c.weightx = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    contentPanel.add( createOtherSettings(), c );

    add( contentPanel, BorderLayout.NORTH );

    reset();
  }

  private JPanel createLookandFeelPanel() {
    final JComboBox lookAndFeels = new JComboBox( lookAndFeelModel );

    final JPanel lookAndFeelPanel = new JPanel( new GridBagLayout() );
    lookAndFeelPanel.setBorder( BorderFactory.createTitledBorder
      ( SettingsMessages.getInstance().getString( "GeneralSettingsPanel.LookAndFeel" ) ) );

    final GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 0;
    c.weightx = 1;
    c.fill = GridBagConstraints.NONE;
    c.anchor = GridBagConstraints.WEST;
    lookAndFeelPanel.add( lookAndFeels, c );
    return lookAndFeelPanel;
  }

  private JPanel createOtherSettings() {
    final JPanel updatesPanel = new JPanel();
    updatesPanel.setLayout( new GridBagLayout() );
    updatesPanel.setBorder( BorderFactory.createTitledBorder
      ( SettingsMessages.getInstance().getString( "GeneralSettingsPanel.OtherSettings" ) ) );

    GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 2;
    c.weightx = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.WEST;
    updatesPanel.add( showIndexColumns, c );

    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 1;
    c.gridwidth = 2;
    c.weightx = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.WEST;
    updatesPanel.add( experimentalFeatures, c );

    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 2;
    c.gridwidth = 2;
    c.weightx = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.WEST;
    updatesPanel.add( showDeprecatedItems, c );

    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 3;
    c.gridwidth = 2;
    c.weightx = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.WEST;
    updatesPanel.add( showExpertItems, c );

    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 5;
    c.gridwidth = 2;
    c.weightx = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.WEST;
    updatesPanel.add( showDebugItems, c );

    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 6;
    c.gridwidth = 2;
    c.weightx = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.WEST;
    updatesPanel.add( openLastReport, c );

    return updatesPanel;
  }


  private JPanel createDataFormatSettings() {
    final JPanel updatesPanel = new JPanel();
    updatesPanel.setLayout( new GridBagLayout() );
    updatesPanel.setBorder( BorderFactory.createTitledBorder
      ( SettingsMessages.getInstance().getString( "GeneralSettingsPanel.DataFormatSettings" ) ) );

    GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 0;
    c.gridwidth = 2;
    c.weightx = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.WEST;
    updatesPanel.add( experimentalFeatures, c );

    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 10;
    c.gridwidth = 1;
    c.weightx = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.WEST;
    updatesPanel
      .add( new JLabel( SettingsMessages.getInstance().getString( "GeneralSettingsPanel.DateFormatLabel" ) ), c );

    c = new GridBagConstraints();
    c.gridx = 1;
    c.gridy = 10;
    c.gridwidth = 1;
    c.weightx = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.WEST;
    updatesPanel.add( dateFormatField, c );

    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 11;
    c.gridwidth = 1;
    c.weightx = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.WEST;
    updatesPanel
      .add( new JLabel( SettingsMessages.getInstance().getString( "GeneralSettingsPanel.TimeFormatLabel" ) ), c );

    c = new GridBagConstraints();
    c.gridx = 1;
    c.gridy = 11;
    c.gridwidth = 1;
    c.weightx = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.WEST;
    updatesPanel.add( timeFormatField, c );

    c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 12;
    c.gridwidth = 1;
    c.weightx = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.WEST;
    updatesPanel
      .add( new JLabel( SettingsMessages.getInstance().getString( "GeneralSettingsPanel.DatetimeFormatLabel" ) ), c );

    c = new GridBagConstraints();
    c.gridx = 1;
    c.gridy = 12;
    c.gridwidth = 1;
    c.weightx = 1;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.WEST;
    updatesPanel.add( datetimeFormatField, c );

    c = new GridBagConstraints();
    c.gridx = 1;
    c.gridy = 13;
    c.gridwidth = 1;
    c.anchor = GridBagConstraints.WEST;
    updatesPanel.add( new JButton( new EditFormatAction() ), c );

    return updatesPanel;
  }


  public JComponent getComponent() {
    return this;
  }

  public Icon getIcon() {
    return IconLoader.getInstance().getGeneralSettingsIcon32();
  }

  public String getTitle() {
    return SettingsMessages.getInstance().getString( "SettingsDialog.General" );
  }

  public ValidationResult validate( final ValidationResult result ) {
    return result;
  }

  public void apply() {
    if ( experimentalFeatures.isSelected() ) {
      WorkspaceSettings.getInstance().setMaturityLevel( MaturityLevel.Community );
    } else {
      WorkspaceSettings.getInstance().setMaturityLevel( MaturityLevel.Production );
    }
    WorkspaceSettings.getInstance().setDateFormatPattern( dateFormatField.getText() );
    WorkspaceSettings.getInstance().setDatetimeFormatPattern( datetimeFormatField.getText() );
    WorkspaceSettings.getInstance().setTimeFormatPattern( timeFormatField.getText() );
    WorkspaceSettings.getInstance().setShowIndexColumns( showIndexColumns.isSelected() );
    WorkspaceSettings.getInstance().setShowDeprecatedItems( showDeprecatedItems.isSelected() );
    WorkspaceSettings.getInstance().setShowExpertItems( showExpertItems.isSelected() );
    WorkspaceSettings.getInstance().setDebugFeaturesVisible( showDebugItems.isSelected() );
    WorkspaceSettings.getInstance().setReopenLastReport( openLastReport.isSelected() );
    if ( lookAndFeelModel.getSelectedItem() != null ) {
      WorkspaceSettings.getInstance().setLNF( lookAndFeelModel.getSelectedItem().toString() );
    }

    dateFormatModel.setNumberFormats( dateFormats );
    numberFormatModel.setNumberFormats( numberFormats );
  }

  public void reset() {
    dateFormatField.setText( WorkspaceSettings.getInstance().getDateFormatPattern() );
    datetimeFormatField.setText( WorkspaceSettings.getInstance().getDatetimeFormatPattern() );
    timeFormatField.setText( WorkspaceSettings.getInstance().getTimeFormatPattern() );

    MaturityLevel maturityLevel = WorkspaceSettings.getInstance().getMaturityLevel();
    experimentalFeatures.setSelected( !MaturityLevel.Limited.isMature( maturityLevel ) );

    lookAndFeelModel.setSelectedItem( WorkspaceSettings.getInstance().getLNF() );
    showIndexColumns.setSelected( WorkspaceSettings.getInstance().isShowIndexColumns() );
    showDeprecatedItems.setSelected( WorkspaceSettings.getInstance().isShowDeprecatedItems() );
    showExpertItems.setSelected( WorkspaceSettings.getInstance().isShowExpertItems() );
    showDebugItems.setSelected( WorkspaceSettings.getInstance().isDebugFeaturesVisible() );
    openLastReport.setSelected( WorkspaceSettings.getInstance().isReopenLastReport() );

    dateFormats = dateFormatModel.getNumberFormats();
    numberFormats = numberFormatModel.getNumberFormats();
  }
}
