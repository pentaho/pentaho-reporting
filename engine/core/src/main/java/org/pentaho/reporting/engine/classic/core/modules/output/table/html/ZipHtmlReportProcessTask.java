/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.modules.output.table.html;

import org.pentaho.reporting.engine.classic.core.AbstractReportProcessTask;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.event.ReportProgressListener;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.FlowReportProcessor;
import org.pentaho.reporting.libraries.repository.ContentItem;
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.DefaultNameGenerator;
import org.pentaho.reporting.libraries.repository.NameGenerator;
import org.pentaho.reporting.libraries.repository.RepositoryUtilities;
import org.pentaho.reporting.libraries.repository.zipwriter.ZipRepository;

import java.io.OutputStream;

public class ZipHtmlReportProcessTask extends AbstractReportProcessTask {
  public ZipHtmlReportProcessTask() {
  }

  public void run() {
    if ( isValid() == false ) {
      setError( new ReportProcessingException( "Error: The task is not configured properly." ) );
      return;
    }

    setError( null );
    try {
      final MasterReport report = getReport();
      final ContentLocation contentLocation = getBodyContentLocation();
      final NameGenerator nameGenerator = getBodyNameGenerator();
      final ContentItem contentItem =
          contentLocation.createItem( nameGenerator.generateName( null, "application/zip" ) );
      final OutputStream out = contentItem.getOutputStream();

      try {
        final ZipRepository zipRepository = new ZipRepository( out );
        try {
          final ContentLocation root = zipRepository.getRoot();
          final ContentLocation data =
              RepositoryUtilities.createLocation( zipRepository, RepositoryUtilities.splitPath( "data", "/" ) );

          final FlowHtmlOutputProcessor outputProcessor = new FlowHtmlOutputProcessor();

          final HtmlPrinter printer = new AllItemsHtmlPrinter( report.getResourceManager() );
          printer.setContentWriter( root, new DefaultNameGenerator( root, "report" ) );
          printer.setDataWriter( data, new DefaultNameGenerator( data, "content" ) );
          printer.setUrlRewriter( new SingleRepositoryURLRewriter() );
          outputProcessor.setPrinter( printer );

          final FlowReportProcessor sp = new FlowReportProcessor( report, outputProcessor );
          try {
            final ReportProgressListener[] progressListeners = getReportProgressListeners();
            for ( int i = 0; i < progressListeners.length; i++ ) {
              final ReportProgressListener listener = progressListeners[i];
              sp.addReportProgressListener( listener );
            }
            sp.processReport();
          } finally {
            sp.close();
          }
        } finally {
          zipRepository.close();
        }
      } finally {
        out.close();
      }
    } catch ( Throwable e ) {
      setError( e );
    }

  }

  public String getReportMimeType() {
    return "application/zip";
  }
}
