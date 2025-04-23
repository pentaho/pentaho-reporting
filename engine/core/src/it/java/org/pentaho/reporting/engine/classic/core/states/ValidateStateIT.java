/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.states;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.function.FormulaExpression;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.PrintReportProcessor;

import javax.swing.table.DefaultTableModel;

import static org.junit.Assert.assertFalse;

public class ValidateStateIT {

  @Before
  public void setUp() {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testExportParameter() throws ReportProcessingException {
    final FormulaExpression function = new FormulaExpression();
    function.setName( "out" );
    function.setFormula( "=output // this formula does not even parse!" );

    SubReport subReport = new SubReport();
    subReport.addExportParameter( "out", "out" );
    subReport.addExpression( function );

    MasterReport report = new MasterReport();
    report.setDataFactory( new TableDataFactory( report.getQuery(), new DefaultTableModel( 2, 2 ) ) );
    final RelationalGroup rootGroup = (RelationalGroup) report.getRootGroup();
    rootGroup.getHeader().addSubReport( (SubReport) subReport.derive() );
    report.getItemBand().addSubReport( (SubReport) subReport.derive() );

    final PrintReportProcessor proc = new PrintReportProcessor( report );
    assertFalse( proc.isError() );
  }
}
