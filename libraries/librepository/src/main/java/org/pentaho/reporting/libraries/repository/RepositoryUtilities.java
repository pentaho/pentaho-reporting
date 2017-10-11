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

package org.pentaho.reporting.libraries.repository;

import org.pentaho.reporting.libraries.base.util.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * A collection of repository related helper methods that make it easier to work with repositories.
 *
 * @author Thomas Morgner
 */
public class RepositoryUtilities {
  /**
   * Private constructor to prevent object creation.
   */
  private RepositoryUtilities() {
  }

  /**
   * Returns the content entity for the given path name.
   *
   * @param repository the repository from where to retrieve the content entity.
   * @param name       the path name as array of name-segments.
   * @return the entity at the position, never null.
   * @throws ContentIOException if the path did not point to a valid content entity.
   * @see RepositoryUtilities#splitPath(String, String)
   */
  public static ContentEntity getEntity( final Repository repository, final String[] name )
    throws ContentIOException {
    if ( repository == null ) {
      throw new NullPointerException( "Repository given must not be null." );
    }
    if ( name == null ) {
      throw new NullPointerException( "Path-Name array must not be null." );
    }

    final int length = name.length;
    if ( length == 0 ) {
      return repository.getRoot();
    }

    ContentLocation node = repository.getRoot();
    for ( int i = 0; i < length - 1; i++ ) {
      final String nameItem = name[ i ];
      final ContentEntity entry = node.getEntry( nameItem );
      if ( entry instanceof ContentLocation == false ) {
        // its ok, if we hit the last item
        throw new ContentIOException( "No such item: " + nameItem + " in " + node.getContentId() );
      }
      node = (ContentLocation) entry;
    }
    return node.getEntry( name[ length - 1 ] );
  }

  /**
   * Checks whether a given pathname points to a valid content entity.
   *
   * @param repository the repository from where to retrieve the content entity.
   * @param name       the path name as array of name-segments.
   * @return true, if the entity exists, false otherwise.
   * @throws ContentIOException if an unexpected repository error occured.
   * @see RepositoryUtilities#splitPath(String, String)
   */
  public static boolean isExistsEntity( final Repository repository, final String[] name )
    throws ContentIOException {
    if ( repository == null ) {
      throw new NullPointerException( "Repository given must not be null." );
    }
    if ( name == null ) {
      throw new NullPointerException( "Path-Name array must not be null." );
    }

    final int length = name.length;
    if ( length == 0 ) {
      return true;
    }

    ContentLocation node = repository.getRoot();
    for ( int i = 0; i < length - 1; i++ ) {
      final String nameItem = name[ i ];
      if ( node.exists( nameItem ) == false ) {
        // if there is no such path segment, indicate non-existence
        return false;
      }

      final ContentEntity entry = node.getEntry( nameItem );
      if ( entry instanceof ContentLocation == false ) {
        // if the inner path segment is a leaf, indicate non-existence
        return false;
      }
      node = (ContentLocation) entry;
    }
    // finally check for the last item... 
    return node.exists( name[ length - 1 ] );
  }

  /**
   * Tries to create a content item with the given path-name in the repository. This call will succeed if and only if
   * all but the last segment of the name point to Content-Locations and if the content-item does not yet exist.
   *
   * @param repository the repository in which a new entity should be created.
   * @param name       the name of the new entity as path name.
   * @return the newly created content-item.
   * @throws ContentIOException if an repository error occured or if the path was not valid.
   */
  public static ContentItem createItem( final Repository repository, final String[] name )
    throws ContentIOException {
    if ( repository == null ) {
      throw new NullPointerException( "Repository given must not be null." );
    }
    if ( name == null ) {
      throw new NullPointerException( "Path-Name array must not be null." );
    }

    final int length = name.length;
    if ( length == 0 ) {
      throw new IllegalArgumentException( "Empty name not permitted." );
    }

    ContentLocation node = repository.getRoot();
    for ( int i = 0; i < length - 1; i++ ) {
      final String nameItem = name[ i ];
      if ( node.exists( nameItem ) == false ) {
        // create it
        node = node.createLocation( nameItem );
      } else {
        final ContentEntity entry = node.getEntry( nameItem );
        if ( entry instanceof ContentLocation == false ) {
          // its ok, if we hit the last item
          throw new ContentIOException( "No such item." );
        }
        node = (ContentLocation) entry;
      }
    }
    return node.createItem( name[ length - 1 ] );
  }

  /**
   * Tries to create a content location with the given path-name in the repository. This call will succeed if and only
   * if all but the last segment of the name point to Content-Locations and if the content-entity does not yet exist.
   *
   * @param repository the repository in which a new entity should be created.
   * @param name       the name of the new entity as path name.
   * @return the newly created content-location.
   * @throws ContentIOException if an repository error occured or if the path was not valid.
   */
  public static ContentLocation createLocation( final Repository repository, final String[] name )
    throws ContentIOException {
    if ( repository == null ) {
      throw new NullPointerException( "Repository given must not be null." );
    }
    if ( name == null ) {
      throw new NullPointerException( "Path-Name array must not be null." );
    }

    final int length = name.length;
    if ( length == 0 ) {
      throw new IllegalArgumentException( "Empty name not permitted." );
    }

    ContentLocation node = repository.getRoot();
    for ( int i = 0; i < length - 1; i++ ) {
      final String nameItem = name[ i ];
      if ( node.exists( nameItem ) == false ) {
        // create it
        node = node.createLocation( nameItem );
      } else {
        final ContentEntity entry = node.getEntry( nameItem );
        if ( entry instanceof ContentLocation == false ) {
          // its ok, if we hit the last item
          throw new ContentIOException( "No such item." );
        }
        node = (ContentLocation) entry;
      }
    }
    return node.createLocation( name[ length - 1 ] );
  }

  /**
   * Splits a string on the given separator. Multiple occurences of the separator are unified into a single separator.
   *
   * @param name      the path name.
   * @param separator the separator on which to split.
   * @return the name as array of atomar path elements.
   */
  public static String[] splitPath( final String name, final String separator ) {
    if ( name == null ) {
      throw new NullPointerException( "Path-Name must not be null." );
    }
    if ( separator == null ) {
      throw new NullPointerException( "Separator must not be null." );
    }
    final StringTokenizer strtok = new StringTokenizer( name, separator, false );
    final int tokenCount = strtok.countTokens();
    final String[] retval = new String[ tokenCount ];
    int i = 0;
    boolean emptyTokenRemoved = false;
    while ( strtok.hasMoreTokens() ) {
      final String token = strtok.nextToken();
      retval[ i ] = token;
      if ( "".equals( token ) == false ) {
        i += 1;
      } else {
        emptyTokenRemoved = true;
      }
    }

    if ( emptyTokenRemoved == false ) {
      return retval;
    }

    final String[] reducedArray = new String[ i ];
    System.arraycopy( retval, 0, reducedArray, 0, i );
    return reducedArray;
  }

  /**
   * Splits a string on the given separator. Multiple occurences of the separator result in empty strings as path
   * elements in the returned array.
   *
   * @param name      the path name.
   * @param separator the separator on which to split.
   * @return the name as array of atomar path elements.
   */
  public static String[] split( final String name, final String separator ) {
    if ( name == null ) {
      throw new NullPointerException( "Path-Name must not be null." );
    }
    if ( separator == null ) {
      throw new NullPointerException( "Separator must not be null." );
    }

    final StringTokenizer strtok = new StringTokenizer( name, separator, false );
    final int tokenCount = strtok.countTokens();
    final String[] retval = new String[ tokenCount ];
    int i = 0;
    while ( strtok.hasMoreTokens() ) {
      final String token = strtok.nextToken();
      retval[ i ] = token;
      i += 1;
    }

    return retval;
  }

  /**
   * Builds a absolute pathname for the given entity.
   *
   * @param entity the entity for which the pathname should be computed.
   * @return the absolute path.
   */
  public static String[] buildNameArray( ContentEntity entity ) {
    if ( entity == null ) {
      throw new NullPointerException( "Entity given must not be null." );
    }

    final ArrayList collector = new ArrayList( 20 );
    while ( entity != null ) {
      final ContentLocation parent = entity.getParent();
      if ( parent != null ) {
        // this filters out the root ..
        collector.add( 0, entity.getName() );
      }
      entity = parent;
    }
    return (String[]) collector.toArray( new String[ collector.size() ] );
  }

  /**
   * Builds a string of an absolute pathname for the given entity and using the given separator to separate filename
   * segments..
   *
   * @param entity    the entity for which the pathname should be computed.
   * @param separator the filename separator.
   * @return the absolute path.
   */
  public static String buildName( ContentEntity entity, final String separator ) {
    if ( entity == null ) {
      throw new NullPointerException( "ContentEntity must not be null." );
    }
    if ( separator == null ) {
      throw new NullPointerException( "Separator must not be null." );
    }

    int size = 0;
    final ArrayList collector = new ArrayList();
    while ( entity != null ) {
      final ContentLocation parent = entity.getParent();
      if ( parent != null ) {
        // this filters out the root ..
        final String name = entity.getName();
        if ( name.length() == 0 ) {
          throw new IllegalStateException( "ContentLocation with an empty name" );
        }
        if ( isInvalidPathName( name ) ) {
          throw new IllegalStateException( "ContentLocation with an illegal name: " + name );
        }
        collector.add( name );
        size += 1;
        size += name.length();
      }
      entity = parent;
    }

    final StringBuffer builder = new StringBuffer( size );
    final int maxIdx = collector.size() - 1;
    for ( int i = maxIdx; i >= 0; i-- ) {
      final String s = (String) collector.get( i );
      if ( i != maxIdx ) {
        builder.append( separator );
      }
      builder.append( s );
    }
    return builder.toString();
  }

  /**
   * Checks whether the given entity name is valid for filesystems. This method rejects filenames that either contain a
   * slash ('/') or backslash ('\') which both are commonly used path-separators and it rejects filenames that contain
   * only dots (as the dot names are used as directory traversal names).
   *
   * @param name the filename that should be tested. This name must be a single name section, not a full path.
   * @return true, if the pathname is valid, false otherwise.
   */
  public static boolean isInvalidPathName( String name ) {
    if ( name == null ) {
      throw new NullPointerException( "Name must not be null." );
    }

    boolean onlyDots = true;
    for ( int i = 0; i < name.length(); i++ ) {
      final char c = name.charAt( i );
      if ( onlyDots && c != '.' ) {
        onlyDots = false;
      }
      if ( c == '\\' || c == '/' ) {
        return true;
      }
    }
    return onlyDots;
  }

  /**
   * Writes the given repository as ZIP-File into the given output stream.
   *
   * @param outputStream the output stream that should receive the zipfile.
   * @param repository   the repository that should be written.
   * @throws IOException        if an IO error prevents the writing of the file.
   * @throws ContentIOException if a repository related IO error occurs.
   */
  public static void writeAsZip( final OutputStream outputStream,
                                 final Repository repository ) throws IOException, ContentIOException {
    final ZipOutputStream zipout = new ZipOutputStream( outputStream );
    writeToZipStream( zipout, repository );
    zipout.finish();
    zipout.flush();
  }

  /**
   * Writes the given repository to the given ZIP-output stream.
   *
   * @param zipOutputStream the output stream that represents the ZipFile to be generated.
   * @param repository      the repository that should be written.
   * @throws IOException        if an IO error prevents the writing of the file.
   * @throws ContentIOException if a repository related IO error occurs.
   */
  public static void writeToZipStream( final ZipOutputStream zipOutputStream,
                                       final Repository repository ) throws IOException, ContentIOException {
    writeLocation( repository.getRoot(), zipOutputStream );
  }

  /**
   * Recursively writes the given contentlocation and all content-items into the given Zip output stream.
   *
   * @param outputStream the output stream that should receive the zipfile.
   * @param location     the content location that should be written.
   * @throws IOException        if an IO error prevents the writing of the file.
   * @throws ContentIOException if a repository related IO error occurs.
   */
  private static void writeLocation( final ContentLocation location,
                                     final ZipOutputStream outputStream ) throws IOException, ContentIOException {
    final ContentEntity[] contentEntities = location.listContents();
    for ( int i = 0; i < contentEntities.length; i++ ) {
      final ContentEntity entity = contentEntities[ i ];
      final String fullName = RepositoryUtilities.buildName( entity, "/" );
      if ( entity instanceof ContentLocation ) {
        final ContentLocation childlocation = (ContentLocation) entity;
        final ZipEntry dirEntry = new ZipEntry( fullName + '/' );
        final Object comment =
          entity.getAttribute( LibRepositoryBoot.ZIP_DOMAIN, LibRepositoryBoot.ZIP_COMMENT_ATTRIBUTE );
        if ( comment != null ) {
          dirEntry.setComment( String.valueOf( comment ) );
        }
        final Object version = entity.getAttribute
          ( LibRepositoryBoot.REPOSITORY_DOMAIN, LibRepositoryBoot.VERSION_ATTRIBUTE );
        if ( version instanceof Date ) {
          final Date date = (Date) version;
          dirEntry.setTime( date.getTime() );
        }
        outputStream.putNextEntry( dirEntry );
        writeLocation( childlocation, outputStream );
      } else if ( entity instanceof ContentItem ) {
        final ContentItem item = (ContentItem) entity;
        final ZipEntry itemEntry = new ZipEntry( fullName );
        final Object comment =
          entity.getAttribute( LibRepositoryBoot.ZIP_DOMAIN, LibRepositoryBoot.ZIP_COMMENT_ATTRIBUTE );
        if ( comment != null ) {
          itemEntry.setComment( String.valueOf( comment ) );
        }
        final Object version = entity.getAttribute
          ( LibRepositoryBoot.REPOSITORY_DOMAIN, LibRepositoryBoot.VERSION_ATTRIBUTE );
        if ( version instanceof Date ) {
          final Date date = (Date) version;
          itemEntry.setTime( date.getTime() );
        }

        // need the CRC and the size if method is "stored".
        final Object crc32 = entity.getAttribute
          ( LibRepositoryBoot.ZIP_DOMAIN, LibRepositoryBoot.ZIP_CRC32_ATTRIBUTE );
        final Object size = entity.getAttribute
          ( LibRepositoryBoot.REPOSITORY_DOMAIN, LibRepositoryBoot.SIZE_ATTRIBUTE );
        if ( crc32 instanceof Long && size instanceof Long ) {
          final Long crc32Long = (Long) crc32;
          final Long sizeLong = (Long) size;
          itemEntry.setSize( sizeLong.longValue() );
          itemEntry.setCrc( crc32Long.longValue() );
          final int method = getZipMethod( item );
          final int compression = getZipLevel( item );
          outputStream.setMethod( method );
          outputStream.setLevel( compression );
        }
        outputStream.putNextEntry( itemEntry );
        final InputStream inputStream = item.getInputStream();
        try {
          IOUtils.getInstance().copyStreams( inputStream, outputStream );
        } finally {
          inputStream.close();
        }
        outputStream.closeEntry();
      }
    }
  }

  /**
   * Computes the declared Zip-Compression level for the given content-item. If the content-items attributes do not
   * contain a definition, the default compression is used instead.
   *
   * @param item the content item for which the compression factor should be computed.
   * @return the compression level.
   */
  public static int getZipLevel( final ContentItem item ) {
    final Object method =
      item.getAttribute( LibRepositoryBoot.ZIP_DOMAIN, LibRepositoryBoot.ZIP_COMPRESSION_ATTRIBUTE );
    if ( method instanceof Number == false ) {
      return Deflater.DEFAULT_COMPRESSION;
    }

    final Number n = (Number) method;
    final int level = n.intValue();
    if ( level < 0 || level > 9 ) {
      return Deflater.DEFAULT_COMPRESSION;
    }
    return level;
  }

  /**
   * Computes the declared Zip-Compression mode for the given content-item. If the content-items attributes do not
   * contain a valid definition, the default compression is used instead.
   *
   * @param item the content item for which the compression mode should be computed.
   * @return the compression mode, either ZipOutputStream.DEFLATED or ZipOutputStream.STORED.
   */
  public static int getZipMethod( final ContentItem item ) {
    final Object method =
      item.getAttribute( LibRepositoryBoot.ZIP_DOMAIN, LibRepositoryBoot.ZIP_METHOD_ATTRIBUTE );
    if ( method instanceof Number == false ) {
      return ZipOutputStream.DEFLATED;
    }

    final Number n = (Number) method;
    final int level = n.intValue();
    if ( level != ZipOutputStream.STORED ) {
      return ZipOutputStream.DEFLATED;
    }
    return ZipOutputStream.STORED;
  }

}
