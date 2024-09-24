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
