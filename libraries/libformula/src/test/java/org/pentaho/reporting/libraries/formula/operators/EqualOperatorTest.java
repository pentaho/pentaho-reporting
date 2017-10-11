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

package org.pentaho.reporting.libraries.formula.operators;

import org.pentaho.reporting.libraries.formula.FormulaTestBase;

/**
 * Creation-Date: 10.04.2007, 15:31:58
 *
 * @author Thomas Morgner
 */
public class EqualOperatorTest extends FormulaTestBase {

  public Object[][] createDataTest() {
    return new Object[][]
      {
        { "1=\"1\"", Boolean.TRUE },
        { "2=2.0", Boolean.TRUE },
        { "\"2004-01-01\"=DATE(2004; 1; 1)", Boolean.TRUE }, // comparing values of different types should yield 'false'
      };
  }

  public EqualOperatorTest() {
  }

  public EqualOperatorTest( final String s ) {
    super( s );
  }

  public void testDefault() throws Exception {
    runDefaultTest();
  }

}
