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
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.core.meta;

import java.io.ByteArrayOutputStream;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessTask;
import org.pentaho.reporting.engine.classic.core.ReportProcessTaskUtil;
import org.pentaho.reporting.engine.classic.core.metadata.ReportProcessTaskRegistry;

public class ReportProcessTaskRegistryTest extends TestCase
{
  public ReportProcessTaskRegistryTest()
  {
  }

  public ReportProcessTaskRegistryTest(final String s)
  {
    super(s);
  }

  protected void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }

  public void testRegistrationComplete()
  {
    final ReportProcessTaskRegistry registry = ReportProcessTaskRegistry.getInstance();
    assertTrue(registry.getExportTypes().length > 0);
    assertTrue(registry.isExportTypeRegistered("pageable/X-AWT-Graphics"));

    final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    final ReportProcessTask processTask = registry.createProcessTask("pageable/X-AWT-Graphics");
    ReportProcessTaskUtil.configureBodyStream(processTask, byteArrayOutputStream, "image", null);
    processTask.setReport(new MasterReport());
    processTask.run();
    assertTrue(String.valueOf(processTask.getError()), processTask.isTaskSuccessful());
    assertTrue(byteArrayOutputStream.toByteArray().length > 0);
  }
}
