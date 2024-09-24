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
 * Provides a high-level event system for receiving hyperlink events.
 *
 * @author Thomas Morgner
 */
public interface ReportHyperlinkListener extends EventListener {
  public void hyperlinkActivated( ReportHyperlinkEvent event );
}
