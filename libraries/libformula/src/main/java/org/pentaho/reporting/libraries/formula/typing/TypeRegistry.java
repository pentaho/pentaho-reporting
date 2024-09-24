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
* Copyright (c) 2006 - 2019 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.formula.typing;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;

import java.util.Date;

/**
 * The type registry manages the known value types.
 *
 * @author Thomas Morgner
 */
public interface TypeRegistry {
  /**
   * Returns an comparator for the given types.
   *
   * @param type1
   * @param type2
   * @return
   */
  public ExtendedComparator getComparator( Type type1, Type type2 );

  /**
   * Converts the object of the given type into a number. If the object is not convertible, a NumberFormatException is
   * thrown. (This conversion is used by the operator implementations.)
   *
   * @param type1
   * @param value
   * @return the value as number or ZERO if the value is unconvertible.
   * @throws TypeConversionException if the type cannot be represented as number.
   */
  public Number convertToNumber( Type type1, Object value )
    throws EvaluationException;

  /**
   * Converts the object of the given type into a number. If the object is not convertible, a NumberFormatException is
   * thrown. (This conversion is used by the operator implementations.)
   *
   * @param type1
   * @param value
   * @param strictTypeChecks
   * @return the value as number or ZERO if the value is unconvertible.
   * @throws TypeConversionException if the type cannot be represented as number.
   */
  public Number convertToNumber( Type type1, Object value, final boolean strictTypeChecks )
    throws EvaluationException;

  /**
   * (This conversion is used by the operator implementations.)
   *
   * @param type1
   * @param value
   * @return the value as string or an empty string, if the value given is null.
   * @throws TypeConversionException
   */
  public String convertToText( Type type1, Object value ) throws EvaluationException;

  /**
   * Converts the object of the given type into a boolean.
   *
   * @param type1
   * @param value
   * @return The value as Boolean or null.
   */
  public Boolean convertToLogical( Type type1, Object value ) throws EvaluationException;

  /**
   * Converts the object of the given type into a date.
   *
   * @param type1
   * @param value
   * @return The value as Date or null.
   */
  public Date convertToDate( Type type1, Object value ) throws EvaluationException;

  /**
   * Converts the given (type,value) pair into a numeric sequence. If the flag "strictTypeChecks" is set to true, the
   * value sequence will only evaluate numeric values. A non-strict sequence will treat text as zero and logical values
   * as 0 or 1.
   *
   * @param type
   * @param value
   * @param strictTypeChecks
   * @return
   * @throws TypeConversionException
   */
  public NumberSequence convertToNumberSequence( final Type type, final Object value, final boolean strictTypeChecks )
    throws EvaluationException;

  public Sequence convertToSequence( final Type type, final Object value )
    throws EvaluationException;

  public ArrayCallback convertToArray( final Type type, final Object value )
    throws EvaluationException;

  /**
   * Checks whether the target type would accept the specified value object and value type. (This conversion is used by
   * the functions.)
   *
   * @param targetType
   * @param valuePair
   */
  public TypeValuePair convertTo( final Type targetType,
                                  final TypeValuePair valuePair ) throws EvaluationException;

  public Type guessTypeOfObject( Object o );
}
