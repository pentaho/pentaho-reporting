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


package org.pentaho.reporting.designer.core.editor.report.lineal;

import org.pentaho.reporting.designer.core.editor.report.ResizeRootBandComponent;
import org.pentaho.reporting.designer.core.editor.report.RootBandRenderingModel;
import org.pentaho.reporting.designer.core.editor.report.layouting.ElementRenderer;
import org.pentaho.reporting.designer.core.util.CanvasImageLoader;
import org.pentaho.reporting.engine.classic.core.PageDefinition;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.ArrayList;

/**
 * A container for the vertical lineals of all report-root-bands. This container manages the mapping between vertical
 * lineal components and report-root-bands.
 *
 * @author Thomas Morgner
 */
public class AllVerticalLinealsComponent extends JPanel {
  private class ReportUpdateHandler implements ChangeListener {
    private ReportUpdateHandler() {
    }

    public void stateChanged( final ChangeEvent e ) {
      refresh();
    }
  }

  private class BottomImageBorder extends JPanel {
    public BottomImageBorder() {
      setOpaque( false );
    }

    protected void paintComponent( final Graphics g ) {
      super.paintComponent( g );
      final ImageIcon leftCornerBorder = CanvasImageLoader.getInstance().getLeftCornerShadowImage();
      final ImageIcon bottomBorder = CanvasImageLoader.getInstance().getBottomShadowImage();
      g.drawImage( leftCornerBorder.getImage(), getWidth() - 23, 0, leftCornerBorder.getIconWidth(),
        leftCornerBorder.getIconHeight(), null );
      g.drawImage( bottomBorder.getImage(), getWidth() - 15, 0, getWidth(), bottomBorder.getIconHeight(), null );
    }
  }

  private ArrayList<VerticalLinealComponent> lineals;
  private JPanel innerCarrier;
  private RootBandRenderingModel renderContext;

  public AllVerticalLinealsComponent( final RootBandRenderingModel renderContext ) {
    if ( renderContext == null ) {
      throw new NullPointerException();
    }

    setLayout( new BorderLayout() );
    this.renderContext = renderContext;
    this.renderContext.addChangeListener( new ReportUpdateHandler() );
    this.lineals = new ArrayList();

    this.innerCarrier = new JPanel();
    this.innerCarrier.setBackground( new Color( 255, 255, 255, 0 ) );
    this.innerCarrier.setOpaque( false );
    this.innerCarrier.setLayout( new BoxLayout( innerCarrier, BoxLayout.Y_AXIS ) );

    add( innerCarrier, BorderLayout.NORTH );

    setOpaque( false );
    setBackground( new Color( 255, 255, 255, 0 ) );

    refresh();
  }

  public void refresh() {
    for ( int i = 0; i < lineals.size(); i++ ) {
      final VerticalLinealComponent component = lineals.get( i );
      component.setPageDefinition( null, null );
    }
    lineals.clear();
    innerCarrier.removeAll();

    final ElementRenderer[] allRenderers = renderContext.getAllRenderers();
    final PageDefinition definition = renderContext.getPageDefinition();
    for ( int i = 0; i < allRenderers.length; i++ ) {
      final ElementRenderer renderer = allRenderers[ i ];
      // final boolean showBorder = renderer.getElementType() instanceof PageHeaderType;
      final boolean showBorder = false;

      final VerticalLinealComponent c = new VerticalLinealComponent( showBorder, renderContext.getRenderContext() );
      c.setPageDefinition( definition, renderer );
      lineals.add( c );

      innerCarrier.add( c );
      innerCarrier.add( new ResizeRootBandComponent( false, renderer, renderContext.getRenderContext() ) );
    }
    innerCarrier.add( new BottomImageBorder() );

    innerCarrier.invalidate();
    innerCarrier.revalidate();
  }
}
