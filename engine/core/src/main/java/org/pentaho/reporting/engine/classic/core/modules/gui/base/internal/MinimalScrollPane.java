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

package org.pentaho.reporting.engine.classic.core.modules.gui.base.internal;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JScrollPane;

/**
 * Rewires the scrollpane's preferred size property to the minimum-size property to avoid a non-linear behavior when
 * used in the gridbag layout.
 *
 * @author Thomas Morgner
 */
public class MinimalScrollPane extends JScrollPane {
  /**
   * Creates a <code>JScrollPane</code> that displays the view component in a viewport whose view position can be
   * controlled with a pair of scrollbars. The scrollbar policies specify when the scrollbars are displayed, For
   * example, if <code>vsbPolicy</code> is <code>VERTICAL_SCROLLBAR_AS_NEEDED</code> then the vertical scrollbar only
   * appears if the view doesn't fit vertically. The available policy settings are listed at
   * {@link #setVerticalScrollBarPolicy} and {@link #setHorizontalScrollBarPolicy}.
   *
   * @param view
   *          the component to display in the scrollpanes viewport
   * @param vsbPolicy
   *          an integer that specifies the vertical scrollbar policy
   * @param hsbPolicy
   *          an integer that specifies the horizontal scrollbar policy
   * @see #setViewportView
   */
  public MinimalScrollPane( final Component view, final int vsbPolicy, final int hsbPolicy ) {
    super( view, vsbPolicy, hsbPolicy );
  }

  /**
   * Creates a <code>JScrollPane</code> that displays the contents of the specified component, where both horizontal and
   * vertical scrollbars appear whenever the component's contents are larger than the view.
   *
   * @param view
   *          the component to display in the scrollpane's viewport
   * @see #setViewportView
   */
  public MinimalScrollPane( final Component view ) {
    super( view );
  }

  /**
   * Creates an empty (no viewport view) <code>JScrollPane</code> with specified scrollbar policies. The available
   * policy settings are listed at {@link #setVerticalScrollBarPolicy} and {@link #setHorizontalScrollBarPolicy}.
   *
   * @param vsbPolicy
   *          an integer that specifies the vertical scrollbar policy
   * @param hsbPolicy
   *          an integer that specifies the horizontal scrollbar policy
   * @see #setViewportView
   */
  public MinimalScrollPane( final int vsbPolicy, final int hsbPolicy ) {
    super( vsbPolicy, hsbPolicy );
  }

  /**
   * Creates an empty (no viewport view) <code>JScrollPane</code> where both horizontal and vertical scrollbars appear
   * when needed.
   */
  public MinimalScrollPane() {
  }

  /**
   * If the <code>preferredSize</code> has been set to a non-<code>null</code> value just returns it. If the UI
   * delegate's <code>getPreferredSize</code> method returns a non <code>null</code> value then return that; otherwise
   * defer to the component's layout manager.
   *
   * @return the value of the <code>preferredSize</code> property
   * @see #setPreferredSize
   * @see javax.swing.plaf.ComponentUI
   */
  public Dimension getPreferredSize() {
    final Dimension preferredSize = super.getPreferredSize();
    if ( preferredSize == null ) {
      return null;
    }
    return super.getMinimumSize();
  }
}
