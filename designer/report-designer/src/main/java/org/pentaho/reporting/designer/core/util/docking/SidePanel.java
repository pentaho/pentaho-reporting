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

package org.pentaho.reporting.designer.core.util.docking;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * The SidePanel holds buttons and content windows for the global pane. Content is defined via a "Category" object and
 * for each category added, a button will be created and the content itself will be added to the SideWindowCarrierPanel.
 * The alignment controls where both the drag-area and the button-area are located.
 *
 * @author Thomas Morgner.
 */
public class SidePanel extends JComponent {
  private SideWindowCarrierPanel windowContent;
  private JComponent buttons;
  private GlobalPane.Alignment alignment;

  public SidePanel( final GlobalPane.Alignment alignment ) {
    this.alignment = alignment;
    windowContent = new SideWindowCarrierPanel( alignment );

    buttons = new JPanel();

    setLayout( new BorderLayout() );
    add( windowContent, BorderLayout.CENTER );

    switch( alignment ) {
      case TOP:
        buttons.setLayout( new BoxLayout( buttons, BoxLayout.X_AXIS ) );
        add( buttons, BorderLayout.NORTH );
        break;
      case BOTTOM:
        buttons.setLayout( new BoxLayout( buttons, BoxLayout.X_AXIS ) );
        add( buttons, BorderLayout.SOUTH );
        break;
      case LEFT:
        buttons.setLayout( new BoxLayout( buttons, BoxLayout.Y_AXIS ) );
        add( buttons, BorderLayout.WEST );
        break;
      case RIGHT:
        buttons.setLayout( new BoxLayout( buttons, BoxLayout.Y_AXIS ) );
        add( buttons, BorderLayout.EAST );
        break;
    }
  }

  private class ToggleVisibleAction implements ActionListener {
    private Category component;
    private ImageToggleButton button;

    private ToggleVisibleAction( final ImageToggleButton button, final Category component ) {
      this.button = button;
      this.component = component;
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      component.setMinimized( !component.isMinimized() );
      button.setSelected( component.isMinimized() == false );
    }
  }

  public void add( final Category category ) {
    final String title = category.getTitle();
    final ImageIcon icon = category.getIconBig();
    final JComponent component = category.getMainComponent();

    final ImageToggleButton button = new ImageToggleButton( icon, title, alignment );
    button.setSelected( component.isVisible() );
    button.addActionListener( new ToggleVisibleAction( button, category ) );
    buttons.add( button );

    windowContent.addWindow( category );
  }

  public void setPreferredContentSize( final Integer value ) {
    windowContent.setPreferredContentSize( value );
  }

  public Integer getPreferredContentSize() {
    return windowContent.getPreferredContentSize();
  }

  public boolean isButtonsVisible() {
    return buttons.isVisible();
  }

  public void setButtonsVisible( final boolean visible ) {
    buttons.setVisible( visible );
    revalidate();
  }
}
