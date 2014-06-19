/*!
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
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package org.pentaho.reporting.designer.core.editor.report.elements;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.util.Locale;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.designer.core.ReportDesignerBoot;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.elements.InsertCrosstabGroupAction;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
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
import org.pentaho.reporting.engine.classic.core.CrosstabGroup;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.ReportDesignerParserModule;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

/**
 * Crosstab drag handler
 *
 * @author Sulaiman Karmali
 */
public class CrosstabReportElementDragHandler extends AbstractSubReportElementDragHandler
{

  public CrosstabReportElementDragHandler()
  {
    super();
  }

  protected void postProcessDrop(final Element visualElement,
                                 final Band target,
                                 final ReportElementEditorContext dragContext,
                                 final Point2D point)
  {
    final Element rootBand = findRootBand(dragContext, point);
    SwingUtilities.invokeLater(new CrosstabConfigureHandler
        ((SubReport) visualElement, target, dragContext, rootBand == target));
  }

  protected Element createElement(final ElementMetaData elementMetaData,
                                  final String fieldName,
                                  final ReportDocumentContext context) throws InstantiationException
  {
    // Create a crosstab element
    final ElementType type = elementMetaData.create();
    final CrosstabElement visualElement = new CrosstabElement();
    visualElement.setElementType(type);
    visualElement.setRootGroup(new CrosstabGroup());

    // Hide all bands except for Details
    visualElement.getPageHeader().setAttribute(ReportDesignerParserModule.NAMESPACE, ReportDesignerParserModule.HIDE_IN_LAYOUT_GUI_ATTRIBUTE, Boolean.TRUE);
    visualElement.getReportHeader().setAttribute(ReportDesignerParserModule.NAMESPACE, ReportDesignerParserModule.HIDE_IN_LAYOUT_GUI_ATTRIBUTE, Boolean.TRUE);
    visualElement.getReportFooter().setAttribute(ReportDesignerParserModule.NAMESPACE, ReportDesignerParserModule.HIDE_IN_LAYOUT_GUI_ATTRIBUTE, Boolean.TRUE);
    visualElement.getPageFooter().setAttribute(ReportDesignerParserModule.NAMESPACE, ReportDesignerParserModule.HIDE_IN_LAYOUT_GUI_ATTRIBUTE, Boolean.TRUE);
    visualElement.getWatermark().setAttribute(ReportDesignerParserModule.NAMESPACE, ReportDesignerParserModule.HIDE_IN_LAYOUT_GUI_ATTRIBUTE, Boolean.TRUE);

    type.configureDesignTimeDefaults(visualElement, Locale.getDefault());

    final ElementStyleSheet styleSheet = visualElement.getStyle();
    styleSheet.setStyleProperty(ElementStyleKeys.MIN_WIDTH, DEFAULT_WIDTH);
    styleSheet.setStyleProperty(ElementStyleKeys.MIN_HEIGHT, DEFAULT_HEIGHT);

    return visualElement;
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
      this.subReport = (CrosstabElement) subReport;
      this.parent = parent;
      this.dragContext = dragContext;
      this.rootband = rootband;
    }

    public void run()
    {
      final ReportDocumentContext context = dragContext.getRenderContext();
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

      dragContext.getRenderContext().getSelectionModel().setSelectedElements(new Object[]{subReport});
    }
  }
}
