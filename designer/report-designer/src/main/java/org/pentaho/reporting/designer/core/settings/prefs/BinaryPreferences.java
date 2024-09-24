/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.designer.core.settings.prefs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class BinaryPreferences extends AbstractPreferences {
  private static final Log logger = LogFactory.getLog( BinaryPreferences.class );
  private Properties properties;
  private BinaryPreferences parent;
  private long lastModificationTime;
  private String rootPath;

  public BinaryPreferences( final String rootPath ) {
    super( null, "" );
    this.rootPath = rootPath;
  }

  public BinaryPreferences( final BinaryPreferences parent, final String name ) {
    super( parent, name );
    this.parent = parent;
    this.properties = new Properties();
  }

  protected void putSpi( final String key, final String value ) {
    initCache();
    properties.setProperty( key, value );
    lastModificationTime = Math.max( lastModificationTime + 1, System.currentTimeMillis() );
    try {
      flush();
    } catch ( BackingStoreException e ) {
      if ( logger.isInfoEnabled() ) {
        logger.info( "Failed to flush configuration changes" );//NON-NLS
      } else if ( logger.isDebugEnabled() ) {
        logger.info( "Failed to flush configuration changes", e );//NON-NLS
      }
    }
  }

  private void initCache() {
    if ( lastModificationTime == 0 ) {
      final File pathForNode = new File( getPathForNode() );
      try {
        load( pathForNode );
      } catch ( BackingStoreException e ) {
        logger.warn( "Failed to load data", e );//NON-NLS
        // ignored 
      }
    }
  }

  protected String getSpi( final String key ) {
    initCache();
    return properties.getProperty( key );
  }

  protected void removeSpi( final String key ) {
    initCache();
    properties.remove( key );
    lastModificationTime = Math.max( lastModificationTime + 1, System.currentTimeMillis() );
    try {
      flush();
    } catch ( BackingStoreException e ) {
      if ( logger.isInfoEnabled() ) {
        logger.info( "Failed to flush configuration changes" );//NON-NLS
      } else if ( logger.isDebugEnabled() ) {
        logger.info( "Failed to flush configuration changes", e );//NON-NLS
      }
    }
  }

  public long getLastModificationTime() {
    return lastModificationTime;
  }

  protected String[] keysSpi() throws BackingStoreException {
    initCache();
    return (String[]) properties.keySet().toArray( new String[ properties.size() ] );
  }

  protected String[] childrenNamesSpi() throws BackingStoreException {
    final ArrayList<String> result = new ArrayList<String>();
    final File pathForNode = new File( getPathForNode() );
    final File[] dirContents = pathForNode.listFiles();
    if ( dirContents != null ) {
      for ( int i = 0; i < dirContents.length; i++ ) {
        if ( dirContents[ i ].isDirectory() ) {
          result.add( decodePath( dirContents[ i ].getName() ) );
        }
      }
    }
    return result.toArray( new String[ result.size() ] );
  }

  protected AbstractPreferences childSpi( final String name ) {
    if ( name == null || name.length() == 0 ) {
      throw new IllegalArgumentException();
    }

    return new BinaryPreferences( this, name );
  }

  protected void syncSpi() throws BackingStoreException {
    final File pathForNode = new File( getPathForNode() );
    if ( pathForNode.exists() == false && properties.isEmpty() ) {
      return;
    }

    load( pathForNode );

    if ( pathForNode.exists() == false ) {
      if ( pathForNode.mkdirs() == false ) {
        throw new BackingStoreException( "Failed to write config " + pathForNode ); //$NON-NLS-1$
      }
    }

    final File target = new File( pathForNode, "prefs.properties" );//NON-NLS
    if ( target.exists() == false || target.lastModified() < lastModificationTime ) {
      try {
        final OutputStream out = new BufferedOutputStream( new FileOutputStream( target ) );
        try {
          properties.store( out, "" );
        } finally {
          out.close();
        }
      } catch ( final Exception e ) {
        throw new BackingStoreException( "Failed to write config " + target ); //$NON-NLS-1$
      }
    }
  }

  private void load( final File pathForNode )
    throws BackingStoreException {
    if ( pathForNode.exists() ) {
      // load ..
      final File target = new File( pathForNode, "prefs.properties" );//NON-NLS
      if ( target.lastModified() > lastModificationTime ) {
        if ( target.exists() ) {
          try {
            final InputStream out = new BufferedInputStream( new FileInputStream( target ) );
            try {
              properties.clear();
              properties.load( out );
              lastModificationTime = Math.max( lastModificationTime + 1, System.currentTimeMillis() );
            } finally {
              out.close();
            }
          } catch ( final Exception e ) {
            UncaughtExceptionsModel.getInstance().addException( e );
            throw new BackingStoreException( "Failed to write config " + target ); //$NON-NLS-1$
          }
        }
      }
    }
  }

  protected void flushSpi() throws BackingStoreException {
    // no-op
    syncSpi();
  }


  protected void removeNodeSpi() throws BackingStoreException {
    // delete the directory ..
    final File pathForNode = new File( getPathForNode() );
    if ( pathForNode.exists() ) {
      final File target = new File( pathForNode, "prefs.properties" );//NON-NLS
      if ( target.delete() == false ) {
        throw new BackingStoreException( "Unable to delete node-backend" );
      }
      if ( pathForNode.delete() == false ) {
        throw new BackingStoreException( "Unable to delete node-backend" );
      }
    }
  }

  private String getPathForNode() {
    if ( parent != null ) {
      return parent.getPathForNode() + File.separatorChar + encodePath( name() );
    }

    return rootPath;
  }


  /**
   * Encodes the given configuration path. All non-ascii characters get replaced by an escape sequence.
   *
   * @param path the path.
   * @return the translated path.
   * @throws java.util.prefs.BackingStoreException if something goes wrong.
   */
  private static String decodePath( final String path ) throws BackingStoreException {
    try {
      final char[] data = path.toCharArray();
      final StringBuffer encoded = new StringBuffer( path.length() );
      int seenDollarIndex = -1;
      for ( int i = 0; i < data.length; i++ ) {
        if ( seenDollarIndex > -1 ) {
          if ( data[ i ] == '$' ) {
            encoded.append( '$' );
            seenDollarIndex = -1;
            continue;
          }

          if ( i - seenDollarIndex == 4 ) {
            final int c = Integer.parseInt( path.substring( seenDollarIndex + 1, i + 1 ), 16 );
            encoded.append( (char) c );
            seenDollarIndex = -1;
            continue;
          } else {
            continue;
          }
        }

        if ( data[ i ] == '$' ) {
          seenDollarIndex = i;
        } else {
          encoded.append( data[ i ] );
        }
      }
      return encoded.toString();
    } catch ( NumberFormatException nfe ) {
      nfe.printStackTrace();
      throw new BackingStoreException( "Failed to decode name: " + path );
    }
  }

  /**
   * Encodes the given configuration path. All non-ascii characters get replaced by an escape sequence.
   *
   * @param path the path.
   * @return the translated path.
   */
  private static String encodePath( final String path ) {
    final char[] data = path.toCharArray();
    final StringBuffer encoded = new StringBuffer( path.length() );
    for ( int i = 0; i < data.length; i++ ) {
      if ( data[ i ] == '$' ) {
        // double quote
        encoded.append( '$' );
        encoded.append( '$' );
      } else if ( Character.isJavaIdentifierPart( data[ i ] ) == false ) {
        // padded hex string
        encoded.append( '$' );
        final String hex = Integer.toHexString( data[ i ] );
        for ( int x = hex.length(); x < 4; x++ ) {
          encoded.append( '0' );
        }
        encoded.append( hex );
      } else {
        encoded.append( data[ i ] );
      }
    }
    return encoded.toString();
  }

}
