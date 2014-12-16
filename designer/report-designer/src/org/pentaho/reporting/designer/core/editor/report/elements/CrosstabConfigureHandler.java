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
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.designer.core.ReportDesignerBoot;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.elements.InsertCrosstabGroupAction;
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
import org.pentaho.reporting.engine.classic.core.CrosstabElement;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.ReportDesignerParserModule;
import org.pentaho.reporting.libraries.base.util.ArgumentNullException;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

public class CrosstabConfigureHandler implements Runnable
{
  private final Component component;
  private final ReportDesignerContext designerContext;
  private final ReportDocumentContext renderContext;
  private final CrosstabElement subReport;
  private final Band parent;
  private final boolean rootband;

  public CrosstabConfigureHandler(final CrosstabElement subReport,
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

  public CrosstabConfigureHandler(final CrosstabElement subReport,
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
          Messages.getString("CrosstabReportElementDragHandler.BandedOrInlineSubreportQuestion"),
          Messages.getString("CrosstabReportElementDragHandler.InsertSubreport"),
          JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null,
          new String[]{Messages.getString("CrosstabReportElementDragHandler.Inline"),
              Messages.getString("CrosstabReportElementDragHandler.Banded"),
              Messages.getString("CrosstabReportElementDragHandler.Cancel")},
          Messages.getString("CrosstabReportElementDragHandler.Inline"));
      if (result == JOptionPane.CLOSED_OPTION || result == 2)
      {
        return;
      }

      if (result == 0)
      {
        undo.addChange(Messages.getString("CrosstabReportElementDragHandler.UndoEntry"),
            new ElementEditUndoEntry(parent.getObjectID(), parent.getElementCount(), null, subReport));
        parent.addElement(subReport);
      }
      else
      {
        final AbstractRootLevelBand arb = (AbstractRootLevelBand) parent;
        undo.addChange(Messages.getString("CrosstabReportElementDragHandler.UndoEntry"),
            new BandedSubreportEditUndoEntry(parent.getObjectID(), arb.getSubReportCount(), null, subReport));
        arb.addSubReport(subReport);
      }
    }
    else
    {
      undo.addChange(Messages.getString("CrosstabReportElementDragHandler.UndoEntry"),
          new ElementEditUndoEntry(parent.getObjectID(), parent.getElementCount(), null, subReport));
      parent.addElement(subReport);
    }

    final Window window = LibSwingUtil.getWindowAncestor(designerContext.getView().getParent());
    final AbstractReportDefinition reportDefinition = designerContext.getActiveContext().getReportDefinition();

    try
    {
      // Create the new subreport tab - this is where the contents of the Crosstab
      // dialog will go.  Zoom the crosstab canvas to 150% as crosstab has a lot of elements to display
      subReport.setDataFactory(reportDefinition.getDataFactory());
      subReport.getReportDefinition().setAttribute(ReportDesignerBoot.DESIGNER_NAMESPACE, ReportDesignerBoot.ZOOM, 1.5f);

      final int idx = designerContext.addSubReport(designerContext.getActiveContext(), subReport);
      designerContext.setActiveDocument(designerContext.getReportRenderContext(idx));
    }
    catch (ReportDataFactoryException e)
    {
      UncaughtExceptionsModel.getInstance().addException(e);
    }


    // Prompt user to either create or use an existing data-source.
    final SubReportDataSourceDialog crosstabDataSourceDialog;
    if (window instanceof Dialog)
    {
      crosstabDataSourceDialog = new SubReportDataSourceDialog((Dialog) window);
    }
    else if (window instanceof Frame)
    {
      crosstabDataSourceDialog = new SubReportDataSourceDialog((Frame) window);
    }
    else
    {
      crosstabDataSourceDialog = new SubReportDataSourceDialog();
    }

    // User has prompted to select a data-source.  Get the selected query
    final String queryName = crosstabDataSourceDialog.performSelection(designerContext);
    if (queryName != null)
    {
      subReport.setQuery(queryName);

      // Invoke Crosstab dialog
      final InsertCrosstabGroupAction crosstabAction = new InsertCrosstabGroupAction();
      crosstabAction.setReportDesignerContext(designerContext);
      crosstabAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, ""));
    }

    subReport.addInputParameter("*", "*");

    renderContext.getSelectionModel().setSelectedElements(new Object[]{subReport});
  }

  public static void configureDefaults(final CrosstabElement visualElement)
  {
    visualElement.setAutoSort(Boolean.TRUE);

    // Hide all bands except for Details
    visualElement.getPageHeader().setAttribute(ReportDesignerParserModule.NAMESPACE, ReportDesignerParserModule.HIDE_IN_LAYOUT_GUI_ATTRIBUTE, Boolean.TRUE);
    visualElement.getReportHeader().setAttribute(ReportDesignerParserModule.NAMESPACE, ReportDesignerParserModule.HIDE_IN_LAYOUT_GUI_ATTRIBUTE, Boolean.TRUE);
    visualElement.getReportFooter().setAttribute(ReportDesignerParserModule.NAMESPACE, ReportDesignerParserModule.HIDE_IN_LAYOUT_GUI_ATTRIBUTE, Boolean.TRUE);
    visualElement.getPageFooter().setAttribute(ReportDesignerParserModule.NAMESPACE, ReportDesignerParserModule.HIDE_IN_LAYOUT_GUI_ATTRIBUTE, Boolean.TRUE);
    visualElement.getWatermark().setAttribute(ReportDesignerParserModule.NAMESPACE, ReportDesignerParserModule.HIDE_IN_LAYOUT_GUI_ATTRIBUTE, Boolean.TRUE);
  }
}
