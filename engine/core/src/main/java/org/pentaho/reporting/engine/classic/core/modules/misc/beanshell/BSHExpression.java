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

package org.pentaho.reporting.engine.classic.core.modules.misc.beanshell;

import bsh.EvalError;
import bsh.Interpreter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.function.AbstractExpression;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.Reader;

/**
 * An expression that uses the BeanShell scripting framework to perform a scripted calculation. The expression itself is
 * contained in a function called
 * <p/>
 * <code>Object getValue()</code>
 * <p/>
 * and this function is defined in the <code>expression</code> property. You have to overwrite the function
 * <code>getValue()</code> to begin and to end your expression, but you are free to add your own functions to the
 * script.
 * <p/>
 * By default, base Java core and extension packages are imported for you. They are:
 * <ul>
 * <li><code>java.lang<code>
 * <li><code>java.io</code>
 * <li><code>java.util</code>
 * <li><code>java.net</code>
 * <li><code>java.awt</code>
 * <li><code>java.awt.event</code>
 * <li><code>javax.swing</code>
 * <li><code>javax.swing.event</code>
 * </ul>
 * <p/>
 * An example in the XML format: (from report1.xml)
 * <p/>
 * 
 * <pre>
 * <expression name="expression" class="org.pentaho.reporting.engine.classic.core.modules.misc.beanshell
 * .BSHExpression">
 * <properties>
 * <property name="expression">
 * // you may import packages and classes or use the fully qualified name of the class
 * import org.pentaho.reporting.engine.classic.core.*;
 * <p/>
 * String userdefinedFunction (String parameter, Date date)
 * {
 * return parameter + " - the current date is " + date);
 * }
 * <p/>
 * // use simple java code to perform the expression. You may use all classes
 * // available in your classpath as if you write "real" java code in your favourite
 * // IDE.
 * // See the www.beanshell.org site for more information ...
 * //
 * // A return value of type "Object" is alway implied ...
 * getValue ()
 * {
 * return userdefinedFunction ("Hello World :) ", new Date());
 * }
 * </property>
 * </properties>
 * </expression>
 * </pre>
 *
 * @author Thomas Morgner
 */
public class BSHExpression extends AbstractExpression {
  private static final Log logger = LogFactory.getLog( BSHExpression.class );
  /**
   * The headerfile with the default initialisations.
   */
  public static final String BSHHEADERFILE =
      "org/pentaho/reporting/engine/classic/core/modules/misc/beanshell/BSHExpressionHeader.txt"; //$NON-NLS-1$

  /**
   * The beanshell-interpreter used to evaluate the expression.
   */
  private transient Interpreter interpreter;
  private transient boolean invalid;

  private String expression;

  /**
   * default constructor, create a new BeanShellExpression.
   */
  public BSHExpression() {
  }

  /**
   * This method tries to create a new and fully initialized BeanShell interpreter.
   *
   * @return the interpreter or null, if there was no way to create the interpreter.
   */
  protected Interpreter createInterpreter() {
    try {
      final Interpreter interpreter = new Interpreter();
      initializeInterpreter( interpreter );
      return interpreter;
    } catch ( Throwable e ) {
      logger.warn( "Unable to initialize the expression", e ); //$NON-NLS-1$
      return null;
    }
  }

  /**
   * Initializes the bean-shell interpreter by executing the code in the BSHExpressionHeader.txt file.
   *
   * @param interpreter
   *          the interpreter that should be initialized.
   * @throws EvalError
   *           if an BeanShell-Error occured.
   * @throws IOException
   *           if the beanshell file could not be read.
   */
  protected void initializeInterpreter( final Interpreter interpreter ) throws EvalError, IOException {
    final InputStream in = ObjectUtilities.getResourceRelativeAsStream( "BSHExpressionHeader.txt", BSHExpression.class ); //$NON-NLS-1$
    // read the header, creates a skeleton
    final Reader r = new InputStreamReader( new BufferedInputStream( in ) );
    try {
      interpreter.eval( r );
    } finally {
      r.close();
    }

    // now add the userdefined expression
    // the expression is given in form of a function with the signature of:
    //
    // Object getValue ()
    //
    if ( getExpression() != null ) {
      interpreter.eval( expression );
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
    boolean allowScriptEval = ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty(
      "org.pentaho.reporting.engine.classic.core.allowScriptEvaluation", "false" )
      .equalsIgnoreCase( "true" );

    if ( !allowScriptEval ) {
      BSHExpression.logger.error( "Scripts are prevented from running by default in order to avoid"
        + " potential remote code execution.  The system administrator must enable this capability." );
      return null;
    }

    if ( invalid ) {
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
      interpreter.set( "runtime", getRuntime() ); //$NON-NLS-1$
      interpreter.set( "dataRow", getDataRow() ); //$NON-NLS-1$
      return interpreter.eval( "getValue ();" ); //$NON-NLS-1$
    } catch ( Exception e ) {
      if ( logger.isWarnEnabled() ) {
        logger.warn( "Evaluation error: " + //$NON-NLS-1$
            e.getClass() + " - " + e.getMessage() ); //$NON-NLS-1$
      } else if ( logger.isDebugEnabled() ) {
        logger.debug( "Evaluation error: " + //$NON-NLS-1$
            e.getClass() + " - " + e.getMessage(), e ); //$NON-NLS-1$
      }

      return null;
    } finally {
      try {
        interpreter.set( "runtime", null ); //$NON-NLS-1$
        interpreter.set( "dataRow", null ); //$NON-NLS-1$
      } catch ( EvalError er ) {
        // ignored ..
      }
    }
  }

  /**
   * Return a new instance of this expression. The copy is initialized and uses the same parameters as the original, but
   * does not share any objects.
   *
   * @return a copy of this function.
   */
  public Expression getInstance() {
    final BSHExpression ex = (BSHExpression) super.getInstance();
    ex.interpreter = null;
    return ex;
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
   * Sets the beanshell script as string.
   *
   * @return the script.
   */
  public String getExpression() {
    return expression;
  }

  /**
   * Sets the beanshell script that should be executed. The script should define a getValue() method which returns a
   * single object.
   *
   * @param expression
   *          the script.
   */
  public void setExpression( final String expression ) {
    this.expression = expression;
    this.invalid = false;
    this.interpreter = null;
  }
}
