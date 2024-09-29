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

package org.pentaho.reporting.engine.classic.core.modules.gui.pdf;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.ExportTaskCommon;
import org.pentaho.reporting.engine.classic.core.modules.gui.rtf.RTFExportTask;
import org.pentaho.reporting.libraries.base.config.DefaultConfiguration;
import org.pentaho.reporting.libraries.base.config.ModifiableConfiguration;

import java.io.File;

public class PDFExportTaskTest extends ExportTaskCommon {
  @Test
  public void testCreateParentFolder() {
    String filename = getTestDirectory() + "/" +"testfile.pdf";

    MasterReport report = Mockito.mock( MasterReport.class );
    ModifiableConfiguration conf = Mockito.spy( new DefaultConfiguration() );
    conf.setConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.gui.pdf.TargetFileName", filename );
    conf.setConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.gui.pdf.CreateParentFolder", "true" );
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
      PdfExportTask task = new PdfExportTask( report, null, null );
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
}
