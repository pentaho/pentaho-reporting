package org.pentaho.reporting.libraries.formula.typing.sequence;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.lvalues.LValue;
import org.pentaho.reporting.libraries.formula.typing.ArrayCallback;
import org.pentaho.reporting.libraries.formula.typing.Sequence;

import java.util.Collection;
import java.util.LinkedList;

public class RecursiveSequence implements Sequence {
  private LinkedList<Object> stack;
  private FormulaContext context;

  public RecursiveSequence( final Object object,
                            final FormulaContext context ) {
    this.context = context;
    this.stack = new LinkedList<Object>();
    this.stack.push( object );
  }

  public boolean hasNext() throws EvaluationException {
    if ( stack.isEmpty() ) {
      return false;
    }
    final Object o = stack.pop();
    if ( o instanceof Object[] ) {
      final Object[] array = (Object[]) o;
      final RawArraySequence s = new RawArraySequence( array );
      stack.push( s );
      return hasNext();
    } else if ( o instanceof Collection ) {
      final Collection array = (Collection) o;
      final RawArraySequence s = new RawArraySequence( array );
      stack.push( s );
      return hasNext();
    } else if ( o instanceof ArrayCallback ) {
      final ArrayCallback array = (ArrayCallback) o;
      final AnySequence s = new AnySequence( array, context );
      stack.push( s );
      return hasNext();
    } else if ( o instanceof Sequence ) {
      final Sequence s = (Sequence) o;
      if ( s.hasNext() ) {
        final Object object = s.next();
        stack.push( s );
        stack.push( object );
      }
      return hasNext();
    }

    stack.push( o );
    return true;
  }

  public Object next() throws EvaluationException {
    final Object o = stack.pop();
    if ( o instanceof Sequence ) {
      final Sequence sequence = (Sequence) o;
      return sequence.next();
    }
    return ( o );
  }

  public LValue nextRawValue() throws EvaluationException {
    throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_UNEXPECTED_VALUE );
  }
}
