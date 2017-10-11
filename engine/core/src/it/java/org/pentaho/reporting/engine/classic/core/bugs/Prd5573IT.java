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
