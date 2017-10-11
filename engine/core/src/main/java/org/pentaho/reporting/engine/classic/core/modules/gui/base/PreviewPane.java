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
 * Copyright (c) 2001 - 2016 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.gui.base;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.IllegalComponentStateException;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.print.PageFormat;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.PageDefinition;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.event.ReportProgressEvent;
import org.pentaho.reporting.engine.classic.core.event.ReportProgressListener;
import org.pentaho.reporting.engine.classic.core.imagemap.ImageMap;
import org.pentaho.reporting.engine.classic.core.imagemap.ImageMapEntry;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableReplacedContentBox;
import org.pentaho.reporting.engine.classic.core.layout.output.RenderUtility;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.actions.ZoomAction;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.event.ReportHyperlinkEvent;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.event.ReportHyperlinkListener;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.event.ReportMouseEvent;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.event.ReportMouseListener;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.internal.ActionCategory;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.internal.ActionPluginComparator;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.internal.CategoryTreeItem;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.internal.PageBackgroundDrawable;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.internal.PreviewDrawablePanel;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.internal.PreviewPaneUtilities;
import org.pentaho.reporting.engine.classic.core.modules.gui.common.IconTheme;
import org.pentaho.reporting.engine.classic.core.modules.gui.common.StatusListener;
import org.pentaho.reporting.engine.classic.core.modules.gui.common.StatusType;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ActionPlugin;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.CenterLayout;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ReportEventSource;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.SwingGuiContext;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.WindowSizeLimiter;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.PageDrawable;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.PrintReportProcessor;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.util.Worker;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.Messages;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.designtime.swing.KeyedComboBoxModel;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;
import org.pentaho.reporting.libraries.xmlns.LibXmlInfo;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;

/**
 * Creation-Date: 11.11.2006, 19:36:13
 *
 * @author Thomas Morgner
 */
public class PreviewPane extends JPanel implements ReportEventSource {
  private static final Log logger = LogFactory.getLog( PreviewPane.class );

  private class PreviewGuiContext implements SwingGuiContext {
    protected PreviewGuiContext() {
    }

    public Window getWindow() {
      return LibSwingUtil.getWindowAncestor( PreviewPane.this );
    }

    public Locale getLocale() {
      final MasterReport report = getReportJob();
      if ( report != null ) {
        final Locale bundleLocale = report.getResourceBundleFactory().getLocale();
        if ( bundleLocale != null ) {
          return bundleLocale;
        }
        return report.getReportEnvironment().getLocale();
      }
      return Locale.getDefault();
    }

    public IconTheme getIconTheme() {
      return PreviewPane.this.getIconTheme();
    }

    public Configuration getConfiguration() {
      return PreviewPane.this.computeContextConfiguration();
    }

    public StatusListener getStatusListener() {
      return PreviewPane.this.getStatusListener();
    }

    public ReportEventSource getEventSource() {
      return PreviewPane.this;
    }
  }

  private class PreviewPaneStatusUpdater implements Runnable {
    private StatusType type;
    private String text;
    private Throwable error;

    protected PreviewPaneStatusUpdater( final StatusType type, final String text, final Throwable error ) {
      this.type = type;
      this.text = text;
      this.error = error;
    }

    public Throwable getError() {
      return error;
    }

    public StatusType getType() {
      return type;
    }

    public String getText() {
      return text;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used to create a thread, starting the thread
     * causes the object's <code>run</code> method to be called in that separately executing thread.
     * <p/>
     * The general contract of the method <code>run</code> is that it may take any action whatsoever.
     *
     * @see Thread#run()
     */
    public void run() {
      setStatusType( type );
      setStatusText( text );
      setError( error );
    }
  }

  /**
   * The StatusListener here shields the preview pane from any attempt to tamper with it.
   */
  private class PreviewPaneStatusListener implements StatusListener {
    protected PreviewPaneStatusListener() {
    }

    public void setStatus( final StatusType type, final String text, final Throwable error ) {
      if ( SwingUtilities.isEventDispatchThread() ) {
        setStatusType( type );
        setStatusText( text );
        setError( error );
      } else {
        SwingUtilities.invokeLater( new PreviewPaneStatusUpdater( type, text, error ) );
      }
    }
  }

  private class RepaginationRunnable implements Runnable, ReportProgressListener {
    private PrintReportProcessor processor;

    protected RepaginationRunnable( final PrintReportProcessor processor ) {
      this.processor = processor;
    }

    public void reportProcessingStarted( final ReportProgressEvent event ) {
      forwardReportStartedEvent( event );
    }

    public void reportProcessingUpdate( final ReportProgressEvent event ) {
      forwardReportUpdateEvent( event );
    }

    public void reportProcessingFinished( final ReportProgressEvent event ) {
      forwardReportFinishedEvent( event );
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used to create a thread, starting the thread
     * causes the object's <code>run</code> method to be called in that separately executing thread.
     * <p/>
     * The general contract of the method <code>run</code> is that it may take any action whatsoever.
     *
     * @see Thread#run()
     */
    public void run() {
      this.processor.addReportProgressListener( this );
      try {
        final UpdatePaginatingPropertyHandler startPaginationNotify =
            new UpdatePaginatingPropertyHandler( processor, true, false, 0 );
        if ( SwingUtilities.isEventDispatchThread() ) {
          startPaginationNotify.run();
        } else {
          SwingUtilities.invokeLater( startPaginationNotify );
        }

        // Perform the pagination ..
        final int pageCount = processor.getNumberOfPages();
        final UpdatePaginatingPropertyHandler endPaginationNotify =
            new UpdatePaginatingPropertyHandler( processor, false, true, pageCount );
        if ( SwingUtilities.isEventDispatchThread() ) {
          endPaginationNotify.run();
        } else {
          SwingUtilities.invokeLater( endPaginationNotify );
        }
      } catch ( Exception e ) {
        final UpdatePaginatingPropertyHandler endPaginationNotify =
            new UpdatePaginatingPropertyHandler( processor, false, false, 0 );
        if ( SwingUtilities.isEventDispatchThread() ) {
          endPaginationNotify.run();
        } else {
          SwingUtilities.invokeLater( endPaginationNotify );
        }
        PreviewPane.logger.error( "Pagination failed.", e ); //$NON-NLS-1$
      } finally {
        this.processor.removeReportProgressListener( this );
      }
    }
  }

  private class UpdatePaginatingPropertyHandler implements Runnable {
    private boolean paginating;
    private boolean paginated;
    private int pageCount;
    private PrintReportProcessor processor;

    protected UpdatePaginatingPropertyHandler( final PrintReportProcessor processor, final boolean paginating,
        final boolean paginated, final int pageCount ) {
      this.processor = processor;
      this.paginating = paginating;
      this.paginated = paginated;
      this.pageCount = pageCount;
    }

    public void run() {
      if ( processor != getPrintReportProcessor() ) {
        PreviewPane.logger.debug( messages.getString( "PreviewPane.DEBUG_NO_LONGER_VALID" ) ); //$NON-NLS-1$
        return;
      }

      PreviewPane.logger.debug( messages.getString( "PreviewPane.DEBUG_PAGINATION", String.valueOf( paginating ),
          String.valueOf( pageCount ) ) ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      if ( paginating == false ) {
        setNumberOfPages( pageCount );
        if ( getPageNumber() < 1 ) {
          setPageNumber( 1 );
        } else if ( getPageNumber() > pageCount ) {
          setPageNumber( pageCount );
        }
      }
      setPaginating( paginating );
      setPaginated( paginated );

      if ( processor.isError() ) {
        setError( processor.getErrorReason() );
        setStatusType( StatusType.ERROR );
        setStatusText( processor.getErrorReason().getLocalizedMessage() );
      }
    }
  }

  private class PreviewUpdateHandler implements PropertyChangeListener {
    protected PreviewUpdateHandler() {
    }

    public void propertyChange( final PropertyChangeEvent evt ) {
      final String propertyName = evt.getPropertyName();
      if ( PreviewPane.PAGINATING_PROPERTY.equals( propertyName ) ) {
        if ( isPaginating() ) {
          PreviewPane.this.getReportPreviewArea().setDrawableAsRawObject( getPaginatingDrawable() );
        } else {
          updateVisiblePage( getPageNumber() );
        }
      } else if ( PreviewPane.REPORT_JOB_PROPERTY.equals( propertyName ) ) {
        if ( getReportJob() == null ) {
          PreviewPane.this.getReportPreviewArea().setDrawableAsRawObject( getNoReportDrawable() );
        }
        // else the paginating property will be fired anyway ..
      } else if ( PreviewPane.PAGE_NUMBER_PROPERTY.equals( propertyName ) ) {
        if ( isPaginating() ) {
          return;
        }

        updateVisiblePage( getPageNumber() );
      }
    }
  }

  private class UpdateZoomHandler implements PropertyChangeListener {
    protected UpdateZoomHandler() {
    }

    /**
     * This method gets called when a bound property is changed.
     *
     * @param evt
     *          A PropertyChangeEvent object describing the event source and the property that has changed.
     */

    public void propertyChange( final PropertyChangeEvent evt ) {
      if ( "zoom".equals( evt.getPropertyName() ) == false ) { //$NON-NLS-1$
        return;
      }

      final double zoom = getZoom();
      pageDrawable.setZoom( zoom );
      final KeyedComboBoxModel<Double, String> zoomModel = PreviewPane.this.getZoomModel();
      zoomModel.setSelectedKey( new Double( zoom ) );
      if ( zoomModel.getSelectedKey() == null ) {
        zoomModel.setSelectedItem( formatZoomText( zoom ) );
      }
      drawablePanel.revalidate();
    }
  }

  /**
   * Maps incomming report-mouse events into Hyperlink events.
   */
  private class HyperLinkEventProcessor implements ReportMouseListener {
    private boolean mouseLinkActive;

    private HyperLinkEventProcessor() {
    }

    public void reportMouseClicked( final ReportMouseEvent event ) {
      final RenderNode renderNode = event.getSourceNode();
      final String target = extractLink( renderNode, event );
      if ( target == null ) {
        return;
      }

      final String window = (String) renderNode.getStyleSheet().getStyleProperty( ElementStyleKeys.HREF_WINDOW );
      final String title = (String) renderNode.getStyleSheet().getStyleProperty( ElementStyleKeys.HREF_TITLE );
      final ReportHyperlinkEvent hyEvent =
          new ReportHyperlinkEvent( PreviewPane.this, renderNode, target, window, title );
      fireReportHyperlinkEvent( hyEvent );
    }

    private String extractLink( final RenderNode node, final ReportMouseEvent event ) {
      if ( node instanceof RenderableReplacedContentBox ) {
        // process image map
        final ImageMap imageMap = RenderUtility.extractImageMap( (RenderableReplacedContentBox) node );
        if ( imageMap != null ) {
          final PageDrawable physicalPageDrawable = drawablePanel.getPageDrawable();
          final PageFormat pf = physicalPageDrawable.getPageFormat();
          final float x1 = (float) ( event.getSourceEvent().getX() / zoom );
          final float y1 = (float) ( event.getSourceEvent().getY() / zoom );
          final float imageMapX = (float) ( x1 - pf.getImageableX() - StrictGeomUtility.toExternalValue( node.getX() ) );
          final float imageMapY = (float) ( y1 - pf.getImageableY() - StrictGeomUtility.toExternalValue( node.getY() ) );
          final ImageMapEntry[] imageMapEntries = imageMap.getEntriesForPoint( imageMapX, imageMapY );
          for ( int i = 0; i < imageMapEntries.length; i++ ) {
            final ImageMapEntry entry = imageMapEntries[i];
            final Object imageMapTarget = entry.getAttribute( LibXmlInfo.XHTML_NAMESPACE, "href" );
            if ( imageMapTarget != null ) {
              return String.valueOf( imageMapTarget );
            }
          }
        }
      }

      final String target = (String) node.getStyleSheet().getStyleProperty( ElementStyleKeys.HREF_TARGET );
      if ( target == null ) {
        return null;
      }
      return target;
    }

    public void reportMousePressed( final ReportMouseEvent event ) {
      // not used.
    }

    public void reportMouseReleased( final ReportMouseEvent event ) {
      // not used.
    }

    public void reportMouseMoved( final ReportMouseEvent event ) {
      if ( isHyperlinkSystemActive() == false ) {
        return;
      }

      final RenderNode renderNode = event.getSourceNode();
      final String target = extractLink( renderNode, event );
      if ( target == null ) {
        if ( mouseLinkActive ) {
          setCursor( Cursor.getDefaultCursor() );
          mouseLinkActive = false;
        }
        return;
      }

      if ( mouseLinkActive == false ) {
        setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        mouseLinkActive = true;
      }
    }

    public void reportMouseDragged( final ReportMouseEvent event ) {
    }
  }

  private static class ScrollablePanel extends JPanel implements Scrollable {
    /**
     * Creates a new <code>JPanel</code> with a double buffer and a flow layout.
     */
    private ScrollablePanel() {
      setLayout( new CenterLayout() );
    }

    /**
     * Returns the preferred size of the viewport for a view component. For example, the preferred size of a
     * <code>JList</code> component is the size required to accommodate all of the cells in its list. However, the value
     * of <code>preferredScrollableViewportSize</code> is the size required for <code>JList.getVisibleRowCount</code>
     * rows. A component without any properties that would affect the viewport size should just return
     * <code>getPreferredSize</code> here.
     *
     * @return the preferredSize of a <code>JViewport</code> whose view is this <code>Scrollable</code>
     * @see javax.swing.JViewport#getPreferredSize
     */
    public Dimension getPreferredScrollableViewportSize() {
      return getPreferredSize();
    }

    /**
     * Components that display logical rows or columns should compute the scroll increment that will completely expose
     * one new row or column, depending on the value of orientation. Ideally, components should handle a partially
     * exposed row or column by returning the distance required to completely expose the item.
     * <p/>
     * Scrolling containers, like JScrollPane, will use this method each time the user requests a unit scroll.
     *
     * @param visibleRect
     *          The view area visible within the viewport
     * @param orientation
     *          Either SwingConstants.VERTICAL or SwingConstants.HORIZONTAL.
     * @param direction
     *          Less than zero to scroll up/left, greater than zero for down/right.
     * @return The "unit" increment for scrolling in the specified direction. This value should always be positive.
     */
    public int getScrollableUnitIncrement( final Rectangle visibleRect, final int orientation, final int direction ) {
      return 20;
    }

    /**
     * Components that display logical rows or columns should compute the scroll increment that will completely expose
     * one block of rows or columns, depending on the value of orientation.
     * <p/>
     * Scrolling containers, like JScrollPane, will use this method each time the user requests a block scroll.
     *
     * @param visibleRect
     *          The view area visible within the viewport
     * @param orientation
     *          Either SwingConstants.VERTICAL or SwingConstants.HORIZONTAL.
     * @param direction
     *          Less than zero to scroll up/left, greater than zero for down/right.
     * @return The "block" increment for scrolling in the specified direction. This value should always be positive.
     */
    public int getScrollableBlockIncrement( final Rectangle visibleRect, final int orientation, final int direction ) {
      return 100;
    }

    /**
     * Return true if a viewport should always force the width of this <code>Scrollable</code> to match the width of the
     * viewport. For example a normal text view that supported line wrapping would return true here, since it would be
     * undesirable for wrapped lines to disappear beyond the right edge of the viewport. Note that returning true for a
     * Scrollable whose ancestor is a JScrollPane effectively disables horizontal scrolling.
     * <p/>
     * Scrolling containers, like JViewport, will use this method each time they are validated.
     *
     * @return True if a viewport should force the Scrollables width to match its own.
     */
    public boolean getScrollableTracksViewportWidth() {
      return false;
    }

    /**
     * Return true if a viewport should always force the height of this Scrollable to match the height of the viewport.
     * For example a columnar text view that flowed text in left to right columns could effectively disable vertical
     * scrolling by returning true here.
     * <p/>
     * Scrolling containers, like JViewport, will use this method each time they are validated.
     *
     * @return True if a viewport should force the Scrollables height to match its own.
     */
    public boolean getScrollableTracksViewportHeight() {
      return false;
    }
  }

  /**
   * A zoom select action.
   */
  private static class ZoomSelectAction extends AbstractAction {
    private KeyedComboBoxModel source;
    private PreviewPane pane;

    /**
     * Creates a new action.
     */
    protected ZoomSelectAction( final KeyedComboBoxModel source, final PreviewPane pane ) {
      this.source = source;
      this.pane = pane;
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e
     *          the event.
     */
    public void actionPerformed( final ActionEvent e ) {
      final Double selected = (Double) source.getSelectedKey();
      if ( selected != null ) {
        pane.setZoom( selected.doubleValue() );
      }
    }
  }

  private static final double[] ZOOM_FACTORS = { 0.5, 0.75, 1, 1.25, 1.50, 2.00 };

  private static final int DEFAULT_ZOOM_INDEX = 2;
  public static final String STATUS_TEXT_PROPERTY = "statusText"; //$NON-NLS-1$
  public static final String STATUS_TYPE_PROPERTY = "statusType"; //$NON-NLS-1$
  public static final String REPORT_CONTROLLER_PROPERTY = "reportController"; //$NON-NLS-1$
  public static final String ZOOM_PROPERTY = "zoom"; //$NON-NLS-1$
  public static final String CLOSED_PROPERTY = "closed"; //$NON-NLS-1$
  public static final String ERROR_PROPERTY = "error"; //$NON-NLS-1$

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
  public static final String PREVIEW_PREFERRED_WIDTH =
      "org.pentaho.reporting.engine.classic.core.modules.gui.base.PreferredWidth"; //$NON-NLS-1$

  /**
   * The preferred height key.
   */
  public static final String PREVIEW_PREFERRED_HEIGHT =
      "org.pentaho.reporting.engine.classic.core.modules.gui.base.PreferredHeight"; //$NON-NLS-1$

  /**
   * The maximum width key.
   */
  public static final String PREVIEW_MAXIMUM_WIDTH =
      "org.pentaho.reporting.engine.classic.core.modules.gui.base.MaximumWidth"; //$NON-NLS-1$

  /**
   * The maximum height key.
   */
  public static final String PREVIEW_MAXIMUM_HEIGHT =
      "org.pentaho.reporting.engine.classic.core.modules.gui.base.MaximumHeight"; //$NON-NLS-1$

  /**
   * The maximum zoom key.
   */
  public static final String ZOOM_MAXIMUM_KEY =
      "org.pentaho.reporting.engine.classic.core.modules.gui.base.MaximumZoom"; //$NON-NLS-1$

  /**
   * The minimum zoom key.
   */
  public static final String ZOOM_MINIMUM_KEY =
      "org.pentaho.reporting.engine.classic.core.modules.gui.base.MinimumZoom"; //$NON-NLS-1$

  /**
   * The default maximum zoom.
   */
  private static final float ZOOM_MAXIMUM_DEFAULT = 20.0f; // 2000%

  /**
   * The default minimum zoom.
   */
  private static final float ZOOM_MINIMUM_DEFAULT = 0.01f; // 1%

  private static final String MENUBAR_AVAILABLE_KEY =
      "org.pentaho.reporting.engine.classic.core.modules.gui.base.MenuBarAvailable"; //$NON-NLS-1$
  private static final String TOOLBAR_AVAILABLE_KEY =
      "org.pentaho.reporting.engine.classic.core.modules.gui.base.ToolbarAvailable"; //$NON-NLS-1$
  private static final String TOOLBAR_FLOATABLE_KEY =
      "org.pentaho.reporting.engine.classic.core.modules.gui.base.ToolbarFloatable"; //$NON-NLS-1$

  private Object paginatingDrawable;
  private Object noReportDrawable;
  private PageBackgroundDrawable pageDrawable;

  private PreviewDrawablePanel drawablePanel;
  private ReportController reportController;
  private JMenu[] menus;
  private JToolBar toolBar;
  private String statusText;
  private Throwable error;
  private String title;
  private StatusType statusType;
  private boolean closed;
  private MasterReport reportJob;

  private int numberOfPages;
  private int pageNumber;
  private SwingGuiContext swingGuiContext;
  private IconTheme iconTheme;
  private double zoom;
  private boolean paginating;
  private boolean paginated;

  private PrintReportProcessor printReportProcessor;

  private Worker paginationWorker;
  private JPanel toolbarHolder;
  private JPanel outerReportControllerHolder;
  private boolean reportControllerInner;
  private String reportControllerLocation;
  private JComponent reportControllerComponent;
  private KeyedComboBoxModel<Double, String> zoomModel;
  private PreviewPane.PreviewPaneStatusListener statusListener;
  private static final JMenu[] EMPTY_MENU = new JMenu[0];
  private boolean toolbarFloatable;
  private ArrayList reportProgressListener;

  private double maxZoom;
  private double minZoom;

  private Messages messages;
  private WindowSizeLimiter sizeLimiter;
  private boolean deferredRepagination;
  private ArrayList hyperlinkListeners;
  private transient ReportHyperlinkListener[] cachedHyperlinkListeners;
  private Map<ActionCategory, ActionPlugin[]> actionPlugins;
  private Map<ActionCategory, ZoomAction[]> zoomActions;
  private JComboBox zoomSelectorBox;

  private JScrollPane reportPaneScrollPane;

  private int reportControllerSliderSize;
  private boolean performInitializationRunning;

  /**
   * Creates a new <code>JPanel</code> with a double buffer and a flow layout.
   */
  public PreviewPane() {
    this( true );
  }

  public PreviewPane( final boolean init ) {
    messages =
        new Messages( getLocale(), SwingPreviewModule.BUNDLE_NAME, ObjectUtilities
            .getClassLoader( SwingPreviewModule.class ) );
    sizeLimiter = new WindowSizeLimiter();
    zoomActions = new HashMap<ActionCategory, ZoomAction[]>();

    this.menus = PreviewPane.EMPTY_MENU;
    setLayout( new BorderLayout() );

    zoomModel = new KeyedComboBoxModel();
    zoomModel.setAllowOtherValue( true );
    zoom = PreviewPane.ZOOM_FACTORS[PreviewPane.DEFAULT_ZOOM_INDEX];

    final Configuration configuration = ClassicEngineBoot.getInstance().getGlobalConfig();
    minZoom = getMinimumZoom( configuration );
    maxZoom = getMaximumZoom( configuration );

    pageDrawable = new PageBackgroundDrawable();

    drawablePanel = new PreviewDrawablePanel();
    drawablePanel.setOpaque( false );
    drawablePanel.setBackground( null );
    drawablePanel.setDoubleBuffered( true );
    drawablePanel.setDrawableAsRawObject( pageDrawable );
    drawablePanel.addReportMouseListener( new HyperLinkEventProcessor() );

    swingGuiContext = new PreviewGuiContext();

    final JPanel reportPaneHolder = new ScrollablePanel();
    reportPaneHolder.setOpaque( false );
    reportPaneHolder.setBackground( null );
    reportPaneHolder.setBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) );
    reportPaneHolder.add( drawablePanel );

    reportPaneScrollPane = new JScrollPane( reportPaneHolder );
    reportPaneScrollPane.getVerticalScrollBar().setUnitIncrement( 20 );
    reportPaneScrollPane.setBackground( null );
    reportPaneScrollPane.setOpaque( false );
    reportPaneScrollPane.getViewport().setOpaque( false );
    ( (JComponent) reportPaneScrollPane.getViewport().getView() ).setOpaque( false );

    toolbarHolder = new JPanel();
    toolbarHolder.setLayout( new BorderLayout() );

    outerReportControllerHolder = new JPanel();
    outerReportControllerHolder.setOpaque( false );
    outerReportControllerHolder.setBackground( null );
    outerReportControllerHolder.setLayout( new BorderLayout() );
    outerReportControllerHolder.add( toolbarHolder, BorderLayout.NORTH );
    outerReportControllerHolder.add( reportPaneScrollPane, BorderLayout.CENTER );
    add( outerReportControllerHolder, BorderLayout.CENTER );

    addPropertyChangeListener( new PreviewUpdateHandler() );
    addPropertyChangeListener( "zoom", new UpdateZoomHandler() ); //$NON-NLS-1$
    statusListener = new PreviewPaneStatusListener();

    zoomSelectorBox = createZoomSelector( this );
    setReportController( new ParameterReportController() );
    if ( init ) {
      initializeWithoutJob();
    }
  }

  protected JComboBox createZoomSelector( final PreviewPane pane ) {
    final JComboBox zoomSelect = new JComboBox( pane.getZoomModel() );
    zoomSelect.addActionListener( new ZoomSelectAction( pane.getZoomModel(), pane ) );
    zoomSelect.setAlignmentX( Component.RIGHT_ALIGNMENT );
    return zoomSelect;
  }

  public PreviewDrawablePanel getReportPreviewArea() {
    return drawablePanel;
  }

  public boolean isDeferredRepagination() {
    return deferredRepagination;
  }

  public void setDeferredRepagination( final boolean deferredRepagination ) {
    this.deferredRepagination = deferredRepagination;
  }

  public synchronized PrintReportProcessor getPrintReportProcessor() {
    return printReportProcessor;
  }

  protected synchronized void setPrintReportProcessor( final PrintReportProcessor printReportProcessor ) {
    this.printReportProcessor = printReportProcessor;
  }

  public JMenu[] getMenu() {
    return menus;
  }

  protected void setMenu( final JMenu[] menus ) {
    if ( menus == null ) {
      throw new NullPointerException();
    }
    final JMenu[] oldmenu = this.menus;
    this.menus = (JMenu[]) menus.clone();
    firePropertyChange( PreviewPane.MENU_PROPERTY, oldmenu, this.menus );
  }

  public JToolBar getToolBar() {
    return toolBar;
  }

  public String getStatusText() {
    return statusText;
  }

  public void setStatusText( final String statusText ) {
    final String oldStatus = this.statusText;
    this.statusText = statusText;

    firePropertyChange( PreviewPane.STATUS_TEXT_PROPERTY, oldStatus, statusText );
  }

  public Throwable getError() {
    return error;
  }

  public void setError( final Throwable error ) {
    final Throwable oldError = this.error;
    this.error = error;
    firePropertyChange( PreviewPane.ERROR_PROPERTY, oldError, error );
  }

  public StatusType getStatusType() {
    return statusType;
  }

  public void setStatusType( final StatusType statusType ) {
    final StatusType oldType = this.statusType;
    this.statusType = statusType;

    firePropertyChange( PreviewPane.STATUS_TYPE_PROPERTY, oldType, statusType );
  }

  public ReportController getReportController() {
    return reportController;
  }

  public void setReportController( final ReportController reportController ) {
    final ReportController oldController = this.reportController;
    this.reportController = reportController;
    firePropertyChange( PreviewPane.REPORT_CONTROLLER_PROPERTY, oldController, reportController );

    if ( this.reportController != oldController ) {
      if ( oldController != null ) {
        oldController.deinitialize( this );
      }
      // Now add the controller to the GUI ..
      refreshReportController( reportController );
    }
  }

  private void refreshReportController( final ReportController newReportController ) {
    for ( int i = 0; i < outerReportControllerHolder.getComponentCount(); i++ ) {
      final Component maybeSplitPane = outerReportControllerHolder.getComponent( i );
      if ( maybeSplitPane instanceof JSplitPane ) {
        final JSplitPane splitPane = (JSplitPane) maybeSplitPane;
        reportControllerSliderSize = splitPane.getDividerLocation();
        break;
      }
    }

    if ( newReportController == null ) {
      if ( reportControllerComponent != null ) {
        // thats relatively easy.
        outerReportControllerHolder.removeAll();
        outerReportControllerHolder.add( toolbarHolder, BorderLayout.NORTH );
        outerReportControllerHolder.add( reportPaneScrollPane, BorderLayout.CENTER );
        reportControllerComponent = null;
        reportControllerInner = false;
        reportControllerLocation = null;
      }
    } else {
      final JComponent rcp = newReportController.getControlPanel();
      if ( rcp == null ) {
        if ( reportControllerComponent != null ) {
          outerReportControllerHolder.removeAll();
          outerReportControllerHolder.add( toolbarHolder, BorderLayout.NORTH );
          outerReportControllerHolder.add( reportPaneScrollPane, BorderLayout.CENTER );
          reportControllerComponent = null;
          reportControllerInner = false;
          reportControllerLocation = null;
        }
      } else if ( reportControllerComponent != rcp || reportControllerInner != newReportController.isInnerComponent()
          || ObjectUtilities.equal( reportControllerLocation, newReportController.getControllerLocation() ) == false ) {
        // if either the controller component or its position (inner vs outer)
        // and border-position has changed, then refresh ..
        this.reportControllerLocation = newReportController.getControllerLocation();
        this.reportControllerInner = newReportController.isInnerComponent();
        this.reportControllerComponent = newReportController.getControlPanel();

        outerReportControllerHolder.removeAll();
        if ( reportControllerInner ) {
          final JSplitPane innerHolder = new JSplitPane();
          innerHolder.setOpaque( false );
          if ( BorderLayout.SOUTH.equals( reportControllerLocation ) ) {
            innerHolder.setOrientation( JSplitPane.VERTICAL_SPLIT );
            innerHolder.setTopComponent( reportPaneScrollPane );
            innerHolder.setBottomComponent( reportControllerComponent );
          } else if ( BorderLayout.EAST.equals( reportControllerLocation ) ) {
            innerHolder.setOrientation( JSplitPane.HORIZONTAL_SPLIT );
            innerHolder.setLeftComponent( reportPaneScrollPane );
            innerHolder.setRightComponent( reportControllerComponent );
          } else if ( BorderLayout.WEST.equals( reportControllerLocation ) ) {
            innerHolder.setOrientation( JSplitPane.HORIZONTAL_SPLIT );
            innerHolder.setRightComponent( reportPaneScrollPane );
            innerHolder.setLeftComponent( reportControllerComponent );
          } else {
            innerHolder.setOrientation( JSplitPane.VERTICAL_SPLIT );
            innerHolder.setBottomComponent( reportPaneScrollPane );
            innerHolder.setTopComponent( reportControllerComponent );
          }

          if ( reportControllerSliderSize > 0 ) {
            innerHolder.setDividerLocation( reportControllerSliderSize );
          }
          outerReportControllerHolder.add( toolbarHolder, BorderLayout.NORTH );
          outerReportControllerHolder.add( innerHolder, BorderLayout.CENTER );
        } else {
          final JPanel reportPaneHolder = new JPanel();
          reportPaneHolder.setOpaque( false );
          reportPaneHolder.setLayout( new BorderLayout() );
          reportPaneHolder.add( toolbarHolder, BorderLayout.NORTH );
          reportPaneHolder.add( reportPaneScrollPane, BorderLayout.CENTER );

          final JSplitPane innerHolder = new JSplitPane();
          if ( BorderLayout.SOUTH.equals( reportControllerLocation ) ) {
            innerHolder.setOrientation( JSplitPane.VERTICAL_SPLIT );
            innerHolder.setTopComponent( reportPaneHolder );
            innerHolder.setBottomComponent( reportControllerComponent );
          } else if ( BorderLayout.EAST.equals( reportControllerLocation ) ) {
            innerHolder.setOrientation( JSplitPane.HORIZONTAL_SPLIT );
            innerHolder.setLeftComponent( reportPaneHolder );
            innerHolder.setRightComponent( reportControllerComponent );
          } else if ( BorderLayout.WEST.equals( reportControllerLocation ) ) {
            innerHolder.setOrientation( JSplitPane.HORIZONTAL_SPLIT );
            innerHolder.setRightComponent( reportPaneHolder );
            innerHolder.setLeftComponent( reportControllerComponent );
          } else {
            innerHolder.setOrientation( JSplitPane.VERTICAL_SPLIT );
            innerHolder.setBottomComponent( reportPaneHolder );
            innerHolder.setTopComponent( reportControllerComponent );
          }
          if ( reportControllerSliderSize > 0 ) {
            innerHolder.setDividerLocation( reportControllerSliderSize );
          }
          outerReportControllerHolder.add( innerHolder, BorderLayout.CENTER );
        }
      }
    }
  }

  public MasterReport getReportJob() {
    return reportJob;
  }

  public void setReportJob( final MasterReport reportJob ) {
    final MasterReport oldJob = this.reportJob;
    this.reportJob = reportJob;

    firePropertyChange( PreviewPane.REPORT_JOB_PROPERTY, oldJob, reportJob );

    if ( reportJob == null ) {
      killThePaginationWorker();
      setPaginated( false );
      setPageNumber( 0 );
      setNumberOfPages( 0 );
      refreshReportController( reportController );
      initializeWithoutJob();
    } else {
      refreshReportController( reportController );
      initializeFromReport();
    }

  }

  public double getZoom() {
    return zoom;
  }

  public void setZoom( final double zoom ) {
    final double oldZoom = this.zoom;
    this.zoom = Math.max( Math.min( zoom, maxZoom ), minZoom );
    if ( this.zoom != oldZoom ) {
      firePropertyChange( PreviewPane.ZOOM_PROPERTY, oldZoom, zoom );

      updateZoomModel( zoom );
    }
  }

  private void updateZoomModel( final double zoom ) {
    for ( int i = 0; i < zoomModel.getSize(); i++ ) {
      final Object o = zoomModel.getKeyAt( i );
      if ( o instanceof Double ) {
        Double d = (Double) o;
        if ( d.doubleValue() == zoom ) {
          zoomModel.setSelectedKey( d );
          return;
        }
      }
    }
    zoomModel.setSelectedItem( formatZoomText( zoom ) );
  }

  public boolean isClosed() {
    return closed;
  }

  public void setClosed( final boolean closed ) {
    final boolean oldClosed = this.closed;
    this.closed = closed;
    firePropertyChange( PreviewPane.CLOSED_PROPERTY, oldClosed, closed );
    if ( closed ) {
      prepareShutdown();
    }
  }

  private void prepareShutdown() {
    synchronized ( this ) {

      paginationWorker = null;

      if ( printReportProcessor != null ) {
        printReportProcessor.cancel();
        printReportProcessor.close();
        printReportProcessor = null;
      }
      closeToolbar();
    }
  }

  private int getUserDefinedCategoryPosition() {
    return ParserUtil.parseInt( swingGuiContext.getConfiguration().getConfigProperty(
        "org.pentaho.reporting.engine.classic.core.modules.gui.swing.user-defined-category.position" ), 15000 ); //$NON-NLS-1$
  }

  public Locale getLocale() {
    if ( getParent() == null ) {
      try {
        return super.getLocale();
      } catch ( IllegalComponentStateException ex ) {
        return Locale.getDefault();
      }
    }
    return super.getLocale();
  }

  public int getNumberOfPages() {
    return numberOfPages;
  }

  public void setNumberOfPages( final int numberOfPages ) {
    final int oldPageNumber = this.numberOfPages;
    this.numberOfPages = numberOfPages;
    firePropertyChange( PreviewPane.NUMBER_OF_PAGES_PROPERTY, oldPageNumber, numberOfPages );
  }

  public int getPageNumber() {
    return pageNumber;
  }

  public void setPageNumber( final int pageNumber ) {
    final int oldPageNumber = this.pageNumber;
    this.pageNumber = pageNumber;
    // Log.debug("Setting PageNumber: " + pageNumber);
    firePropertyChange( PreviewPane.PAGE_NUMBER_PROPERTY, oldPageNumber, pageNumber );
  }

  public IconTheme getIconTheme() {
    return iconTheme;
  }

  protected void setIconTheme( final IconTheme theme ) {
    final IconTheme oldTheme = this.iconTheme;
    this.iconTheme = theme;
    firePropertyChange( PreviewPane.ICON_THEME_PROPERTY, oldTheme, theme );
  }

  protected void initializeFromReport() {
    final PageDefinition pageDefinition = reportJob.getPageDefinition();
    if ( pageDefinition.getPageCount() > 0 ) {
      final PageFormat pageFormat = pageDefinition.getPageFormat( 0 );
      pageDrawable.setDefaultWidth( (int) pageFormat.getWidth() );
      pageDrawable.setDefaultHeight( (int) pageFormat.getHeight() );
    }

    if ( reportJob.getTitle() == null ) {
      setTitle( messages.getString( "PreviewPane.EMPTY_TITLE" ) ); //$NON-NLS-1$
    } else {
      setTitle( messages.getString( "PreviewPane.PREVIEW_TITLE", reportJob.getTitle() ) ); //$NON-NLS-1$
    }

    final Configuration configuration = reportJob.getConfiguration();
    setIconTheme( PreviewPaneUtilities.createIconTheme( configuration ) );

    performInitialization( configuration );

    if ( deferredRepagination == false ) {
      startPagination();
    }
  }

  protected void initializeWithoutJob() {
    if ( printReportProcessor != null ) {
      printReportProcessor.close();
      printReportProcessor = null;
    }

    setTitle( messages.getString( "PreviewPane.EMPTY_TITLE" ) ); //$NON-NLS-1$
    final Configuration configuration = ClassicEngineBoot.getInstance().getGlobalConfig();
    setIconTheme( PreviewPaneUtilities.createIconTheme( configuration ) );
    performInitialization( configuration );
  }

  private synchronized void performInitialization( final Configuration configuration ) {
    if ( performInitializationRunning ) {
      throw new IllegalStateException( "This method is not re-entrant" );
    }

    try {
      performInitializationRunning = true;
      applyDefinedDimension( configuration );

      final Double key = zoomModel.getSelectedKey();
      zoomModel.clear();
      for ( int i = 0; i < PreviewPane.ZOOM_FACTORS.length; i++ ) {
        zoomModel.add( new Double( PreviewPane.ZOOM_FACTORS[i] ), formatZoomText( PreviewPane.ZOOM_FACTORS[i] ) );
      }
      if ( key == null ) {
        updateZoomModel( zoom );
      } else {
        zoomModel.setSelectedKey( key );
      }

      if ( this.actionPlugins != null ) {
        for ( final ActionPlugin[] plugins : this.actionPlugins.values() ) {
          for ( int i = 0; i < plugins.length; i++ ) {
            final ActionPlugin plugin = plugins[i];
            plugin.deinitialize( swingGuiContext );
            plugins[i] = null;
          }
        }
        this.actionPlugins = null;
      }

      this.actionPlugins = PreviewPaneUtilities.loadActions( swingGuiContext );

      if ( "true".equals( configuration.getConfigProperty( PreviewPane.MENUBAR_AVAILABLE_KEY ) ) ) { //$NON-NLS-1$
        buildMenu();
      } else {
        setMenu( PreviewPane.EMPTY_MENU );
      }

      if ( toolBar != null ) {
        toolbarHolder.remove( toolBar );
      }
      if ( "true".equals( configuration.getConfigProperty( PreviewPane.TOOLBAR_AVAILABLE_KEY ) ) ) { //$NON-NLS-1$
        final boolean floatable =
            isToolbarFloatable()
                || "true".equals( configuration.getConfigProperty( PreviewPane.TOOLBAR_FLOATABLE_KEY ) ); //$NON-NLS-1$
        toolBar = buildToolbar( floatable );
      } else {
        toolBar = null;
      }
      if ( toolBar != null ) {
        toolbarHolder.add( toolBar, BorderLayout.NORTH );
      }
    } finally {
      performInitializationRunning = false;
    }
  }

  /**
   * Read the defined dimensions from the report's configuration and set them to the Dialog. If a maximum size is
   * defined, add a WindowSizeLimiter to check the maximum size
   *
   * @param configuration
   *          the report-configuration of this dialog.
   */
  private void applyDefinedDimension( final Configuration configuration ) {
    String width = configuration.getConfigProperty( PreviewPane.PREVIEW_PREFERRED_WIDTH );
    String height = configuration.getConfigProperty( PreviewPane.PREVIEW_PREFERRED_HEIGHT );

    // only apply if both values are set.
    if ( width != null && height != null ) {
      try {
        final Dimension pref = createCorrectedDimensions( Integer.parseInt( width ), Integer.parseInt( height ) );
        setPreferredSize( pref );
      } catch ( Exception nfe ) {
        PreviewPane.logger.warn( "Preferred viewport size is defined, but the specified values are invalid." ); //$NON-NLS-1$
      }
    }

    width = configuration.getConfigProperty( PreviewPane.PREVIEW_MAXIMUM_WIDTH );
    height = configuration.getConfigProperty( PreviewPane.PREVIEW_MAXIMUM_HEIGHT );

    removeComponentListener( sizeLimiter );

    // only apply if at least one value is set.
    if ( width != null || height != null ) {
      try {
        final int iWidth = ( width == null ) ? Short.MAX_VALUE : (int) parseRelativeFloat( width );
        final int iHeight = ( height == null ) ? Short.MAX_VALUE : (int) parseRelativeFloat( height );
        final Dimension pref = createCorrectedDimensions( iWidth, iHeight );
        setMaximumSize( pref );
        addComponentListener( sizeLimiter );
      } catch ( Exception nfe ) {
        PreviewPane.logger.warn( "Maximum viewport size is defined, but the specified values are invalid." ); //$NON-NLS-1$
      }
    }
  }

  protected float parseRelativeFloat( final String value ) {
    if ( value == null ) {
      throw new NumberFormatException();
    }
    final String tvalue = value.trim();
    if ( tvalue.length() > 0 && tvalue.charAt( tvalue.length() - 1 ) == '%' ) { //$NON-NLS-1$
      final String number = tvalue.substring( 0, tvalue.length() - 1 ); //$NON-NLS-1$
      return Float.parseFloat( number ) * -1.0f;
    } else {
      return Float.parseFloat( tvalue );
    }
  }

  /**
   * Correct the given width and height. If the values are negative, the height and width is considered a proportional
   * value where -100 corresponds to 100%. The proportional attributes are given is relation to the screen width and
   * height.
   *
   * @param w
   *          the to be corrected width
   * @param h
   *          the height that should be corrected
   * @return the dimension of width and height, where all relative values are normalized.
   */
  private Dimension createCorrectedDimensions( int w, int h ) {
    final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    if ( w < 0 ) {
      w = ( w * screenSize.width / -100 );
    }
    if ( h < 0 ) {
      h = ( h * screenSize.height / -100 );
    }
    return new Dimension( w, h );
  }

  /**
   * Gets a list of Actions to add to the toolbar ahead of all other toolbar actions. Left protected for subclasses to
   * override.
   *
   * @return Action[] an array of javax.swing.Action objects
   */
  protected Action[] getToolbarPreActions() {
    return new Action[] {};
  }

  protected JToolBar buildToolbar( final boolean floatable ) {
    toolBar = new JToolBar();
    toolBar.setFloatable( floatable );

    final Action[] preActions = getToolbarPreActions();
    if ( preActions != null && preActions.length > 0 ) {
      for ( int i = 0; i < preActions.length; i++ ) {
        toolBar.add( preActions[i] );
      }
      toolBar.addSeparator();
    }

    final ArrayList<ActionPlugin> list = new ArrayList<ActionPlugin>();
    for ( final ActionPlugin[] plugins : actionPlugins.values() ) {
      list.addAll( Arrays.asList( plugins ) );
    }
    final ActionPlugin[] plugins = list.toArray( new ActionPlugin[list.size()] );
    Arrays.sort( plugins, new ActionPluginComparator() );
    PreviewPaneUtilities.addActionsToToolBar( toolBar, plugins, zoomSelectorBox, this );
    return toolBar;
  }

  public void setToolbarFloatable( final boolean toolbarFloatable ) {
    this.toolbarFloatable = toolbarFloatable;
  }

  public boolean isToolbarFloatable() {
    return toolbarFloatable;
  }

  private void closeToolbar() {
    if ( toolBar == null ) {
      return;
    }
    if ( toolBar.getParent() != toolbarHolder ) {
      // ha!, we detected that the toolbar is floating ...
      // Log.debug (currentToolbar.getParent());
      final Window w = SwingUtilities.windowForComponent( toolBar );
      if ( w != null ) {
        w.setVisible( false );
        w.dispose();
      }
    }
    toolBar.setVisible( false );
  }

  public SwingGuiContext getSwingGuiContext() {
    return swingGuiContext;
  }

  public KeyedComboBoxModel<Double, String> getZoomModel() {
    return zoomModel;
  }

  protected final String formatZoomText( final double zoom ) {
    final NumberFormat numberFormat = NumberFormat.getPercentInstance( swingGuiContext.getLocale() );
    return ( numberFormat.format( zoom ) );
  }

  private void buildMenu() {
    for ( final ZoomAction[] zoomActions : this.zoomActions.values() ) {
      for ( int i = 0; i < zoomActions.length; i++ ) {
        final ZoomAction zoomAction = zoomActions[i];
        zoomAction.deinitialize();
      }
    }
    this.zoomActions.clear();

    final HashMap<ActionCategory, JMenu> menus = new HashMap<ActionCategory, JMenu>();
    final int userPos = getUserDefinedCategoryPosition();

    boolean insertedUserDefinedActions = false;
    final ArrayList<ActionCategory> collectedCategories = new ArrayList<ActionCategory>();
    for ( final Map.Entry<ActionCategory, ActionPlugin[]> entry : actionPlugins.entrySet() ) {
      final ActionCategory cat = entry.getKey();
      collectedCategories.add( cat );

      final ActionPlugin[] plugins = entry.getValue();
      if ( plugins.length > 0 ) {
        if ( plugins[0] == null ) {
          throw new NullPointerException();
        }
      }

      if ( insertedUserDefinedActions == false && cat.getPosition() > userPos ) {
        final ReportController controller = getReportController();
        if ( controller != null ) {
          controller.initialize( this );
          final JMenu[] controlerMenus = controller.getMenus();
          for ( int i = 0; i < controlerMenus.length; i++ ) {
            final ActionCategory userCategory = new ActionCategory();
            userCategory.setName( "X-User-Category-" + i ); //$NON-NLS-1$
            userCategory.setPosition( userPos + i );
            userCategory.setUserDefined( true );
            menus.put( userCategory, controlerMenus[i] );
            collectedCategories.add( userCategory );
          }
        }

        insertedUserDefinedActions = true;
      }

      final JMenu menu = PreviewPaneUtilities.createMenu( cat );
      zoomActions.put( cat, PreviewPaneUtilities.buildMenu( menu, plugins, this ) );
      menus.put( cat, menu );
    }

    final ActionCategory[] categories = collectedCategories.toArray( new ActionCategory[collectedCategories.size()] );
    final CategoryTreeItem[] categoryTreeItems = PreviewPaneUtilities.buildMenuTree( categories );

    final ArrayList<CategoryTreeItem> menuList = new ArrayList<CategoryTreeItem>();
    for ( int i = 0; i < categoryTreeItems.length; i++ ) {
      final CategoryTreeItem item = categoryTreeItems[i];
      final JMenu menu = menus.get( item.getCategory() );
      // now connect all menus ..
      final CategoryTreeItem[] childs = item.getChilds();
      Arrays.sort( childs );
      for ( int j = 0; j < childs.length; j++ ) {
        final CategoryTreeItem child = childs[j];
        final JMenu childMenu = menus.get( child.getCategory() );
        if ( childMenu != null ) {
          menu.add( childMenu );
        }
      }

      if ( item.getParent() == null ) {
        menuList.add( item );
      }
    }

    Collections.sort( menuList );
    final ArrayList<JMenu> retval = new ArrayList<JMenu>();
    for ( int i = 0; i < menuList.size(); i++ ) {
      final CategoryTreeItem item = menuList.get( i );
      final JMenu menu = menus.get( item.getCategory() );
      if ( item.getCategory().isUserDefined() || menu.getItemCount() > 0 ) {
        retval.add( menu );
      }
    }

    setMenu( retval.toArray( new JMenu[retval.size()] ) );
  }

  public String getTitle() {
    return title;
  }

  public void setTitle( final String title ) {
    final String oldTitle = this.title;
    this.title = title;
    firePropertyChange( PreviewPane.TITLE_PROPERTY, oldTitle, title );
  }

  public double[] getZoomFactors() {
    return (double[]) PreviewPane.ZOOM_FACTORS.clone();
  }

  public boolean isPaginated() {
    return paginated;
  }

  public void setPaginated( final boolean paginated ) {
    final boolean oldPaginated = this.paginated;
    this.paginated = paginated;
    firePropertyChange( PreviewPane.PAGINATED_PROPERTY, oldPaginated, paginated );
  }

  public boolean isPaginating() {
    return paginating;
  }

  public void setPaginating( final boolean paginating ) {
    final boolean oldPaginating = this.paginating;
    this.paginating = paginating;
    firePropertyChange( PreviewPane.PAGINATING_PROPERTY, oldPaginating, paginating );
  }

  public synchronized void startPagination() {
    killThePaginationWorker();

    if ( printReportProcessor != null ) {
      printReportProcessor.close();
      printReportProcessor = null;
    }
    // Reset the pagination to automatic mode now. This way changes to the page-format will trigger
    // pagination again in a safe manor.
    deferredRepagination = false;

    try {
      final MasterReport reportJob = getReportJob();
      printReportProcessor = new PrintReportProcessor( reportJob );

      paginationWorker = createWorker();
      paginationWorker.setWorkload( new RepaginationRunnable( printReportProcessor ) );
    } catch ( ReportProcessingException e ) {
      PreviewPane.logger.error( "Unable to start report pagination:", e ); // NON-NLS
      setStatusType( StatusType.ERROR );
      setStatusText( messages.getString( "PreviewPane.ERROR_ON_PAGINATION" ) );
    }

  }

  private void killThePaginationWorker() {

    if ( printReportProcessor != null ) {
      printReportProcessor.cancel();
    }

    if ( paginationWorker != null ) {
      // make sure that old pagination handler does not run longer than
      // necessary..
      // noinspection SynchronizeOnNonFinalField
      final Worker paginationWorker = this.paginationWorker;

      while ( paginationWorker.isAvailable() == false && paginationWorker.isFinish() == false ) {
        try {
          synchronized ( paginationWorker ) {
            paginationWorker.wait( 500 );
          }
        } catch ( InterruptedException e ) {
          // Got interrupted while waiting ...
        }
      }
      this.paginationWorker = null;
    }
  }

  protected Worker createWorker() {
    return new Worker();
  }

  public Object getNoReportDrawable() {
    return noReportDrawable;
  }

  public void setNoReportDrawable( final Object noReportDrawable ) {
    this.noReportDrawable = noReportDrawable;
  }

  public Object getPaginatingDrawable() {
    return paginatingDrawable;
  }

  public void setPaginatingDrawable( final Object paginatingDrawable ) {
    this.paginatingDrawable = paginatingDrawable;
  }

  protected void updateVisiblePage( final int pageNumber ) {
    //
    if ( printReportProcessor == null ) {
      throw new IllegalStateException();
    }

    // todo: This can be very expensive - so we better move this off the event-dispatcher
    final int pageIndex = getPageNumber() - 1;
    if ( pageIndex < 0 || pageIndex >= printReportProcessor.getNumberOfPages() ) {
      this.pageDrawable.setBackend( null );
      this.drawablePanel.setDrawableAsRawObject( pageDrawable );
    } else {
      final PageDrawable drawable = printReportProcessor.getPageDrawable( pageIndex );
      this.pageDrawable.setBackend( drawable );
      this.drawablePanel.setDrawableAsRawObject( pageDrawable );
    }
  }

  protected StatusListener getStatusListener() {
    return statusListener;
  }

  public void addReportProgressListener( final ReportProgressListener progressListener ) {
    if ( progressListener == null ) {
      throw new NullPointerException();
    }

    if ( reportProgressListener == null ) {
      reportProgressListener = new ArrayList();
    }
    reportProgressListener.add( progressListener );
  }

  public void removeReportProgressListener( final ReportProgressListener progressListener ) {
    if ( reportProgressListener == null ) {
      return;
    }
    reportProgressListener.remove( progressListener );
  }

  protected void forwardReportStartedEvent( final ReportProgressEvent event ) {
    if ( reportProgressListener == null ) {
      return;
    }
    for ( int i = 0; i < reportProgressListener.size(); i++ ) {
      final ReportProgressListener listener = (ReportProgressListener) reportProgressListener.get( i );
      listener.reportProcessingStarted( event );
    }
  }

  protected void forwardReportUpdateEvent( final ReportProgressEvent event ) {
    if ( reportProgressListener == null ) {
      return;
    }
    for ( int i = 0; i < reportProgressListener.size(); i++ ) {
      final ReportProgressListener listener = (ReportProgressListener) reportProgressListener.get( i );
      listener.reportProcessingUpdate( event );
    }
  }

  protected void forwardReportFinishedEvent( final ReportProgressEvent event ) {
    if ( reportProgressListener == null ) {
      return;
    }
    for ( int i = 0; i < reportProgressListener.size(); i++ ) {
      final ReportProgressListener listener = (ReportProgressListener) reportProgressListener.get( i );
      listener.reportProcessingFinished( event );
    }
  }

  private double getMaximumZoom( final Configuration configuration ) {
    final String value = configuration.getConfigProperty( PreviewPane.ZOOM_MAXIMUM_KEY );
    return ParserUtil.parseFloat( value, PreviewPane.ZOOM_MAXIMUM_DEFAULT );
  }

  private double getMinimumZoom( final Configuration configuration ) {
    final String value = configuration.getConfigProperty( PreviewPane.ZOOM_MINIMUM_KEY );
    return ParserUtil.parseFloat( value, PreviewPane.ZOOM_MINIMUM_DEFAULT );
  }

  public void addReportHyperlinkListener( final ReportHyperlinkListener listener ) {
    if ( listener == null ) {
      throw new NullPointerException();
    }
    if ( hyperlinkListeners == null ) {
      hyperlinkListeners = new ArrayList();
    }
    hyperlinkListeners.add( listener );
    cachedHyperlinkListeners = null;
  }

  public void removeReportHyperlinkListener( final ReportHyperlinkListener listener ) {
    if ( listener == null ) {
      throw new NullPointerException();
    }
    if ( hyperlinkListeners == null ) {
      return;
    }
    hyperlinkListeners.remove( listener );
    cachedHyperlinkListeners = null;
  }

  protected boolean isHyperlinkSystemActive() {
    if ( hyperlinkListeners == null ) {
      return false;
    }
    return hyperlinkListeners.isEmpty() == false;
  }

  protected void fireReportHyperlinkEvent( final ReportHyperlinkEvent event ) {
    if ( hyperlinkListeners == null ) {
      return;
    }

    if ( cachedHyperlinkListeners == null ) {
      cachedHyperlinkListeners =
          (ReportHyperlinkListener[]) hyperlinkListeners
              .toArray( new ReportHyperlinkListener[hyperlinkListeners.size()] );
    }
    final ReportHyperlinkListener[] myListeners = cachedHyperlinkListeners;
    for ( int i = 0; i < myListeners.length; i++ ) {
      final ReportHyperlinkListener listener = myListeners[i];
      listener.hyperlinkActivated( event );
    }
  }

  protected Configuration computeContextConfiguration() {
    final MasterReport report = getReportJob();
    if ( report != null ) {
      return report.getConfiguration();
    }
    return ClassicEngineBoot.getInstance().getGlobalConfig();
  }
}
