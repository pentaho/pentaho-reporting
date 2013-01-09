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

import java.awt.Color;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

/**
 * A helper class to map colors into names and vice versa.
 *
 * @author Thomas Morgner
 * @deprecated This class is not used anywhere and will be removed in the next cycle.
 */
public final class ColorUtilitiy
{
  //private static final HashMap knownColorNamesByColor;
  private static final HashMap knownColorsByName;

  static
  {
    //knownColorNamesByColor = new HashMap();
    knownColorsByName = new HashMap();
    try
    {
      final Field[] fields = Color.class.getFields();
      for (int i = 0; i < fields.length; i++)
      {
        final Field f = fields[i];
        if (Modifier.isPublic(f.getModifiers())
            && Modifier.isFinal(f.getModifiers())
            && Modifier.isStatic(f.getModifiers()))
        {
          final String name = f.getName();
          final Object oColor = f.get(null);
          if (oColor instanceof Color)
          {
            //knownColorNamesByColor.put(oColor, name.toLowerCase());
            knownColorsByName.put(name.toLowerCase(), oColor);
          }
        }
      }
    }
    catch (Exception e)
    {
      // ignore ..
    }
  }

  /**
   * Utility class constructor prevents object creation.
   */
  private ColorUtilitiy()
  {
  }

  /**
   * Parse a String into a Color.
   * <p/>
   * This method will accept either a color name (a field name from {@link Color},
   * case insensitive e.g. "red"), or a HTML hex color string (e.g. "#ff0000"
   * for Color.RED).
   *
   * @param value String to parse for color name or color number.
   * @return Color for s.
   */
  private static Color parseColor(final String value)
  {
    if (value == null)
    {
      return null;
    }

    final Object o = knownColorsByName.get(value.toLowerCase());
    if (o != null)
    {
      return (Color) o;
    }

    try
    {
      // get color by hex or octal value
      return Color.decode(value.trim());
    }
    catch (final NumberFormatException nfe)
    {
      return null;
    }
  }

  /**
   * Parse a String into a Color, and returns the given default value if the color is not parsable.
   * <p/>
   * This method will accept either a color name (a field name from {@link Color},
   * case insensitive e.g. "red"), or a HTML hex color string (e.g. "#ff0000"
   * for Color.RED).
   *
   * @param colorText String to parse for color name or color number.
   * @param defValue the default value that should be returned if the string is not parseable or null.
   * @return Color for the text.
   */
  public static Color convertColor(final String colorText,
                                   final Color defValue)
  {
    if (colorText == null || colorText.length() == 0)
    {
      return defValue;
    }

    final Color retval = parseColor(colorText);
    if (retval == null)
    {
      return defValue;
    }
    return retval;
  }
}
