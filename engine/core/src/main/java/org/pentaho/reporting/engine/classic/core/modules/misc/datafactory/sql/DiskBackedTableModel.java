/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.MetaTableModel;
import org.pentaho.reporting.engine.classic.core.modules.misc.tablemodel.TableMetaData;
import org.pentaho.reporting.engine.classic.core.util.CloseableTableModel;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;
import org.pentaho.reporting.engine.classic.core.wizard.EmptyDataAttributes;
import org.pentaho.reporting.libraries.base.util.IOUtils;

/**
 * A disk-backed TableModel that spills row data to a temporary file instead of holding all rows in heap memory.
 * Uses RandomAccessFile for O(1) seek-based reads and a small fixed-size in-memory row cache for fast access.
 * This allows processing millions of rows with constant heap usage.
 *
 * <p>Memory usage for 4.5M rows:
 * <ul>
 *   <li>During spill: JDBC fetchSize buffer (~10K rows) + write buffer (1 MB) + encode buffer (~4 KB) ≈ 60 MB</li>
 *   <li>After spill: rowOffsets (36 MB) + cache (4096 rows ~2 MB) + read buffer (64 KB) ≈ 40 MB</li>
 * </ul>
 */
public class DiskBackedTableModel extends AbstractTableModel
    implements CloseableTableModel, MetaTableModel {

  private static final Log logger = LogFactory.getLog( DiskBackedTableModel.class );

  // Serialization type tags
  private static final byte TYPE_NULL = 0;
  private static final byte TYPE_STRING = 1;
  private static final byte TYPE_INT = 2;
  private static final byte TYPE_LONG = 3;
  private static final byte TYPE_DOUBLE = 4;
  private static final byte TYPE_FLOAT = 5;
  private static final byte TYPE_BOOLEAN = 6;
  private static final byte TYPE_BIGDECIMAL = 7;
  private static final byte TYPE_BYTES = 8;
  private static final byte TYPE_SHORT = 9;
  private static final byte TYPE_BYTE = 10;
  private static final byte TYPE_SQL_DATE = 11;
  private static final byte TYPE_SQL_TIME = 12;
  private static final byte TYPE_SQL_TIMESTAMP = 13;
  private static final byte TYPE_DATE = 14;

  private File tempFile;
  private transient RandomAccessFile raf;
  private int rowCount;
  private final int colCount;
  private final String[] columnNames;
  private final Class[] columnTypes;
  private final TableMetaData metaData;

  // Row offset index: byte offset in file for each row
  private long[] rowOffsets;

  //Fixed-size HashMap-backed row cache with circular FIFO eviction — O(1) lookup instead of O(CACHE_SIZE)
  private static final int CACHE_SIZE = 4096;
  private transient HashMap<Integer, Object[]> cacheMap;
  private int[] cacheEvictionOrder;
  private int cacheInsertPos;

  // Reusable read buffer for readRow — avoids per-read allocation
  private byte[] readBuffer = new byte[ 65536 ];

  /**
   * Reads all rows from the given ResultSet, writes them to a temp file, and builds an offset index.
   * The ResultSet and its Statement are closed after reading.
   */
  public DiskBackedTableModel( final ResultSet rs,
                                final String[] header, final Class[] colTypes,
                                final TableMetaData tableMetaData ) throws SQLException {
    this.columnNames = header;
    this.columnTypes = colTypes;
    this.metaData = tableMetaData;
    this.colCount = header.length;
    this.rowCount = 0;

    // Initialize HashMap cache
    this.cacheMap = new HashMap<>( CACHE_SIZE * 2 );
    this.cacheEvictionOrder = new int[ CACHE_SIZE ];
    this.cacheInsertPos = 0;
    Arrays.fill( cacheEvictionOrder, -1 );

    // Pre-allocate offset array with a moderate initial size.
    // The ensureOffsetCapacity() method will grow the array dynamically as needed.
    this.rowOffsets = new long[ 65536 ];

    try {
      spill( rs );
    } catch ( IOException ioe ) {
      logger.error( "Failed to write data to temp file.", ioe );
      throw new SQLException( "Failed to spill data to disk: " + ioe.getMessage() );
    } finally {
      Statement statement = null;
      try {
        statement = rs.getStatement();
      } catch ( SQLException sqle ) {
        logger.warn( "Failed to get statement from resultset", sqle );
      }
      try {
        rs.close();
      } catch ( SQLException sqle ) {
        logger.warn( "Failed to close resultset", sqle );
      }
      try {
        if ( statement != null ) {
          statement.close();
        }
      } catch ( SQLException sqle ) {
        logger.warn( "Failed to close statement", sqle );
      }
    }
  }

  /**
   * Ensures the rowOffsets array can hold at least the given index.
   */
  private void ensureOffsetCapacity( final int index ) {
    if ( index >= rowOffsets.length ) {
      int newLen = rowOffsets.length + ( rowOffsets.length >> 1 );
      if ( newLen <= index ) {
        newLen = index + 1;
      }
      rowOffsets = Arrays.copyOf( rowOffsets, newLen );
    }
  }

  /**
   * Reads all rows from the ResultSet and writes them directly to a temp file.
   * Uses aggressive GC every 1lakh rows to prevent JDBC driver buffer accumulation.
   */
  private void spill( final ResultSet rs ) throws SQLException, IOException {
    tempFile = File.createTempFile( "pentaho_report_data_", ".tmp" );
      // 1 MB write buffer for sequential I/O performance
    try ( CountingOutputStream cos = new CountingOutputStream(
            new java.io.BufferedOutputStream( new FileOutputStream( tempFile ), 1048576 ) );
          DataOutputStream dos = new DataOutputStream( cos ) ) {
      while ( rs.next() ) {
        // ensureOffsetCapacity() will NOT expand since rowOffsets is pre-allocated to 5M
        // This prevents any Array.copyOf() operations that could trigger GC pauses
        ensureOffsetCapacity( rowCount );
        rowOffsets[ rowCount ] = cos.getBytesWritten();

        for ( int col = 0; col < colCount; col++ ) {
          final Object val = rs.getObject( col + 1 );
          Object resolved;
          resolved = getObject( val );
          writeValue( dos, resolved );
          resolved = null; // Help GC collect the object immediately
        }

        rowCount++;

        // This must run more frequently than fetchSize (100000) to prevent driver buffer buildup
        if ( rowCount % 100000 == 0 ) {
          dos.flush();

          // Log heap usage to diagnose memory pressure
          final Runtime rt = Runtime.getRuntime();
          final long usedMB = ( rt.totalMemory() - rt.freeMemory() ) / ( 1024 * 1024 );
          final long maxMB = rt.maxMemory() / ( 1024 * 1024 );
          final long diskMB = cos.getBytesWritten() / ( 1024 * 1024 );
          final long heapPercent = ( usedMB * 100 ) / maxMB;

          logger.info( "DiskBackedTableModel: row " + rowCount + ", disk: " + diskMB
              + " MB, heap: " + usedMB + "/" + maxMB + " MB (" + heapPercent + "%)" );

          // Escalating memory management strategy
          if ( usedMB > ( maxMB * 75 / 100 ) ) {
            // Heap is 75%+ full — aggressive recovery attempt
            logger.warn( "WARN: Heap usage at " + heapPercent + "% at row " + rowCount
                + ". Initiating aggressive garbage collection." );

          }
        }
      }

      dos.flush();
    } catch ( IOException ioe ) {
      logger.error( "Failed to write data to temp file.", ioe );
      throw new SQLException( "Failed to spill data to disk: " + ioe.getMessage() );
    }

    // Trim offset array to exact size to free unused memory
    if ( rowOffsets.length != rowCount ) {
      rowOffsets = Arrays.copyOf( rowOffsets, rowCount );
    }

    // Open RandomAccessFile for reading — stays open for lifetime of model
    try {
      raf = new RandomAccessFile( tempFile, "r" );
    } catch ( IOException ioe ) {
      logger.error( "Failed to open temp file for reading", ioe );
      throw new SQLException( "Failed to open temp file for reading: " + ioe.getMessage() );
    }

    logger.info( "DiskBackedTableModel: completed spill of " + rowCount + " rows to "
        + tempFile.getAbsolutePath() + " (" + tempFile.length() + " bytes)" );
  }

  private static Object getObject( Object val ) throws SQLException {
    Object resolved;
    try {
      if ( val instanceof Blob blobValue ) {
        resolved = IOUtils.getInstance().readBlob( blobValue );
      } else if ( val instanceof Clob clobValue ) {
        resolved = IOUtils.getInstance().readClob( clobValue );
      } else {
        resolved = val;
      }
    } catch ( IOException ioe ) {
      logger.error( "IO error while reading BLOB/CLOB data.", ioe );
      throw new SQLException( "IO error while reading data: " + ioe.getMessage() );
    }
    return resolved;
  }

  /*Converts the input string into a UTF-8 encoded byte array using a standards-compliant approach,
   ensuring proper handling of all Unicode code points (including surrogate pairs).
   The method writes the length of the encoded byte array followed by the actual bytes to the provided DataOutputStream.*/
  private void writeStringReusable( final DataOutputStream dos, final String val ) throws IOException {
      // Use standards-compliant UTF-8 encoding to correctly handle all Unicode code points,
      // including supplementary characters represented as surrogate pairs.
    final byte[] utf8 = val.getBytes( StandardCharsets.UTF_8 );
    dos.writeInt( utf8.length );
    dos.write( utf8 );
  }

  /**
   * Reads a length-prefixed UTF-8 string.
   */
  private String readString( final DataInputStream dis ) throws IOException {
    final int len = dis.readInt();
    final byte[] utf8 = new byte[ len ];
    dis.readFully( utf8 );
    return new String( utf8, StandardCharsets.UTF_8 );
  }

  /**
   * Writes a single cell value to the DataOutputStream with a type tag prefix.
   */
  private void writeValue( final DataOutputStream dos, final Object val ) throws IOException {
    if ( val == null ) {
      dos.writeByte( TYPE_NULL );
      return;
    }

    if ( writePrimitiveTypes( dos, val ) ) {
      return;
    }
    if ( writeDateTypes( dos, val ) ) {
      return;
    }
    if ( writeOtherTypes( dos, val ) ) {
      return;
    }

      // Fallback
    dos.writeByte( TYPE_STRING );
    writeStringReusable( dos, val.toString() );
  }

  private boolean writePrimitiveTypes( DataOutputStream dos, Object val ) throws IOException {
    if ( val instanceof String stringVal ) {
      dos.writeByte( TYPE_STRING );
      writeStringReusable( dos, stringVal );
    } else if ( val instanceof Integer integerVal ) {
      dos.writeByte( TYPE_INT );
      dos.writeInt( integerVal );
    } else if ( val instanceof Long longVal ) {
      dos.writeByte( TYPE_LONG );
      dos.writeLong( longVal );
    } else if ( val instanceof Double doubleVal ) {
      dos.writeByte( TYPE_DOUBLE );
      dos.writeDouble( doubleVal );
    } else if ( val instanceof Float floatVal ) {
      dos.writeByte( TYPE_FLOAT );
      dos.writeFloat( floatVal );
    } else if ( val instanceof Boolean booleanVal ) {
      dos.writeByte( TYPE_BOOLEAN );
      dos.writeBoolean( booleanVal );
    } else if ( val instanceof Short shortVal ) {
      dos.writeByte( TYPE_SHORT );
      dos.writeShort( shortVal );
    } else if ( val instanceof Byte byteVal ) {
      dos.writeByte( TYPE_BYTE );
      dos.writeByte( byteVal );
    } else {
      return false;
    }
    return true;
  }

  private boolean writeDateTypes( DataOutputStream dos, Object val ) throws IOException {
    if ( val instanceof java.sql.Timestamp timestampVal ) {
      dos.writeByte( TYPE_SQL_TIMESTAMP );
      dos.writeLong( timestampVal.getTime() );
      dos.writeInt( timestampVal.getNanos() );
    } else if ( val instanceof java.sql.Date dateVal ) {
      dos.writeByte( TYPE_SQL_DATE );
      dos.writeLong( dateVal.getTime() );
    } else if ( val instanceof java.sql.Time timeVal ) {
      dos.writeByte( TYPE_SQL_TIME );
      dos.writeLong( timeVal.getTime() );
    } else if ( val instanceof java.util.Date dateVal ) {
      dos.writeByte( TYPE_DATE );
      dos.writeLong( dateVal.getTime() );
    } else {
      return false;
    }
    return true;
  }

  private boolean writeOtherTypes( DataOutputStream dos, Object val ) throws IOException {
    if ( val instanceof BigDecimal bigDecimalVal ) {
      dos.writeByte( TYPE_BIGDECIMAL );
      writeStringReusable( dos, bigDecimalVal.toString() );
    } else if ( val instanceof byte[] bytes ) {
      dos.writeByte( TYPE_BYTES );
      dos.writeInt( bytes.length );
      dos.write( bytes );
    } else {
      return false;
    }
    return true;
  }

  /**
   * Reads a single cell value from the DataInputStream.
   */
  private Object readValue( final DataInputStream dis ) throws IOException {
    final byte type = dis.readByte();
    switch ( type ) {
      case TYPE_NULL:
        return null;
      case TYPE_STRING:
        return readString( dis );
      case TYPE_INT:
        return dis.readInt();
      case TYPE_LONG:
        return dis.readLong();
      case TYPE_DOUBLE:
        return dis.readDouble();
      case TYPE_FLOAT:
        return dis.readFloat();
      case TYPE_BOOLEAN:
        return dis.readBoolean();
      case TYPE_BIGDECIMAL:
        return new BigDecimal( readString( dis ) );
      case TYPE_SQL_TIMESTAMP: {
        long time = dis.readLong();
        int nanos = dis.readInt();
        java.sql.Timestamp ts = new java.sql.Timestamp( time );
        ts.setNanos( nanos );
        return ts;
      }
      case TYPE_SQL_DATE:
        return new java.sql.Date( dis.readLong() );
      case TYPE_SQL_TIME:
        return new java.sql.Time( dis.readLong() );
      case TYPE_DATE:
        return new java.util.Date( dis.readLong() );
      case TYPE_BYTES: {
        int len = dis.readInt();
        byte[] bytes = new byte[ len ];
        dis.readFully( bytes );
        return bytes;
      }
      case TYPE_SHORT:
        return dis.readShort();
      case TYPE_BYTE:
        return dis.readByte();
      default:
        logger.warn( "Unknown type tag in temp file: " + type );
        return null;
    }
  }

  /**
   * Reads a full row from the temp file using RandomAccessFile.seek() — O(1) seek.
   */
  private Object[] readRow( final int rowIndex ) {
    if ( raf == null || rowIndex < 0 || rowIndex >= rowCount ) {
      return new Object[0];
    }

    try {
      final long offset = rowOffsets[ rowIndex ];
      final long nextOffset;
      if ( rowIndex + 1 < rowCount ) {
        nextOffset = rowOffsets[ rowIndex + 1 ];
      } else {
        synchronized ( raf ) {
          nextOffset = raf.length();
        }
      }
      final int rowLen = (int) ( nextOffset - offset );

      if ( rowLen > readBuffer.length ) {
        readBuffer = new byte[ rowLen ];
      }

      // Single seek + single bulk read — minimal I/O calls
      synchronized ( raf ) {
        raf.seek( offset );
        raf.readFully( readBuffer, 0, rowLen );
      }

      // Parse from in-memory byte array — no further disk I/O
      final DataInputStream dis = new DataInputStream( new ByteArrayInputStream( readBuffer, 0, rowLen ) );
      final Object[] row = new Object[ colCount ];
      for ( int col = 0; col < colCount; col++ ) {
        row[ col ] = readValue( dis );
      }
      return row;
    } catch ( IOException ioe ) {
      logger.error( "Failed to read row " + rowIndex + " from temp file", ioe );
      return new Object[ 0 ];
    }
  }

  /**
   * Gets a row, using a HashMap-based cache with O(1) lookup and circular eviction.
   * This replaces the previous O(CACHE_SIZE) linear scan which was called on every getValueAt().
   */
  private Object[] getCachedRow( final int rowIndex ) {
    // O(1) lookup
    final Object[] cached = cacheMap.get( rowIndex );
    if ( cached != null ) {
      return cached;
    }

    // Cache miss — read from disk
    final Object[] row = readRow( rowIndex );

      // Evict the oldest entry
    final int evictRow = cacheEvictionOrder[ cacheInsertPos ];
    if ( evictRow >= 0 ) {
      cacheMap.remove( evictRow );
    }
      // Insert new entry
    cacheEvictionOrder[ cacheInsertPos ] = rowIndex;
    cacheMap.put( rowIndex, row );
    cacheInsertPos = ( cacheInsertPos + 1 ) % CACHE_SIZE;

    return row;
  }

  // ---- TableModel implementation ----

  @Override
  public int getRowCount() {
    return rowCount;
  }

  @Override
  public int getColumnCount() {
    return colCount;
  }

  @Override
  public String getColumnName( final int column ) {
    if ( columnNames == null || column >= columnNames.length ) {
      return super.getColumnName( column );
    }
    return columnNames[ column ];
  }

  @Override
  public Class getColumnClass( final int columnIndex ) {
    if ( columnTypes == null || columnIndex >= columnTypes.length ) {
      return Object.class;
    }
    return columnTypes[ columnIndex ];
  }

  @Override
  public Object getValueAt( final int row, final int column ) {
    final Object[] rowValues = getCachedRow( row );
    if ( column >= rowValues.length ) {
      return null;
    }
    return rowValues[ column ];
  }

  // ---- CloseableTableModel implementation ----

  @Override
  public void close() {
    if ( cacheMap != null ) {
      cacheMap.clear();
      cacheMap = null;
    }
    cacheEvictionOrder = null;
    rowOffsets = null;
    readBuffer = null;
    if ( raf != null ) {
      try {
        raf.close();
      } catch ( IOException e ) {
        logger.warn( "Failed to close RandomAccessFile", e );
      }
      raf = null;
    }
    if ( tempFile != null ) {
      try {
        java.nio.file.Files.deleteIfExists( tempFile.toPath() );
      } catch ( IOException e ) {
        logger.warn( "Failed to delete temp file: " + tempFile.getAbsolutePath(), e );
        tempFile.deleteOnExit();
      }
      tempFile = null;
    }
  }

  // ---- MetaTableModel implementation ----

  @Override
  public DataAttributes getCellDataAttributes( final int row, final int column ) {
    if ( metaData == null ) {
      return EmptyDataAttributes.INSTANCE;
    }
    return metaData.getCellDataAttribute( row, column );
  }

  @Override
  public boolean isCellDataAttributesSupported() {
    if ( metaData == null ) {
      return false;
    }
    return metaData.isCellDataAttributesSupported();
  }

  @Override
  public DataAttributes getColumnAttributes( final int column ) {
    if ( metaData == null ) {
      return EmptyDataAttributes.INSTANCE;
    }
    return metaData.getColumnAttribute( column );
  }

  @Override
  public DataAttributes getTableAttributes() {
    if ( metaData == null ) {
      return null;
    }
    return metaData.getTableAttribute();
  }

  /**
   * An OutputStream wrapper that tracks the total number of bytes written as a long value.
   * Sits between DataOutputStream and BufferedOutputStream to count bytes accurately
   * without the int overflow issue of DataOutputStream.size().
   */
  private static class CountingOutputStream extends java.io.OutputStream {
    private final java.io.OutputStream delegate;
    private long bytesWritten;

    CountingOutputStream( final java.io.OutputStream delegate ) {
      this.delegate = delegate;
      this.bytesWritten = 0;
    }

    long getBytesWritten() {
      return bytesWritten;
    }

    @Override
    public void write( final int b ) throws IOException {
      delegate.write( b );
      bytesWritten++;
    }

    @Override
    public void write( final byte[] b, final int off, final int len ) throws IOException {
      delegate.write( b, off, len );
      bytesWritten += len;
    }

    @Override
    public void flush() throws IOException {
      delegate.flush();
    }

    @Override
    public void close() throws IOException {
      delegate.close();
    }
  }
}

