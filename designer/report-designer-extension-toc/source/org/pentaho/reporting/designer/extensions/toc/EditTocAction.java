package org.pentaho.reporting.designer.extensions.toc;

import java.awt.event.ActionEvent;
import javax.swing.Action;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.AbstractElementSelectionAction;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.model.selection.ReportSelectionModel;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.extensions.toc.TocElement;

public class EditTocAction extends AbstractElementSelectionAction
{
  /**
   * Defines an <code>Action</code> object with a default
   * description string and default icon.
   */
  public EditTocAction()
  {
    putValue(Action.NAME, Messages.getInstance().getString("EditTocAction.Text"));
    putValue(Action.SHORT_DESCRIPTION, Messages.getInstance().getString("EditTocAction.Description"));
    putValue(Action.MNEMONIC_KEY, Messages.getInstance().getOptionalMnemonic("EditTocAction.Mnemonic"));
    putValue(Action.ACCELERATOR_KEY, Messages.getInstance().getOptionalKeyStroke("EditTocAction.Accelerator"));
  }


  protected void updateSelection()
  {
    if (isSingleElementSelection() == false)
    {
      setEnabled(false);
      return;
    }

    final Object selectedElement = getSelectionModel().getSelectedElement(0);
    if (selectedElement instanceof TocElement)
    {
      setEnabled(true);
      return;
    }
    setEnabled(false);
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed(final ActionEvent e)
  {
    final ReportDesignerContext designerContext = getReportDesignerContext();
    if (designerContext == null)
    {
      return;
    }

    final ReportRenderContext activeReportContext = getActiveContext();
    if (activeReportContext == null)
    {
      return;
    }

    final ReportSelectionModel selectionModel1 = getSelectionModel();
    if (selectionModel1 == null)
    {
      return;
    }
    final Object leadSelection = selectionModel1.getLeadSelection();
    if (leadSelection instanceof Element == false)
    {
      return;
    }

    final Element element = (Element) leadSelection;
    if (element instanceof TocElement)
    {
      final int contextCount = designerContext.getReportRenderContextCount();
      for (int i = 0; i < contextCount; i++)
      {
        final ReportRenderContext rrc = designerContext.getReportRenderContext(i);
        if (rrc.getReportDefinition() == element)
        {
          designerContext.setActiveContext(rrc);
          return;
        }
      }

      final TocElement report = (TocElement) element;
      try
      {
        designerContext.addSubReport(activeReportContext, report);
      }
      catch (ReportDataFactoryException e1)
      {
        UncaughtExceptionsModel.getInstance().addException(e1);
      }
    }

  }
}
