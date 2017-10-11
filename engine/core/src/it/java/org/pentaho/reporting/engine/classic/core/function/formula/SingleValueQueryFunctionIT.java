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

import junit.framework.Assert;
import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.function.FormulaExpression;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.net.URL;

public class SingleValueQueryFunctionIT extends TestCase {
  public SingleValueQueryFunctionIT() {
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
    function.setFormula( "=SINGLEVALUEQUERY(\"Good\")" );

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
    function.setFormula( "=SINGLEVALUEQUERY(\"Bad\")" );

    report.getReportHeader().setAttributeExpression( AttributeNames.Core.NAMESPACE, AttributeNames.Core.NAME, function );

    try {
      DebugReportRunner.createPDF( report );
      Assert.fail();
    } catch ( Exception e ) {
    }

  }

}
