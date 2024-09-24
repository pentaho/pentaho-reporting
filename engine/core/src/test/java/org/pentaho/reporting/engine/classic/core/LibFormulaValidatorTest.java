/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.reporting.engine.classic.core;

import junit.framework.TestCase;
import org.pentaho.reporting.libraries.base.util.DebugLog;
import org.pentaho.reporting.libraries.formula.DefaultFormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaBoot;
import org.pentaho.reporting.libraries.formula.function.FunctionDescription;
import org.pentaho.reporting.libraries.formula.function.FunctionRegistry;

import java.util.ArrayList;
import java.util.Locale;

public class LibFormulaValidatorTest extends TestCase {
  public LibFormulaValidatorTest() {
  }

  public LibFormulaValidatorTest( final String name ) {
    super( name );
  }

  protected void setUp() throws Exception {
    LibFormulaBoot.getInstance().start();
  }

  public void testLibFormulaMetadata() {

    final DefaultFormulaContext context = new DefaultFormulaContext();
    final FunctionRegistry functionRegistry = context.getFunctionRegistry();
    final String[] names = functionRegistry.getFunctionNames();
    final ArrayList<String> failedNames = new ArrayList<String>();
    for ( int i = 0; i < names.length; i++ ) {
      final String name = names[i];
      final FunctionDescription data = functionRegistry.getMetaData( name );
      try {
        assertNotNull( data.getCategory() );
        assertNotNull( data.getDescription( Locale.ENGLISH ) );
        assertNotNull( data.getDisplayName( Locale.ENGLISH ) );
        assertNotNull( data.getValueType() );
        final int count = data.getParameterCount();
        for ( int x = 0; x < count; x++ ) {
          assertNotNull( data.getParameterType( x ) );
          assertNotNull( data.getParameterDescription( x, Locale.ENGLISH ) );
          assertNotNull( data.getParameterDisplayName( x, Locale.ENGLISH ) );
        }
      } catch ( Throwable t ) {
        failedNames.add( name );
        t.printStackTrace();
      }
    }

    if ( failedNames.isEmpty() ) {
      return;
    }

    DebugLog.log( "Missing metadata for LibFormula functions:" );
    for ( int i = 0; i < failedNames.size(); i++ ) {
      DebugLog.log( " :" + failedNames.get( i ) );
    }
    fail();
  }
}
