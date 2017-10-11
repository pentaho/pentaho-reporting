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

public class ParameterTextFunctionIT extends FormulaTestBase {
  public ParameterTextFunctionIT() {
  }

  public ParameterTextFunctionIT( final String s ) {
    super( s );
  }

  protected Object[][] createDataTest() {
    return new Object[][] { { "PARAMETERTEXT(DATE(2009;10;10))", "2009-10-10T00%3A00%3A00.000%2B0000" },
      { "PARAMETERTEXT(100000)", "100000" }, { "PARAMETERTEXT(1000.001)", "1000.001" },
      { "PARAMETERTEXT(\"AAAA\"; TRUE())", "AAAA" }, { "PARAMETERTEXT(\"&:;\"; FALSE())", "&:;" },
      { "PARAMETERTEXT(\"&:;\"; TRUE())", "%26%3A%3B" },

    };
  }

  public void testDefault() throws Exception {
    runDefaultTest();
  }
}
