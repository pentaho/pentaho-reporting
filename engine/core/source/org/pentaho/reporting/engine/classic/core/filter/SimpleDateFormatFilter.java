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
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.core.filter;

import java.text.DateFormatSymbols;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;

/**
 * A filter that creates string from dates. This filter will format java.util. Date objects using a
 * java.text.SimpleDateFormat to create the string representation for the date obtained from the datasource.
 * <p/>
 * If the object read from the datasource is no date, the NullValue defined by setNullValue(Object) is returned.
 * <p/>
 * This implementation uses a SimpleDateFormat and grants more control over the parsing results.
 *
 * @author Thomas Morgner
 * @see java.text.SimpleDateFormat
 */
public class SimpleDateFormatFilter extends DateFormatFilter
{
  /**
   * The last locale used to convert numbers.
   */
  private Locale lastLocale;
  /**
   * A flag indicating whether this filter should try to detect locales changes.
   */
  private boolean keepState;

  /**
   * DefaultConstructor.
   */
  public SimpleDateFormatFilter()
  {
    setFormatter(new SimpleDateFormat());
  }

  /**
   * Returns the SimpleDateFormat object used in this filter.
   *
   * @return The date format object.
   */
  public SimpleDateFormat getSimpleDateFormat()
  {
    return (SimpleDateFormat) getFormatter();
  }

  /**
   * Sets the date format for the filter.
   *
   * @param format The format.
   * @throws NullPointerException if the format given is null
   */
  public void setSimpleDateFormat(final SimpleDateFormat format)
  {
    super.setFormatter(format);
  }

  /**
   * Sets the date format for the filter. This narrows the allows formats down to SimpleDateFormat.
   *
   * @param format The format.
   * @throws NullPointerException if the format given is null
   * @throws ClassCastException   if the format given is no DateFormat
   */
  public void setFormatter(final Format format)
  {
    final SimpleDateFormat sdfmt = (SimpleDateFormat) format;
    super.setFormatter(sdfmt);
  }

  /**
   * Returns the formatString for this SimpleDateFormat. For a more detailed explaination of SimpleDateFormat
   * formatstrings see java.text.SimpleDateFormat.
   *
   * @return the format string for the used DateFormat.
   * @see java.text.SimpleDateFormat
   */
  public String getFormatString()
  {
    return getSimpleDateFormat().toPattern();
  }

  /**
   * defines the formatString for this SimpleDateFormat.
   *
   * @param format the formatString
   * @throws IllegalArgumentException if the string is invalid
   */
  public void setFormatString(final String format)
  {
    if (format == null)
    {
      throw new NullPointerException();
    }
    getSimpleDateFormat().applyPattern(format);
    invalidateCache();
  }

  /**
   * Returns a localized formatString for this SimpleDateFormat. For a more detailed explaination of SimpleDateFormat
   * formatstrings see java.text.SimpleDateFormat.
   *
   * @return the localized format string.
   * @see java.text.SimpleDateFormat
   */
  public String getLocalizedFormatString()
  {
    return getSimpleDateFormat().toLocalizedPattern();
  }


  /**
   * defines the localized formatString for this SimpleDateFormat.
   *
   * @param format the formatString
   * @throws IllegalArgumentException if the string is invalid
   */
  public void setLocalizedFormatString(final String format)
  {
    getSimpleDateFormat().applyLocalizedPattern(format);
    invalidateCache();
  }

  /**
   * Defines, whether the filter should keep its state, if a locale change is detected. This will effectivly disable the
   * locale update.
   *
   * @return true, if the locale should not update the DateSymbols, false otherwise.
   */
  public boolean isKeepState()
  {
    return keepState;
  }

  /**
   * Defines, whether the filter should keep its state, if a locale change is detected. This will effectivly disable the
   * locale update.
   *
   * @param keepState set to true, if the locale should not update the DateSymbols, false otherwise.
   */
  public void setKeepState(final boolean keepState)
  {
    this.keepState = keepState;
  }

  /**
   * Returns the formatted string. The value is read using the data source given and formated using the formatter of
   * this object. The formating is guaranteed to completly form the object to an string or to return the defined
   * NullValue.
   * <p/>
   * If format, datasource or object are null, the NullValue is returned.
   *
   * @param runtime the expression runtime that is used to evaluate formulas and expressions when computing the value of
   *                this filter.
   * @param element
   * @return The formatted value.
   */
  public Object getValue(final ExpressionRuntime runtime, final ReportElement element)
  {
    if (keepState == false && runtime != null)
    {
      final Locale locale = runtime.getResourceBundleFactory().getLocale();
      if (locale != null && locale.equals(lastLocale) == false)
      {
        lastLocale = locale;
        getSimpleDateFormat().setDateFormatSymbols(new DateFormatSymbols(locale));
        invalidateCache();
      }
    }
    return super.getValue(runtime, element);
  }


  public FormatSpecification getFormatString(final ExpressionRuntime runtime,
                                             final ReportElement element,
                                             FormatSpecification formatSpecification)
  {
    if (formatSpecification == null)
    {
      formatSpecification = new FormatSpecification();
    }
    formatSpecification.redefine(FormatSpecification.TYPE_DATE_FORMAT, getFormatString());
    return formatSpecification;
  }

}
