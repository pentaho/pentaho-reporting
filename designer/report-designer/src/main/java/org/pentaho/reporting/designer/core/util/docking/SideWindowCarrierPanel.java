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
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

/**
 * The SideWindowCarrierPanel combines the drag-area (to resize the component) with the actual content of the of the
 * SidePanel. The content is stored in a list and each update builds a hierarchy of splitpanes to display the content to
 * the user.
 *
 * @author Thomas Morgner.
 */
public class SideWindowCarrierPanel extends JComponent {
  private class DragHandler extends MouseAdapter implements MouseMotionListener {
    private int startX;
    private int startY;

    private DragHandler() {
    }

    public void mousePressed( final MouseEvent e ) {
      startX = e.getX();
      startY = e.getY();
    }

    public void mouseReleased( final MouseEvent e ) {
    }

    public void mouseDragged( final MouseEvent e ) {
      if ( alignment == GlobalPane.Alignment.LEFT ) {
        final int x = e.getX();
        final int xDiff = startX - x;
        final int w = Math.max( 0, getWidth() - xDiff );

        final Dimension s = contentPane.getSize();
        s.width = w;
        contentPane.setPreferredSize( s );
        contentPane.revalidate();
      } else if ( alignment == GlobalPane.Alignment.RIGHT ) {
        final int x = e.getX();
        final int xDiff = startX - x;
        final int w = Math.max( 0, getWidth() + xDiff );

        final Dimension s = contentPane.getSize();
        s.width = w;
        contentPane.setPreferredSize( s );
        contentPane.revalidate();
      } else if ( alignment == GlobalPane.Alignment.TOP ) {
        final int y = e.getY();
        final int yDiff = startY - y;
        final int h = Math.max( 0, getHeight() - yDiff );

        final Dimension s = contentPane.getSize();
        s.height = h;
        contentPane.setPreferredSize( s );
        contentPane.revalidate();
      } else if ( alignment == GlobalPane.Alignment.BOTTOM ) {
        final int y = e.getY();
        final int yDiff = startY - y;
        final int h = Math.max( 0, getHeight() + yDiff );

        final Dimension s = contentPane.getSize();
        s.height = h;
        contentPane.setPreferredSize( s );
        contentPane.revalidate();
      }
    }

    /**
     * Invoked when the mouse cursor has been moved onto a component but no buttons have been pushed.
     */
    public void mouseMoved( final MouseEvent e ) {

    }
  }

  private static class KillAllBordersBasicSplitPaneUI extends BasicSplitPaneUI {
    public BasicSplitPaneDivider createDefaultDivider() {
      return new BasicSplitPaneDivider( this ) {
        public void setBorder( final Border b ) {
          // ahh, lovely mac-osx would slap its borders everywhere. 
        }
      };
    }
  }

  private class CategoryVisibleUpdateHandler implements PropertyChangeListener {
    /**
     * This method gets called when a bound property is changed.
     *
     * @param evt A PropertyChangeEvent object describing the event source and the property that has changed.
     */
    public void propertyChange( final PropertyChangeEvent evt ) {
      revalidateComponent();
    }
  }

  private ArrayList<Category> childs;
  private JPanel dragPanel;
  private GlobalPane.Alignment alignment;
  private JComponent contentPane;
  private CategoryVisibleUpdateHandler visibleUpdateHandler;

  public SideWindowCarrierPanel( final GlobalPane.Alignment alignment ) {
    this.setVisible( false );
    this.visibleUpdateHandler = new CategoryVisibleUpdateHandler();
    this.alignment = alignment;
    this.childs = new ArrayList<Category>();
    this.contentPane = new JPanel();
    this.contentPane.setLayout( new BorderLayout() );

    dragPanel = new JPanel();
    dragPanel.setPreferredSize( new Dimension( 5, 5 ) );
    dragPanel.setSize( 5, 5 );
    dragPanel.setOpaque( true );
    dragPanel.setBorder( BorderFactory.createEmptyBorder( 0, 0, 1, 0 ) );

    final DragHandler dragHandler = new DragHandler();
    dragPanel.addMouseListener( dragHandler );
    dragPanel.addMouseMotionListener( dragHandler );

    setLayout( new BorderLayout() );
    add( contentPane, BorderLayout.CENTER );

    switch( alignment ) {
      case TOP:
        add( dragPanel, BorderLayout.SOUTH );
        dragPanel.setCursor( Cursor.getPredefinedCursor( Cursor.N_RESIZE_CURSOR ) );
        break;
      case BOTTOM:
        add( dragPanel, BorderLayout.NORTH );
        dragPanel.setCursor( Cursor.getPredefinedCursor( Cursor.S_RESIZE_CURSOR ) );
        break;
      case LEFT:
        add( dragPanel, BorderLayout.EAST );
        dragPanel.setCursor( Cursor.getPredefinedCursor( Cursor.W_RESIZE_CURSOR ) );
        break;
      case RIGHT:
        add( dragPanel, BorderLayout.WEST );
        dragPanel.setCursor( Cursor.getPredefinedCursor( Cursor.E_RESIZE_CURSOR ) );
        break;
    }
  }

  public void addWindow( final Category toolWindow ) {
    if ( childs.contains( toolWindow ) ) {
      return;
    }

    childs.add( toolWindow );
    toolWindow.addPropertyChangeListener( Category.MINIMIZED_PROPERTY, visibleUpdateHandler );
    revalidateComponent();
  }

  public void removeWindow( final Category toolWindow ) {
    childs.remove( toolWindow );
    toolWindow.removePropertyChangeListener( Category.MINIMIZED_PROPERTY, visibleUpdateHandler );
    revalidateComponent();
  }

  public Category getWindow( final int index ) {
    return childs.get( index );
  }

  public int getWindowCount() {
    return childs.size();
  }

  public void setToolWindowHeight( final int height ) {
    for ( int i = 0; i < childs.size(); i++ ) {
      final Category category = childs.get( i );
      final JComponent toolWindow = category.getMainComponent();
      final Dimension size = toolWindow.getSize();
      size.height = height - dragPanel.getHeight();
      toolWindow.setSize( size );
    }
  }

  public void setToolWindowWidth( final int width ) {
    for ( int i = 0; i < childs.size(); i++ ) {
      final Category category = childs.get( i );
      final JComponent toolWindow = category.getMainComponent();
      final Dimension size = toolWindow.getSize();
      size.width = width - dragPanel.getWidth();
      toolWindow.setSize( size );
    }
  }

  public JComponent getDragPanel() {
    return dragPanel;
  }

  public void revalidateComponent() {
    try {
      recomputeVisibleFlag();
      contentPane.removeAll();
      if ( childs.isEmpty() ) {
        return;
      }

      final ArrayList<JComponent> tempList = new ArrayList<JComponent>( childs.size() );
      for ( int i = 0; i < childs.size(); i++ ) {
        final Category category = childs.get( i );
        if ( category.isMinimized() == false ) {
          tempList.add( category.getMainComponent() );
        }
      }

      if ( tempList.isEmpty() ) {
        // noinspection UnnecessaryReturnStatement
        return;// just to be more clear
      }


      if ( tempList.size() == 1 ) {
        final JComponent mainComponent = tempList.get( 0 );
        contentPane.add( mainComponent, BorderLayout.CENTER );
        return;
      }

      final JComponent firstComponent = tempList.get( 0 );

      JSplitPane splitPane = new JSplitPane( alignment.getDirection(), true );
      splitPane.setUI( new KillAllBordersBasicSplitPaneUI() );
      splitPane.setBorder( new EmptyBorder( 0, 0, 0, 0 ) );
      splitPane.setTopComponent( firstComponent );
      splitPane.setDividerLocation( 0.5 );
      splitPane.setResizeWeight( 0.5 );
      splitPane.setContinuousLayout( true );
      contentPane.add( splitPane, BorderLayout.CENTER );

      for ( int i = 1; i < tempList.size(); i++ ) {
        final JComponent toolWindow = tempList.get( i );
        if ( i == tempList.size() - 1 ) {
          splitPane.setBottomComponent( toolWindow );
          splitPane.setDividerLocation( 0.5 );
          splitPane.setResizeWeight( 0.5 );
        } else {
          final JSplitPane childSplitPane = new JSplitPane( alignment.getDirection(), true );
          childSplitPane.setUI( new KillAllBordersBasicSplitPaneUI() );
          childSplitPane.setBorder( new EmptyBorder( 0, 0, 0, 0 ) );
          childSplitPane.setTopComponent( toolWindow );
          childSplitPane.setDividerLocation( 0.5 );
          childSplitPane.setResizeWeight( 0.5 );

          splitPane.setBottomComponent( childSplitPane );
          splitPane.setDividerLocation( 0.5 );
          splitPane.setResizeWeight( 0.5 );
          splitPane = childSplitPane;
        }
      }
    } finally {
      revalidate();
      invalidate();
      repaint();
    }
  }

  private void recomputeVisibleFlag() {
    for ( int i = 0; i < childs.size(); i++ ) {
      final Category category = childs.get( i );
      if ( category.isMinimized() == false ) {
        setVisible( true );
        return;
      }
    }
    setVisible( false );
  }

  public void setPreferredContentSize( final Integer value ) {
    if ( value == null ) {
      contentPane.setPreferredSize( null );
      contentPane.revalidate();
      return;
    }

    final Dimension dimension = contentPane.getSize();
    if ( alignment == GlobalPane.Alignment.LEFT || alignment == GlobalPane.Alignment.RIGHT ) {
      dimension.width = value;
    } else if ( alignment == GlobalPane.Alignment.TOP || alignment == GlobalPane.Alignment.BOTTOM ) {
      dimension.height = value;
    }
    contentPane.setPreferredSize( dimension );
    contentPane.revalidate();
  }

  public Integer getPreferredContentSize() {
    final Dimension dimension = contentPane.getPreferredSize();
    if ( dimension == null ) {
      return null;
    }
    if ( alignment == GlobalPane.Alignment.LEFT || alignment == GlobalPane.Alignment.RIGHT ) {
      return dimension.width;
    } else if ( alignment == GlobalPane.Alignment.TOP || alignment == GlobalPane.Alignment.BOTTOM ) {
      return dimension.height;
    }
    return null;
  }
}
