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
 * Copyright (c) 2000 - 2017 Hitachi Vantara, Simba Management Limited and Contributors...  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf;

import com.lowagie.text.DocWriter;
import com.lowagie.text.pdf.PdfReader;
import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Ignore;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * Checks, whether the given report is encrypted.
 *
 * @author Thomas Morgner
 */
public class PdfEncryptionValidateIT extends TestCase {
  private static final Log logger = LogFactory.getLog( PdfEncryptionValidateIT.class );

  public PdfEncryptionValidateIT() {
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

//  @Ignore
//  public void testSaveEncrypted() throws Exception {
//    final URL url = getClass().getResource( "pdf-encryption-validate.xml" );
//    assertNotNull( url );
//    final ResourceManager resourceManager = new ResourceManager();
//    resourceManager.registerDefaults();
//    final Resource directly = resourceManager.createDirectly( url, MasterReport.class );
//    final MasterReport report = (MasterReport) directly.getResource();
//
//    final byte[] b = createPDF( report );
//    final PdfReader reader = new PdfReader( b, DocWriter.getISOBytes( "Duck" ) );
//    assertTrue( reader.isEncrypted() );
//  }

  /**
   * Saves a report to PDF format.
   *
   * @param report
   *          the report.
   * @return true or false.
   */
  private static byte[] createPDF( final MasterReport report ) {
    final ByteArrayOutputStream bout = new ByteArrayOutputStream();
    OutputStream out = null;
    try {
      out = new BufferedOutputStream( bout );
      PdfReportUtil.createPDF( report, out );
    } catch ( Exception e ) {
      logger.error( "Writing PDF failed.", e );
    } finally {
      try {
        if ( out != null ) {
          out.close();
        }
      } catch ( Exception e ) {
        logger.error( "Saving PDF failed.", e );
      }
    }
    return bout.toByteArray();
  }

}
