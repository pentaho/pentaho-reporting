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
import org.pentaho.reporting.engine.classic.core.DefaultImageReference;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.filter.types.ContentType;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.layout.output.DefaultProcessingContext;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ClassicEngineFactoryParameters;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriter;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugExpressionRuntime;
import org.pentaho.reporting.libraries.docbundle.BundleUtilities;
import org.pentaho.reporting.libraries.docbundle.DocumentBundle;
import org.pentaho.reporting.libraries.docbundle.MemoryDocumentBundle;
import org.pentaho.reporting.libraries.resourceloader.ParameterKey;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import javax.imageio.ImageIO;
import javax.swing.table.DefaultTableModel;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Prd3159IT extends TestCase {
  public Prd3159IT() {
  }

  public Prd3159IT( final String name ) {
    super( name );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
    new File( "bin/test-tmp" ).mkdirs();
  }

  public void testLoadSave() throws Exception {
    final ResourceKey key = createImageKey();

    final Element element = new Element();
    element.setElementType( new ContentType() );
    element.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, key );

    // first, create the report with an embedded resource in it.
    final MasterReport report = new MasterReport();
    report.getReportHeader().addElement( element );
    // .. save it.
    saveReport( report, new File( "bin/test-tmp/prd-3159-load-save-1.prpt" ) );

    // load it to establish the context in all resource-keys ..
    final ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    final Resource resource =
        mgr.createDirectly( new File( "bin/test-tmp/prd-3159-load-save-1.prpt" ), MasterReport.class );

    // save it once, that changes the bundle ...
    final MasterReport report2 = (MasterReport) resource.getResource();
    saveReport( report2, new File( "bin/test-tmp/prd-3159-load-save-2.prpt" ) );
    // save it twice, that triggers the crash...
    saveReport( report2, new File( "bin/test-tmp/prd-3159-load-save-2.prpt" ) );

    final ProcessingContext processingContext = new DefaultProcessingContext();
    final DebugExpressionRuntime runtime = new DebugExpressionRuntime( new DefaultTableModel(), 0, processingContext );

    final Element reportElement = (Element) report2.getReportHeader().getElement( 0 );
    final Object designValue = reportElement.getElementType().getDesignValue( runtime, reportElement );
    final DefaultImageReference image = (DefaultImageReference) designValue;
    assertEquals( 20, image.getImageWidth() );
    assertEquals( 20, image.getImageHeight() );

  }

  public void testLoadSaveFromDisk() throws Exception {
    ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    final Resource orgRes = mgr.createDirectly( Prd3159IT.class.getResource( "Prd-3159.prpt" ), MasterReport.class );
    // .. save it.
    final MasterReport report = (MasterReport) orgRes.getResource();
    saveReport( report, new File( "bin/test-tmp/prd-3159-load-save-disk-1.prpt" ) );

    // load it to establish the context in all resource-keys ..
    final Resource resource =
        mgr.createDirectly( new File( "bin/test-tmp/prd-3159-load-save-disk-1.prpt" ), MasterReport.class );

    // save it once, that changes the bundle ...
    final MasterReport report2 = (MasterReport) resource.getResource();
    saveReport( report2, new File( "bin/test-tmp/prd-3159-load-save-disk-2.prpt" ) );
    // save it twice, that triggers the crash...
    saveReport( report2, new File( "bin/test-tmp/prd-3159-load-save-disk-2.prpt" ) );

    final ProcessingContext processingContext = new DefaultProcessingContext();
    final DebugExpressionRuntime runtime = new DebugExpressionRuntime( new DefaultTableModel(), 0, processingContext );

    final Element reportElement = (Element) report2.getPageHeader().getElement( 4 );
    final Object designValue = reportElement.getElementType().getDesignValue( runtime, reportElement );
    final DefaultImageReference image = (DefaultImageReference) designValue;
    assertEquals( 456, image.getImageWidth() );
    assertEquals( 69, image.getImageHeight() );

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

    final ByteArrayOutputStream bout = new ByteArrayOutputStream();
    ImageIO.write( new BufferedImage( 20, 20, BufferedImage.TYPE_INT_ARGB ), "png", bout );

    final String mimeType = "image/png";
    final String pattern = "resources/image{0}.png"; // NON-NLS
    final Map<ParameterKey, Object> parameters = new HashMap<ParameterKey, Object>();
    parameters.put( ClassicEngineFactoryParameters.ORIGINAL_VALUE, "c:\\invalid dir\\test.png" );
    parameters.put( ClassicEngineFactoryParameters.MIME_TYPE, mimeType );
    parameters.put( ClassicEngineFactoryParameters.PATTERN, pattern );
    parameters.put( ClassicEngineFactoryParameters.EMBED, "true" ); // NON-NLS
    // create an embedded key in here.
    return resMgr.createKey( bout.toByteArray(), parameters );
  }
}

// need parser fix: On PRD load, the resource-keys loaded must be made embedded-as-edited, and thus must
// be byte-arrays on the key with the bundle-key as parent.
// On save: No change needed.
