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

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.libraries.base.util.ClassQueryTool;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

public class ExpressionQueryTool extends ClassQueryTool {
  private ArrayList expressions;

  public ExpressionQueryTool() {
    expressions = new ArrayList();
  }

  protected boolean isValidClass( final String className ) {
    if ( className.startsWith( "java." ) || className.startsWith( "javax." ) ) {
      return false;
    }
    return true;
  }

  protected void processClass( final ClassLoader classLoader, final Class c ) {
    if ( Expression.class.isAssignableFrom( c ) == false ) {
      return;
    }
    if ( c.isInterface() ) {
      return;
    }
    final int modifiers = c.getModifiers();
    if ( Modifier.isAbstract( modifiers ) ) {
      return;
    }
    expressions.add( c );
  }

  public Class[] getExpressions() {
    return (Class[]) expressions.toArray( new Class[ expressions.size() ] );
  }

  public static void main( String[] args ) throws IOException {
    ClassicEngineBoot.getInstance().start();
    ExpressionQueryTool eqt = new ExpressionQueryTool();
    eqt.processDirectory( null );
    for ( int i = 0; i < eqt.expressions.size(); i++ ) {
      System.out.println( eqt.expressions.get( i ) );

    }
  }
}
