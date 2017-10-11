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

package org.pentaho.reporting.engine.classic.core.function.formula;

import org.pentaho.reporting.libraries.formula.FormulaTestBase;

public class MParameterTextFunctionIT extends FormulaTestBase {
  public MParameterTextFunctionIT() {
  }

  public MParameterTextFunctionIT( final String s ) {
    super( s );
  }

  protected Object[][] createDataTest() {
    return new Object[][] {
      // plain-value behaviour must be same as PARAMETERTEXT

      { "MPARAMETERTEXT(DATE(2009;10;10); \"test\")", "2009-10-10T00%3A00%3A00.000%2B0000" },
      { "MPARAMETERTEXT(100000; \"test\")", "100000" }, { "MPARAMETERTEXT(1000.001; \"test\")", "1000.001" },
      { "MPARAMETERTEXT(\"AAAA\"; \"test\"; TRUE())", "AAAA" },
      { "MPARAMETERTEXT(\"&:;\"; \"test\"; FALSE())", "&:;" },
      { "MPARAMETERTEXT(\"&:;\"; \"test\"; TRUE())", "%26%3A%3B" },

      { "MPARAMETERTEXT({ DATE(2009;10;10) | \"old\"}; \"test\")", "2009-10-10T00%3A00%3A00.000%2B0000&test=old" },
      { "MPARAMETERTEXT({100000 | \"old\"}; \"test\")", "100000&test=old" },
      { "MPARAMETERTEXT({1000.001 | \"old\"}; \"test\")", "1000.001&test=old" },
      { "MPARAMETERTEXT({\"AAAA\" | \"old\"}; \"test\"; TRUE())", "AAAA&test=old" },
      { "MPARAMETERTEXT({\"&:;\" | \"old\"}; \"test\"; FALSE())", "&:;&test=old" },
      { "MPARAMETERTEXT({\"&:;\" | \"old\"}; \"test\"; TRUE())", "%26%3A%3B&test=old" },

      { "MPARAMETERTEXT({\"AAAA\" | \"o:l:d\"}; \"t:e:st\"; TRUE())", "AAAA&t%3Ae%3Ast=o%3Al%3Ad" }, };
  }

  public void testDefault() throws Exception {
    runDefaultTest();
  }
}
