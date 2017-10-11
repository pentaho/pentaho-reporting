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
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.gui.common.IconTheme;
import org.pentaho.reporting.engine.classic.core.modules.gui.common.StatusType;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.JStatusBar;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ReportProgressBar;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ReportProgressDialog;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.RequestFocusHandler;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.Messages;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

/**
 * Creation-Date: 11.11.2006, 19:35:16
 *
 * @author Thomas Morgner
 */
public class PreviewDialog extends JDialog {
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
      final PreviewPane previewPane = getPreviewPane();
      final JStatusBar statusBar = getStatusBar();
      if ( PreviewPane.MENU_PROPERTY.equals( propertyName ) ) {
        // Update the menu
        final JMenu[] menus = previewPane.getMenu();
        updateMenu( menus );
        return;
      }

      if ( PreviewPane.TITLE_PROPERTY.equals( propertyName ) ) {
        setTitle( previewPane.getTitle() );
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
          pageLabel.setVisible( false );
          statusBar.setStatus( StatusType.INFORMATION, messages.getString( "PreviewDialog.USER_PAGINATING" ) ); //$NON-NLS-1$
          if ( progressBar != null ) {
            previewPane.addReportProgressListener( progressBar );
            progressBar.setOnlyPagination( true );
            progressBar.setVisible( true );
            progressBar.revalidate();
          }
          if ( progressDialog != null ) {
            previewPane.addReportProgressListener( progressDialog );
            LibSwingUtil.centerDialogInParent( progressDialog );
            progressDialog.setOnlyPagination( true );
            progressDialog.setVisible( true );
          }
        } else {
          pageLabel.setVisible( true );
          statusBar.setStatus( StatusType.NONE, "" ); //$NON-NLS-1$
          if ( progressBar != null ) {
            progressBar.setOnlyPagination( false );
            progressBar.setVisible( false );
            previewPane.removeReportProgressListener( progressBar );
            progressBar.revalidate();
          }
          if ( progressDialog != null ) {
            previewPane.removeReportProgressListener( progressDialog );
            progressDialog.setOnlyPagination( false );
            progressDialog.setVisible( false );
          }
        }
        return;
      }

      if ( PreviewPane.PAGE_NUMBER_PROPERTY.equals( propertyName )
          || PreviewPane.NUMBER_OF_PAGES_PROPERTY.equals( propertyName ) ) {
        pageLabel.setText( previewPane.getPageNumber() + "/" + previewPane.getNumberOfPages() ); //$NON-NLS-1$
        return;
      }

      if ( PreviewPane.CLOSED_PROPERTY.equals( propertyName ) ) {
        if ( previewPane.isClosed() ) {
          setVisible( false );
          dispose();
        } else {
          setVisible( true );
        }
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
        // DebugLog.log("Def-pagination");
      }
      // else
      // {
      // DebugLog.log("No def-pagination");
      // }
    }
  }

  private PreviewPane previewPane;
  private JStatusBar statusBar;
  private ReportProgressBar progressBar;
  private ReportProgressDialog progressDialog;
  private JLabel pageLabel;
  private Messages messages;

  /**
   * Creates a non-modal dialog without a title and without a specified <code>Frame</code> owner. A shared, hidden frame
   * will be set as the owner of the dialog.
   * <p/>
   * This constructor sets the component's locale property to the value returned by
   * <code>JComponent.getDefaultLocale</code>.
   *
   * @throws java.awt.HeadlessException
   *           if GraphicsEnvironment.isHeadless() returns true.
   * @see java.awt.GraphicsEnvironment#isHeadless
   * @see javax.swing.JComponent#getDefaultLocale
   */
  public PreviewDialog() {
    init();
  }

  /**
   * Creates a non-modal dialog without a title with the specified <code>Frame</code> as its owner. If
   * <code>owner</code> is <code>null</code>, a shared, hidden frame will be set as the owner of the dialog.
   * <p/>
   * This constructor sets the component's locale property to the value returned by
   * <code>JComponent.getDefaultLocale</code>.
   *
   * @param owner
   *          the <code>Frame</code> from which the dialog is displayed
   * @throws java.awt.HeadlessException
   *           if GraphicsEnvironment.isHeadless() returns true.
   * @see java.awt.GraphicsEnvironment#isHeadless
   * @see javax.swing.JComponent#getDefaultLocale
   */
  public PreviewDialog( final Frame owner ) {
    super( owner );
    init();
  }

  /**
   * Creates a modal or non-modal dialog without a title and with the specified owner <code>Frame</code>. If
   * <code>owner</code> is <code>null</code>, a shared, hidden frame will be set as the owner of the dialog.
   * <p/>
   * This constructor sets the component's locale property to the value returned by
   * <code>JComponent.getDefaultLocale</code>.
   *
   * @param owner
   *          the <code>Frame</code> from which the dialog is displayed
   * @param modal
   *          true for a modal dialog, false for one that allows others windows to be active at the same time
   * @throws java.awt.HeadlessException
   *           if GraphicsEnvironment.isHeadless() returns true.
   * @see java.awt.GraphicsEnvironment#isHeadless
   * @see javax.swing.JComponent#getDefaultLocale
   */
  public PreviewDialog( final Frame owner, final boolean modal ) {
    super( owner, modal );
    init();
  }

  /**
   * Creates a non-modal dialog without a title with the specified <code>Dialog</code> as its owner.
   * <p/>
   * This constructor sets the component's locale property to the value returned by
   * <code>JComponent.getDefaultLocale</code>.
   *
   * @param owner
   *          the non-null <code>Dialog</code> from which the dialog is displayed
   * @throws java.awt.HeadlessException
   *           if GraphicsEnvironment.isHeadless() returns true.
   * @see java.awt.GraphicsEnvironment#isHeadless
   * @see javax.swing.JComponent#getDefaultLocale
   */
  public PreviewDialog( final Dialog owner ) {
    super( owner );
    init();
  }

  /**
   * Creates a modal or non-modal dialog without a title and with the specified owner dialog.
   * <p/>
   * This constructor sets the component's locale property to the value returned by
   * <code>JComponent.getDefaultLocale</code>.
   *
   * @param owner
   *          the non-null <code>Dialog</code> from which the dialog is displayed
   * @param modal
   *          true for a modal dialog, false for one that allows other windows to be active at the same time
   * @throws java.awt.HeadlessException
   *           if GraphicsEnvironment.isHeadless() returns true.
   * @see java.awt.GraphicsEnvironment#isHeadless
   * @see javax.swing.JComponent#getDefaultLocale
   */
  public PreviewDialog( final Dialog owner, final boolean modal ) {
    super( owner, modal );
    init();
  }

  /**
   * Creates a non-modal dialog without a title and without a specified <code>Frame</code> owner. A shared, hidden frame
   * will be set as the owner of the dialog.
   * <p/>
   * This constructor sets the component's locale property to the value returned by
   * <code>JComponent.getDefaultLocale</code>.
   *
   * @throws java.awt.HeadlessException
   *           if GraphicsEnvironment.isHeadless() returns true.
   * @see java.awt.GraphicsEnvironment#isHeadless
   * @see javax.swing.JComponent#getDefaultLocale
   */
  public PreviewDialog( final MasterReport report ) {
    init();
    setReportJob( report );
  }

  /**
   * Creates a non-modal dialog without a title with the specified <code>Frame</code> as its owner. If
   * <code>owner</code> is <code>null</code>, a shared, hidden frame will be set as the owner of the dialog.
   * <p/>
   * This constructor sets the component's locale property to the value returned by
   * <code>JComponent.getDefaultLocale</code>.
   *
   * @param owner
   *          the <code>Frame</code> from which the dialog is displayed
   * @throws java.awt.HeadlessException
   *           if GraphicsEnvironment.isHeadless() returns true.
   * @see java.awt.GraphicsEnvironment#isHeadless
   * @see javax.swing.JComponent#getDefaultLocale
   */
  public PreviewDialog( final MasterReport report, final Frame owner ) {
    super( owner );
    init();
    setReportJob( report );
  }

  /**
   * Creates a modal or non-modal dialog without a title and with the specified owner <code>Frame</code>. If
   * <code>owner</code> is <code>null</code>, a shared, hidden frame will be set as the owner of the dialog.
   * <p/>
   * This constructor sets the component's locale property to the value returned by
   * <code>JComponent.getDefaultLocale</code>.
   *
   * @param owner
   *          the <code>Frame</code> from which the dialog is displayed
   * @param modal
   *          true for a modal dialog, false for one that allows others windows to be active at the same time
   * @throws java.awt.HeadlessException
   *           if GraphicsEnvironment.isHeadless() returns true.
   * @see java.awt.GraphicsEnvironment#isHeadless
   * @see javax.swing.JComponent#getDefaultLocale
   */
  public PreviewDialog( final MasterReport report, final Frame owner, final boolean modal ) {
    super( owner, modal );
    init();
    setReportJob( report );
  }

  /**
   * Creates a non-modal dialog without a title with the specified <code>Dialog</code> as its owner.
   * <p/>
   * This constructor sets the component's locale property to the value returned by
   * <code>JComponent.getDefaultLocale</code>.
   *
   * @param owner
   *          the non-null <code>Dialog</code> from which the dialog is displayed
   * @throws java.awt.HeadlessException
   *           if GraphicsEnvironment.isHeadless() returns true.
   * @see java.awt.GraphicsEnvironment#isHeadless
   * @see javax.swing.JComponent#getDefaultLocale
   */
  public PreviewDialog( final MasterReport report, final Dialog owner ) {
    super( owner );
    init();
    setReportJob( report );
  }

  /**
   * Creates a modal or non-modal dialog without a title and with the specified owner dialog.
   * <p/>
   * This constructor sets the component's locale property to the value returned by
   * <code>JComponent.getDefaultLocale</code>.
   *
   * @param owner
   *          the non-null <code>Dialog</code> from which the dialog is displayed
   * @param modal
   *          true for a modal dialog, false for one that allows other windows to be active at the same time
   * @throws java.awt.HeadlessException
   *           if GraphicsEnvironment.isHeadless() returns true.
   * @see java.awt.GraphicsEnvironment#isHeadless
   * @see javax.swing.JComponent#getDefaultLocale
   */
  public PreviewDialog( final MasterReport report, final Dialog owner, final boolean modal ) {
    super( owner, modal );
    init();
    setReportJob( report );
  }

  protected void init() {
    setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );

    addComponentListener( new RequestFocusHandler() );
    messages =
        new Messages( getLocale(), SwingPreviewModule.BUNDLE_NAME, ObjectUtilities
            .getClassLoader( SwingPreviewModule.class ) );

    previewPane = new PreviewPane();
    previewPane.setDeferredRepagination( true );
    addComponentListener( new TriggerPaginationListener( previewPane ) );

    statusBar = new JStatusBar( previewPane.getIconTheme() );

    pageLabel = new JLabel();

    final Configuration configuration = ClassicEngineBoot.getInstance().getGlobalConfig();
    final boolean progressBarEnabled = "true".equals( configuration //$NON-NLS-1$
        .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.gui.base.ProgressBarEnabled" ) ); //$NON-NLS-1$
    final boolean progressDialogEnabled = "true".equals( configuration //$NON-NLS-1$
        .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.gui.base.ProgressDialogEnabled" ) ); //$NON-NLS-1$

    if ( progressBarEnabled ) {
      progressBar = new ReportProgressBar();
      progressBar.setVisible( false );
      previewPane.addReportProgressListener( progressBar );
      previewPane.addPropertyChangeListener( new PreviewPanePropertyChangeHandler() );
    } else {
      progressBar = null;
    }

    if ( progressDialogEnabled ) {
      progressDialog = new ReportProgressDialog( this );
      final MasterReport reportJob = previewPane.getReportJob();
      if ( reportJob == null || reportJob.getTitle() == null ) {
        progressDialog.setTitle( messages.getString( "ProgressDialog.EMPTY_TITLE" ) );
        progressDialog.setMessage( messages.getString( "ProgressDialog.EMPTY_TITLE" ) );
      } else {
        progressDialog.setTitle( messages.getString( "ProgressDialog.TITLE", reportJob.getTitle() ) );
        progressDialog.setMessage( messages.getString( "ProgressDialog.TITLE", reportJob.getTitle() ) );
      }
      progressDialog.pack();
    } else {
      progressDialog = null;
    }

    final JComponent extensionArea = statusBar.getExtensionArea();
    extensionArea.setLayout( new BoxLayout( extensionArea, BoxLayout.X_AXIS ) );
    if ( progressBar != null ) {
      extensionArea.add( progressBar );
    }
    extensionArea.add( pageLabel );

    final JComponent contentPane = new JPanel();
    contentPane.setLayout( new BorderLayout() );
    contentPane.add( previewPane, BorderLayout.CENTER );
    contentPane.add( statusBar, BorderLayout.SOUTH );
    setContentPane( contentPane );

    updateMenu( previewPane.getMenu() );
    setTitle( previewPane.getTitle() );
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

  public void dispose() {
    super.dispose();
    previewPane.setClosed( true );
  }

  public PreviewPane getPreviewPane() {
    return previewPane;
  }

  public JStatusBar getStatusBar() {
    return statusBar;
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
