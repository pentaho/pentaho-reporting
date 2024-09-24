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

package org.pentaho.reporting.engine.classic.core.function;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;

@SuppressWarnings( "HardCodedStringLiteral" )
public class Prd3985IT extends TestCase {
  public Prd3985IT() {
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testSyntaxErrorFormula() {
    final FormulaExpression function = new FormulaExpression();
    function.setName( "Test" );
    function.setFormula( "=IF(" ); // clearly an bogus formula
    function.setFailOnError( true );
    final MasterReport report = new MasterReport();
    report.addExpression( function );

    try {
      DebugReportRunner.createPDF( report );
      Assert.fail();
    } catch ( Exception e ) {
      // empty is ok here.
    }
  }

  public void testSyntaxErrorFormulaFunction() {
    final FormulaFunction function = new FormulaFunction();
    function.setName( "Test" );
    function.setFormula( "=IF(" ); // clearly an bogus formula
    function.setFailOnError( true );
    final MasterReport report = new MasterReport();
    report.addExpression( function );

    try {
      DebugReportRunner.createPDF( report );
      Assert.fail();
    } catch ( Exception e ) {
      // empty is ok here.
    }
  }

  public void testSyntaxErrorFormulaFunctionInit() {
    final FormulaFunction function = new FormulaFunction();
    function.setName( "Test" );
    function.setInitial( "=IF(" ); // clearly an bogus formula
    function.setFormula( "=TRUE()" );
    function.setFailOnError( true );
    final MasterReport report = new MasterReport();
    report.addExpression( function );

    try {
      DebugReportRunner.createPDF( report );
      Assert.fail();
    } catch ( Exception e ) {
      // empty is ok here.
    }
  }

  public void testSyntaxErrorAttributeExpression() {
    final FormulaExpression function = new FormulaExpression();
    function.setName( "Test" );
    function.setFormula( "=IF(" ); // clearly an bogus formula

    final MasterReport report = new MasterReport();
    report.getReportConfiguration().setConfigProperty(
        "org.pentaho.reporting.engine.classic.core.FailOnAttributeExpressionErrors", "true" );
    report.getReportHeader().setAttributeExpression( AttributeNames.Core.NAMESPACE, AttributeNames.Core.NAME, function );

    try {
      DebugReportRunner.createPDF( report );
      Assert.fail();
    } catch ( Exception e ) {
      // empty is ok here.
    }
  }

  public void testSyntaxErrorStyleExpression() {
    final FormulaExpression function = new FormulaExpression();
    function.setName( "Test" );
    function.setFormula( "=IF(" ); // clearly an bogus formula

    final MasterReport report = new MasterReport();
    report.getReportConfiguration().setConfigProperty(
        "org.pentaho.reporting.engine.classic.core.FailOnStyleExpressionErrors", "true" );
    report.getReportHeader().setStyleExpression( ElementStyleKeys.VISIBLE, function );

    try {
      DebugReportRunner.createPDF( report );
      Assert.fail();
    } catch ( Exception e ) {
      // empty is ok here.
    }
  }

  public void testEvalErrorFormula() {
    final FormulaExpression function = new FormulaExpression();
    function.setName( "Test" );
    function.setFormula( "=ERROR(\"test\")" ); // clearly an bogus formula
    function.setFailOnError( true );
    final MasterReport report = new MasterReport();
    report.addExpression( function );

    try {
      DebugReportRunner.createPDF( report );
      Assert.fail();
    } catch ( Exception e ) {
      // empty is ok here.
    }
  }

  public void testEvalErrorFormulaFunction() {
    final FormulaFunction function = new FormulaFunction();
    function.setName( "Test" );
    function.setFormula( "=ERROR(\"test\")" ); // clearly an bogus formula
    function.setFailOnError( true );
    final MasterReport report = new MasterReport();
    report.addExpression( function );

    try {
      DebugReportRunner.createPDF( report );
      Assert.fail();
    } catch ( Exception e ) {
      // empty is ok here.
    }
  }

  public void testEvalErrorFormulaFunctionInit() {
    final FormulaFunction function = new FormulaFunction();
    function.setName( "Test" );
    function.setInitial( "=ERROR(\"test\")" ); // clearly an bogus formula
    function.setFormula( "=TRUE()" );
    function.setFailOnError( true );
    final MasterReport report = new MasterReport();
    report.addExpression( function );

    try {
      DebugReportRunner.createPDF( report );
      Assert.fail();
    } catch ( Exception e ) {
      // empty is ok here.
    }
  }

  public void testEvalErrorAttributeExpression() {
    final FormulaExpression function = new FormulaExpression();
    function.setName( "Test" );
    function.setFormula( "=ERROR(\"test\")" ); // clearly an bogus formula

    final MasterReport report = new MasterReport();
    report.getReportConfiguration().setConfigProperty(
        "org.pentaho.reporting.engine.classic.core.FailOnAttributeExpressionErrors", "true" );
    report.getReportHeader().setAttributeExpression( AttributeNames.Core.NAMESPACE, AttributeNames.Core.NAME, function );

    try {
      DebugReportRunner.createPDF( report );
      Assert.fail();
    } catch ( Exception e ) {
      // empty is ok here.
    }
  }

  public void testEvalErrorStyleExpression() {
    final FormulaExpression function = new FormulaExpression();
    function.setName( "Test" );
    function.setFormula( "=ERROR(\"test\")" ); // clearly an bogus formula

    final MasterReport report = new MasterReport();
    report.getReportConfiguration().setConfigProperty(
        "org.pentaho.reporting.engine.classic.core.FailOnStyleExpressionErrors", "true" );
    report.getReportHeader().setStyleExpression( ElementStyleKeys.VISIBLE, function );

    try {
      DebugReportRunner.createPDF( report );
      Assert.fail();
    } catch ( Exception e ) {
      // empty is ok here.
    }
  }

}
