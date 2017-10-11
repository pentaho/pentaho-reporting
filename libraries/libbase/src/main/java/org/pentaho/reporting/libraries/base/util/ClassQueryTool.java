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
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * The class-query tool loads classes using a classloader and calls "processClass" for each class encountered. This is
 * highly expensive and sometimes dangerous excercise as the classloading may trigger static initializers and may
 * exhaust the "permgen" space of the Virtual machine.
 * <p/>
 * If possible anyhow, do not use this class.
 *
 * @author Thomas Morgner
 */
public abstract class ClassQueryTool {
  /**
   * A logger.
   */
  private static final Log logger = LogFactory.getLog( ClassQueryTool.class );

  /**
   * The default constructor.
   */
  protected ClassQueryTool() {
  }

  /**
   * Processes a single class-file entry. The method will try to load the given entry as java-class and if that
   * successeds will then call the "processClass" method to let the real implementation handle the class.
   *
   * @param classLoader the classloader that should be used for class- and resource loading.
   * @param entryName   the file name in the classpath.
   */
  protected void processEntry( final ClassLoader classLoader, final String entryName ) {
    if ( entryName == null ) {
      throw new NullPointerException();
    }
    if ( classLoader == null ) {
      throw new NullPointerException();
    }

    if ( entryName.endsWith( ".class" ) == false ) {
      return;
    }
    final String className = entryName.substring( 0, entryName.length() - 6 ).replace( '/', '.' );
    if ( isValidClass( className ) == false ) {
      return;
    }
    try {
      final Class c = Class.forName( className, false, classLoader );
      processClass( classLoader, c );
    } catch ( NoClassDefFoundError ndef ) {
      // Ignore silently. This happens a lot if the classpath is incomplete.
    } catch ( Throwable e ) {
      // ignore ..
      logger.debug( "At class '" + className + "': " + e );
    }
  }

  /**
   * Checks, whether the class is valid. If the class-name is not considered valid by this method, the class will not be
   * processed. Use this to pre-filter the class-stream as loading classes is expensive.
   *
   * @param className the name of the class.
   * @return true, if the class should be processed, false otherwise.
   */
  protected boolean isValidClass( final String className ) {
    return true;
  }

  /**
   * The handler method that is called for every class encountered on the classpath.
   *
   * @param classLoader the classloader used to load the class.
   * @param c           the class that should be handled.
   */
  protected abstract void processClass( final ClassLoader classLoader, final Class c );

  /**
   * Processes a single jar file. The Jar file is processed in the order of the entries contained within the
   * ZIP-directory.
   *
   * @param classLoader the classloader
   * @param jarFile     the URL pointing to the jar file to be parsed.
   */
  private void processJarFile( final ClassLoader classLoader, final URL jarFile ) {
    try {
      final ZipInputStream zf = new ZipInputStream( jarFile.openStream() );
      ZipEntry ze;
      while ( ( ze = zf.getNextEntry() ) != null ) {
        if ( !ze.isDirectory() ) {
          processEntry( classLoader, ze.getName() );
        }
      }
      zf.close();
    } catch ( final IOException e1 ) {
      logger.debug( "Caught IO-Exception while processing file " + jarFile, e1 );
    }
  }

  /**
   * Processes all entries from a given directory, ignoring any subdirectory contents. If the directory contains
   * sub-directories these directories are not searched for JAR or ZIP files.
   * <p/>
   * In addition to the directory given as parameter, the direcories and JAR/ZIP-files on the classpath are also
   * searched for entries.
   * <p/>
   * If directory is null, only the classpath is searched.
   *
   * @param directory the directory to be searched, or null to just use the classpath.
   * @throws IOException       if an error occured while loading the resources from the directory.
   * @throws SecurityException if access to the system properties or access to the classloader is restricted.
   * @noinspection AccessOfSystemProperties
   */
  public void processDirectory( final File directory ) throws IOException {
    final ArrayList<URL> allURLs = new ArrayList<URL>();
    final ArrayList<URL> jarURLs = new ArrayList<URL>();
    final ArrayList<File> directoryURLs = new ArrayList<File>();

    final String classpath = System.getProperty( "java.class.path" );
    final String pathSeparator = System.getProperty( "path.separator" );
    final StringTokenizer tokenizer = new StringTokenizer( classpath, pathSeparator );

    while ( tokenizer.hasMoreTokens() ) {
      final String pathElement = tokenizer.nextToken();

      final File directoryOrJar = new File( pathElement );
      final File file = directoryOrJar.getAbsoluteFile();
      if ( file.isDirectory() && file.exists() && file.canRead() ) {
        allURLs.add( file.toURI().toURL() );
        directoryURLs.add( file );
        continue;
      }

      if ( !file.isFile() || ( file.exists() == false ) || ( file.canRead() == false ) ) {
        continue;
      }

      final String fileName = file.getName();
      if ( fileName.endsWith( ".jar" ) || fileName.endsWith( ".zip" ) ) {
        allURLs.add( file.toURI().toURL() );
        jarURLs.add( file.toURI().toURL() );
      }
    }

    if ( directory != null && directory.isDirectory() ) {
      final File[] driverFiles = directory.listFiles();
      for ( int i = 0; i < driverFiles.length; i++ ) {
        final File file = driverFiles[ i ];
        if ( file.isDirectory() && file.exists() && file.canRead() ) {
          allURLs.add( file.toURI().toURL() );
          directoryURLs.add( file );
          continue;
        }

        if ( !file.isFile() || ( file.exists() == false ) || ( file.canRead() == false ) ) {
          continue;
        }

        final String fileName = file.getName();
        if ( fileName.endsWith( ".jar" ) || fileName.endsWith( ".zip" ) ) {
          allURLs.add( file.toURI().toURL() );
          jarURLs.add( file.toURI().toURL() );
        }
      }
    }

    final URL[] urlsArray = jarURLs.toArray( new URL[ jarURLs.size() ] );
    final File[] dirsArray = directoryURLs.toArray( new File[ directoryURLs.size() ] );
    final URL[] allArray = allURLs.toArray( new URL[ allURLs.size() ] );

    for ( int i = 0; i < allArray.length; i++ ) {
      final URL url = allArray[ i ];
      logger.debug( url );
    }
    for ( int i = 0; i < urlsArray.length; i++ ) {
      final URL url = urlsArray[ i ];
      final URLClassLoader classLoader = new URLClassLoader( allArray );
      processJarFile( classLoader, url );
    }
    for ( int i = 0; i < dirsArray.length; i++ ) {
      final File file = dirsArray[ i ];
      final URLClassLoader classLoader = new URLClassLoader( allArray );
      processDirectory( classLoader, file, "" );
    }
  }

  /**
   * Processes all entries from a given directory. If the directory contains sub-directories these directories are
   * processed in recursive depth-first mannor.
   *
   * @param classLoader the classloader to be used for loading classes.
   * @param file        the directory to be searched.
   * @param pathPrefix  the path prefix used to construct absolute filenames within the classpath.
   */
  private void processDirectory( final URLClassLoader classLoader, final File file, final String pathPrefix ) {
    final File[] files = file.listFiles();
    for ( int i = 0; i < files.length; i++ ) {
      final File subFile = files[ i ];
      if ( subFile.exists() == false || subFile.canRead() == false ) {
        continue;
      }

      if ( subFile.isDirectory() ) {
        processDirectory( classLoader, subFile, pathPrefix + subFile.getName() + '/' );
      } else if ( subFile.isFile() ) {
        processEntry( classLoader, pathPrefix + subFile.getName() );
      }
    }
  }
}
