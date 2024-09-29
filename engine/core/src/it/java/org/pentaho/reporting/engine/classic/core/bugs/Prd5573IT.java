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


package org.pentaho.reporting.engine.classic.core.bugs;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.BandType;
import org.pentaho.reporting.engine.classic.core.layout.table.TableTestUtil;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;

import javax.swing.table.DefaultTableModel;
import java.io.File;


public class Prd5573IT {

  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
    new File( "test-output" ).mkdir();
  }

  @Test( expected = ReportProcessingException.class )
  public void testReport() throws Exception {
    final MasterReport report = new MasterReport();
    report.setDataFactory( new TableDataFactory( "query", new DefaultTableModel( 10, 1 ) ) );
    report.setQuery( "query" );

    final Band table = TableTestUtil.createTable( 1, 1, 6, true );
    final ReportElement cell = table.getElement( 0 )
      .getChildElementByType( new BandType() )
      .getChildElementByType( new BandType() );

    cell.getStyle().setStyleProperty( ElementStyleKeys.HEIGHT, 700f );
    table.setName( "table" );
    report.getReportHeader().addElement( table );
    report.getReportHeader().setLayout( "block" );

    DebugReportRunner.layoutPages( report, 0, 1, 2 );

  }
}
