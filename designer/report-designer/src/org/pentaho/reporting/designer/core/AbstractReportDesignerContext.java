package org.pentaho.reporting.designer.core;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

import org.pentaho.reporting.designer.core.auth.GlobalAuthenticationStore;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.model.ModelUtility;
import org.pentaho.reporting.designer.core.settings.RecentFilesModel;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.event.ReportModelListener;

public abstract class AbstractReportDesignerContext implements ReportDesignerContext
{
  private static class SubReportsRemovealHandler implements ReportModelListener
  {
    private AbstractReportDesignerContext designerContext;

    private SubReportsRemovealHandler(final AbstractReportDesignerContext designerContext)
    {
      this.designerContext = designerContext;
    }

    public void nodeChanged(final ReportModelEvent event)
    {
      designerContext.setSelectionWaiting(false);

      if (!event.isNodeDeleteEvent())
      {
        return;
      }

      final Object o = event.getParameter();
      if (o instanceof Section == false)
      {
        return;
      }

      final SubReport[] subReports = ModelUtility.findSubReports((Section) o);
      for (int i = 0; i < subReports.length; i++)
      {
        final SubReport report = subReports[i];
        final int count = designerContext.getReportRenderContextCount();
        for (int x = 0; x < count; x++)
        {
          final ReportRenderContext context = designerContext.getReportRenderContext(x);
          if (context.getReportDefinition() == report)
          {
            designerContext.removeReportRenderContext(x);
            break;
          }
        }
      }
    }
  }


  private PropertyChangeSupport propertyChangeSupport;
  private String statusText;
  private ReportRenderContext activeContext;
  private ArrayList<ReportRenderContext> contexts;
  private RecentFilesModel recentFilesModel;
  private boolean selectionWaiting;
  private GlobalAuthenticationStore authenticationStore;
  private int page;
  private int pageTotal;
  private ReportDesignerView view;

  public AbstractReportDesignerContext(final ReportDesignerView view)
  {
    if (view == null)
    {
      throw new NullPointerException();
    }
    this.view = view;
    this.recentFilesModel = new RecentFilesModel();
    this.contexts = new ArrayList<ReportRenderContext>();
    this.propertyChangeSupport = new PropertyChangeSupport(this);
    this.authenticationStore = new GlobalAuthenticationStore();
  }

  public RecentFilesModel getRecentFilesModel()
  {
    return recentFilesModel;
  }

  public int addMasterReport(final MasterReport masterReportElement)
  {
    setSelectionWaiting(false);

    masterReportElement.setDataFactory(CompoundDataFactory.normalize(masterReportElement.getDataFactory()));
    final ReportRenderContext context =
        new ReportRenderContext(masterReportElement, masterReportElement, null, getGlobalAuthenticationStore());
    contexts.add(context);
    context.resetChangeTracker();

    masterReportElement.addReportModelListener(new SubReportsRemovealHandler(this));

    final int index = contexts.size() - 1;
    propertyChangeSupport.fireIndexedPropertyChange(REPORT_RENDER_CONTEXT_PROPERTY, index, null, context);
    return index;
  }

  public int addSubReport(final ReportRenderContext parentReportContext, final SubReport subReportElement)
  {
    setSelectionWaiting(false);

    subReportElement.setDataFactory(CompoundDataFactory.normalize(subReportElement.getDataFactory()));
    final ReportRenderContext context = new ReportRenderContext(parentReportContext.getMasterReportElement(),
        subReportElement, parentReportContext,
        getGlobalAuthenticationStore());
    contexts.add(context);

    subReportElement.addReportModelListener(new SubReportsRemovealHandler(this));

    final int index = contexts.size() - 1;
    propertyChangeSupport.fireIndexedPropertyChange(REPORT_RENDER_CONTEXT_PROPERTY, index, null, context);
    return index;
  }

  public void removeReportRenderContext(final int index)
  {
    // todo: Also remove all subreports ..
    setSelectionWaiting(false);

    final ReportRenderContext context = contexts.get(index);
    try
    {
      contexts.remove(index);
      if (context != activeContext)
      {
        propertyChangeSupport.fireIndexedPropertyChange(REPORT_RENDER_CONTEXT_PROPERTY, index, context, null);
        return;
      }

      if (index == 0)
      {
        if (contexts.isEmpty() == false)
        {
          setActiveContext(contexts.get(0));
        }
        else
        {
          setActiveContext(null);
        }
      }
      else
      {
        setActiveContext(contexts.get(index - 1));
      }
      propertyChangeSupport.fireIndexedPropertyChange(REPORT_RENDER_CONTEXT_PROPERTY, index, context, null);
    }
    finally
    {
      context.dispose();
    }
  }

  public int getReportRenderContextCount()
  {
    return contexts.size();
  }

  public ReportRenderContext getReportRenderContext(final int index)
  {
    return contexts.get(index);
  }

  public ReportRenderContext getActiveContext()
  {
    return activeContext;
  }

  public void setActiveContext(final ReportRenderContext activeContext)
  {
    if (activeContext != null)
    {
      if (contexts.contains(activeContext) == false)
      {
        throw new IllegalArgumentException("None of my contexts");
      }
    }

    setSelectionWaiting(false);

    final ReportRenderContext context = this.activeContext;
    this.activeContext = activeContext;
    propertyChangeSupport.firePropertyChange(ACTIVE_CONTEXT_PROPERTY, context, activeContext);
  }

  public String getStatusText()
  {
    return statusText;
  }

  public void setStatusText(final String statusText)
  {
    final String oldText = this.statusText;
    this.statusText = statusText;
    propertyChangeSupport.firePropertyChange(STATUS_TEXT_PROPERTY, oldText, statusText);
  }

  public void addPropertyChangeListener(final PropertyChangeListener listener)
  {
    propertyChangeSupport.addPropertyChangeListener(listener);
  }

  public void removePropertyChangeListener(final PropertyChangeListener listener)
  {
    propertyChangeSupport.removePropertyChangeListener(listener);
  }

  public void addPropertyChangeListener(final String propertyName, final PropertyChangeListener listener)
  {
    propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
  }

  public void removePropertyChangeListener(final String propertyName, final PropertyChangeListener listener)
  {
    propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
  }

  public ReportDesignerView getView()
  {
    return view;
  }

  public int findActiveContextIndex()
  {
    for (int i = 0; i < contexts.size(); i++)
    {
      final ReportRenderContext context = contexts.get(i);
      if (context == activeContext)
      {
        return i;
      }
    }
    return -1;
  }

  public boolean isSelectionWaiting()
  {
    return selectionWaiting;
  }

  public void setSelectionWaiting(final boolean selectionWaiting)
  {
    final boolean oldSelectionWaiting = this.selectionWaiting;
    this.selectionWaiting = selectionWaiting;
    propertyChangeSupport.firePropertyChange("selectionWaiting", oldSelectionWaiting, selectionWaiting);//NON-NLS
  }

  public GlobalAuthenticationStore getGlobalAuthenticationStore()
  {
    return authenticationStore;
  }

  public void setPageNumbers(final int page, final int pageTotal)
  {
    final int oldPage = this.page;
    final int oldPageTotal = this.pageTotal;
    this.page = page;
    this.pageTotal = pageTotal;

    propertyChangeSupport.firePropertyChange("pageTotal", oldPageTotal, pageTotal);//NON-NLS
    propertyChangeSupport.firePropertyChange("page", oldPage, page);//NON-NLS
  }

  public int getPage()
  {
    return page;
  }

  public int getPageTotal()
  {
    return pageTotal;
  }
}
