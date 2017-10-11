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

package org.pentaho.reporting.designer.core.editor.preview;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.editor.ZoomModel;
import org.pentaho.reporting.designer.core.editor.ZoomModelListener;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportInterruptedException;
import org.pentaho.reporting.engine.classic.core.ReportParameterValidationException;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.PreviewPane;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ReportProgressBar;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ReportProgressDialog;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.Frame;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Todo: Document me!
 *
 * @author Thomas Morgner
 */
public class ReportPreviewComponent extends JPanel {
  private class StatusHandler implements PropertyChangeListener {
    private PreviewPane previewPane;
    private ReportDesignerContext context;

    private StatusHandler( final PreviewPane previewPane, final ReportDesignerContext context ) {
      this.previewPane = previewPane;
      this.context = context;
    }

    public void propertyChange( final PropertyChangeEvent evt ) {
      if ( PreviewPane.STATUS_TEXT_PROPERTY.equals( evt.getPropertyName() ) ) {
        context.setStatusText( previewPane.getStatusText() );
      }
      final String propertyName = evt.getPropertyName();
      if ( PreviewPane.ERROR_PROPERTY.equals( propertyName ) ) {
        final Throwable error = previewPane.getError();
        if ( error != null
                && error instanceof ReportParameterValidationException == false
                && error instanceof ReportInterruptedException == false ) {
          UncaughtExceptionsModel.getInstance().addException( error );
        }
        return;
      }

      if ( PreviewPane.STATUS_TEXT_PROPERTY.equals( propertyName )
              || PreviewPane.STATUS_TYPE_PROPERTY.equals( propertyName ) ) {
        context.setStatusText( previewPane.getStatusText() );
        return;
      }

      if ( PreviewPane.PAGINATING_PROPERTY.equals( propertyName ) ) {
        if ( Boolean.TRUE.equals( evt.getNewValue() ) ) {

          context.setStatusText( "Paginating..." ); //$NON-NLS-1$
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
            progressDialog.toFront();
          }
        } else {
          context.setStatusText( "" ); //$NON-NLS-1$
          if ( progressBar != null ) {
            progressBar.setOnlyPagination( false );
            progressBar.setVisible( false );
            previewPane.removeReportProgressListener( progressBar );
            progressBar.revalidate();
          }
          if ( progressDialog != null ) {
            previewPane.removeReportProgressListener( progressDialog );
            progressDialog.setOnlyPagination( false );
            progressDialog.dispose();
          }
        }
      }
    }
  }

  private class RequestFocusTask implements Runnable {
    public void run() {
      requestFocusInWindow();
    }
  }

  private class ZoomUpdateHandler implements ZoomModelListener, PropertyChangeListener {
    public void zoomFactorChanged() {
      if ( zoomModel == null ) {
        return;
      }
      setZoom( zoomModel.getZoomAsPercentage() );
    }

    /**
     * This method gets called when a bound property is changed.
     *
     * @param evt A PropertyChangeEvent object describing the event source and the property that has changed.
     */
    public void propertyChange( final PropertyChangeEvent evt ) {
      if ( PreviewPane.ZOOM_PROPERTY.equals( evt.getPropertyName() ) ) {
        final float factor = (float) previewPane.getZoom();
        if ( zoomModel.getZoomAsPercentage() != factor ) {
          zoomModel.setZoomAsPercentage( factor );
        }
      }
    }
  }

  private PreviewPane previewPane;
  private ZoomModel zoomModel;
  private ZoomUpdateHandler zoomUpdateHandler;
  private ReportProgressDialog progressDialog;
  private ReportProgressBar progressBar;

  public ReportPreviewComponent( final ReportDesignerContext context ) {
    if ( context == null ) {
      throw new NullPointerException();
    }

    setBackground( new Color( 0, 0, 0, 0 ) );
    setBorder( new EmptyBorder( 0, 0, 0, 0 ) );
    setOpaque( false );

    setLayout( new BorderLayout() );
    zoomUpdateHandler = new ZoomUpdateHandler();

    previewPane = new DesignerPreviewPane( context );
    previewPane.setOpaque( false );
    previewPane.setBackground( new Color( 0, 0, 0, 0 ) );
    previewPane.setBorder( new EmptyBorder( 0, 0, 0, 0 ) );
    previewPane.addPropertyChangeListener( new StatusHandler( previewPane, context ) );
    add( previewPane, BorderLayout.CENTER );


    final Component parent = context.getView().getParent();
    if ( parent instanceof Dialog ) {
      progressDialog = new ReportProgressDialog( (Dialog) parent );
    } else if ( parent instanceof Frame ) {
      progressDialog = new ReportProgressDialog( (Frame) parent );
    } else {
      progressDialog = new ReportProgressDialog();
    }
    progressDialog.pack();

    progressBar = new ReportProgressBar();
    progressBar.setVisible( false );
    previewPane.addReportProgressListener( progressBar );
  }

  public void updatePreview( final ReportDocumentContext reportRenderContext ) {
    if ( zoomModel != null ) {
      zoomModel.removeZoomModelListener( zoomUpdateHandler );
      previewPane.removePropertyChangeListener( PreviewPane.ZOOM_PROPERTY, zoomUpdateHandler );
    }

    if ( reportRenderContext == null ) {
      previewPane.setReportJob( null );
      zoomModel = null;
    } else {
      final MasterReport reportDialog = reportRenderContext.getContextRoot();
      previewPane.setReportJob( reportDialog );

      zoomModel = reportRenderContext.getZoomModel();
      zoomModel.addZoomModelListener( zoomUpdateHandler );
      previewPane.addPropertyChangeListener( PreviewPane.ZOOM_PROPERTY, zoomUpdateHandler );
      previewPane.setZoom( zoomModel.getZoomAsPercentage() );

      EventQueue.invokeLater( new RequestFocusTask() );
    }
  }


  public void setZoom( final double zoomFactor ) {
    previewPane.setZoom( zoomFactor );
  }

  public double getZoom() {
    return previewPane.getZoom();
  }

  public void dispose() {
    previewPane.setClosed( true );
  }
}
