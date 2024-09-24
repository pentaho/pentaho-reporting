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
 * Copyright (c) 2017 Hitachi Vantara, Simba Management Limited and Contributors...  All rights reserved.
 */
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
