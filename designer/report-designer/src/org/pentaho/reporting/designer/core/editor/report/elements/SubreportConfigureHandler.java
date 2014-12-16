/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2009 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.reporting.designer.core.editor.report.elements;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import javax.swing.JOptionPane;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.editor.parameters.SubReportDataSourceDialog;
import org.pentaho.reporting.designer.core.editor.report.ReportElementEditorContext;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.designer.core.util.undo.BandedSubreportEditUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.ElementEditUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.UndoManager;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.AbstractRootLevelBand;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.ReportDesignerParserModule;
import org.pentaho.reporting.libraries.base.util.ArgumentNullException;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

public class SubreportConfigureHandler implements Runnable
{
  private final Component component;
  private final ReportDesignerContext designerContext;
  private final ReportDocumentContext renderContext;
  private final SubReport subReport;
  private final Band parent;
  private final boolean rootband;

  public SubreportConfigureHandler(final SubReport subReport,
                                   final Band parent,
                                   final ReportElementEditorContext dragContext,
                                   final boolean rootband)
  {
    ArgumentNullException.validate("subReport", subReport);
    ArgumentNullException.validate("parent", parent);
    ArgumentNullException.validate("dragContext", dragContext);

    this.subReport = subReport;
    this.parent = parent;
    this.component = dragContext.getRepresentationContainer();
    this.designerContext = dragContext.getDesignerContext();
    this.renderContext = dragContext.getRenderContext();
    this.rootband = rootband;
  }

  public SubreportConfigureHandler(final SubReport subReport,
                                   final Band parent,
                                   final ReportDesignerContext designerContext,
                                   final ReportDocumentContext renderContext)
  {
    ArgumentNullException.validate("subReport", subReport);
    ArgumentNullException.validate("parent", parent);
    ArgumentNullException.validate("designerContext", designerContext);
    ArgumentNullException.validate("renderContext", renderContext);

    this.subReport = subReport;
    this.parent = parent;
    this.component = designerContext.getView().getParent();
    this.designerContext = designerContext;
    this.renderContext = renderContext;
    this.rootband = parent instanceof AbstractRootLevelBand;
  }

  public void run()
  {
    final UndoManager undo = renderContext.getUndo();
    if (rootband)
    {
      final int result = JOptionPane.showOptionDialog(component,
          Messages.getString("SubreportReportElementDragHandler.BandedOrInlineSubreportQuestion"),
          Messages.getString("SubreportReportElementDragHandler.InsertSubreport"),
          JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null,
          new String[]{Messages.getString("SubreportReportElementDragHandler.Inline"),
              Messages.getString("SubreportReportElementDragHandler.Banded"),
              Messages.getString("SubreportReportElementDragHandler.Cancel")},
          Messages.getString("SubreportReportElementDragHandler.Inline"));
      if (result == JOptionPane.CLOSED_OPTION || result == 2)
      {
        return;
      }

      if (result == 0)
      {
        undo.addChange(Messages.getString("SubreportReportElementDragHandler.UndoEntry"),
            new ElementEditUndoEntry(parent.getObjectID(), parent.getElementCount(), null, subReport));
        parent.addElement(subReport);
      }
      else
      {
        final AbstractRootLevelBand arb = (AbstractRootLevelBand) parent;
        undo.addChange(Messages.getString("SubreportReportElementDragHandler.UndoEntry"),
            new BandedSubreportEditUndoEntry(parent.getObjectID(), arb.getSubReportCount(), null, subReport));
        arb.addSubReport(subReport);
      }
    }
    else
    {
      undo.addChange(Messages.getString("SubreportReportElementDragHandler.UndoEntry"),
          new ElementEditUndoEntry(parent.getObjectID(), parent.getElementCount(), null, subReport));
      parent.addElement(subReport);
    }

    final Window window = LibSwingUtil.getWindowAncestor(designerContext.getView().getParent());
    final AbstractReportDefinition reportDefinition = designerContext.getActiveContext().getReportDefinition();

    try
    {
      // Create the new subreport tab and update the active context to point to new subreport.
      subReport.setDataFactory(reportDefinition.getDataFactory());

      final int idx = designerContext.addSubReport(designerContext.getActiveContext(), subReport);
      designerContext.setActiveDocument(designerContext.getReportRenderContext(idx));
    }
    catch (ReportDataFactoryException e)
    {
      UncaughtExceptionsModel.getInstance().addException(e);
    }

    // Prompt user to either create or use an existing data-source.
    final SubReportDataSourceDialog subreportDataSourceDialog;
    if (window instanceof Dialog)
    {
      subreportDataSourceDialog = new SubReportDataSourceDialog((Dialog) window);
    }
    else if (window instanceof Frame)
    {
      subreportDataSourceDialog = new SubReportDataSourceDialog((Frame) window);
    }
    else
    {
      subreportDataSourceDialog = new SubReportDataSourceDialog();
    }

    final String queryName = subreportDataSourceDialog.performSelection(designerContext);
    if (queryName != null)
    {
      subReport.setQuery(queryName);
    }

    subReport.addInputParameter("*", "*");

    renderContext.getSelectionModel().setSelectedElements(new Object[]{subReport});
  }

  public static void configureDefaults(final SubReport visualElement) {
    visualElement.setAutoSort(Boolean.TRUE);
    visualElement.getRelationalGroup(0).getHeader().setAttribute(ReportDesignerParserModule.NAMESPACE, ReportDesignerParserModule.HIDE_IN_LAYOUT_GUI_ATTRIBUTE, Boolean.TRUE);
    visualElement.getRelationalGroup(0).getFooter().setAttribute(ReportDesignerParserModule.NAMESPACE, ReportDesignerParserModule.HIDE_IN_LAYOUT_GUI_ATTRIBUTE, Boolean.TRUE);
    visualElement.getDetailsFooter().setAttribute(ReportDesignerParserModule.NAMESPACE, ReportDesignerParserModule.HIDE_IN_LAYOUT_GUI_ATTRIBUTE, Boolean.TRUE);
    visualElement.getDetailsHeader().setAttribute(ReportDesignerParserModule.NAMESPACE, ReportDesignerParserModule.HIDE_IN_LAYOUT_GUI_ATTRIBUTE, Boolean.TRUE);
    visualElement.getNoDataBand().setAttribute(ReportDesignerParserModule.NAMESPACE, ReportDesignerParserModule.HIDE_IN_LAYOUT_GUI_ATTRIBUTE, Boolean.TRUE);
    visualElement.getWatermark().setAttribute(ReportDesignerParserModule.NAMESPACE, ReportDesignerParserModule.HIDE_IN_LAYOUT_GUI_ATTRIBUTE, Boolean.TRUE);
  }
}
