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
 * Copyright (c) 2001 - 2009 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.library.parameter.values;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;


public final class ConverterRegistry
{
  private static ConverterRegistry instance;
  private HashMap<Class,ValueConverter> registeredClasses;

  public static synchronized ConverterRegistry getInstance()
  {
    if (instance == null)
    {
      instance = new ConverterRegistry();
    }
    return instance;
  }

  private ConverterRegistry()
  {
    registeredClasses = new HashMap<Class,ValueConverter>();
    registeredClasses.put(BigDecimal.class, new BigDecimalValueConverter());
    registeredClasses.put(BigInteger.class, new BigIntegerValueConverter());
    registeredClasses.put(Boolean.class, new BooleanValueConverter());
    registeredClasses.put(Boolean.TYPE, new BooleanValueConverter());
    registeredClasses.put(Byte.class, new ByteValueConverter());
    registeredClasses.put(Byte.TYPE, new ByteValueConverter());
    registeredClasses.put(Character.class, new CharacterValueConverter());
    registeredClasses.put(Character.TYPE, new CharacterValueConverter());
    registeredClasses.put(Color.class, new ColorValueConverter());
    registeredClasses.put(Double.class, new DoubleValueConverter());
    registeredClasses.put(Double.TYPE, new DoubleValueConverter());
    registeredClasses.put(Float.class, new FloatValueConverter());
    registeredClasses.put(Float.TYPE, new FloatValueConverter());
    registeredClasses.put(Integer.class, new IntegerValueConverter());
    registeredClasses.put(Integer.TYPE, new IntegerValueConverter());
    registeredClasses.put(Long.class, new LongValueConverter());
    registeredClasses.put(Long.TYPE, new LongValueConverter());
    registeredClasses.put(Short.class, new ShortValueConverter());
    registeredClasses.put(Short.TYPE, new ShortValueConverter());
    registeredClasses.put(String.class, new StringValueConverter());
    registeredClasses.put(Number.class, new BigDecimalValueConverter());
    registeredClasses.put(Locale.class, new LocaleValueConverter());
    registeredClasses.put(TimeZone.class, new TimeZoneValueConverter());
    registeredClasses.put(Date.class, new DateValueConverter());
    registeredClasses.put(java.sql.Date.class, new SQLDateValueConverter());
    registeredClasses.put(Time.class, new SQLTimeValueConverter());
    registeredClasses.put(Timestamp.class, new SQLTimestampValueConverter());
  }

  public ValueConverter getValueConverter(final Class c)
  {
    final ValueConverter plain = registeredClasses.get(c);
    if (plain != null)
    {
      return plain;
    }
    if (c.isArray())
    {
      final Class componentType = c.getComponentType();
      final ValueConverter componentConverter = getValueConverter(componentType);
      if (componentConverter != null)
      {
        return new ArrayValueConverter(componentType, componentConverter);
      }
    }
    if (c.isEnum())
    {
      return new EnumValueConverter(c);
    }
    return null;
  }

  /**
   * Converts an object to an attribute value.
   *
   * @param o the object.
   * @return the attribute value.
   * @throws ValueConversionException if there was an error during the conversion.
   */
  public static String toAttributeValue(final Object o) throws ValueConversionException
  {
    if (o == null)
    {
      return null;
    }
    final ValueConverter vc =
        ConverterRegistry.getInstance().getValueConverter(o.getClass());
    if (vc == null)
    {
      return null;
    }
    return vc.toAttributeValue(o);
  }

  /**
   * Converts a string to a property value.
   *
   * @param s the string.
   * @param c the target class.
   * @return the object converted from the given string into a object of the target class.
   * @throws ValueConversionException if there was an error during the conversion.
   */
  public static Object toPropertyValue(final String s, final Class c) throws ValueConversionException
  {
    if (s == null)
    {
      return null;
    }
    if (c == null)
    {
      return null;
    }
    final ValueConverter vc = ConverterRegistry.getInstance().getValueConverter(c);
    if (vc == null)
    {
      return null;
    }
    return vc.toPropertyValue(s);
  }

  public static Object convert(final Object o, final Class c, final Object defaultValue)
  {
    if (o == null)
    {
      return defaultValue;
    }
    if (c.isInstance(o))
    {
      return o;
    }
    try
    {
      final String s = String.valueOf(o);
      return toPropertyValue(s, c);
    }
    catch (ValueConversionException e)
    {
      return defaultValue;
    }
  }
}
