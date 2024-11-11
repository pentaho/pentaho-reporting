/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/

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
