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
 * Copyright (c) 2008 - 2009 Pentaho Corporation, .  All rights reserved.
 */

package org.pentaho.reporting.designer.core;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.BeanInfo;
import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.TabbedPaneUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.designer.core.actions.elements.InsertElementAction;
import org.pentaho.reporting.designer.core.actions.elements.MorphAction;
import org.pentaho.reporting.designer.core.actions.elements.barcode.BarcodeTypeAction;
import org.pentaho.reporting.designer.core.actions.global.AboutAction;
import org.pentaho.reporting.designer.core.actions.global.OpenRecentReportAction;
import org.pentaho.reporting.designer.core.actions.global.OpenReportAction;
import org.pentaho.reporting.designer.core.actions.global.OpenSampleReportAction;
import org.pentaho.reporting.designer.core.actions.global.QuitAction;
import org.pentaho.reporting.designer.core.actions.global.SelectTabAction;
import org.pentaho.reporting.designer.core.actions.global.SettingsAction;
import org.pentaho.reporting.designer.core.actions.global.ZoomAction;
import org.pentaho.reporting.designer.core.actions.report.CloseAllReportsAction;
import org.pentaho.reporting.designer.core.actions.report.CloseChildReportsAction;
import org.pentaho.reporting.designer.core.actions.report.CloseOtherReportsAction;
import org.pentaho.reporting.designer.core.actions.report.CloseReportAction;
import org.pentaho.reporting.designer.core.actions.report.CloseUnmodifiedReportsAction;
import org.pentaho.reporting.designer.core.editor.ContextMenuUtility;
import org.pentaho.reporting.designer.core.editor.ElementPropertiesPanel;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.editor.ReportRendererComponent;
import org.pentaho.reporting.designer.core.editor.fieldselector.FieldSelectorPaletteDialog;
import org.pentaho.reporting.designer.core.editor.palette.PaletteButton;
import org.pentaho.reporting.designer.core.editor.structuretree.ReportTree;
import org.pentaho.reporting.designer.core.editor.structuretree.StructureTreePanel;
import org.pentaho.reporting.designer.core.inspections.InspectionSidePanePanel;
import org.pentaho.reporting.designer.core.settings.SettingsListener;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.designer.core.status.StatusBar;
import org.pentaho.reporting.designer.core.util.CanvasImageLoader;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.designer.core.util.docking.Category;
import org.pentaho.reporting.designer.core.util.docking.GlobalPane;
import org.pentaho.reporting.designer.core.util.docking.InternalWindow;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.designer.core.welcome.SamplesTreeBuilder;
import org.pentaho.reporting.designer.core.welcome.WelcomePane;
import org.pentaho.reporting.designer.core.widgets.CloseTabIcon;
import org.pentaho.reporting.designer.core.xul.ActionSwingMenuitem;
import org.pentaho.reporting.designer.core.xul.XulDesignerFrame;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.CrosstabElement;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.event.ReportModelListener;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementTypeRegistry;
import org.pentaho.reporting.engine.classic.core.metadata.GroupedMetaDataComparator;
import org.pentaho.reporting.engine.classic.extensions.modules.sbarcodes.BarcodeTypePropertyEditor;
import org.pentaho.reporting.libraries.base.util.DebugLog;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.designtime.swing.BorderlessButton;
import org.pentaho.reporting.libraries.designtime.swing.MacOSXIntegration;
import org.pentaho.reporting.libraries.docbundle.ODFMetaAttributeNames;
import org.pentaho.ui.xul.XulComponent;
import org.pentaho.ui.xul.XulException;
import org.pentaho.ui.xul.XulLoader;
import org.pentaho.ui.xul.components.XulMenuitem;
import org.pentaho.ui.xul.containers.XulMenu;
import org.pentaho.ui.xul.containers.XulMenupopup;
import org.pentaho.ui.xul.swing.tags.SwingMenuseparator;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class ReportDesignerFrame extends JFrame
{
  /**
   * Class for handling cursor changes during drags
   * See PRD-1674
   */
  public class DefaultDropTargetListener extends DropTargetAdapter
  {
    public void dragEnter(final DropTargetDragEvent dtde)
    {
      final DropTarget dropTarget = (DropTarget) dtde.getSource();
      dropTarget.getComponent().setCursor(DragSource.DefaultCopyNoDrop);
    }

    public void dragExit(final DropTargetEvent dte)
    {
      final DropTarget dropTarget = (DropTarget) dte.getSource();
      dropTarget.getComponent().setCursor(Cursor.getDefaultCursor());
    }

    public void drop(final DropTargetDropEvent dtde)
    {
      final DropTarget dropTarget = (DropTarget) dtde.getSource();
      dropTarget.getComponent().setCursor(Cursor.getDefaultCursor());
      dtde.rejectDrop();
    }
  }

  private static class ScrollbarSyncHandler extends ComponentAdapter
  {
    private final JScrollBar theVerticalScrollBar;
    private final JToolBar toolBar;

    public ScrollbarSyncHandler(final JScrollBar theVerticalScrollBar, final JToolBar toolBar)
    {
      this.theVerticalScrollBar = theVerticalScrollBar;
      this.toolBar = toolBar;
    }

    public void componentResized(final ComponentEvent e)
    {
      if (theVerticalScrollBar.isShowing())
      {
        toolBar.setBorder(BorderFactory.createEmptyBorder(25, 0, 0, 19));
      }
      else
      {
        toolBar.setBorder(BorderFactory.createEmptyBorder(25, 0, 0, 4));
      }
    }
  }

  private class WindowCloseHandler extends WindowAdapter
  {
    private WindowCloseHandler()
    {
    }

    /**
     * Invoked when a window is in the process of being closed. The close operation can be overridden at this point.
     */
    public void windowClosing(final WindowEvent e)
    {
      final QuitAction quitAction = new QuitAction();
      quitAction.setReportDesignerContext(context);
      quitAction.actionPerformed(new ActionEvent(this, 0, "quit", 0)); // NON-NLS
    }
  }

  private static class InternalZoomAction extends ZoomAction
  {
    private InternalZoomAction(final int percentage)
    {
      super(percentage);
    }

    public boolean isSelected()
    {
      final ReportRenderContext activeContext = getActiveContext();
      if (activeContext != null)
      {
        return activeContext.getZoomModel().getZoomAsPercentage() == (getPercentage() / 100f);
      }
      return false;
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(final ActionEvent e)
    {
      final ReportRenderContext activeContext = getActiveContext();
      if (activeContext != null)
      {
        activeContext.getZoomModel().setZoomAsPercentage(getPercentage() / 100f);
      }
    }
  }

  private class ReportTabActivationHandler implements ChangeListener, PropertyChangeListener
  {
    private ReportTabActivationHandler()
    {
    }

    public void propertyChange(final PropertyChangeEvent evt)
    {
      final ReportRenderContext activeContext = getContext().getActiveContext();
      final JTabbedPane editorPanes = getReportEditorPane();
      final int i = findTabForContext(activeContext);
      if (i != -1 && editorPanes.getSelectedIndex() != i)
      {
        editorPanes.setSelectedIndex(i);
      }

      updateFrameTitle();
    }

    private int findTabForContext(final ReportRenderContext activeContext)
    {
      final JTabbedPane editorPanes = getReportEditorPane();
      final int count = editorPanes.getTabCount();
      for (int i = 0; i < count; i++)
      {
        final Component c = editorPanes.getComponentAt(i);
        if (c instanceof ReportRendererComponent)
        {
          final ReportRendererComponent rrc = (ReportRendererComponent) c;
          if (rrc.getRenderContext() == activeContext)
          {
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
    public void stateChanged(final ChangeEvent e)
    {
      final JTabbedPane editorPanes = getReportEditorPane();
      final DefaultReportDesignerContext context = getContext();
      if (editorPanes.getTabCount() == 0)
      {
        context.setActiveContext(null);
        return;
      }

      final int index = editorPanes.getSelectedIndex();
      if (index == -1 || editorPanes.getComponentAt(index) instanceof ReportRendererComponent == false)
      {
        context.setActiveContext(null);
      }
      else
      {
        // try to sync the context.
        final ReportRendererComponent rendererComponent = (ReportRendererComponent) editorPanes.getComponentAt(index);
        final ReportRenderContext rendererContext = rendererComponent.getRenderContext();
        for (int i = 0; i < context.getReportRenderContextCount(); i++)
        {
          final ReportRenderContext context1 = context.getReportRenderContext(i);
          if (context1 == rendererContext)
          {
            context.setActiveContext(rendererContext);
            rendererContext.getInspectionRunner().startTimer();
            return;
          }
        }

        if (context.getReportRenderContextCount() > 0)
        {
          // we couldn't find a context, but we do at least have a context
          // instead of blowing away the context, let's grab one and
          // attempt to use it
          // this works around PRD-1609
          context.setActiveContext(context.getReportRenderContext(context.getReportRenderContextCount() - 1));
        }
        else
        {
          // not found, so we are probably in a remove at the moment.
          context.setActiveContext(null);
        }
      }
    }
  }

  private class StatusTextHandler implements PropertyChangeListener
  {
    private StatusTextHandler()
    {
    }

    /**
     * This method gets called when a bound property is changed.
     *
     * @param evt A PropertyChangeEvent object describing the event source and the property that has changed.
     */

    public void propertyChange(final PropertyChangeEvent evt)
    {
      getStatusBar().setGeneralInfoText(getContext().getStatusText());
    }
  }

  private static class ReportNameUpdateHandler implements ReportModelListener
  {
    private JTabbedPane reportEditorPane;
    private JComponent tabComponent;
    private AbstractReportDefinition report;
    private ReportDesignerFrame reportDesignerFrame;

    private ReportNameUpdateHandler(final JTabbedPane reportEditorPane,
                                    final ReportDesignerFrame reportDesignerFrame,
                                    final JComponent tabComponent,
                                    final AbstractReportDefinition report)
    {
      this.reportEditorPane = reportEditorPane;
      this.tabComponent = tabComponent;
      this.report = report;
      this.reportDesignerFrame = reportDesignerFrame;
    }

    public void nodeChanged(final ReportModelEvent event)
    {
      if (event.getElement() == report &&
          event.getType() == ReportModelEvent.NODE_PROPERTIES_CHANGED)
      {
        final int tabCount = reportEditorPane.getTabCount();
        for (int i = 0; i < tabCount; i++)
        {
          if (reportEditorPane.getComponentAt(i) == tabComponent)
          {
            final TabRenderer tabComponent = (TabRenderer) reportEditorPane.getTabComponentAt(i);
            final String reportTitle = computeTabName(report);
            tabComponent.setRawTabName(reportTitle);
            reportDesignerFrame.recomputeAllTabTitles();
            reportDesignerFrame.rebuildReportMenu();
            break;
          }
        }
      }
    }
  }

  private class TabRenderer extends JComponent implements ActionListener
  {
    private String rawTabName;
    private JLabel label;
    private JButton closeButton;

    public TabRenderer(final Icon icon, final String tabName)
    {
      if (tabName == null)
      {
        throw new NullPointerException();
      }
      this.rawTabName = tabName;

      closeButton = new BorderlessButton();
      closeButton.setBorder(new EmptyBorder(0, 0, 0, 0));
      closeButton.setPressedIcon(new CloseTabIcon(false, true));
      closeButton.setIcon(new CloseTabIcon(false, false));
      closeButton.setRolloverIcon(new CloseTabIcon(true, false));
      closeButton.setRolloverEnabled(true);
      closeButton.setContentAreaFilled(false);
      closeButton.setBorderPainted(false);
      closeButton.addActionListener(this);

      label = new JLabel(tabName, icon, SwingConstants.LEFT);

      setLayout(new BorderLayout());
      add(closeButton, BorderLayout.EAST);
      add(label, BorderLayout.CENTER);
    }

    private int findTab()
    {
      final JTabbedPane tabbedPane = getReportEditorPane();
      final int count = tabbedPane.getTabCount();
      for (int i = 0; i < count; i++)
      {
        final Component at = tabbedPane.getTabComponentAt(i);
        if (at == this)
        {
          return i;
        }
      }
      return -1;
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(final ActionEvent e)
    {
      final int tab = findTab();
      if (tab == -1)
      {
        return;
      }

      final CloseReportAction cra = new CloseReportAction(tab);
      cra.setReportDesignerContext(getContext());
      cra.actionPerformed(e);
    }

    public String getTitle()
    {
      return label.getText();
    }

    public void setTitle(final String title)
    {
      label.setText(title);
    }

    public String getRawTabName()
    {
      return rawTabName;
    }

    public void setRawTabName(final String rawTabName)
    {
      this.rawTabName = rawTabName;
    }

    public String recomputeTabName()
    {
      final JTabbedPane editorPane = ReportDesignerFrame.this.getReportEditorPane();
      final int count = editorPane.getTabCount();
      int found = 0;
      for (int i = 0; i < count; i++)
      {
        final Component at = editorPane.getTabComponentAt(i);
        if (at == this)
        {
          if (found == 0)
          {
            return rawTabName;
          }
          else
          {
            return rawTabName + "<" + found + ">";
          }
        }
        else if (at instanceof TabRenderer)
        {
          final TabRenderer otherRenderer = (TabRenderer) at;
          if (rawTabName.equals(otherRenderer.rawTabName))
          {
            found += 1;
          }
        }
        else
        {
          if (rawTabName.equals(editorPane.getTitleAt(i)))
          {
            found += 1;
          }
        }
      }
      return rawTabName;
    }
  }

  private class ReportEditorContextHandler implements PropertyChangeListener
  {
    private ReportEditorContextHandler()
    {
    }

    /**
     * This method gets called when a bound property is changed.
     *
     * @param evt A PropertyChangeEvent object describing the event source and the property that has changed.
     */

    public void propertyChange(final PropertyChangeEvent evt)
    {
      if (ReportDesignerContext.REPORT_RENDER_CONTEXT_PROPERTY.equals(evt.getPropertyName()) == false)
      {
        return;
      }

      if (!(evt instanceof IndexedPropertyChangeEvent))
      {
        throw new UnsupportedOperationException();
      }

      final JTabbedPane editorPanes = getReportEditorPane();
      final IndexedPropertyChangeEvent ievt = (IndexedPropertyChangeEvent) evt;
      if (ievt.getNewValue() != null)
      {
        // added
        final ReportRenderContext renderContext = (ReportRenderContext) ievt.getNewValue();
        final ReportRendererComponent rendererComponent = new ReportRendererComponent(getContext(), renderContext);

        // register the listeners ...
        final AbstractReportDefinition report = renderContext.getReportDefinition();
        report.addReportModelListener(new ReportNameUpdateHandler(editorPanes, ReportDesignerFrame.this, rendererComponent, report));

        final String title = computeTabName(report);
        final Image iconImage = report.getElementType().getMetaData().getIcon(Locale.getDefault(), BeanInfo.ICON_COLOR_16x16);
        final Icon icon;
        if (iconImage != null)
        {
          icon = new ImageIcon(iconImage);
        }
        else
        {
          icon = null;
        }
        editorPanes.addTab(title, null, rendererComponent);
        editorPanes.setTabComponentAt(editorPanes.getTabCount() - 1, new TabRenderer(icon, title));
        editorPanes.setSelectedComponent(rendererComponent);
      }
      else if (ievt.getOldValue() != null)
      {
        if (editorPanes.getTabCount() <= 0)
        {
          return;
        }
        // removed
        int index = ievt.getIndex();
        final ReportRenderContext reportRenderContext = (ReportRenderContext) ievt.getOldValue();
        boolean removedTab = false;

        for (int i = 0; i < editorPanes.getTabCount(); i++)
        {
          final Component tabContent = editorPanes.getComponentAt(i);
          if (tabContent instanceof ReportRendererComponent)
          {
            final ReportRendererComponent myReportRenderer = (ReportRendererComponent) tabContent;
            if (myReportRenderer.getRenderContext() == reportRenderContext)
            {
              index = i;
              editorPanes.removeTabAt(index);
              removedTab = true;
              myReportRenderer.dispose();
              break;
            }
          }
        }

        // if we didn't remove a tab, the tab was probably not a report, so remove it by index
        if (!removedTab)
        {
          editorPanes.removeTabAt(index);
        }

        // unregister the listeners ...
        if (editorPanes.getTabCount() > 0)
        {
          if (index < editorPanes.getTabCount())
          {
            editorPanes.setSelectedIndex(index);
          }
          else
          {
            editorPanes.setSelectedIndex(editorPanes.getTabCount() - 1);
          }
        }
      }

      recomputeAllTabTitles();
      rebuildReportMenu();
    }
  }

  private static class RecentFilesUpdateHandler implements SettingsListener
  {
    private ReportDesignerContext context;
    private XulDesignerFrame xulDesignerFrame;
    private XulMenupopup reopenMenu;
    private XulMenuitem clearMenu;

    private RecentFilesUpdateHandler(final ReportDesignerContext context,
                                     final XulDesignerFrame xulDesignerFrame,
                                     final XulMenupopup reopenMenu,
                                     final XulMenuitem clearMenu)
    {
      this.context = context;
      this.xulDesignerFrame = xulDesignerFrame;
      this.reopenMenu = reopenMenu;
      this.clearMenu = clearMenu;
    }

    public void settingsChanged()
    {
      final File[] recentFiles = context.getRecentFilesModel().getRecentFiles();
      final List<XulComponent> xulComponents = reopenMenu.getChildNodes();
      final XulComponent[] objects = xulComponents.toArray(new XulComponent[xulComponents.size()]);
      for (int i = 0; i < objects.length; i++)
      {
        final XulComponent object = objects[i];
        reopenMenu.removeChild(object);
      }
      if (recentFiles.length == 0)
      {
        clearMenu.setDisabled(true);
      }
      else
      {
        clearMenu.setDisabled(false);
        for (int i = 0; i < recentFiles.length; i++)
        {
          final File file = recentFiles[i];
          if (file.exists() == false)
          {
            continue;
          }
          final OpenRecentReportAction action = new OpenRecentReportAction(file);
          final ActionSwingMenuitem actionSwingMenuitem = xulDesignerFrame.createMenu(action);
          actionSwingMenuitem.setReportDesignerContext(context);
          reopenMenu.addChild(actionSwingMenuitem);
        }
        reopenMenu.addChild(new SwingMenuseparator(null, null, null, null));
      }
    }
  }

  private class ReportTabPanePopupHandler extends MouseAdapter
  {
    private ReportTabPanePopupHandler()
    {
    }

    private void handlePopup(final MouseEvent e)
    {
      final JTabbedPane reportEditorPane = getReportEditorPane();
      final TabbedPaneUI ui = reportEditorPane.getUI();
      final int tabIndex = ui.tabForCoordinate(reportEditorPane, e.getX(), e.getY());
      final JPopupMenu popupMenu = new JPopupMenu();

      final CloseReportAction closeThisAction = new CloseReportAction(tabIndex);
      closeThisAction.setReportDesignerContext(getContext());
      final CloseChildReportsAction closeChildsAction = new CloseChildReportsAction(tabIndex);
      closeChildsAction.setReportDesignerContext(getContext());
      final CloseOtherReportsAction closeOthersAction = new CloseOtherReportsAction(tabIndex);
      closeOthersAction.setReportDesignerContext(getContext());
      final CloseAllReportsAction closeAllAction = new CloseAllReportsAction();
      closeAllAction.setReportDesignerContext(getContext());
      final CloseUnmodifiedReportsAction closeUnmodifiedReportsAction = new CloseUnmodifiedReportsAction();
      closeUnmodifiedReportsAction.setReportDesignerContext(getContext());

      popupMenu.add(new JMenuItem(closeThisAction));
      popupMenu.addSeparator();
      popupMenu.add(new JMenuItem(closeChildsAction));
      popupMenu.add(new JMenuItem(closeUnmodifiedReportsAction));
      popupMenu.add(new JMenuItem(closeOthersAction));
      popupMenu.add(new JMenuItem(closeAllAction));
      popupMenu.show(reportEditorPane, e.getX(), e.getY());
    }

    public void mouseClicked(final MouseEvent e)
    {
      if (e.isPopupTrigger())
      {
        handlePopup(e);
      }
    }

    public void mousePressed(final MouseEvent e)
    {
      if (e.isPopupTrigger())
      {
        handlePopup(e);
      }
    }

    public void mouseReleased(final MouseEvent e)
    {
      if (e.isPopupTrigger())
      {
        handlePopup(e);
      }
    }
  }

  private class FrameViewController implements ReportDesignerView
  {
    private PropertyChangeSupport propertyChangeSupport;

    private FrameViewController()
    {
      propertyChangeSupport = new PropertyChangeSupport(this);
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

    public boolean isStructureVisible()
    {
      final Category reportTreeToolWindow = getReportTreeToolWindow();
      return reportTreeToolWindow != null && reportTreeToolWindow.isMinimized() == false;
    }

    public void setStructureVisible(final boolean visible)
    {
      final Category reportTreeToolWindow = getReportTreeToolWindow();
      final boolean oldValue = reportTreeToolWindow.isMinimized() == false;
      reportTreeToolWindow.setMinimized(visible == false);
      propertyChangeSupport.firePropertyChange(STRUCTURE_VISIBLE_PROPERTY, oldValue, visible);
    }

    public boolean isPropertiesEditorVisible()
    {
      final Category attributeToolWindow = getAttributeToolWindow();
      if (attributeToolWindow == null)
      {
        return false;
      }
      return attributeToolWindow.isMinimized() == false;
    }

    public void setPropertiesEditorVisible(final boolean visible)
    {
      final Category attributeToolWindow = getAttributeToolWindow();
      final boolean oldValue = attributeToolWindow.isMinimized() == false;
      attributeToolWindow.setMinimized(visible == false);
      propertyChangeSupport.firePropertyChange(PROPERTIES_EDITOR_VISIBLE_PROPERTY, oldValue, visible);
    }

    public boolean isMessagesVisible()
    {
      final Category inspectionsToolWindow = getInspectionsToolWindow();
      return inspectionsToolWindow != null && !inspectionsToolWindow.isMinimized();
    }

    public void setMessagesVisible(final boolean visible)
    {
      final Category inspectionsToolWindow = getInspectionsToolWindow();
      final boolean oldValue = inspectionsToolWindow.isMinimized() == false;
      inspectionsToolWindow.setMinimized(visible == false);
      propertyChangeSupport.firePropertyChange(MESSAGES_VISIBLE_PROPERTY, oldValue, visible);
    }

    public boolean isPreviewVisible()
    {
      final JTabbedPane reportEditorPane = getReportEditorPane();
      if (reportEditorPane == null || reportEditorPane.getTabCount() == 0)
      {
        return false;
      }
      if (reportEditorPane.getSelectedComponent() instanceof ReportRendererComponent)
      {
        final ReportRendererComponent rendererComponent = (ReportRendererComponent) reportEditorPane.getSelectedComponent();
        return !rendererComponent.isDesignVisible();
      }
      return false;
    }

    public void setPreviewVisible(final boolean visible)
    {
      final boolean oldValue = isPreviewVisible();
      final JTabbedPane reportEditorPane = getReportEditorPane();
      if (reportEditorPane != null && reportEditorPane.getTabCount() > 0)
      {
        if (reportEditorPane.getSelectedComponent() instanceof ReportRendererComponent)
        {
          final ReportRendererComponent rendererComponent = (ReportRendererComponent) reportEditorPane.getSelectedComponent();
          if (visible)
          {
            rendererComponent.showPreview();
          }
          else
          {
            rendererComponent.showDesign();
          }
        }
      }
      propertyChangeSupport.firePropertyChange(PREVIEW_VISIBLE_PROPERTY, oldValue, visible);
    }

    public boolean isWelcomeVisible()
    {
      if (welcomePane == null)
      {
        return false;
      }
      return welcomePane.isVisible();
    }

    public void setWelcomeVisible(final boolean visible)
    {
      if (welcomePane == null)
      {
        return;
      }
      welcomePane.setVisible(visible);
      welcomePane.toFront();
    }

    public boolean isFieldSelectorVisible()
    {
      if (fieldSelectorPaletteDialog == null)
      {
        return false;
      }
      return fieldSelectorPaletteDialog.isVisible();
    }

    public void setFieldSelectorVisible(final boolean visible)
    {
      if (fieldSelectorPaletteDialog == null)
      {
        return;
      }
      fieldSelectorPaletteDialog.setVisible(visible);
      fieldSelectorPaletteDialog.toFront();
    }

    public void redrawAll()
    {
      getReportEditorPane().invalidate();
      getReportEditorPane().revalidate();
      getReportEditorPane().repaint();
    }
  }

  private class StructureAndDataTabChangeHandler implements ChangeListener
  {
    private StructureAndDataTabChangeHandler()
    {
    }

    public void stateChanged(final ChangeEvent e)
    {
      final ElementPropertiesPanel attributeEditorPanel = getAttributeEditorPanel();
      if (attributeEditorPanel == null)
      {
        return;
      }
      final ReportRenderContext activeContext = getContext().getActiveContext();
      if (activeContext == null)
      {
        return;
      }
      final JTabbedPane tabs = (JTabbedPane) e.getSource();
      if (tabs.getSelectedIndex() == 0)
      {
        attributeEditorPanel.setAllowAttributeCard(true);
        attributeEditorPanel.setAllowDataSourceCard(false);
        attributeEditorPanel.setAllowExpressionCard(false);
        attributeEditorPanel.reset(activeContext.getSelectionModel());
      }
      else
      {
        attributeEditorPanel.setAllowAttributeCard(false);
        attributeEditorPanel.setAllowDataSourceCard(true);
        attributeEditorPanel.setAllowExpressionCard(true);
        attributeEditorPanel.reset(activeContext.getSelectionModel());
      }
    }
  }

  private class ShowWelcomeScreenTask implements Runnable
  {
    public void run()
    {
      viewController.setWelcomeVisible(WorkspaceSettings.getInstance().isShowLauncher());
    }
  }

  private class FancyTabbedPane extends JTabbedPane
  {
    /**
     * Creates an empty <code>TabbedPane</code> with a default
     * tab placement of <code>JTabbedPane.TOP</code>.
     *
     * @see #addTab
     */
    private FancyTabbedPane()
    {
    }

    protected void paintComponent(final Graphics g)
    {
      super.paintComponent(g);
      if (getTabCount() == 0)
      {
        final Image img = CanvasImageLoader.getInstance().getBackgroundImage().getImage();
        g.drawImage(img, 0, 0, this);
      }
    }
  }

  private class DragSelectionToggleHandler implements AWTEventListener
  {
    private DragSelectionToggleHandler()
    {
    }

    /**
     * Invoked when an event is dispatched in the AWT.
     */
    public void eventDispatched(final AWTEvent event)
    {
      if (event instanceof KeyEvent == false)
      {
        return;
      }
      final KeyEvent keyevent = (KeyEvent) event;
      if (KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow() ==
          ReportDesignerFrame.this)
      {
        context.setSelectionWaiting(keyevent.isShiftDown());
      }
    }
  }

  private class PageTextHandler implements PropertyChangeListener
  {
    /**
     * This method gets called when a bound property is changed.
     *
     * @param evt A PropertyChangeEvent object describing the event source
     *            and the property that has changed.
     */
    public void propertyChange(final PropertyChangeEvent evt)
    {
      statusBar.setPages(context.getPage(), context.getPageTotal());
    }
  }

  private static final Log logger = LogFactory.getLog(ReportDesignerFrame.class);
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

  public ReportDesignerFrame() throws XulException
  {
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    final ImageIcon icon = IconLoader.getInstance().getProductIcon();
    if (icon != null)
    {
      setIconImage(icon.getImage());
    }

    setTitle(Messages.getString("ReportDesignerFrame.Title"));
    addWindowListener(new WindowCloseHandler());

    viewController = new FrameViewController();
    context = new DefaultReportDesignerContext(this, viewController);

    welcomePane = new WelcomePane(ReportDesignerFrame.this, getContext());
    fieldSelectorPaletteDialog = new FieldSelectorPaletteDialog(ReportDesignerFrame.this, getContext());

    statusBar = new StatusBar(context);

    reportEditorPane = new FancyTabbedPane();
    reportEditorPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
    reportEditorPane.getModel().addChangeListener(new ReportTabActivationHandler());
    reportEditorPane.addMouseListener(new ReportTabPanePopupHandler());

    dockingPane = new GlobalPane(false);
    dockingPane.setMainComponent(reportEditorPane);
    initializeToolWindows();

    final JPanel contentPane = new JPanel();
    contentPane.setLayout(new BorderLayout());
    contentPane.add(createToolBar("main-toolbar"), BorderLayout.NORTH); // NON-NLS
    contentPane.add(dockingPane, BorderLayout.CENTER);
    contentPane.add(statusBar, BorderLayout.SOUTH);
    contentPane.add(createPaletteToolBar(), BorderLayout.WEST);
    setContentPane(contentPane);

    setJMenuBar(createMenuBar());
    setDropTarget(new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, new DefaultDropTargetListener()));

    context.addPropertyChangeListener(ReportDesignerContext.ACTIVE_CONTEXT_PROPERTY, new ReportTabActivationHandler());
    context.addPropertyChangeListener(ReportDesignerContext.STATUS_TEXT_PROPERTY, new StatusTextHandler());
    context.addPropertyChangeListener(ReportDesignerContext.PAGE_PROPERTY, new PageTextHandler());
    context.addPropertyChangeListener(ReportDesignerContext.REPORT_RENDER_CONTEXT_PROPERTY, new ReportEditorContextHandler());

    Toolkit.getDefaultToolkit().addAWTEventListener(new DragSelectionToggleHandler(), AWTEvent.KEY_EVENT_MASK);

    if (MacOSXIntegration.MAC_OS_X)
    {
      try
      {
        final AboutAction aboutAction = new AboutAction();
        aboutAction.setReportDesignerContext(context);

        final SettingsAction settingsAction = new SettingsAction();
        settingsAction.setReportDesignerContext(context);

        final QuitAction quitAction = new QuitAction();
        quitAction.setReportDesignerContext(context);

        final OpenReportAction openReportAction = new OpenReportAction();
        openReportAction.setReportDesignerContext(context);

        MacOSXIntegration.setOpenFileAction(openReportAction);
        MacOSXIntegration.setAboutAction(aboutAction);
        MacOSXIntegration.setPreferencesAction(settingsAction);
        MacOSXIntegration.setQuitAction(quitAction);
      }
      catch (Throwable e)
      {
        DebugLog.log("Failed to activate MacOS-X integration", e); // NON-NLS
      }
    }
  }

  public void initWindowLocations(final File[] filesToOpen)
  {
    fieldSelectorPaletteDialog.initWindowLocation();
    viewController.setFieldSelectorVisible(WorkspaceSettings.getInstance().isFieldSelectorVisible());

    welcomePane.setLocationRelativeTo(ReportDesignerFrame.this);

    if (filesToOpen == null || filesToOpen.length == 0)
    {
      if (WorkspaceSettings.getInstance().isReopenLastReport())
      {
        final File[] recentFiles = context.getRecentFilesModel().getRecentFiles();
        if (recentFiles.length > 0)
        {
          SwingUtilities.invokeLater(new OpenReportAction.OpenReportTask(recentFiles[0], context));
          return;
        }
      }

      /*
      * PRD-1676:  Since the report designer frame will steal the focus from the Welcome dialog
      * we must let the report designer frame finish it's initialization before we try to display
      * the welcome panel.
      */
      SwingUtilities.invokeLater(new ShowWelcomeScreenTask());
    }
    else
    {
      for (int i = 0; i < filesToOpen.length; i++)
      {
        final File file = filesToOpen[i];
        SwingUtilities.invokeLater(new OpenReportAction.OpenReportTask(file, context));
      }
    }
  }

  public FrameViewController getViewController()
  {
    return viewController;
  }

  private JComponent createToolBar(final String id)
  {
    final JComponent toolBar = context.getXulDesignerFrame().getToolBar(id);
    if (toolBar instanceof JToolBar)
    {
      final JToolBar realToolBar = (JToolBar) toolBar;
      realToolBar.setFloatable(false);
    }
    return toolBar;
  }

  protected Category getAttributeToolWindow()
  {
    return attributeToolWindow;
  }

  protected Category getReportTreeToolWindow()
  {
    return reportTreeToolWindow;
  }

  protected Category getInspectionsToolWindow()
  {
    return inspectionsToolWindow;
  }

  protected ElementPropertiesPanel getAttributeEditorPanel()
  {
    return attributeEditorPanel;
  }

  protected JTabbedPane getReportEditorPane()
  {
    return reportEditorPane;
  }

  protected DefaultReportDesignerContext getContext()
  {
    return context;
  }

  protected StatusBar getStatusBar()
  {
    return statusBar;
  }

  private JMenuBar createMenuBar()
  {
    final JMenuBar menuBar = context.getXulDesignerFrame().getMenuBar();

    createRecentFilesMenu();
    createZoomMenu();
    createMorphMenu();
    createInsertElementsMenu();
    createInsertDataSourcesMenu();
    createSamplesMenu();
    return menuBar;
  }

  private void createInsertDataSourcesMenu()
  {
    final JMenu insertDataSourcesMenu = context.getXulDesignerFrame().getMenuById("insert-datasources-menu");// NON-NLS
    if (insertDataSourcesMenu != null)
    {
      ContextMenuUtility.createDataSourceMenu(context, insertDataSourcesMenu);
    }
  }

  private void createInsertElementsMenu()
  {
    final XulMenupopup insertElementsMenu = context.getXulDesignerFrame().getXulMenuPopupById("insert-elements-popup");// NON-NLS
    if (insertElementsMenu != null)
    {
      final ElementMetaData[] datas = ElementTypeRegistry.getInstance().getAllElementTypes();
      Arrays.sort(datas, new GroupedMetaDataComparator());
      Object grouping = null;
      boolean firstElement = true;
      for (int i = 0; i < datas.length; i++)
      {
        final ElementMetaData data = datas[i];
        if (data.isHidden())
        {
          continue;
        }
        
        final String currentGrouping = data.getGrouping(Locale.getDefault());
        if (firstElement == false)
        {
          if (ObjectUtilities.equal(currentGrouping, grouping) == false)
          {
            grouping = currentGrouping;
            SwingMenuseparator separator = new SwingMenuseparator(null, null, null, "menuseparator");
            insertElementsMenu.addChild(separator);
          }
        }
        else
        {
          grouping = currentGrouping;
          firstElement = false;
        }
        
        final InsertElementAction action = new InsertElementAction(data);
        action.setReportDesignerContext(context);
        ActionSwingMenuitem menuItem = new ActionSwingMenuitem(ActionSwingMenuitem.MENUITEM);
        menuItem.setAction(action);
        insertElementsMenu.addChild(menuItem);
      }
    }
  }

  private void createRecentFilesMenu()
  {
    final XulComponent reopenMenu = context.getXulDesignerFrame().getXulComponentById("file-reopen-popup"); // NON-NLS
    final XulComponent clearMenuitem = context.getXulDesignerFrame().getXulComponentById("file-clear-recent");// NON-NLS
    if (reopenMenu instanceof XulMenupopup && clearMenuitem instanceof XulMenuitem)
    {
      final RecentFilesUpdateHandler updateHandler = new RecentFilesUpdateHandler
          (context, context.getXulDesignerFrame(), (XulMenupopup) reopenMenu, (XulMenuitem) clearMenuitem);
      updateHandler.settingsChanged();
      context.getRecentFilesModel().addSettingsListener(updateHandler);
    }
  }

  private void createZoomMenu()
  {
    final XulComponent zoomMenu = context.getXulDesignerFrame().getXulComponentById("view-zoom-selection-popup");// NON-NLS
    if (zoomMenu instanceof XulMenupopup)
    {
      final InternalZoomAction zoom50action = new InternalZoomAction(50);
      final InternalZoomAction zoom100action = new InternalZoomAction(100);
      final InternalZoomAction zoom200action = new InternalZoomAction(200);
      final InternalZoomAction zoom400action = new InternalZoomAction(400);

      zoom50action.setReportDesignerContext(context);
      zoom100action.setReportDesignerContext(context);
      zoom200action.setReportDesignerContext(context);
      zoom400action.setReportDesignerContext(context);

      zoomMenu.addChild(context.getXulDesignerFrame().createMenu(zoom50action));
      zoomMenu.addChild(context.getXulDesignerFrame().createMenu(zoom100action));
      zoomMenu.addChild(context.getXulDesignerFrame().createMenu(zoom200action));
      zoomMenu.addChild(context.getXulDesignerFrame().createMenu(zoom400action));
    }
  }

  private void createSamplesMenu()
  {
    final XulComponent samplesopup = context.getXulDesignerFrame().getXulComponentById("help-samples-popup");// NON-NLS
    if (samplesopup instanceof XulMenupopup == false)
    {
      return;
    }

    final XulMenupopup xulMenupopup = (XulMenupopup) samplesopup;
    final TreeModel treeModel = SamplesTreeBuilder.getSampleTreeModel();
    final Object root = treeModel.getRoot();
    try
    {
      insertReports(treeModel, root, xulMenupopup);
    }
    catch (XulException e)
    {
      logger.warn("Failed to initialize sample menu", e);
    }
  }

  private void insertReports(final TreeModel model,
                             final Object currentLevel,
                             final XulMenupopup popup) throws XulException
  {
    final int childCount = model.getChildCount(currentLevel);
    for (int i = 0; i < childCount; i += 1)
    {
      final XulDesignerFrame frame = context.getXulDesignerFrame();

      final Object child = model.getChild(currentLevel, i);
      if (model.isLeaf(child))
      {
        final DefaultMutableTreeNode node = (DefaultMutableTreeNode) child;
        final File file = new File(String.valueOf(node.getUserObject()));
        final OpenSampleReportAction action = new OpenSampleReportAction(file, node.toString());
        action.setReportDesignerContext(context);
        popup.addChild(frame.createMenu(action));
      }
      else
      {
        final XulLoader xulLoader = frame.getWindow().getXulDomContainer().getXulLoader();
        final XulMenu menu = (XulMenu) xulLoader.createElement("MENU");
        menu.setLabel(String.valueOf(child));
        popup.addChild(menu);

        final XulMenupopup childPopup = (XulMenupopup) xulLoader.createElement("MENUPOPUP");
        menu.addChild(childPopup);
        insertReports(model, child, childPopup);
      }
    }
  }

  private void createMorphMenu()
  {
    final JMenu morphMenu = context.getXulDesignerFrame().getMenuById("modify-morph-menu");// NON-NLS
    if (morphMenu != null)
    {
      final ElementMetaData[] datas = ElementTypeRegistry.getInstance().getAllElementTypes();
      Arrays.sort(datas, new GroupedMetaDataComparator());
      Object grouping = null;
      boolean firstElement = true;
      for (int i = 0; i < datas.length; i++)
      {
        final ElementMetaData data = datas[i];
        if (data.isHidden())
        {
          continue;
        }
        final String currentGrouping = data.getGrouping(Locale.getDefault());
        if (firstElement == false)
        {
          if (ObjectUtilities.equal(currentGrouping, grouping) == false)
          {
            grouping = currentGrouping;
            morphMenu.addSeparator();
          }
        }
        else
        {
          grouping = currentGrouping;
          firstElement = false;
        }

        try
        {
          final MorphAction action = new MorphAction(data.create());
          action.setReportDesignerContext(context);
          morphMenu.add(new JMenuItem(action));
        }
        catch (InstantiationException e)
        {
          UncaughtExceptionsModel.getInstance().addException(e);
        }
      }

    }
  }

  private void initializeToolWindows()
  {
    this.reportTreeToolWindow = createStructureTreeToolWindow();
    this.attributeToolWindow = createAttributesToolWindow();
    this.inspectionsToolWindow = createInspectionsToolWindow();

    dockingPane.add(GlobalPane.Alignment.RIGHT, reportTreeToolWindow);
    dockingPane.add(GlobalPane.Alignment.RIGHT, attributeToolWindow);
    dockingPane.add(GlobalPane.Alignment.BOTTOM, inspectionsToolWindow);
    dockingPane.setPreferredContentSize(GlobalPane.Alignment.BOTTOM, 100);
    dockingPane.setPreferredContentSize(GlobalPane.Alignment.RIGHT, 300);
    // initialize add elements
    initAddElements("popup-Band.add-element-menu");// NON-NLS
    initAddElements("popup-RootLevelBand.add-element-menu");// NON-NLS
    initSetBarcodeTypeElements("popup-Barcode.type");// NON-NLS

  }

  private Category createAttributesToolWindow()
  {
    attributeEditorPanel = new ElementPropertiesPanel();
    attributeEditorPanel.setAllowAttributeCard(true);
    attributeEditorPanel.setReportDesignerContext(context);

    final ImageIcon propertyTableIcon = IconLoader.getInstance().getPropertyTableIcon();

    return new Category(propertyTableIcon, Messages.getString("Attribute.Title"), attributeEditorPanel);// NON-NLS
  }

  private Category createInspectionsToolWindow()
  {
    // this is the bottom 'messages' panel
    final InspectionSidePanePanel inspectionsMessagePanel = new InspectionSidePanePanel();
    inspectionsMessagePanel.setReportDesignerContext(context);

    final InternalWindow inspectionGadgetInternalWindow = new InternalWindow(Messages.getString("InspectionGadget.Title"));// NON-NLS
    inspectionGadgetInternalWindow.add(inspectionsMessagePanel, BorderLayout.CENTER);

    final Category category = new Category
        (IconLoader.getInstance().getMessagesIcon(),
            Messages.getString("Messages.Title"),// NON-NLS
            inspectionGadgetInternalWindow);
    category.setMinimized(true);
    return category;
  }

  private Category createStructureTreeToolWindow()
  {
    // report structure
    final StructureTreePanel reportTree = new StructureTreePanel(ReportTree.RENDER_TYPE.REPORT);
    reportTree.setReportDesignerContext(context);
    final JPanel structurePanel = new JPanel(new BorderLayout());
    final JComponent structureToolBar = createToolBar("report-structure-toolbar");// NON-NLS
    structurePanel.add(structureToolBar, BorderLayout.NORTH);
    structurePanel.add(reportTree, BorderLayout.CENTER);

    final JPanel dataPanel = new JPanel(new BorderLayout());

    final JComponent dataToolBar = createToolBar("report-fields-toolbar");// NON-NLS
    dataPanel.add(dataToolBar, BorderLayout.NORTH);
    final StructureTreePanel dataTree = new StructureTreePanel(ReportTree.RENDER_TYPE.DATA);
    dataTree.setReportDesignerContext(context);
    dataPanel.add(dataTree, BorderLayout.CENTER);

    final JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
    tabs.addChangeListener(new StructureAndDataTabChangeHandler());
    tabs.add(Messages.getString("StructureView.Structure"), structurePanel);// NON-NLS
    tabs.add(Messages.getString("StructureView.Data"), dataPanel);// NON-NLS

    return new Category
        (IconLoader.getInstance().getReportTreeIcon(),
            Messages.getString("StructureView.Title"),// NON-NLS
            tabs);

  }


  private void initSetBarcodeTypeElements(final String id)
  {
    final JMenu menu = context.getXulDesignerFrame().getMenuById(id);
    if (menu != null)
    {
      final BarcodeTypePropertyEditor editor = new BarcodeTypePropertyEditor();
      final String[] tags = editor.getTags();
      for (int i = 0; i < tags.length; i++)
      {
        final String tag = tags[i];
        final BarcodeTypeAction action = new BarcodeTypeAction(tag);
        action.setReportDesignerContext(context);
        menu.add(new JRadioButtonMenuItem(action));
      }
    }
  }

  private void initAddElements(final String id)
  {
    final JMenu menu = context.getXulDesignerFrame().getMenuById(id);
    if (menu != null)
    {
      final ElementMetaData[] datas = ElementTypeRegistry.getInstance().getAllElementTypes();
      Arrays.sort(datas, new GroupedMetaDataComparator());
      Object grouping = null;
      boolean firstElement = true;
      for (int i = 0; i < datas.length; i++)
      {
        final ElementMetaData data = datas[i];
        if (data.isHidden())
        {
          continue;
        }
        if (WorkspaceSettings.getInstance().isShowExpertItems() == false && data.isExpert())
        {
          continue;
        }
        if (WorkspaceSettings.getInstance().isShowDeprecatedItems() == false && data.isDeprecated())
        {
          continue;
        }

        final String currentGrouping = data.getGrouping(Locale.getDefault());
        if (firstElement == false)
        {
          if (ObjectUtilities.equal(currentGrouping, grouping) == false)
          {
            grouping = currentGrouping;
            menu.addSeparator();
          }
        }
        else
        {
          grouping = currentGrouping;
          firstElement = false;
        }
        final InsertElementAction action = new InsertElementAction(data);
        action.setReportDesignerContext(context);
        menu.add(new JMenuItem(action));
      }
    }
  }

  private JComponent createPaletteToolBar()
  {
    final JToolBar toolBar = new JToolBar();
    toolBar.setFloatable(false);
    toolBar.setOrientation(JToolBar.VERTICAL);

    final ElementMetaData[] datas = ElementTypeRegistry.getInstance().getAllElementTypes();
    Arrays.sort(datas, new GroupedMetaDataComparator());
    Object grouping = null;
    boolean firstElement = true;
    for (int i = 0; i < datas.length; i++)
    {
      final ElementMetaData data = datas[i];
      if (data.isHidden())
      {
        continue;
      }
      if (WorkspaceSettings.getInstance().isShowExpertItems() == false && data.isExpert())
      {
        continue;
      }
      if (WorkspaceSettings.getInstance().isShowDeprecatedItems() == false && data.isDeprecated())
      {
        continue;
      }

      final String currentGrouping = data.getGrouping(Locale.getDefault());
      if (firstElement == false)
      {
        if (ObjectUtilities.equal(currentGrouping, grouping) == false)
        {
          grouping = currentGrouping;
          toolBar.addSeparator();
        }
      }
      else
      {
        grouping = currentGrouping;
        firstElement = false;
      }
      final InsertElementAction action = new InsertElementAction(data);
      action.setReportDesignerContext(context);
      toolBar.add(new PaletteButton(data, context));
    }

    final JScrollPane paletteScrollpane = new JScrollPane(toolBar);
    paletteScrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    paletteScrollpane.addComponentListener(new ScrollbarSyncHandler(paletteScrollpane.getVerticalScrollBar(), toolBar));
    return paletteScrollpane;
  }

  private void rebuildReportMenu()
  {
    final XulDesignerFrame xulDesignerFrame = context.getXulDesignerFrame();
    final XulComponent reopenMenu = xulDesignerFrame.getXulComponentById("window.reports-area");// NON-NLS
    if (reopenMenu == null)
    {
      return;
    }

    final List<XulComponent> xulComponents = reopenMenu.getChildNodes();
    final XulComponent[] objects = xulComponents.toArray(new XulComponent[xulComponents.size()]);
    for (int i = 0; i < objects.length; i++)
    {
      final XulComponent object = objects[i];
      reopenMenu.removeChild(object);
    }

    final JTabbedPane tabbedPane = getReportEditorPane();
    final int count = tabbedPane.getTabCount();
    if (count > 0)
    {
      reopenMenu.addChild(new SwingMenuseparator(null, null, null, "menu-separator")); // NON-NLS
      for (int i = 0; i < count; i++)
      {
        final Component at = tabbedPane.getTabComponentAt(i);
        final String tabName;
        if (at instanceof TabRenderer)
        {
          final TabRenderer renderer = (TabRenderer) at;
          tabName = renderer.getTitle();
        }
        else
        {
          tabName = tabbedPane.getTitleAt(i);
        }
        final SelectTabAction action = new SelectTabAction(i, tabName);
        final ActionSwingMenuitem actionSwingMenuitem = xulDesignerFrame.createMenu(action);
        actionSwingMenuitem.setReportDesignerContext(context);
        reopenMenu.addChild(actionSwingMenuitem);
      }
    }
  }

  private static String computeTabName(final AbstractReportDefinition report)
  {
    if (report instanceof MasterReport)
    {
      final MasterReport mreport = (MasterReport) report;
      final Object title = mreport.getBundle().getMetaData().getBundleAttribute
          (ODFMetaAttributeNames.DublinCore.NAMESPACE, ODFMetaAttributeNames.DublinCore.TITLE);
      if (title instanceof String)
      {
        return (String) title;
      }
    }

    final String name = report.getName();
    if (StringUtils.isEmpty(name) == false)
    {
      return name;
    }

    final String theSavePath = (String) report.getAttribute(ReportDesignerBoot.DESIGNER_NAMESPACE, "report-save-path");// NON-NLS
    if (!StringUtils.isEmpty(theSavePath))
    {
      final String fileName = IOUtils.getInstance().getFileName(theSavePath);
      return IOUtils.getInstance().stripFileExtension(fileName);
    }

    if (report instanceof MasterReport)
    {
      return Messages.getString("ReportDesignerFrame.TabName.UntitledReport");// NON-NLS
    }
    else if (report instanceof CrosstabElement)
    {
      return Messages.getString("ReportDesignerFrame.TabName.UntitledCrosstab");// NON-NLS
    }
    else
    {
      return Messages.getString("ReportDesignerFrame.TabName.UntitledSubReport");// NON-NLS
    }
  }

  protected void updateFrameTitle()
  {
    final int i = getReportEditorPane().getSelectedIndex();
    final MasterReport report;
    final String reportName;
    if (getContext() != null && getContext().getActiveContext() != null)
    {
      if (i == -1)
      {
        reportName = null;
      }
      else
      {
        final Component at = getReportEditorPane().getTabComponentAt(i);
        if (at instanceof TabRenderer)
        {
          final TabRenderer renderer = (TabRenderer) at;
          reportName = renderer.getTitle();
        }
        else
        {
          reportName = getReportEditorPane().getTitleAt(i);
        }
      }

      report = getContext().getActiveContext().getMasterReportElement();
    }
    else
    {
      report = null;
      reportName = null;
    }
    setTitle(computeFrameTitle(reportName, report));
  }

  private String computeFrameTitle(final String tabName, final MasterReport report)
  {
    if (tabName == null || report == null)
    {
      return Messages.getString("ReportDesignerFrame.Title");// NON-NLS
    }

    final String path = (String) report.getAttribute(ReportDesignerBoot.DESIGNER_NAMESPACE, "report-save-path");// NON-NLS
    if (StringUtils.isEmpty(path))
    {
      return Messages.getString("ReportDesignerFrame.TitleWithName", tabName);// NON-NLS
    }
    else
    {
      return Messages.getString("ReportDesignerFrame.TitleWithNameAndPath", tabName, path);// NON-NLS
    }
  }

  private void recomputeAllTabTitles()
  {
    final JTabbedPane editorPane = getReportEditorPane();
    final int count = editorPane.getTabCount();
    for (int i = 0; i < count; i++)
    {
      final Component at = editorPane.getTabComponentAt(i);
      if (at instanceof TabRenderer)
      {
        final TabRenderer renderer = (TabRenderer) at;
        renderer.setTitle(renderer.recomputeTabName());
      }
    }

    updateFrameTitle();
  }


}
