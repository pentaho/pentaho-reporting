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

import org.pentaho.reporting.designer.core.editor.format.SelectCustomColorAction;
import org.pentaho.reporting.designer.core.settings.SettingsMessages;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.libraries.designtime.swing.ColorComboBox;
import org.pentaho.reporting.libraries.designtime.swing.EllipsisButton;

import javax.swing.*;
import java.awt.*;

public class ColorSettingsPanel extends JPanel implements SettingsPlugin {
  private JComboBox gridColorSelectorBox;
  private JComboBox guideColorSelectorBox;
  private JComboBox alignmentHintColorSelectorBox;
  private JComboBox overlapHintColorSelectorBox;

  public ColorSettingsPanel() {
    gridColorSelectorBox = new ColorComboBox();
    guideColorSelectorBox = new ColorComboBox();
    alignmentHintColorSelectorBox = new ColorComboBox();
    overlapHintColorSelectorBox = new ColorComboBox();

    setLayout( new GridBagLayout() );

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 5, 5, 0, 5 );
    add( new JLabel( SettingsMessages.getInstance().getString( "ColorSettingsPanel.GridColor" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 5, 5, 0, 5 );
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    add( gridColorSelectorBox, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 5, 5, 0, 5 );
    gbc.fill = GridBagConstraints.VERTICAL;
    add( new EllipsisButton( new SelectCustomColorAction( gridColorSelectorBox ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 5, 5, 0, 5 );
    add( new JLabel( SettingsMessages.getInstance().getString( "ColorSettingsPanel.GuideColor" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 5, 5, 0, 5 );
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    add( guideColorSelectorBox, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 5, 5, 0, 5 );
    gbc.fill = GridBagConstraints.VERTICAL;
    add( new EllipsisButton( new SelectCustomColorAction( guideColorSelectorBox ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 5, 5, 0, 5 );
    add( new JLabel( SettingsMessages.getInstance().getString( "ColorSettingsPanel.AlignmentHintColor" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 2;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 5, 5, 0, 5 );
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    add( alignmentHintColorSelectorBox, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 2;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 5, 5, 0, 5 );
    gbc.fill = GridBagConstraints.VERTICAL;
    add( new EllipsisButton( new SelectCustomColorAction( alignmentHintColorSelectorBox ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 5, 5, 0, 5 );
    add( new JLabel( SettingsMessages.getInstance().getString( "ColorSettingsPanel.OverlapHintColor" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 3;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 5, 5, 0, 5 );
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    add( overlapHintColorSelectorBox, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 3;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 5, 5, 0, 5 );
    gbc.fill = GridBagConstraints.VERTICAL;
    add( new EllipsisButton( new SelectCustomColorAction( overlapHintColorSelectorBox ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 4;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = 1;
    gbc.weighty = 1;
    add( new JPanel(), gbc );

    reset();
  }

  public JComponent getComponent() {
    return this;
  }

  public Icon getIcon() {
    return IconLoader.getInstance().getGeneralSettingsIcon32();
  }

  public String getTitle() {
    return SettingsMessages.getInstance().getString( "ColorSettingsPanel.Title" );
  }

  public void apply() {
    WorkspaceSettings.getInstance().setGridColor( (Color) gridColorSelectorBox.getSelectedItem() );
    WorkspaceSettings.getInstance().setGuideColor( (Color) guideColorSelectorBox.getSelectedItem() );
    WorkspaceSettings.getInstance().setAlignmentHintColor( (Color) alignmentHintColorSelectorBox.getSelectedItem() );
    WorkspaceSettings.getInstance().setOverlapErrorColor( (Color) overlapHintColorSelectorBox.getSelectedItem() );
  }

  public void reset() {
    gridColorSelectorBox.setSelectedItem( WorkspaceSettings.getInstance().getGridColor() );
    guideColorSelectorBox.setSelectedItem( WorkspaceSettings.getInstance().getGuideColor() );
    alignmentHintColorSelectorBox.setSelectedItem( WorkspaceSettings.getInstance().getAlignmentHintColor() );
    overlapHintColorSelectorBox.setSelectedItem( WorkspaceSettings.getInstance().getOverlapErrorColor() );
  }

  public ValidationResult validate( final ValidationResult result ) {
    return result;
  }
}
