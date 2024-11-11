/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.modules.gui.print;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.event.ReportProgressListener;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.PrintReportProcessor;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.config.ModifiableConfiguration;

import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

/**
 * Creation-Date: 05.09.2005, 18:36:03
 *
 * @author Thomas Morgner
 */
public class PrintUtil {
  private static final Log logger = LogFactory.getLog( PrintUtil.class );

  public static final String PRINTER_JOB_NAME_KEY =
      "org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.print.JobName"; //$NON-NLS-1$
  public static final String NUMBER_COPIES_KEY =
      "org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.print.NumberOfCopies"; //$NON-NLS-1$

  private PrintUtil() {
  }

  public static void printDirectly( final MasterReport report ) throws PrinterException, ReportProcessingException {
    printDirectly( report, null );
  }

  public static void printDirectly( final MasterReport report, final ReportProgressListener progressListener )
    throws PrinterException, ReportProcessingException {
    final ModifiableConfiguration reportConfiguration = report.getReportConfiguration();
    final String jobName = reportConfiguration.getConfigProperty( PRINTER_JOB_NAME_KEY, report.getTitle() );

    final PrinterJob printerJob = PrinterJob.getPrinterJob();
    if ( jobName != null ) {
      printerJob.setJobName( jobName );
    }

    final PrintReportProcessor reportPane = new PrintReportProcessor( report );
    if ( progressListener != null ) {
      reportPane.addReportProgressListener( progressListener );
    }
    printerJob.setPageable( reportPane );
    try {
      printerJob.setCopies( getNumberOfCopies( reportConfiguration ) );
      printerJob.print();
    } finally {
      reportPane.close();
      if ( progressListener != null ) {
        reportPane.removeReportProgressListener( progressListener );
      }
    }
  }

  public static boolean print( final MasterReport report ) throws PrinterException, ReportProcessingException {
    return print( report, null );
  }

  public static boolean print( final MasterReport report, final ReportProgressListener progressListener )
    throws PrinterException, ReportProcessingException {
    final ModifiableConfiguration reportConfiguration = report.getReportConfiguration();
    final String jobName = reportConfiguration.getConfigProperty( PRINTER_JOB_NAME_KEY, report.getTitle() );

    final PrinterJob printerJob = PrinterJob.getPrinterJob();
    if ( jobName != null ) {
      printerJob.setJobName( jobName );
    }

    final PrintReportProcessor reportPane = new PrintReportProcessor( report );
    if ( progressListener != null ) {
      reportPane.addReportProgressListener( progressListener );
    }

    try {
      reportPane.fireProcessingStarted();
      printerJob.setPageable( reportPane );
      printerJob.setCopies( getNumberOfCopies( reportConfiguration ) );
      if ( printerJob.printDialog() ) {
        printerJob.print();
        return true;
      }
      return false;
    } finally {
      reportPane.fireProcessingFinished();
      reportPane.close();
      if ( progressListener != null ) {
        reportPane.removeReportProgressListener( progressListener );
      }
    }
  }

  public static int getNumberOfCopies( final Configuration configuration ) {
    try {
      return Math.max( 1, Integer.parseInt( configuration.getConfigProperty( NUMBER_COPIES_KEY, "1" ) ) ); //$NON-NLS-1$
    } catch ( Exception e ) {
      logger.warn( "PrintUtil: Number of copies declared for the report is invalid" ); //$NON-NLS-1$
      return 1;
    }
  }
}
