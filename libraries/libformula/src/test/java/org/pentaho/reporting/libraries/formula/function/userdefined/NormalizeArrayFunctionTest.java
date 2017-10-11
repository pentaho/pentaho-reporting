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

package org.pentaho.reporting.libraries.formula.function.userdefined;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaTestBase;
import org.pentaho.reporting.libraries.formula.common.TestFormulaContext;
import org.pentaho.reporting.libraries.formula.typing.sequence.RecursiveSequence;

import java.math.BigDecimal;

public class NormalizeArrayFunctionTest extends FormulaTestBase {
  public void testDefault() throws Exception {
    runDefaultTest();
  }

  public void testRecursiveSequence() throws EvaluationException {
    final TestFormulaContext context = new TestFormulaContext();
    final RecursiveSequence sequence =
      new RecursiveSequence( new Object[] { new String[ 0 ], new Integer[ 0 ], "A" }, context );
    while ( sequence.hasNext() ) {
      System.out.println( sequence.next() );
    }
  }

  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "NORMALIZEARRAY({11 | 21 | [.B18] | [.C19]})", new Object[]
          { new BigDecimal( 11 ), new BigDecimal( 21 ), new BigDecimal( 1 ),
            new BigDecimal( 2 ), new BigDecimal( 3 ), new BigDecimal( 42 ), new BigDecimal( 43 ) } },
        { "NORMALIZEARRAY({11 | 21 | [.B18] | [.B19]})", new Object[]
          { new BigDecimal( 11 ), new BigDecimal( 21 ), new BigDecimal( 1 ), new BigDecimal( 2 ),
            new BigDecimal( 3 ) } },
        { "NORMALIZEARRAY({11 | 21 | [.B18]})", new Object[]
          { new BigDecimal( 11 ), new BigDecimal( 21 ), new BigDecimal( 1 ), new BigDecimal( 2 ),
            new BigDecimal( 3 ) } },
      };
  }

}
