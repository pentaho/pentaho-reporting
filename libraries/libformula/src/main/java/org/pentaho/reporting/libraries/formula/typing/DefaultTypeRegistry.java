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
 * Copyright (c) 2006 - 2021 Hitachi Vantara and Contributors.  All rights reserved.
 */

package org.pentaho.reporting.libraries.formula.typing;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LocalizationContext;
import org.pentaho.reporting.libraries.formula.lvalues.DefaultDataTable;
import org.pentaho.reporting.libraries.formula.lvalues.LValue;
import org.pentaho.reporting.libraries.formula.lvalues.StaticValue;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.coretypes.AnyType;
import org.pentaho.reporting.libraries.formula.typing.coretypes.DateTimeType;
import org.pentaho.reporting.libraries.formula.typing.coretypes.LogicalType;
import org.pentaho.reporting.libraries.formula.typing.coretypes.NumberType;
import org.pentaho.reporting.libraries.formula.typing.coretypes.TextType;
import org.pentaho.reporting.libraries.formula.typing.sequence.AnyNumberSequence;
import org.pentaho.reporting.libraries.formula.typing.sequence.AnySequence;
import org.pentaho.reporting.libraries.formula.typing.sequence.DefaultNumberSequence;
import org.pentaho.reporting.libraries.formula.util.DateUtil;
import org.pentaho.reporting.libraries.formula.util.HSSFDateUtil;
import org.pentaho.reporting.libraries.formula.util.NumberUtil;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.Time;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Creation-Date: 02.11.2006, 12:46:08
 *
 * @author Thomas Morgner
 */
public class DefaultTypeRegistry implements TypeRegistry {
  private static final Log logger = LogFactory.getLog( DefaultTypeRegistry.class );

  private static class ArrayConverterCallback implements ArrayCallback {
    private Object retval;
    private Type targetType;

    private ArrayConverterCallback( final Object retval, final Type targetType ) {
      this.retval = retval;
      this.targetType = targetType;
    }

    public LValue getRaw( final int row, final int column ) {
      return new StaticValue( retval, targetType );
    }

    public Object getValue( final int row, final int column ) throws EvaluationException {
      if ( row == 0 && column == 0 ) {
        return retval;
      }
      return null;
    }

    public Type getType( final int row, final int column ) throws EvaluationException {
      if ( row == 0 && column == 0 ) {
        return targetType;
      }
      return null;
    }

    public int getColumnCount() {
      return 1;
    }

    public int getRowCount() {
      return 1;
    }
  }

  private static final BigDecimal NUM_TRUE = new BigDecimal( "1" );
  private static final BigDecimal NUM_FALSE = new BigDecimal( "0" );
  private static final double ZERO = 0.0;

  private FormulaContext context;

  public DefaultTypeRegistry() {
  }

  /**
   * Returns an comparator for the given types.
   *
   * @param type1
   * @param type2
   * @return
   */
  public ExtendedComparator getComparator( final Type type1, final Type type2 ) {
    final DefaultComparator comparator = new DefaultComparator();
    comparator.inititalize( context );
    return comparator;
  }

  /**
   * converts the object of the given type into a number. If the object is not convertible, a NumberFormatException is
   * thrown. If the given value is null or not parsable as number, return null.
   *
   * @param sourceType
   * @param value
   * @return
   * @throws NumberFormatException if the type cannot be represented as number.
   */
  public Number convertToNumber( final Type sourceType, final Object value )
    throws EvaluationException {
    return convertToNumber( sourceType, value, true );
  }

  public Number convertToNumber( final Type sourceType, final Object value, final boolean strictTypeChecks )
    throws EvaluationException {
    final LocalizationContext localizationContext = context.getLocalizationContext();

    if ( value == null ) {
      // there's no point in digging deeper - there *is* no value ..
      throw TypeConversionException.getInstance();
    }

    final boolean isAnyType = sourceType.isFlagSet( Type.ANY_TYPE );
    if ( sourceType.isFlagSet( Type.NUMERIC_TYPE ) || isAnyType ) {
      if ( sourceType.isFlagSet( Type.DATETIME_TYPE )
        || sourceType.isFlagSet( Type.TIME_TYPE )
        || sourceType.isFlagSet( Type.DATE_TYPE )
        || isAnyType ) {
        if ( value instanceof Date ) {
          final BigDecimal serial = HSSFDateUtil.getExcelDate( (Date) value );
          return DateUtil.normalizeDate( serial, sourceType );
        }
      }

      if ( value instanceof Number ) {
        return (Number) value;
      }
    }

    if ( sourceType.isFlagSet( Type.LOGICAL_TYPE ) || isAnyType ) {
      if ( value instanceof Boolean ) {
        if ( Boolean.TRUE.equals( value ) ) {
          return NUM_TRUE;
        } else {
          return NUM_FALSE;
        }
      }
    }

    if ( sourceType.isFlagSet( Type.TEXT_TYPE ) || isAnyType ) {
      final String val = computeStringValue( value );

      // first, try to parse the value as a big-decimal.
      try {
        return new BigDecimal( val );
      } catch ( NumberFormatException e ) {
        // ignore ..
      }

      for ( final DateFormat df : localizationContext.getDateFormats( DateTimeType.DATETIME_TYPE ) ) {
        final Date date = parse( df, val );
        if ( date != null ) {
          return HSSFDateUtil.getExcelDate( date );
        }
      }

      for ( final DateFormat df : localizationContext.getDateFormats( DateTimeType.DATE_TYPE ) ) {
        final Date date = parse( df, val );
        if ( date != null ) {
          return HSSFDateUtil.getExcelDate( date );
        }
      }

      for ( final DateFormat df : localizationContext.getDateFormats( DateTimeType.TIME_TYPE ) ) {
        final Date date = parse( df, val );
        if ( date != null ) {
          return HSSFDateUtil.getExcelDate( date );
        }
      }

      // then checking for numbers
      for ( final NumberFormat format : localizationContext.getNumberFormats() ) {
        final Number number = parse( format, val );
        if ( number != null ) {
          return number;
        }
      }

      if ( !strictTypeChecks ) {
        return 0;
      }
    }

    throw TypeConversionException.getInstance();
  }

  private static Number parse( final NumberFormat format, final String source ) {
    final ParsePosition parsePosition = new ParsePosition( 0 );
    final Number result = format.parse( source, parsePosition );
    if ( parsePosition.getIndex() == 0 || parsePosition.getIndex() != source.length() ) {
      return null;
    }
    return result;
  }

  private static Date parse( final DateFormat format, final String source ) {
    final ParsePosition parsePosition = new ParsePosition( 0 );
    final Date result = format.parse( source, parsePosition );
    if ( parsePosition.getIndex() == 0 || parsePosition.getIndex() != source.length() ) {
      return null;
    }
    return result;
  }

  /**
   * @param configuration
   * @param formulaContext
   * @deprecated Use the single-argument function instead.
   */
  public void initialize( final Configuration configuration,
                          final FormulaContext formulaContext ) {
    this.initialize( formulaContext );
  }

  public void initialize( final FormulaContext formulaContext ) {
    if ( formulaContext == null ) {
      throw new NullPointerException();
    }
    this.context = formulaContext;
  }

  public String convertToText( final Type type1, final Object value )
    throws EvaluationException {
    if ( value == null ) {
      return "";
    }

    // already converted or compatible
    if ( type1.isFlagSet( Type.TEXT_TYPE ) ) {
      // no need to check whatever it is a String
      return computeStringValue( value );
    }

    if ( type1.isFlagSet( Type.LOGICAL_TYPE ) ) {
      if ( value instanceof Boolean ) {
        final Boolean b = (Boolean) value;
        if ( Boolean.TRUE.equals( b ) ) {
          return "TRUE";
        } else {
          return "FALSE";
        }
      } else {
        throw TypeConversionException.getInstance();
      }
    }

    // 2 types of numeric : numbers and dates
    if ( type1.isFlagSet( Type.NUMERIC_TYPE ) ) {
      final LocalizationContext localizationContext = context.getLocalizationContext();
      if ( type1.isFlagSet( Type.DATETIME_TYPE ) || type1.isFlagSet( Type.DATE_TYPE ) || type1
        .isFlagSet( Type.TIME_TYPE ) ) {
        final Date d = convertToDate( type1, value );
        final List dateFormats = localizationContext.getDateFormats( type1 );
        if ( dateFormats != null && dateFormats.size() >= 1 ) {
          final DateFormat format = (DateFormat) dateFormats.get( 0 );
          return format.format( d );
        } else {
          // fallback
          return DateFormat.getDateTimeInstance( DateFormat.FULL, DateFormat.FULL, localizationContext.getLocale() )
            .format( d );
        }
      } else {
        try {
          final Number n = convertToNumber( type1, value );
          final List<NumberFormat> numberFormats = localizationContext.getNumberFormats();
          if ( numberFormats.isEmpty() ) {
            // use the canonical format ..
            return NumberFormat.getNumberInstance( localizationContext.getLocale() ).format( n );
          } else {
            numberFormats.get( 0 ).format( n );
          }
        } catch ( EvaluationException nfe ) {
          // ignore ..
        }
      }
    }

    return computeStringValue( value );
  }

  private String computeStringValue( final Object retval ) throws EvaluationException {
    if ( retval instanceof Clob ) {
      try {
        return IOUtils.getInstance().readClob( (Clob) retval );
      } catch ( Exception e ) {
        return null;
      }
    }
    if ( retval instanceof String ) {
      return (String) retval;
    }
    if ( retval != null ) {
      return unwrap( retval, new StringBuilder() ).toString();
    }
    return null;
  }

  private StringBuilder unwrap( final Object retval, final StringBuilder b ) throws EvaluationException {
    if ( retval.getClass().isArray() ) {
      return unwrapArray( retval, b );
    }
    if ( retval instanceof Sequence ) {
      return unwrapSequence( (Sequence) retval, b );
    }
    if ( retval instanceof ArrayCallback ) {
      return unwrapArrayCallback( (ArrayCallback) retval, b );
    }
    if ( retval instanceof Collection ) {
      return unwrapCollection( (Collection<?>) retval, b );
    }
    return b.append( retval );
  }

  private StringBuilder unwrapCollection( final Collection<?> retval, final StringBuilder b )
    throws EvaluationException {
    final Iterator<?> it = retval.iterator();
    while ( it.hasNext() ) {
      unwrap( it.next(), b );
      if ( it.hasNext() ) {
        b.append( ", " );
      }
    }
    return b;
  }

  private StringBuilder unwrapSequence( final Sequence retval, final StringBuilder b ) throws EvaluationException {
    while ( retval.hasNext() ) {
      unwrap( retval.next(), b );
      if ( retval.hasNext() ) {
        b.append( ", " );
      }
    }
    return b;
  }

  private StringBuilder unwrapArrayCallback( final ArrayCallback retval, final StringBuilder b )
    throws EvaluationException {
    int rc = retval.getRowCount();
    int cc = retval.getColumnCount();
    for ( int r = 0; r < rc; r += 1 ) {
      for ( int c = 0; c < cc; c += 1 ) {
        if ( r != 0 || c != 0 ) {
          b.append( ", " );
        }
        unwrap( retval.getValue( r, c ), b );
      }
    }
    return b;
  }

  private StringBuilder unwrapArray( final Object retval, final StringBuilder b ) throws EvaluationException {
    int length = Array.getLength( retval );
    for ( int i = 0; i < length; i += 1 ) {
      if ( i != 0 ) {
        b.append( ", " );
      }
      unwrap( Array.get( retval, i ), b );
    }
    return b;
  }

  public Boolean convertToLogical( final Type type1, final Object value )
    throws TypeConversionException {
    if ( value == null ) {
      return Boolean.FALSE;
    }

    // already converted or compatible
    if ( type1.isFlagSet( Type.LOGICAL_TYPE ) || type1.isFlagSet( Type.ANY_TYPE ) ) {
      if ( value instanceof Boolean ) {
        return (Boolean) value;
      }

      // fallback
      if ( "true".equalsIgnoreCase( String.valueOf( value ) ) ) {
        return Boolean.TRUE;
      }
      return Boolean.FALSE;
    }

    if ( type1.isFlagSet( Type.NUMERIC_TYPE ) ) {
      // no need to check between different types of numeric
      if ( value instanceof Number ) {
        final Number num = (Number) value;
        if ( ZERO != num.doubleValue() ) {
          return Boolean.TRUE;
        }
      }

      // fallback
      return Boolean.FALSE;
    }

    if ( type1.isFlagSet( Type.TEXT_TYPE ) ) {
      // no need to convert it to String
      try {
        final String str = computeStringValue( value );
        if ( "TRUE".equalsIgnoreCase( str ) ) {
          return Boolean.TRUE;
        } else if ( "FALSE".equalsIgnoreCase( str ) ) {
          return Boolean.FALSE;
        }
      } catch ( final EvaluationException e ) {
        throw TypeConversionException.getInstance();
      }
    }

    throw TypeConversionException.getInstance();
  }

  public Date convertToDate( final Type type1, final Object value )
    throws EvaluationException {
    if ( type1.isFlagSet( Type.NUMERIC_TYPE ) || type1.isFlagSet( Type.ANY_TYPE ) ) {
      if ( type1.isFlagSet( Type.DATE_TYPE )
        || type1.isFlagSet( Type.DATETIME_TYPE )
        || type1.isFlagSet( Type.TIME_TYPE ) || type1.isFlagSet( Type.ANY_TYPE ) ) {
        if ( value instanceof Date ) {
          return DateUtil.normalizeDate( (Date) value, type1 );
        }
      }
    }
    final Number serial = convertToNumber( type1, value );
    final BigDecimal bd = NumberUtil.getAsBigDecimal( serial );
    return HSSFDateUtil.getJavaDate( bd );
  }

  public ArrayCallback convertToArray( final Type type, final Object value ) throws EvaluationException {
    if ( value instanceof ArrayCallback ) {
      return (ArrayCallback) value;
    }

    if ( value == null ) {
      return new DefaultDataTable().getAsArray();
    }

    final Class valueType = value.getClass();
    if ( valueType.isArray() == false ) {
      if ( value instanceof Collection ) {
        final Collection colVal = (Collection) value;
        final DefaultDataTable table = new DefaultDataTable();
        final Iterator iterator = colVal.iterator();
        int i = 0;
        while ( iterator.hasNext() ) {
          table.setObject( i, 0, new StaticValue( iterator.next() ) );
          i += 1;
        }
        return table.getAsArray();
      }
      return new ArrayConverterCallback( value, type );
    }

    final Class componentType = valueType.getComponentType();
    if ( componentType.isArray() ) {
      final DefaultDataTable table = new DefaultDataTable();
      final int length = Array.getLength( value );
      for ( int row = 0; row < length; row++ ) {
        final Object innerArray = Array.get( value, row );
        final int innerLength = Array.getLength( innerArray );
        for ( int col = 0; col < innerLength; col++ ) {
          table.setObject( row, col, new StaticValue( Array.get( innerArray, col ) ) );
        }
      }
      return table.getAsArray();
    }

    final DefaultDataTable table = new DefaultDataTable();
    final int length = Array.getLength( value );
    for ( int i = 0; i < length; i++ ) {
      table.setObject( i, 0, new StaticValue( Array.get( value, i ) ) );
    }
    return table.getAsArray();
  }


  /**
   * An internal method that converts the given value-pair into a sequence.
   *
   * @param targetType
   * @param valuePair
   * @return
   * @throws TypeConversionException if there was a error while converting types.
   */
  private TypeValuePair convertToSequence( final Type targetType, final TypeValuePair valuePair )
    throws EvaluationException {
    if ( targetType.isFlagSet( Type.NUMERIC_SEQUENCE_TYPE ) ) {
      return new TypeValuePair( targetType,
        convertToNumberSequence( valuePair.getType(), valuePair.getValue(), true ) );
    }

    return new TypeValuePair( targetType, convertToSequence( valuePair.getType(), valuePair.getValue() ) );
  }

  public Sequence convertToSequence( final Type type, final Object value ) throws EvaluationException {
    // scalar
    if ( type.isFlagSet( Type.SCALAR_TYPE ) ) {
      return new AnySequence( new StaticValue( value, type ), context );
    } else if ( type.isFlagSet( Type.SEQUENCE_TYPE ) ) {
      // already a sequence
      if ( value instanceof Sequence ) {
        return (Sequence) value;
      } else {
        logger.warn( "Assertation failure: Type declared to be a sequence, but no sequence found inside." );
        throw TypeConversionException.getInstance();
      }
    } else if ( type.isFlagSet( Type.ARRAY_TYPE ) ) {
      // an array source
      if ( value instanceof ArrayCallback ) {
        return new AnySequence( (ArrayCallback) value, context );
      } else if ( value instanceof Object[] ) {
        return new AnySequence( convertToArray( type, value ), context );
      } else {
        logger.warn( "Assertation failure: Type declared to be array, but no array callback found inside." );
        throw TypeConversionException.getInstance();
      }
    }
    throw TypeConversionException.getInstance();
  }

  public NumberSequence convertToNumberSequence( final Type type, final Object value, final boolean strict )
    throws EvaluationException {
    // sequence array
    if ( type.isFlagSet( Type.NUMERIC_SEQUENCE_TYPE ) ) {
      if ( value instanceof DefaultNumberSequence ) {
        return (NumberSequence) value;
      } else {
        // an empty sequence ...
        return new DefaultNumberSequence( context );
      }
    } else if ( type.isFlagSet( Type.ARRAY_TYPE ) ) {
      // array
      if ( value instanceof ArrayCallback ) {
        if ( strict ) {
          return new DefaultNumberSequence( (ArrayCallback) value, context );
        } else {
          return new AnyNumberSequence( (ArrayCallback) value, context );
        }
      } else {
        logger.warn( "Assertation failure: Type declared to be array, but no array callback found inside." );
        throw TypeConversionException.getInstance();
      }
    } else if ( type.isFlagSet( Type.SCALAR_TYPE ) || type.isFlagSet( Type.NUMERIC_TYPE ) ) {
      // else scalar
      return new DefaultNumberSequence(
        new StaticValue( convertToNumber( type, value, strict ), NumberType.GENERIC_NUMBER ), context );
    } else {
      return new DefaultNumberSequence( context );
    }
  }

  /**
   * Checks whether the target type would accept the specified value object and value type.<br/> This method is called
   * for auto conversion of function parameters using the conversion type declared by the function metadata.
   *
   * @param targetType
   * @param valuePair
   * @noinspection ObjectEquality is tested at the end of the method for performance reasons only. We just want to
   * detect whether a new object has been created or not.
   */
  public TypeValuePair convertTo( final Type targetType,
                                  final TypeValuePair valuePair ) throws EvaluationException {
    if ( targetType.isFlagSet( Type.ARRAY_TYPE ) ) {
      if ( valuePair.getType().isFlagSet( Type.ARRAY_TYPE ) ) {
        return valuePair;
      } else if ( targetType.isFlagSet( Type.SEQUENCE_TYPE ) ) {
        return convertTo( targetType, valuePair );
      } else {
        final Object o = valuePair.getValue();
        if ( o != null && o.getClass().isArray() ) {
          return new TypeValuePair( targetType, convertToArray( valuePair.getType(), o ) );
        } else {
          final Object retval = convertPlainToPlain( targetType, valuePair.getType(), valuePair.getValue() );
          return new TypeValuePair( targetType, new ArrayConverterCallback( retval, targetType ) );
        }
      }
    } else if ( targetType.isFlagSet( Type.SEQUENCE_TYPE ) ) {
      if ( valuePair.getType().isFlagSet( Type.ARRAY_TYPE ) ) {
        return convertToSequence( targetType, valuePair );
      } else if ( targetType.isFlagSet( Type.SEQUENCE_TYPE ) ) {
        return valuePair;
      } else {
        final Object retval = convertPlainToPlain( targetType, valuePair.getType(), valuePair.getValue() );
        final ArrayConverterCallback converterCallback = new ArrayConverterCallback( retval, targetType );
        return convertToSequence( targetType, new TypeValuePair( AnyType.ANY_ARRAY, converterCallback ) );
      }
    }

    // else scalar
    final Object value = valuePair.getValue();
    final Object o = convertPlainToPlain( targetType, valuePair.getType(), value );
    if ( value == o ) {
      return valuePair;
    }
    return new TypeValuePair( targetType, o );
  }

  private Object convertPlainToPlain( final Type targetType, final Type sourceType,
                                      final Object value ) throws EvaluationException {
    if ( targetType.isFlagSet( Type.NUMERIC_TYPE ) ) {
      if ( targetType.isFlagSet( Type.LOGICAL_TYPE ) ) {
        if ( sourceType.isFlagSet( Type.LOGICAL_TYPE ) ) {
          return value;
        }

        return convertToLogical( sourceType, value );
      }

      if ( value instanceof Date ) {
        if ( targetType.isFlagSet( Type.DATE_TYPE )
          || targetType.isFlagSet( Type.DATETIME_TYPE )
          || targetType.isFlagSet( Type.TIME_TYPE ) ) {
          final Date toJavaDate = (Date) value;
          return DateUtil.normalizeDate( toJavaDate, targetType, false );
        }
      }

      final Number serial = convertToNumber( sourceType, value );
      if ( targetType.isFlagSet( Type.DATE_TYPE )
        || targetType.isFlagSet( Type.DATETIME_TYPE )
        || targetType.isFlagSet( Type.TIME_TYPE ) ) {
        final BigDecimal fromAsBigDecimal = NumberUtil.getAsBigDecimal( serial );
        final BigDecimal normalizedSerial = DateUtil.normalizeDate( fromAsBigDecimal, targetType );
        final Date toJavaDate = HSSFDateUtil.getJavaDate( normalizedSerial );
        return DateUtil.normalizeDate( toJavaDate, targetType, false );
      }
      return serial;
    } else if ( targetType.isFlagSet( Type.TEXT_TYPE ) ) {
      return convertToText( sourceType, value );
    }

    // Unknown type - ignore it, crash later :)
    return value;
  }

  public Type guessTypeOfObject( final Object o ) {
    if ( o instanceof Number ) {
      return NumberType.GENERIC_NUMBER;
    } else if ( o instanceof Time ) {
      return DateTimeType.TIME_TYPE;
    } else if ( o instanceof java.sql.Date ) {
      return DateTimeType.DATE_TYPE;
    } else if ( o instanceof Date ) {
      return DateTimeType.DATETIME_TYPE;
    } else if ( o instanceof Boolean ) {
      return LogicalType.TYPE;
    } else if ( o instanceof String ) {
      return TextType.TYPE;
    }

    return AnyType.TYPE;
  }
}
