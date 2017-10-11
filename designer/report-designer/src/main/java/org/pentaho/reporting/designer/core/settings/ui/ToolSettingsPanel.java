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

import org.pentaho.reporting.designer.core.settings.SettingsMessages;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class ToolSettingsPanel extends JPanel {
  private class SelectExecutableAction extends AbstractAction {
    private SelectExecutableAction() {
      putValue( Action.NAME, SettingsMessages.getInstance().getString( "ToolSettingsPanel.showFileChooserButton" ) );
    }

    public void actionPerformed( final ActionEvent e ) {
      performSelectFile();
    }
  }

  private class EnableTextFieldsHandler implements ChangeListener {
    private EnableTextFieldsHandler() {
    }

    public void stateChanged( final ChangeEvent e ) {
      updateState();
    }
  }

  private JRadioButton defaultBrowserRadioButton;
  private JRadioButton customExecutableRadioButton;

  private JButton showFileChooserButton;
  private JTextField customExecutableTextField;
  private JTextField customParametersTextField;

  private JLabel sampleCustomExecutableLabel;
  private JLabel customExecutableLabel;
  private JLabel customParametersLabel;

  protected ToolSettingsPanel() {
    setLayout( new GridBagLayout() );

    defaultBrowserRadioButton = new JRadioButton( getDefaultApplicationTranslation() );
    defaultBrowserRadioButton.setEnabled( true );
    defaultBrowserRadioButton.addChangeListener( new EnableTextFieldsHandler() );
    customExecutableRadioButton = new JRadioButton( getCustomApplicationTranslation() );
    customExecutableRadioButton.addChangeListener( new EnableTextFieldsHandler() );

    final ButtonGroup buttonGroup = new ButtonGroup();
    buttonGroup.add( defaultBrowserRadioButton );
    buttonGroup.add( customExecutableRadioButton );

    showFileChooserButton = new JButton( new SelectExecutableAction() );
    showFileChooserButton.setMargin( new Insets( 0, 0, 0, 0 ) );

    customExecutableTextField = new JTextField( null, 0 );
    customParametersTextField = new JTextField( "{0}", 0 );
    sampleCustomExecutableLabel =
      new JLabel( SettingsMessages.getInstance().getString( "ToolSettingsPanel.sampleCustomExecutableTextArea" ) );

    customExecutableLabel =
      new JLabel( SettingsMessages.getInstance().getString( "ToolSettingsPanel.customExecutableTextField" ) );
    customParametersLabel =
      new JLabel( SettingsMessages.getInstance().getString( "ToolSettingsPanel.customParametersTextField" ) );

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 3;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 4, 4, 4, 4 );
    add( defaultBrowserRadioButton, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.gridwidth = 3;
    gbc.insets = new Insets( 0, 4, 4, 4 );
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    add( customExecutableRadioButton, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 4, 4, 4, 4 );
    add( customExecutableLabel, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 2;
    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 4, 4, 4, 4 );
    add( customExecutableTextField, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 2;
    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 4, 4, 4, 4 );
    add( showFileChooserButton, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 0, 4, 4, 4 );
    add( customParametersLabel, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 3;
    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 0, 4, 4, 4 );
    add( customParametersTextField, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 4;
    gbc.gridwidth = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 0, 4, 4, 4 );
    add( sampleCustomExecutableLabel, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 5;
    gbc.gridheight = GridBagConstraints.REMAINDER;
    gbc.fill = GridBagConstraints.VERTICAL;
    gbc.weighty = 1;
    add( Box.createGlue(), gbc );

    updateState();
  }

  protected String getCustomApplicationTranslation() {
    return SettingsMessages.getInstance().getString( "ToolSettingsPanel.customExecutableRadioButton" );
  }

  protected String getDefaultApplicationTranslation() {
    return SettingsMessages.getInstance().getString( "ToolSettingsPanel.defaultBrowserRadioButton" );
  }

  protected void updateState() {
    final boolean enable = customExecutableRadioButton.isSelected();
    customExecutableLabel.setEnabled( enable );
    customParametersLabel.setEnabled( enable );
    customExecutableTextField.setEnabled( enable );
    customParametersTextField.setEnabled( enable );
    sampleCustomExecutableLabel.setEnabled( enable );
    showFileChooserButton.setEnabled( enable );
  }


  protected void performSelectFile() {
    final JFileChooser fileChooser = new JFileChooser();
    final int value = fileChooser.showOpenDialog( ToolSettingsPanel.this );
    if ( value == JFileChooser.APPROVE_OPTION ) {
      final File selectedFile = fileChooser.getSelectedFile();
      try {
        customExecutableTextField.setText( selectedFile.getCanonicalPath() );
      } catch ( IOException e1 ) {
        UncaughtExceptionsModel.getInstance().addException( e1 );
      }
    }
  }

  public boolean isUseDefaultApplication() {
    return defaultBrowserRadioButton.isSelected();
  }

  public void setUseDefaultApplication( final boolean useDefaultApplication ) {
    defaultBrowserRadioButton.setSelected( useDefaultApplication );
    customExecutableRadioButton.setSelected( !useDefaultApplication );
  }

  public String getCustomExecutable() {
    return customExecutableTextField.getText();
  }

  public void setCustomExecutable( final String customApplication ) {
    customExecutableTextField.setText( customApplication );
  }

  public String getCustomExecutableParameters() {
    return customParametersTextField.getText();
  }

  public void setCustomExecutableParameters( final String customApplication ) {
    customParametersTextField.setText( customApplication );
  }

  public ValidationResult validate( final ValidationResult result ) {
    if ( isUseDefaultApplication() ) {
      return result;
    }
    if ( getCustomExecutableParameters().contains( "{0}" ) == false ) {
      result.addValidationMessage( new ValidationMessage( ValidationMessage.Severity.ERROR,
        SettingsMessages.getInstance().getString( "ToolSettingsPanel.MissingVariable" ) ) );
    }
    return result;
  }
}
