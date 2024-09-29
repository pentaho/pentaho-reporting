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


package org.pentaho.reporting.engine.classic.core.modules.gui.base;

import javax.swing.JComponent;
import javax.swing.JMenu;

/**
 * A report controller. This provides some means of configuring the preview components.
 * <p/>
 * The controller should use the propertyChange events provided by the PreviewProxyBase and the ReportPane to update its
 * state.
 * <p/>
 * To force a new repagination, use the <code>refresh</code> method of the PreviewProxyBase.
 *
 * @author Thomas Morgner
 */
public interface ReportController {
  /**
   * Returns the graphical representation of the controller. This component will be added between the menu bar and the
   * toolbar.
   * <p/>
   * Changes to this property are not detected automatically, you have to call "refreshController" whenever you want to
   * display a completly new control panel.
   *
   * @return the controller component.
   */
  public JComponent getControlPanel();

  /**
   * Returns the menus that should be inserted into the menubar.
   * <p/>
   * Changes to this property are not detected automatically, you have to call "refreshControler" whenever the contents
   * of the menu array changed.
   *
   * @return the menus as array, never null.
   */
  public JMenu[] getMenus();

  /**
   * Defines, whether the controller component is placed between the preview pane and the toolbar.
   *
   * @return true, if this is a inner component.
   */
  public boolean isInnerComponent();

  /**
   * Returns the location for the report controller, one of BorderLayout.NORTH, BorderLayout.SOUTH, BorderLayout.EAST or
   * BorderLayout.WEST.
   *
   * @return the location;
   */
  public String getControllerLocation();

  /**
   * Called to initialize the report controller and to connect it to the preview pane.
   *
   * @param pane
   */
  public void initialize( PreviewPane pane );

  /**
   * Called when the report controller gets removed.
   *
   * @param pane
   */
  public void deinitialize( PreviewPane pane );
}
