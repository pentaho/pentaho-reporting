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

import org.pentaho.reporting.engine.classic.core.function.AbstractExpression;
import org.pentaho.reporting.libraries.base.util.URLEncoder;

import java.io.UnsupportedEncodingException;

/**
 * Performs an URL encoding on the value read from the given field. As the URL-encoding schema is a binary encoding, a
 * real character encoding must be given as well. If not defined otherwise, ISO-8859-1 is used.
 *
 * @author Thomas Morgner
 * @deprecated This can be replaced by a formula.
 */
public class URLEncodeExpression extends AbstractExpression {
  /**
   * The field name from where to read the string that should be URL-encoded.
   */
  private String field;
  /**
   * The character-encoding that should be used for the URL-encoding of the value.
   */
  private String encoding;

  /**
   * Default Constructor.
   */
  public URLEncodeExpression() {
    encoding = "UTF-8";
  }

  /**
   * Returns the name of the datarow-column from where to read the string value.
   *
   * @return the field.
   */
  public String getField() {
    return field;
  }

  /**
   * Defines the name of the datarow-column from where to read the string value.
   *
   * @param field
   *          the field.
   */
  public void setField( final String field ) {
    this.field = field;
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
   * Encodes the value read from the defined field. The value is converted to a string using the "toString" method.
   *
   * @return the value of the function.
   */
  public Object getValue() {
    if ( field == null ) {
      return null;
    }

    final Object value = getDataRow().get( getField() );
    if ( value == null ) {
      return null;
    }
    try {
      return URLEncoder.encode( String.valueOf( value ), encoding );
    } catch ( UnsupportedEncodingException e ) {
      return null;
    }
  }

}
