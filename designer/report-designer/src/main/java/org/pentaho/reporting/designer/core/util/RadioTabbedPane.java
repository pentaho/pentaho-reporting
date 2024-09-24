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

package org.pentaho.reporting.designer.core.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Todo: Document me!
 * <p/>
 *
 * @author Thomas Morgner.
 */
public class RadioTabbedPane extends JComponent {
  private class SelectTabAction implements ActionListener {
    private int cardName;

    private SelectTabAction( final int index ) {
      this.cardName = index;
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      setSelectedIndex( cardName );
    }
  }

  public static final String SELECTED_INDEX_PROPERTY = "selectedIndex";

  private ArrayList components;
  private ArrayList radioButtons;
  private JPanel contentPanel;
  private JPanel selectorPanel;
  private ButtonGroup buttonGroup;
  private CardLayout cardLayout;
  private int currentIndex;

  public RadioTabbedPane() {
    components = new ArrayList();
    radioButtons = new ArrayList();
    buttonGroup = new ButtonGroup();

    cardLayout = new CardLayout();
    contentPanel = new JPanel();
    contentPanel.setLayout( cardLayout );
    selectorPanel = new JPanel();
    selectorPanel.setLayout( new FlowLayout() );

    setLayout( new BorderLayout() );
    add( contentPanel, BorderLayout.CENTER );
    add( selectorPanel, BorderLayout.NORTH );
  }

  public void addTab( final String name, final Component tab ) {
    final String cardName = String.valueOf( components.size() );
    final JRadioButton radioButton = new JRadioButton( name );
    radioButton.addActionListener( new SelectTabAction( components.size() ) );

    components.add( tab );
    buttonGroup.add( radioButton );
    radioButtons.add( radioButton );

    selectorPanel.add( radioButton );
    contentPanel.add( cardName, tab );

    cardLayout.show( contentPanel, cardName );
    radioButton.setSelected( true );
  }

  public void setSelectedIndex( final int index ) {
    if ( index < 0 || index >= components.size() ) {
      throw new IndexOutOfBoundsException();
    }
    final JRadioButton o = (JRadioButton) radioButtons.get( index );
    o.setSelected( true );
    cardLayout.show( contentPanel, String.valueOf( index ) );
    final int oldIndex = this.currentIndex;
    currentIndex = index;
    firePropertyChange( SELECTED_INDEX_PROPERTY, oldIndex, index );
  }

  public int getSelectedIndex() {
    return currentIndex;
  }
}
