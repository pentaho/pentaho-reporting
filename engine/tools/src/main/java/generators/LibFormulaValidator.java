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


package generators;

import junit.framework.TestCase;
import org.pentaho.reporting.libraries.formula.DefaultFormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaBoot;
import org.pentaho.reporting.libraries.formula.function.FunctionDescription;
import org.pentaho.reporting.libraries.formula.function.FunctionRegistry;

import java.util.Locale;

public class LibFormulaValidator extends TestCase {
  public static void main( String[] args ) {
    LibFormulaBoot.getInstance().start();

    final DefaultFormulaContext context = new DefaultFormulaContext();
    final FunctionRegistry functionRegistry = context.getFunctionRegistry();
    final String[] names = functionRegistry.getFunctionNames();
    for ( int i = 0; i < names.length; i++ ) {
      final String name = names[ i ];
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
        System.out.println( "Failed at " + name );
      }
    }
  }
}
