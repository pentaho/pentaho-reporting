/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

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
