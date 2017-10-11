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

package org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.ArrayClassFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.URLClassFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.datasource.DefaultDataSourceFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.elements.DefaultElementFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.objects.BandLayoutClassFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.objects.DefaultClassFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.stylekey.DefaultStyleKeyFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.stylekey.PageableLayoutStyleKeyFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.templates.DefaultTemplateCollection;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriter;
import org.pentaho.reporting.engine.classic.core.modules.parser.extwriter.ReportWriterException;
import org.pentaho.reporting.libraries.base.util.MemoryStringWriter;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

public class PlainTextExportIT extends TestCase {
  public PlainTextExportIT() {
  }

  public PlainTextExportIT( final String s ) {
    super( s );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  private String exportReport( final MasterReport template, final String encoding ) throws Exception {
    final MasterReport report = (MasterReport) template.clone();
    report.getReportConfiguration().setConfigProperty(
        "org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.Encoding", encoding );
    final ByteArrayOutputStream bo = new ByteArrayOutputStream();
    PlainTextReportUtil.createPlainText( report, bo, 15, 10 );
    final byte[] data = bo.toByteArray();
    return new String( data, encoding );
  }

  public void testExport() throws Exception {
    final URL url = getClass().getResource( "plain-text-export.xml" );
    assertNotNull( url );
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly( url, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();

    assertNotNull( report );
    final String rdefBeforeFirst = writeReport( report );
    final String utf16 = exportReport( report, "UTF-16" );
    final String rdefAfterFirst = writeReport( report );
    assertEquals( rdefBeforeFirst, rdefAfterFirst );
    final String utf8 = exportReport( report, "UTF-8" );
    assertEquals( utf8, utf16 );
  }

  private String writeReport( final MasterReport report ) throws IOException, ReportWriterException {
    final MemoryStringWriter oWriter = new MemoryStringWriter();
    final ReportWriter rc = new ReportWriter( report, "UTF-16", ReportWriter.createDefaultConfiguration( report ) );

    rc.addClassFactoryFactory( new URLClassFactory() );
    rc.addClassFactoryFactory( new DefaultClassFactory() );
    rc.addClassFactoryFactory( new BandLayoutClassFactory() );
    rc.addClassFactoryFactory( new ArrayClassFactory() );

    rc.addStyleKeyFactory( new DefaultStyleKeyFactory() );
    rc.addStyleKeyFactory( new PageableLayoutStyleKeyFactory() );
    rc.addTemplateCollection( new DefaultTemplateCollection() );
    rc.addElementFactory( new DefaultElementFactory() );
    rc.addDataSourceFactory( new DefaultDataSourceFactory() );

    rc.write( oWriter );
    oWriter.close();
    return oWriter.toString();

  }
}
