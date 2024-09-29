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
import org.pentaho.reporting.designer.core.editor.report.ReportElementEditor;
import org.pentaho.reporting.designer.core.editor.report.ReportElementInlineEditor;

import javax.swing.*;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class DefaultReportElementEditor implements ReportElementEditor {
  public DefaultReportElementEditor() {
  }

  public ReportElementInlineEditor createInlineEditor() {
    return new DefaultReportElementInlineEditor();
  }

  public ReportElementDragHandler createDragHandler() {
    return new DefaultReportElementDragHandler();
  }

  public JComponent createPropertiesPane() {
    return null;
  }
}
