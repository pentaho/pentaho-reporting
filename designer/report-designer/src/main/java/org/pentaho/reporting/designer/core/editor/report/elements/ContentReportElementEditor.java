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

import org.pentaho.reporting.designer.core.editor.report.ReportElementInlineEditor;

/**
 * Todo: Document me!
 * <p/>
 * Date: 06.05.2009 Time: 10:39:44
 *
 * @author Thomas Morgner.
 */
public class ContentReportElementEditor extends DefaultReportElementEditor {
  public ContentReportElementEditor() {
  }

  public ReportElementInlineEditor createInlineEditor() {
    return new ContentReportElementInlineEditor();
  }
}
