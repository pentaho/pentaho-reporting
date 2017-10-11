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

public class EngineeringNotationFunctionIT extends FormulaTestBase {
  public Object[][] createDataTest() {
    return new Object[][] { { "ENGINEERINGNOTATION(0)", "0 " }, { "ENGINEERINGNOTATION(1)", "1 " },
      { "ENGINEERINGNOTATION(100)", "100 " }, { "ENGINEERINGNOTATION(1000)", "1k" },
      { "ENGINEERINGNOTATION(10000)", "10k" }, { "ENGINEERINGNOTATION(-11)", "-11 " },
      { "ENGINEERINGNOTATION(-1000)", "-1k" }, { "ENGINEERINGNOTATION(-100000000)", "-100M" },
      { "ENGINEERINGNOTATION(0.0000101000)", "10\u00b5" },

      { "ENGINEERINGNOTATION(1; 5)", "1.000 " }, { "ENGINEERINGNOTATION(100; 5)", "100.00 " },
      { "ENGINEERINGNOTATION(1000; 6)", "1.0000k" }, { "ENGINEERINGNOTATION(10000; 0)", "10k" },
      { "ENGINEERINGNOTATION(-11; 1)", "-11 " }, { "ENGINEERINGNOTATION(-1000; 5)", "-1.000k" },
      { "ENGINEERINGNOTATION(-100000000; 0)", "-100M" }, { "ENGINEERINGNOTATION(0.0000101000; 5)", "10.100\u00b5" },

      { "ENGINEERINGNOTATION(1; 5; FALSE())", "1.00000 " }, { "ENGINEERINGNOTATION(100; 5; FALSE())", "100.00000 " },
      { "ENGINEERINGNOTATION(1000; 6; FALSE())", "1.000000k" }, { "ENGINEERINGNOTATION(10000; 0; FALSE())", "10k" },
      { "ENGINEERINGNOTATION(-11; 1; FALSE())", "-11.0 " }, { "ENGINEERINGNOTATION(-1000; 5; FALSE())", "-1.00000k" },
      { "ENGINEERINGNOTATION(-100000000; 0; FALSE())", "-100M" },
      { "ENGINEERINGNOTATION(0.0000101000; 5; FALSE())", "10.10000\u00b5" }, };
  }

  public void testDefault() throws Exception {
    runDefaultTest();
  }

}
