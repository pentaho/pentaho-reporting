/*
 * (c) COPYRIGHT 1999 World Wide Web Consortium
 * (Massachusetts Institute of Technology, Institut National de Recherche
 *  en Informatique et en Automatique, Keio University).
 * All Rights Reserved. http://www.w3.org/Consortium/Legal/
 *
 * $Id: MediaListImpl.java 1830 2006-04-23 14:51:03Z taqua $
 */
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
 * Copyright (c) 2009 - 2017 Hitachi Vantara.  All rights reserved.
 */

package org.w3c.flute.parser;

import org.w3c.css.sac.SACMediaList;

/**
 * @author Philippe Le Hegaret
 * @version $Revision$
 */
public class MediaListImpl implements SACMediaList {

  String[] array = new String[ 10 ];
  int current;

  public int getLength() {
    return current;
  }

  public String item( int index ) {
    if ( ( index < 0 ) || ( index >= current ) ) {
      return null;
    }
    return array[ index ];
  }

  void addItem( String medium ) {
    if ( medium.equals( "all" ) ) {
      array[ 0 ] = "all";
      current = 1;
      return;
    }
    for ( int i = 0; i < current; i++ ) {
      if ( medium.equals( array[ i ] ) ) {
        return;
      }
    }
    if ( current == array.length ) {
      String[] old = array;
      array = new String[ current + current ];
      System.arraycopy( old, 0, array, 0, current );
    }
    array[ current++ ] = medium;
  }

  /**
   * Returns a string representation of this object.
   */
  public String toString() {
    int _i;

    switch( current ) {
      case 0:
        return "";
      case 1:
        return array[ 0 ];
      default:
        boolean not_done = true;
        int i = 0;
        StringBuffer buf = new StringBuffer( 50 );
        do {
          buf.append( array[ i++ ] );
          if ( i == current ) {
            not_done = false;
          } else {
            buf.append( ", " );
          }
        } while ( not_done );
        return buf.toString();
    }
  }
}
