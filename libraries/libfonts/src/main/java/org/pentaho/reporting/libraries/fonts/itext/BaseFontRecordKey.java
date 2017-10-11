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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.fonts.itext;

/**
 * A PDF font record key. This class is immutable.
 *
 * @author Thomas Morgner
 */
public final class BaseFontRecordKey {
  /**
   * The file name of the font file.
   */
  private String fileName;


  /**
   * The encoding.
   */
  private final String encoding;

  private boolean embedded;

  private Integer hashCode;

  /**
   * Creates a new key.
   *
   * @param fileName the physical filename name of the font file.
   * @param encoding the encoding.
   */
  public BaseFontRecordKey( final String fileName,
                            final String encoding, final boolean embedded ) {
    if ( fileName == null ) {
      throw new NullPointerException( "font name is null." );
    }
    if ( encoding == null ) {
      throw new NullPointerException( "Encoding is null." );
    }
    this.fileName = fileName;
    this.encoding = encoding;
    this.embedded = embedded;
  }

  /**
   * Indicates whether some other object is "equal to" this BaseFontRecordKey.
   *
   * @param o the object to test.
   * @return true or false.
   */
  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( !( o instanceof BaseFontRecordKey ) ) {
      return false;
    }

    final BaseFontRecordKey key = (BaseFontRecordKey) o;
    if ( embedded != key.embedded ) {
      return false;
    }
    if ( !fileName.equals( key.fileName ) ) {
      return false;
    }
    if ( !encoding.equals( key.encoding ) ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    if ( hashCode == null ) {
      int result = fileName.hashCode();
      result = 29 * result + encoding.hashCode();
      result = 29 * result + ( embedded ? 1 : 0 );
      hashCode = new Integer( result );
    }
    return hashCode.intValue();
  }

  /**
   * Returns a string representation of the object. In general, the <code>toString</code> method returns a string that
   * "textually represents" this object. The result should be a concise but informative representation that is easy for
   * a person to read. It is recommended that all subclasses override this method.
   * <p/>
   * The <code>toString</code> method for class <code>Object</code> returns a string consisting of the name of the class
   * of which the object is an instance, the at-sign character `<code>@</code>', and the unsigned hexadecimal
   * representation of the hash code of the object. In other words, this method returns a string equal to the value of:
   * <blockquote>
   * <pre>
   * getClass().getName() + '@' + Integer.toHexString(hashCode())
   * </pre></blockquote>
   *
   * @return a string representation of the object.
   */
  public String toString() {
    return ( "FontKey={name=" + fileName + "; encoding=" +
      encoding + "; embedded=" + embedded + '}' );

  }
}
