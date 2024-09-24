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
 * Copyright (c) 2001 - 2017 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.function;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineCoreModule;
import org.pentaho.reporting.engine.classic.core.InvalidReportStateException;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.formula.ErrorValue;
import org.pentaho.reporting.libraries.formula.Formula;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;

/**
 * The formula function is a stateful version of the FormulaExpression and is used to evaluate a LibFormula/OpenFormula
 * expression. This function can be used if a stateful evaluation is needed or if the formula value should be
 * initialized to a certain value before the real evaluation starts.
 *
 * @author Thomas Morgner
 */
public final class FormulaFunction extends AbstractFunction {
  private static final Log logger = LogFactory.getLog( FormulaFunction.class );
  /**
   * A cached version of the compiled formula.
   */
  private transient Formula compiledFormula;
  /**
   * The formula namespace as defined by OpenFormula.
   */
  private String formulaNamespace;
  /**
   * The formula itself.
   */
  private String formulaExpression;
  /**
   * The formula as specified by the user. This is the formula and the namespace.
   */
  private String formula;

  /**
   * The formula namespace of the initial formula as defined by OpenFormula.
   */
  private String initialNamespace;
  /**
   * The initial formula itself.
   */
  private String initialExpression;
  /**
   * The initial formula as specified by the user. This is the formula and the namespace.
   */
  private String initial;
  /**
   * A flag indicating whether the initial-formula has been evaluated.
   */
  private boolean initialized;
  /**
   * A flag indicating that the formula cannot be parsed.
   */
  private Exception formulaError;

  private Boolean failOnError;

  /**
   * Default Constructor.
   */
  public FormulaFunction() {
  }

  public Boolean getFailOnError() {
    return failOnError;
  }

  public void setFailOnError( final Boolean failOnError ) {
    this.failOnError = failOnError;
  }

  /**
   * Returns the defined formula context from the report processing context.
   *
   * @return the formula context.
   */
  private FormulaContext getFormulaContext() {
    final ProcessingContext globalContext = getRuntime().getProcessingContext();
    return globalContext.getFormulaContext();
  }

  /**
   * Returns the initial formula (incuding the optional namespace) as defined by the OpenFormula standard.
   *
   * @return the formula as text.
   */
  public String getInitial() {
    return initial;
  }

  /**
   * Returns the initial formula expression. The initial formula is used at the first call only.
   *
   * @return the initial formula expression.
   */
  public String getInitialExpression() {
    return initialExpression;
  }

  /**
   * Returns the formula namespace if the initial formula. If the formula specified by the user starts with "=", then
   * the namespace "report" is assumed.
   *
   * @return the namespace of the formula.
   */
  public String getInitialNamespace() {
    return initialNamespace;
  }

  /**
   * Defines the initial formula (incuding the optional namespace) as defined by the OpenFormula standard. The initial
   * formula is used at the first call only.
   *
   * @param initial
   *          the initial formula as text.
   */
  public void setInitial( String initial ) {
    this.initial = initial;
    if ( initial == null ) {
      initialNamespace = null;
      initialExpression = null;
    } else {
      if ( initial.endsWith( ";" ) ) {
        logger.warn( "A initial-formula with a trailing semicolon is not valid. Auto-correcting the initial-formula." );
        initial = initial.substring( 0, initial.length() - 1 );
      }

      final int separator = initial.indexOf( ':' );
      if ( separator <= 0 || ( ( separator + 1 ) == initial.length() ) ) {
        if ( initial.length() > 0 && initial.charAt( 0 ) == '=' ) {
          initialNamespace = "report";
          initialExpression = initial.substring( 1 );
        } else {
          // error: invalid formula.
          initialNamespace = null;
          initialExpression = null;
        }
      } else {
        initialNamespace = initial.substring( 0, separator );
        initialExpression = initial.substring( separator + 1 );
      }
    }
  }

  /**
   * Resets the function state.
   *
   * @param event
   *          the report event.
   */
  public void reportInitialized( final ReportEvent event ) {
    initialized = false;
  }

  /**
   * Returns the formula (incuding the optional namespace) as defined by the OpenFormula standard.
   *
   * @return the formula as text.
   */
  public String getFormula() {
    return formula;
  }

  /**
   * Returns the formula namespace. If the formula specified by the user starts with "=", then the namespace "report" is
   * assumed.
   *
   * @return the namespace of the formula.
   */
  public String getFormulaNamespace() {
    return formulaNamespace;
  }

  /**
   * Returns the formula expression.
   *
   * @return the formula expression.
   */
  public String getFormulaExpression() {
    return formulaExpression;
  }

  /**
   * Defines the formula (incuding the optional namespace) as defined by the OpenFormula standard.
   *
   * @param formula
   *          the formula as text.
   */
  public void setFormula( final String formula ) {
    this.formula = formula;
    if ( formula == null ) {
      formulaNamespace = null;
      formulaExpression = null;
    } else {
      final int separator = formula.indexOf( ':' );
      if ( separator <= 0 || ( ( separator + 1 ) == formula.length() ) ) {
        if ( formula.length() > 0 && formula.charAt( 0 ) == '=' ) {
          formulaNamespace = "report";
          formulaExpression = formula.substring( 1 );
        } else {
          // error: invalid formula.
          formulaNamespace = null;
          formulaExpression = null;
        }
      } else {
        formulaNamespace = formula.substring( 0, separator );
        formulaExpression = formula.substring( separator + 1 );
      }
    }
    this.compiledFormula = null;
    this.formulaError = null;
  }

  /**
   * Computes the value of the formula by evaluating the initial formula against the current data-row.
   *
   * @return the computed value or null, if an error occured.
   */
  private Object computeInitialValue() {
    try {
      if ( initial != null ) {
        final ExpressionRuntime expressionRuntime = getRuntime();
        final Formula initFormula = new Formula( initialExpression );
        final ReportFormulaContext context = new ReportFormulaContext( getFormulaContext(), expressionRuntime );
        try {
          initFormula.initialize( context );
          final Object evaluate = initFormula.evaluate();
          if ( Boolean.TRUE.equals( getComputedFailOnError() ) ) {
            if ( evaluate instanceof ErrorValue ) {
              throw new InvalidReportStateException( String.format(
                  "Failed to evaluate formula-expression with error %s", // NON-NLS
                  evaluate ) );
            }
          }
          return evaluate;
        } finally {
          context.close();
        }
      }

      // if the code above did not trigger, compute a regular thing ..
      return computeRegularValue();
    } catch ( Exception e ) {
      if ( FormulaFunction.logger.isDebugEnabled() ) {
        final Configuration config = getReportConfiguration();
        if ( "true".equals( config
            .getConfigProperty( "org.pentaho.reporting.engine.classic.core.function.LogFormulaFailureCause" ) ) ) {
          FormulaFunction.logger.debug( "Failed to compute the initial value [" + formulaExpression + ']', e );
        } else {
          FormulaFunction.logger.debug( "Failed to compute the initial value [" + formulaExpression + ']' );
        }
      }
      if ( Boolean.TRUE.equals( getComputedFailOnError() ) ) {
        throw new InvalidReportStateException( String.format( "Failed to evaluate formula-function with error %s", // NON-NLS
            e.getMessage() ), e );
      }
      return LibFormulaErrorValue.ERROR_UNEXPECTED_VALUE;
    }
  }

  /**
   * Computes the value of the formula by evaluating the formula against the current data-row.
   *
   * @return the computed value or null, if an error occurred.
   */
  private Object computeRegularValue() {
    if ( formulaError != null ) {
      if ( Boolean.TRUE.equals( getComputedFailOnError() ) ) {
        throw new InvalidReportStateException( String.format(
            "Previously failed to evaluate formula-expression with error %s", // NON-NLS
            formulaError ) );
      }
      return LibFormulaErrorValue.ERROR_UNEXPECTED_VALUE;
    }

    try {
      if ( compiledFormula == null ) {
        compiledFormula = new Formula( formulaExpression );
      }
      final ExpressionRuntime expressionRuntime = getRuntime();
      final ReportFormulaContext context = new ReportFormulaContext( getFormulaContext(), expressionRuntime );
      try {
        compiledFormula.initialize( context );
        final Object evaluate = compiledFormula.evaluate();
        if ( Boolean.TRUE.equals( getComputedFailOnError() ) ) {
          if ( evaluate instanceof ErrorValue ) {
            throw new InvalidReportStateException( String.format(
                "Failed to evaluate formula-expression with error %s", // NON-NLS
                evaluate ) );
          }
        }
        return evaluate;
      } finally {
        context.close();
      }
    } catch ( Exception e ) {
      formulaError = e;
      if ( FormulaFunction.logger.isDebugEnabled() ) {
        final Configuration config = getReportConfiguration();
        if ( "true".equals( config
            .getConfigProperty( "org.pentaho.reporting.engine.classic.core.function.LogFormulaFailureCause" ) ) ) {
          FormulaFunction.logger.debug( "Failed to compute the regular value [" + formulaExpression + ']', e );
        } else {
          FormulaFunction.logger.debug( "Failed to compute the regular value [" + formulaExpression + ']' );
        }
      }
      if ( Boolean.TRUE.equals( getComputedFailOnError() ) ) {
        throw new InvalidReportStateException( String.format( "Failed to evaluate formula-function with error %s", // NON-NLS
            e.getMessage() ), e );
      }
      return LibFormulaErrorValue.ERROR_UNEXPECTED_VALUE;
    }
  }

  /**
   * Return the computed value of the formula. The first call will return the initial-value instead.
   *
   * @return the value of the function.
   */
  public Object getValue() {
    try {
      if ( initialized == false ) {
        initialized = true;
        return computeInitialValue();
      }
      return computeRegularValue();
    } catch ( final InvalidReportStateException e ) {
      throw e;
    }
  }

  /**
   * Clones the expression, expression should be reinitialized after the cloning.
   * <P>
   * Expression maintain no state, cloning is done at the beginning of the report processing to disconnect the used
   * expression from any other object space.
   *
   * @return A clone of this expression.
   * @throws CloneNotSupportedException
   *           this should never happen.
   */
  public Object clone() throws CloneNotSupportedException {
    final FormulaFunction o = (FormulaFunction) super.clone();
    if ( compiledFormula != null ) {
      o.compiledFormula = (Formula) compiledFormula.clone();
    }
    return o;
  }

  /**
   * Return a completly separated copy of this function. The copy does no longer share any changeable objects with the
   * original function.
   *
   * @return a copy of this function.
   */
  public Expression getInstance() {
    final FormulaFunction instance = (FormulaFunction) super.getInstance();
    instance.compiledFormula = null;
    instance.formulaError = null;
    return instance;
  }

  private Boolean getComputedFailOnError() {
    return failOnError == null
            ? "true".equals( getReportConfiguration().getConfigProperty( ClassicEngineCoreModule.STRICT_ERROR_HANDLING_KEY ) )
            : failOnError;
  }
}
