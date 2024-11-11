/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.designer.core.editor.format;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.libraries.designtime.swing.ColorComboBox;
import org.pentaho.reporting.libraries.designtime.swing.EllipsisButton;

import javax.swing.*;
import java.awt.*;

public class ColorPropertiesPane extends JPanel {
  private JComboBox colorSelectorBox;
  private JComboBox backgroundSelectorBox;
  private FontPreviewPane previewPane;

  public ColorPropertiesPane() {
    colorSelectorBox = new ColorComboBox();
    backgroundSelectorBox = new ColorComboBox();
    previewPane = new FontPreviewPane();

    setLayout( new GridBagLayout() );

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 5, 5, 0, 5 );
    add( new JLabel( Messages.getString( "ColorPropertiesPane.Foreground" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 5, 5, 0, 5 );
    add( colorSelectorBox, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 5, 5, 0, 5 );
    gbc.fill = GridBagConstraints.VERTICAL;
    add( new EllipsisButton( new SelectCustomColorAction( colorSelectorBox ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 5, 5, 0, 5 );
    add( new JLabel( Messages.getString( "ColorPropertiesPane.Background" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 5, 5, 0, 5 );
    add( backgroundSelectorBox, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 5, 5, 0, 5 );
    gbc.fill = GridBagConstraints.VERTICAL;
    add( new EllipsisButton( new SelectCustomColorAction( backgroundSelectorBox ) ), gbc );


    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 5;
    gbc.gridwidth = 3;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.insets = new Insets( 5, 5, 5, 5 );
    add( previewPane, gbc );

  }

  public void initializeFromStyle( final ElementStyleSheet styleSheet ) {
    final Object paint = styleSheet.getStyleProperty( ElementStyleKeys.PAINT );
    final Object background = styleSheet.getStyleProperty( ElementStyleKeys.BACKGROUND_COLOR );

    colorSelectorBox.setSelectedItem( paint );
    backgroundSelectorBox.setSelectedItem( background );
  }

  public void commitValues( final ElementStyleSheet styleSheet ) {
    styleSheet.setStyleProperty( ElementStyleKeys.PAINT, colorSelectorBox.getSelectedItem() );
    styleSheet.setStyleProperty( ElementStyleKeys.BACKGROUND_COLOR, backgroundSelectorBox.getSelectedItem() );
  }
}
