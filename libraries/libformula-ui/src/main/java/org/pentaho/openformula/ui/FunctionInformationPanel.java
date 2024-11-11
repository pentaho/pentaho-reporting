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


package org.pentaho.openformula.ui;

import org.pentaho.reporting.libraries.formula.function.FunctionDescription;
import org.pentaho.reporting.libraries.formula.typing.TypeUtil;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

public class FunctionInformationPanel extends JPanel {
  private FunctionDescription selectedFunction;
  private JTextArea functionDescription;
  private JLabel functionReturnType;

  /**
   * Creates a new <code>JPanel</code> with a double buffer and a flow layout.
   */
  public FunctionInformationPanel() {
    final JLabel returnLabel = new JLabel( Messages.getInstance().getString( "FunctionInformationPanel.ReturnType" ) );
    final Font f = new Font( returnLabel.getFont().getName(), Font.BOLD, returnLabel.getFont().getSize() );
    returnLabel.setFont( f );

    final JLabel descLabel = new JLabel( Messages.getInstance().getString( "FunctionInformationPanel.Description" ) );
    descLabel.setFont( f );

    functionDescription = new JTextArea();
    functionDescription.setEditable( false );
    functionDescription.setLineWrap( true );
    functionDescription.setRows( 2 );
    functionDescription.setBackground( this.getBackground() );

    functionReturnType = new JLabel();
    functionReturnType.setFont( functionDescription.getFont() );

    setLayout( new GridBagLayout() );

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.insets = new Insets( 2, 2, 2, 2 );
    gbc.anchor = GridBagConstraints.NORTHWEST;
    add( returnLabel, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.weightx = 1;
    gbc.insets = new Insets( 2, 2, 2, 2 );
    gbc.fill = GridBagConstraints.HORIZONTAL;
    add( functionReturnType, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.insets = new Insets( 0, 2, 2, 2 );
    gbc.anchor = GridBagConstraints.NORTHWEST;
    add( descLabel, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.insets = new Insets( 0, 2, 2, 2 );
    gbc.fill = GridBagConstraints.BOTH;
    add( functionDescription, gbc );

    setPreferredSize( new Dimension( 460, 60 ) );
  }

  public FunctionDescription getSelectedFunction() {
    return selectedFunction;
  }

  public void setSelectedFunction( final FunctionDescription selectedFunction ) {
    this.selectedFunction = selectedFunction;
    if ( selectedFunction == null ) {
      this.functionDescription.setText( "" );
      this.functionReturnType.setText( "" );
    } else {
      this.functionDescription.setText( selectedFunction.getDescription( Locale.getDefault() ) );
      this.functionReturnType.setText( TypeUtil.getParameterType( selectedFunction.getValueType(), getLocale() ) );
    }
    repaint();
  }
}
