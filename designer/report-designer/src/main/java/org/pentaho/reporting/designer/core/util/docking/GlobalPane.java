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

/**
 * The GlobalPane is a component that offers side-windows in addition to a desktop or content area. The pane offers four
 * area to add content. Content is provided via Category-objects, which carry the actual JComponent as well as some
 * metadata (icons, text and the minimized-state).
 *
 * @author Thomas Morgner.
 */
public class GlobalPane extends JComponent {
  public static enum Alignment {
    TOP( JSplitPane.HORIZONTAL_SPLIT ),
    BOTTOM( JSplitPane.HORIZONTAL_SPLIT ),
    LEFT( JSplitPane.VERTICAL_SPLIT ),
    RIGHT( JSplitPane.VERTICAL_SPLIT );

    private int direction;

    private Alignment( final int direction ) {
      this.direction = direction;
    }

    public int getDirection() {
      return direction;
    }
  }

  private SidePanel topPanel;
  private SidePanel leftPanel;
  private SidePanel bottomPanel;
  private SidePanel rightPanel;
  private JPanel contentPane;

  public GlobalPane( final boolean buttonsVisible ) {
    leftPanel = new SidePanel( Alignment.LEFT );
    leftPanel.setButtonsVisible( buttonsVisible );
    rightPanel = new SidePanel( Alignment.RIGHT );
    rightPanel.setButtonsVisible( buttonsVisible );
    topPanel = new SidePanel( Alignment.TOP );
    topPanel.setButtonsVisible( buttonsVisible );
    bottomPanel = new SidePanel( Alignment.BOTTOM );
    bottomPanel.setButtonsVisible( buttonsVisible );

    contentPane = new JPanel( new BorderLayout() );
    setLayout( new BorderLayout() );
    add( leftPanel, BorderLayout.WEST );
    add( rightPanel, BorderLayout.EAST );
    add( topPanel, BorderLayout.NORTH );
    add( bottomPanel, BorderLayout.SOUTH );
    add( contentPane, BorderLayout.CENTER );
  }

  public void setMainComponent( final Component component ) {
    contentPane.removeAll();
    if ( component != null ) {
      contentPane.add( component );
    }
  }

  public Component getMainComponent() {
    if ( contentPane.getComponentCount() == 0 ) {
      return null;
    }
    return contentPane.getComponent( 0 );
  }

  public void add( final Alignment position, final Category category ) {
    if ( position == null ) {
      throw new NullPointerException();
    }
    switch( position ) {
      case TOP:
        topPanel.add( category );
        break;
      case LEFT:
        leftPanel.add( category );
        break;
      case BOTTOM:
        bottomPanel.add( category );
        break;
      case RIGHT:
        rightPanel.add( category );
        break;
      default:
        throw new IllegalArgumentException();
    }
  }

  public boolean isButtonsVisible( final Alignment position ) {
    if ( position == null ) {
      throw new NullPointerException();
    }
    switch( position ) {
      case TOP:
        return topPanel.isButtonsVisible();
      case LEFT:
        return leftPanel.isButtonsVisible();
      case BOTTOM:
        return bottomPanel.isButtonsVisible();
      case RIGHT:
        return rightPanel.isButtonsVisible();
      default:
        throw new IllegalArgumentException();
    }
  }

  public void setButtonsVisible( final Alignment position, final boolean visible ) {
    if ( position == null ) {
      throw new NullPointerException();
    }
    switch( position ) {
      case TOP:
        topPanel.setButtonsVisible( visible );
        break;
      case LEFT:
        leftPanel.setButtonsVisible( visible );
        break;
      case BOTTOM:
        bottomPanel.setButtonsVisible( visible );
        break;
      case RIGHT:
        rightPanel.setButtonsVisible( visible );
        break;
      default:
        throw new IllegalArgumentException();
    }
  }

  public void setPreferredContentSize( final Alignment position, final Integer value ) {
    if ( position == null ) {
      throw new NullPointerException();
    }
    switch( position ) {
      case TOP:
        topPanel.setPreferredContentSize( value );
        break;
      case LEFT:
        leftPanel.setPreferredContentSize( value );
        break;
      case BOTTOM:
        bottomPanel.setPreferredContentSize( value );
        break;
      case RIGHT:
        rightPanel.setPreferredContentSize( value );
        break;
      default:
        throw new IllegalArgumentException();
    }
  }

  public Integer getPreferredContentSize( final Alignment position ) {
    if ( position == null ) {
      throw new NullPointerException();
    }
    switch( position ) {
      case TOP:
        return topPanel.getPreferredContentSize();
      case LEFT:
        return leftPanel.getPreferredContentSize();
      case BOTTOM:
        return bottomPanel.getPreferredContentSize();
      case RIGHT:
        return rightPanel.getPreferredContentSize();
      default:
        throw new IllegalArgumentException();
    }
  }
}
