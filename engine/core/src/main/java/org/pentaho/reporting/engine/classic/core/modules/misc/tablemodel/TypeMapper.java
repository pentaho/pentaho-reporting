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

package org.pentaho.reporting.engine.classic.core.modules.misc.tablemodel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.net.URL;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Struct;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;

/**
 * @author $Author$
 * @version $Id$
 */
public class TypeMapper {
  private static final Log logger = LogFactory.getLog( TypeMapper.class );
  private static final Class byteArrayClass = byte[].class;

  private static Class mapSQLType( final int t ) {
    switch ( t ) {
      case Types.ARRAY:
        return Object[].class;
      case Types.BIGINT:
        return Long.class;
      case Types.BINARY:
        return byteArrayClass;
      case Types.BIT:
        return Boolean.class;
      case Types.BLOB:
        return Blob.class;
      case Types.BOOLEAN: // Types.BOOLEAN was not part of JDK1.2.2
        return Boolean.class;
      case Types.CHAR:
        return String.class;
      case Types.CLOB:
        return Clob.class;
      case Types.DATALINK: // Types.DATALINK was not part of JDK 1.2.2
        return URL.class;
      case Types.DATE:
        return java.sql.Date.class;
      case Types.DECIMAL:
        return java.math.BigDecimal.class;
      case Types.DISTINCT:
        return Object.class;
      case Types.DOUBLE:
        return Double.class;
      case Types.FLOAT:
        return Double.class;
      case Types.INTEGER:
        return Integer.class;
      case Types.JAVA_OBJECT:
        return Object.class;
      case Types.LONGVARBINARY:
        return byteArrayClass;
      case Types.LONGVARCHAR:
        return String.class;
      case Types.NCLOB:
        return NClob.class;
      case Types.NULL:
        return Object.class;
      case Types.NUMERIC:
        return java.math.BigDecimal.class;
      case Types.NCHAR:
      case Types.NVARCHAR:
      case Types.LONGNVARCHAR:
        return String.class;
      case Types.OTHER:
        return Object.class;
      case Types.REAL:
        return Float.class;
      case Types.REF:
        return Ref.class;
      case Types.ROWID:
        return RowId.class;
      case Types.SMALLINT:
        return Short.class;
      case Types.STRUCT:
        return Struct.class;
      case Types.SQLXML:
        return SQLXML.class;
      case Types.TIME:
        return Time.class;
      case Types.TIMESTAMP:
        return Timestamp.class;
      case Types.TINYINT:
        return Byte.class;
      case Types.VARBINARY:
        return byteArrayClass;
      case Types.VARCHAR:
        return String.class;
      default:
        return Object.class;
    }
  }

  public static Class[] mapTypes( final ResultSetMetaData rsmd ) {
    final Class[] types;
    try {
      types = new Class[ rsmd.getColumnCount() ];
    } catch ( SQLException sqle ) {
      // indicate that we do not have knowledge about any types ..
      return null;
    }

    final int typeLength = types.length;
    for ( int i = 0; i < typeLength; i++ ) {
      types[ i ] = mapForColumn( rsmd, i );
      if ( types[ i ] == null ) {
        logger.error( "JDBC Driver returned <null> as column type. This driver violates the JDBC specifications." );
        types[ i ] = Object.class;
      }
    }

    return types;
  }

  public static Class<?> mapForColumn( ResultSetMetaData rsmd, int i ) {
    try {
      final ClassLoader cl = ObjectUtilities.getClassLoader( TypeMapper.class );
      try {
        final String tn = rsmd.getColumnClassName( i + 1 );
        if ( tn == null ) {
          final int colType = rsmd.getColumnType( i + 1 );
          return mapSQLType( colType );
        } else {
          return Class.forName( tn, false, cl );
        }
      } catch ( final Exception oops ) {
        // ignore exception
        final int colType = rsmd.getColumnType( i + 1 );
        return mapSQLType( colType );
      }
    } catch ( Exception e ) {
      // still ignore the exception
      return Object.class;
    }
  }

  private TypeMapper() {
  }
}
