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
 *  Copyright (c) 2006 - 2015 Pentaho Corporation..  All rights reserved.
 */
package org.pentaho.reporting.engine.classic.extensions.datasources.kettle;

import static org.junit.Assert.*;

import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ParameterMapping;

public class FormulaParameterTest {

  @Test
  public void create() {
    FormulaParameter formulaParameter = FormulaParameter.create( "TEST_REPORT_FIELD_NAME", "TEST_TRANS_PARAM_NAME" );
    assertEquals( "=[TEST_REPORT_FIELD_NAME]", formulaParameter.getFormula() );
  }

  @Test
  public void convert_formula_parameter_to_parameter_mapping() {
    FormulaParameter formulaParameter = new FormulaParameter( "TEST_TRANS_PARAM_NAME", "=[TEST_REPORT_FIELD_NAME]" );
    ParameterMapping[] actualResult = FormulaParameter.convert( new FormulaParameter[] { formulaParameter } );
    assertEquals( 1, actualResult.length );
    assertEquals( "TEST_TRANS_PARAM_NAME", actualResult[0].getAlias() );
    assertEquals( "TEST_REPORT_FIELD_NAME", actualResult[0].getName() );
  }

  @Test
  public void convert_parameter_mapping_to_formula_parameter() {
    ParameterMapping parameterMapping = new ParameterMapping( "TEST_REPORT_FIELD_NAME", "TEST_TRANS_PARAM_NAME" );
    FormulaParameter[] actualResult = FormulaParameter.convert( new ParameterMapping[] { parameterMapping } );
    FormulaParameter expectedResult = new FormulaParameter( "TEST_TRANS_PARAM_NAME", "=[TEST_REPORT_FIELD_NAME]" );
    assertArrayEquals( new FormulaParameter[] { expectedResult }, actualResult );
  }

}
