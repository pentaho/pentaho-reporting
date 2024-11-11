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


package org.pentaho.reporting.designer.core.editor;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.text.DecimalFormat;

/**
 * Todo: Document me!
 *
 * @author Thomas Morgner
 */
public class ZoomController extends JLabel {
  private class ZoomHandler extends MouseAdapter implements
    MouseMotionListener, MouseWheelListener {
    private int dragStartPoint;

    public void mouseWheelMoved( final MouseWheelEvent e ) {
      // every click is 1%
      final float diff = e.getWheelRotation() * 0.01f;
      final float zoomFactor = zoomModel.getZoomAsPercentage();
      zoomModel.setZoomAsPercentage( Math.min( 5, Math.max( 0.1f, zoomFactor - diff ) ) );
    }

    public void mouseClicked( final MouseEvent e ) {
      if ( e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1 ) {
        zoomModel.setZoomAsPercentage( 1 );
      }
    }

    public void mousePressed( final MouseEvent e ) {
      dragStartPoint = e.getX();
    }

    public void mouseDragged( final MouseEvent e ) {
      // every point moved is 1% + or - to the zoom factor
      final float diff = ( dragStartPoint - e.getX() ) * 0.01f;
      final float zoomFactor = zoomModel.getZoomAsPercentage();
      zoomModel.setZoomAsPercentage( Math.min( 5, Math.max( 0.1f, zoomFactor - diff ) ) );
      dragStartPoint = e.getX();
    }

    public void mouseMoved( final MouseEvent e ) {

    }
  }

  private class ZoomUpdateHandler implements ZoomModelListener {
    public void zoomFactorChanged() {
      setText( format.format( zoomModel.getZoomAsPercentage() ) );
    }
  }

  private ZoomModel zoomModel;
  private DecimalFormat format;

  public ZoomController( final ZoomModel model ) {
    if ( model == null ) {
      throw new NullPointerException();
    }
    this.format = new DecimalFormat( "##0%" );
    this.zoomModel = model;
    this.zoomModel.addZoomModelListener( new ZoomUpdateHandler() );

    setBackground( SystemColor.control );
    setHorizontalAlignment( JLabel.CENTER );
    setText( format.format( zoomModel.getZoomAsPercentage() ) );
    final ZoomHandler zoomHandler = new ZoomHandler();
    addMouseListener( zoomHandler );
    addMouseMotionListener( zoomHandler );
    addMouseWheelListener( zoomHandler );
  }

  /**
   * If the minimum size has been set to a non-<code>null</code> value just returns it.  If the UI delegate's
   * <code>getMinimumSize</code> method returns a non-<code>null</code> value then return that; otherwise defer to the
   * component's layout manager.
   *
   * @return the value of the <code>minimumSize</code> property
   * @see #setMinimumSize
   * @see ComponentUI
   */
  public Dimension getMinimumSize() {
    return getPreferredSize();
  }
}
