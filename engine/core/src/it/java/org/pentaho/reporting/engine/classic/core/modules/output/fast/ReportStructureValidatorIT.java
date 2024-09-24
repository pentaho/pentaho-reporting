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
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.fast;

import java.io.IOException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.validator.ReportStructureValidator;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.xls.FastExcelReportUtil;
import org.pentaho.reporting.libraries.base.util.NullOutputStream;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class ReportStructureValidatorIT {
  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testReport() throws ResourceException, ReportProcessingException, IOException {
    URL url = getClass().getResource( "Prd-5129.prpt" );
    MasterReport report = (MasterReport) new ResourceManager().createDirectly( url, MasterReport.class ).getResource();
    ReportStructureValidator v = new ReportStructureValidator();
    Assert.assertTrue( v.isValidForFastProcessing( report ) );
    FastExcelReportUtil.processXls( report, new NullOutputStream() );
  }
}
