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

package org.pentaho.reporting.designer.core.actions.report.export;

import org.pentaho.reporting.engine.classic.core.modules.gui.pdf.PdfExportPlugin;

/**
 * Todo: Document Me
 *
 * @author Ezequiel Cuellar
 */
public final class ExportPdfAction extends AbstractExportAction {
  public ExportPdfAction() {
    super( new PdfExportPlugin() );
  }
}
