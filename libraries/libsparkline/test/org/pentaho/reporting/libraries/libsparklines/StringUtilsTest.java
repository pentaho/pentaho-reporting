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

package org.pentaho.reporting.libraries.libsparklines;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;
import java.math.BigDecimal;
import java.util.StringTokenizer;
import org.pentaho.reporting.libraries.libsparklines.util.StringUtils;

/**
 * StringUtils Tester.
 *
 * @author Cedric Pronzato
 */
public class StringUtilsTest extends TestCase
{
  private static final String INTEGERS = "10,58,-15";
  private static final Number[] NUMBERS = {new Integer(10), new Integer(58), new Integer(-15), new Double("2.56d"),
      new Integer(-89), new Float("45f"), new BigDecimal("10e+6")};
  private static final String DOUBLES = "10,58,-15,2.56 ,  -89";
  private static final String ALL = "10,58,-15,2.56,-89, 45., 10e+6";

  public StringUtilsTest(final String name)
  {
    super(name);
  }

  public void setUp() throws Exception
  {
    super.setUp();
  }

  public void tearDown() throws Exception
  {
    super.tearDown();
  }

  public static Test suite()
  {
    return new TestSuite(StringUtilsTest.class);
  }

  public void testToIntListSimple()
  {
    final Number[] numbers = StringUtils.toIntList(INTEGERS);
    assertNotNull("The list is null", numbers);
    assertEquals("Unexpected list size", 3, numbers.length);
    for (int i = 0; i < numbers.length; i++)
    {
      assertEquals("Unexpected number value", NUMBERS[i], numbers[i]);
    }
  }

  public void testToIntListDouble()
  {
    try
    {
      final Number[] numbers = StringUtils.toIntList(DOUBLES);
      fail("Should not be able to parse double");
    }
    catch (Exception e)
    {
      //nothing
    }
  }

  public void testToBigDecimalListDouble()
  {
    final Number[] numbers = StringUtils.toBigDecimalList(DOUBLES);
    assertNotNull("The list is null", numbers);
    assertEquals("Unexpected list size", 5, numbers.length);
    for (int i = 0; i < numbers.length; i++)
    {
      final BigDecimal ref = new BigDecimal(NUMBERS[i].toString());
      final BigDecimal value = new BigDecimal(numbers[i].toString());
      assertEquals("Unexpected number value", 0, ref.compareTo(value));
    }
  }

  public void testToBigDecimalListEveryTypes()
  {
    final Number[] numbers = StringUtils.toBigDecimalList(ALL);
    assertNotNull("The list is null", numbers);
    assertEquals("Unexpected list size", 7, numbers.length);
    for (int i = 0; i < numbers.length; i++)
    {
      final BigDecimal ref = new BigDecimal(NUMBERS[i].toString());
      final BigDecimal value = new BigDecimal(numbers[i].toString());
      assertEquals("Unexpected number value", 0, ref.compareTo(value));
    }
  }

  public static void testBigDecimal()
  {
    final BigDecimal bigDecimal = new BigDecimal("10e+6");
    assertNotNull(bigDecimal);
    final StringTokenizer stringTokenizer = new StringTokenizer("10, 10e+6 ", ",");
    assertEquals("Unexpected Tokenizer size", 2, stringTokenizer.countTokens());
    while (stringTokenizer.hasMoreTokens())
    {
      final String s = stringTokenizer.nextToken().trim();
      final BigDecimal bigDecimal2 = new BigDecimal(s);
      assertNotNull(bigDecimal2);
    }
  }
}
