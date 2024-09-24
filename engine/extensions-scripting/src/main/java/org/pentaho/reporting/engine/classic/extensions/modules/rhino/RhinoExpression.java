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

package org.pentaho.reporting.engine.classic.extensions.modules.rhino;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.pentaho.reporting.engine.classic.core.function.AbstractExpression;
import org.pentaho.reporting.engine.classic.core.states.LegacyDataRowWrapper;

import java.io.Serializable;

/**
 * An expression that uses the Rhino scripting framework to perform a scripted calculation. The expression itself is
 * contained in a function called
 * <p/>
 * <code>Object getValue()</code>
 * <p/>
 * and this function is defined in the <code>expression</code> property. You have to overwrite the function
 * <code>getValue()</code> to begin and to end your expression, but you are free to add your own functions to the
 * script.
 * <p/>
 *
 * @author Thomas Morgner
 * @deprecated Use BSHExpression instead.
 */
public class RhinoExpression extends AbstractExpression implements Serializable {
  private String expression;

  /**
   * default constructor, create a new BeanShellExpression.
   */
  public RhinoExpression() {
  }


  protected LegacyDataRowWrapper initializeScope( final Scriptable scope ) {
    final LegacyDataRowWrapper dataRowWrapper = new LegacyDataRowWrapper();
    dataRowWrapper.setParent( getRuntime().getDataRow() );
    final Object wrappedDataRow = Context.javaToJS( dataRowWrapper, scope );
    ScriptableObject.putProperty( scope, "dataRow", wrappedDataRow ); // NON-NLS
    return dataRowWrapper;
  }

  /**
   * Evaluates the defined expression. If an exception or an evaluation error occures, the evaluation returns null and
   * the error is logged. The current datarow and a copy of the expressions properties are set to script-internal
   * variables. Changes to the properties will not alter the expressions original properties and will be lost when the
   * evaluation is finished.
   * <p/>
   * Expressions do not maintain a state and no assumptions about the order of evaluation can be made.
   *
   * @return the evaluated value or null.
   */
  public Object getValue() {
    if ( expression == null ) {
      return null;
    }

    LegacyDataRowWrapper wrapper = null;
    try {
      final ContextFactory contextFactory = new ContextFactory();
      final Context context = contextFactory.enterContext();
      final Scriptable scope = context.initStandardObjects();
      wrapper = initializeScope( scope );

      final Object o = context.evaluateString( scope, expression, getName(), 1, null );
      if ( o instanceof NativeJavaObject ) {
        final NativeJavaObject object = (NativeJavaObject) o;
        return object.unwrap();
      }
      return o;
    } finally {
      if ( wrapper != null ) {
        wrapper.setParent( null );
      }
      Context.exit();
    }
  }

  @SuppressWarnings( "UnusedDeclaration" )
  public String getExpression() {
    return expression;
  }

  @SuppressWarnings( "UnusedDeclaration" )
  public void setExpression( final String expression ) {
    this.expression = expression;
  }
}
