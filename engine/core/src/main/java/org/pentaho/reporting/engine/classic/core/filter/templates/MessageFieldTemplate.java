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

package org.pentaho.reporting.engine.classic.core.filter.templates;

import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.filter.MessageFormatFilter;
import org.pentaho.reporting.engine.classic.core.filter.StringFilter;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;

/**
 * The message field template simplifies the on-the-fly creation of strings.
 *
 * @author Thomas Morgner
 * @see org.pentaho.reporting.engine.classic.core.filter.MessageFormatSupport
 */
public class MessageFieldTemplate extends AbstractTemplate {
  /**
   * A string filter.
   */
  private StringFilter stringFilter;

  /**
   * The message format filter inlines data from other sources into a string.
   */
  private MessageFormatFilter messageFormatFilter;

  /**
   * Creates a new string field template.
   */
  public MessageFieldTemplate() {
    messageFormatFilter = new MessageFormatFilter();
    stringFilter = new StringFilter();
    stringFilter.setDataSource( messageFormatFilter );
  }

  /**
   * Returns the format string used in the message format filter. This is a raw value which contains untranslated
   * references to column names. It cannot be used directly in java.text.MessageFormat objects.
   *
   * @return the format string.
   */
  public String getFormat() {
    return messageFormatFilter.getFormatString();
  }

  /**
   * Redefines the format string for the message format. The assigned message format string must be given as raw value,
   * where column references are given in the format $(COLNAME).
   *
   * @param format
   *          the new format string.
   */
  public void setFormat( final String format ) {
    this.messageFormatFilter.setFormatString( format );
  }

  /**
   * Returns the value displayed by the field when the data source value is <code>null</code>.
   *
   * @return A value to represent <code>null</code>.
   */
  public String getNullValue() {
    return stringFilter.getNullValue();
  }

  /**
   * Sets the value displayed by the field when the data source value is <code>null</code>.
   *
   * @param nullValue
   *          the value that represents <code>null</code>.
   */
  public void setNullValue( final String nullValue ) {
    messageFormatFilter.setNullString( nullValue );
    stringFilter.setNullValue( nullValue );
  }

  /**
   * Returns the current value for the data source.
   *
   * @param runtime
   *          the expression runtime that is used to evaluate formulas and expressions when computing the value of this
   *          filter.
   * @param element
   * @return the value.
   */
  public Object getValue( final ExpressionRuntime runtime, final ReportElement element ) {
    return stringFilter.getValue( runtime, element );
  }

  /**
   * Clones the template.
   *
   * @return the clone.
   * @throws CloneNotSupportedException
   *           this should never happen.
   */
  public MessageFieldTemplate clone() throws CloneNotSupportedException {
    final MessageFieldTemplate template = (MessageFieldTemplate) super.clone();
    template.stringFilter = stringFilter.clone();
    template.messageFormatFilter = (MessageFormatFilter) template.stringFilter.getDataSource();
    return template;
  }

}
