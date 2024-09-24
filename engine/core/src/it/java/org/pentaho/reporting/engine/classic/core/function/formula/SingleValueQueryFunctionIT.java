/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

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
