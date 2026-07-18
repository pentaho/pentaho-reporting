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



package org.pentaho.reporting.designer.core.actions.report.export;

import org.pentaho.reporting.engine.classic.core.modules.gui.print.PrintingPlugin;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class PrintReportAction extends AbstractExportAction {
  public PrintReportAction() {
    super( new PrintingPlugin() );
  }
}
