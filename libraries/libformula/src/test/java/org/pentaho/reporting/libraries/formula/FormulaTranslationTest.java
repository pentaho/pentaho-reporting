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
