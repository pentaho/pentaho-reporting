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

package org.pentaho.reporting.engine.classic.core.backlog6746;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.FormulaExpression;
import org.pentaho.reporting.engine.classic.core.function.date.DateExpression;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionRegistry;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriter;
import org.pentaho.reporting.libraries.base.util.MemoryByteArrayOutputStream;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import static junit.framework.Assert.assertTrue;

@Ignore
public class Backlog6746Test {
  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testModuleLoaded() {
    Assert.assertTrue( ClassicEngineBoot.getInstance().getPackageManager().isModuleAvailable( Backlog6746Module.class.getName() ));
    Assert.assertTrue( ExpressionRegistry.getInstance().getExpressionMetaData( Backlog6746Expression.class.getName() ) != null);
  }

  @Test
  public void testLoadSaveAsExpression() throws Exception {
    FormulaExpression fe = new FormulaExpression();
    fe.setFormula( "TRUE()" );

    DateExpression de = new DateExpression();
    de.setDay( 10 );
    de.setMonth( 5 );
    de.setYear( 2005 );

    Backlog6746Expression e = new Backlog6746Expression();
    e.setName( "test" );
    e.addExpression( "my-property" , fe);
    e.addExpression( "my-property-2" , de);

    MasterReport r = new MasterReport();
    r.addExpression( e );

    MasterReport r2 = saveAndLoad( r );
    final Expression test = r2.getExpressions().get( "test" );
    Backlog6746Expression testCast = (Backlog6746Expression) test;
    FormulaExpression fe1 = (FormulaExpression) testCast.getExpressionMap().get("my-property");
    FormulaExpression fe2 = (FormulaExpression) e.getExpressionMap().get("my-property");
    Assert.assertEquals( fe1.getFormulaExpression(), fe2.getFormulaExpression() );
  }

  private MasterReport saveAndLoad( final MasterReport originalReport ) throws Exception {
    final MemoryByteArrayOutputStream bout = new MemoryByteArrayOutputStream();
    BundleWriter.writeReportToZipStream( originalReport, bout );
    assertTrue( bout.getLength() > 0 );
    /*
     * final File f = File.createTempFile("test-output-", ".prpt", new File ("test-output")); final FileOutputStream
     * outputStream = new FileOutputStream(f); outputStream.write(bout.toByteArray()); outputStream.close();
     */
    final ResourceManager mgr = new ResourceManager();
    final Resource reportRes = mgr.createDirectly( bout.toByteArray(), MasterReport.class );
    return (MasterReport) reportRes.getResource();
  }

}
