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

package org.pentaho.reporting.libraries.formula;

import junit.framework.TestCase;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.formula.common.TestFormulaContext;
import org.pentaho.reporting.libraries.formula.function.FunctionDescription;
import org.pentaho.reporting.libraries.formula.function.FunctionRegistry;

import java.util.Locale;

public class FormulaTranslationTest extends TestCase {
  private FormulaContext context;

  public FormulaTranslationTest() {
  }

  public FormulaTranslationTest( final String name ) {
    super( name );
  }

  protected void setUp() throws Exception {
    context = new TestFormulaContext( TestFormulaContext.testCaseDataset );
    LibFormulaBoot.getInstance().start();
  }

  public void testTranslationsAvailable() {
    FunctionRegistry registry = context.getFunctionRegistry();
    String[] functions = registry.getFunctionNames();
    for ( int i = 0; i < functions.length; i++ ) {
      String function = functions[ i ];
      FunctionDescription functionDesc = registry.getMetaData( function );
      assertFalse( StringUtils.isEmpty( functionDesc.getDisplayName( Locale.ENGLISH ) ) );
      assertFalse( StringUtils.isEmpty( functionDesc.getDescription( Locale.ENGLISH ) ) );
      int count = functionDesc.getParameterCount();
      for ( int x = 0; x < count; x++ ) {
        assertFalse( StringUtils.isEmpty( functionDesc.getParameterDescription( x, Locale.ENGLISH ) ) );
        assertFalse( StringUtils.isEmpty( functionDesc.getParameterDisplayName( x, Locale.ENGLISH ) ) );
      }
    }
  }
}
