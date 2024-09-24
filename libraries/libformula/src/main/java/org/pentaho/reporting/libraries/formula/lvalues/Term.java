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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.formula.lvalues;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.operators.InfixOperator;

import java.util.ArrayList;

/**
 * An term is a list of LValues connected by operators. For the sake of efficiency, this is not stored as tree. We store
 * the term as a list in the following format: (headValue)(OP value)* ...
 *
 * @author Thomas Morgner
 */
public class Term extends AbstractLValue {
  private static final LValue[] EMPTY_L_VALUE = new LValue[ 0 ];
  private static final InfixOperator[] EMPTY_OPERATOR = new InfixOperator[ 0 ];
  private static final long serialVersionUID = -1854082494425470979L;

  private LValue optimizedHeadValue;
  private LValue headValue;
  private ArrayList operators;
  private ArrayList operands;
  private InfixOperator[] operatorArray;
  private LValue[] operandsArray;
  private boolean initialized;

  public Term( final LValue headValue ) {
    if ( headValue == null ) {
      throw new NullPointerException();
    }

    this.headValue = headValue;
  }

  public TypeValuePair evaluate() throws EvaluationException {
    TypeValuePair result = optimizedHeadValue.evaluate();
    for ( int i = 0; i < operandsArray.length; i++ ) {
      final LValue value = operandsArray[ i ];
      final InfixOperator op = operatorArray[ i ];
      result = op.evaluate( getContext(), result, value.evaluate() );
    }
    return result;
  }

  public void add( final InfixOperator operator, final LValue operand ) {
    if ( operator == null ) {
      throw new NullPointerException();
    }
    if ( operand == null ) {
      throw new NullPointerException();
    }

    if ( operands == null || operators == null ) {
      this.operands = new ArrayList();
      this.operators = new ArrayList();
    }

    operands.add( operand );
    operators.add( operator );
    initialized = false;
  }

  public void initialize( final FormulaContext context ) throws EvaluationException {
    super.initialize( context );
    if ( operands == null || operators == null ) {
      this.optimizedHeadValue = headValue;
      this.optimizedHeadValue.initialize( context );
      this.operandsArray = EMPTY_L_VALUE;
      this.operatorArray = EMPTY_OPERATOR;
      return;
    }

    if ( initialized ) {
      optimizedHeadValue.initialize( context );
      for ( int i = 0; i < operandsArray.length; i++ ) {
        final LValue lValue = operandsArray[ i ];
        lValue.initialize( context );
      }
      return;
    }

    optimize();
    this.optimizedHeadValue.initialize( context );
    for ( int i = 0; i < operandsArray.length; i++ ) {
      final LValue value = operandsArray[ i ];
      value.initialize( context );
    }
    initialized = true;
  }

  private void optimize() {
    if ( operands == null || operators == null ) {
      this.optimizedHeadValue = headValue;
      this.operandsArray = EMPTY_L_VALUE;
      this.operatorArray = EMPTY_OPERATOR;
      return;
    }
    final ArrayList operators = (ArrayList) this.operators.clone();
    final ArrayList operands = (ArrayList) this.operands.clone();
    this.optimizedHeadValue = headValue;

    while ( true ) {
      // now start to optimize everything.
      // first, search the operator with the highest priority..
      final InfixOperator op = (InfixOperator) operators.get( 0 );
      int level = op.getLevel();
      boolean moreThanOne = false;
      for ( int i = 1; i < operators.size(); i++ ) {
        final InfixOperator operator = (InfixOperator) operators.get( i );
        final int opLevel = operator.getLevel();
        if ( opLevel != level ) {
          moreThanOne = true;
          level = Math.min( opLevel, level );
        }
      }

      if ( moreThanOne == false ) {
        // No need to optimize the operators ..
        break;
      }

      // There are at least two op-levels in this term.
      Term subTerm = null;
      for ( int i = 0; i < operators.size(); i++ ) {
        final InfixOperator operator = (InfixOperator) operators.get( i );
        if ( operator.getLevel() != level ) {
          subTerm = null;
          continue;
        }

        if ( subTerm == null ) {
          if ( i == 0 ) {
            subTerm = new Term( optimizedHeadValue );
            optimizedHeadValue = subTerm;
          } else {
            final LValue lval = (LValue) operands.get( i - 1 );
            subTerm = new Term( lval );
            operands.set( i - 1, subTerm );
          }
        }

        // OK, now a term exists, and we should join it.
        final LValue operand = (LValue) operands.get( i );
        subTerm.add( operator, operand );
        operands.remove( i );
        operators.remove( i );
        // Rollback the current index ..
        //noinspection AssignmentToForLoopParameter
        i -= 1;
      }
    }

    this.operatorArray = (InfixOperator[])
      operators.toArray( new InfixOperator[ operators.size() ] );
    this.operandsArray = (LValue[])
      operands.toArray( new LValue[ operands.size() ] );
  }

  /**
   * Returns any dependent lvalues (parameters and operands, mostly).
   *
   * @return
   */
  public LValue[] getChildValues() {
    if ( operandsArray == null ) {
      optimize();
    }
    final LValue[] values = new LValue[ operandsArray.length + 1 ];
    values[ 0 ] = headValue;
    System.arraycopy( operandsArray, 0, values, 1, operandsArray.length );
    return values;
  }


  public String toString() {
    return toString( false );
  }

  public String toString( final boolean root ) {
    final StringBuilder b = new StringBuilder( 100 );

    if ( !root ) {
      b.append( '(' );
    }
    b.append( headValue );
    if ( operands != null && operators != null ) {
      for ( int i = 0; i < operands.size(); i++ ) {
        final InfixOperator op = (InfixOperator) operators.get( i );
        final LValue value = (LValue) operands.get( i );
        b.append( op );
        b.append( value );
      }
    }
    if ( !root ) {
      b.append( ')' );
    }
    return b.toString();
  }

  /**
   * Checks whether the LValue is constant. Constant lvalues always return the same value.
   *
   * @return
   */
  public boolean isConstant() {
    if ( headValue.isConstant() == false ) {
      return false;
    }

    for ( int i = 0; i < operands.size(); i++ ) {
      final LValue value = (LValue) operands.get( i );
      if ( value.isConstant() == false ) {
        return false;
      }
    }
    return true;
  }

  public Object clone() throws CloneNotSupportedException {
    final Term o = (Term) super.clone();
    if ( operands != null ) {
      o.operands = (ArrayList) operands.clone();
    }
    if ( operators != null ) {
      o.operators = (ArrayList) operators.clone();
    }
    o.headValue = (LValue) headValue.clone();
    o.optimizedHeadValue = null;
    o.operandsArray = null;
    o.operatorArray = null;
    o.initialized = false;
    return o;
  }

  public LValue[] getOperands() {
    return (LValue[]) operands.toArray( new LValue[ operands.size() ] );
  }

  public InfixOperator[] getOperators() {
    return (InfixOperator[]) operators.toArray( new InfixOperator[ operators.size() ] );
  }

  public LValue getHeadValue() {
    return headValue;
  }

  /**
   * Allows access to the post optimized head value note that without the optimization, it's difficult to traverse
   * libformula's object model.
   *
   * @return optimized head value
   */
  public LValue getOptimizedHeadValue() {
    return optimizedHeadValue;
  }
  //
  //  /**
  //   * Allows access to the post optimized operator array
  //   *
  //   * @return optimized operator array
  //   */
  //  public InfixOperator[] getOptimizedOperators()
  //  {
  //    return operatorArray;
  //  }
  //
  //  /**
  //   * Allows access to the post optimized operand array
  //   *
  //   * @return optimized operand array
  //   */
  //  public LValue[] getOptimizedOperands()
  //  {
  //    return operandsArray;
  //  }

  public ParsePosition getParsePosition() {
    final ParsePosition parsePosition = super.getParsePosition();
    if ( parsePosition == null ) {
      final int startColumn = headValue.getParsePosition().getStartColumn();
      final int startLine = headValue.getParsePosition().getStartLine();
      final ParsePosition lastParsePos =
        operandsArray[ operandsArray.length - 1 ].getParsePosition();
      final int endColumn = lastParsePos.getEndColumn();
      final int endLine = lastParsePos.getEndLine();
      setParsePosition( new ParsePosition( startLine, startColumn, endLine, endColumn ) );
    }
    return super.getParsePosition();
  }
}
