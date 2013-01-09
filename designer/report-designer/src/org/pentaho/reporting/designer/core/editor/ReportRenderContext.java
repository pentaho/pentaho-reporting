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

package org.pentaho.reporting.designer.core.editor;

import java.util.HashMap;
import java.util.TreeSet;

import org.pentaho.reporting.designer.core.ReportDesignerBoot;
import org.pentaho.reporting.designer.core.auth.AuthenticationStore;
import org.pentaho.reporting.designer.core.auth.GlobalAuthenticationStore;
import org.pentaho.reporting.designer.core.auth.ReportAuthenticationStore;
import org.pentaho.reporting.designer.core.editor.report.layouting.ReportLayouter;
import org.pentaho.reporting.designer.core.inspections.AutoInspectionRunner;
import org.pentaho.reporting.designer.core.model.ModelUtility;
import org.pentaho.reporting.designer.core.model.ReportDataSchemaModel;
import org.pentaho.reporting.designer.core.model.selection.DefaultReportSelectionModel;
import org.pentaho.reporting.designer.core.model.selection.ReportSelectionModel;
import org.pentaho.reporting.designer.core.util.undo.UndoManager;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.PageDefinition;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.event.ReportModelListener;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

/**
 * A render context that covers a single report-model. Report-Definition and master-report can be the same object, but
 * are not necessarily the same. ReportDef could be a subreport and the master-report is always the single
 * master-report.
 *
 * @author Thomas Morgner
 */
public class ReportRenderContext
{
  // TODO add a new delete listener that is looking for embedded resources - separate class

  private class NodeDeleteListener implements ReportModelListener
  {
    private NodeDeleteListener()
    {
    }

    public void nodeChanged(final ReportModelEvent event)
    {
      if (event.isNodeDeleteEvent())
      {
        final ReportSelectionModel selectionModel = getSelectionModel();
        final AbstractReportDefinition reportDefinition = getReportDefinition();
        final Object element = event.getElement();
        if (element instanceof Element)
        {
          final Element[] selectedElements = selectionModel.getSelectedVisualElements();
          for (int i = 0; i < selectedElements.length; i++)
          {
            final Element selectedElement = selectedElements[i];
            if (ModelUtility.isDescendant(reportDefinition, selectedElement) == false)
            {
              selectionModel.remove(element);
            }
          }
        }
        else
        {
          selectionModel.remove(element);
        }
      }
    }
  }

  private class ZoomUpdateHandler implements ZoomModelListener
  {
    private ZoomUpdateHandler()
    {
    }

    public void zoomFactorChanged()
    {
      //noinspection UnnecessaryBoxing
      reportDefinition.setAttribute(ReportDesignerBoot.DESIGNER_NAMESPACE, ReportDesignerBoot.ZOOM,
          new Float(zoomModel.getZoomAsPercentage()), false);
    }
  }

  private static final String AUTHENTICATION_STORE_PROPERTY = "authentication-store";

  private TreeSet<Integer> expandedNodes;
  private long changeTracker;
  private ZoomModel zoomModel;
  private MasterReport masterReportElement;
  private AbstractReportDefinition reportDefinition;
  private ReportSelectionModel selectionModel;
  private UndoManager undo;
  private ReportDataSchemaModel reportDataSchemaModel;
  private AutoInspectionRunner inspectionRunner;
  private NodeDeleteListener deleteListener;
  private HashMap<String, Object> properties;
  private ReportLayouter reportLayouter;

  public ReportRenderContext(final MasterReport masterReport)
  {
    this(masterReport, masterReport, null, new GlobalAuthenticationStore());
  }
  public ReportRenderContext(final MasterReport masterReportElement,
                             final AbstractReportDefinition report,
                             final ReportRenderContext parentContext,
                             final GlobalAuthenticationStore globalAuthenticationStore)
  {
    this(masterReportElement, report, parentContext, globalAuthenticationStore, false);
  }

  public ReportRenderContext(final MasterReport masterReportElement,
                             final AbstractReportDefinition report,
                             final ReportRenderContext parentContext,
                             final GlobalAuthenticationStore globalAuthenticationStore,
                             final boolean computationTarget)
  {
    if (masterReportElement == null)
    {
      throw new NullPointerException();
    }
    if (report == null)
    {
      throw new NullPointerException();
    }

    if (parentContext == null)
    {
      this.properties = new HashMap<String, Object>();
    }
    else
    {
      this.properties = parentContext.properties;
    }

    this.selectionModel = new DefaultReportSelectionModel();
    this.masterReportElement = masterReportElement;

    this.deleteListener = new NodeDeleteListener();

    this.reportDefinition = report;
    this.reportDefinition.addReportModelListener(deleteListener);

    this.expandedNodes = new TreeSet<Integer>();

    this.zoomModel = new ZoomModel();
    this.zoomModel.addZoomModelListener(new ZoomUpdateHandler());

    this.reportDataSchemaModel = new ReportDataSchemaModel(masterReportElement, report);
    if (!computationTarget)
    {
      this.inspectionRunner = new AutoInspectionRunner(this);
      this.reportDefinition.addReportModelListener(inspectionRunner);

      final Object o = this.reportDefinition.getAttribute(ReportDesignerBoot.DESIGNER_NAMESPACE, "undo"); // NON-NLS
      if (o instanceof UndoManager)
      {
        this.undo = (UndoManager) o;
      }
      else
      {
        this.undo = new UndoManager();
      }
    }

    final Object f = this.reportDefinition.getAttribute(ReportDesignerBoot.DESIGNER_NAMESPACE, ReportDesignerBoot.ZOOM);
    if (f instanceof Float)
    {
      final Float zoomFactor = (Float) f;
      //noinspection UnnecessaryUnboxing
      this.zoomModel.setZoomAsPercentage(zoomFactor.floatValue());
    }

    final Object maybeAuthStore = getProperty(AUTHENTICATION_STORE_PROPERTY);
    if (maybeAuthStore == null)
    {
      setProperty(AUTHENTICATION_STORE_PROPERTY, new ReportAuthenticationStore(globalAuthenticationStore));
    }

    this.reportLayouter = new ReportLayouter(this);
  }

  public ReportLayouter getReportLayouter()
  {
    return reportLayouter;
  }

  public ReportSelectionModel getSelectionModel()
  {
    return selectionModel;
  }

  public ReportDataSchemaModel getReportDataSchemaModel()
  {
    return reportDataSchemaModel;
  }

  public AutoInspectionRunner getInspectionRunner()
  {
    return inspectionRunner;
  }

  public ZoomModel getZoomModel()
  {
    return zoomModel;
  }

  public MasterReport getMasterReportElement()
  {
    return masterReportElement;
  }

  public AbstractReportDefinition getReportDefinition()
  {
    return reportDefinition;
  }

  public PageDefinition getPageDefinition()
  {
    return masterReportElement.getPageDefinition();
  }

  public boolean isChanged()
  {
    return getMasterReportElement().getChangeTracker() != changeTracker;
  }

  public void resetChangeTracker()
  {
    this.changeTracker = getMasterReportElement().getChangeTracker();
  }

  public UndoManager getUndo()
  {
    return undo;
  }

  public ResourceManager getResourceManager()
  {
    return masterReportElement.getResourceManager();
  }

  public void dispose()
  {
    if (inspectionRunner != null)
    {
      this.inspectionRunner.dispose();
      this.reportDefinition.removeReportModelListener(inspectionRunner);
    }
    this.reportDefinition.removeReportModelListener(deleteListener);
  }

  public void setProperty(final String property, final Object value)
  {
    if (value == null)
    {
      properties.remove(property);
      return;
    }
    properties.put(property, value);
  }

  public Object getProperty(final String property)
  {
    return properties.get(property);
  }

  public void addExpandedNode(final int aRow)
  {
    expandedNodes.add(aRow);
  }

  public void removeExpandedNode(final int aRow)
  {
    expandedNodes.remove(aRow);
  }

  public Object[] getExpandedNodes()
  {
    return expandedNodes.toArray();
  }

  public AuthenticationStore getAuthenticationStore()
  {
    return (AuthenticationStore) getProperty(AUTHENTICATION_STORE_PROPERTY);
  }
}
