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

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * A buffer which represents a Metafile header.
 * <p/>
 * The meta file header has the following structure <table border="1"> <tr> <th>offset</th> <th>length in bytes</th>
 * <th>name</th> <th>meaning</th> </tr> <tr> <td>0x00</td> <td>2</td> <td>mfType</td> <td>MetaFile type: 0x1 = memory
 * based meta file, 0x2 = disk based meta file</td> </tr> <tr> <td>0x02</td> <td>2</td> <td>mfHeader</td> <td>length of
 * header in words (16bit)</td> </tr> <tr> <td>0x04</td> <td>2</td> <td>mfVersion</td> <td>Windows version used to save
 * the file as BCD number. 0x30 for windows 3.0, 0x31 for win3.1 etc.</td> </tr> <tr> <td>0x06</td> <td>4</td>
 * <td>mfSize</td> <td>File length in words</td> </tr> <tr> <td>0x0A</td> <td>2</td> <td>mfNoObj</td> <td>maximum number
 * of objects in the file</td> </tr> <tr> <td>0x0c</td> <td>4</td> <td>mfMaxRec</td> <td>Maximum record length</td>
 * </tr> <tr> <td>0x10</td> <td>2</td> <td>mfnoPar</td> <td>Not used</td> </tr> </table>
 */
public class MfHeader extends Buffer {
  /**
   * A constant stating that the given file is not Wmf-File at all.
   */
  public static final int QUALITY_NO = 0;    // Can't convert.
  /**
   * A constant stating that the given file could be a Wmf-File.
   */
  public static final int QUALITY_MAYBE = 1; // Might be able to convert.
  /**
   * A constant stating that the given file is a Wmf-File.
   */
  public static final int QUALITY_YES = 2;   // Can convert.

  private static final int PLACEABLE_HEADER_SIZE = 22;
  private static final int STANDARD_HEADER_SIZE = 18;

  /**
   * Metadata Positions This implementation always reserves space for both the standard and the extended wmf header; the
   * standard header is always placed after the extended header.
   */
  private static final int WMF_FILE_TYPE = PLACEABLE_HEADER_SIZE;     // WORD
  private static final int WMF_HEADER_SIZE = PLACEABLE_HEADER_SIZE + 0x2;   // WORD
  // private static final int WMF_VERSION = PLACEABLE_HEADER_SIZE + 0x4;       // WORD
  private static final int WMF_FILE_SIZE = PLACEABLE_HEADER_SIZE + 0x06;     // DWORD
  private static final int WMF_NUM_OF_REC = PLACEABLE_HEADER_SIZE + 0x0a;    // WORD
  private static final int WMF_MAX_REC_SIZE = PLACEABLE_HEADER_SIZE + 0x0c;  // DWORD
  //  private static final int WMF_NUM_PARAMS = PLACEABLE_HEADER_SIZE + 0x10;    // WORD always 0 not used

  /**
   * MetaData type: WmfFile is a memory copy.
   */
  private static final int WMF_TYPE_MEM = 0;
  /** MetaData type: WmfFile is a disk copy. */
  //  private static final int WMF_TYPE_DISK = 1;

  /**
   * A magic number indicating that this is a Aldus WMF file.
   */
  private static final int ALDUS_MAGIC_NUMBER_VAL = 0x9ac6cdd7;

  private static final int ALDUS_MAGIC_NUMBER_POS = 0;
  //  private static final int ALDUS_HANDLE_POS = 4;
  private static final int ALDUS_POS_LEFT = 6;
  private static final int ALDUS_POS_TOP = 8;
  private static final int ALDUS_POS_RIGHT = 10;
  private static final int ALDUS_POS_BOTTOM = 12;
  private static final int ALDUS_RESOLUTION = 14; // units per inch

  public MfHeader() {
  }
  //  private static final int ALDUS_RESERVED = 16;
  //  private static final int ALDUS_CHECKSUM = 20;

  /**
   * Is the given input a metafile? We have to guess by reading the header and/or by looking at the file name.
   *
   * @param inName the file name of the stream source
   * @param in     the input stream.
   * @return either QUALITY_NO, QUALITY_MAYBE or QUALITY_YES.
   * @throws IOException if an error occured.
   */
  public static int isMetafile( final String inName, final InputStream in ) throws IOException {
    if ( in != null ) {
      // See if we have a valid header.

      in.mark( PLACEABLE_HEADER_SIZE + STANDARD_HEADER_SIZE );
      final MfHeader header = new MfHeader();
      header.read( in );
      in.reset();

      if ( !header.isValid() ) {
        return QUALITY_NO;
      }
      if ( header.isPlaceable() ) {
        return QUALITY_YES;
      }
      // We are not so confident of identifying non-placeable
      // metafiles, so we require both isValid() and the file
      // extension to match.
    }

    // True if the extension is .wmf.
    if ( inName.regionMatches( true, inName.length() - 4, ".wmf", 0, 4 ) ) {
      return QUALITY_MAYBE;
    }

    return QUALITY_NO;
  }

  /**
   * Read the header from the given input.
   *
   * @param in the input stream
   * @throws IOException if an error occured.
   */
  public void read( final InputStream in )
    throws IOException {
    final int total = PLACEABLE_HEADER_SIZE + STANDARD_HEADER_SIZE;
    setCapacity( total );

    read( in, 0, 4 );
    if ( isPlaceable() ) {
      // read the standard header and the extended Aldus header
      read( in, 4, total - 4 );
    } else {
      // Ignore the space for the placeable header, move the (already read)
      // standard header information to the correct position (after the space
      // of the (non-existent) extended header
      move( 0, PLACEABLE_HEADER_SIZE, 4 );
      // read the remaining bytes of the standard header ...
      read( in, PLACEABLE_HEADER_SIZE + 4, STANDARD_HEADER_SIZE - 4 );
    }

    // Now have the placeable header at the start of the headers buffer,
    // and the windows header following it.
  }

  /**
   * Return true if this is an Aldus placeable header.
   *
   * @return true, if this is an Aldus placeable header, false otherwise.
   */
  private boolean isPlaceable() {
    // Verify magic number.
    return getInt( ALDUS_MAGIC_NUMBER_POS ) == ALDUS_MAGIC_NUMBER_VAL;
  }

  /**
   * Returns true if it looks like a real metafile. This implementation does not support Memory-WmfFiles.
   *
   * @return true, if this file is valid, false otherwise.
   */
  public boolean isValid() {
    final int type = getShort( WMF_FILE_TYPE );  // Memory or disk.
    if ( type == WMF_TYPE_MEM ) {
      // type == null means this is a wmf from memory. we don't want that
      return false;
    }
    if ( getShort( WMF_HEADER_SIZE ) != 9 )  // Header size.
    {
      // A VALID wmf-File has always a standard-header size of 9 WORDS == 18 bytes
      return false;
    }
    return true;
  }

  /**
   * Return the bounding box of this metafile. This returns an empty (0,0,0,0) rectangle if this file is not placeable.
   *
   * @return the bounding box of the metafile.
   */
  public Rectangle getBBox() {
    final int left = getShort( ALDUS_POS_LEFT );
    final int top = getShort( ALDUS_POS_TOP );
    final int right = getShort( ALDUS_POS_RIGHT );
    final int bottom = getShort( ALDUS_POS_BOTTOM );
    return new Rectangle( left, top, right - left, bottom - top );
  }

  /**
   * Gets the defined resolution, if this is an Aldus-File, null otherwise.
   *
   * @return the image resolution or 0 if not defined.
   */
  public int getUnitsPerInch() {
    return getShort( ALDUS_RESOLUTION );
  }

  /**
   * Gets the file size of the WmfFile.
   *
   * @return the filesize in bytes.
   */
  public int getFileSize() {
    return getInt( WMF_FILE_SIZE ) * 2;
  }

  /**
   * Gets the number of records stored in this metafile.
   *
   * @return the number of records.
   */
  public int getObjectsSize() {
    return getShort( WMF_NUM_OF_REC );
  }

  /**
   * Gets the size of the largest Record.
   *
   * @return the maximum record size.
   */
  public int getMaxRecordSize() {
    return getInt( WMF_MAX_REC_SIZE ) * 2;
  }

  /**
   * Gets the header size.
   *
   * @return the header size.
   */
  public int getHeaderSize() {
    if ( isPlaceable() ) {
      return PLACEABLE_HEADER_SIZE + STANDARD_HEADER_SIZE;
    } else {
      return STANDARD_HEADER_SIZE;
    }
  }

}
