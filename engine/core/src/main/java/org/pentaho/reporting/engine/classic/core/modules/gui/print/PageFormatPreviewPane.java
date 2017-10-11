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

package org.pentaho.reporting.engine.classic.core.modules.gui.print;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;

import javax.swing.JPanel;

import org.pentaho.reporting.engine.classic.core.SimplePageDefinition;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.internal.PageBackgroundDrawable;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.DrawablePanel;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.PageDrawable;
import org.pentaho.reporting.engine.classic.core.util.PageFormatFactory;

/**
 * A component that renders a picture of an empty page, so that the page-setup-dialog can show the user an approximation
 * of the current input.
 *
 * @author Thomas Morgner.
 */
public class PageFormatPreviewPane extends JPanel {
  protected class SimplePageDrawable implements PageDrawable {
    public SimplePageDrawable() {
    }

    /**
     * Describes the physical output characteristics like page size, margins, and imaginable area.
     *
     * @return
     */
    public PageFormat getPageFormat() {
      if ( pageDefinition == null ) {
        return new PageFormat();
      }

      final PageFormat pageFormat = pageDefinition.getPageFormat();
      final Paper orgPaper = pageFormat.getPaper();
      final PageFormatFactory pff = PageFormatFactory.getInstance();

      final double virtualPaperWidth =
          orgPaper.getImageableWidth() + pff.getLeftBorder( orgPaper ) + pff.getRightBorder( orgPaper );
      final double virtualPaperHeight =
          orgPaper.getImageableHeight() + pff.getTopBorder( orgPaper ) + pff.getBottomBorder( orgPaper );

      final Paper p = pff.createPaper( virtualPaperWidth, virtualPaperHeight );
      pff.setBorders( p, pff.getTopBorder( orgPaper ), pff.getLeftBorder( orgPaper ), pff.getBottomBorder( orgPaper ),
          pff.getRightBorder( orgPaper ) );
      return pff.createPageFormat( p, pageFormat.getOrientation() );
    }

    public Dimension getPreferredSize() {
      if ( pageDefinition == null ) {
        return new Dimension();
      }

      final PageFormat pageFormat = getPageFormat();
      return new Dimension( (int) pageFormat.getWidth(), (int) pageFormat.getHeight() );
    }

    public void draw( final Graphics2D graphics, final Rectangle2D bounds ) {
      final PageFormat gpf = getPageFormat();

      final Rectangle2D.Double imageableArea =
          new Rectangle2D.Double( gpf.getImageableX(), gpf.getImageableY(), gpf.getImageableWidth(), gpf
              .getImageableHeight() );
      graphics.setPaint( new Color( 225, 225, 225 ) );
      graphics.fill( imageableArea );
      graphics.setPaint( Color.gray );
      graphics.draw( imageableArea );

      final int pcH = pageDefinition.getPageCountHorizontal();
      final int pcW = pageDefinition.getPageCountVertical();

      final Line2D line = new Line2D.Double();
      for ( int splitH = 1; splitH < pcH; splitH += 1 ) {
        final double xPos = gpf.getImageableX() + ( splitH * gpf.getImageableWidth() );
        line.setLine( xPos, gpf.getImageableY(), xPos, gpf.getImageableY() + gpf.getImageableHeight() );
        graphics.draw( line );
      }

      for ( int splitW = 1; splitW < pcW; splitW += 1 ) {
        final double yPos = gpf.getImageableY() + ( splitW * gpf.getImageableHeight() );
        line.setLine( gpf.getImageableX(), yPos, gpf.getImageableX() + gpf.getImageableWidth(), yPos );
        graphics.draw( line );
      }
    }

    public RenderNode[] getNodesAt( final double x, final double y, final String namespace, final String name ) {
      return EMPTY_NODES;
    }

    public RenderNode[] getNodesAt( final double x, final double y, final double width, final double height,
        final String namespace, final String name ) {
      return EMPTY_NODES;
    }
  }

  protected class AutoScalePageBackgroundDrawable extends PageBackgroundDrawable {
    public Dimension getPreferredSize() {
      return new Dimension( 0, 0 );
    }

    /**
     * Draws the object.
     *
     * @param g2
     *          the graphics device.
     * @param area
     *          the area inside which the object should be drawn.
     */
    public void draw( final Graphics2D g2, final Rectangle2D area ) {
      setZoom( 1 );
      final Dimension preferredSize = super.getPreferredSize();
      final double scaleX = area.getWidth() / preferredSize.getWidth();
      final double scaleY = area.getHeight() / preferredSize.getHeight();
      setZoom( Math.min( scaleX, scaleY ) );
      super.draw( g2, area );
    }
  }

  private SimplePageDefinition pageDefinition;
  private DrawablePanel drawablePanel;
  private static final RenderNode[] EMPTY_NODES = new RenderNode[0];
  private PageBackgroundDrawable pageBackgroundDrawable;

  /**
   * Creates a new <code>JPanel</code> with a double buffer and a flow layout.
   */
  public PageFormatPreviewPane() {
    drawablePanel = new DrawablePanel();

    pageBackgroundDrawable = new AutoScalePageBackgroundDrawable();
    pageBackgroundDrawable.setBackend( new SimplePageDrawable() );

    setLayout( new BorderLayout() );
    add( drawablePanel );
  }

  public SimplePageDefinition getPageDefinition() {
    return pageDefinition;
  }

  public void setPageDefinition( final SimplePageDefinition pageDefinition ) {
    this.pageDefinition = pageDefinition;
    if ( pageDefinition != null ) {
      drawablePanel.setDrawableAsRawObject( pageBackgroundDrawable );
    } else {
      drawablePanel.setDrawableAsRawObject( null );
    }
    repaint();
  }
}
