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

package org.pentaho.reporting.engine.classic.core.modules.gui.commonswing;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.FocusManager;

/**
 * Creation-Date: 30.11.2007, 12:58:54
 *
 * @author Thomas Morgner
 */
public class RequestFocusHandler extends ComponentAdapter {

  public RequestFocusHandler() {
  }

  public void componentShown( final ComponentEvent e ) {
    FocusManager.getCurrentManager().focusNextComponent( e.getComponent() );
  }
}
