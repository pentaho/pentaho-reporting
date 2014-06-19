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
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package org.pentaho.reporting.libraries.formula.function.userdefined;

import org.pentaho.reporting.libraries.formula.FormulaTestBase;

public class ArrayContainsFunctionTest extends FormulaTestBase
{
  public void testDefault() throws Exception
  {
    runDefaultTest();
  }

  public Object[][] createDataTest()
  {
    return new Object[][]
        {
            {"ARRAYCONTAINS([.B3:.B8]; \"7\")", Boolean.TRUE, },
            {"ARRAYCONTAINS([.B3:.B8]; \"A\")", Boolean.FALSE, },
            {"ARRAYCONTAINS([.B3:.B8]; \"7\"; 2; 3)", Boolean.TRUE},
            {"ARRAYCONTAINS([.B3:.B8]; \"7\"; 2; 4)", Boolean.FALSE},
            {"ARRAYCONTAINS([.B3:.B8]; \"A\"; 2; 3)", Boolean.FALSE},
            {"ARRAYCONTAINS([.B3:.B8]; \"7\"; 4; 3)", Boolean.FALSE},
        };
  }

}
