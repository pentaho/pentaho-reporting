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

package org.pentaho.reporting.engine.classic.core.filter;

import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.util.Locale;
import java.util.TimeZone;

/**
 * A filter that formats values from a data source to a string representation.
 * <p/>
 * This filter will format objects using a {@link java.text.MessageFormat} to create the string representation for the
 * number obtained from the datasource.
 *
 * @author Joerg Schaible
 * @author Thomas Morgner
 * @see java.text.MessageFormat
 */
public class MessageFormatFilter implements DataSource {

  /**
   * The message format support translates raw message strings into useable MessageFormat parameters and read the
   * necessary input data from the datarow.
   */
  private MessageFormatSupport messageFormatSupport;
  /**
   * The current locale.
   */
  private transient Locale locale;
  private transient TimeZone timeZone;

  /**
   * Default constructor.
   * <P>
   * Uses a general number format for the current locale.
   */
  public MessageFormatFilter() {
    messageFormatSupport = new MessageFormatSupport();
  }

  /**
   * Defines the format string for the {@link java.text.MessageFormat} object used in this implementation.
   *
   * @param format
   *          the message format.
   */
  public void setFormatString( final String format ) {
    messageFormatSupport.setFormatString( format );
  }

  /**
   * Returns the format string used in the message format.
   *
   * @return the format string.
   */
  public String getFormatString() {
    return messageFormatSupport.getFormatString();
  }

  /**
   * Returns the formatted string. The value is read using the data source given and formated using the formatter of
   * this object. The formating is guaranteed to completly form the object to an string or to return the defined
   * NullValue.
   * <p/>
   * If format, datasource or object are null, the NullValue is returned.
   *
   * @param runtime
   *          the expression runtime that is used to evaluate formulas and expressions when computing the value of this
   *          filter.
   * @param element
   * @return The formatted value.
   */
  public Object getValue( final ExpressionRuntime runtime, final ReportElement element ) {
    if ( runtime == null ) {
      return null;
    }

    final ResourceBundleFactory resourceBundleFactory = runtime.getResourceBundleFactory();
    final Locale newLocale = resourceBundleFactory.getLocale();
    if ( ObjectUtilities.equal( newLocale, locale ) == false ) {
      messageFormatSupport.setLocale( resourceBundleFactory.getLocale() );
      locale = newLocale;
    }
    final TimeZone newTimeZone = resourceBundleFactory.getTimeZone();
    if ( ObjectUtilities.equal( newTimeZone, timeZone ) == false ) {
      messageFormatSupport.setTimeZone( newTimeZone );
      timeZone = newTimeZone;
    }
    return messageFormatSupport.performFormat( runtime.getDataRow() );
  }

  /**
   * Clones this <code>DataSource</code>.
   *
   * @return the clone.
   * @throws CloneNotSupportedException
   *           this should never happen.
   */
  public MessageFormatFilter clone() throws CloneNotSupportedException {
    final MessageFormatFilter mf = (MessageFormatFilter) super.clone();
    mf.messageFormatSupport = (MessageFormatSupport) messageFormatSupport.clone();
    return mf;
  }

  /**
   * Returns the replacement text if one of the referenced fields in the message is null.
   *
   * @return the replacement string for null-values.
   */
  public String getNullString() {
    return messageFormatSupport.getNullString();
  }

  /**
   * Defines the replacement text that is used, if one of the referenced fields in the message is null.
   *
   * @param nullString
   *          the replacement string for null-values.
   */
  public void setNullString( final String nullString ) {
    this.messageFormatSupport.setNullString( nullString );
  }
}
