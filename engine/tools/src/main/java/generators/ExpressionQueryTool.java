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
