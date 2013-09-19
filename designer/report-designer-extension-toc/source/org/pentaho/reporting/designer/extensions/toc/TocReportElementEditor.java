package org.pentaho.reporting.designer.extensions.toc;

import org.pentaho.reporting.designer.core.editor.report.ReportElementDragHandler;
import org.pentaho.reporting.designer.core.editor.report.ReportElementInlineEditor;
import org.pentaho.reporting.designer.core.editor.report.elements.DefaultReportElementEditor;

public class TocReportElementEditor extends DefaultReportElementEditor
{
  public TocReportElementEditor()
  {
  }

  public ReportElementInlineEditor createInlineEditor()
  {
    return new TocReportElementInlineEditor();
  }

  public ReportElementDragHandler createDragHandler()
  {
    return new TocReportElementDragHandler();
  }
}

