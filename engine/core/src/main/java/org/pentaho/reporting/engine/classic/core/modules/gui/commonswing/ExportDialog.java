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



package org.pentaho.reporting.engine.classic.core.modules.gui.commonswing;

import org.pentaho.reporting.engine.classic.core.MasterReport;

/**
 * Creation-Date: 15.05.2007, 16:42:51
 *
 * @author Thomas Morgner
 */
public interface ExportDialog {
  public boolean performQueryForExport( final MasterReport reportJob, final SwingGuiContext guiContext );
}
