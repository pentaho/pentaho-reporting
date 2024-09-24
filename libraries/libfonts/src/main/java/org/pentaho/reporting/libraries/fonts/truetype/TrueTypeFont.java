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

package org.pentaho.reporting.libraries.fonts.truetype;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.fonts.ByteAccessUtilities;
import org.pentaho.reporting.libraries.fonts.io.FileFontDataInputSource;
import org.pentaho.reporting.libraries.fonts.io.FontDataInputSource;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * Creation-Date: 06.11.2005, 18:27:21
 *
 * @author Thomas Morgner
 */
public class TrueTypeFont {
  private static final Log logger = LogFactory.getLog( TrueTypeFont.class );

  private static class TrueTypeFontHeader {
    public static final int ENTRY_LENGTH = 12;

    private long version;
    private int numTables;
    private int searchRange;
    private int entrySelector;
    private int rangeShift;

    protected TrueTypeFontHeader( final byte[] data ) throws IllegalStateException {
      this.version = ByteAccessUtilities.readULong( data, 0 );
      if ( version != 0x00010000 && version != 0x4F54544F ) {
        throw new IllegalStateException( "Not a valid TTF or OTF file: Signature not recognized." );
      }

      this.numTables = ByteAccessUtilities.readUShort( data, 4 );
      this.searchRange = ByteAccessUtilities.readUShort( data, 6 );
      this.entrySelector = ByteAccessUtilities.readUShort( data, 8 );
      this.rangeShift = ByteAccessUtilities.readUShort( data, 10 );
    }

    public long getVersion() {
      return version;
    }

    public int getNumTables() {
      return numTables;
    }

    public int getSearchRange() {
      return searchRange;
    }

    public int getEntrySelector() {
      return entrySelector;
    }

    public int getRangeShift() {
      return rangeShift;
    }
  }

  private static class TableDirectoryEntry {
    public static final int ENTRY_LENGTH = 16;

    private long tag;
    private long checkSum;
    private int offset;
    private int length;
    private FontTable table;

    protected TableDirectoryEntry( final byte[] data, final int offset ) {
      this.tag = ByteAccessUtilities.readULong( data, offset );
      this.checkSum = ByteAccessUtilities.readULong( data, offset + 4 );
      this.offset = (int) ByteAccessUtilities.readULong( data, offset + 8 );
      this.length = (int) ByteAccessUtilities.readULong( data, offset + 12 );
    }

    public long getTag() {
      return tag;
    }

    public long getCheckSum() {
      return checkSum;
    }

    public int getOffset() {
      return offset;
    }

    public int getLength() {
      return length;
    }

    public FontTable getTable() {
      return table;
    }

    public void setTable( final FontTable table ) {
      this.table = table;
    }

    /**
     * Returns a string representation of the object. In general, the <code>toString</code> method returns a string that
     * "textually represents" this object. The result should be a concise but informative representation that is easy
     * for a person to read. It is recommended that all subclasses override this method.
     * <p/>
     * The <code>toString</code> method for class <code>Object</code> returns a string consisting of the name of the
     * class of which the object is an instance, the at-sign character `<code>@</code>', and the unsigned hexadecimal
     * representation of the hash code of the object. In other words, this method returns a string equal to the value
     * of: <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return a string representation of the object.
     */
    public String toString() {
      final char c1 = (char) ( ( tag >> 24 ) & 255 );
      final char c2 = (char) ( ( tag >> 16 ) & 255 );
      final char c3 = (char) ( ( tag >> 8 ) & 255 );
      final char c4 = (char) ( tag & 255 );
      return "TableDirectoryEntry={" + c1 + c2 + c3 + c4 + ',' + table + '}';
    }
  }

  private long offset;
  private String filename;
  private FontDataInputSource input;
  private transient byte[] readBuffer;
  private TableDirectoryEntry[] directory;
  private TrueTypeFontHeader header;
  private int collectionIndex;


  public TrueTypeFont( final FontDataInputSource filename )
    throws IOException {
    this( filename, 0, -1 );
  }

  public TrueTypeFont( final FontDataInputSource filename, final long offset )
    throws IOException {
    this( filename, offset, -1 );
  }

  public TrueTypeFont( final FontDataInputSource filename,
                       final long offset,
                       final int collectionIndex )
    throws IOException {
    if ( offset < 0 ) {
      throw new IndexOutOfBoundsException();
    }
    this.collectionIndex = collectionIndex;
    this.offset = offset;
    this.input = filename;

    this.filename = filename.getFileName();
    this.header = new TrueTypeFontHeader
      ( readFully( offset, TrueTypeFontHeader.ENTRY_LENGTH ) );
    this.directory = readTableDirectory();
  }

  public TrueTypeFont( final File filename )
    throws IOException {
    this( filename, 0, -1 );
  }

  public TrueTypeFont( final File filename, final long offset )
    throws IOException {
    this( filename, offset, -1 );
  }

  public TrueTypeFont( final File filename,
                       final long offset,
                       final int collectionIndex )
    throws IOException {
    this( new FileFontDataInputSource( filename ), offset, collectionIndex );
  }

  public int getCollectionIndex() {
    return collectionIndex;
  }

  private TableDirectoryEntry[] readTableDirectory() throws IOException {
    final int numTables = header.getNumTables();
    final int directorySize =
      numTables * TableDirectoryEntry.ENTRY_LENGTH;
    final byte[] directoryData =
      readFully( offset + TrueTypeFontHeader.ENTRY_LENGTH, directorySize );
    final TableDirectoryEntry[] directory = new TableDirectoryEntry[ numTables ];
    for ( int i = 0; i < header.getNumTables(); i += 1 ) {
      final int dirOffset = TableDirectoryEntry.ENTRY_LENGTH * i;
      directory[ i ] = new TableDirectoryEntry( directoryData, dirOffset );
    }
    return directory;
  }

  protected byte[] readFully( final long offset, final int length )
    throws IOException {
    if ( readBuffer == null ) {
      readBuffer = new byte[ Math.max( 8192, length ) ];
    } else if ( readBuffer.length < length ) {
      readBuffer = new byte[ length ];
    }

    input.readFullyAt( offset, readBuffer, length );
    if ( ( readBuffer.length - length ) > 0 ) {
      Arrays.fill( readBuffer, length, readBuffer.length, (byte) 0 );
    }
    return readBuffer;
  }

  public long getOffset() {
    return offset;
  }

  /**
   * The file that was used to load the font. This is deprecated, as only the transition version of JFreeReport is using
   * this hack.
   *
   * @return
   */
  public String getFilename() {
    return filename;
  }

  public FontTable getTable( final long key ) throws IOException {
    final int dirLength = directory.length;
    for ( int i = 0; i < dirLength; i++ ) {
      final TableDirectoryEntry entry = directory[ i ];
      if ( entry.getTag() == key ) {
        final FontTable table = entry.getTable();
        if ( table != null ) {
          return table;
        }
        final FontTable readTable = readTable( entry );
        if ( readTable == null ) {
          return null;
        }
        entry.setTable( readTable );
        return readTable;
      }
    }
    // no such table in the font ..
    return null;
  }

  protected FontTable readTable( final TableDirectoryEntry table )
    throws IOException {
    if ( table.getTag() == NameTable.TABLE_ID ) {
      final byte[] buffer =
        readFully( table.getOffset(), table.getLength() );
      return new NameTable( buffer );
    }
    if ( table.getTag() == FontHeaderTable.TABLE_ID ) {
      final byte[] buffer =
        readFully( table.getOffset(), table.getLength() );
      return new FontHeaderTable( buffer );
    }
    if ( table.getTag() == HorizontalHeaderTable.TABLE_ID ) {
      final byte[] buffer =
        readFully( table.getOffset(), table.getLength() );
      return new HorizontalHeaderTable( buffer );
    }
    if ( table.getTag() == OS2Table.TABLE_ID ) {
      final FontHeaderTable header =
        (FontHeaderTable) getTable( FontHeaderTable.TABLE_ID );
      if ( header == null ) {
        logger.warn( "The font '" + filename + "' does not have a 'head' table. The font file is not valid." );
        return null;
      }
      final byte[] buffer =
        readFully( table.getOffset(), table.getLength() );
      return new OS2Table( buffer, header.getUnitsPerEm() );
    }
    if ( table.getTag() == PostscriptInformationTable.TABLE_ID ) {
      final byte[] buffer =
        readFully( table.getOffset(), table.getLength() );
      return new PostscriptInformationTable( buffer );
    }
    return null;
  }

  public void dispose() {
    input.dispose();
  }

  protected void finalize() throws Throwable {
    super.finalize();
    dispose();
  }

  public FontDataInputSource getInputSource() {
    return input;
  }
}
