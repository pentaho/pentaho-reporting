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
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriter;
import org.pentaho.reporting.libraries.base.util.MemoryByteArrayOutputStream;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class PmdDataFactoryWriteTest extends TestCase {
  public PmdDataFactoryWriteTest() {
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }


  protected MasterReport postProcess( final MasterReport originalReport ) throws Exception {
    final MemoryByteArrayOutputStream bout = new MemoryByteArrayOutputStream();
    BundleWriter.writeReportToZipStream( originalReport, bout );
    assertTrue( bout.getLength() > 0 );

    final ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    final Resource reportRes = mgr.createDirectly( bout.toByteArray(), MasterReport.class );
    return (MasterReport) reportRes.getResource();
  }

  public void testWrite() throws Exception {
    final PmdDataFactory df = new PmdDataFactory();
    df.setGlobalScript( "GlobalScript" );
    df.setGlobalScriptLanguage( "GlobalScriptLanguage" );
    df.setQuery( "QueryName", "QueryText", "ScriptLanguage", "Script" );
    final MasterReport report = new MasterReport();
    report.setDataFactory( df );

    final MasterReport masterReport = postProcess( report );
    final PmdDataFactory dataFactory = (PmdDataFactory) masterReport.getDataFactory();
    assertEquals( "QueryName", dataFactory.getQueryNames()[ 0 ] );
    assertEquals( "QueryText", dataFactory.getQuery( "QueryName" ) );
    assertEquals( "ScriptLanguage", dataFactory.getScriptingLanguage( "QueryName" ) );
    assertEquals( "Script", dataFactory.getScript( "QueryName" ) );
    assertEquals( "GlobalScript", dataFactory.getGlobalScript() );
    assertEquals( "GlobalScriptLanguage", dataFactory.getGlobalScriptLanguage() );
  }
}
