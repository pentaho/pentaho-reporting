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
