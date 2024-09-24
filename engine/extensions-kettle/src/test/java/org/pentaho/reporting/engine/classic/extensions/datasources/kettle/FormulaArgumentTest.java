/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */
package org.pentaho.reporting.engine.classic.extensions.datasources.kettle;

import static org.junit.Assert.*;

import org.junit.Test;
import org.pentaho.reporting.libraries.formula.parser.ParseException;

public class FormulaArgumentTest {

  @Test
  public void create() {
    FormulaArgument argument = FormulaArgument.create( "TEST_FIELD" );
    String actualFormulaString = argument.getFormula();
    assertEquals( "=[TEST_FIELD]", actualFormulaString );
  }

  @Test
  public void getFormula() {
    FormulaArgument argument = new FormulaArgument( "=[TEST_FIELD]" );
    assertEquals( "=[TEST_FIELD]", argument.getFormula() );
  }

  @Test
  public void getReferencedFields() throws ParseException {
    FormulaArgument argument = new FormulaArgument( "=[TEST_FIELD]" );
    assertArrayEquals( new String[] { "TEST_FIELD" }, argument.getReferencedFields() );
  }

  @Test
  public void convert_formula_argument_to_strings() {
    FormulaArgument argument = new FormulaArgument( "=[TEST_FIELD]" );
    FormulaArgument argument1 = new FormulaArgument( "=[TEST_FIELD1]" );
    String[] actualResult = FormulaArgument.convert( new FormulaArgument[] { argument, argument1 } );
    assertArrayEquals( new String[] { "TEST_FIELD", "TEST_FIELD1" }, actualResult );
  }

  @Test
  public void convert_strings_to_formula_argument() {
    FormulaArgument[] actualResult = FormulaArgument.convert( new String[] { "TEST_FIELD", "TEST_FIELD1" } );
    FormulaArgument expectedArgument = new FormulaArgument( "=[TEST_FIELD]" );
    FormulaArgument expectedArgument1 = new FormulaArgument( "=[TEST_FIELD1]" );
    assertArrayEquals( new FormulaArgument[] { expectedArgument, expectedArgument1 }, actualResult );
  }

}
