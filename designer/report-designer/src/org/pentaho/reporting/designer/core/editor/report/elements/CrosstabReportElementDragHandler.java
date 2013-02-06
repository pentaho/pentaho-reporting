/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2009 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.reporting.designer.core.editor.report.elements;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.Locale;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.elements.InsertCrosstabGroupAction;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
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
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.ReportDesignerParserModule;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

/**
 * Crosstab drag handler
 *
 * @author Sulaiman Karmali
 */
public class CrosstabReportElementDragHandler extends BaseReportElementDragHandler
{

  public CrosstabReportElementDragHandler()
  {
    super();
  }

  protected void invokeConfigureHandler(final SubReport visualElement,
                                        final Band band,
                                        final ReportElementEditorContext dragContext,
                                        final boolean rootBand)
  {
    SwingUtilities.invokeLater(new CrosstabConfigureHandler(visualElement, band, dragContext, rootBand));
  }

  /**
   * Crosstab specific handling.  Create the visual element and set appropriate attributes
   * @param elementMetaData
   * @return
   * @throws Exception
   */
  protected SubReport setReportStyle(final ElementMetaData elementMetaData) throws Exception
  {
    try
    {
      // Create a crosstab element
      final ElementType type = elementMetaData.create();
      final CrosstabElement visualElement = new CrosstabElement();
      visualElement.setElementType(type);

      // Hide all bands except for Details
      visualElement.getRelationalGroup(0).getHeader().setAttribute(ReportDesignerParserModule.NAMESPACE, ReportDesignerParserModule.HIDE_IN_LAYOUT_GUI_ATTRIBUTE,Boolean.TRUE);
      visualElement.getRelationalGroup(0).getFooter().setAttribute(ReportDesignerParserModule.NAMESPACE, ReportDesignerParserModule.HIDE_IN_LAYOUT_GUI_ATTRIBUTE,Boolean.TRUE);
      visualElement.getPageHeader().setAttribute(ReportDesignerParserModule.NAMESPACE, ReportDesignerParserModule.HIDE_IN_LAYOUT_GUI_ATTRIBUTE, Boolean.TRUE);
      visualElement.getReportHeader().setAttribute(ReportDesignerParserModule.NAMESPACE, ReportDesignerParserModule.HIDE_IN_LAYOUT_GUI_ATTRIBUTE, Boolean.TRUE);
      visualElement.getDetailsFooter().setAttribute(ReportDesignerParserModule.NAMESPACE, ReportDesignerParserModule.HIDE_IN_LAYOUT_GUI_ATTRIBUTE, Boolean.TRUE);
      visualElement.getDetailsHeader().setAttribute(ReportDesignerParserModule.NAMESPACE, ReportDesignerParserModule.HIDE_IN_LAYOUT_GUI_ATTRIBUTE, Boolean.TRUE);
      visualElement.getReportFooter().setAttribute(ReportDesignerParserModule.NAMESPACE, ReportDesignerParserModule.HIDE_IN_LAYOUT_GUI_ATTRIBUTE, Boolean.TRUE);
      visualElement.getPageFooter().setAttribute(ReportDesignerParserModule.NAMESPACE, ReportDesignerParserModule.HIDE_IN_LAYOUT_GUI_ATTRIBUTE, Boolean.TRUE);
      visualElement.getNoDataBand().setAttribute(ReportDesignerParserModule.NAMESPACE, ReportDesignerParserModule.HIDE_IN_LAYOUT_GUI_ATTRIBUTE, Boolean.TRUE);
      visualElement.getWatermark().setAttribute(ReportDesignerParserModule.NAMESPACE, ReportDesignerParserModule.HIDE_IN_LAYOUT_GUI_ATTRIBUTE, Boolean.TRUE);

      type.configureDesignTimeDefaults(visualElement, Locale.getDefault());

      return visualElement;
    }
    catch (Exception e)
    {
      UncaughtExceptionsModel.getInstance().addException(e);
      throw e;
    }
  }



  private static class CrosstabConfigureHandler implements Runnable
  {
    private CrosstabElement subReport;
    private Band parent;
    private ReportElementEditorContext dragContext;
    private boolean rootband;

    private CrosstabConfigureHandler(final SubReport subReport,
                                     final Band parent,
                                     final ReportElementEditorContext dragContext,
                                     final boolean rootband)
    {
      this.subReport = (CrosstabElement)subReport;
      this.parent = parent;
      this.dragContext = dragContext;
      this.rootband = rootband;
    }

    public void run()
    {
      final ReportRenderContext context = dragContext.getRenderContext();
      if (rootband)
      {
        final int result = JOptionPane.showOptionDialog(dragContext.getRepresentationContainer(),
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
          final UndoManager undo = context.getUndo();
          undo.addChange(Messages.getString("CrosstabReportElementDragHandler.UndoEntry"),
                         new ElementEditUndoEntry(parent.getObjectID(), parent.getElementCount(), null, subReport));
          parent.addElement(subReport);
        }
        else
        {
          final AbstractRootLevelBand arb = (AbstractRootLevelBand) parent;
          final UndoManager undo = context.getUndo();
          undo.addChange(Messages.getString("CrosstabReportElementDragHandler.UndoEntry"),
                         new BandedSubreportEditUndoEntry(parent.getObjectID(), arb.getSubReportCount(), null, subReport));
          arb.addSubReport(subReport);
        }
      }
      else
      {
        final UndoManager undo = context.getUndo();
        undo.addChange(Messages.getString("CrosstabReportElementDragHandler.UndoEntry"),
                       new ElementEditUndoEntry(parent.getObjectID(), parent.getElementCount(), null, subReport));
        parent.addElement(subReport);
      }

      final ReportDesignerContext designerContext = dragContext.getDesignerContext();
      final Window window = LibSwingUtil.getWindowAncestor(designerContext.getParent());
      final AbstractReportDefinition reportDefinition = designerContext.getActiveContext().getReportDefinition();

      try
      {
        // Create the new subreport tab - this is where the contents of the Crosstab
        // dialog will go.
        subReport.setDataFactory(reportDefinition.getDataFactory());

        final ResourceBundleFactory rbf = subReport.getResourceBundleFactory();
        subReport.setResourceBundleFactory(rbf);

        final int idx = designerContext.addSubReport(designerContext.getActiveContext(), subReport);
        designerContext.setActiveContext(designerContext.getReportRenderContext(idx));
      }
      catch (ReportDataFactoryException e)
      {
        UncaughtExceptionsModel.getInstance().addException(e);
      }


      // Prompt user to either create or use an existing data-source.
      final SubReportDataSourceDialog crosstabDataSourceDialog;
      crosstabDataSourceDialog = new SubReportDataSourceDialog((JFrame)window);

      // User has prompted to select a data-source.  Get the selected query
      final String queryName = crosstabDataSourceDialog.performSelection(designerContext);
      if (queryName != null)
      {
        subReport.setQuery(queryName);

        // Invoke Crosstab dialog
        InsertCrosstabGroupAction crosstabAction = new InsertCrosstabGroupAction();
        crosstabAction.setReportDesignerContext(designerContext);
        crosstabAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, ""));
      }
      else
      {
        // User did not select a query.  We need to undo the sub-report
      }


      dragContext.getRenderContext().getSelectionModel().setSelectedElements(new Object[]{subReport});
    }
  }
}
