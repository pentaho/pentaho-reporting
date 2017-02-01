package org.pentaho.reporting.libraries.formula.typing.sequence;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.lvalues.LValue;
import org.pentaho.reporting.libraries.formula.lvalues.StaticValue;
import org.pentaho.reporting.libraries.formula.typing.Sequence;

import java.util.Collection;

public class RawArraySequence implements Sequence {
  private Object[] array;
  private int counter;

  public RawArraySequence( final Object[] array ) {
    this.array = array;
    this.counter = 0;
  }

  public RawArraySequence( final Collection array ) {
    this.array = array.toArray();
    this.counter = 0;
  }

  public boolean hasNext() throws EvaluationException {
    return counter < array.length;
  }

  public Object next() throws EvaluationException {
    final Object retval = array[ counter ];
    counter += 1;
    return retval;
  }

  public LValue nextRawValue() throws EvaluationException {
    final Object retval = array[ counter ];
    counter += 1;
    return new StaticValue( retval );
  }
}
