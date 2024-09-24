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

package org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics;

import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportParameterValidationException;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.event.ReportProgressEvent;
import org.pentaho.reporting.engine.classic.core.event.ReportProgressListener;
import org.pentaho.reporting.engine.classic.core.layout.output.PageState;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.base.PageableReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.internal.DrawablePrintable;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.internal.GraphicsOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.internal.QueryPhysicalPageInterceptor;

/**
 * Creation-Date: 09.04.2007, 13:28:33
 *
 * @author Thomas Morgner
 */
public class PrintReportProcessor extends PageableReportProcessor implements Pageable {
  private static final Log logger = LogFactory.getLog( PrintReportProcessor.class );
  private Throwable error;

  public PrintReportProcessor( final MasterReport report ) throws ReportProcessingException {
    super( report, new GraphicsOutputProcessor( report.getConfiguration(), report.getResourceManager() ) );
    setFullStreamingProcessor( false );
  }

  protected GraphicsOutputProcessor getGraphicsProcessor() {
    return (GraphicsOutputProcessor) getOutputProcessor();
  }

  /**
   * Returns the number of pages in the set. To enable advanced printing features, it is recommended that
   * <code>Pageable</code> implementations return the true number of pages rather than the UNKNOWN_NUMBER_OF_PAGES
   * constant.
   *
   * @return the number of pages in this <code>Pageable</code>.
   */
  public synchronized int getNumberOfPages() {
    if ( isError() ) {
      return 0;
    }

    if ( isPaginated() == false ) {
      try {
        prepareReportProcessing();
        PrintReportProcessor.logger.debug( "After pagination, we have " + getGraphicsProcessor().getPhysicalPageCount()
            + " physical pages." ); // NON-NLS
      } catch ( ReportParameterValidationException e ) {
        error = e;
        return 0;
      } catch ( Exception e ) {
        PrintReportProcessor.logger.error( "PrintReportProcessor: ", e ); // NON-NLS
        error = e;
        return 0;
      }
    }
    return getGraphicsProcessor().getPhysicalPageCount();
  }

  /**
   * Manually triggers the pagination. This method will block until the pagination is finished and will do nothing if an
   * error occurred.
   *
   * @return true, if the pagination was successful, false otherwise.
   */
  public synchronized boolean paginate() {
    if ( isError() ) {
      return false;
    }

    if ( isPaginated() == false ) {
      try {
        prepareReportProcessing();
        return true;
      } catch ( Exception e ) {
        error = e;
        return false;
      }
    }
    return true;
  }

  /**
   * Returns the <code>PageFormat</code> of the page specified by <code>pageIndex</code>.
   *
   * @param pageIndex
   *          the zero based index of the page whose <code>PageFormat</code> is being requested
   * @return the <code>PageFormat</code> describing the size and orientation.
   * @throws IndexOutOfBoundsException
   *           if the <code>Pageable</code> does not contain the requested page.
   */
  public synchronized PageFormat getPageFormat( final int pageIndex ) throws IndexOutOfBoundsException {
    if ( isError() ) {
      return null;
    }

    if ( isPaginated() == false ) {
      try {
        prepareReportProcessing();
      } catch ( Exception e ) {
        error = e;
        return null;
      }
    }

    try {
      final PageDrawable pageDrawable = processPage( pageIndex );
      return pageDrawable.getPageFormat();
    } catch ( Exception e ) {
      PrintReportProcessor.logger.error( "Failed to return a valid pageformat: ", e ); // NON-NLS
      throw new IllegalStateException( "Unable to return a valid pageformat." );
    }
  }

  /**
   * Returns the <code>Printable</code> instance responsible for rendering the page specified by <code>pageIndex</code>.
   *
   * @param pageIndex
   *          the zero based index of the page whose <code>Printable</code> is being requested
   * @return the <code>Printable</code> that renders the page.
   * @throws IndexOutOfBoundsException
   *           if the <code>Pageable</code> does not contain the requested page.
   */
  public synchronized Printable getPrintable( final int pageIndex ) throws IndexOutOfBoundsException {
    if ( isError() ) {
      return null;
    }

    if ( isPaginated() == false ) {
      try {
        prepareReportProcessing();
      } catch ( Exception e ) {
        error = e;
        return null;
      }
    }

    try {
      final PageDrawable pageDrawable = processPage( pageIndex );
      return new DrawablePrintable( pageDrawable );
    } catch ( Exception e ) {
      PrintReportProcessor.logger.error( "Failed to return a valid pageable object: ", e ); // NON-NLS
      throw new IllegalStateException( "Unable to return a valid pageformat." );
    }
  }

  /**
   * Returns the <code>PageDrawable</code> instance responsible for rendering the page specified by
   * <code>pageIndex</code>.
   *
   * @param pageIndex
   *          the zero based index of the page whose <code>Printable</code> is being requested
   * @return the <code>PageDrawable</code> that renders the page.
   * @throws IndexOutOfBoundsException
   *           if the <code>Pageable</code> does not contain the requested page.
   */
  public synchronized PageDrawable getPageDrawable( final int pageIndex ) {
    if ( isError() ) {
      return null;
    }

    if ( isPaginated() == false ) {
      try {
        prepareReportProcessing();
      } catch ( Exception e ) {
        error = e;
        PrintReportProcessor.logger.error( "Failed to paginate", e ); // NON-NLS
        return null;
      }
    }

    try {
      return processPage( pageIndex );
    } catch ( Exception e ) {
      error = e;
      PrintReportProcessor.logger.error( "Failed to process the page", e ); // NON-NLS
      throw new IllegalStateException( "Unable to return a valid pageformat." );
    }
  }

  /**
   * An internal method that returns the page-drawable for the given page.
   *
   * @param page
   *          the page number.
   * @return the pagedrawable for the given page.
   * @throws ReportProcessingException
   *           if a report processing error occurred.
   */
  protected PageDrawable processPage( final int page ) throws ReportProcessingException {
    final GraphicsOutputProcessor outputProcessor = getGraphicsProcessor();
    try {
      // set up the scene. We can assume that the report has been paginated by now ..
      PageState state = getPhysicalPageState( page );
      final QueryPhysicalPageInterceptor interceptor =
          new QueryPhysicalPageInterceptor( outputProcessor.getPhysicalPage( page ) );
      outputProcessor.setInterceptor( interceptor );
      while ( interceptor.isMoreContentNeeded() ) {
        state = processPage( state, true );
      }
      return interceptor.getDrawable();
    } finally {
      outputProcessor.setInterceptor( null );
    }
  }

  /**
   * Checks whether an error occurred. The Exception itself can be queried using 'getErrorReason()'.
   *
   * @return true, if an error occurred, false otherwise.
   */
  public boolean isError() {
    return error != null;
  }

  /**
   * This method throws an UnsupportedOperationException as printing is a passive process and cannot be started here. To
   * print the whole report, use this Pageable implementation and pass it to one of the JDKs printing sub-systems.
   *
   * @throws ReportProcessingException
   */
  public void processReport() throws ReportProcessingException {
    throw new UnsupportedOperationException( "Printing is a passive process." );
  }

  public void fireProcessingStarted() {
    fireProcessingStarted( new ReportProgressEvent( this ) );
  }

  public void fireProcessingFinished() {
    fireProcessingFinished( new ReportProgressEvent( this ) );
  }

  /**
   * Sends a repagination update to all registered listeners.
   *
   * @param state
   *          the state.
   */
  protected synchronized void fireStateUpdate( final ReportProgressEvent state ) {
    super.fireStateUpdate( state );
  }

  /**
   * Sends a repagination update to all registered listeners.
   *
   * @param state
   *          the state.
   */
  protected synchronized void fireProcessingStarted( final ReportProgressEvent state ) {
    super.fireProcessingStarted( state );
  }

  /**
   * Sends a repagination update to all registered listeners.
   *
   * @param state
   *          the state.
   */
  protected synchronized void fireProcessingFinished( final ReportProgressEvent state ) {
    super.fireProcessingFinished( state );
  }

  /**
   * Adds a repagination listener. This listener will be informed of pagination events.
   *
   * @param l
   *          the listener.
   */
  public synchronized void addReportProgressListener( final ReportProgressListener l ) {
    super.addReportProgressListener( l );
  }

  /**
   * Removes a repagination listener.
   *
   * @param l
   *          the listener.
   */
  public synchronized void removeReportProgressListener( final ReportProgressListener l ) {
    super.removeReportProgressListener( l );
  }

  /**
   * Returns the last exception that has been caught.
   *
   * @return the error reason.
   */
  public Throwable getErrorReason() {
    return error;
  }
}
