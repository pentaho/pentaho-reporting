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

package org.pentaho.reporting.engine.classic.core.function.formula;

import junit.framework.TestCase;
import org.junit.Assert;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.function.FormulaExpression;
import org.pentaho.reporting.engine.classic.core.function.ReportFormulaContext;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.libraries.formula.DefaultFormulaContext;
import org.pentaho.reporting.libraries.formula.Formula;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.lang.reflect.Array;
import java.net.URL;

public class MultiValueQueryFunctionIT extends TestCase {
  public MultiValueQueryFunctionIT() {
  }

  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testErrorHandlingGood() throws Exception {
    final URL url = getClass().getResource( "Prd-3985.prpt" );
    final ResourceManager mgr = new ResourceManager();
    final MasterReport report = (MasterReport) mgr.createDirectly( url, MasterReport.class ).getResource();
    report.getReportConfiguration().setConfigProperty(
        "org.pentaho.reporting.engine.classic.core.FailOnAttributeExpressionErrors", "true" );

    final FormulaExpression function = new FormulaExpression();
    function.setName( "Test" );
    function.setFormula( "=MULTIVALUEQUERY(\"Good\")" );

    report.getReportHeader().setAttributeExpression( AttributeNames.Core.NAMESPACE, AttributeNames.Core.NAME, function );

    try {
      DebugReportRunner.createPDF( report );
    } catch ( Exception e ) {
      Assert.fail();
    }

  }

  public void testErrorHandlingBad() throws Exception {
    final URL url = getClass().getResource( "Prd-3985.prpt" );
    final ResourceManager mgr = new ResourceManager();
    final MasterReport report = (MasterReport) mgr.createDirectly( url, MasterReport.class ).getResource();
    report.getReportConfiguration().setConfigProperty(
        "org.pentaho.reporting.engine.classic.core.FailOnAttributeExpressionErrors", "true" );

    final FormulaExpression function = new FormulaExpression();
    function.setName( "Test" );
    function.setFormula( "=MULTIVALUEQUERY(\"Bad\")" );

    report.getReportHeader().setAttributeExpression( AttributeNames.Core.NAMESPACE, AttributeNames.Core.NAME, function );

    try {
      DebugReportRunner.createPDF( report );
      Assert.fail();
    } catch ( Exception e ) {
      // ignored
    }
  }

  public void testLimit() throws Exception {
    final TableModel table = new DefaultTableModel( new Object[] { "Column" }, 100 );

    final TableDataFactory tdf = new TableDataFactory();
    tdf.addTable( "query", table );

    DebugExpressionRuntime rt = new DebugExpressionRuntime() {
      public DataFactory getDataFactory() {
        return tdf;
      }
    };
    ReportFormulaContext fc = new ReportFormulaContext( new DefaultFormulaContext(), rt );
    final Formula f = new Formula( "MULTIVALUEQUERY(\"query\"; \"Column\"; 0; 5)" );
    f.initialize( fc );
    final Object v = f.evaluate();
    Assert.assertNotNull( v );
    Assert.assertTrue( v.getClass().isArray() );
    Assert.assertEquals( 5, Array.getLength( v ) );
  }

  public void testLargerLimit() throws Exception {
    final TableModel table = new DefaultTableModel( new Object[] { "Column" }, 100 );

    final TableDataFactory tdf = new TableDataFactory();
    tdf.addTable( "query", table );

    DebugExpressionRuntime rt = new DebugExpressionRuntime() {
      public DataFactory getDataFactory() {
        return tdf;
      }
    };
    ReportFormulaContext fc = new ReportFormulaContext( new DefaultFormulaContext(), rt );
    final Formula f = new Formula( "MULTIVALUEQUERY(\"query\"; \"Column\"; 0; 500)" );
    f.initialize( fc );
    final Object v = f.evaluate();
    Assert.assertNotNull( v );
    Assert.assertTrue( v.getClass().isArray() );
    Assert.assertEquals( 100, Array.getLength( v ) );
  }

  public void testUnlimitedQuery() throws Exception {
    final TableModel table = new DefaultTableModel( new Object[] { "Column" }, 100 );

    final TableDataFactory tdf = new TableDataFactory();
    tdf.addTable( "query", table );

    DebugExpressionRuntime rt = new DebugExpressionRuntime() {
      public DataFactory getDataFactory() {
        return tdf;
      }
    };
    ReportFormulaContext fc = new ReportFormulaContext( new DefaultFormulaContext(), rt );
    final Formula f = new Formula( "MULTIVALUEQUERY(\"query\"; \"Column\")" );
    f.initialize( fc );
    final Object v = f.evaluate();
    Assert.assertNotNull( v );
    Assert.assertTrue( v.getClass().isArray() );
    Assert.assertEquals( 100, Array.getLength( v ) );
  }
}
