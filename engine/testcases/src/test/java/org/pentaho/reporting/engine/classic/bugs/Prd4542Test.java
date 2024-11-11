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


package org.pentaho.reporting.engine.classic.bugs;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriter;
import org.pentaho.reporting.libraries.base.util.NullOutputStream;
import org.pentaho.reporting.libraries.docbundle.BundleUtilities;
import org.pentaho.reporting.libraries.docbundle.DocumentBundle;
import org.pentaho.reporting.libraries.docbundle.MemoryDocumentBundle;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.IOException;
import java.net.URL;

public class Prd4542Test extends TestCase {
  public Prd4542Test() {
  }

  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testLoadAndSavePlain() throws Exception {
    URL source = getClass().getResource( "Prd-4542.prpt" );
    ResourceManager mgr = new ResourceManager();
    MasterReport report = (MasterReport) mgr.createDirectly( source, MasterReport.class ).getResource();
    BundleWriter.writeReportToZipStream( report, new NullOutputStream() );
  }

  public void testLoadAndSaveForEdit() throws Exception {
    URL source = getClass().getResource( "Prd-4542.prpt" );
    ResourceManager mgr = new ResourceManager();
    MasterReport report = loadReport( source, mgr );
    BundleWriter.writeReportToZipStream( report, new NullOutputStream() );
  }

  // This is how PRD loads reports for editing
  public static MasterReport loadReport( final Object selectedFile, final ResourceManager resourceManager )
    throws ResourceException, IOException {
    final Resource directly = resourceManager.createDirectly( selectedFile, MasterReport.class );
    final MasterReport resource = (MasterReport) directly.getResource();
    final DocumentBundle bundle = resource.getBundle();
    if ( bundle == null ) {
      // Ok, that should not happen if we work with the engine's parsers, but better safe than sorry.
      final MemoryDocumentBundle documentBundle = new MemoryDocumentBundle( resource.getContentBase() );
      documentBundle.getWriteableDocumentMetaData().setBundleType( ClassicEngineBoot.BUNDLE_TYPE );
      resource.setBundle( documentBundle );
      resource.setContentBase( documentBundle.getBundleMainKey() );
    } else {
      final MemoryDocumentBundle mem = new MemoryDocumentBundle( resource.getContentBase() );
      BundleUtilities.copyStickyInto( mem, bundle );
      BundleUtilities.copyMetaData( mem, bundle );
      resource.setBundle( mem );
      resource.setContentBase( mem.getBundleMainKey() );
    }

    final Object visible =
      resource.getBundle().getMetaData().getBundleAttribute( ClassicEngineBoot.METADATA_NAMESPACE, "visible" );//NON-NLS
    if ( "true".equals( visible ) )//NON-NLS
    {
      resource.setAttribute( AttributeNames.Pentaho.NAMESPACE, "visible", Boolean.TRUE );//NON-NLS
    } else if ( "false".equals( visible ) )//NON-NLS
    {
      resource.setAttribute( AttributeNames.Pentaho.NAMESPACE, "visible", Boolean.FALSE );//NON-NLS
    }
    return resource;
  }
}
