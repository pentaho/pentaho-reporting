/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.reporting.designer.core;

import org.pentaho.ui.xul.impl.XulEventHandler;

import java.util.Map;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public interface ReportDesignerUiPlugin {
  /**
   * Returny any extra handlers. Never return null.
   *
   * @return
   */
  public Map<String, String> getXulAdditionalHandlers();

  /**
   * Returns the overlay source file. Can be null, if no overlay is needed.
   *
   * @return
   */
  public String[] getOverlaySources();

  public XulEventHandler[] createEventHandlers();
}
