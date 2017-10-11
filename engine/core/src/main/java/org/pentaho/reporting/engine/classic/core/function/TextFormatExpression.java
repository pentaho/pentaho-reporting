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

package org.pentaho.reporting.engine.classic.core.function;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.URLEncoder;
import org.pentaho.reporting.libraries.formatting.FastMessageFormat;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

/**
 * A TextFormatExpression uses a java.text.MessageFormat to concat and format one or more values evaluated from an
 * expression, function or report datasource.
 * <p/>
 * The TextFormatExpression uses the pattern property to define the global format-pattern used when evaluating the
 * expression. The dataRow fields used to fill the expressions placeholders are defined in a list of properties where
 * the property-names are numbers. The property counting starts at "0".
 * <p/>
 * The Syntax of the <code>pattern</code> property is explained in the class {@link java.text.MessageFormat}.
 * <p/>
 * Example:
 * 
 * <pre>
 * <expression name="expr" class="org.pentaho.reporting.engine.classic.core.function.TextFormatExpression">
 * <properties>
 * <property name="pattern">Invoice for your order from {0, date, EEE, MMM d,
 * yyyy}</property>
 * <property name="fields[0]">printdate</property>
 * </properties>
 * </expression>
 * </pre>
 * <p/>
 * The {@link org.pentaho.reporting.engine.classic.core.function.strings.MessageFormatExpression} allows to specify
 * named field-references in the pattern, which greatly simplifies the pattern-definition.
 *
 * @author Thomas Morgner
 * @deprecated Use the MessageFormatExpression instead.
 */
@SuppressWarnings( "deprecation" )
public class TextFormatExpression extends AbstractExpression {
  private static final Log logger = LogFactory.getLog( TextFormatExpression.class );
  /**
   * An ordered list containing the fieldnames used in the expression.
   */
  private ArrayList fields;

  /**
   * A temporary array to reduce the number of object creations.
   */
  private transient Object[] fieldValues;
  /**
   * A temporary array to reduce the number of object creations. This array is used to compute the cache's validity.
   */
  private transient Object[] oldFieldValues;

  /**
   * The current locale. This is used to optimize the expression evaluation.
   */
  private transient Locale locale;
  /**
   * The current message format object.
   */
  private transient FastMessageFormat messageFormat;

  /**
   * The message-format pattern used to compute the result.
   */
  private String pattern;
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
   * A cached result.
   */
  private String cachedResult;

  /**
   * Default constructor, creates a new unnamed TextFormatExpression.
   */
  public TextFormatExpression() {
    fields = new ArrayList();
    encoding = "iso-8859-1";
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
   * Evaluates the expression by collecting all values defined in the fieldlist from the datarow. The collected values
   * are then parsed and formated by the MessageFormat-object.
   *
   * @return a string containing the pattern inclusive the formatted values from the datarow
   */
  public Object getValue() {
    if ( fields.isEmpty() ) {
      return getPattern();
    }

    final ResourceBundleFactory factory = getResourceBundleFactory();
    if ( messageFormat == null || ObjectUtilities.equal( locale, factory.getLocale() ) == false ) {
      messageFormat = new FastMessageFormat( getPattern(), factory.getLocale() );
      this.locale = factory.getLocale();
    }

    try {
      if ( oldFieldValues == null || oldFieldValues.length != fields.size() ) {
        oldFieldValues = new Object[fields.size()];
      } else if ( fieldValues != null && fieldValues.length == oldFieldValues.length ) {
        System.arraycopy( fieldValues, 0, oldFieldValues, 0, fields.size() );
      }

      fieldValues = getFieldValues( fieldValues );
      final String result;
      if ( cachedResult != null && Arrays.equals( oldFieldValues, fieldValues ) ) {
        result = cachedResult;
      } else {
        result = messageFormat.format( fieldValues );
        cachedResult = result;
      }

      if ( isUrlEncodeResult() ) {
        return URLEncoder.encode( result, getEncoding() );
      } else {
        return result;
      }
    } catch ( UnsupportedEncodingException e ) {
      TextFormatExpression.logger.debug( "Unsupported Encoding: " + encoding );
      return null;
    }
  }

  /**
   * Collects the values of all fields defined in the fieldList.
   *
   * @param retval
   *          an optional array that will receive the field values.
   * @return an Object-array containing all defined values from the datarow
   * @throws java.io.UnsupportedEncodingException
   *           if the character encoding is not recognized by the JDK.
   */
  protected Object[] getFieldValues( Object[] retval ) throws UnsupportedEncodingException {
    final int size = fields.size();
    if ( retval == null || retval.length != size ) {
      retval = new Object[size];
    }

    final DataRow dataRow = getDataRow();
    for ( int i = 0; i < size; i++ ) {
      final String field = (String) fields.get( i );
      if ( field == null ) {
        retval[i] = null;
        continue;
      }
      final Object fieldValue = dataRow.get( field );
      if ( isUrlEncodeValues() ) {
        if ( fieldValue == null ) {
          retval[i] = null;
        } else if ( fieldValue instanceof Date ) {
          retval[i] = fieldValue;
        } else if ( fieldValue instanceof Number ) {
          retval[i] = fieldValue;
        } else if ( isUrlEncodeValues() ) {
          retval[i] = URLEncoder.encode( String.valueOf( fieldValue ), encoding );
        } else {
          retval[i] = fieldValue;
        }
      } else {
        retval[i] = fieldValue;
      }
    }
    return retval;
  }

  /**
   * Returns the pattern defined for this expression.
   *
   * @return the pattern.
   */
  public String getPattern() {
    return pattern;
  }

  /**
   * Defines the pattern for this expression. The pattern syntax is defined by the java.text.MessageFormat object and
   * the given pattern string has to be valid according to the rules defined there.
   *
   * @param pattern
   *          the pattern string
   */
  public void setPattern( final String pattern ) {
    if ( pattern == null ) {
      throw new NullPointerException();
    }
    this.messageFormat = null;
    this.pattern = pattern;
    this.cachedResult = null;
  }

  /**
   * Return a completly separated copy of this function. The copy does no longer share any changeable objects with the
   * original function.
   *
   * @return a copy of this function.
   */
  public Expression getInstance() {
    final TextFormatExpression tex = (TextFormatExpression) super.getInstance();
    tex.fields = (ArrayList) fields.clone();
    tex.fieldValues = null;
    tex.oldFieldValues = null;
    tex.cachedResult = null;
    return tex;
  }

  /**
   * Defines the field in the field-list at the given index.
   *
   * @param index
   *          the position in the list, where the field should be defined.
   * @param field
   *          the name of the field.
   */
  public void setField( final int index, final String field ) {
    if ( fields.size() == index ) {
      fields.add( field );
    } else {
      fields.set( index, field );
    }
    fieldValues = null;
    oldFieldValues = null;
    cachedResult = null;
  }

  /**
   * Returns the defined field at the given index-position.
   *
   * @param index
   *          the position of the field name that should be queried.
   * @return the field name at the given position.
   */
  public String getField( final int index ) {
    return (String) fields.get( index );
  }

  /**
   * Returns the number of fields defined in this expression.
   *
   * @return the number of fields.
   */
  public int getFieldCount() {
    return fields.size();
  }

  /**
   * Returns all defined fields as array of strings.
   *
   * @return all the fields.
   */
  public String[] getField() {
    return (String[]) fields.toArray( new String[fields.size()] );
  }

  /**
   * Defines all fields as array. This completely replaces any previously defined fields.
   *
   * @param fields
   *          the new list of fields.
   */
  public void setField( final String[] fields ) {
    this.fields.clear();
    this.fields.addAll( Arrays.asList( fields ) );
    fieldValues = null;
    oldFieldValues = null;
    cachedResult = null;
  }
}
