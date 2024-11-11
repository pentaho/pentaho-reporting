/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.designer.core.actions.report.export;

import org.pentaho.reporting.engine.classic.core.modules.gui.xls.XSSFExcelExportPlugin;

/**
 * Todo: Document Me
 *
 * @author Ezequiel Cuellar
 */
public final class ExportXlsxAction extends AbstractExportAction {
  public ExportXlsxAction() {
    super( new XSSFExcelExportPlugin() );
  }
}
