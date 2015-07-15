package org.pentaho.reporting.libraries.formula.typing;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.Messages;

import java.util.ArrayList;
import java.util.Locale;

public class TypeUtil {
  private TypeUtil() {
  }

  public static String getParameterType( final Type parameterType,
                                         final Locale locale ) {
    final Messages messages = new Messages( locale );
    if ( parameterType.isFlagSet( Type.DATE_TYPE ) ) {
      return messages.getString( "Type.Date" );
    } else if ( parameterType.isFlagSet( Type.LOGICAL_TYPE ) ) {
      return messages.getString( "Type.Logical" );
    }
    if ( parameterType.isFlagSet( Type.DATETIME_TYPE ) ) {
      return messages.getString( "Type.Datetime" );
    }
    if ( parameterType.isFlagSet( Type.NUMERIC_TYPE ) ) {
      return messages.getString( "Type.Number" );
    }
    if ( parameterType.isFlagSet( Type.NUMERIC_SEQUENCE_TYPE ) ) {
      return messages.getString( "Type.NumberSequence" );
    }
    if ( parameterType.isFlagSet( Type.NUMERIC_UNIT ) ) {
      return messages.getString( "Type.Unit" );
    } else if ( parameterType.isFlagSet( Type.TEXT_TYPE ) ) {
      return messages.getString( "Type.Text" );
    } else if ( parameterType.isFlagSet( Type.ANY_TYPE ) ) {
      return messages.getString( "Type.AnyType" );
    }
    return messages.getString( "Type.Invalid" );
  }


  public static Object[] normalize( final Sequence sequence ) throws EvaluationException {
    if ( sequence == null ) {
      return new Object[ 0 ];
    }
    final ArrayList retval = new ArrayList();
    while ( sequence.hasNext() ) {
      final Object o = sequence.next();
      if ( o != null ) {
        retval.add( o );
      }
    }
    return retval.toArray();
  }

  public static Object[] normalize( final ArrayCallback sequence ) throws EvaluationException {
    if ( sequence == null ) {
      return new Object[ 0 ];
    }
    final ArrayList retval = new ArrayList();
    final int rowCount = sequence.getRowCount();
    final int colCount = sequence.getColumnCount();
    for ( int row = 0; row < rowCount; row += 1 ) {
      for ( int col = 0; col < colCount; col += 1 ) {
        retval.add( sequence.getValue( row, col ) );
      }
    }
    return retval.toArray();
  }
}
