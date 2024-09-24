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

package org.pentaho.reporting.engine.classic.core.metadata;

import java.io.ByteArrayOutputStream;

import junit.framework.TestCase;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessTask;
import org.pentaho.reporting.engine.classic.core.ReportProcessTaskUtil;

public class ReportProcessTaskRegistryTest extends TestCase {
  public ReportProcessTaskRegistryTest() {
  }

  public ReportProcessTaskRegistryTest( final String s ) {
    super( s );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testRegistrationComplete() {
    final ReportProcessTaskRegistry registry = ReportProcessTaskRegistry.getInstance();
    assertTrue( registry.getExportTypes().length > 0 );
    assertTrue( registry.isExportTypeRegistered( "pageable/X-AWT-Graphics" ) );

    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    final ReportProcessTask processTask = registry.createProcessTask( "pageable/X-AWT-Graphics" );
    ReportProcessTaskUtil.configureBodyStream( processTask, byteArrayOutputStream, "image", null );
    processTask.setReport( new MasterReport() );
    processTask.run();
    assertTrue( String.valueOf( processTask.getError() ), processTask.isTaskSuccessful() );
    assertTrue( byteArrayOutputStream.toByteArray().length > 0 );
  }
}
