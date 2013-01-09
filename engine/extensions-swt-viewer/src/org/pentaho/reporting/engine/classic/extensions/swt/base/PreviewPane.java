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

package org.pentaho.reporting.engine.classic.extensions.swt.base;

/**
 * =========================================================
 * Pentaho-Reporting-Classic : a free Java reporting library
 * =========================================================
 *
 * Project Info:  http://reporting.pentaho.org/
 *
 * (C) Copyright 2001-2007, by Object Refinery Ltd, Pentaho Corporation and Contributors.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc.
 * in the United States and other countries.]
 *
 * ------------
 * PreviewPane.java
 * ------------
 * (C) Copyright 2001-2007, by Object Refinery Ltd, Pentaho Corporation and Contributors.
 */

import java.awt.print.PageFormat;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.PageDefinition;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.event.ReportProgressEvent;
import org.pentaho.reporting.engine.classic.core.event.ReportProgressListener;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.PageDrawable;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.PrintReportProcessor;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.util.Worker;
import org.pentaho.reporting.engine.classic.extensions.swt.Util;
import org.pentaho.reporting.engine.classic.extensions.swt.base.actions.AboutActionPlugin;
import org.pentaho.reporting.engine.classic.extensions.swt.base.actions.ControlAction;
import org.pentaho.reporting.engine.classic.extensions.swt.base.actions.ExitActionPlugin;
import org.pentaho.reporting.engine.classic.extensions.swt.base.actions.GoToActionPlugin;
import org.pentaho.reporting.engine.classic.extensions.swt.base.actions.GoToFirstPageActionPlugin;
import org.pentaho.reporting.engine.classic.extensions.swt.base.actions.GoToLastPageActionPlugin;
import org.pentaho.reporting.engine.classic.extensions.swt.base.actions.GoToNextPageActionPlugin;
import org.pentaho.reporting.engine.classic.extensions.swt.base.actions.GoToPreviousPageActionPlugin;
import org.pentaho.reporting.engine.classic.extensions.swt.base.actions.ZoomInActionPlugin;
import org.pentaho.reporting.engine.classic.extensions.swt.base.actions.ZoomOutActionPlugin;
import org.pentaho.reporting.engine.classic.extensions.swt.base.event.ReportHyperlinkEvent;
import org.pentaho.reporting.engine.classic.extensions.swt.base.event.ReportHyperlinkListener;
import org.pentaho.reporting.engine.classic.extensions.swt.base.event.ReportMouseEvent;
import org.pentaho.reporting.engine.classic.extensions.swt.base.event.ReportMouseListener;
import org.pentaho.reporting.engine.classic.extensions.swt.base.internal.PageBackgroundDrawable;
import org.pentaho.reporting.engine.classic.extensions.swt.base.internal.PreviewDrawablePanel;
import org.pentaho.reporting.engine.classic.extensions.swt.base.internal.PreviewPaneUtilities;
import org.pentaho.reporting.engine.classic.extensions.swt.common.IconTheme;
import org.pentaho.reporting.engine.classic.extensions.swt.common.StatusListener;
import org.pentaho.reporting.engine.classic.extensions.swt.commonSWT.ReportEventSource;
import org.pentaho.reporting.engine.classic.extensions.swt.commonSWT.SwtGuiContext;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.Messages;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.designtime.swing.KeyedComboBoxModel;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;

/**
 * Creation-Date: 8/17/2008
 *
 * @author Baochuan Lu
 */
public class PreviewPane extends Composite implements ReportEventSource
{
  private static final Log logger = LogFactory.getLog(PreviewPane.class);

  private class PreviewGuiContext implements SwtGuiContext
  {
    protected PreviewGuiContext()
    {
    }

    public Shell getShell()
    {
      return PreviewPane.this.getShell();
    }

    public Locale getLocale()
    {
      final MasterReport report = getReportJob();
      if (report != null)
      {
        return report.getResourceBundleFactory().getLocale();
      }
      return Locale.getDefault();
    }

    public IconTheme getIconTheme()
    {
      return PreviewPane.this.getIconTheme();
    }

    public Configuration getConfiguration()
    {
      final MasterReport report = getReportJob();
      if (report != null)
      {
        return report.getConfiguration();
      }
      return ClassicEngineBoot.getInstance().getGlobalConfig();
    }

    public StatusListener getStatusListener()
    {
      // return PreviewPane.this.getStatusListener();
      return null;
    }

    public ReportEventSource getEventSource()
    {
      return PreviewPane.this;
    }
  }

  private class RepaginationRunnable implements Runnable,
      ReportProgressListener
  {
    private PrintReportProcessor processor;

    protected RepaginationRunnable(final PrintReportProcessor processor)
    {
      this.processor = processor;
    }

    public void reportProcessingStarted(final ReportProgressEvent event)
    {
      forwardReportStartedEvent(event);
    }

    public void reportProcessingUpdate(final ReportProgressEvent event)
    {
      forwardReportUpdateEvent(event);
    }

    public void reportProcessingFinished(final ReportProgressEvent event)
    {
      forwardReportFinishedEvent(event);
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used to
     * create a thread, starting the thread causes the object's <code>run</code>
     * method to be called in that separately executing thread. <p/> The general
     * contract of the method <code>run</code> is that it may take any action
     * whatsoever.
     *
     * @see Thread#run()
     */
    public void run()
    {
      this.processor.addReportProgressListener(this);
      try
      {
        final UpdatePaginatingPropertyHandler startPaginationNotify = new UpdatePaginatingPropertyHandler(
            processor, true, false, 0);
        startPaginationNotify.run();

        // Perform the pagination ..
        final int pageCount = processor.getNumberOfPages();
        final UpdatePaginatingPropertyHandler endPaginationNotify = new UpdatePaginatingPropertyHandler(
            processor, false, true, pageCount);
        endPaginationNotify.run();
      }
      catch (Exception e)
      {
        final UpdatePaginatingPropertyHandler endPaginationNotify = new UpdatePaginatingPropertyHandler(
            processor, false, false, 0);
        endPaginationNotify.run();
        PreviewPane.logger.error("Pagination failed.", e); //$NON-NLS-1$
      }
      finally
      {
        this.processor.removeReportProgressListener(this);
      }
    }
  }

  private class UpdatePaginatingPropertyHandler implements Runnable
  {
    private boolean paginating;
    private boolean paginated;
    private int pageCount;
    private PrintReportProcessor processor;

    protected UpdatePaginatingPropertyHandler(
        final PrintReportProcessor processor, final boolean paginating,
        final boolean paginated, final int pageCount)
    {
      this.processor = processor;
      this.paginating = paginating;
      this.paginated = paginated;
      this.pageCount = pageCount;
    }

    public void run()
    {
      if (processor != getPrintReportProcessor())
      {
        PreviewPane.logger.debug(messages
            .getString("PreviewPane.DEBUG_NO_LONGER_VALID")); //$NON-NLS-1$
        return;
      }

      // PreviewPane.logger.debug(messages.getString("PreviewPane.DEBUG_PAGINATION",
      // String.valueOf(paginating), String.valueOf(pageCount))); //$NON-NLS-1$
      // //$NON-NLS-2$ //$NON-NLS-3$
      if (paginating == false)
      {
        setNumberOfPages(pageCount);
        if (getPageNumber() < 1)
        {
          setPageNumber(1);
        }
        else if (getPageNumber() > pageCount)
        {
          setPageNumber(pageCount);
        }
      }
      setPaginating(paginating);
      setPaginated(paginated);

      if (processor.isError())
      {
        // setStatusType(StatusType.ERROR);
        // setStatusText(processor.getErrorReason().getLocalizedMessage());
      }
    }
  }

  private class PreviewUpdateHandler implements PropertyChangeListener
  {
    protected PreviewUpdateHandler()
    {
    }

    public void propertyChange(final PropertyChangeEvent evt)
    {
      final String propertyName = evt.getPropertyName();
      if (PreviewPane.PAGINATING_PROPERTY.equals(propertyName))
      {
        if (isPaginating())
        {
          PreviewPane.this.getReportPreviewArea().setDrawableAsRawObject(
              getPaginatingDrawable());
        }
        else
        {
          updateVisiblePage(getPageNumber());
        }
      }
      else if (PreviewPane.REPORT_JOB_PROPERTY.equals(propertyName))
      {
        if (getReportJob() == null)
        {
          PreviewPane.this.getReportPreviewArea().setDrawableAsRawObject(
              getNoReportDrawable());
        }
        // else the paginating property will be fired anyway ..
      }
      else if (PreviewPane.PAGE_NUMBER_PROPERTY.equals(propertyName))
      {
        if (isPaginating())
        {
          return;
        }

        updateVisiblePage(getPageNumber());
      }
    }
  }

  private class UpdateZoomHandler implements PropertyChangeListener
  {
    protected UpdateZoomHandler()
    {
    }

    /**
     * This method gets called when a bound property is changed.
     *
     * @param evt A PropertyChangeEvent object describing the event source and the
     *            property that has changed.
     */

    public void propertyChange(final PropertyChangeEvent evt)
    {
      if ("zoom".equals(evt.getPropertyName()) == false) //$NON-NLS-1$
      {
        return;
      }

      final double zoom = getZoom();
      pageDrawable.setZoom(zoom);
      // final KeyedComboBoxModel zoomModel = PreviewPane.this.getZoomModel();
      // zoomModel.setSelectedKey(new Double(zoom));
      // if (zoomModel.getSelectedKey() == null)
      // {
      // zoomModel.setSelectedItem(formatZoomText(zoom));
      // }
      // drawablePanel.redraw();
      drawablePanel.update();
      // PreviewPane.this.update();
      PreviewPane.this.layout(true);
      // layout(true);
    }
  }

  protected final String formatZoomText(final double zoom)
  {
    final NumberFormat numberFormat = NumberFormat.getPercentInstance(Locale
        .getDefault());
    return (numberFormat.format(zoom));
  }

  /**
   * Maps incomming report-mouse events into Hyperlink events.
   */
  private class HyperLinkEventProcessor implements ReportMouseListener
  {
    private HyperLinkEventProcessor()
    {
    }

    public void reportMouseClicked(final ReportMouseEvent event)
    {
      final RenderNode renderNode = event.getSourceNode();
      final String target = (String) renderNode.getStyleSheet()
          .getStyleProperty(ElementStyleKeys.HREF_TARGET);
      if (target == null)
      {
        return;
      }

      final String window = (String) renderNode.getStyleSheet()
          .getStyleProperty(ElementStyleKeys.HREF_WINDOW);
      final String title = (String) renderNode.getStyleSheet()
          .getStyleProperty(ElementStyleKeys.HREF_TITLE);
      final ReportHyperlinkEvent hyEvent = new ReportHyperlinkEvent(
          PreviewPane.this, renderNode, target, window, title);
      fireReportHyperlinkEvent(hyEvent);
    }

    public void reportMousePressed(final ReportMouseEvent event)
    {
      // not used.
    }

    public void reportMouseReleased(final ReportMouseEvent event)
    {
      // not used.
    }
  }

  private static final double[] ZOOM_FACTORS = {0.5, 0.75, 1, 1.25, 1.50, 2.00};
  private static final int DEFAULT_ZOOM_INDEX = 2;
  public static final String STATUS_TEXT_PROPERTY = "statusText"; //$NON-NLS-1$
  public static final String STATUS_TYPE_PROPERTY = "statusType"; //$NON-NLS-1$
  public static final String REPORT_CONTROLLER_PROPERTY = "reportController"; //$NON-NLS-1$
  public static final String ZOOM_PROPERTY = "zoom"; //$NON-NLS-1$
  public static final String CLOSED_PROPERTY = "closed"; //$NON-NLS-1$

  public static final String REPORT_JOB_PROPERTY = "reportJob"; //$NON-NLS-1$
  public static final String PAGINATING_PROPERTY = "paginating"; //$NON-NLS-1$
  public static final String PAGINATED_PROPERTY = "paginated"; //$NON-NLS-1$
  public static final String PAGE_NUMBER_PROPERTY = "pageNumber"; //$NON-NLS-1$
  public static final String NUMBER_OF_PAGES_PROPERTY = "numberOfPages"; //$NON-NLS-1$

  public static final String ICON_THEME_PROPERTY = "iconTheme"; //$NON-NLS-1$
  public static final String TITLE_PROPERTY = "title"; //$NON-NLS-1$
  public static final String MENU_PROPERTY = "menu"; //$NON-NLS-1$

  /**
   * The preferred width key.
   */
  public static final String PREVIEW_PREFERRED_WIDTH = "org.pentaho.reporting.engine.classic.core.modules.gui.base.PreferredWidth"; //$NON-NLS-1$

  /**
   * The preferred height key.
   */
  public static final String PREVIEW_PREFERRED_HEIGHT = "org.pentaho.reporting.engine.classic.core.modules.gui.base.PreferredHeight"; //$NON-NLS-1$

  /**
   * The maximum width key.
   */
  public static final String PREVIEW_MAXIMUM_WIDTH = "org.pentaho.reporting.engine.classic.core.modules.gui.base.MaximumWidth"; //$NON-NLS-1$

  /**
   * The maximum height key.
   */
  public static final String PREVIEW_MAXIMUM_HEIGHT = "org.pentaho.reporting.engine.classic.core.modules.gui.base.MaximumHeight"; //$NON-NLS-1$

  /**
   * The maximum zoom key.
   */
  public static final String ZOOM_MAXIMUM_KEY = "org.pentaho.reporting.engine.classic.core.modules.gui.base.MaximumZoom"; //$NON-NLS-1$

  /**
   * The minimum zoom key.
   */
  public static final String ZOOM_MINIMUM_KEY = "org.pentaho.reporting.engine.classic.core.modules.gui.base.MinimumZoom"; //$NON-NLS-1$

  /**
   * The default maximum zoom.
   */
  private static final float ZOOM_MAXIMUM_DEFAULT = 20.0f; // 2000%

  /**
   * The default minimum zoom.
   */
  private static final float ZOOM_MINIMUM_DEFAULT = 0.01f; // 1%

  /**
   * @deprecated use the paginating property instead
   */
  public static final String LOCK_INTERFACE_PROPERTY = "lockInterface"; //$NON-NLS-1$
  private static final String MENUBAR_AVAILABLE_KEY = "org.pentaho.reporting.engine.classic.core.modules.gui.base.MenuBarAvailable"; //$NON-NLS-1$

  private Object paginatingDrawable;
  private Object noReportDrawable;
  private PageBackgroundDrawable pageDrawable;

  private PreviewDrawablePanel drawablePanel;
  private boolean closed;
  private MasterReport reportJob;

  private int numberOfPages;
  private int pageNumber;
  private SwtGuiContext swtGuiContext;
  private IconTheme iconTheme;
  private double zoom;
  private boolean paginating;
  private boolean paginated;

  private PrintReportProcessor printReportProcessor;

  private Worker paginationWorker;
  private ArrayList<ReportProgressListener> reportProgressListeners;

  private double maxZoom;
  private double minZoom;

  private Messages messages;
  private boolean deferredRepagination;
  private ArrayList hyperlinkListeners;
  private transient ReportHyperlinkListener[] cachedHyperlinkListeners;

  private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

  private ApplicationWindow app;

  public PreviewPane(final Composite parent, final int style, final ApplicationWindow appWindow)
  {
    super(parent, style);
    app = appWindow;
    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 1;
    gridLayout.marginHeight = 10;
    gridLayout.marginWidth = 10;
    this.setLayout(gridLayout);
    // this.setLayout(new FillLayout());
    final GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.CENTER;
    gridData.verticalAlignment = GridData.CENTER;
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;

    messages = new Messages(Locale.getDefault(), SWTPreviewModule.BUNDLE_NAME,
        ObjectUtilities.getClassLoader(SWTPreviewModule.class));

    zoom = PreviewPane.ZOOM_FACTORS[PreviewPane.DEFAULT_ZOOM_INDEX];
    final Configuration configuration = ClassicEngineBoot.getInstance()
        .getGlobalConfig();
    minZoom = getMinimumZoom(configuration);
    maxZoom = getMaximumZoom(configuration);

    pageDrawable = new PageBackgroundDrawable();

    drawablePanel = new PreviewDrawablePanel(this, SWT.NONE);
    drawablePanel.setBackground(this.getDisplay().getSystemColor(
        SWT.COLOR_GREEN));
    drawablePanel.setDrawableAsRawObject(pageDrawable);
    drawablePanel.addReportMouseListener(new HyperLinkEventProcessor());

    drawablePanel.setLayoutData(gridData);

    swtGuiContext = new PreviewGuiContext();

    addPropertyChangeListener(new PreviewUpdateHandler());
    addPropertyChangeListener("zoom", new UpdateZoomHandler()); //$NON-NLS-1$
    // statusListener = new PreviewPaneStatusListener();

    // setReportController(new ParameterReportController());
    // initializeWithoutJob();
  }

  public PreviewDrawablePanel getReportPreviewArea()
  {
    return drawablePanel;
  }

  public boolean isDeferredRepagination()
  {
    return deferredRepagination;
  }

  public void setDeferredRepagination(final boolean deferredRepagination)
  {
    this.deferredRepagination = deferredRepagination;
  }

  public synchronized PrintReportProcessor getPrintReportProcessor()
  {
    return printReportProcessor;
  }

  protected synchronized void setPrintReportProcessor(
      final PrintReportProcessor printReportProcessor)
  {
    this.printReportProcessor = printReportProcessor;
  }

  public MasterReport getReportJob()
  {
    return reportJob;
  }

  public void setReportJob(final MasterReport reportJob)
  {
    final MasterReport oldJob = this.reportJob;
    this.reportJob = reportJob;

    if (reportJob == null)
    {
      setPaginated(false);
      setPageNumber(0);
      setNumberOfPages(0);
      initializeWithoutJob();
    }
    else
    {
      initializeFromReport();
    }
  }

  public double getZoom()
  {
    return zoom;
  }

  public void setZoom(final double zoom)
  {
    final double oldZoom = this.zoom;
    this.zoom = Math.max(Math.min(zoom, maxZoom), minZoom);
    if (this.zoom != oldZoom)
    {
      pcs.firePropertyChange(PreviewPane.ZOOM_PROPERTY, oldZoom, zoom);
    }
  }

  public double[] getZoomFactors()
  {
    return (double[]) PreviewPane.ZOOM_FACTORS.clone();
  }

  public boolean isPaginated()
  {
    return paginated;
  }

  public boolean isClosed()
  {
    return closed;
  }

  public void setClosed(final boolean closed)
  {
    final boolean oldClosed = this.closed;
    this.closed = closed;
    pcs.firePropertyChange(PreviewPane.CLOSED_PROPERTY, oldClosed, closed);
    if (closed)
    {
      prepareShutdown();
    }
  }

  private void prepareShutdown()
  {
    synchronized (this)
    {
      if (paginationWorker != null)
      {
        // noinspection SynchronizeOnNonFinalField
        synchronized (paginationWorker)
        {
          paginationWorker.finish();
        }
        paginationWorker = null;
      }
      if (printReportProcessor != null)
      {
        printReportProcessor.close();
        printReportProcessor = null;
      }
      // closeToolbar();
    }
  }

  public int getNumberOfPages()
  {
    return numberOfPages;
  }

  public void setNumberOfPages(final int numberOfPages)
  {
    final int oldPageNumber = this.numberOfPages;
    this.numberOfPages = numberOfPages;
    pcs.firePropertyChange(PreviewPane.NUMBER_OF_PAGES_PROPERTY, oldPageNumber,
        numberOfPages);
  }

  public int getPageNumber()
  {
    return pageNumber;
  }

  public void setPageNumber(final int pageNumber)
  {
    final int oldPageNumber = this.pageNumber;
    this.pageNumber = pageNumber;
    // Log.debug("Setting PageNumber: " + pageNumber);
    pcs.firePropertyChange(PreviewPane.PAGE_NUMBER_PROPERTY, oldPageNumber,
        pageNumber);
  }

  public IconTheme getIconTheme()
  {
    return iconTheme;
  }

  protected void setIconTheme(final IconTheme theme)
  {
    final IconTheme oldTheme = this.iconTheme;
    this.iconTheme = theme;
    pcs.firePropertyChange(PreviewPane.ICON_THEME_PROPERTY, oldTheme, theme);
  }

  protected void initializeFromReport()
  {
    final PageDefinition pageDefinition = reportJob.getPageDefinition();
    if (pageDefinition.getPageCount() > 0)
    {
      final PageFormat pageFormat = pageDefinition.getPageFormat(0);
      pageDrawable.setDefaultWidth((int) pageFormat.getWidth());
      pageDrawable.setDefaultHeight((int) pageFormat.getHeight());
    }

    // if (reportJob.getTitle() == null)
    // {
    // this.getShell().setText(messages.getString("PreviewPane.EMPTY_TITLE"));
    // //$NON-NLS-1$
    // }
    // else
    // {
    // this.getShell().setText(messages.getString("PreviewPane.PREVIEW_TITLE",
    // reportJob.getTitle())); //$NON-NLS-1$
    // }

    final Configuration configuration = reportJob.getConfiguration();
    setIconTheme(PreviewPaneUtilities.createIconTheme(configuration));
    Util.printConfiguraiton(configuration);

    performInitialization(configuration);

    // if (deferredRepagination == false)
    // {
    startPagination();
    // }
  }

  protected void initializeWithoutJob()
  {
    // setTitle(messages.getString("PreviewPane.EMPTY_TITLE")); //$NON-NLS-1$
    final Configuration configuration = ClassicEngineBoot.getInstance()
        .getGlobalConfig();
    setIconTheme(PreviewPaneUtilities.createIconTheme(configuration));
    performInitialization(configuration);
  }

  private void performInitialization(final Configuration configuration)
  {
    applyDefinedDimension(configuration);

    if ("true".equals(configuration.getConfigProperty(PreviewPane.MENUBAR_AVAILABLE_KEY))) //$NON-NLS-1$
    {
      buildMenu();
    }
    // zoomModel.clear();
    // for (int i = 0; i < PreviewPane.ZOOM_FACTORS.length; i++)
    // {
    // zoomModel.add(new Double(PreviewPane.ZOOM_FACTORS[i]),
    // formatZoomText(PreviewPane.ZOOM_FACTORS[i]));
    // }
    // zoom = PreviewPane.ZOOM_FACTORS[PreviewPane.DEFAULT_ZOOM_INDEX];
  }

  private void buildMenu()
  {
    final Shell shell = getShell();
    final Menu menuBar = new Menu(shell, SWT.BAR);
    // ToolBar toolBar = new ToolBar(this.getParent().getShell(), SWT.HORIZONTAL
    // | SWT.FLAT | SWT.RIGHT);
    // ToolBarManager toolBarManager = new ToolBarManager(toolBar);
    final ToolBarManager toolBarManager = app.getToolBarManager();
    final MenuManager reportMenuManager = new MenuManager("Report");

    final GoToActionPlugin goToActionPlugin = new GoToActionPlugin();
    goToActionPlugin.initialize(swtGuiContext);
    final ControlAction goToAction = new ControlAction(goToActionPlugin, this);
    reportMenuManager.add(goToAction);

    // PageSetupPlugin pageSetupPlugin = new PageSetupPlugin();
    // pageSetupPlugin.initialize(swtGuiContext);
    // ControlAction pageSetupAction = new ControlAction(pageSetupPlugin, this);
    // reportMenuManager.add(pageSetupAction);
    // toolBarManager.add(pageSetupAction);

    final ExitActionPlugin exitActionPlugin = new ExitActionPlugin();
    exitActionPlugin.initialize(swtGuiContext);
    final ControlAction exitAction = new ControlAction(exitActionPlugin, this);
    reportMenuManager.add(exitAction);

    final MenuManager helpMenuManager = new MenuManager("Help");
    final AboutActionPlugin aboutActionPlugin = new AboutActionPlugin();
    aboutActionPlugin.initialize(swtGuiContext);
    final ControlAction aboutAction = new ControlAction(aboutActionPlugin, this);
    helpMenuManager.add(aboutAction);

    final GoToFirstPageActionPlugin goToFirstPageActionPlugin = new GoToFirstPageActionPlugin();
    goToFirstPageActionPlugin.initialize(swtGuiContext);
    final ControlAction goToFirstPageAction = new ControlAction(
        goToFirstPageActionPlugin, this);
    toolBarManager.add(goToFirstPageAction);

    final GoToPreviousPageActionPlugin goToPreviousPageActionPlugin = new GoToPreviousPageActionPlugin();
    goToPreviousPageActionPlugin.initialize(swtGuiContext);
    final ControlAction goToPreviousPageAction = new ControlAction(
        goToPreviousPageActionPlugin, this);
    toolBarManager.add(goToPreviousPageAction);

    final GoToNextPageActionPlugin goToNextPageActionPlugin = new GoToNextPageActionPlugin();
    goToNextPageActionPlugin.initialize(swtGuiContext);
    final ControlAction goToNextPageAction = new ControlAction(
        goToNextPageActionPlugin, this);
    toolBarManager.add(goToNextPageAction);

    final GoToLastPageActionPlugin goToLastPageActionPlugin = new GoToLastPageActionPlugin();
    goToLastPageActionPlugin.initialize(swtGuiContext);
    final ControlAction goToLastPageAction = new ControlAction(
        goToLastPageActionPlugin, this);
    toolBarManager.add(goToLastPageAction);

    final ZoomInActionPlugin zoomInActionPlugin = new ZoomInActionPlugin();
    zoomInActionPlugin.initialize(swtGuiContext);
    final ControlAction zoomInAction = new ControlAction(zoomInActionPlugin, this);
    toolBarManager.add(zoomInAction);

    final ZoomOutActionPlugin zoomOutActionPlugin = new ZoomOutActionPlugin();
    zoomOutActionPlugin.initialize(swtGuiContext);
    final ControlAction zoomOutAction = new ControlAction(zoomOutActionPlugin, this);
    toolBarManager.add(zoomOutAction);

    final MenuManager exportMenuManager = new MenuManager("Export");

    final MenuManager viewMenuManager = new MenuManager("View");

    reportMenuManager.fill(menuBar, -2);
    helpMenuManager.fill(menuBar, -1);

    shell.setMenuBar(menuBar);
    // toolBar.pack();
    toolBarManager.update(true); // make toolbar visible
    shell.pack();
  }

  /**
   * Read the defined dimensions from the report's configuration and set them to
   * the Dialog. If a maximum size is defined, add a WindowSizeLimiter to check
   * the maximum size
   *
   * @param configuration the report-configuration of this dialog.
   */
  private void applyDefinedDimension(final Configuration configuration)
  {
    String width = configuration
        .getConfigProperty(PreviewPane.PREVIEW_PREFERRED_WIDTH);
    String height = configuration
        .getConfigProperty(PreviewPane.PREVIEW_PREFERRED_HEIGHT);

    // only apply if both values are set.
    if (width != null && height != null)
    {
    }

    width = configuration.getConfigProperty(PreviewPane.PREVIEW_MAXIMUM_WIDTH);
    height = configuration
        .getConfigProperty(PreviewPane.PREVIEW_MAXIMUM_HEIGHT);

    // only apply if at least one value is set.
    if (width != null || height != null)
    {
    }
  }

  protected float parseRelativeFloat(final String value)
  {
    if (value == null)
    {
      throw new NumberFormatException();
    }
    final String tvalue = value.trim();
    if (tvalue.length() > 0 && tvalue.charAt(tvalue.length() - 1) == '%') //$NON-NLS-1$
    {
      final String number = tvalue.substring(0, tvalue.length() - 1); //$NON-NLS-1$
      return Float.parseFloat(number) * -1.0f;
    }
    else
    {
      return Float.parseFloat(tvalue);
    }
  }

  public synchronized void startPagination()
  {
    if (printReportProcessor != null)
    {
      printReportProcessor.close();
      printReportProcessor = null;
    }
    // Reset the pagination to automatic mode now. This way changes to the
    // page-format will trigger
    // pagination again in a safe manor.
    deferredRepagination = false;

    try
    {
      final MasterReport reportJob = getReportJob();
      printReportProcessor = new PrintReportProcessor(reportJob);
      // printReportProcessor.getNumberOfPages();
      // updateVisiblePage(1);
      paginationWorker = new Worker();
      paginationWorker.setWorkload(new RepaginationRunnable(
          printReportProcessor));
    }
    catch (ReportProcessingException e)
    {
      PreviewPane.logger.error("Unable to start report pagination:", e);
    }

  }

  public Object getNoReportDrawable()
  {
    return noReportDrawable;
  }

  public void setNoReportDrawable(final Object noReportDrawable)
  {
    this.noReportDrawable = noReportDrawable;
  }

  public Object getPaginatingDrawable()
  {
    return paginatingDrawable;
  }

  public void setPaginatingDrawable(final Object paginatingDrawable)
  {
    this.paginatingDrawable = paginatingDrawable;
  }

  protected void updateVisiblePage(final int pageNumber)
  {
    if (printReportProcessor == null)
    {
      throw new IllegalStateException();
    }

    final int pageIndex = pageNumber - 1;
    if (pageIndex < 0 || pageIndex >= printReportProcessor.getNumberOfPages())
    {
      this.pageDrawable.setBackend(null);
      this.drawablePanel.setDrawableAsRawObject(pageDrawable);
    }
    else
    {
      final PageDrawable drawable = printReportProcessor.getPageDrawable(pageIndex);
      pageDrawable.setBackend(drawable);
      drawablePanel.setDrawableAsRawObject(pageDrawable);
    }
  }

  public void setPaginated(final boolean paginated)
  {
    final boolean oldPaginated = this.paginated;
    this.paginated = paginated;
    pcs.firePropertyChange(PreviewPane.PAGINATED_PROPERTY, oldPaginated,
        paginated);
  }

  public void setPaginating(final boolean paginating)
  {
    final boolean oldPaginating = this.paginating;
    this.paginating = paginating;
    pcs.firePropertyChange(PreviewPane.PAGINATING_PROPERTY, oldPaginating,
        paginating);
    pcs.firePropertyChange(PreviewPane.LOCK_INTERFACE_PROPERTY, oldPaginating,
        paginating);
  }

  public void addReportProgressListener(
      final ReportProgressListener progressListener)
  {
    if (progressListener == null)
    {
      throw new NullPointerException();
    }

    if (reportProgressListeners == null)
    {
      reportProgressListeners = new ArrayList<ReportProgressListener>();
    }
    reportProgressListeners.add(progressListener);
  }

  public void removeReportProgressListener(
      final ReportProgressListener progressListener)
  {
    if (reportProgressListeners == null)
    {
      return;
    }
    reportProgressListeners.remove(progressListener);
  }

  protected void forwardReportStartedEvent(final ReportProgressEvent event)
  {
    if (reportProgressListeners == null)
    {
      return;
    }
    for (int i = 0; i < reportProgressListeners.size(); i++)
    {
      final ReportProgressListener listener = (ReportProgressListener) reportProgressListeners
          .get(i);
      listener.reportProcessingStarted(event);
    }
  }

  protected void forwardReportUpdateEvent(final ReportProgressEvent event)
  {
    if (reportProgressListeners == null)
    {
      return;
    }
    for (int i = 0; i < reportProgressListeners.size(); i++)
    {
      final ReportProgressListener listener = (ReportProgressListener) reportProgressListeners
          .get(i);
      listener.reportProcessingUpdate(event);
    }
  }

  protected void forwardReportFinishedEvent(final ReportProgressEvent event)
  {
    if (reportProgressListeners == null)
    {
      return;
    }
    for (int i = 0; i < reportProgressListeners.size(); i++)
    {
      final ReportProgressListener listener = (ReportProgressListener) reportProgressListeners
          .get(i);
      listener.reportProcessingFinished(event);
    }
  }

  private double getMaximumZoom(final Configuration configuration)
  {
    final String value = configuration.getConfigProperty(PreviewPane.ZOOM_MAXIMUM_KEY);
    return ParserUtil.parseFloat(value, PreviewPane.ZOOM_MAXIMUM_DEFAULT);
  }

  private double getMinimumZoom(final Configuration configuration)
  {
    final String value = configuration.getConfigProperty(PreviewPane.ZOOM_MINIMUM_KEY);
    return ParserUtil.parseFloat(value, PreviewPane.ZOOM_MINIMUM_DEFAULT);
  }

  public boolean isPaginating()
  {
    return paginating;
  }

  public void addPropertyChangeListener(
      final PropertyChangeListener propertyChangeListener)
  {
    pcs.addPropertyChangeListener(propertyChangeListener);
  }

  public void addPropertyChangeListener(final String property,
                                        final PropertyChangeListener propertyChangeListener)
  {
    pcs.addPropertyChangeListener(property, propertyChangeListener);
  }

  public void removePropertyChangeListener(
      final PropertyChangeListener propertyChangeListener)
  {
    pcs.removePropertyChangeListener(propertyChangeListener);
  }

  public void removePropertyChangeListener(final String property,
                                           final PropertyChangeListener propertyChangeListener)
  {
    pcs.removePropertyChangeListener(property, propertyChangeListener);
  }

  protected void fireReportHyperlinkEvent(final ReportHyperlinkEvent event)
  {
    if (hyperlinkListeners == null)
    {
      return;
    }

    if (cachedHyperlinkListeners == null)
    {
      cachedHyperlinkListeners = (ReportHyperlinkListener[]) hyperlinkListeners
          .toArray(new ReportHyperlinkListener[hyperlinkListeners.size()]);
    }
    final ReportHyperlinkListener[] myListeners = cachedHyperlinkListeners;
    for (int i = 0; i < myListeners.length; i++)
    {
      final ReportHyperlinkListener listener = myListeners[i];
      listener.hyperlinkActivated(event);
    }
  }

  public KeyedComboBoxModel getZoomModel()
  {
    //return zoomModel;
    return null;
  }
}
