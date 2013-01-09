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
 * Copyright (c) 2008 - 2009 Larry Ogrodnek, Pentaho Corporation and Contributors.  All rights reserved.
 */

package org.pentaho.reporting.libraries.libsparklines.util;

import java.util.StringTokenizer;
import java.math.BigDecimal;

/**
 * Some simple String utilities.
 *
 * @author Larry Ogrodnek <larry@cheesesteak.net>
 * @author Cedric Pronzato
 * @deprecated None of the methods is actually used in production code.
 */
public final class StringUtils
{
  private static final Number[] EMPTY_NUMBERS = new Number[0];

  /**
   * Utility class constructor prevents object creation.
   */
  private StringUtils()
  {
  }

  /**
   * Checks, whether a given string is null or empty.
   *
   * @param s string to be checked.
   * @return true, if the string is empty, false otherwise.
   */
  public static boolean isEmpty(final String s)
  {
    return (s == null || s.length() == 0);
  }

  /**
   * Converts the given string, which is assumed to be a comma separated list of numbers, into a array of numbers.
   *
   * @param s the string to be converted.
   * @return the array of numbers produced from the string.
   * @deprecated use #toBigDecimalList instead.
   */
  public static Number[] toIntList(final String s)
  {
    return toIntList(s, ",");
  }

  /**
   * Converts the given string into a array of numbers using the given separator as splitting argument.
   *
   * @param s   the string to be converted.
   * @param sep the separator, usually a comma.
   * @return the array of numbers produced from the string.
   * @deprecated use #toBigDecimalList instead.
   */
  public static Number[] toIntList(final String s, final String sep)
  {
    if (isEmpty(s))
    {
      return EMPTY_NUMBERS;
    }

    final StringTokenizer stringTokenizer = new StringTokenizer(s, sep);
    final Number[] ret = new Number[stringTokenizer.countTokens()];

    int i = 0;
    while (stringTokenizer.hasMoreTokens())
    {
      ret[i] = (new Integer(stringTokenizer.nextToken().trim()));
      i += 1;
    }

    return ret;
  }

  /**
   * Converts the given string, which is assumed to be a comma separated list of numbers, into a array of
   * <code>BigDecimal</code> numbers.<br/> Take care that <code>BigDecimal</code> string constructor do not support
   * inputs like "10f", "5d" ...
   *
   * @param s the string to be converted.
   * @return the array of numbers produced from the string.
   * @throws NumberFormatException if the string <code>s</code> does not contain valid numbers.
   */
  public static Number[] toBigDecimalList(final String s)
  {
    return toBigDecimalList(s, ",");
  }

  /**
   * Converts the given string into a array of <code>BigDecimal</code> numbers using the given separator as splitting
   * argument.<br/> Take care that <code>BigDecimal</code> string constructor do not support inputs like "10f", "5d"
   * ...
   *
   * @param s   the string to be converted.
   * @param sep the separator, usually a comma.
   * @return the array of numbers produced from the string.
   * @throws NumberFormatException if the string <code>s</code> does not contain valid numbers.
   */
  public static Number[] toBigDecimalList(final String s, final String sep)
  {
    if (isEmpty(s))
    {
      return EMPTY_NUMBERS;
    }

    final StringTokenizer stringTokenizer = new StringTokenizer(s, sep);
    final Number[] ret = new Number[stringTokenizer.countTokens()];

    int i = 0;
    while (stringTokenizer.hasMoreTokens())
    {
      final String val = stringTokenizer.nextToken().trim();
      ret[i] = new BigDecimal(val);
      i += 1;
    }

    return ret;
  }
}
