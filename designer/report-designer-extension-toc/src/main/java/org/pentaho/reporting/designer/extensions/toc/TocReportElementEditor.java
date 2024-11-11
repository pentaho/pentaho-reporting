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


package org.pentaho.reporting.designer.extensions.toc;

import org.pentaho.reporting.designer.core.editor.report.ReportElementDragHandler;
import org.pentaho.reporting.designer.core.editor.report.ReportElementInlineEditor;
import org.pentaho.reporting.designer.core.editor.report.elements.DefaultReportElementEditor;

public class TocReportElementEditor extends DefaultReportElementEditor {
  public TocReportElementEditor() {
  }

  public ReportElementInlineEditor createInlineEditor() {
    return new TocReportElementInlineEditor();
  }

  public ReportElementDragHandler createDragHandler() {
    return new TocReportElementDragHandler();
  }
}

