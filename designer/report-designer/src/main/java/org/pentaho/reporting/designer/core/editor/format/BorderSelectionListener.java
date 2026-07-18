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



package org.pentaho.reporting.designer.core.editor.format;

import java.util.EventListener;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public interface BorderSelectionListener extends EventListener {
  public void selectionAdded( BorderSelectionEvent event );

  public void selectionRemoved( BorderSelectionEvent event );
}
