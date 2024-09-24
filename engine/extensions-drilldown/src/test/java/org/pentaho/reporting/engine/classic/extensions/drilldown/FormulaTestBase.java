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

package org.pentaho.reporting.engine.classic.extensions.drilldown;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.StaticDataRow;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.function.GenericExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.function.ReportFormulaContext;
import org.pentaho.reporting.engine.classic.core.layout.output.DefaultProcessingContext;
import org.pentaho.reporting.libraries.formula.Formula;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.common.TestFormulaContext;

import javax.swing.table.DefaultTableModel;

public abstract class FormulaTestBase extends TestCase {
  private FormulaContext context;

  protected FormulaTestBase() {
  }

  protected FormulaTestBase( final String s ) {
    super( s );
  }

  protected void setUp() throws Exception {
    final ExpressionRuntime runtime = new GenericExpressionRuntime
      ( new StaticDataRow(), new DefaultTableModel(), 0, new DefaultProcessingContext() );
    context = new ReportFormulaContext( new TestFormulaContext( TestFormulaContext.testCaseDataset ), runtime );
    ClassicEngineBoot.getInstance().start();
  }

  protected abstract Object[][] createDataTest();

  public FormulaContext getContext() {
    return context;
  }

  protected void runDefaultTest() throws Exception {
    final Object[][] dataTest = createDataTest();
    runTest( dataTest );
  }

  protected void runTest( final Object[][] dataTest ) throws Exception {
    for ( int i = 0; i < dataTest.length; i++ ) {
      final Object[] objects = dataTest[ i ];
      performTest( (String) objects[ 0 ], objects[ 1 ] );
    }
  }

  protected void performTest( final String formul, final Object result ) throws Exception {
    performTest( formul, result, this.context );
  }

  protected void performTest( final String formul, final Object result, final FormulaContext context )
    throws Exception {
    final Formula formula = new Formula( formul );
    formula.initialize( context );
    final Object eval = formula.evaluateTyped().getValue();
    if ( result instanceof Comparable && eval instanceof Comparable ) {
      final Comparable n = (Comparable) result;
      try {
        assertTrue( "Failure typed comparison on " + formul + ": \n  expected \n" + result + "\n but found \n" + eval,
          n.compareTo( eval ) == 0 );
      } catch ( final ClassCastException cce ) {
        cce.printStackTrace();
        fail( "ClassCast: Failure typed comparison on " + formul + ": " + result + " vs. " + eval );
      }
    } else {
      assertEquals( "Failure on " + formul, result, eval );
    }
  }

}
