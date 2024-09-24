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

package org.pentaho.reporting.designer.extensions.legacycharts;

import org.pentaho.reporting.designer.core.editor.report.ReportElementInlineEditor;
import org.pentaho.reporting.designer.core.editor.report.elements.DefaultReportElementEditor;

public class LegacyChartReportElementEditor extends DefaultReportElementEditor {
  public LegacyChartReportElementEditor() {
  }

  public ReportElementInlineEditor createInlineEditor() {
    return new LegacyChartReportElementInlineEditor();
  }
}
