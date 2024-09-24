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
