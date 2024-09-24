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

package org.pentaho.reporting.engine.classic.core.bugs;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.DriverConnectionProvider;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.SQLReportDataFactory;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.net.URL;

public class Prd4843IT {
  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testSampleReport() throws Exception {
    URL resource = getClass().getResource( "Prd-4843.prpt" );
    MasterReport report =
        (MasterReport) new ResourceManager().createDirectly( resource, MasterReport.class ).getResource();
    CompoundDataFactory dataFactory = (CompoundDataFactory) report.getDataFactory();
    SQLReportDataFactory dataFactory1 = (SQLReportDataFactory) dataFactory.get( 0 );
    DriverConnectionProvider conProv1 = (DriverConnectionProvider) dataFactory1.getConnectionProvider();
    Assert.assertEquals( "abcdefghijk", conProv1.getProperty( "user" ) );
    Assert.assertEquals( "abcdefghijk", conProv1.getProperty( "password" ) );
    SQLReportDataFactory dataFactory2 = (SQLReportDataFactory) dataFactory.get( 1 );
    DriverConnectionProvider conProv2 = (DriverConnectionProvider) dataFactory2.getConnectionProvider();
    Assert.assertEquals( "abcdefghijkl", conProv2.getProperty( "user" ) );
    Assert.assertEquals( "abcdefghijkl", conProv2.getProperty( "password" ) );
  }
}
