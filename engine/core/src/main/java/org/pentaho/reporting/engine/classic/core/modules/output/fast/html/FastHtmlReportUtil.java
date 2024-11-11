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


package org.pentaho.reporting.engine.classic.core.modules.output.fast.html;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.validator.ReportStructureValidator;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.FileSystemURLRewriter;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlReportUtil;
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.DefaultNameGenerator;
import org.pentaho.reporting.libraries.repository.stream.StreamRepository;

import java.io.IOException;
import java.io.OutputStream;

public class FastHtmlReportUtil {
  public static void processStreamHtml( MasterReport report, OutputStream out ) throws ReportProcessingException,
    IOException {
    ReportStructureValidator validator = new ReportStructureValidator();
    if ( validator.isValidForFastProcessing( report ) == false ) {
      HtmlReportUtil.createStreamHTML( report, out );
      return;
    }

    final StreamRepository targetRepository = new StreamRepository( out );
    final ContentLocation targetRoot = targetRepository.getRoot();
    final FastHtmlContentItems contentItems = new FastHtmlContentItems();
    contentItems.setContentWriter( targetRoot, new DefaultNameGenerator( targetRoot, "index", "html" ) );
    contentItems.setDataWriter( null, null );
    contentItems.setUrlRewriter( new FileSystemURLRewriter() );

    final FastHtmlExportProcessor reportProcessor = new FastHtmlExportProcessor( report, contentItems );
    reportProcessor.processReport();
    reportProcessor.close();
    out.flush();
  }
}
