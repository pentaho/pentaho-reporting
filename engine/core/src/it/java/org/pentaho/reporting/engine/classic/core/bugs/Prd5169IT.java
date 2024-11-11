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
