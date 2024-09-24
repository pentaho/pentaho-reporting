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
package org.pentaho.reporting.engine.classic.core.modules.gui.rtf;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import org.junit.Assert;
import org.mockito.Mockito;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.ExportTaskCommon;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ReportProgressDialog;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.SwingGuiContext;

import org.junit.Test;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.config.DefaultConfiguration;
import org.pentaho.reporting.libraries.base.config.ModifiableConfiguration;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.File;
import java.io.IOException;

public class RTFExportTaskTest extends ExportTaskCommon {
  @Test
  public void testCreateParentFolder() {
    String filename = getTestDirectory() + "/" + "testfile.rtf";

    MasterReport report = Mockito.mock( MasterReport.class );
    ModifiableConfiguration conf = Mockito.spy( new DefaultConfiguration() );
    conf.setConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.gui.rtf.FileName", filename );
    conf.setConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.gui.rtf.CreateParentFolder", "true" );
    Mockito.doReturn(conf).when(report).getConfiguration();
    File file = null;
    File directory;
    try {
      file = new File( filename ).getCanonicalFile();
      directory = file.getParentFile();
      if ( directory != null ) {
        if ( directory.exists() == true ) {
          directory.delete();
        }
      }
      Assert.assertFalse( directory.exists() );
      RTFExportTask task = new RTFExportTask( report, null, null );
      task.run();
    } catch (Exception e1) {
      e1.printStackTrace();
    }
    directory = file.getParentFile();
    Assert.assertTrue( directory != null );
    Assert.assertTrue( directory.exists() );
    file.delete();
    directory.delete();
  }

  @Test
  public void testExportToRTFFile() throws Exception {
    String filename = "./testfile.rtf";
    MasterReport report = Mockito.mock( MasterReport.class );
    ModifiableConfiguration conf = Mockito.spy( new DefaultConfiguration() );
    conf.setConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.gui.rtf.FileName", filename );
    Mockito.doReturn( conf ).when( report ).getConfiguration();
    File file = new File( filename ).getCanonicalFile();
    Assert.assertFalse( file.exists() );
    RTFExportTask task = new RTFExportTask( report, null, null );
    task.run();
    Assert.assertTrue( file != null );
    Assert.assertTrue( file.exists() );
    file.delete();
  }
}
