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
 *  Copyright (c) 2006 - 2015 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.bugs;

import java.io.ByteArrayOutputStream;
import java.net.URL;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PdfReportUtil;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import com.lowagie.text.pdf.PRStream;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;

public class Prd5780Test {

  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void checkPdfImagesForCorruption() throws Exception {
    URL u = getClass().getResource( "Prd-5780.prpt" );
    final MasterReport report =
        (MasterReport) new ResourceManager().createDirectly( u, MasterReport.class ).getResource();
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    PdfReportUtil.createPDF( report, bout );

    PdfReader reader = new PdfReader( bout.toByteArray() );
    for ( int i = 1; i < reader.getXrefSize(); i += 1 ) {
      final PdfObject pdfObject = reader.getPdfObject( i );
      if ( pdfObject.isStream() ) {
        PRStream stream = (PRStream) pdfObject;
        if ( PdfName.IMAGE.equals( stream.get( PdfName.SUBTYPE ) ) ) {
          byte[] raw = PdfReader.getStreamBytesRaw( stream, reader.getSafeFile() );
          if ( PdfName.FLATEDECODE.equals( stream.get( PdfName.FILTER ) ) ) {
            Assert.assertNotNull( "Stream is corrupted.", PdfReader.FlateDecode( raw, true ) );
          }
        }
      }
    }

  }
}