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

package org.pentaho.reporting.engine.classic.core.modules.gui.base.event;

import java.util.EventListener;

/**
 * A report specific mouse-listener. Implementations of this interface can receive wrapped-up mouse events.
 *
 * @author Thomas Morgner
 */
public interface ReportMouseListener extends EventListener {
  public void reportMouseClicked( ReportMouseEvent event );

  public void reportMousePressed( ReportMouseEvent event );

  public void reportMouseReleased( ReportMouseEvent event );

  public void reportMouseMoved( ReportMouseEvent event );

  public void reportMouseDragged( ReportMouseEvent event );
}
