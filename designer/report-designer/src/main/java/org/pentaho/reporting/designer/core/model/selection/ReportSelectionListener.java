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


package org.pentaho.reporting.designer.core.model.selection;

import java.util.EventListener;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public interface ReportSelectionListener extends EventListener {
  public void selectionAdded( ReportSelectionEvent event );

  public void selectionRemoved( ReportSelectionEvent event );

  public void leadSelectionChanged( ReportSelectionEvent event );
}
