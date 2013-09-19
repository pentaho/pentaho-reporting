package org.pentaho.reporting.designer.extensions.toc;

import java.awt.Component;
import javax.swing.AbstractCellEditor;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.editor.report.ReportElementEditorContext;
import org.pentaho.reporting.designer.core.editor.report.ReportElementInlineEditor;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.extensions.toc.TocElement;

public class TocReportElementInlineEditor extends AbstractCellEditor implements ReportElementInlineEditor
{
  public TocReportElementInlineEditor()
  {
  }

  public Component getElementCellEditorComponent(final ReportElementEditorContext rootBandRenderComponent,
                                                 final ReportElement value)
  {
    final ReportDesignerContext context = rootBandRenderComponent.getDesignerContext();
    final int contextCount = context.getReportRenderContextCount();
    for (int i = 0; i < contextCount; i++)
    {
      final ReportRenderContext rrc = context.getReportRenderContext(i);
      if (rrc.getReportDefinition() == value)
      {
        context.setActiveContext(rrc);
        return null;
      }
    }

    final TocElement report = (TocElement) value;
    try
    {
      context.addSubReport(rootBandRenderComponent.getRenderContext(), report);
    }
    catch (ReportDataFactoryException e1)
    {
      UncaughtExceptionsModel.getInstance().addException(e1);
    }

    return null;
  }

  public Object getCellEditorValue()
  {
    return null;
  }
}

