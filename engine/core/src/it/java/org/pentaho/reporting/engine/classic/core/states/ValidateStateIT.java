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

package org.pentaho.reporting.engine.classic.core.states;

import junit.framework.TestCase;
import org.junit.Ignore;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.function.FormulaExpression;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;

import javax.swing.table.DefaultTableModel;

public class ValidateStateIT extends TestCase {
  public ValidateStateIT() {
  }

  public ValidateStateIT( final String name ) {
    super( name );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

//  @Ignore
//  public void testExportParameter() {
//    final FormulaExpression function = new FormulaExpression();
//    function.setName( "out" );
//    function.setFormula( "=output // this formula does not even parse!" );
//
//    SubReport subReport = new SubReport();
//    subReport.addExportParameter( "out", "out" );
//    subReport.addExpression( function );
//
//    MasterReport report = new MasterReport();
//    report.setDataFactory( new TableDataFactory( report.getQuery(), new DefaultTableModel( 2, 2 ) ) );
//    final RelationalGroup rootGroup = (RelationalGroup) report.getRootGroup();
//    rootGroup.getHeader().addSubReport( (SubReport) subReport.derive() );
//    report.getItemBand().addSubReport( (SubReport) subReport.derive() );
//
//    DebugReportRunner.execGraphics2D( report );
//
//  }
}
