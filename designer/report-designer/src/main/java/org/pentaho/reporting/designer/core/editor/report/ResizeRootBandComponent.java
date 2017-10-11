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

package org.pentaho.reporting.designer.core.editor.report;

import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.editor.report.layouting.ElementRenderer;
import org.pentaho.reporting.designer.core.util.CanvasImageLoader;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

/**
 * User: Martin Date: 25.01.2006 Time: 20:55:17
 */
public class ResizeRootBandComponent extends JPanel {
  private boolean mousePressed;
  private boolean mouseInside;
  private Color hoverColor;
  private Color normalColor;
  private Border hoverBorder;
  private Border normalBorder;
  private ElementRenderer rootBandRenderer;
  private ReportDocumentContext renderContext;
  private boolean linealComponent;

  public ResizeRootBandComponent( final boolean linealComponent,
                                  final ElementRenderer renderer,
                                  final ReportDocumentContext renderContext ) {
    if ( renderer == null ) {
      throw new NullPointerException();
    }
    if ( renderContext == null ) {
      throw new NullPointerException();
    }
    this.linealComponent = linealComponent;
    this.renderContext = renderContext;
    this.rootBandRenderer = renderer;

    if ( linealComponent ) {
      normalColor = new Color( 212, 212, 212 );
      normalBorder = BorderFactory.createLineBorder( new Color( 188, 188, 188 ), 1 );
      hoverColor = new Color( 212, 212, 212 );
      hoverBorder = BorderFactory.createLineBorder( new Color( 128, 128, 128 ), 1 );
    } else {
      normalColor = new Color( 255, 255, 255, 0 );
      hoverColor = new Color( 255, 255, 255, 0 );
      hoverBorder = BorderFactory.createEmptyBorder( 1, 1, 1, 1 );
      normalBorder = BorderFactory.createEmptyBorder( 1, 1, 1, 1 );
    }

    setOpaque( false );
    setBorder( normalBorder );
    setMinimumSize( new Dimension( 6, 4 ) );
    setPreferredSize( new Dimension( 6, 4 ) );

    setBackground( normalColor );

    final MouseUpdateHandler handler = new MouseUpdateHandler();
    addMouseListener( handler );
    addMouseMotionListener( handler );
    setCursor( Cursor.getPredefinedCursor( Cursor.N_RESIZE_CURSOR ) );
  }

  protected void paintComponent( Graphics g ) {
    super.paintComponent( g );
    if ( !linealComponent ) {
      g.setColor( Color.WHITE );
      g.fillRect( getWidth() - 15, 0, getWidth(), getHeight() );

      ImageIcon leftBorder = CanvasImageLoader.getInstance().getLeftShadowImage();
      g.drawImage( leftBorder.getImage(), getWidth() - 23, 0, leftBorder.getIconWidth(), getHeight(), null );

      g.setColor( Color.DARK_GRAY );
      g.drawLine( 0, getHeight() / 2, getWidth(), getHeight() / 2 );
    }
  }

  protected void updateState( final boolean mousePressed, final boolean mouseInside ) {
    this.mousePressed = mousePressed;
    this.mouseInside = mouseInside;

    if ( mouseInside || this.mousePressed ) {
      setBorder( hoverBorder );
      setBackground( hoverColor );
    } else {
      setBorder( normalBorder );
      setBackground( normalColor );
    }
    repaint();
  }

  public boolean isMouseInside() {
    return mouseInside;
  }

  public boolean isMousePressed() {
    return mousePressed;
  }

  public ElementRenderer getRootBandRenderer() {
    return rootBandRenderer;
  }

  protected ReportDocumentContext getRenderContext() {
    return renderContext;
  }

  private class MouseUpdateHandler extends MouseAdapter implements MouseMotionListener {
    private int startPosition;

    public void mousePressed( final MouseEvent e ) {
      startPosition = e.getY();
      updateState( true, isMouseInside() );
    }

    public void mouseReleased( final MouseEvent e ) {
      startPosition = -1;
      updateState( false, isMouseInside() );
    }

    @Override
    public void mouseEntered( final MouseEvent e ) {
      updateState( isMousePressed(), true );
    }

    @Override
    public void mouseExited( final MouseEvent e ) {
      updateState( isMousePressed(), false );
    }

    public void mouseDragged( final MouseEvent e ) {
      final int diff = startPosition - e.getY();
      final ElementRenderer rootBandRenderer = getRootBandRenderer();

      final float zoomAsPercentage = getRenderContext().getZoomModel().getZoomAsPercentage();
      final double height = rootBandRenderer.getVisualHeight() - ( diff / zoomAsPercentage );
      if ( height < 0 ) {
        return;
      }
      rootBandRenderer.setVisualHeight( height );
    }

    public void mouseMoved( final MouseEvent e ) {
    }
  }
}
