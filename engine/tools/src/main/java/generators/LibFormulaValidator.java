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
