/*
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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.formula.common;

import junit.framework.Assert;
import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaTestBase;

import java.math.BigDecimal;

public class OperatorTest extends FormulaTestBase {
  protected Object[][] createDataTest() {
    return new Object[][]
      {
        { "1 + 1", new BigDecimal( 2 ) },
        { "1 - 1", new BigDecimal( 0 ) },
        { "1 = 1", Boolean.TRUE },
        { "1 <> 1", Boolean.FALSE },
        { "1 < 1", Boolean.FALSE },
        { "1 > 1", Boolean.FALSE },
        { "1 >= 1", Boolean.TRUE },
        { "1 <= 1", Boolean.TRUE },
        { "1 * 1", new BigDecimal( 1 ) },
        { "1 ^ 1", new BigDecimal( 1.0 ) },
        { "1 / 1", new BigDecimal( 1 ) },
        { "1%", new BigDecimal( "0.01" ) },
        { "0.1%", new BigDecimal( "0.001" ) } };
  }

  public void testDefault() throws Exception {
    runDefaultTest();
  }

  public void testRangeOperator() throws EvaluationException {
    final Object resolveReference = getContext().resolveReference( ".B4:.B5" );
    Assert.assertNotNull( "Reference should not be null", resolveReference );
  }
}
