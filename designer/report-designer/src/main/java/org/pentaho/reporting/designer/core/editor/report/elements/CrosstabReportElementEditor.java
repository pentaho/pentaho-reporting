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


package org.pentaho.reporting.designer.core.editor.report.elements;

import org.pentaho.reporting.designer.core.editor.report.ReportElementDragHandler;
import org.pentaho.reporting.designer.core.editor.report.ReportElementInlineEditor;

/**
 * Crosstab element editor
 *
 * @author Sulaiman Karmali
 */
public class CrosstabReportElementEditor extends DefaultReportElementEditor {
  public CrosstabReportElementEditor() {
  }

  public ReportElementInlineEditor createInlineEditor() {
    return new SubreportReportElementInlineEditor();
  }

  public ReportElementDragHandler createDragHandler() {
    return new CrosstabReportElementDragHandler();
  }
}
