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

package org.pentaho.reporting.engine.classic.core.bugs;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.filter.types.ContentType;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.layout.output.DefaultProcessingContext;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriter;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugExpressionRuntime;
import org.pentaho.reporting.libraries.docbundle.BundleUtilities;
import org.pentaho.reporting.libraries.docbundle.DocumentBundle;
import org.pentaho.reporting.libraries.docbundle.MemoryDocumentBundle;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Prd3319IT extends TestCase {
  public Prd3319IT() {
  }

  public Prd3319IT( final String name ) {
    super( name );
  }

  public void setUp() {
    ClassicEngineBoot.getInstance().start();
    new File( "bin/test-tmp" ).mkdirs();
  }

  public void testLoadSaveFromDisk() throws Exception {
    final ResourceKey key = createImageKey();

    final Element element = new Element();
    element.setElementType( new ContentType() );
    element.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, key );
    // .. save it.
    final MasterReport report = new MasterReport();
    report.getReportHeader().addElement( element );

    saveReport( report, new File( "bin/test-tmp/prd-3319-load-save-disk-1.prpt" ) );

    // load it to establish the context in all resource-keys ..
    final ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    final Resource resource =
        mgr.createDirectly( new File( "bin/test-tmp/prd-3319-load-save-disk-1.prpt" ), MasterReport.class );

    // save it once, that changes the bundle ...
    final MasterReport report2 = (MasterReport) resource.getResource();
    saveReport( report2, new File( "bin/test-tmp/prd-3319-load-save-disk-2.prpt" ) );
    // save it twice, that triggers the crash...
    saveReport( report2, new File( "bin/test-tmp/prd-3319-load-save-disk-2.prpt" ) );

    final ProcessingContext processingContext = new DefaultProcessingContext();
    final DebugExpressionRuntime runtime = new DebugExpressionRuntime( new DefaultTableModel(), 0, processingContext );

    final Element reportElement = report2.getReportHeader().getElement( 0 );
    Object attribute = reportElement.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE );

    assertTrue( attribute instanceof ResourceKey );
    ResourceKey atKey = (ResourceKey) attribute;
    assertEquals( "http://127.0.0.1:65535/image.jpg", atKey.getIdentifierAsString() );
  }

  /**
   * This method does what the report designer does on save.
   *
   * @param report
   * @param file
   * @throws Exception
   */
  private void saveReport( final MasterReport report, final File file ) throws Exception {
    BundleWriter.writeReportToZipFile( report, file );
    final ResourceManager resourceManager = report.getResourceManager();
    final Resource bundleResource = resourceManager.createDirectly( file, DocumentBundle.class );
    final DocumentBundle bundle = (DocumentBundle) bundleResource.getResource();
    final ResourceKey bundleKey = bundle.getBundleKey();

    final MemoryDocumentBundle mem = new MemoryDocumentBundle();
    BundleUtilities.copyStickyInto( mem, bundle );
    BundleUtilities.copyMetaData( mem, bundle );
    report.setBundle( mem );
    report.setContentBase( mem.getBundleMainKey() );
    report.setDefinitionSource( bundleKey );
  }

  private ResourceKey createImageKey() throws IOException, ResourceKeyCreationException {
    final ResourceManager resMgr = new ResourceManager();
    resMgr.registerDefaults();
    return resMgr.createKey( new URL( "http://127.0.0.1:65535/image.jpg" ) );
  }
}
