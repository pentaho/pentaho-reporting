/*
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
 * Copyright (c) 2001 - 2021 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.misc.bsf;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.function.AbstractExpression;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.function.WrapperExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.states.LegacyDataRowWrapper;

import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * An expression that uses the Bean scripting framework to perform a scripted calculation.
 *
 * @author Thomas Morgner
 */
public class BSFExpression extends AbstractExpression {
  private static final Log logger = LogFactory.getLog( BSFExpression.class );
  /**
   * The interpreter used to evaluate the expression.
   */
  private transient BSFManager interpreter;
  private transient boolean invalid;
  private transient LegacyDataRowWrapper dataRowWrapper;
  private transient WrapperExpressionRuntime runtimeWrapper;

  private String language;
  private String script;
  private String expression;

  /**
   * Default constructor, create a new BeanShellExpression.
   */
  public BSFExpression() {
  }

  /**
   * Creates a new interpreter instance.
   *
   * @return the interpreter or null, if there was an error.
   */
  protected BSFManager createInterpreter() {
    try {
      final BSFManager interpreter = new BSFManager();
      initializeInterpreter( interpreter );
      return interpreter;
    } catch ( Exception e ) {
      BSFExpression.logger.error( "Unable to initialize the expression", e ); //$NON-NLS-1$
      return null;
    }
  }

  /**
   * Initializes the Bean-Scripting Framework manager.
   *
   * @param interpreter
   *          the BSF-Manager that should be initialized.
   * @throws BSFException
   *           if an error occured.
   */
  protected void initializeInterpreter( final BSFManager interpreter ) throws BSFException {
    dataRowWrapper = new LegacyDataRowWrapper();
    runtimeWrapper = new WrapperExpressionRuntime();
    runtimeWrapper.update( getDataRow(), getRuntime() );
    interpreter.declareBean( "runtime", runtimeWrapper, ExpressionRuntime.class ); //$NON-NLS-1$
    interpreter.declareBean( "dataRow", dataRowWrapper, DataRow.class ); //$NON-NLS-1$
    if ( script != null ) {
      interpreter.exec( getLanguage(), "script", 1, 1, getScript() ); //$NON-NLS-1$
    }
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
    boolean allowBeanShellEval = ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty(
      "org.pentaho.reporting.engine.classic.core.allowScriptEvaluation", "false" )
      .equalsIgnoreCase( "true" );

    if ( !allowBeanShellEval ) {
      BSFExpression.logger.error( "Scripts are prevented from running by default in order to avoid"
        + " potential remote code execution.  The system administrator must enable this capability." );
      return null;
    }

    if ( invalid || expression == null ) {
      return null;
    }
    if ( interpreter == null ) {
      interpreter = createInterpreter();
      if ( interpreter == null ) {
        invalid = true;
        return null;
      }
    }
    try {
      runtimeWrapper.update( null, getRuntime() );
      dataRowWrapper.setParent( getDataRow() );
      return interpreter.eval( getLanguage(), "expression", 1, 1, getExpression() ); //$NON-NLS-1$
    } catch ( Exception e ) {
      BSFExpression.logger.warn( "Evaluation error: " + //$NON-NLS-1$
          e.getClass() + " - " + e.getMessage(), e ); //$NON-NLS-1$
      return null;
    } finally {
      runtimeWrapper.update( null, null );
      dataRowWrapper.setParent( null );
    }
  }

  /**
   * Clones the expression and reinitializes the script.
   *
   * @return a clone of the expression.
   * @throws CloneNotSupportedException
   *           this should never happen.
   */
  public Object clone() throws CloneNotSupportedException {
    final BSFExpression expression = (BSFExpression) super.clone();
    expression.interpreter = null;
    return expression;
  }

  /**
   * Serialisation support. The transient child elements were not saved.
   *
   * @param in
   *          the input stream.
   * @throws IOException
   *           if there is an I/O error.
   * @throws ClassNotFoundException
   *           if a serialized class is not defined on this system.
   */
  private void readObject( final ObjectInputStream in ) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
  }

  /**
   * Returns the script that gets evaluated every time the getValue() method is called.
   *
   * @return the script.
   */
  public String getExpression() {
    return expression;
  }

  /**
   * Invalidates the interpreter-cache and forces a reinterpretation of the script.
   */
  protected void invalidate() {
    this.interpreter = null;
  }

  /**
   * Sets the script that should be executed. Whats in the script depends on what langage is selected.
   *
   * @param expression
   *          the script.
   */
  public void setExpression( final String expression ) {
    this.expression = expression;
    this.interpreter = null;
  }

  /**
   * Returns the programming language, in which the interpreter work.
   *
   * @return the programming language, which must be one of the supported BSF-Languages.
   */
  public String getLanguage() {
    return language;
  }

  /**
   * Defines the programming language of the script and expression.
   *
   * @param language
   *          the programming language of the script.
   */
  public void setLanguage( final String language ) {
    this.language = language;
    this.interpreter = null;
  }

  /**
   * Returns the script. The script is a predefined piece of code that gets executed once. It can (and should) be used
   * to perform global initializations and to define functions.
   *
   * @return the script (can be null).
   */
  public String getScript() {
    return script;
  }

  /**
   * Defines the script. The script is a predefined piece of code that gets executed once. It can (and should) be used
   * to perform global initializations and to define functions.
   *
   * @param script
   *          an initialization script.
   */
  public void setScript( final String script ) {
    this.script = script;
    this.interpreter = null;
  }
}
