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

package org.pentaho.reporting.engine.classic.extensions.datasources.pmd;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeDataSchemaModel;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugJndiContextFactoryBuilder;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import javax.naming.spi.NamingManager;
import java.net.URL;

public class Prd4125Test extends TestCase {
  public Prd4125Test() {
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
    if ( NamingManager.hasInitialContextFactoryBuilder() == false ) {
      NamingManager.setInitialContextFactoryBuilder( new DebugJndiContextFactoryBuilder() );
    }
  }

  public void testRuntime() throws Exception {
    URL resource = getClass().getResource( "Prd-4125.prpt" );
    ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    MasterReport report = (MasterReport) mgr.createDirectly( resource, MasterReport.class ).getResource();

    DebugReportRunner.executeAll( report );
  }

  public void testDesignTime() throws ResourceException {
    URL resource = getClass().getResource( "Prd-4125.prpt" );
    ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    MasterReport report = (MasterReport) mgr.createDirectly( resource, MasterReport.class ).getResource();

    DesignTimeDataSchemaModel model = new DesignTimeDataSchemaModel( report );
    String[] columnNames = model.getColumnNames();
    assertEquals( 26, columnNames.length );
  }
}
