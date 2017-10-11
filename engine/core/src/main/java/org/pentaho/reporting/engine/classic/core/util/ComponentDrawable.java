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

package org.pentaho.reporting.engine.classic.core.util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Window;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A Drawable that renders a AWT-component. This only works if the AWT is not in headless mode.
 *
 * @author Thomas Morgner
 */
public class ComponentDrawable {
  private static final Log logger = LogFactory.getLog( ComponentDrawable.class );

  /**
   * A runnable that executes the drawing operation on the event-dispatcher thread.
   */
  private class PainterRunnable implements Runnable {
    /**
     * The draw-area as defined by the drawable.
     */
    private Rectangle2D area;
    /**
     * The graphics-context to which the runnable draws to.
     */
    private Graphics2D graphics;

    /**
     * Default constructor.
     */
    protected PainterRunnable() {
    }

    /**
     * Returns the Graphics2D to which the drawable should be rendered.
     *
     * @return the graphics.
     */
    public Graphics2D getGraphics() {
      return graphics;
    }

    /**
     * Defines the Graphics2D to which the drawable should be rendered.
     *
     * @param graphics
     *          the graphics.
     */
    public void setGraphics( final Graphics2D graphics ) {
      this.graphics = graphics;
    }

    /**
     * Returns the draw-area to which the drawable should be rendered.
     *
     * @return the graphics.
     */
    public Rectangle2D getArea() {
      return area;
    }

    /**
     * Defines the draw-area to which the drawable should be rendered.
     *
     * @param area
     *          the graphics.
     */
    public void setArea( final Rectangle2D area ) {
      this.area = area;
    }

    /**
     * Draws the drawable.
     */
    public void run() {
      try {
        final Component component = getComponent();
        if ( component instanceof Window ) {
          final Window w = (Window) component;
          w.validate();
        } else if ( isOwnPeerConnected() ) {
          final Window w = ComponentDrawable.getWindowAncestor( component );
          if ( w != null ) {
            w.validate();
          }
        } else {
          peerSupply.pack();
          contentPane.add( component );
        }

        component.setBounds( (int) area.getX(), (int) area.getY(), (int) area.getWidth(), (int) area.getHeight() );
        component.validate();
        component.paint( graphics );
      } finally {
        cleanUp();
      }
    }
  }

  /**
   * A runnable that queries the preferred size. As this may involve font-computations, this has to be done on the
   * EventDispatcher thread.
   */
  private class PreferredSizeRunnable implements Runnable {
    /**
     * The return value.
     */
    private Dimension retval;

    /**
     * Default Constructor.
     */
    protected PreferredSizeRunnable() {
    }

    /**
     * Returns the dimension that has been computed.
     *
     * @return the preferred size.
     */
    public Dimension getRetval() {
      return retval;
    }

    /**
     * Computes the preferred size on the EventDispatcherThread.
     */
    public void run() {
      retval = null;
      try {
        final Component component = getComponent();
        if ( component instanceof Window == false && isOwnPeerConnected() == false ) {
          peerSupply.pack();
          contentPane.add( component );
          contentPane.validate();
          component.validate();
        } else if ( isOwnPeerConnected() ) {
          retval = component.getSize();
          return;
        } else {
          component.validate();
        }
        retval = component.getPreferredSize();
      } finally {
        cleanUp();
      }
    }
  }

  /**
   * A runnable that queries the defined size. As this may involve font-computations, this has to be done on the
   * EventDispatcher thread.
   */
  private class DefinedSizeRunnable implements Runnable {
    /**
     * The return value.
     */
    private Dimension retval;

    /**
     * Default Constructor.
     */
    protected DefinedSizeRunnable() {
    }

    /**
     * Returns the dimension that has been computed.
     *
     * @return the declared size.
     */
    public Dimension getRetval() {
      return retval;
    }

    /**
     * Computes the declared size on the EventDispatcherThread.
     */
    public void run() {
      retval = null;
      try {
        final Component component = getComponent();
        if ( component instanceof Window == false && isOwnPeerConnected() == false ) {
          peerSupply.pack();
          contentPane.add( component );
        }
        retval = component.getSize();
      } finally {
        cleanUp();
      }
    }
  }

  /**
   * A flag indicating whether the aspect ratio should be preserved by the layouter.
   */
  private boolean preserveAspectRatio;
  /**
   * The component that should be drawn.
   */
  private Component component;
  /**
   * The Frame that connects the component to the native Window-System.
   */
  private JFrame peerSupply;
  /**
   * The content pane of the frame.
   */
  private JPanel contentPane;
  /**
   * The runnable that paints the component.
   */
  private PainterRunnable runnable;
  /**
   * The runnable that computes the preferred size.
   */
  private PreferredSizeRunnable preferredSizeRunnable;
  /**
   * The runnable that computes the declared size.
   */
  private DefinedSizeRunnable sizeRunnable;
  /**
   * A flag indicating whether all paint-operations should be performed on the Event-Dispatcher thread.
   */
  private boolean paintSynchronized;
  /**
   * A flag indicating whether components are allowed to provide their own AWT-Window.
   */
  private boolean allowOwnPeer;

  /**
   * Default Constructor.
   */
  public ComponentDrawable() {
    this( new JFrame() );
  }

  /**
   * Creates a new ComponentDrawable with the given Frame as peer-supply.
   *
   * @param peerSupply
   *          the frame that should be used as peer-source.
   */
  public ComponentDrawable( final JFrame peerSupply ) {
    if ( peerSupply == null ) {
      throw new NullPointerException();
    }
    this.peerSupply = peerSupply;
    this.contentPane = new JPanel();
    this.contentPane.setLayout( null );
    peerSupply.setContentPane( contentPane );
    this.runnable = new PainterRunnable();
    this.preferredSizeRunnable = new PreferredSizeRunnable();
    this.sizeRunnable = new DefinedSizeRunnable();
  }

  /**
   * Returns, whether components are allowed to provide their own AWT-Window.
   *
   * @return true, if foreign peers are allowed, false otherwise.
   */
  public boolean isAllowOwnPeer() {
    return allowOwnPeer;
  }

  /**
   * Defines, whether components are allowed to provide their own AWT-Window.
   *
   * @param allowOwnPeer
   *          true, if components can provide their own peers, false otherwise.
   */
  public void setAllowOwnPeer( final boolean allowOwnPeer ) {
    this.allowOwnPeer = allowOwnPeer;
  }

  /**
   * Returns, whether component operations will happen on the Event-Dispatcher threads. As the AWT is not synchronized,
   * weird things can happen if AWT functionality is executed on user threads.
   *
   * @return true, if all operations will be done on the AWT-EventDispatcher thread, false if they should be done in the
   *         local thread.
   */
  public boolean isPaintSynchronized() {
    return paintSynchronized;
  }

  /**
   * Defines, whether component operations will happen on the Event-Dispatcher threads. As the AWT is not synchronized,
   * weird things can happen if AWT functionality is executed on user threads.
   *
   * @param paintSynchronized
   *          true, if all operations will be done on the AWT-EventDispatcher thread, false if they should be done in
   *          the local thread.
   */
  public void setPaintSynchronized( final boolean paintSynchronized ) {
    this.paintSynchronized = paintSynchronized;
  }

  /**
   * A helper method that performs some cleanup and disconnects the component from the AWT and the Swing-Framework to
   * avoid memory-leaks.
   */
  protected final void cleanUp() {
    if ( component instanceof JComponent && isOwnPeerConnected() == false ) {
      final JComponent jc = (JComponent) component;
      RepaintManager.currentManager( jc ).removeInvalidComponent( jc );
      RepaintManager.currentManager( jc ).markCompletelyClean( jc );
    }
    contentPane.removeAll();
    RepaintManager.currentManager( contentPane ).removeInvalidComponent( contentPane );
    RepaintManager.currentManager( contentPane ).markCompletelyClean( contentPane );
    peerSupply.dispose();
  }

  /**
   * Returns the component that should be drawn.
   *
   * @return the component.
   */
  public Component getComponent() {
    return component;
  }

  /**
   * Defines the component that should be drawn.
   *
   * @param component
   *          the component.
   */
  public void setComponent( final Component component ) {
    this.component = component;
    prepareComponent( component );
  }

  /**
   * Returns the preferred size of the drawable. If the drawable is aspect ratio aware, these bounds should be used to
   * compute the preferred aspect ratio for this drawable.
   * <p/>
   * This calls {@link java.awt.Component#getPreferredSize()} on the given component.
   *
   * @return the preferred size.
   */
  public Dimension getPreferredSize() {
    if ( component == null ) {
      return new Dimension( 0, 0 );
    }
    if ( SwingUtilities.isEventDispatchThread() || paintSynchronized == false ) {
      preferredSizeRunnable.run();
      return preferredSizeRunnable.getRetval();
    }

    try {
      SwingUtilities.invokeAndWait( preferredSizeRunnable );
      return preferredSizeRunnable.getRetval();
    } catch ( Exception e ) {
      ComponentDrawable.logger.warn( "Failed to compute the preferred size." );
    }
    return new Dimension( 0, 0 );
  }

  /**
   * Returns the declared size of the drawable. If the drawable is aspect ratio aware, these bounds should be used to
   * compute the declared aspect ratio for this drawable.
   * <p/>
   * This calls {@link java.awt.Component#getSize()} on the given component.
   *
   * @return the preferred size.
   */
  public Dimension getSize() {
    if ( component == null ) {
      return new Dimension( 0, 0 );
    }
    if ( SwingUtilities.isEventDispatchThread() || paintSynchronized == false ) {
      sizeRunnable.run();
      return sizeRunnable.getRetval();
    }

    try {
      SwingUtilities.invokeAndWait( sizeRunnable );
      return sizeRunnable.getRetval();
    } catch ( Exception e ) {
      ComponentDrawable.logger.warn( "Failed to compute the defined size." );
    }
    return new Dimension( 0, 0 );
  }

  /**
   * A private helper method that checks, whether the component provides an own peer.
   *
   * @return true, if the component has an own peer, false otherwise.
   */
  protected final boolean isOwnPeerConnected() {
    if ( allowOwnPeer == false ) {
      return false;
    }
    final Window windowAncestor = ComponentDrawable.getWindowAncestor( component );
    return ( windowAncestor != null && windowAncestor != peerSupply );
  }

  /**
   * A private helper method that locates the Window to which the component is currently added.
   *
   * @param component
   *          the component for which the Window should be returned.
   * @return the AWT-Window that is the (possibly indirect) parent of this component.
   */
  protected static Window getWindowAncestor( final Component component ) {
    Component parent = component.getParent();
    while ( parent != null ) {
      if ( parent instanceof Window ) {
        return (Window) parent;
      }
      parent = parent.getParent();
    }
    return null;
  }

  /**
   * Defines whether the layouter should preserve the aspect ratio of the component's preferred size.
   *
   * @param preserveAspectRatio
   *          true, if the aspect ratio should be preserved, false otherwise.
   */
  public void setPreserveAspectRatio( final boolean preserveAspectRatio ) {
    this.preserveAspectRatio = preserveAspectRatio;
  }

  /**
   * Returns true, if this drawable will preserve an aspect ratio during the drawing.
   *
   * @return true, if an aspect ratio is preserved, false otherwise.
   */
  public boolean isPreserveAspectRatio() {
    return preserveAspectRatio;
  }

  /**
   * Draws the component.
   *
   * @param g2
   *          the graphics device.
   * @param area
   *          the area inside which the object should be drawn.
   */
  public void draw( final Graphics2D g2, final Rectangle2D area ) {
    if ( component == null ) {
      return;
    }

    runnable.setArea( area );
    runnable.setGraphics( g2 );

    if ( SwingUtilities.isEventDispatchThread() || paintSynchronized == false ) {
      runnable.run();
    } else {
      try {
        SwingUtilities.invokeAndWait( runnable );
      } catch ( Exception e ) {
        ComponentDrawable.logger.warn( "Failed to redraw the component." );
      }
    }
  }

  /**
   * Prepares the component for drawing. This recursively disables the double-buffering as this would interfere with the
   * drawing.
   *
   * @param c
   *          the component that should be prepared.
   */
  private void prepareComponent( final Component c ) {
    if ( c instanceof JComponent ) {
      final JComponent jc = (JComponent) c;
      jc.setDoubleBuffered( false );
      final Component[] childs = jc.getComponents();
      for ( int i = 0; i < childs.length; i++ ) {
        final Component child = childs[i];
        prepareComponent( child );
      }
    }
  }
}
