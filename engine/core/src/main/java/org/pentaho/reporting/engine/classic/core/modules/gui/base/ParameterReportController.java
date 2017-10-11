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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.parameters.ReportParameterDefinition;
import org.pentaho.reporting.engine.classic.core.util.ReportParameterValues;

/**
 * The default controller for all reports (unless redefined by the user). This controller is responsible for providing a
 * parameter-UI for the reports.
 *
 * @author Thomas Morgner
 */
public class ParameterReportController implements ReportController {
  private class ParameterUpdateHandler implements ChangeListener {
    private ParameterUpdateHandler() {
    }

    /**
     * Invoked when the target of the listener has changed its state.
     *
     * @param e
     *          a ChangeEvent object
     */
    public void stateChanged( final ChangeEvent e ) {
      final PreviewPane previewPane = getPreviewPane();
      if ( previewPane == null ) {
        return;
      }
      final ReportParameterValues properties = getControllerPane().getReportParameterValues();
      final MasterReport masterReport = previewPane.getReportJob();
      final ReportParameterValues reportParameters = masterReport.getParameterValues();
      final String[] strings = properties.getColumnNames();
      for ( int i = 0; i < strings.length; i++ ) {
        final String propertyName = strings[i];
        reportParameters.put( propertyName, properties.get( propertyName ) );
      }
      previewPane.setReportJob( masterReport );
    }
  }

  private class ReportUpdateHandler implements PropertyChangeListener {
    private ReportUpdateHandler() {
    }

    /**
     * This method gets called when a bound property is changed.
     *
     * @param evt
     *          A PropertyChangeEvent object describing the event source and the property that has changed.
     */

    public void propertyChange( final PropertyChangeEvent evt ) {
      final MasterReport report = getPreviewPane().getReportJob();
      final ParameterReportControllerPane reportControllerPane = getControllerPane();
      try {
        reportControllerPane.setReport( report );
      } catch ( ReportProcessingException e ) {
        // Cannot recover from that
        reportControllerPane.setErrorMessage( e.getMessage() );
        logger.debug( "Failed", e ); // NON-NLS
      }
    }
  }

  private static final Log logger = LogFactory.getLog( ParameterReportController.class );
  private ParameterReportControllerPane controllerPane;
  private PreviewPane previewPane;
  private ParameterReportController.ReportUpdateHandler reportUpdateHandler;
  private static final JMenu[] EMPTY_MENUS = new JMenu[0];

  public ParameterReportController() {
    reportUpdateHandler = new ReportUpdateHandler();

    controllerPane = new ParameterReportControllerPane();
    controllerPane.addChangeListener( new ParameterUpdateHandler() );
  }

  protected PreviewPane getPreviewPane() {
    return previewPane;
  }

  protected ParameterReportControllerPane getControllerPane() {
    return controllerPane;
  }

  /**
   * Returns the graphical representation of the controller. This component will be added between the menu bar and the
   * toolbar.
   * <p/>
   * Changes to this property are not detected automatically, you have to call "refreshController" whenever you want to
   * display a completely new control panel.
   *
   * @return the controller component.
   */
  public JComponent getControlPanel() {
    final MasterReport report = controllerPane.getReport();
    if ( report == null ) {
      return null;
    }
    final ReportParameterDefinition definition = report.getParameterDefinition();
    if ( definition == null ) {
      return null;
    }
    if ( definition.getParameterCount() == 0 ) {
      return null;
    }
    return controllerPane;
  }

  /**
   * Returns the menus that should be inserted into the menubar.
   * <p/>
   * Changes to this property are not detected automatically, you have to call "refreshControler" whenever the contents
   * of the menu array changed.
   *
   * @return the menus as array, never null.
   */
  public JMenu[] getMenus() {
    return EMPTY_MENUS;
  }

  /**
   * Defines, whether the controller component is placed between the preview pane and the toolbar.
   *
   * @return true, if this is a inner component.
   */
  public boolean isInnerComponent() {
    return true;
  }

  /**
   * Returns the location for the report controller, one of BorderLayout.NORTH, BorderLayout.SOUTH, BorderLayout.EAST or
   * BorderLayout.WEST.
   *
   * @return the location;
   */
  public String getControllerLocation() {
    return BorderLayout.NORTH;
  }

  public void initialize( final PreviewPane pane ) {
    try {
      if ( this.previewPane != null ) {
        this.previewPane.removePropertyChangeListener( PreviewPane.REPORT_JOB_PROPERTY, reportUpdateHandler );
        this.controllerPane.setReport( null );
      }
      this.previewPane = pane;
      if ( this.previewPane != null ) {
        this.previewPane.addPropertyChangeListener( PreviewPane.REPORT_JOB_PROPERTY, reportUpdateHandler );
        this.controllerPane.setReport( previewPane.getReportJob() );
      }
    } catch ( ReportProcessingException e ) {
      // Cannot recover from that
      controllerPane.setErrorMessage( e.getMessage() );
      logger.debug( "Failed", e ); // NON-NLS
    }

  }

  /**
   * Called when the report controller gets removed.
   *
   * @param pane
   */
  public void deinitialize( final PreviewPane pane ) {
    try {
      this.previewPane.removePropertyChangeListener( PreviewPane.REPORT_JOB_PROPERTY, reportUpdateHandler );
      this.controllerPane.setReport( null );
    } catch ( ReportProcessingException e ) {
      // Cannot recover from that
      controllerPane.setErrorMessage( e.getMessage() );
      logger.debug( "Failed", e ); // NON-NLS
    }
  }
}
