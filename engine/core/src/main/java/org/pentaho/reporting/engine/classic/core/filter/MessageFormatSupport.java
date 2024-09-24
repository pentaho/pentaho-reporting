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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.filter.types.ElementTypeUtils;
import org.pentaho.reporting.engine.classic.core.util.PropertyLookupParser;
import org.pentaho.reporting.libraries.base.util.CSVTokenizer;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.formatting.FastMessageFormat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * The message format support class helps to translate named references to fields in a message format string into
 * numeric index positions. With the help of this mapping, we can use a standard Java MessageFormat object to reference
 * fields by their name instead of an arbitrary index position.
 * <p/>
 * A field is referenced by the pattern "$(fieldname)". For additional formatting, all MessageFormat format options are
 * available using the format "$(fieldname, &lt;message option&gt;)". To format a date field with the default short date
 * format, one would use the pattern $(datefield,date,short).
 *
 * @author Thomas Morgner
 */
public class MessageFormatSupport implements Serializable, Cloneable {
  private static final Log logger = LogFactory.getLog( MessageFormatSupport.class );
  private static final String[] EMPTY_FIELDS = new String[0];

  /**
   * The message compiler maps all named references into numeric references.
   */
  protected static class MessageCompiler extends PropertyLookupParser {
    /**
     * The list of fields that have been encountered during the compile process.
     */
    private ArrayList<String> fields;

    /**
     * Default Constructor.
     */
    protected MessageCompiler() {
      this.fields = new ArrayList<String>();
      setMarkerChar( '$' );
      setOpeningBraceChar( '(' );
      setClosingBraceChar( ')' );
    }

    /**
     * Looks up the property with the given name. This replaces the name with the current index position.
     *
     * @param name
     *          the name of the property to look up.
     * @return the translated value.
     */
    protected String lookupVariable( final String name ) {
      final CSVTokenizer tokenizer = new CSVTokenizer( name, false );
      if ( tokenizer.hasMoreTokens() == false ) {
        return null;
      }
      final String varName = tokenizer.nextToken();

      final StringBuilder b = new StringBuilder( name.length() );
      b.append( '{' );
      b.append( String.valueOf( fields.size() ) );
      while ( tokenizer.hasMoreTokens() ) {
        b.append( ',' );
        b.append( tokenizer.nextToken() );
      }
      b.append( '}' );
      final String formatString = b.toString();
      fields.add( varName );
      return formatString;
    }

    /**
     * Returns the collected fields as string-array. The order of the array contents matches the order of the
     * index-position references in the translated message format.
     *
     * @return the fields as array.
     */
    public String[] getFields() {
      return fields.toArray( new String[fields.size()] );
    }
  }

  /**
   * The fields that have been collected during the compile process. The array also acts as mapping of index positions
   * to field names.
   */
  private String[] fields;
  /**
   * The message-format object that is used to format the text.
   */
  private FastMessageFormat format;
  /**
   * The original format string.
   */
  private String formatString;
  /**
   * The translated message format string. All named references have been resolved to numeric index positions.
   */
  private String compiledFormat;
  /**
   * The replacement text that is used if one of the referenced message parameters is null.
   */
  private String nullString;

  /**
   * The current locale of the message format.
   */
  private transient Locale locale;

  private transient TimeZone timeZone;

  /**
   * An internal list of all parameters.
   */
  private transient Object[] parameters;
  /**
   * An internal list of all parameters. This list is used to compare the current parameters with the cached ones.
   */
  private transient Object[] oldParameters;
  /**
   * The cached formatted string value.
   */
  private String cachedValue;

  /**
   * Default Constructor.
   */
  public MessageFormatSupport() {
  }

  /**
   * Returns the original format string that is used to format the fields. This format string contains named references.
   *
   * @return the format string.
   */
  public String getFormatString() {
    return formatString;
  }

  /**
   * Updates the named format string and compiles a new field list and message-format string.
   *
   * @param formatString
   *          the format string.
   */
  public void setFormatString( final String formatString ) {
    if ( formatString == null ) {
      throw new NullPointerException( "Format must not be null" );
    }

    if ( ObjectUtilities.equal( formatString, this.formatString ) ) {
      return;
    }

    this.formatString = formatString;
    this.parameters = null;
    this.oldParameters = null;
    this.cachedValue = null;
    this.fields = null;
    this.format = null;
  }

  /**
   * Formats the message using the fields from the given data-row as values for the parameters.
   *
   * @param dataRow
   *          the data row.
   * @return the formated message.
   */
  public String performFormat( final DataRow dataRow ) {
    if ( fields == null ) {
      final MessageCompiler compiler = new MessageCompiler();
      this.compiledFormat = compiler.translateAndLookup( formatString );
      this.fields = compiler.getFields();

      if ( fields.length > 0 ) {
        final Locale locale = this.locale == null ? Locale.getDefault() : this.locale;
        final TimeZone timeZone = this.timeZone == null ? TimeZone.getDefault() : this.timeZone;

        this.format = new FastMessageFormat( this.compiledFormat, locale, timeZone );
        if ( nullString != null ) {
          this.format.setNullString( nullString );
        }
      } else {
        this.format = null;
      }
    }

    if ( fields.length == 0 || format == null ) {
      return formatString;
    }

    if ( parameters == null ) {
      parameters = new Object[fields.length];
    }
    final int parameterCount = parameters.length;
    if ( oldParameters == null ) {
      oldParameters = new Object[fields.length];
    } else {
      System.arraycopy( parameters, 0, oldParameters, 0, parameterCount );
    }

    for ( int i = 0; i < fields.length; i++ ) {
      final String field = fields[i];
      final Object o = dataRow.get( field );
      if ( o instanceof Number ) {
        parameters[i] = o;
      } else if ( o instanceof Date ) {
        parameters[i] = o;
      } else {
        parameters[i] = ElementTypeUtils.toString( o );
      }
    }

    if ( cachedValue != null && Arrays.equals( parameters, oldParameters ) ) {
      return cachedValue;
    }

    cachedValue = format.format( parameters );
    return cachedValue;
  }

  /**
   * Returns the compiled message format string.
   *
   * @return the compiled message format string.
   */
  public String getCompiledFormat() {
    return compiledFormat;
  }

  /**
   * Returns the locale that is used to format the messages.
   *
   * @return the locale in the message format.
   */
  public Locale getLocale() {
    return locale;
  }

  /**
   * Updates the locale that is used to format the messages.
   *
   * @param locale
   *          the locale in the message format.
   */
  public void setLocale( final Locale locale ) {
    if ( ObjectUtilities.equal( locale, this.locale ) ) {
      return;
    }
    this.locale = locale;
    this.parameters = null;
    this.oldParameters = null;
    this.cachedValue = null;
    this.fields = null;
    this.format = null;
  }

  public TimeZone getTimeZone() {
    return timeZone;
  }

  public void setTimeZone( final TimeZone timeZone ) {
    if ( ObjectUtilities.equal( timeZone, this.timeZone ) ) {
      return;
    }
    this.timeZone = timeZone;
    this.parameters = null;
    this.oldParameters = null;
    this.cachedValue = null;
    this.fields = null;
    this.format = null;
  }

  /**
   * Returns the replacement text that is used if one of the referenced message parameters is null.
   *
   * @return the replacement text for null-values.
   */
  public String getNullString() {
    return nullString;
  }

  /**
   * Defines the replacement text that is used if one of the referenced message parameters is null.
   *
   * @param nullString
   *          the replacement text for null-values.
   */
  public void setNullString( final String nullString ) {
    if ( ObjectUtilities.equal( nullString, this.nullString ) == false ) {
      this.nullString = nullString;
      if ( format != null ) {
        this.format.setNullString( nullString );
      }

      this.oldParameters = null;
      this.cachedValue = null;
    }
  }

  public String[] getFields() {
    if ( fields == null ) {
      return EMPTY_FIELDS;
    }
    return fields.clone();
  }

  /**
   * Creates a copy of this message format support object.
   *
   * @return the copy.
   * @throws CloneNotSupportedException
   *           if an error occured.
   */
  public Object clone() throws CloneNotSupportedException {
    final MessageFormatSupport support = (MessageFormatSupport) super.clone();
    if ( format != null ) {
      support.format = (FastMessageFormat) format.clone();
    }
    if ( parameters != null ) {
      support.parameters = parameters.clone();
    }

    this.oldParameters = null;
    this.cachedValue = null;
    return support;
  }
}
