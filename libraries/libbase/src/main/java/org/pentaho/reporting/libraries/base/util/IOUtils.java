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

package org.pentaho.reporting.libraries.base.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * The IOUtils provide some IO related helper methods.
 *
 * @author Thomas Morgner.
 */
public class IOUtils {
  /**
   * the singleton instance of the utility package.
   */
  private static IOUtils instance;
  private static final Log logger = LogFactory.getLog( IOUtils.class );

  /**
   * DefaultConstructor.
   */
  private IOUtils() {
  }

  /**
   * Gets the singleton instance of the utility package.
   *
   * @return the singleton instance.
   */
  public static synchronized IOUtils getInstance() {
    if ( instance == null ) {
      instance = new IOUtils();
    }
    return instance;
  }

  /**
   * Checks, whether the URL uses a file based protocol.
   *
   * @param url the url.
   * @return true, if the url is file based.
   */
  private boolean isFileStyleProtocol( final URL url ) {
    if ( url == null ) {
      throw new NullPointerException();
    }

    final String protocol = url.getProtocol();
    if ( "http".equals( protocol ) ) {
      return true;
    }
    if ( "https".equals( protocol ) ) {
      return true;
    }
    if ( "ftp".equals( protocol ) ) {
      return true;
    }
    if ( "file".equals( protocol ) ) {
      return true;
    }
    if ( "jar".equals( protocol ) ) {
      return true;
    }
    return false;
  }

  /**
   * Parses the given name and returns the name elements as List of Strings.
   *
   * @param name the name, that should be parsed.
   * @return the parsed name.
   */
  private List<String> parseName( final String name ) {
    final ArrayList<String> list = new ArrayList<String>();
    final StringTokenizer strTok = new StringTokenizer( name, "/" );
    while ( strTok.hasMoreElements() ) {
      final String s = (String) strTok.nextElement();
      if ( s.length() != 0 ) {
        list.add( s );
      }
    }
    return list;
  }

  /**
   * Transforms the name list back into a single string, separated with "/".
   *
   * @param name  the name list.
   * @param query the (optional) query for the URL.
   * @return the constructed name.
   */
  private String formatName( final List name, final String query ) {
    final StringBuilder b = new StringBuilder( 128 );
    final Iterator it = name.iterator();
    while ( it.hasNext() ) {
      b.append( it.next() );
      if ( it.hasNext() ) {
        b.append( '/' );
      }
    }
    if ( query != null ) {
      b.append( '?' );
      b.append( query );
    }
    return b.toString();
  }

  /**
   * Compares both name lists, and returns the last common index shared between the two lists.
   *
   * @param baseName the name created using the base url.
   * @param urlName  the target url name.
   * @return the number of shared elements.
   */
  private int startsWithUntil( final List baseName, final List urlName ) {
    final int minIdx = Math.min( urlName.size(), baseName.size() );
    for ( int i = 0; i < minIdx; i++ ) {
      final String baseToken = (String) baseName.get( i );
      final String urlToken = (String) urlName.get( i );
      if ( !baseToken.equals( urlToken ) ) {
        return i;
      }
    }
    return minIdx;
  }

  /**
   * Checks, whether the URL points to the same service. A service is equal if the protocol, host and port are equal.
   *
   * @param url     a url
   * @param baseUrl an other url, that should be compared.
   * @return true, if the urls point to the same host and port and use the same protocol, false otherwise.
   */
  private boolean isSameService( final URL url, final URL baseUrl ) {
    if ( !url.getProtocol().equals( baseUrl.getProtocol() ) ) {
      return false;
    }
    if ( !url.getHost().equals( baseUrl.getHost() ) ) {
      return false;
    }
    if ( url.getPort() != baseUrl.getPort() ) {
      return false;
    }
    return true;
  }

  /**
   * Creates a relative url by stripping the common parts of the the url. If the baseFile denotes a directory, it must
   * end with a slash.
   *
   * @param targetFile the to be stripped url
   * @param baseFile   the base url, to which the <code>url</code> is relative to.
   * @return the relative url, or the url unchanged, if there is no relation beween both URLs.
   */
  public String createRelativePath( final String targetFile, final String baseFile ) {
    if ( targetFile == null ) {
      throw new NullPointerException( "targetFile must not be null." );
    }
    if ( baseFile == null ) {
      throw new NullPointerException( "baseFile must not be null." );
    }

    // If the URL contains a query, ignore that URL; do not
    // attemp to modify it...
    final List baseName = parseName( baseFile );
    if ( baseName.isEmpty() ) {
      return targetFile;
    }
    final List<String> urlName = parseName( targetFile );
    if ( urlName.isEmpty() ) {
      return targetFile;
    }

    if ( ( baseFile.length() > 0 && baseFile.charAt( baseFile.length() - 1 ) == '/' ) == false ) {
      // remove trailing slashes and ensure that the last element in baseName points to a directory
      baseName.remove( baseName.size() - 1 );
    }

    // if both urls are identical, then return the plain file name...
    if ( baseFile.equals( targetFile ) ) {
      return urlName.get( urlName.size() - 1 );
    }

    int commonIndex = startsWithUntil( urlName, baseName );
    if ( commonIndex == 0 ) {
      return targetFile;
    }

    if ( commonIndex == urlName.size() ) {
      // correct the base index if there is some weird mapping
      // detected,
      // fi. the file url is fully included in the base url:
      //
      // base: /file/test/funnybase
      // file: /file/test
      //
      // this could be a valid configuration whereever virtual
      // mappings are allowed.
      commonIndex -= 1;
    }

    final ArrayList<String> retval = new ArrayList<String>();
    if ( ( baseName.size() + 1 ) != urlName.size() ) {
      final int levels = baseName.size() - commonIndex;
      for ( int i = 0; i < levels; i++ ) {
        retval.add( ".." );
      }
    }

    retval.addAll( urlName.subList( commonIndex, urlName.size() ) );
    return formatName( retval, null );
  }

  /**
   * Creates a relative url by stripping the common parts of the the url. If the base-URL denotes a directory, it must
   * end with a slash.
   *
   * @param url     the to be stripped url
   * @param baseURL the base url, to which the <code>url</code> is relative to.
   * @return the relative url, or the url unchanged, if there is no relation beween both URLs.
   */
  public String createRelativeURL( final URL url, final URL baseURL ) {
    if ( url == null ) {
      throw new NullPointerException( "content url must not be null." );
    }
    if ( baseURL == null ) {
      throw new NullPointerException( "baseURL must not be null." );
    }
    if ( isFileStyleProtocol( url ) && isSameService( url, baseURL ) ) {

      // If the URL contains a query, ignore that URL; do not
      // attemp to modify it...
      final List<String> urlName = parseName( getPath( url ) );
      final List<String> baseName = parseName( getPath( baseURL ) );
      final String query = getQuery( url );

      if ( !isPath( baseURL ) ) {
        baseName.remove( baseName.size() - 1 );
      }

      // if both urls are identical, then return the plain file name...
      if ( String.valueOf( url ).equals( String.valueOf( baseURL ) ) ) {
        return urlName.get( urlName.size() - 1 );
      }

      int commonIndex = startsWithUntil( urlName, baseName );
      if ( commonIndex == 0 ) {
        return url.toExternalForm();
      }

      if ( commonIndex == urlName.size() ) {
        // correct the base index if there is some weird mapping
        // detected,
        // fi. the file url is fully included in the base url:
        //
        // base: /file/test/funnybase
        // file: /file/test
        //
        // this could be a valid configuration whereever virtual
        // mappings are allowed.
        commonIndex -= 1;
      }

      final ArrayList<String> retval = new ArrayList<String>();
      if ( baseName.size() != urlName.size() ) {
        final int levels = baseName.size() - commonIndex;
        for ( int i = 0; i < levels; i++ ) {
          retval.add( ".." );
        }
      }

      retval.addAll( urlName.subList( commonIndex, urlName.size() ) );
      return formatName( retval, query );
    }
    return url.toExternalForm();
  }

  /**
   * Returns <code>true</code> if the URL represents a path, and <code>false</code> otherwise.
   *
   * @param baseURL the URL.
   * @return A boolean.
   */
  private boolean isPath( final URL baseURL ) {
    final String path = getPath( baseURL );
    if ( path.length() > 0 && path.charAt( path.length() - 1 ) == '/' ) {
      return true;
    } else if ( "file".equals( baseURL.getProtocol() ) ) {
      final File f = new File( path );
      try {
        if ( f.isDirectory() ) {
          return true;
        }
      } catch ( SecurityException se ) {
        // ignored ...
      }
    }
    return false;
  }

  /**
   * Implements the JDK 1.3 method URL.getPath(). The path is defined as URL.getFile() minus the (optional) query.
   *
   * @param url the URL
   * @return the path
   */
  private String getQuery( final URL url ) {
    final String file = url.getFile();
    final int queryIndex = file.indexOf( '?' );
    if ( queryIndex == -1 ) {
      return null;
    }
    return file.substring( queryIndex + 1 );
  }

  /**
   * Implements the JDK 1.3 method URL.getPath(). The path is defined as URL.getFile() minus the (optional) query.
   *
   * @param url the URL
   * @return the path
   */
  private String getPath( final URL url ) {
    final String file = url.getFile();
    final int queryIndex = file.indexOf( '?' );
    if ( queryIndex == -1 ) {
      return file;
    }
    return file.substring( 0, queryIndex );
  }

  /**
   * Copies the InputStream into the OutputStream, until the end of the stream has been reached. This method uses a
   * buffer of 4096 kbyte.
   *
   * @param in  the inputstream from which to read.
   * @param out the outputstream where the data is written to.
   * @throws java.io.IOException if a IOError occurs.
   */
  public void copyStreams( final InputStream in, final OutputStream out )
    throws IOException {
    copyStreams( in, out, 4096 );
  }

  /**
   * Copies the InputStream into the OutputStream, until the end of the stream has been reached.
   *
   * @param in         the inputstream from which to read.
   * @param out        the outputstream where the data is written to.
   * @param buffersize the buffer size.
   * @throws java.io.IOException if a IOError occurs.
   */
  public void copyStreams( final InputStream in, final OutputStream out,
                           final int buffersize ) throws IOException {
    // create a 4kbyte buffer to read the file
    final byte[] bytes = new byte[ buffersize ];

    // the input stream does not supply accurate available() data
    // the zip entry does not know the size of the data
    int bytesRead = in.read( bytes );
    while ( bytesRead > -1 ) {
      out.write( bytes, 0, bytesRead );
      bytesRead = in.read( bytes );
    }
  }

  /**
   * Copies the contents of the Reader into the Writer, until the end of the stream has been reached. This method uses a
   * buffer of 4096 kbyte.
   *
   * @param in  the reader from which to read.
   * @param out the writer where the data is written to.
   * @throws java.io.IOException if a IOError occurs.
   */
  public void copyWriter( final Reader in, final Writer out )
    throws IOException {
    copyWriter( in, out, 4096 );
  }

  /**
   * Copies the contents of the Reader into the Writer, until the end of the stream has been reached.
   *
   * @param in         the reader from which to read.
   * @param out        the writer where the data is written to.
   * @param buffersize the buffer size.
   * @throws java.io.IOException if a IOError occurs.
   */
  public void copyWriter( final Reader in, final Writer out,
                          final int buffersize )
    throws IOException {
    // create a 4kbyte buffer to read the file
    final char[] bytes = new char[ buffersize ];

    // the input stream does not supply accurate available() data
    // the zip entry does not know the size of the data
    int bytesRead = in.read( bytes );
    while ( bytesRead > -1 ) {
      out.write( bytes, 0, bytesRead );
      bytesRead = in.read( bytes );
    }
  }

  /**
   * Reads the given number of bytes into the target array. This method does not return until all bytes are read. In
   * case a end-of-stream is reached, the method throws an Exception.
   *
   * @param in     the inputstream from where to read.
   * @param data   the array where to store the data.
   * @param offset the offset in the array where to store the data.
   * @param length the number of bytes to be read.
   * @throws IOException if an IO error occured or the End of the stream has been reached.
   */
  public void readFully( final InputStream in,
                         final byte[] data,
                         final int offset,
                         final int length ) throws IOException {
    int bytesToRead = length;
    int bytesRead = 0;
    do {
      final int size = in.read( data, offset + bytesRead, bytesToRead );
      if ( size == -1 ) {
        throw new IOException( "End-Of-File reached" );
      }
      bytesToRead = bytesToRead - size;
      bytesRead += size;
    }
    while ( bytesToRead > 0 );
  }

  /**
   * Reads the given number of bytes into the target array. This method does not return until all bytes are read. In
   * case a end-of-stream is reached, the method throws an Exception.
   *
   * @param in     the inputstream from where to read.
   * @param data   the array where to store the data.
   * @param offset the offset in the array where to store the data.
   * @param length the number of bytes to be read.
   * @throws IOException if an IO error occured or the End of the stream has been reached.
   */
  public int readSafely( final InputStream in,
                         final byte[] data,
                         final int offset,
                         final int length ) throws IOException {
    int bytesToRead = length;
    int bytesRead = 0;
    do {
      final int size = in.read( data, offset + bytesRead, bytesToRead );
      if ( size == -1 ) {
        return bytesRead;
      }
      bytesToRead = bytesToRead - size;
      bytesRead += size;
    }
    while ( bytesToRead > 0 );

    // end of file reached ..
    return 0;
  }

  /**
   * Extracts the file name from the URL.
   *
   * @param url the url.
   * @return the extracted filename.
   */
  public String getFileName( final URL url ) {
    final String fileRaw = url.getFile();
    final int query = fileRaw.lastIndexOf( '?' );
    final String file;
    if ( query == -1 ) {
      file = fileRaw;
    } else {
      file = fileRaw.substring( 0, query );
    }

    // Now the processing is the same as if it is a string
    return getFileName( file );
  }

  /**
   * Extracts the last file name from the given pathname.
   *
   * @param path the path name.
   * @return the extracted filename.
   */
  public String getFileName( final String path ) {
    // Check for slash and backslash
    final int last = Math.max( path.lastIndexOf( '/' ), path.lastIndexOf( '\\' ) );
    if ( last < 0 ) {
      return path;
    }
    return path.substring( last + 1 );
  }

  /**
   * Removes the file extension from the given file name.
   *
   * @param file the file name.
   * @return the file name without the file extension.
   */
  public String stripFileExtension( final String file ) {
    final int idx = file.lastIndexOf( '.' );
    // handles unix hidden files and files without an extension.
    if ( idx < 1 ) {
      return file;
    }
    return file.substring( 0, idx );
  }

  /**
   * Returns the file extension of the given file name. The returned value will contain the dot.
   *
   * @param file the file name.
   * @return the file extension.
   */
  public String getFileExtension( final String file ) {
    final int idx = file.lastIndexOf( '.' );
    // handles unix hidden files and files without an extension.
    if ( idx < 1 ) {
      return "";
    }
    return file.substring( idx );
  }

  /**
   * Checks, whether the child directory is a subdirectory of the base directory.
   *
   * @param base  the base directory.
   * @param child the suspected child directory.
   * @return true, if the child is a subdirectory of the base directory.
   * @throws java.io.IOException if an IOError occured during the test.
   */
  public boolean isSubDirectory( File base, File child )
    throws IOException {
    base = base.getCanonicalFile();
    child = child.getCanonicalFile();

    File parentFile = child;
    while ( parentFile != null ) {
      if ( base.equals( parentFile ) ) {
        return true;
      }
      parentFile = parentFile.getParentFile();
    }
    return false;
  }


  /**
   * Returns a reference to a file with the specified name that is located somewhere on the classpath.  The code for
   * this method is an adaptation of code supplied by Dave Postill.
   *
   * @param name the filename.
   * @return a reference to a file or <code>null</code> if no file could be found.
   * @throws SecurityException if access to the system properties or filesystem is forbidden.
   * @noinspection AccessOfSystemProperties
   */
  public File findFileOnClassPath( final String name ) throws SecurityException {

    final String classpath = System.getProperty( "java.class.path" );
    final String pathSeparator = System.getProperty( "path.separator" );

    final StringTokenizer tokenizer = new StringTokenizer( classpath, pathSeparator );

    while ( tokenizer.hasMoreTokens() ) {
      final String pathElement = tokenizer.nextToken();

      final File directoryOrJar = new File( pathElement );
      final File absoluteDirectoryOrJar = directoryOrJar.getAbsoluteFile();

      if ( absoluteDirectoryOrJar.isFile() ) {
        final File target = new File( absoluteDirectoryOrJar.getParent(), name );
        if ( target.exists() ) {
          return target;
        }
      } else {
        final File target = new File( directoryOrJar, name );
        if ( target.exists() ) {
          return target;
        }
      }

    }
    return null;

  }

  /**
   * Computes the absolute filename for the target file using the baseFile as root directory. If the baseFile is null or
   * empty, the target file will be normalized (all navigation elements like ".." are removed).
   *
   * @param targetFile the target file name.
   * @param baseFile   the base file (can be null).
   * @return the absolute path.
   */
  public String getAbsolutePath( final String targetFile, final String baseFile ) {
    if ( targetFile == null ) {
      throw new NullPointerException( "targetFile must not be null." );
    }
    if ( baseFile == null || ( baseFile != null && baseFile.isEmpty() ) ) {
      return stripNavigationPaths( targetFile );
    }

    if ( targetFile.length() > 0 && targetFile.charAt( 0 ) == '/' ) {
      return stripNavigationPaths( targetFile.substring( 1 ) );
    }

    final List<String> baseName = parseName( baseFile );
    if ( baseName.isEmpty() ) {
      return stripNavigationPaths( targetFile );
    }
    final List urlName = parseName( targetFile );
    if ( urlName.isEmpty() ) {
      return stripNavigationPaths( baseFile );
    }

    if ( ( baseFile.length() > 0 &&
      baseFile.charAt( baseFile.length() - 1 ) == '/' ) == false ) {
      // trailing slashes indicate directory,
      // so remove last entry if the basefile name does not end with a slash (ie it points to a file)
      baseName.remove( baseName.size() - 1 );
      if ( baseName.isEmpty() ) {
        return stripNavigationPaths( targetFile );
      }
    }

    for ( int i = 0; i < urlName.size(); i++ ) {
      final String pathElement = (String) urlName.get( i );
      if ( ( pathElement != null && pathElement.isEmpty() ) || pathElement == null ) {
        continue;
      }
      if ( ".".equals( pathElement ) ) {
        continue;
      }
      if ( "..".equals( pathElement ) ) {
        if ( baseName.isEmpty() == false ) {
          baseName.remove( baseName.size() - 1 );
        }
        continue;
      }
      baseName.add( pathElement );
    }

    final String s = formatName( baseName, null );
    if ( targetFile.length() > 0 && targetFile.charAt( targetFile.length() - 1 ) == '/' ) {
      return s + '/';
    }
    return s;
  }

  /**
   * Normalizes the given pathname.
   *
   * @param targetFile the target file to be normalized, never null.
   * @return the normalized filename.
   */
  private String stripNavigationPaths( final String targetFile ) {
    final List<String> list = parseName( targetFile );
    final int capacity = list.size();
    final List<String> path = new ArrayList<String>( capacity );
    for ( int i = 0; i < capacity; i++ ) {
      final String pathElement = list.get( i );
      if ( ( pathElement != null && pathElement.isEmpty() ) || pathElement == null ) {
        continue;
      }
      if ( ".".equals( pathElement ) ) {
        continue;
      }
      if ( "..".equals( pathElement ) ) {
        if ( path.isEmpty() == false ) {
          path.remove( path.size() - 1 );
        }
        continue;
      }
      path.add( pathElement );
    }

    final String s = formatName( path, null );
    if ( targetFile.length() > 0 && targetFile.charAt( targetFile.length() - 1 ) == '/' ) {
      return s + '/';
    }
    return s;
  }

  /**
   * Returns the path-portion of the given path (anything before the last slash or backslash) or an empty string.
   *
   * @param path the path or filename from where to extract the path name.
   * @return the extracted path or a empty string.
   */
  public String getPath( final String path ) {
    // Check for slash and backslash
    final int last = Math.max( path.lastIndexOf( '/' ), path.lastIndexOf( '\\' ) );
    if ( last < 0 ) {
      return "";
    }
    return path.substring( 0, last );
  }

  /**
   * Converts a SQL-Clob object into a String. If the Clob is larger than 2^31 characters, we cannot convert it. If
   * there are errors converting it, this method will log the cause and return null.
   *
   * @param clob the clob to be read as string.
   * @return the string or null in case of errors.
   */
  public String readClob( final Clob clob ) throws IOException, SQLException {
    final long length = clob.length();
    if ( length > Integer.MAX_VALUE ) {
      logger.warn( "This CLOB contains more than 2^31 characters. We cannot handle that." );
      throw new IOException( "This CLOB contains more than 2^31 characters. We cannot handle that." );
    }

    final Reader inStream = clob.getCharacterStream();
    final MemoryStringWriter outStream = new MemoryStringWriter( (int) length, 65536 );
    try {
      IOUtils.getInstance().copyWriter( inStream, outStream );
    } finally {
      try {
        inStream.close();
      } catch ( IOException e ) {
        logger.warn( "Failed to close input stream. No worries, we will be alright anyway.", e );
      }
    }
    return outStream.toString();
  }


  /**
   * Converts a SQL-Clob object into a String. If the Clob is larger than 2^31 characters, we cannot convert it. If
   * there are errors converting it, this method will log the cause and return null.
   *
   * @param clob the clob to be read as string.
   * @return the string or null in case of errors.
   */
  public byte[] readBlob( final Blob clob ) throws IOException, SQLException {
    final long length = clob.length();
    if ( length > Integer.MAX_VALUE ) {
      logger.warn( "This CLOB contains more than 2^31 characters. We cannot handle that." );
      throw new IOException( "This BLOB contains more than 2^31 characters. We cannot handle that." );
    }

    final InputStream inStream = clob.getBinaryStream();
    final MemoryByteArrayOutputStream outStream = new MemoryByteArrayOutputStream( (int) length, 65536 );
    try {
      IOUtils.getInstance().copyStreams( inStream, outStream );
    } finally {
      try {
        inStream.close();
      } catch ( IOException e ) {
        logger.warn( "Failed to close input stream. No worries, we will be alright anyway.", e );
      }
    }
    return outStream.toByteArray();
  }
}
