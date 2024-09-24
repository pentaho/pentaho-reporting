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
