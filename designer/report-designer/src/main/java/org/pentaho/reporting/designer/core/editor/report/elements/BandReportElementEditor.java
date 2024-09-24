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

package org.pentaho.reporting.designer.core.editor.report.elements;

import org.pentaho.reporting.designer.core.editor.report.ReportElementDragHandler;
import org.pentaho.reporting.designer.core.editor.report.ReportElementInlineEditor;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class BandReportElementEditor extends DefaultReportElementEditor {
  public BandReportElementEditor() {
  }

  public ReportElementInlineEditor createInlineEditor() {
    return null;
  }

  public ReportElementDragHandler createDragHandler() {
    return new BandReportElementDragHandler();
  }
}
