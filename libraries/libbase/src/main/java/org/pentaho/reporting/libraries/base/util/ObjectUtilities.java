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
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

/**
 * A collection of useful static utility methods for handling classes and object instantiation.
 *
 * @author Thomas Morgner
 */
public final class ObjectUtilities {
  private static final Log LOGGER = LogFactory.getLog( ObjectUtilities.class );

  /**
   * A constant for using the TheadContext as source for the classloader.
   */
  public static final String THREAD_CONTEXT = "ThreadContext";
  /**
   * A constant for using the ClassContext as source for the classloader.
   */
  public static final String CLASS_CONTEXT = "ClassContext";

  /**
   * By default use the thread context.
   */
  private static String classLoaderSource = THREAD_CONTEXT;
  /**
   * The custom classloader to be used (if not null).
   */
  private static ClassLoader classLoader;
  private static final Integer[] EMPTY_VERSIONS = new Integer[ 0 ];

  /**
   * Default constructor - private.
   */
  private ObjectUtilities() {
  }

  /**
   * Returns the internal configuration entry, whether the classloader of the thread context or the context classloader
   * should be used.
   *
   * @return the classloader source, either THREAD_CONTEXT or CLASS_CONTEXT.
   */
  public static String getClassLoaderSource() {
    return classLoaderSource;
  }

  /**
   * Defines the internal configuration entry, whether the classloader of the thread context or the context classloader
   * should be used.
   * <p/>
   * This setting can only be defined using the API, there is no safe way to put this into an external configuration
   * file.
   *
   * @param classLoaderSource the classloader source, either THREAD_CONTEXT or CLASS_CONTEXT.
   */
  public static void setClassLoaderSource( final String classLoaderSource ) {
    ObjectUtilities.classLoaderSource = classLoaderSource;
  }

  /**
   * Returns <code>true</code> if the two objects are equal OR both <code>null</code>.
   *
   * @param o1 object 1 (<code>null</code> permitted).
   * @param o2 object 2 (<code>null</code> permitted).
   * @return <code>true</code> or <code>false</code>.
   */
  public static boolean equal( final Object o1, final Object o2 ) {
    if ( o1 == o2 ) {
      return true;
    }
    if ( o1 != null ) {
      return o1.equals( o2 );
    } else {
      return false;
    }
  }

  /**
   * Performs a comparison on two file objects to determine if they refer to the same file. The
   * <code>File.equals()</code> method requires that the files refer to the same file in the same way (relative vs.
   * absolute).
   *
   * @param file1 the first file (<code>null</code> permitted).
   * @param file2 the second file (<code>null</code> permitted).
   * @return <code>true</code> if the files refer to the same file, <code>false</code> otherwise
   */
  public static boolean equals( final File file1, final File file2 ) {
    if ( file1 == file2 ) {
      return true;
    }
    if ( file1 != null && file2 != null ) {
      try {
        return file1.getCanonicalFile().equals( file2.getCanonicalFile() );
      } catch ( IOException ioe ) {
        // There was an error accessing the filesystem
        return file1.equals( file2 );
      }
    }
    return false;
  }

  /**
   * Returns a clone of the specified object, if it can be cloned, otherwise throws a CloneNotSupportedException.
   *
   * @param object the object to clone (<code>null</code> not permitted).
   * @return A clone of the specified object.
   * @throws CloneNotSupportedException if the object cannot be cloned.
   */
  public static Object clone( final Object object )
    throws CloneNotSupportedException {
    if ( object == null ) {
      throw new IllegalArgumentException( "Null 'object' argument." );
    }
    final Class aClass = object.getClass();
    if ( aClass.isArray() ) {
      final int length = Array.getLength( object );
      final Object clone = Array.newInstance( aClass.getComponentType(), length );
      //noinspection SuspiciousSystemArraycopy
      System.arraycopy( object, 0, clone, 0, length );
      return object;
    }

    try {
      final Method method = aClass.getMethod( "clone", (Class[]) null );
      if ( Modifier.isPublic( method.getModifiers() ) ) {
        return method.invoke( object, (Object[]) null );
      }
      throw new CloneNotSupportedException( "Failed to clone: Method 'clone()' is not public on class " + aClass );
    } catch ( NoSuchMethodException e ) {
      LOGGER.warn( "Object without clone() method is impossible on class " + aClass, e );
    } catch ( IllegalAccessException e ) {
      LOGGER.warn( "Object.clone(): unable to call method 'clone()'  on class " + aClass, e );
    } catch ( InvocationTargetException e ) {
      LOGGER.warn( "Object without clone() method is impossible on class " + aClass, e );
    }
    throw new CloneNotSupportedException
      ( "Failed to clone: Clone caused an Exception while cloning type " + aClass );

  }

  /**
   * Redefines the custom classloader.
   *
   * @param classLoader the new classloader or null to use the default.
   */
  public static synchronized void setClassLoader( final ClassLoader classLoader ) {
    ObjectUtilities.classLoader = classLoader;
  }

  /**
   * Returns the custom classloader or null, if no custom classloader is defined.
   *
   * @return the custom classloader or null to use the default.
   */
  public static ClassLoader getClassLoader() {
    return classLoader;
  }

  /**
   * Returns the classloader, which was responsible for loading the given class.
   *
   * @param c the classloader, either an application class loader or the boot loader.
   * @return the classloader, never null.
   * @throws SecurityException if the SecurityManager does not allow to grab the context classloader.
   */
  public static ClassLoader getClassLoader( final Class c ) {
    final String localClassLoaderSource;
    synchronized( ObjectUtilities.class ) {
      if ( classLoader != null ) {
        return classLoader;
      }
      localClassLoaderSource = classLoaderSource;
    }

    if ( "ThreadContext".equals( localClassLoaderSource ) ) {
      final ClassLoader threadLoader = Thread.currentThread().getContextClassLoader();
      if ( threadLoader != null ) {
        return threadLoader;
      }
    }

    // Context classloader - do not cache ..
    final ClassLoader applicationCL = c.getClassLoader();
    if ( applicationCL == null ) {
      return ClassLoader.getSystemClassLoader();
    } else {
      return applicationCL;
    }
  }


  /**
   * Returns the resource specified by the <strong>absolute</strong> name.
   *
   * @param name the name of the resource
   * @param c    the source class
   * @return the url of the resource or null, if not found.
   */
  public static URL getResource( final String name, final Class c ) {
    if ( name == null ) {
      throw new NullPointerException();
    }

    final ClassLoader cl = getClassLoader( c );
    if ( cl == null ) {
      return null;
    }
    return cl.getResource( name );
  }

  /**
   * Returns the resource specified by the <strong>relative</strong> name.
   *
   * @param name the name of the resource relative to the given class
   * @param c    the source class
   * @return the url of the resource or null, if not found.
   */
  public static URL getResourceRelative( final String name, final Class c ) {
    if ( name == null ) {
      throw new NullPointerException();
    }

    final ClassLoader cl = getClassLoader( c );
    final String cname = convertName( name, c );
    if ( cl == null ) {
      return null;
    }
    return cl.getResource( cname );
  }

  /**
   * Transform the class-relative resource name into a global name by appending it to the classes package name. If the
   * name is already a global name (the name starts with a "/"), then the name is returned unchanged.
   *
   * @param name the resource name
   * @param c    the class which the resource is relative to
   * @return the tranformed name.
   */
  private static String convertName( final String name, Class c ) {
    if ( name.length() > 0 && name.charAt( 0 ) == '/' ) {
      // strip leading slash..
      return name.substring( 1 );
    }

    // we cant work on arrays, so remove them ...
    while ( c.isArray() ) {
      c = c.getComponentType();
    }
    // extract the package ...
    final String baseName = c.getName();
    final int index = baseName.lastIndexOf( '.' );
    if ( index == -1 ) {
      return name;
    }

    final String pkgName = baseName.substring( 0, index );
    return pkgName.replace( '.', '/' ) + '/' + name;
  }

  /**
   * Returns the inputstream for the resource specified by the <strong>absolute</strong> name.
   *
   * @param name    the name of the resource
   * @param context the source class
   * @return the url of the resource or null, if not found.
   */
  public static InputStream getResourceAsStream( final String name,
                                                 final Class context ) {
    final URL url = getResource( name, context );
    if ( url == null ) {
      return null;
    }

    try {
      return url.openStream();
    } catch ( IOException e ) {
      return null;
    }
  }

  /**
   * Returns the inputstream for the resource specified by the <strong>relative</strong> name.
   *
   * @param name    the name of the resource relative to the given class
   * @param context the source class
   * @return the url of the resource or null, if not found.
   */
  public static InputStream getResourceRelativeAsStream
  ( final String name, final Class context ) {
    final URL url = getResourceRelative( name, context );
    if ( url == null ) {
      return null;
    }

    try {
      return url.openStream();
    } catch ( IOException e ) {
      return null;
    }
  }

  /**
   * Tries to create a new instance of the given class. This is a short cut for the common bean instantiation code.
   *
   * @param className the class name as String, never null.
   * @param source    the source class, from where to get the classloader.
   * @return the instantiated object or null, if an error occured.
   * @deprecated This class is not typesafe and instantiates the specified object without any additional checks.
   */
  public static Object loadAndInstantiate( final String className,
                                           final Class source ) {
    return loadAndInstantiate( className, source, null );
  }

  /**
   * Tries to create a new instance of the given class. This is a short cut for the common bean instantiation code. This
   * method is a type-safe method and will not instantiate the class unless it is an instance of the given type.
   *
   * @param className the class name as String, never null.
   * @param source    the source class, from where to get the classloader.
   * @param type      the expected type of the object that is being instantiated.
   * @return the instantiated object, which is guaranteed to be of the given type, or null, if an error occured.
   */
  public static <T> T loadAndInstantiate( final String className,
                                          final Class source,
                                          final Class<T> type ) {
    if ( className == null || className.length() == 0 ) {
      return null;
    }
    try {
      final ClassLoader loader = getClassLoader( source );
      final Class c = Class.forName( className, false, loader );
      return instantiateSafe( c, type );
    } catch ( ClassNotFoundException e ) {
      if ( LOGGER.isDebugEnabled() ) {
        LOGGER.debug( "Specified class " + className + " does not exist.", e );
      }
      // sometimes, this one is expected.
    } catch ( NoClassDefFoundError e ) {
      if ( LOGGER.isDebugEnabled() ) {
        LOGGER.debug( noClassDefFoundErrorMessage( className ), e );
      }
    }
    return null;
  }

  public static <T> T instantiateSafe( final Class clazz,
                                       final Class<T> type ) {
    try {
      if ( type != null && type.isAssignableFrom( clazz ) == false ) {
        // this is unacceptable and means someone messed up the configuration
        LOGGER.warn( "Specified class " + clazz.getName() + " is not of expected type " + type );
        return null;
      }
      //noinspection unchecked
      return (T) clazz.newInstance();
    } catch ( NoClassDefFoundError e ) {
      if ( LOGGER.isDebugEnabled() ) {
        LOGGER.debug( noClassDefFoundErrorMessage( clazz.getName() ), e );
      }
    } catch ( Throwable e ) {
      // this is more severe than a class not being found at all
      if ( LOGGER.isDebugEnabled() ) {
        LOGGER.debug( "Specified class " + clazz.getName() + " failed to instantiate correctly.", e );
      } else {
        LOGGER.info( "Specified class " + clazz.getName() + " failed to instantiate correctly." );
      }
    }
    return null;
  }

  private static String noClassDefFoundErrorMessage( String clazz ) {
    return "Specified class " + clazz + " cannot be loaded [NOCLASSDEFERROR].";
  }

  public static <T> Class<? extends T> loadAndValidate( final String className,
                                                        final Class source,
                                                        final Class<T> type ) {
    if ( className == null || className.length() == 0 ) {
      return null;
    }
    try {
      final ClassLoader loader = getClassLoader( source );
      final Class c = Class.forName( className, false, loader );
      if ( type != null && type.isAssignableFrom( c ) == false ) {
        // this is unacceptable and means someone messed up the configuration
        LOGGER.warn( "Specified class " + className + " is not of expected type " + type );
        return null;
      }
      //noinspection unchecked
      return (Class<? extends T>) c;
    } catch ( ClassNotFoundException e ) {
      if ( LOGGER.isDebugEnabled() ) {
        LOGGER.debug( "Specified class " + className + " does not exist.", e );
      }
      // sometimes, this one is expected.
    } catch ( NoClassDefFoundError e ) {
      if ( LOGGER.isDebugEnabled() ) {
        LOGGER.debug( noClassDefFoundErrorMessage( className ), e );
      }
    } catch ( Throwable e ) {
      // this is more severe than a class not being found at all
      if ( LOGGER.isDebugEnabled() ) {
        LOGGER.info( "Specified class " + className + " failed to instantiate correctly.", e );
      } else {
        LOGGER.info( "Specified class " + className + " failed to instantiate correctly." );
      }
    }
    return null;
  }

  /**
   * Checks whether the current JDK is at least JDK 1.4.
   *
   * @return true, if the JDK has been recognized as JDK 1.4, false otherwise.
   * @noinspection AccessOfSystemProperties
   */
  public static boolean isJDK14() {
    try {
      final ClassLoader loader = getClassLoader( ObjectUtilities.class );
      if ( loader != null ) {
        try {
          Class.forName( "java.util.RandomAccess", false, loader );
          return true;
        } catch ( ClassNotFoundException e ) {
          return false;
        }
      }
    } catch ( Exception e ) {
      // the safe test failed, now lets check the system properties ...
    }
    // OK, the quick and dirty, but secure way failed. Lets try it
    // using the standard way.
    try {
      final String version = System.getProperty
        ( "java.vm.specification.version" );
      // parse the beast...
      if ( version == null ) {
        return false;
      }

      final Integer[] versions = parseVersions( version );
      final Integer[] target = new Integer[] { new Integer( 1 ), new Integer( 4 ) };
      return ( ObjectUtilities.compareVersionArrays( versions, target ) >= 0 );
    } catch ( Exception e ) {
      // if that fails too, we assume the safe "no-JDK 1.4" mode.
      return false;
    }
  }

  /**
   * Compares version numbers.
   *
   * @param a1 the first array.
   * @param a2 the second array.
   * @return -1 if a1 is less than a2, 0 if a1 and a2 are equal, and +1 otherwise.
   */
  public static int compareVersionArrays( final Integer[] a1, final Integer[] a2 ) {
    final int length = Math.min( a1.length, a2.length );
    for ( int i = 0; i < length; i++ ) {
      final Integer o1 = a1[ i ];
      final Integer o2 = a2[ i ];
      if ( o1 == null && o2 == null ) {
        // cannot decide ..
        continue;
      }
      if ( o1 == null ) {
        return 1;
      }
      if ( o2 == null ) {
        return -1;
      }
      final int retval = o1.compareTo( o2 );
      if ( retval != 0 ) {
        return retval;
      }
    }
    return 0;
  }

  /**
   * Parses a version string into numbers.
   *
   * @param version the version.
   * @return the parsed version array.
   */
  public static Integer[] parseVersions( final String version ) {
    if ( version == null ) {
      return EMPTY_VERSIONS;
    }

    final ArrayList<Integer> versions = new ArrayList<Integer>();
    final StringTokenizer strtok = new StringTokenizer( version, "." );
    while ( strtok.hasMoreTokens() ) {
      try {
        versions.add( new Integer( strtok.nextToken() ) );
      } catch ( NumberFormatException nfe ) {
        break;
      }
    }
    return versions.toArray( new Integer[ versions.size() ] );
  }

  /**
   * Compares two arrays and determines if they are equal. This method allows to pass <code>null</code> null references
   * for any of the arrays.
   *
   * @param array1 the first array to compare.
   * @param array2 the second array to compare.
   * @return true, if both arrays are equal or both arrays are null, false otherwise.
   */
  public static boolean equalArray( final Object[] array1, final Object[] array2 ) {
    //noinspection ArrayEquality
    if ( array1 == array2 ) {
      return true;
    } else if ( array1 == null || array2 == null ) {
      return false;
    } else {
      return Arrays.equals( array1, array2 );
    }
  }

  public static int hashCode( final Object[] array1 ) {
    if ( array1 == null ) {
      return 0;
    }

    return Arrays.hashCode( array1 );
  }
}
