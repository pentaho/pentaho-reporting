/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package org.pentaho.reporting.libraries.resourceloader.loader;

import org.pentaho.reporting.libraries.resourceloader.ResourceKeyCreationException;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Creation-Date: 05.04.2006, 16:02:53
 *
 * @author Thomas Morgner
 */
public class LoaderUtils {
  private LoaderUtils() {
  }

  public static String stripLeadingSlashes( final String s ) {
    int leadingSlashes = 0;
    while ( s.length() > leadingSlashes && s.charAt( leadingSlashes ) == '/' ) {
      leadingSlashes += 1;
    }
    if ( leadingSlashes == 0 ) {
      return s;
    }
    return s.substring( leadingSlashes );
  }

  /**
   * Merges two paths. A path is recognized as an absolute path, if it has an URL-schema definition attached. A parent
   * is recognized as container path (a directory, in the common language), if it ends with a slash.
   * <p/>
   * Todo: Introduce escaping using "\" as escape char.
   *
   * @param parent
   * @param child
   * @return
   * @throws ResourceKeyCreationException
   */
  public static String mergePaths( final String parent,
                                   final String child )
    throws ResourceKeyCreationException {
    final int childPrefix = child.indexOf( "://" );
    if ( childPrefix > 0 ) {
      return child;
    }

    final String parentResource;
    final String parentPrefix;
    final int parentPrefixPos = parent.indexOf( "://" );
    if ( parentPrefixPos > 0 ) {
      parentResource = parent.substring( parentPrefixPos + 3 );
      parentPrefix = parent.substring( 0, parentPrefixPos + 3 );
    } else {
      parentResource = parent;
      parentPrefix = "";
    }

    final List<String> parentList;
    if ( parentResource.length() > 0 && parentResource.charAt( parentResource.length() - 1 ) == '/' ) {
      parentList = parseName( parentResource, false );
    } else {
      parentList = parseName( parentResource, true );
    }
    // construct the full name ...
    parentList.addAll( parseName( child, false ) );
    // and normalize it by removing all '.' and '..' elements.
    final ArrayList<String> normalizedList = new ArrayList<String>();
    for ( int i = 0; i < parentList.size(); i++ ) {
      final String o = parentList.get( i );
      if ( ".".equals( o ) ) {
        continue;
      }
      if ( "..".equals( o ) ) {
        if ( normalizedList.isEmpty() == false ) {
          // remove last element
          normalizedList.remove( normalizedList.size() - 1 );
        }
      } else {
        normalizedList.add( o );
      }
    }

    if ( normalizedList.isEmpty() ) {
      throw new ResourceKeyCreationException( "Unable to build a valid key." );
    }
    final StringBuilder buffer = new StringBuilder();
    buffer.append( parentPrefix );

    for ( int i = 0; i < normalizedList.size(); i++ ) {
      final String s = normalizedList.get( i );
      if ( i > 0 ) {
        buffer.append( '/' );
      }
      buffer.append( s );
    }
    return buffer.toString();
  }


  /**
   * Parses the given name and returns the name elements as List of Strings.
   *
   * @param name the name, that should be parsed.
   * @return the parsed name.
   */
  private static List<String> parseName( final String name, final boolean skipLast ) {
    final StringTokenizer strTok = new StringTokenizer( name, "/" );
    final int count = strTok.countTokens();
    final ArrayList<String> list = new ArrayList<String>( count );
    while ( strTok.hasMoreElements() ) {
      final String s = (String) strTok.nextElement();
      if ( s.length() != 0 ) {
        list.add( s );
      }
    }
    if ( skipLast && list.isEmpty() == false ) {
      list.remove( list.size() - 1 );
    }
    return list;
  }


  /**
   * Extracts the file name from a path name.
   *
   * @param file the path name.
   * @return the extracted filename.
   */
  public static String getFileName( final String file ) {
    final int last = file.lastIndexOf( '/' );
    if ( last < 0 ) {
      return file;
    }
    return file.substring( last + 1 );
  }

}
