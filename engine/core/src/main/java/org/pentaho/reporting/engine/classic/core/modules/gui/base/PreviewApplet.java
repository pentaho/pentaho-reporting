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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.gui.base;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BoxLayout;
import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.gui.common.IconTheme;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.JStatusBar;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ReportProgressBar;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.RequestFocusHandler;

/**
 * Creation-Date: 11.11.2006, 19:35:22
 *
 * @author Thomas Morgner
 */
public class PreviewApplet extends JApplet {
  private class PreviewPanePropertyChangeHandler implements PropertyChangeListener {

    protected PreviewPanePropertyChangeHandler() {
    }

    /**
     * This method gets called when a bound property is changed.
     *
     * @param evt
     *          A PropertyChangeEvent object describing the event source and the property that has changed.
     */

    public void propertyChange( final PropertyChangeEvent evt ) {
      final String propertyName = evt.getPropertyName();
      if ( PreviewPane.MENU_PROPERTY.equals( propertyName ) ) {
        // Update the menu
        final JMenu[] menus = previewPane.getMenu();
        if ( menus != null && menus.length > 0 ) {
          final JMenuBar menuBar = new JMenuBar();
          for ( int i = 0; i < menus.length; i++ ) {
            final JMenu menu = menus[i];
            menuBar.add( menu );
          }
          setJMenuBar( menuBar );
        } else {
          setJMenuBar( null );
        }
        return;
      }

      if ( PreviewPane.STATUS_TEXT_PROPERTY.equals( propertyName )
          || PreviewPane.STATUS_TYPE_PROPERTY.equals( propertyName ) ) {
        statusBar.setStatus( previewPane.getStatusType(), previewPane.getStatusText() );
        return;
      }

      if ( PreviewPane.ICON_THEME_PROPERTY.equals( propertyName ) ) {
        statusBar.setIconTheme( previewPane.getIconTheme() );
        return;
      }

      if ( PreviewPane.PAGINATING_PROPERTY.equals( propertyName ) ) {
        if ( Boolean.TRUE.equals( evt.getNewValue() ) ) {
          progressBar.setOnlyPagination( true );
          progressBar.setVisible( true );
          pageLabel.setVisible( false );
        } else {
          progressBar.setOnlyPagination( false );
          progressBar.setVisible( false );
          pageLabel.setVisible( true );
        }
        progressBar.revalidate();
        return;
      }

      if ( PreviewPane.PAGE_NUMBER_PROPERTY.equals( propertyName )
          || PreviewPane.NUMBER_OF_PAGES_PROPERTY.equals( propertyName ) ) {
        pageLabel.setText( previewPane.getPageNumber() + "/" + previewPane.getNumberOfPages() ); //$NON-NLS-1$
      }

    }
  }

  private static class TriggerPaginationListener extends ComponentAdapter {
    private PreviewPane pane;

    private TriggerPaginationListener( final PreviewPane pane ) {
      this.pane = pane;
    }

    /**
     * Invoked when the component has been made visible.
     */
    public void componentShown( final ComponentEvent e ) {
      if ( pane.isDeferredRepagination() ) {
        pane.startPagination();
      }
    }
  }

  private PreviewPane previewPane;
  private JStatusBar statusBar;
  private ReportProgressBar progressBar;
  private JLabel pageLabel;

  public PreviewApplet() {
  }

  public void init() {
    addComponentListener( new RequestFocusHandler() );

    previewPane = new PreviewPane();
    previewPane.setDeferredRepagination( true );
    addComponentListener( new TriggerPaginationListener( previewPane ) );
    statusBar = new JStatusBar( previewPane.getIconTheme() );

    progressBar = new ReportProgressBar();
    progressBar.setVisible( false );
    previewPane.addReportProgressListener( progressBar );

    pageLabel = new JLabel();
    previewPane.addPropertyChangeListener( new PreviewPanePropertyChangeHandler() );

    final JComponent extensionArea = statusBar.getExtensionArea();
    extensionArea.setLayout( new BoxLayout( extensionArea, BoxLayout.X_AXIS ) );
    extensionArea.add( progressBar );
    extensionArea.add( pageLabel );

    final JComponent contentPane = new JPanel();
    contentPane.setLayout( new BorderLayout() );
    contentPane.add( previewPane, BorderLayout.CENTER );
    contentPane.add( statusBar, BorderLayout.SOUTH );
    setContentPane( contentPane );

    updateMenu( previewPane.getMenu() );
    statusBar.setIconTheme( previewPane.getIconTheme() );
    statusBar.setStatus( previewPane.getStatusType(), previewPane.getStatusText() );
  }

  private void updateMenu( final JMenu[] menus ) {
    if ( menus != null && menus.length > 0 ) {
      final JMenuBar menuBar = new JMenuBar();
      for ( int i = 0; i < menus.length; i++ ) {
        final JMenu menu = menus[i];
        menuBar.add( menu );
      }
      setJMenuBar( menuBar );
    } else {
      setJMenuBar( null );
    }
  }

  public ReportController getReportController() {
    return previewPane.getReportController();
  }

  public void setReportController( final ReportController reportController ) {
    previewPane.setReportController( reportController );
  }

  public IconTheme getIconTheme() {
    return previewPane.getIconTheme();
  }

  public void setIconTheme( final IconTheme theme ) {
    previewPane.setIconTheme( theme );
  }

  public MasterReport getReportJob() {
    return previewPane.getReportJob();
  }

  public void setReportJob( final MasterReport reportJob ) {
    previewPane.setReportJob( reportJob );
  }

  public PreviewPane getPreviewPane() {
    return previewPane;
  }

  public boolean isToolbarFloatable() {
    return previewPane.isToolbarFloatable();
  }

  public void setToolbarFloatable( final boolean toolbarFloatable ) {
    previewPane.setToolbarFloatable( toolbarFloatable );
  }

  public double getZoom() {
    return previewPane.getZoom();
  }

  public void setZoom( final double zoom ) {
    previewPane.setZoom( zoom );
  }
}
