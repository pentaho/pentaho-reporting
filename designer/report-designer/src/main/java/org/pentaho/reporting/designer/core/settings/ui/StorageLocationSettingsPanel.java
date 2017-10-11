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
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.designtime.swing.filechooser.FileChooserService;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * User: Martin Date: 01.03.2006 Time: 18:15:58
 */
public class StorageLocationSettingsPanel extends JPanel implements SettingsPlugin {
  private static class LocationPanel extends JPanel {
    private class BrowseAction extends AbstractAction {
      private JFileChooser fileChooser;

      /**
       * Defines an <code>Action</code> object with a default description string and default icon.
       */
      private BrowseAction() {
        putValue( Action.NAME, ".." );
      }

      /**
       * Invoked when an action occurs.
       */
      public void actionPerformed( final ActionEvent e ) {

        if ( fileChooser == null ) {
          fileChooser = new JFileChooser();
          fileChooser.setAcceptAllFileFilterUsed( false );
          fileChooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
        }
        if ( fileChooser.showOpenDialog( LocationPanel.this ) != JFileChooser.APPROVE_OPTION ) {
          return;
        }
        final File selectedFile = fileChooser.getSelectedFile();
        if ( selectedFile != null ) {
          if ( selectedFile.isDirectory() == false ) {
            predefinedLocationField.setText( selectedFile.getParentFile().getAbsolutePath() );
          } else {
            predefinedLocationField.setText( selectedFile.getAbsolutePath() );
          }
        }
      }
    }

    private class PredefinedLocationSelectionHandler implements ChangeListener {
      private PredefinedLocationSelectionHandler() {
      }

      public void stateChanged( final ChangeEvent e ) {
        predefinedLocationField.setEnabled( predefinedLocationButton.isSelected() );
      }
    }


    private JRadioButton noStoreButton;
    private JRadioButton lastLocationButton;
    private JRadioButton predefinedLocationButton;
    private JTextField predefinedLocationField;
    private String locationType;

    /**
     * Creates a new <code>JPanel</code> with a double buffer and a flow layout.
     *
     * @param locationType
     */
    private LocationPanel( final String locationType ) {
      this.locationType = locationType;

      noStoreButton = new JRadioButton();
      lastLocationButton = new JRadioButton();
      predefinedLocationButton = new JRadioButton();
      predefinedLocationButton.addChangeListener( new PredefinedLocationSelectionHandler() );
      predefinedLocationField = new JTextField();
      predefinedLocationField.setEnabled( false );

      final ButtonGroup bg = new ButtonGroup();
      bg.add( noStoreButton );
      bg.add( lastLocationButton );
      bg.add( predefinedLocationButton );
      final JButton browseButton = new JButton( new BrowseAction() );

      final JLabel noStoreLabel = new JLabel( "Do not remember anything." );
      final JLabel lastLocationLabel = new JLabel( "Remember my last location." );
      final JLabel predefinedLocationLabel = new JLabel( "Always use this directory." );

      setLayout( new GridBagLayout() );

      GridBagConstraints gbc = new GridBagConstraints();
      gbc.gridx = 0;
      gbc.gridy = 0;
      add( noStoreButton, gbc );

      gbc = new GridBagConstraints();
      gbc.gridx = 1;
      gbc.gridy = 0;
      gbc.gridwidth = 2;
      gbc.weightx = 1;
      gbc.fill = GridBagConstraints.HORIZONTAL;
      add( noStoreLabel, gbc );

      gbc = new GridBagConstraints();
      gbc.gridx = 0;
      gbc.gridy = 1;
      add( lastLocationButton, gbc );

      gbc = new GridBagConstraints();
      gbc.gridx = 1;
      gbc.gridy = 1;
      gbc.gridwidth = 2;
      gbc.weightx = 1;
      gbc.fill = GridBagConstraints.HORIZONTAL;
      add( lastLocationLabel, gbc );

      gbc = new GridBagConstraints();
      gbc.gridx = 0;
      gbc.gridy = 2;
      add( predefinedLocationButton, gbc );

      gbc = new GridBagConstraints();
      gbc.gridx = 1;
      gbc.gridy = 2;
      gbc.gridwidth = 2;
      gbc.weightx = 1;
      gbc.fill = GridBagConstraints.HORIZONTAL;
      add( predefinedLocationLabel, gbc );

      gbc = new GridBagConstraints();
      gbc.gridx = 1;
      gbc.gridy = 3;
      gbc.weightx = 1;
      gbc.fill = GridBagConstraints.HORIZONTAL;
      add( predefinedLocationField, gbc );

      gbc = new GridBagConstraints();
      gbc.gridx = 2;
      gbc.gridy = 3;
      add( browseButton, gbc );
    }

    public void apply() {
      if ( noStoreButton.isSelected() ) {
        FileChooserService.getInstance().setStoreLocations( locationType, false );
        FileChooserService.getInstance().setStaticLocation( locationType, null );
      } else if ( lastLocationButton.isSelected() ) {
        FileChooserService.getInstance().setStoreLocations( locationType, true );
        FileChooserService.getInstance().setStaticLocation( locationType, null );
      } else {
        FileChooserService.getInstance().setStoreLocations( locationType, false );
        if ( StringUtils.isEmpty( predefinedLocationField.getText(), true ) ) {
          FileChooserService.getInstance().setStaticLocation( locationType, null );
        } else {
          FileChooserService.getInstance()
            .setStaticLocation( locationType, new File( predefinedLocationField.getText() ) );
        }
      }
    }

    public void reset() {
      final File staticLocation = FileChooserService.getInstance().getStaticLocation( locationType );
      final boolean storePolicy = FileChooserService.getInstance().isStoreLocations( locationType );

      if ( storePolicy == true ) {
        lastLocationButton.setSelected( true );
        predefinedLocationField.setText( null );
      } else {
        if ( staticLocation == null ) {
          noStoreButton.setSelected( true );
          predefinedLocationField.setText( null );
        } else {
          predefinedLocationButton.setSelected( true );
          predefinedLocationField.setText( staticLocation.getAbsolutePath() );
        }
      }
    }
  }

  private LocationPanel[] locationPanels;
  private String[] locationTypes;

  public StorageLocationSettingsPanel() {
    setLayout( new BorderLayout() );

    locationTypes = new String[] {
      "report", "resources", "mondrian", "kettle", "xmifile", "xls" // NON-NLS
    };

    locationPanels = new LocationPanel[ locationTypes.length ];

    final JPanel contentPanel = new JPanel();
    contentPanel.setLayout( new GridBagLayout() );
    for ( int i = 0; i < locationTypes.length; i++ ) {
      final String locationType = locationTypes[ i ];
      locationPanels[ i ] = new LocationPanel( locationType );

      GridBagConstraints c = new GridBagConstraints();
      c.gridx = 0;
      c.gridy = i;
      c.weightx = 0;
      c.anchor = GridBagConstraints.NORTHWEST;
      contentPanel.add( new JLabel
        ( SettingsMessages.getInstance().getString( "StorageLocationSettingsPanel." + locationType ) ), c );//NON-NLS

      c = new GridBagConstraints();
      c.gridx = 1;
      c.gridy = i;
      c.weightx = 1;
      c.fill = GridBagConstraints.HORIZONTAL;
      contentPanel.add( locationPanels[ i ], c );

    }
    add( contentPanel, BorderLayout.NORTH );

    reset();
  }

  public JComponent getComponent() {
    return this;
  }

  public Icon getIcon() {
    return IconLoader.getInstance().getGeneralSettingsIcon32();
  }

  public String getTitle() {
    return SettingsMessages.getInstance().getString( "SettingsDialog.Locations" );
  }

  public ValidationResult validate( final ValidationResult result ) {
    return result;
  }

  public void apply() {
    for ( int i = 0; i < locationPanels.length; i++ ) {
      final LocationPanel locationPanel = locationPanels[ i ];
      locationPanel.apply();
    }
  }

  public void reset() {
    for ( int i = 0; i < locationPanels.length; i++ ) {
      final LocationPanel locationPanel = locationPanels[ i ];
      locationPanel.reset();
    }
  }
}
