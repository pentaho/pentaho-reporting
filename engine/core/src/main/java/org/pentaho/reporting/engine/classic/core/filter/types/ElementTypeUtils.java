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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.filter.types;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.typing.ArrayCallback;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;

import java.math.BigDecimal;
import java.sql.Clob;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class ElementTypeUtils {
  private static final Number[] EMPTY_NUMBERS = new Number[0];

  private ElementTypeUtils() {
  }

  public static Object queryFieldName( final ReportElement element ) {
    if ( element == null ) {
      throw new NullPointerException( "Element must never be null." );
    }

    final Object attribute = element.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.FIELD );
    if ( attribute != null ) {
      return attribute;
    }
    return null;
  }

  public static Object queryStaticValue( final ReportElement element ) {
    if ( element == null ) {
      throw new NullPointerException( "Element must never be null." );
    }

    final Object attribute = element.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE );
    if ( attribute != null ) {
      return attribute;
    }
    return null;
  }

  public static Object queryFieldOrValue( final ExpressionRuntime runtime, final ReportElement element ) {
    if ( runtime == null ) {
      throw new NullPointerException( "Runtime must never be null." );
    }
    if ( element == null ) {
      throw new NullPointerException( "Element must never be null." );
    }

    // This has been possibly computed by the system using a formula or other attribute-expression.
    final Object value = element.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE );
    if ( value != null ) {
      return value;
    }
    final Object field = element.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.FIELD );
    if ( field != null ) {
      return runtime.getDataRow().get( String.valueOf( field ) );
    }
    return null;
  }

  public static String queryResourceId( final ExpressionRuntime runtime, final ReportElement element ) {
    if ( runtime == null ) {
      throw new NullPointerException( "Runtime must never be null." );
    }
    if ( element == null ) {
      throw new NullPointerException( "Element must never be null." );
    }

    final Object resourceId =
        element.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.RESOURCE_IDENTIFIER );
    if ( resourceId != null ) {
      return String.valueOf( resourceId );
    }

    return runtime.getConfiguration().getConfigProperty( ResourceBundleFactory.DEFAULT_RESOURCE_BUNDLE_CONFIG_KEY );
  }

  public static String toString( final Object object ) {
    if ( object == null ) {
      return null;
    }
    if ( object instanceof String ) {
      return (String) object;
    }
    if ( object instanceof Clob ) {
      final Clob clob = (Clob) object;
      try {
        return IOUtils.getInstance().readClob( clob );
      } catch ( Exception e ) {
        return null;
      }
    }
    if ( object.getClass().isArray() ) {
      if ( object instanceof char[] ) {
        return new String( (char[]) object );
      }
      if ( object instanceof Object[] ) {
        final StringBuilder b = new StringBuilder();
        final Object[] array = (Object[]) object;
        for ( int i = 0; i < array.length; i++ ) {
          if ( i != 0 ) {
            b.append( ", " );
          }
          b.append( toString( array[i] ) );
        }
        return b.toString();
      }
      if ( object instanceof byte[] ) {
        final StringBuilder b = new StringBuilder();
        final byte[] array = (byte[]) object;
        for ( int i = 0; i < array.length; i++ ) {
          if ( i != 0 ) {
            b.append( ", " );
          }
          b.append( array[i] );
        }
        return b.toString();
      }
      if ( object instanceof short[] ) {
        final StringBuilder b = new StringBuilder();
        final short[] array = (short[]) object;
        for ( int i = 0; i < array.length; i++ ) {
          if ( i != 0 ) {
            b.append( ", " );
          }
          b.append( array[i] );
        }
        return b.toString();
      }
      if ( object instanceof boolean[] ) {
        final StringBuilder b = new StringBuilder();
        final boolean[] array = (boolean[]) object;
        for ( int i = 0; i < array.length; i++ ) {
          if ( i != 0 ) {
            b.append( ", " );
          }
          b.append( array[i] );
        }
        return b.toString();
      }
      if ( object instanceof int[] ) {
        final StringBuilder b = new StringBuilder();
        final int[] array = (int[]) object;
        for ( int i = 0; i < array.length; i++ ) {
          if ( i != 0 ) {
            b.append( ", " );
          }
          b.append( array[i] );
        }
        return b.toString();
      }
      if ( object instanceof long[] ) {
        final StringBuilder b = new StringBuilder();
        final long[] array = (long[]) object;
        for ( int i = 0; i < array.length; i++ ) {
          if ( i != 0 ) {
            b.append( ", " );
          }
          b.append( array[i] );
        }
        return b.toString();
      }
      if ( object instanceof float[] ) {
        final StringBuilder b = new StringBuilder();
        final float[] array = (float[]) object;
        for ( int i = 0; i < array.length; i++ ) {
          if ( i != 0 ) {
            b.append( ", " );
          }
          b.append( array[i] );
        }
        return b.toString();
      }
      if ( object instanceof double[] ) {
        final StringBuilder b = new StringBuilder();
        final double[] array = (double[]) object;
        for ( int i = 0; i < array.length; i++ ) {
          if ( i != 0 ) {
            b.append( ", " );
          }
          b.append( array[i] );
        }
        return b.toString();
      }
    }
    return String.valueOf( object );
  }

  public static Number getNumberAttribute( final ReportElement e, final String namespace, final String name,
      final Number defaultValue ) {
    final Object val = e.getAttribute( namespace, name );
    if ( val == null ) {
      return defaultValue;
    }
    if ( val instanceof Number ) {
      return (Number) val;
    }
    return defaultValue;
  }

  public static int getIntAttribute( final ReportElement e, final String namespace, final String name,
      final int defaultValue ) {
    final Object val = e.getAttribute( namespace, name );
    if ( val == null ) {
      return defaultValue;
    }
    if ( val instanceof Number ) {
      final Number nval = (Number) val;
      return nval.intValue();
    }
    return ParserUtil.parseInt( String.valueOf( val ), defaultValue );
  }

  public static String getStringAttribute( final ReportElement e, final String namespace, final String name,
      final String defaultValue ) {
    final Object val = e.getAttribute( namespace, name );
    if ( val == null ) {
      return defaultValue;
    }
    return String.valueOf( val );
  }

  public static boolean getBooleanAttribute( final ReportElement e, final String namespace, final String name,
      final boolean defaultValue ) {
    final Object val = e.getAttribute( namespace, name );
    if ( val == null ) {
      return defaultValue;
    }
    if ( val instanceof Boolean ) {
      final Boolean nval = (Boolean) val;
      return nval.booleanValue();
    }
    return ParserUtil.parseBoolean( String.valueOf( val ), defaultValue );
  }

  public static Number[] getData( final Object o ) {
    final ArrayList<Number> numbers = new ArrayList<Number>();
    try {
      if ( o instanceof ArrayCallback ) {
        final ArrayCallback acb = (ArrayCallback) o;
        final int rowCount = acb.getRowCount();
        final int colCount = acb.getColumnCount();
        for ( int row = 0; row < rowCount; row++ ) {
          for ( int column = 0; column < colCount; column++ ) {
            numbers.add( (Number) acb.getValue( row, column ) );
          }
        }
        return numbers.toArray( new Number[numbers.size()] );
      }

      if ( o instanceof List ) {
        final List<?> l = (List<?>) o;
        for ( int i = 0; i < l.size(); i++ ) {
          final Object value = l.get( i );
          if ( value instanceof Number ) {
            numbers.add( (Number) value );
          } else if ( value instanceof String ) {
            numbers.add( new BigDecimal( (String) value ) );
          }
        }
        return numbers.toArray( new Number[numbers.size()] );
      }
      if ( o instanceof Object[] ) {
        final Object[] l = (Object[]) o;
        arrayToList( numbers, l );
        return numbers.toArray( new Number[numbers.size()] );
      }
      if ( o instanceof String ) {
        return toBigDecimalList( (String) o, "," );
      }
      if ( o instanceof Number ) {
        numbers.add( (Number) o );
        return numbers.toArray( new Number[numbers.size()] );
      }
    } catch ( final NumberFormatException nfe ) {
      // fall through...
    } catch ( EvaluationException e ) {
      // ignore ..
    }
    return null;
  }

  private static void arrayToList( final ArrayList<Number> numbers, final Object[] l ) {
    for ( int i = 0; i < l.length; i++ ) {
      final Object value = l[i];
      if ( value instanceof Number ) {
        numbers.add( (Number) value );
      } else if ( value instanceof String ) {
        numbers.add( new BigDecimal( (String) value ) );
      } else if ( value instanceof Object[] ) {
        final Object[] innerArray = (Object[]) value;
        arrayToList( numbers, innerArray );
      }
    }
  }

  /**
   * Converts the given string into a array of <code>BigDecimal</code> numbers using the given separator as splitting
   * argument.<br/>
   * Take care that <code>BigDecimal</code> string constructor do not support inputs like "10f", "5d" ...
   *
   * @param s
   *          the string to be converted.
   * @param sep
   *          the separator, usually a comma.
   * @return the array of numbers produced from the string.
   */
  private static Number[] toBigDecimalList( final String s, final String sep ) {
    if ( StringUtils.isEmpty( s ) ) {
      return EMPTY_NUMBERS;
    }

    final StringTokenizer stringTokenizer = new StringTokenizer( s, sep );
    final Number[] ret = new Number[stringTokenizer.countTokens()];

    int i = 0;
    while ( stringTokenizer.hasMoreTokens() ) {
      final String val = stringTokenizer.nextToken().trim();
      ret[i] = new BigDecimal( val );
      i += 1;
    }

    return ret;
  }

}
