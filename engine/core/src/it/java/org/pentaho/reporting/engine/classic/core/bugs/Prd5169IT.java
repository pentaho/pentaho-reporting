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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.function.AbstractExpression;
import org.pentaho.reporting.engine.classic.core.parameters.DefaultParameterDefinition;
import org.pentaho.reporting.engine.classic.core.parameters.PlainParameter;
import org.pentaho.reporting.engine.classic.core.states.LayoutProcess;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;

public class Prd5169IT {

  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testSameParametersName() throws Exception {
    final TypedTableModel model = createTestData();

    final MasterReport report = new MasterReport();
    report.setQuery( "default" );
    report.setDataFactory( new TableDataFactory( "default", model ) );

    DefaultParameterDefinition mdef = new DefaultParameterDefinition();
    mdef.addParameterDefinition( new PlainParameter( "Rows", String.class ) );
    report.setParameterDefinition( mdef );
    report.getParameterValues().put( "Rows", "ALL" );

    final RelationalGroup group = new RelationalGroup();
    group.addField( "Rows" );
    report.setRootGroup( group );
    report.addExpression( new ValidateExpression() );

    DebugReportRunner.execGraphics2D( report );
  }

  private TypedTableModel createTestData() {
    final TypedTableModel model = new TypedTableModel( new String[] { "Rows", "Data" } );
    model.addRow( "A1", 100 );
    model.addRow( "A2", 2 );
    model.addRow( "A3", 20 );
    model.addRow( "A2", 8 );
    return model;
  }

  private static class ValidateExpression extends AbstractExpression {
    private String[] validateData;

    private ValidateExpression() {
      setName( "Validate" );
      validateData = new String[] { "A1", "A2", "A3", "A2" };
    }

    public Object getValue() {
      if ( getRuntime().getProcessingContext().getProcessingLevel() == LayoutProcess.LEVEL_STRUCTURAL_PREPROCESSING ) {
        return false;
      }

      final int currentRow = getRuntime().getCurrentRow();

      final Object row = getDataRow().get( "Rows" );
      Assert.assertEquals( validateData[currentRow], row );
      return currentRow;
    }
  }
}
