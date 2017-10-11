/*
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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.gui.base.internal;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.print.PageFormat;
import java.util.ArrayList;

import javax.swing.ToolTipManager;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.imagemap.ImageMap;
import org.pentaho.reporting.engine.classic.core.imagemap.ImageMapEntry;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableReplacedContentBox;
import org.pentaho.reporting.engine.classic.core.layout.output.RenderUtility;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.event.ReportActionEvent;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.event.ReportActionListener;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.event.ReportMouseEvent;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.event.ReportMouseListener;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.DrawablePanel;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.PageDrawable;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.resourceloader.factory.drawable.DrawableWrapper;
import org.pentaho.reporting.libraries.xmlns.LibXmlInfo;

public class PreviewDrawablePanel extends DrawablePanel {
  private class ReportMouseHandler implements MouseListener, MouseMotionListener {
    private ReportMouseHandler() {
    }

    /**
     * Invoked when the mouse button has been clicked (pressed and released) on a component.
     */
    public void mouseClicked( final MouseEvent e ) {
      fireReportMouseClicked( e );
    }

    /**
     * Invoked when a mouse button has been pressed on a component.
     */
    public void mousePressed( final MouseEvent e ) {
      fireReportMousePressed( e );
    }

    /**
     * Invoked when a mouse button has been released on a component.
     */
    public void mouseReleased( final MouseEvent e ) {
      fireReportMouseReleased( e );
    }

    /**
     * Invoked when the mouse enters a component.
     */
    public void mouseEntered( final MouseEvent e ) {
    }

    /**
     * Invoked when the mouse exits a component.
     */
    public void mouseExited( final MouseEvent e ) {
    }

    /**
     * Invoked when a mouse button is pressed on a component and then dragged. <code>MOUSE_DRAGGED</code> events will
     * continue to be delivered to the component where the drag originated until the mouse button is released
     * (regardless of whether the mouse position is within the bounds of the component).
     * <p/>
     * Due to platform-dependent Drag&Drop implementations, <code>MOUSE_DRAGGED</code> events may not be delivered
     * during a native Drag&Drop operation.
     */
    public void mouseDragged( final MouseEvent e ) {
      fireReportMouseDragged( e );
    }

    /**
     * Invoked when the mouse cursor has been moved onto a component but no buttons have been pushed.
     */
    public void mouseMoved( final MouseEvent e ) {
      fireReportMouseMoved( e );
    }
  }

  private class ReportActionHandler implements MouseListener {
    private ReportActionHandler() {
    }

    /**
     * Invoked when the mouse button has been clicked (pressed and released) on a component.
     */
    public void mouseClicked( final MouseEvent e ) {
      fireReportAction( e );
    }

    /**
     * Invoked when a mouse button has been pressed on a component.
     */
    public void mousePressed( final MouseEvent e ) {
    }

    /**
     * Invoked when a mouse button has been released on a component.
     */
    public void mouseReleased( final MouseEvent e ) {
    }

    /**
     * Invoked when the mouse enters a component.
     */
    public void mouseEntered( final MouseEvent e ) {
      // check for hyperlink and turn into a hand cursor
    }

    /**
     * Invoked when the mouse exits a component.
     */
    public void mouseExited( final MouseEvent e ) {
      // check for hyperlink and turn into a normal cursor
    }
  }

  private ArrayList reportMouseListener;
  private transient ReportMouseListener[] cachedReportMouseListeners;
  private ArrayList reportActionListener;
  private transient ReportActionListener[] cachedReportActionListeners;

  public PreviewDrawablePanel() {
    final ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
    toolTipManager.registerComponent( this );

    final ReportMouseHandler reportMouseHandler = new ReportMouseHandler();
    addMouseListener( reportMouseHandler );
    addMouseMotionListener( reportMouseHandler );
    addMouseListener( new ReportActionHandler() );
  }

  public void addReportMouseListener( final ReportMouseListener listener ) {
    if ( listener == null ) {
      throw new NullPointerException();
    }
    if ( reportMouseListener == null ) {
      reportMouseListener = new ArrayList();
    }
    reportMouseListener.add( listener );
    cachedReportMouseListeners = null;
  }

  public void removeReportMouseListener( final ReportMouseListener listener ) {
    if ( listener == null ) {
      throw new NullPointerException();
    }
    if ( reportMouseListener == null ) {
      return;
    }
    reportMouseListener.remove( listener );
    cachedReportMouseListeners = null;
  }

  public void addReportActionListener( final ReportActionListener listener ) {
    if ( listener == null ) {
      throw new NullPointerException();
    }
    if ( reportActionListener == null ) {
      reportActionListener = new ArrayList();
    }
    reportActionListener.add( listener );
    cachedReportActionListeners = null;
  }

  public void removeReportActionListener( final ReportActionListener listener ) {
    if ( listener == null ) {
      throw new NullPointerException();
    }
    if ( reportActionListener == null ) {
      return;
    }
    reportActionListener.remove( listener );
    cachedReportActionListeners = null;
  }

  protected void fireReportMouseClicked( final MouseEvent event ) {
    if ( reportMouseListener == null ) {
      return;
    }

    if ( cachedReportMouseListeners == null ) {
      cachedReportMouseListeners =
          (ReportMouseListener[]) reportMouseListener.toArray( new ReportMouseListener[reportMouseListener.size()] );
    }
    if ( cachedReportMouseListeners.length == 0 ) {
      return;
    }

    final RenderNode[] nodes = getNodesForScreenPoint( event.getX(), event.getY(), null, null );
    if ( nodes == null ) {
      return;
    }

    final ReportMouseListener[] currentListeners = cachedReportMouseListeners;
    for ( int n = 0; n < nodes.length; n++ ) {
      final RenderNode node = nodes[n];
      final ReportMouseEvent reportEvent = new ReportMouseEvent( node, event );

      for ( int i = 0; i < currentListeners.length; i++ ) {
        final ReportMouseListener listener = currentListeners[i];
        listener.reportMouseClicked( reportEvent );
      }
    }
  }

  protected void fireReportMouseMoved( final MouseEvent event ) {
    if ( reportMouseListener == null ) {
      return;
    }

    if ( cachedReportMouseListeners == null ) {
      cachedReportMouseListeners =
          (ReportMouseListener[]) reportMouseListener.toArray( new ReportMouseListener[reportMouseListener.size()] );
    }
    if ( cachedReportMouseListeners.length == 0 ) {
      return;
    }

    final RenderNode[] nodes = getNodesForScreenPoint( event.getX(), event.getY(), null, null );
    if ( nodes == null ) {
      return;
    }

    final ReportMouseListener[] currentListeners = cachedReportMouseListeners;
    for ( int n = 0; n < nodes.length; n++ ) {
      final RenderNode node = nodes[n];
      final ReportMouseEvent reportEvent = new ReportMouseEvent( node, event );

      for ( int i = 0; i < currentListeners.length; i++ ) {
        final ReportMouseListener listener = currentListeners[i];
        listener.reportMouseMoved( reportEvent );
      }
    }
  }

  protected void fireReportMouseDragged( final MouseEvent event ) {
    if ( reportMouseListener == null ) {
      return;
    }

    if ( cachedReportMouseListeners == null ) {
      cachedReportMouseListeners =
          (ReportMouseListener[]) reportMouseListener.toArray( new ReportMouseListener[reportMouseListener.size()] );
    }
    if ( cachedReportMouseListeners.length == 0 ) {
      return;
    }

    final RenderNode[] nodes = getNodesForScreenPoint( event.getX(), event.getY(), null, null );
    if ( nodes == null ) {
      return;
    }

    final ReportMouseListener[] currentListeners = cachedReportMouseListeners;
    for ( int n = 0; n < nodes.length; n++ ) {
      final RenderNode node = nodes[n];
      final ReportMouseEvent reportEvent = new ReportMouseEvent( node, event );

      for ( int i = 0; i < currentListeners.length; i++ ) {
        final ReportMouseListener listener = currentListeners[i];
        listener.reportMouseDragged( reportEvent );
      }
    }
  }

  protected void fireReportAction( final MouseEvent event ) {
    if ( reportActionListener == null ) {
      return;
    }

    if ( cachedReportActionListeners == null ) {
      cachedReportActionListeners =
          (ReportActionListener[]) reportActionListener.toArray( new ReportActionListener[reportActionListener.size()] );
    }
    if ( cachedReportMouseListeners.length == 0 ) {
      return;
    }

    final RenderNode[] nodes =
        getNodesForScreenPoint( event.getX(), event.getY(), AttributeNames.Swing.NAMESPACE, AttributeNames.Swing.ACTION );
    if ( nodes == null ) {
      return;
    }

    final ReportActionListener[] currentListeners = cachedReportActionListeners;
    for ( int n = 0; n < nodes.length; n++ ) {
      final RenderNode node = nodes[n];
      final ReportActionEvent reportEvent = new ReportActionEvent( this, node );

      for ( int i = 0; i < currentListeners.length; i++ ) {
        final ReportActionListener listener = currentListeners[i];
        listener.reportActionPerformed( reportEvent );
      }
    }
  }

  protected void fireReportMousePressed( final MouseEvent event ) {
    if ( reportMouseListener == null ) {
      return;
    }

    if ( cachedReportMouseListeners == null ) {
      cachedReportMouseListeners =
          (ReportMouseListener[]) reportMouseListener.toArray( new ReportMouseListener[reportMouseListener.size()] );
    }
    if ( cachedReportMouseListeners.length == 0 ) {
      return;
    }

    final RenderNode[] nodes = getNodesForScreenPoint( event.getX(), event.getY(), null, null );
    if ( nodes == null ) {
      return;
    }

    final ReportMouseListener[] currentListeners = cachedReportMouseListeners;
    for ( int n = 0; n < nodes.length; n++ ) {
      final RenderNode node = nodes[n];
      final ReportMouseEvent reportEvent = new ReportMouseEvent( node, event );

      for ( int i = 0; i < currentListeners.length; i++ ) {
        final ReportMouseListener listener = currentListeners[i];
        listener.reportMousePressed( reportEvent );
      }
    }
  }

  protected void fireReportMouseReleased( final MouseEvent event ) {
    if ( reportMouseListener == null ) {
      return;
    }

    final RenderNode[] nodes = getNodesForScreenPoint( event.getX(), event.getY(), null, null );
    if ( nodes == null ) {
      return;
    }

    if ( cachedReportMouseListeners == null ) {
      cachedReportMouseListeners =
          (ReportMouseListener[]) reportMouseListener.toArray( new ReportMouseListener[reportMouseListener.size()] );
    }

    final ReportMouseListener[] currentListeners = cachedReportMouseListeners;
    for ( int n = 0; n < nodes.length; n++ ) {
      final RenderNode node = nodes[n];
      final ReportMouseEvent reportEvent = new ReportMouseEvent( node, event );

      for ( int i = 0; i < currentListeners.length; i++ ) {
        final ReportMouseListener listener = currentListeners[i];
        listener.reportMouseReleased( reportEvent );
      }
    }
  }

  private RenderNode[]
    getNodesForScreenPoint( final int x, final int y, final String namespace, final String attribute ) {
    final PageBackgroundDrawable backgroundDrawable = getBackgroundDrawable();
    if ( backgroundDrawable == null ) {
      return null;
    }

    final PageDrawable pageDrawable = getPageDrawable();
    if ( pageDrawable == null ) {
      return null;
    }

    final double zoom = backgroundDrawable.getZoom();
    final RenderNode[] nodes = pageDrawable.getNodesAt( x / zoom, y / zoom, namespace, attribute );
    if ( nodes.length == 0 ) {
      return null;
    }
    return nodes;
  }

  public PageBackgroundDrawable getBackgroundDrawable() {
    final DrawableWrapper wrapper = getDrawable();
    if ( wrapper == null ) {
      return null;
    }
    final Object backend = wrapper.getBackend();
    if ( backend instanceof PageBackgroundDrawable == false ) {
      return null;
    }
    return (PageBackgroundDrawable) backend;
  }

  public PageDrawable getPageDrawable() {
    final PageBackgroundDrawable backgroundDrawable = getBackgroundDrawable();
    if ( backgroundDrawable == null ) {
      return null;
    }

    final PageDrawable physicalPageDrawable = backgroundDrawable.getBackend();
    if ( physicalPageDrawable == null ) {
      return null;
    }
    return physicalPageDrawable;
  }

  /**
   * Returns the string to be used as the tooltip for <i>event</i>. By default this returns any string set using
   * <code>setToolTipText</code>. If a component provides more extensive API to support differing tooltips at different
   * locations, this method should be overridden.
   */
  public String getToolTipText( final MouseEvent event ) {
    final PageBackgroundDrawable backgroundDrawable = getBackgroundDrawable();
    if ( backgroundDrawable == null ) {
      return null;
    }

    final PageDrawable physicalPageDrawable = getPageDrawable();
    if ( physicalPageDrawable == null ) {
      return null;
    }

    final float zoom = (float) backgroundDrawable.getZoom();
    final float x1 = event.getX() / zoom;
    final float y1 = event.getY() / zoom;
    final RenderNode[] nodes = physicalPageDrawable.getNodesAt( x1, y1, null, null );
    if ( nodes.length == 0 ) {
      return null;
    }

    for ( int i = nodes.length - 1; i >= 0; i -= 1 ) {
      final RenderNode node = nodes[i];
      final ReportAttributeMap attributes = node.getAttributes();
      final Object swingTooltip =
          attributes.getAttribute( AttributeNames.Swing.NAMESPACE, AttributeNames.Swing.TOOLTIP );
      if ( swingTooltip != null ) {
        return String.valueOf( swingTooltip );
      }

      final Object htmlTooltip = attributes.getAttribute( AttributeNames.Html.NAMESPACE, AttributeNames.Html.TITLE );
      if ( htmlTooltip != null ) {
        return String.valueOf( htmlTooltip );
      }

      final Object styleTooltip = node.getStyleSheet().getStyleProperty( ElementStyleKeys.HREF_TITLE );
      if ( styleTooltip != null ) {
        return String.valueOf( styleTooltip );
      }

      final Object hrefTarget = node.getStyleSheet().getStyleProperty( ElementStyleKeys.HREF_TARGET );
      if ( hrefTarget != null ) {
        return String.valueOf( hrefTarget );
      }

      if ( node instanceof RenderableReplacedContentBox == false ) {
        continue;
      }

      final ImageMap imageMap = RenderUtility.extractImageMap( (RenderableReplacedContentBox) node );
      if ( imageMap == null ) {
        continue;
      }

      final PageFormat pf = physicalPageDrawable.getPageFormat();
      final float imageMapX = (float) ( x1 - pf.getImageableX() - StrictGeomUtility.toExternalValue( node.getX() ) );
      final float imageMapY = (float) ( y1 - pf.getImageableY() - StrictGeomUtility.toExternalValue( node.getY() ) );

      final ImageMapEntry[] imageMapEntries = imageMap.getEntriesForPoint( imageMapX, imageMapY );
      for ( int j = 0; j < imageMapEntries.length; j++ ) {
        final ImageMapEntry imageMapEntry = imageMapEntries[j];
        final Object imageMapTooltip = imageMapEntry.getAttribute( LibXmlInfo.XHTML_NAMESPACE, "title" );
        if ( imageMapTooltip != null ) {
          return String.valueOf( imageMapTooltip );
        }

        final Object imageMapTarget = imageMapEntry.getAttribute( LibXmlInfo.XHTML_NAMESPACE, "href" );
        if ( imageMapTarget != null ) {
          return String.valueOf( imageMapTarget );
        }
      }
    }
    return null;
  }
}
