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

package org.pentaho.reporting.libraries.pixie.wmf;

import java.io.IOException;
import java.io.InputStream;

/**
 * A Windows metafile record.
 * <p/>
 * Every record has a standard header. <table border="1"> <tr> <th>bytes</th> <th>meaning</th> </tr> <tr> <td>4</td>
 * <td>Size of header in words</td> </tr> <tr> <td>2</td> <td>type of the record</td> </tr> <tr> <td>n*2</td> <td>array
 * with n words parameters</td> </tr> </table>
 */
public class MfRecord extends Buffer {
  /**
   * The size of the record header in bytes.
   */
  public static final int RECORD_HEADER_SIZE = 6;

  private static final int RECORD_TYPE_POS = 4;

  /**
   * the RecordType.
   */
  private MfType type;

  /**
   * Creates a new MetaFileRecord with the specified capacitiy. The capacity is given in 16-Bit words. The resulting
   * buffer has the size of 2*parcount&nbsp;+&nbsp;RECORD_HEADER.
   *
   * @param parcount the number of 16-Bit words stored as bulk in the record.
   */
  public MfRecord( final int parcount ) {
    super( parcount * 2 + RECORD_HEADER_SIZE );
  }

  /**
   * Constructs a new MfRecord by reading the data from the input stream.
   *
   * @param in the source inputstream
   * @throws IOException if an IOError occurs.
   */
  public MfRecord( final InputStream in )
    throws IOException {
    read( in );
  }

  /**
   * Read a record from an input stream.
   */
  protected void read( final InputStream in )
    throws IOException {
    super.read( in, 0, RECORD_HEADER_SIZE );
    final int remaining = getInt( 0 ) * 2 - RECORD_HEADER_SIZE;
    if ( remaining > 0 ) {
      super.read( in, RECORD_HEADER_SIZE, remaining );
    }
    type = MfType.get( getType() );
  }

  /**
   * Queries the 16-bit type of this record.
   *
   * @return the RecordType
   */
  public int getType() {
    return getShort( RECORD_TYPE_POS );
  }

  /**
   * Defines the 16-bit type of this record.
   *
   * @param type the RecordType
   */
  public void setType( final int type ) {
    setShort( RECORD_TYPE_POS, type );
  }

  /**
   * Return a 16-bit param from the given offset. Offset is in 16-bit words.
   *
   * @param p the parameter position in words.
   * @return the parameter value.
   */
  public int getParam( final int p ) {
    return getShort( p * 2 + RECORD_HEADER_SIZE );
  }

  /**
   * Defines a 16-bit param at the given offset. Offset is in 16-bit words.
   *
   * @param p     the parameter position in words.
   * @param value the parameter value.
   */
  public void setParam( final int p, final int value ) {
    setShort( p * 2 + RECORD_HEADER_SIZE, value );
  }

  /**
   * Return a 32-bit param from the given offset. Offset is in 16-bit words.
   *
   * @param p the parameter position in words.
   * @return the parameter value.
   */
  public int getLongParam( final int p ) {  // Offset is in 16-bit words.
    return getInt( p * 2 + RECORD_HEADER_SIZE );
  }

  /**
   * Defines a 32-bit param at the given offset. Offset is in 16-bit words.
   *
   * @param p     the parameter position in words.
   * @param value the parameter value.
   */
  public void setLongParam( final int p, final int value ) {
    setInt( p * 2 + RECORD_HEADER_SIZE, value );
  }

  /**
   * Return a string param from the given offset. Offset is in 16-bit words.
   *
   * @param p the parameter position in words.
   * @return the parameter value.
   */
  public String getStringParam( final int p, final int len ) {
    return getString( p * 2 + RECORD_HEADER_SIZE, len );
  }

  /**
   * Defines a string param at the given offset. Offset is in 16-bit words.
   *
   * @param p the parameter position in words.
   * @param s the parameter value.
   */
  public void setStringParam( final int p, final String s ) {
    setString( p * 2 + RECORD_HEADER_SIZE, s );
  }

  /**
   * Return the name of this type of record.
   */
  public String getName() {
    return type.getName();
  }

  /**
   * Return debug info.
   */
  public String toString() {
    final StringBuffer result = new StringBuffer();
    result.append( type );
    result.append( ' ' );
    result.append( getName() );
    result.append( ": " );

    final StringBuffer str = new StringBuffer();

    final int len = ( getInt( 0 ) - 3 ) * 2;
    for ( int i = 0; i < len; i++ ) {
      if ( ( i % 16 ) == 0 ) {
        result.append( '\n' );
        str.append( '\n' );
      } else if ( ( i % 8 ) == 0 ) {
        result.append( ' ' );
      }

      final int by = getByte( i + RECORD_HEADER_SIZE );

      if ( by < 16 ) {
        result.append( '0' );
      }
      result.append( Integer.toHexString( by ) );
      //str.append ((char) by);
      result.append( ' ' );
    }
    return result.toString();
  }

  /**
   * True if this record marks the screen. Currently such records are ignored.
   */
  public boolean doesMark() {
    return ( type.doesMark() );
  }

  /**
   * True if this record affects mapping modes.
   */
  public boolean isMappingMode() {
    return ( type.isMappingMode() );
  }
}
