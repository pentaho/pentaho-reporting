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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.designer.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.designer.core.actions.elements.InsertElementAction;
import org.pentaho.reporting.designer.core.actions.elements.MorphAction;
import org.pentaho.reporting.designer.core.actions.elements.barcode.BarcodeTypeAction;
import org.pentaho.reporting.designer.core.actions.global.AboutAction;
import org.pentaho.reporting.designer.core.actions.global.OpenReportAction;
import org.pentaho.reporting.designer.core.actions.global.OpenSampleReportAction;
import org.pentaho.reporting.designer.core.actions.global.QuitAction;
import org.pentaho.reporting.designer.core.actions.global.SelectTabAction;
import org.pentaho.reporting.designer.core.actions.global.SettingsAction;
import org.pentaho.reporting.designer.core.actions.report.CloseAllReportsAction;
import org.pentaho.reporting.designer.core.actions.report.CloseChildReportsAction;
import org.pentaho.reporting.designer.core.actions.report.CloseOtherReportsAction;
import org.pentaho.reporting.designer.core.actions.report.CloseReportAction;
import org.pentaho.reporting.designer.core.actions.report.CloseUnmodifiedReportsAction;
import org.pentaho.reporting.designer.core.editor.ContextMenuUtility;
import org.pentaho.reporting.designer.core.editor.ElementPropertiesPanel;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.editor.ReportRendererComponent;
import org.pentaho.reporting.designer.core.editor.fieldselector.FieldSelectorPaletteDialog;
import org.pentaho.reporting.designer.core.editor.palette.PaletteButton;
import org.pentaho.reporting.designer.core.frame.RecentFilesUpdateHandler;
import org.pentaho.reporting.designer.core.inspections.InspectionSidePanePanel;
import org.pentaho.reporting.designer.core.settings.SettingsListener;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.designer.core.status.StatusBar;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.designer.core.util.docking.Category;
import org.pentaho.reporting.designer.core.util.docking.GlobalPane;
import org.pentaho.reporting.designer.core.util.docking.InternalWindow;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.designer.core.welcome.SamplesTreeBuilder;
import org.pentaho.reporting.designer.core.welcome.WelcomePane;
import org.pentaho.reporting.designer.core.widgets.FancyTabbedPane;
import org.pentaho.reporting.designer.core.widgets.TabRenderer;
import org.pentaho.reporting.designer.core.xul.ActionSwingMenuitem;
import org.pentaho.reporting.designer.core.xul.XulDesignerFrame;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementTypeRegistry;
import org.pentaho.reporting.engine.classic.core.metadata.GroupedMetaDataComparator;
import org.pentaho.reporting.engine.classic.core.metadata.MaturityLevel;
import org.pentaho.reporting.engine.classic.extensions.modules.sbarcodes.BarcodeTypePropertyEditor;
import org.pentaho.reporting.libraries.base.util.DebugLog;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.designtime.swing.MacOSXIntegration;
import org.pentaho.ui.xul.XulComponent;
import org.pentaho.ui.xul.XulException;
import org.pentaho.ui.xul.components.XulMenuitem;
import org.pentaho.ui.xul.containers.XulMenupopup;
import org.pentaho.ui.xul.swing.tags.SwingMenuseparator;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.TabbedPaneUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import java.awt.*;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ReportDesignerFrame extends JFrame {
  /**
   * Class for handling cursor changes during drags See PRD-1674
   */
  public class DefaultDropTargetListener extends DropTargetAdapter {
    public void dragEnter( final DropTargetDragEvent dtde ) {
      final DropTarget dropTarget = (DropTarget) dtde.getSource();
      dropTarget.getComponent().setCursor( DragSource.DefaultCopyNoDrop );
    }

    public void dragExit( final DropTargetEvent dte ) {
      final DropTarget dropTarget = (DropTarget) dte.getSource();
      dropTarget.getComponent().setCursor( Cursor.getDefaultCursor() );
    }

    public void drop( final DropTargetDropEvent dtde ) {
      final DropTarget dropTarget = (DropTarget) dtde.getSource();
      dropTarget.getComponent().setCursor( Cursor.getDefaultCursor() );
      dtde.rejectDrop();
    }
  }

  private static class ScrollbarSyncHandler extends ComponentAdapter {
    private final JScrollBar theVerticalScrollBar;
    private final JToolBar toolBar;

    public ScrollbarSyncHandler( final JScrollBar theVerticalScrollBar, final JToolBar toolBar ) {
      this.theVerticalScrollBar = theVerticalScrollBar;
      this.toolBar = toolBar;
    }

    public void componentResized( final ComponentEvent e ) {
      if ( theVerticalScrollBar.isShowing() ) {
        toolBar.setBorder( BorderFactory.createEmptyBorder( 25, 0, 0, 19 ) );
      } else {
        toolBar.setBorder( BorderFactory.createEmptyBorder( 25, 0, 0, 4 ) );
      }
    }
  }

  private class WindowCloseHandler extends WindowAdapter {
    private WindowCloseHandler() {
    }

    /**
     * Invoked when a window is in the process of being closed. The close operation can be overridden at this point.
     */
    public void windowClosing( final WindowEvent e ) {
      final QuitAction quitAction = new QuitAction();
      quitAction.setReportDesignerContext( context );
      quitAction.actionPerformed( new ActionEvent( this, 0, "quit", 0 ) ); // NON-NLS
    }
  }

  private class ReportTabActivationHandler implements ChangeListener, PropertyChangeListener {
    private ReportTabActivationHandler() {
    }

    public void propertyChange( final PropertyChangeEvent evt ) {
      final ReportDesignerDocumentContext activeContext = getContext().getActiveDocument();
      final JTabbedPane editorPanes = getReportEditorPane();
      final int i = findTabForContext( activeContext );
      if ( i != -1 && editorPanes.getSelectedIndex() != i ) {
        editorPanes.setSelectedIndex( i );
      }

      updateFrameTitle();
    }

    private int findTabForContext( final ReportDesignerDocumentContext activeContext ) {
      final JTabbedPane editorPanes = getReportEditorPane();
      final int count = editorPanes.getTabCount();
      for ( int i = 0; i < count; i++ ) {
        final Component c = editorPanes.getComponentAt( i );
        if ( c instanceof ReportRendererComponent ) {
          final ReportRendererComponent rrc = (ReportRendererComponent) c;
          if ( rrc.getRenderContext() == activeContext ) {
            return i;
          }
        }
      }
      return -1;
    }

    /**
     * Invoked when the target of the listener has changed its state.
     *
     * @param e a ChangeEvent object
     */
    public void stateChanged( final ChangeEvent e ) {
      final JTabbedPane editorPanes = getReportEditorPane();
      final DefaultReportDesignerContext context = getContext();
      if ( editorPanes.getTabCount() == 0 ) {
        context.setActiveDocument( null );
        return;
      }

      final int index = editorPanes.getSelectedIndex();
      if ( index == -1 || editorPanes.getComponentAt( index ) instanceof ReportRendererComponent == false ) {
        context.setActiveDocument( null );
      } else {
        // try to sync the context.
        final ReportRendererComponent rendererComponent = (ReportRendererComponent) editorPanes.getComponentAt( index );
        final ReportDocumentContext rendererContext = rendererComponent.getRenderContext();
        for ( int i = 0; i < context.getReportRenderContextCount(); i++ ) {
          final ReportRenderContext context1 = context.getReportRenderContext( i );
          if ( context1 == rendererContext ) {
            context.setActiveDocument( rendererContext );
            rendererContext.onDocumentActivated();
            return;
          }
        }

        if ( context.getReportRenderContextCount() > 0 ) {
          // we couldn't find a context, but we do at least have a context
          // instead of blowing away the context, let's grab one and
          // attempt to use it
          // this works around PRD-1609
          context.setActiveDocument( context.getReportRenderContext( context.getReportRenderContextCount() - 1 ) );
        } else {
          // not found, so we are probably in a remove at the moment.
          context.setActiveDocument( null );
        }
      }
    }
  }

  private class StatusTextHandler implements PropertyChangeListener {
    private StatusTextHandler() {
    }

    /**
     * This method gets called when a bound property is changed.
     *
     * @param evt A PropertyChangeEvent object describing the event source and the property that has changed.
     */

    public void propertyChange( final PropertyChangeEvent evt ) {
      getStatusBar().setGeneralInfoText( getContext().getStatusText() );
    }
  }

  private class ReportEditorContextHandler implements PropertyChangeListener {
    private ReportEditorContextHandler() {
    }

    /**
     * This method gets called when a bound property is changed.
     *
     * @param evt A PropertyChangeEvent object describing the event source and the property that has changed.
     */

    public void propertyChange( final PropertyChangeEvent evt ) {
      if ( ReportDesignerContext.REPORT_RENDER_CONTEXT_PROPERTY.equals( evt.getPropertyName() ) == false ) {
        return;
      }

      if ( !( evt instanceof IndexedPropertyChangeEvent ) ) {
        throw new UnsupportedOperationException();
      }

      final JTabbedPane editorPanes = getReportEditorPane();
      final IndexedPropertyChangeEvent ievt = (IndexedPropertyChangeEvent) evt;
      if ( ievt.getNewValue() != null ) {
        // added
        final ReportRenderContext renderContext = (ReportRenderContext) ievt.getNewValue();
        DefaultReportDesignerContext reportDesignerContext = getContext();
        final ReportRendererComponent rendererComponent =
          new ReportRendererComponent( reportDesignerContext, renderContext );

        final String title = renderContext.getTabName();
        final Icon icon = renderContext.getIcon();
        final TabRenderer tabRenderer = new TabRenderer( icon, title, reportDesignerContext, editorPanes );
        editorPanes.addTab( title, null, rendererComponent );
        editorPanes.setTabComponentAt( editorPanes.getTabCount() - 1, tabRenderer );
        editorPanes.setSelectedComponent( rendererComponent );
        renderContext.addPropertyChangeListener( "tabName",
          new DocumentNameChangeHandler( renderContext, tabRenderer, editorPanes.getTabCount() - 1 ) );

      } else if ( ievt.getOldValue() != null ) {
        if ( editorPanes.getTabCount() <= 0 ) {
          return;
        }
        // removed
        int index = ievt.getIndex();
        final ReportRenderContext reportRenderContext = (ReportRenderContext) ievt.getOldValue();
        boolean removedTab = false;

        for ( int i = 0; i < editorPanes.getTabCount(); i++ ) {
          final Component tabContent = editorPanes.getComponentAt( i );
          if ( tabContent instanceof ReportRendererComponent ) {
            final ReportRendererComponent myReportRenderer = (ReportRendererComponent) tabContent;
            if ( myReportRenderer.getRenderContext() == reportRenderContext ) {
              index = i;
              editorPanes.removeTabAt( index );
              removedTab = true;
              myReportRenderer.dispose();
              break;
            }
          }
        }

        // if we didn't remove a tab, the tab was probably not a report, so remove it by index
        if ( !removedTab ) {
          editorPanes.removeTabAt( index );
        }

        // unregister the listeners ...
        if ( editorPanes.getTabCount() > 0 ) {
          if ( index < editorPanes.getTabCount() ) {
            editorPanes.setSelectedIndex( index );
          } else {
            editorPanes.setSelectedIndex( editorPanes.getTabCount() - 1 );
          }
        }
      }

      recomputeAllTabTitles();
      rebuildReportMenu();
    }
  }

  private class ReportTabPanePopupHandler extends MouseAdapter {
    private ReportTabPanePopupHandler() {
    }

    private void handlePopup( final MouseEvent e ) {
      final JTabbedPane reportEditorPane = getReportEditorPane();
      final TabbedPaneUI ui = reportEditorPane.getUI();
      final int tabIndex = ui.tabForCoordinate( reportEditorPane, e.getX(), e.getY() );
      final JPopupMenu popupMenu = new JPopupMenu();

      final CloseReportAction closeThisAction = new CloseReportAction( tabIndex );
      closeThisAction.setReportDesignerContext( getContext() );
      final CloseChildReportsAction closeChildsAction = new CloseChildReportsAction( tabIndex );
      closeChildsAction.setReportDesignerContext( getContext() );
      final CloseOtherReportsAction closeOthersAction = new CloseOtherReportsAction( tabIndex );
      closeOthersAction.setReportDesignerContext( getContext() );
      final CloseAllReportsAction closeAllAction = new CloseAllReportsAction();
      closeAllAction.setReportDesignerContext( getContext() );
      final CloseUnmodifiedReportsAction closeUnmodifiedReportsAction = new CloseUnmodifiedReportsAction();
      closeUnmodifiedReportsAction.setReportDesignerContext( getContext() );

      popupMenu.add( new JMenuItem( closeThisAction ) );
      popupMenu.addSeparator();
      popupMenu.add( new JMenuItem( closeChildsAction ) );
      popupMenu.add( new JMenuItem( closeUnmodifiedReportsAction ) );
      popupMenu.add( new JMenuItem( closeOthersAction ) );
      popupMenu.add( new JMenuItem( closeAllAction ) );
      popupMenu.show( reportEditorPane, e.getX(), e.getY() );
    }

    public void mouseClicked( final MouseEvent e ) {
      if ( e.isPopupTrigger() ) {
        handlePopup( e );
      }
    }

    public void mousePressed( final MouseEvent e ) {
      if ( e.isPopupTrigger() ) {
        handlePopup( e );
      }
    }

    public void mouseReleased( final MouseEvent e ) {
      if ( e.isPopupTrigger() ) {
        handlePopup( e );
      }
    }
  }

  private class FrameViewController implements ReportDesignerView {
    private PropertyChangeSupport propertyChangeSupport;
    private XulDesignerFrame xulDesignerFrame;
    private Component parent;

    private FrameViewController( final Component parent ) {
      this.propertyChangeSupport = new PropertyChangeSupport( this );
      this.parent = parent;
    }

    private void initializeXulDesignerFrame( final ReportDesignerContext context ) throws XulException {
      this.xulDesignerFrame = new XulDesignerFrame();
      this.xulDesignerFrame.setReportDesignerContext( context );
    }

    public void addPropertyChangeListener( final PropertyChangeListener listener ) {
      propertyChangeSupport.addPropertyChangeListener( listener );
    }

    public void removePropertyChangeListener( final PropertyChangeListener listener ) {
      propertyChangeSupport.removePropertyChangeListener( listener );
    }

    public void addPropertyChangeListener( final String propertyName, final PropertyChangeListener listener ) {
      propertyChangeSupport.addPropertyChangeListener( propertyName, listener );
    }

    public void removePropertyChangeListener( final String propertyName, final PropertyChangeListener listener ) {
      propertyChangeSupport.removePropertyChangeListener( propertyName, listener );
    }

    public boolean isStructureVisible() {
      final Category reportTreeToolWindow = getReportTreeToolWindow();
      return reportTreeToolWindow != null && reportTreeToolWindow.isMinimized() == false;
    }

    public void setStructureVisible( final boolean visible ) {
      final Category reportTreeToolWindow = getReportTreeToolWindow();
      final boolean oldValue = reportTreeToolWindow.isMinimized() == false;
      reportTreeToolWindow.setMinimized( visible == false );
      propertyChangeSupport.firePropertyChange( STRUCTURE_VISIBLE_PROPERTY, oldValue, visible );
    }

    public boolean isPropertiesEditorVisible() {
      final Category attributeToolWindow = getAttributeToolWindow();
      if ( attributeToolWindow == null ) {
        return false;
      }
      return attributeToolWindow.isMinimized() == false;
    }

    public void setPropertiesEditorVisible( final boolean visible ) {
      final Category attributeToolWindow = getAttributeToolWindow();
      final boolean oldValue = attributeToolWindow.isMinimized() == false;
      attributeToolWindow.setMinimized( visible == false );
      propertyChangeSupport.firePropertyChange( PROPERTIES_EDITOR_VISIBLE_PROPERTY, oldValue, visible );
    }

    public boolean isMessagesVisible() {
      final Category inspectionsToolWindow = getInspectionsToolWindow();
      return inspectionsToolWindow != null && !inspectionsToolWindow.isMinimized();
    }

    public void setMessagesVisible( final boolean visible ) {
      final Category inspectionsToolWindow = getInspectionsToolWindow();
      final boolean oldValue = inspectionsToolWindow.isMinimized() == false;
      inspectionsToolWindow.setMinimized( visible == false );
      propertyChangeSupport.firePropertyChange( MESSAGES_VISIBLE_PROPERTY, oldValue, visible );
    }

    public boolean isPreviewVisible() {
      final JTabbedPane reportEditorPane = getReportEditorPane();
      if ( reportEditorPane == null || reportEditorPane.getTabCount() == 0 ) {
        return false;
      }
      if ( reportEditorPane.getSelectedComponent() instanceof ReportRendererComponent ) {
        final ReportRendererComponent rendererComponent =
          (ReportRendererComponent) reportEditorPane.getSelectedComponent();
        return !rendererComponent.isDesignVisible();
      }
      return false;
    }

    public void setPreviewVisible( final boolean visible ) {
      final boolean oldValue = isPreviewVisible();
      final JTabbedPane reportEditorPane = getReportEditorPane();
      if ( reportEditorPane != null && reportEditorPane.getTabCount() > 0 ) {
        if ( reportEditorPane.getSelectedComponent() instanceof ReportRendererComponent ) {
          final ReportRendererComponent rendererComponent =
            (ReportRendererComponent) reportEditorPane.getSelectedComponent();
          if ( visible ) {
            rendererComponent.showPreview();
          } else {
            rendererComponent.showDesign();
          }
        }
      }
      propertyChangeSupport.firePropertyChange( PREVIEW_VISIBLE_PROPERTY, oldValue, visible );
    }

    public boolean isWelcomeVisible() {
      if ( welcomePane == null ) {
        return false;
      }
      return welcomePane.isVisible();
    }

    public void setWelcomeVisible( final boolean visible ) {
      if ( welcomePane == null ) {
        return;
      }
      welcomePane.setVisible( visible );
      welcomePane.toFront();
    }

    public boolean isFieldSelectorVisible() {
      if ( fieldSelectorPaletteDialog == null ) {
        return false;
      }
      return fieldSelectorPaletteDialog.isVisible();
    }

    public void setFieldSelectorVisible( final boolean visible ) {
      if ( fieldSelectorPaletteDialog == null ) {
        return;
      }
      fieldSelectorPaletteDialog.setVisible( visible );
      fieldSelectorPaletteDialog.toFront();
    }

    public void redrawAll() {
      getReportEditorPane().invalidate();
      getReportEditorPane().revalidate();
      getReportEditorPane().repaint();
    }

    public void showDataTree() {
      treePanel.refreshTabPanel( getAttributeEditorPanel() );
      treePanel.showDataTab();
    }

    public JPopupMenu getPopupMenu( final String id ) {
      return xulDesignerFrame.getComponent( id, JPopupMenu.class );
    }

    public JComponent getToolBar( final String id ) {
      JComponent toolBar = xulDesignerFrame.getComponent( id, JComponent.class );
      if ( toolBar instanceof JToolBar ) {
        final JToolBar realToolBar = (JToolBar) toolBar;
        realToolBar.setFloatable( false );
      }
      return toolBar;
    }

    public Component getParent() {
      return parent;
    }

    public <T extends JComponent> T getComponent( final String id, final Class<T> type ) {
      T xulComponentById = xulDesignerFrame.getComponent( id, type );
      if ( type.isInstance( xulComponentById ) ) {
        return (T) xulComponentById;
      }
      return null;
    }

    public <T extends XulComponent> T getXulComponent( final String id, final Class<T> type ) {
      T xulComponentById = xulDesignerFrame.getXulComponent( id, type );
      if ( type.isInstance( xulComponentById ) ) {
        return (T) xulComponentById;
      }
      return null;
    }

    public ActionSwingMenuitem createMenuItem( final Action action ) {
      return xulDesignerFrame.createMenu( action );
    }

    public XulMenupopup createPopupMenu( final String label, final XulComponent parent ) throws XulException {
      return xulDesignerFrame.createPopupMenu( label, parent );
    }
  }

  private class ShowWelcomeScreenTask implements Runnable {
    public void run() {
      viewController.setWelcomeVisible( WorkspaceSettings.getInstance().isShowLauncher() );
    }
  }

  private class DragSelectionToggleHandler implements AWTEventListener {
    private DragSelectionToggleHandler() {
    }

    /**
     * Invoked when an event is dispatched in the AWT.
     */
    public void eventDispatched( final AWTEvent event ) {
      if ( event instanceof KeyEvent == false ) {
        return;
      }
      final KeyEvent keyevent = (KeyEvent) event;
      if ( KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow() ==
        ReportDesignerFrame.this ) {
        context.setSelectionWaiting( keyevent.isShiftDown() );
      }
    }
  }

  private class PageTextHandler implements PropertyChangeListener {
    /**
     * This method gets called when a bound property is changed.
     *
     * @param evt A PropertyChangeEvent object describing the event source and the property that has changed.
     */
    public void propertyChange( final PropertyChangeEvent evt ) {
      statusBar.setPages( context.getPage(), context.getPageTotal() );
    }
  }

  private class DocumentNameChangeHandler implements PropertyChangeListener {
    private ReportDesignerDocumentContext documentContext;
    private TabRenderer tabRenderer;
    private int tabIndex;

    private DocumentNameChangeHandler( final ReportDesignerDocumentContext documentContext,
                                       final TabRenderer tabRenderer,
                                       final int tabIndex ) {
      this.documentContext = documentContext;
      this.tabRenderer = tabRenderer;
      this.tabIndex = tabIndex;
    }

    public void propertyChange( final PropertyChangeEvent evt ) {
      String tabName = documentContext.getTabName();
      tabRenderer.setRawTabName( tabName );
      getReportEditorPane().setTitleAt( tabIndex, tabName );
      recomputeAllTabTitles();
      rebuildReportMenu();
    }
  }

  private static final Log logger = LogFactory.getLog( ReportDesignerFrame.class );
  private final VisibleElementsUpdateHandler visibleElementsUpdateHandler;
  private JTabbedPane reportEditorPane;
  private GlobalPane dockingPane;
  private StatusBar statusBar;
  private DefaultReportDesignerContext context;
  private Category attributeToolWindow;
  private Category reportTreeToolWindow;
  private Category inspectionsToolWindow;
  private FrameViewController viewController;
  private ElementPropertiesPanel attributeEditorPanel;
  private WelcomePane welcomePane;
  private FieldSelectorPaletteDialog fieldSelectorPaletteDialog;
  private TreeSidePanel treePanel;
  private JComponent paletteToolBar;

  public ReportDesignerFrame() throws XulException {
    setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
    final ImageIcon icon = IconLoader.getInstance().getProductIcon();
    if ( icon != null ) {
      setIconImage( icon.getImage() );
    }

    setTitle( Messages.getString( "ReportDesignerFrame.Title" ) );
    addWindowListener( new WindowCloseHandler() );

    viewController = new FrameViewController( this );
    context = new DefaultReportDesignerContext( viewController );
    viewController.initializeXulDesignerFrame( context );

    welcomePane = new WelcomePane( ReportDesignerFrame.this, getContext() );
    fieldSelectorPaletteDialog = new FieldSelectorPaletteDialog( ReportDesignerFrame.this, getContext() );

    statusBar = new StatusBar( context );

    reportEditorPane = new FancyTabbedPane();
    reportEditorPane.setTabLayoutPolicy( JTabbedPane.SCROLL_TAB_LAYOUT );
    reportEditorPane.getModel().addChangeListener( new ReportTabActivationHandler() );
    reportEditorPane.addMouseListener( new ReportTabPanePopupHandler() );

    dockingPane = new GlobalPane( false );
    dockingPane.setMainComponent( reportEditorPane );

    attributeEditorPanel = new ElementPropertiesPanel();
    attributeEditorPanel.setAllowAttributeCard( true );
    attributeEditorPanel.setReportDesignerContext( context );

    treePanel = new TreeSidePanel( context, attributeEditorPanel );

    initializeToolWindows();

    paletteToolBar = createPaletteToolBar();

    final JPanel contentPane = new JPanel();
    contentPane.setLayout( new BorderLayout() );
    contentPane.add( context.getToolBar( "main-toolbar" ), BorderLayout.NORTH ); // NON-NLS
    contentPane.add( dockingPane, BorderLayout.CENTER );
    contentPane.add( statusBar, BorderLayout.SOUTH );
    contentPane.add( paletteToolBar, BorderLayout.WEST );
    setContentPane( contentPane );

    setJMenuBar( createMenuBar() );
    setDropTarget( new DropTarget( this, DnDConstants.ACTION_COPY_OR_MOVE, new DefaultDropTargetListener() ) );

    context
      .addPropertyChangeListener( ReportDesignerContext.ACTIVE_CONTEXT_PROPERTY, new ReportTabActivationHandler() );
    context.addPropertyChangeListener( ReportDesignerContext.STATUS_TEXT_PROPERTY, new StatusTextHandler() );
    context.addPropertyChangeListener( ReportDesignerContext.PAGE_PROPERTY, new PageTextHandler() );
    context.addPropertyChangeListener( ReportDesignerContext.REPORT_RENDER_CONTEXT_PROPERTY,
      new ReportEditorContextHandler() );

    Toolkit.getDefaultToolkit().addAWTEventListener( new DragSelectionToggleHandler(), AWTEvent.KEY_EVENT_MASK );

    if ( MacOSXIntegration.MAC_OS_X ) {
      try {
        final AboutAction aboutAction = new AboutAction();
        aboutAction.setReportDesignerContext( context );

        final SettingsAction settingsAction = new SettingsAction();
        settingsAction.setReportDesignerContext( context );

        final QuitAction quitAction = new QuitAction();
        quitAction.setReportDesignerContext( context );

        final OpenReportAction openReportAction = new OpenReportAction();
        openReportAction.setReportDesignerContext( context );

        MacOSXIntegration.setOpenFileAction( openReportAction );
        MacOSXIntegration.setAboutAction( aboutAction );
        MacOSXIntegration.setPreferencesAction( settingsAction );
        MacOSXIntegration.setQuitAction( quitAction );
      } catch ( Throwable e ) {
        DebugLog.log( "Failed to activate MacOS-X integration", e ); // NON-NLS
      }
    }

    visibleElementsUpdateHandler = new VisibleElementsUpdateHandler();
    WorkspaceSettings.getInstance().addSettingsListener( visibleElementsUpdateHandler );
  }

  public void initWindowLocations( final File[] filesToOpen ) {
    fieldSelectorPaletteDialog.initWindowLocation();
    viewController.setFieldSelectorVisible( WorkspaceSettings.getInstance().isFieldSelectorVisible() );

    welcomePane.setLocationRelativeTo( ReportDesignerFrame.this );

    if ( filesToOpen == null || filesToOpen.length == 0 ) {
      if ( WorkspaceSettings.getInstance().isReopenLastReport() ) {
        final File[] recentFiles = context.getRecentFilesModel().getRecentFiles();
        if ( recentFiles.length > 0 ) {
          SwingUtilities.invokeLater( new OpenReportAction.OpenReportTask( recentFiles[ 0 ], context ) );
          return;
        }
      }

      /*
      * PRD-1676:  Since the report designer frame will steal the focus from the Welcome dialog
      * we must let the report designer frame finish it's initialization before we try to display
      * the welcome panel.
      */
      SwingUtilities.invokeLater( new ShowWelcomeScreenTask() );
    } else {
      for ( int i = 0; i < filesToOpen.length; i++ ) {
        final File file = filesToOpen[ i ];
        SwingUtilities.invokeLater( new OpenReportAction.OpenReportTask( file, context ) );
      }
    }
  }

  protected Category getAttributeToolWindow() {
    return attributeToolWindow;
  }

  protected Category getReportTreeToolWindow() {
    return reportTreeToolWindow;
  }

  protected Category getInspectionsToolWindow() {
    return inspectionsToolWindow;
  }

  protected ElementPropertiesPanel getAttributeEditorPanel() {
    return attributeEditorPanel;
  }

  protected JTabbedPane getReportEditorPane() {
    return reportEditorPane;
  }

  protected DefaultReportDesignerContext getContext() {
    return context;
  }

  protected StatusBar getStatusBar() {
    return statusBar;
  }

  private JMenuBar createMenuBar() {
    createRecentFilesMenu();
    createZoomMenu();
    createMorphMenu();
    createInsertElementsMenu();
    createInsertDataSourcesMenu();
    createSamplesMenu();

    return context.getView().getComponent( "main-menubar", JMenuBar.class );
  }

  private void createInsertDataSourcesMenu() {
    final JMenu insertDataSourcesMenu =
      context.getView().getComponent( "insert-datasources-menu", JMenu.class );// NON-NLS
    if ( insertDataSourcesMenu != null ) {
      ContextMenuUtility.createDataSourceMenu( context, insertDataSourcesMenu );
    }
  }

  private void createInsertElementsMenu() {
    final XulMenupopup insertElementsMenu =
      context.getView().getXulComponent( "insert-elements-popup", XulMenupopup.class );// NON-NLS
    if ( insertElementsMenu == null ) {
      return;
    }

    final ElementMetaData[] datas = ElementTypeRegistry.getInstance().getAllElementTypes();
    Arrays.sort( datas, new GroupedMetaDataComparator() );
    Object grouping = null;
    boolean firstElement = true;
    for ( int i = 0; i < datas.length; i++ ) {
      final ElementMetaData data = datas[ i ];
      if ( data.isHidden() ) {
        continue;
      }

      final String currentGrouping = data.getGrouping( Locale.getDefault() );
      if ( firstElement == false ) {
        if ( ObjectUtilities.equal( currentGrouping, grouping ) == false ) {
          grouping = currentGrouping;
          SwingMenuseparator separator = new SwingMenuseparator( null, null, null, "menuseparator" );
          insertElementsMenu.addChild( separator );
        }
      } else {
        grouping = currentGrouping;
        firstElement = false;
      }

      final InsertElementAction action = new InsertElementAction( data );
      action.setReportDesignerContext( context );
      ActionSwingMenuitem menuItem = new ActionSwingMenuitem( ActionSwingMenuitem.MENUITEM );
      menuItem.setAction( action );
      insertElementsMenu.addChild( menuItem );
    }
  }

  private void createRecentFilesMenu() {
    final XulMenupopup reopenMenu =
      context.getView().getXulComponent( "file-reopen-popup", XulMenupopup.class ); // NON-NLS
    final XulMenuitem clearMenuitem =
      context.getView().getXulComponent( "file-clear-recent", XulMenuitem.class );// NON-NLS
    if ( reopenMenu != null && clearMenuitem != null ) {
      final RecentFilesUpdateHandler updateHandler = new RecentFilesUpdateHandler( context, reopenMenu, clearMenuitem );
      updateHandler.settingsChanged();
      context.getRecentFilesModel().addSettingsListener( updateHandler );
    }
  }

  private void createZoomMenu() {
    final XulComponent zoomMenu =
      context.getView().getXulComponent( "view-zoom-selection-popup", XulMenupopup.class );// NON-NLS
    if ( zoomMenu == null ) {
      return;
    }

    final InternalZoomAction zoom50action = new InternalZoomAction( 50 );
    final InternalZoomAction zoom100action = new InternalZoomAction( 100 );
    final InternalZoomAction zoom200action = new InternalZoomAction( 200 );
    final InternalZoomAction zoom400action = new InternalZoomAction( 400 );

    zoom50action.setReportDesignerContext( context );
    zoom100action.setReportDesignerContext( context );
    zoom200action.setReportDesignerContext( context );
    zoom400action.setReportDesignerContext( context );

    zoomMenu.addChild( context.getView().createMenuItem( zoom50action ) );
    zoomMenu.addChild( context.getView().createMenuItem( zoom100action ) );
    zoomMenu.addChild( context.getView().createMenuItem( zoom200action ) );
    zoomMenu.addChild( context.getView().createMenuItem( zoom400action ) );
  }

  private void createSamplesMenu() {
    final XulMenupopup samplesPopup =
      context.getView().getXulComponent( "help-samples-popup", XulMenupopup.class );// NON-NLS
    if ( samplesPopup == null ) {
      return;
    }

    for ( final XulComponent childNode : new ArrayList<XulComponent>( samplesPopup.getChildNodes() ) ) {
      samplesPopup.removeChild( childNode );
    }

    final TreeModel treeModel = SamplesTreeBuilder.getSampleTreeModel();
    final Object root = treeModel.getRoot();
    try {
      insertReports( treeModel, root, samplesPopup );
    } catch ( XulException e ) {
      logger.warn( "Failed to initialize sample menu", e );
    }
  }

  private void insertReports( final TreeModel model,
                              final Object currentLevel,
                              final XulMenupopup popup ) throws XulException {
    final int childCount = model.getChildCount( currentLevel );
    for ( int i = 0; i < childCount; i += 1 ) {
      final ReportDesignerView frame = context.getView();

      final Object child = model.getChild( currentLevel, i );
      if ( model.isLeaf( child ) ) {
        final DefaultMutableTreeNode node = (DefaultMutableTreeNode) child;
        final File file = new File( String.valueOf( node.getUserObject() ) );
        final OpenSampleReportAction action = new OpenSampleReportAction( file, node.toString() );
        action.setReportDesignerContext( context );
        popup.addChild( frame.createMenuItem( action ) );
      } else {
        final XulMenupopup childPopup = frame.createPopupMenu( String.valueOf( child ), popup );
        insertReports( model, child, childPopup );
      }
    }
  }

  private void createMorphMenu() {
    final JMenu morphMenu = context.getView().getComponent( "format-morph-menu", JMenu.class );// NON-NLS     PRD-4452
    if ( morphMenu == null ) {
      return;
    }

    final ElementMetaData[] datas = ElementTypeRegistry.getInstance().getAllElementTypes();
    Arrays.sort( datas, new GroupedMetaDataComparator() );
    Object grouping = null;
    boolean firstElement = true;
    for ( int i = 0; i < datas.length; i++ ) {
      final ElementMetaData data = datas[ i ];
      if ( data.isHidden() ) {
        continue;
      }
      final String currentGrouping = data.getGrouping( Locale.getDefault() );
      if ( firstElement == false ) {
        if ( ObjectUtilities.equal( currentGrouping, grouping ) == false ) {
          grouping = currentGrouping;
          morphMenu.addSeparator();
        }
      } else {
        grouping = currentGrouping;
        firstElement = false;
      }

      try {
        final MorphAction action = new MorphAction( data.create() );
        action.setReportDesignerContext( context );
        morphMenu.add( new JMenuItem( action ) );
      } catch ( InstantiationException e ) {
        UncaughtExceptionsModel.getInstance().addException( e );
      }
    }
  }

  private void initializeToolWindows() {
    this.reportTreeToolWindow = createStructureTreeToolWindow();
    this.attributeToolWindow = createAttributesToolWindow();
    this.inspectionsToolWindow = createInspectionsToolWindow();

    dockingPane.add( GlobalPane.Alignment.RIGHT, reportTreeToolWindow );
    dockingPane.add( GlobalPane.Alignment.RIGHT, attributeToolWindow );
    dockingPane.add( GlobalPane.Alignment.BOTTOM, inspectionsToolWindow );
    dockingPane.setPreferredContentSize( GlobalPane.Alignment.BOTTOM, 100 );
    dockingPane.setPreferredContentSize( GlobalPane.Alignment.RIGHT, 300 );
    // initialize add elements
    initAddElements( "popup-Band.add-element-menu" );// NON-NLS
    initAddElements( "popup-RootLevelBand.add-element-menu" );// NON-NLS
    initSetBarcodeTypeElements( "popup-Barcode.type" );// NON-NLS

  }

  private Category createAttributesToolWindow() {
    final ImageIcon propertyTableIcon = IconLoader.getInstance().getPropertyTableIcon();

    return new Category( propertyTableIcon, Messages.getString( "Attribute.Title" ), attributeEditorPanel );// NON-NLS
  }

  private Category createInspectionsToolWindow() {
    // this is the bottom 'messages' panel
    final InspectionSidePanePanel inspectionsMessagePanel = new InspectionSidePanePanel();
    inspectionsMessagePanel.setReportDesignerContext( context );

    final InternalWindow inspectionGadgetInternalWindow =
      new InternalWindow( Messages.getString( "InspectionGadget.Title" ) );// NON-NLS
    inspectionGadgetInternalWindow.add( inspectionsMessagePanel, BorderLayout.CENTER );

    final Category category = new Category
      ( IconLoader.getInstance().getMessagesIcon(),
        Messages.getString( "Messages.Title" ),// NON-NLS
        inspectionGadgetInternalWindow );
    category.setMinimized( true );
    return category;
  }

  private Category createStructureTreeToolWindow() {
    return new Category( IconLoader.getInstance().getReportTreeIcon(),
      Messages.getString( "StructureView.Title" ),// NON-NLS
      treePanel );

  }


  private void initSetBarcodeTypeElements( final String id ) {
    final JMenu menu = context.getView().getComponent( id, JMenu.class );
    if ( menu == null ) {
      return;
    }

    final BarcodeTypePropertyEditor editor = new BarcodeTypePropertyEditor();
    final String[] tags = editor.getTags();
    for ( int i = 0; i < tags.length; i++ ) {
      final String tag = tags[ i ];
      final BarcodeTypeAction action = new BarcodeTypeAction( tag );
      action.setReportDesignerContext( context );
      menu.add( new JRadioButtonMenuItem( action ) );
    }
  }

  private void initAddElements( final String id ) {
    final JMenu menu = context.getView().getComponent( id, JMenu.class );
    if ( menu == null ) {
      return;
    }

    final ElementMetaData[] datas = ElementTypeRegistry.getInstance().getAllElementTypes();
    Arrays.sort( datas, new GroupedMetaDataComparator() );
    Object grouping = null;
    boolean firstElement = true;
    for ( int i = 0; i < datas.length; i++ ) {
      final ElementMetaData data = datas[ i ];
      if ( data.isHidden() ) {
        continue;
      }
      if ( !WorkspaceSettings.getInstance().isVisible( data ) ) {
        continue;
      }

      final String currentGrouping = data.getGrouping( Locale.getDefault() );
      if ( firstElement == false ) {
        if ( ObjectUtilities.equal( currentGrouping, grouping ) == false ) {
          grouping = currentGrouping;
          menu.addSeparator();
        }
      } else {
        grouping = currentGrouping;
        firstElement = false;
      }
      final InsertElementAction action = new InsertElementAction( data );
      action.setReportDesignerContext( context );
      menu.add( new JMenuItem( action ) );
    }
  }

  private JComponent createPaletteToolBar() {
    final JToolBar toolBar = new JToolBar();
    toolBar.setFloatable( false );
    toolBar.setOrientation( JToolBar.VERTICAL );

    final ElementMetaData[] datas = ElementTypeRegistry.getInstance().getAllElementTypes();
    Arrays.sort( datas, new GroupedMetaDataComparator() );
    Object grouping = null;
    boolean firstElement = true;
    for ( int i = 0; i < datas.length; i++ ) {
      final ElementMetaData data = datas[ i ];
      if ( data.isHidden() ) {
        continue;
      }

      if ( !WorkspaceSettings.getInstance().isVisible( data ) ) {
        continue;
      }

      final String currentGrouping = data.getGrouping( Locale.getDefault() );
      if ( firstElement == false ) {
        if ( ObjectUtilities.equal( currentGrouping, grouping ) == false ) {
          grouping = currentGrouping;
          toolBar.addSeparator();
        }
      } else {
        grouping = currentGrouping;
        firstElement = false;
      }
      final InsertElementAction action = new InsertElementAction( data );
      action.setReportDesignerContext( context );
      toolBar.add( new PaletteButton( data, context ) );
    }

    final JScrollPane paletteScrollpane = new JScrollPane( toolBar );
    paletteScrollpane.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
    paletteScrollpane
      .addComponentListener( new ScrollbarSyncHandler( paletteScrollpane.getVerticalScrollBar(), toolBar ) );
    return paletteScrollpane;
  }

  private void rebuildReportMenu() {
    final XulComponent reopenMenu = context.getView().getXulComponent( "window.reports-area", XulComponent.class );
    if ( reopenMenu == null ) {
      return;
    }

    final List<XulComponent> xulComponents = reopenMenu.getChildNodes();
    final XulComponent[] objects = xulComponents.toArray( new XulComponent[ xulComponents.size() ] );
    for ( int i = 0; i < objects.length; i++ ) {
      final XulComponent object = objects[ i ];
      reopenMenu.removeChild( object );
    }

    final JTabbedPane tabbedPane = getReportEditorPane();
    final int count = tabbedPane.getTabCount();
    if ( count > 0 ) {
      reopenMenu.addChild( new SwingMenuseparator( null, null, null, "menu-separator" ) ); // NON-NLS
      for ( int i = 0; i < count; i++ ) {
        final Component at = tabbedPane.getTabComponentAt( i );
        final String tabName;
        if ( at instanceof TabRenderer ) {
          final TabRenderer renderer = (TabRenderer) at;
          tabName = renderer.getTitle();
        } else {
          tabName = tabbedPane.getTitleAt( i );
        }
        final SelectTabAction action = new SelectTabAction( i, tabName );
        final ActionSwingMenuitem actionSwingMenuitem = context.getView().createMenuItem( action );
        actionSwingMenuitem.setReportDesignerContext( context );
        reopenMenu.addChild( actionSwingMenuitem );
      }
    }
  }

  protected void updateFrameTitle() {
    final int i = getReportEditorPane().getSelectedIndex();
    final String report;
    final String reportName;
    if ( getContext() != null && getContext().getActiveContext() != null ) {
      if ( i == -1 ) {
        reportName = null;
      } else {
        final Component at = getReportEditorPane().getTabComponentAt( i );
        if ( at instanceof TabRenderer ) {
          final TabRenderer renderer = (TabRenderer) at;
          reportName = renderer.getTitle();
        } else {
          reportName = getReportEditorPane().getTitleAt( i );
        }
      }

      report = getContext().getActiveContext().getDocumentFile();
    } else {
      report = null;
      reportName = null;
    }
    setTitle( computeFrameTitle( reportName, report ) );
  }

  private String computeFrameTitle( final String tabName, final String path ) {
    if ( tabName == null || path == null ) {
      return Messages.getString( "ReportDesignerFrame.Title" );// NON-NLS
    }

    if ( StringUtils.isEmpty( path ) ) {
      return Messages.getString( "ReportDesignerFrame.TitleWithName", tabName );// NON-NLS
    } else {
      return Messages.getString( "ReportDesignerFrame.TitleWithNameAndPath", tabName, path );// NON-NLS
    }
  }

  private void recomputeAllTabTitles() {
    final JTabbedPane editorPane = getReportEditorPane();
    final int count = editorPane.getTabCount();
    for ( int i = 0; i < count; i++ ) {
      final Component at = editorPane.getTabComponentAt( i );
      if ( at instanceof TabRenderer ) {
        final TabRenderer renderer = (TabRenderer) at;
        renderer.setTitle( renderer.recomputeTabName() );
      }
    }

    updateFrameTitle();
  }

  private class VisibleElementsUpdateHandler implements SettingsListener {
    private boolean deprecated;
    private boolean expert;
    private MaturityLevel maturityLevel;

    private VisibleElementsUpdateHandler() {
      deprecated = WorkspaceSettings.getInstance().isShowDeprecatedItems();
      expert = WorkspaceSettings.getInstance().isShowExpertItems();
      maturityLevel = WorkspaceSettings.getInstance().getMaturityLevel();
    }

    public void settingsChanged() {
      if ( isChanged() ) {
        try {
          // throw away the old model and re-parse the XUL file from scratch.
          // This is easier than trying to patch a wild and undocumented implementation of the Xul backend
          viewController.initializeXulDesignerFrame( context );

          getContentPane().remove( paletteToolBar );
          paletteToolBar = createPaletteToolBar();
          getContentPane().add( paletteToolBar, BorderLayout.WEST );

          setJMenuBar( createMenuBar() );

          revalidate();
          repaint();
        } catch ( XulException e ) {
          UncaughtExceptionsModel.getInstance().addException( e );
        }


        deprecated = WorkspaceSettings.getInstance().isShowDeprecatedItems();
        expert = WorkspaceSettings.getInstance().isShowExpertItems();
        maturityLevel = WorkspaceSettings.getInstance().getMaturityLevel();
      }
    }

    private boolean isChanged() {
      if ( WorkspaceSettings.getInstance().isShowDeprecatedItems() != deprecated ) {
        return true;
      }
      if ( WorkspaceSettings.getInstance().isShowExpertItems() != expert ) {
        return true;
      }
      if ( WorkspaceSettings.getInstance().getMaturityLevel() != maturityLevel ) {
        return true;
      }
      return false;
    }
  }
}
