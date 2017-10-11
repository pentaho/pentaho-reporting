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

package org.pentaho.reporting.engine.classic.core.function.strings;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.filter.MessageFormatSupport;
import org.pentaho.reporting.engine.classic.core.function.AbstractExpression;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.libraries.base.util.URLEncoder;

import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * Formats a message using named parameters. The parameters are resolved against the current data-row.
 * <p/>
 * This performs the same task as the MessageFormatFilter does inside a text-element.
 *
 * @author Thomas Morgner
 */
public class MessageFormatExpression extends AbstractExpression {
  private static final Log logger = LogFactory.getLog( MessageFormatExpression.class );

  /**
   * A internal data-row wrapper that URL-encodes all values returned by the data-row.
   */
  private static class EncodeDataRow implements DataRow {
    /**
     * The wrappedDataRow datarow.
     */
    private DataRow wrappedDataRow;
    /**
     * The character encoding used for the URL-encoding.
     */
    private String encoding;

    /**
     * Default Constructor.
     */
    protected EncodeDataRow() {
    }

    /**
     * Returns the wrapped data-row.
     *
     * @return the wrapped data-row.
     */
    public DataRow getWrappedDataRow() {
      return wrappedDataRow;
    }

    /**
     * Defines the wrapped data-row.
     *
     * @param wrappedDataRow
     *          the wrapped datarow.
     */
    public void setWrappedDataRow( final DataRow wrappedDataRow ) {
      this.wrappedDataRow = wrappedDataRow;
    }

    /**
     * Returns the String-encoding used for the URL encoding.
     *
     * @return the string-encoding.
     */
    public String getEncoding() {
      return encoding;
    }

    /**
     * Defines the String-encoding used for the URL encoding.
     *
     * @param encoding
     *          the string-encoding.
     */
    public void setEncoding( final String encoding ) {
      if ( encoding == null ) {
        throw new NullPointerException();
      }
      this.encoding = encoding;
    }

    /**
     * Encodes the given value. The encoding process is skipped, if the value is null, is a number or is a date.
     *
     * @param fieldValue
     *          the value that should be encoded.
     * @return the encoded value.
     */
    private Object encode( final Object fieldValue ) {
      if ( fieldValue == null ) {
        return null;
      }
      if ( fieldValue instanceof Date ) {
        return fieldValue;
      } else if ( fieldValue instanceof Number ) {
        return fieldValue;
      }
      try {
        return URLEncoder.encode( String.valueOf( fieldValue ), encoding );
      } catch ( UnsupportedEncodingException e ) {
        MessageFormatExpression.logger.debug( "Unsupported Encoding: " + encoding );
        return null;
      }
    }

    /**
     * Returns the value of the function, expression or column using its specific name. The given name is translated
     * into a valid column number and the the column is queried. For functions and expressions, the
     * <code>getValue()</code> method is called and for columns from the tablemodel the tablemodel method
     * <code>getValueAt(row, column)</code> gets called.
     *
     * @param col
     *          the item index.
     * @return the value.
     * @throws IllegalStateException
     *           if the datarow detected a deadlock.
     */
    public Object get( final String col ) throws IllegalStateException {
      return encode( wrappedDataRow.get( col ) );
    }

    /**
     * Checks whether the value contained in the column has changed since the last advance-operation.
     *
     * @param name
     *          the name of the column.
     * @return true, if the value has changed, false otherwise.
     */
    public boolean isChanged( final String name ) {
      return wrappedDataRow.isChanged( name );
    }

    public String[] getColumnNames() {
      return wrappedDataRow.getColumnNames();
    }
  }

  /**
   * The message-format pattern used to compute the result.
   */
  private String pattern;

  /**
   * The message format support translates raw message strings into useable MessageFormat parameters and read the
   * necessary input data from the datarow.
   */
  private MessageFormatSupport messageFormatSupport;
  /**
   * A flag indicating whether the data read from the fields should be URL encoded.
   */
  private boolean urlEncodeData;
  /**
   * A flag indicating whether the whole result string should be URL encoded.
   */
  private boolean urlEncodeResult;
  /**
   * The byte-encoding used for the URL encoding.
   */
  private String encoding;

  /**
   * Default constructor.
   */
  public MessageFormatExpression() {
    messageFormatSupport = new MessageFormatSupport();
    encoding = "UTF-8";
  }

  /**
   * Returns the format string used in the message format.
   *
   * @return the format string.
   */
  public String getPattern() {
    return pattern;
  }

  /**
   * Defines the format string for the {@link java.text.MessageFormat} object used in this implementation.
   *
   * @param pattern
   *          the message format.
   */
  public void setPattern( final String pattern ) {
    this.pattern = pattern;
  }

  /**
   * Returns the defined character encoding that is used to transform the Java-Unicode strings into bytes.
   *
   * @return the encoding.
   */
  public String getEncoding() {
    return encoding;
  }

  /**
   * Defines the character encoding that is used to transform the Java-Unicode strings into bytes.
   *
   * @param encoding
   *          the encoding.
   */
  public void setEncoding( final String encoding ) {
    if ( encoding == null ) {
      throw new NullPointerException();
    }
    this.encoding = encoding;
  }

  /**
   * Defines, whether the values read from the data-row should be URL encoded. Dates and Number objects are never
   * encoded.
   *
   * @param urlEncode
   *          true, if the values from the data-row should be URL encoded before they are passed to the MessageFormat,
   *          false otherwise.
   */
  public void setUrlEncodeValues( final boolean urlEncode ) {
    this.urlEncodeData = urlEncode;
  }

  /**
   * Queries, whether the values read from the data-row should be URL encoded.
   *
   * @return true, if the values are encoded, false otherwise.
   */
  public boolean isUrlEncodeValues() {
    return urlEncodeData;
  }

  /**
   * Queries, whether the formatted result-string will be URL encoded.
   *
   * @return true, if the formatted result will be encoded, false otherwise.
   */
  public boolean isUrlEncodeResult() {
    return urlEncodeResult;
  }

  /**
   * Defines, whether the formatted result-string will be URL encoded.
   *
   * @param urlEncodeResult
   *          true, if the formatted result will be encoded, false otherwise.
   */
  public void setUrlEncodeResult( final boolean urlEncodeResult ) {
    this.urlEncodeResult = urlEncodeResult;
  }

  /**
   * Returns the replacement text that is used if one of the referenced message parameters is null.
   *
   * @return the replacement text for null-values.
   */
  public String getNullString() {
    return messageFormatSupport.getNullString();
  }

  /**
   * Defines the replacement text that is used if one of the referenced message parameters is null.
   *
   * @param nullString
   *          the replacement text for null-values.
   */
  public void setNullString( final String nullString ) {
    this.messageFormatSupport.setNullString( nullString );
  }

  /**
   * Returns the formatted message.
   *
   * @return the formatted message.
   */
  public Object getValue() {
    final ResourceBundleFactory resourceBundleFactory = getResourceBundleFactory();
    messageFormatSupport.setFormatString( pattern );
    messageFormatSupport.setLocale( resourceBundleFactory.getLocale() );

    final String result;
    if ( isUrlEncodeValues() ) {
      final EncodeDataRow dataRow = new EncodeDataRow();
      dataRow.setEncoding( encoding );
      dataRow.setWrappedDataRow( getDataRow() );
      result = messageFormatSupport.performFormat( dataRow );
    } else {
      result = messageFormatSupport.performFormat( getDataRow() );
    }

    if ( isUrlEncodeResult() ) {
      try {
        return URLEncoder.encode( result, getEncoding() );
      } catch ( UnsupportedEncodingException e ) {
        MessageFormatExpression.logger.debug( "Unsupported Encoding: " + encoding );
        return null;
      }
    } else {
      return result;
    }
  }

  public Expression getInstance() {
    final MessageFormatExpression ex = (MessageFormatExpression) super.getInstance();
    ex.messageFormatSupport = new MessageFormatSupport();
    return ex;
  }
}
