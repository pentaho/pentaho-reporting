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
* Copyright (c) 2008 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.formatting;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Properties;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class DumpPatterns
{
  public static void main(String[] args) throws IOException
  {
    final Locale[] availableLocales = Locale.getAvailableLocales();
    for (int i = 0; i < availableLocales.length; i++)
    {
      final Locale locale = availableLocales[i];

      final Properties writer = new Properties();
      printDate (writer, DateFormat.SHORT, locale);
      printDate (writer, DateFormat.MEDIUM, locale);
      printDate (writer, DateFormat.LONG, locale);
      printDate (writer, DateFormat.FULL, locale);

      printTime (writer, DateFormat.SHORT, locale);
      printTime (writer, DateFormat.MEDIUM, locale);
      printTime (writer, DateFormat.LONG, locale);
      printTime (writer, DateFormat.FULL, locale);

      printCurrency(writer, locale);
      printInteger(writer, locale);
      printPercentage(writer, locale);
      printNumber(writer, locale);

      final FileOutputStream fout = new FileOutputStream
          ("/tmp/format-patterns" + createSuffix(locale) + ".properties");
      try
      {
        writer.store(fout, "");
      }
      finally {
        fout.close();
      }
    }

  }

  private static String createSuffix(final Locale locale)
  {
    final String cntry = locale.getCountry();
    final String lang = locale.getLanguage();
    if (cntry.length() == 0)
    {
      return "_" + lang.toLowerCase();
    }

    return "_" + lang.toLowerCase() + "_" + cntry.toUpperCase();
  }

  private static String typeToString(int type)
  {
    switch (type)
    {
      case DateFormat.SHORT: return "short";
      case DateFormat.MEDIUM: return "medium";
      case DateFormat.LONG: return "long";
      case DateFormat.FULL: return "full";
      default:
        throw new NullPointerException();
    }
  }

  private static void printCurrency(final Properties writer, Locale locale)
  {
    DecimalFormat sdf = (DecimalFormat) NumberFormat.getCurrencyInstance(locale);
    writer.put ("format.currency" , sdf.toPattern());
  }

  private static void printInteger(final Properties writer, Locale locale)
  {
    DecimalFormat sdf = (DecimalFormat) NumberFormat.getIntegerInstance(locale);
    writer.put ("format.integer" , sdf.toPattern());
  }

  private static void printPercentage(final Properties writer, Locale locale)
  {
    DecimalFormat sdf = (DecimalFormat) NumberFormat.getPercentInstance(locale);
    writer.put ("format.percentage", sdf.toPattern());
  }

  private static void printNumber(final Properties writer, Locale locale)
  {
    DecimalFormat sdf = (DecimalFormat) NumberFormat.getInstance(locale);
    writer.put ("format.number", sdf.toPattern());
  }

  private static void printDate(final Properties writer, int type, Locale locale)
  {
    SimpleDateFormat sdf = (SimpleDateFormat) DateFormat.getDateInstance(type, locale);

    writer.put ("format.date." + typeToString(type) ,sdf.toPattern());
  }

  private static void printTime(final Properties writer, int type, Locale locale)
  {
    SimpleDateFormat sdf = (SimpleDateFormat) DateFormat.getTimeInstance(type, locale);
    writer.put ("format.time." + typeToString(type) , sdf.toPattern());
  }
}
