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

import junit.framework.TestCase;

import java.util.Arrays;

public class LinkedMapTest extends TestCase {
  public LinkedMapTest( final String s ) {
    super( s );
  }

  public void testValidity()
    throws Exception {
    final LinkedMap map = new LinkedMap( 16, 1024 );
    map.put( "1", "A" );
    map.put( "2", "B" );
    map.put( "3", "C" );
    map.put( "4", "D" );
    map.put( "5", "E" );
    map.put( "6", "F" );
    map.put( "1", "A" );
    final Object[] expectedKeys = { "2", "3", "4", "5", "6", "1" };
    final Object[] a2 = map.keys();
    if ( Arrays.equals( expectedKeys, a2 ) == false ) {
      throw new Exception();
    }

    if ( ObjectUtilities.equal( map.get( "1" ), "A" ) == false ) {
      throw new NullPointerException();
    }
    if ( ObjectUtilities.equal( map.get( "2" ), "B" ) == false ) {
      throw new NullPointerException();
    }
    if ( ObjectUtilities.equal( map.get( "3" ), "C" ) == false ) {
      throw new NullPointerException();
    }
    if ( ObjectUtilities.equal( map.get( "4" ), "D" ) == false ) {
      throw new NullPointerException();
    }
    if ( ObjectUtilities.equal( map.get( "5" ), "E" ) == false ) {
      throw new NullPointerException();
    }
    if ( ObjectUtilities.equal( map.get( "6" ), "F" ) == false ) {
      throw new NullPointerException();
    }
    if ( ObjectUtilities.equal( map.get( "1" ), "A" ) == false ) {
      throw new NullPointerException();
    }


    map.remove( "1" );
    map.remove( "2" );

    final Object[] expectedKeys2 = { "3", "4", "5", "6" };
    final Object[] a3 = map.keys();
    if ( Arrays.equals( expectedKeys2, a3 ) == false ) {
      throw new Exception();
    }

    map.remove( "5" );

    final Object[] arrayCache = map.values( new String[ map.size() ] );

    map.remove( "5" );
    map.remove( "3" );
    map.remove( "4" );
    map.remove( "5" );
    map.remove( "6" );

    if ( map.keys().length != 0 ) {
      throw new Exception();
    }
  }


  public void testStrange() {
    final LinkedMap map = new LinkedMap();
    map.put( "A", "1" );
    map.put( "B", "2" );
    map.put( "A", "3" );
    map.remove( "A" );
    map.remove( "B" );
    map.remove( "A" );

  }

  public void testResize() {
    final LinkedMap map = new LinkedMap( 2, 0.75f );
    map.put( "A", "1" );
    assertNotNull( map.get( "A" ) );
    map.put( "B", "2" );
    assertNotNull( map.get( "A" ) );
    assertNotNull( map.get( "B" ) );
    map.put( "C", "3" );
    assertNotNull( map.get( "A" ) );
    assertNotNull( map.get( "B" ) );
    assertNotNull( map.get( "C" ) );
    map.put( "D", "3" );
    assertNotNull( map.get( "A" ) );
    assertNotNull( map.get( "B" ) );
    assertNotNull( map.get( "C" ) );
    assertNotNull( map.get( "D" ) );
    map.put( "E", "3" );
    assertNotNull( map.get( "A" ) );
    assertNotNull( map.get( "B" ) );
    assertNotNull( map.get( "C" ) );
    assertNotNull( map.get( "D" ) );
    assertNotNull( map.get( "E" ) );
    map.put( "F", "3" );
    assertNotNull( map.get( "A" ) );
    assertNotNull( map.get( "B" ) );
    assertNotNull( map.get( "C" ) );
    assertNotNull( map.get( "D" ) );
    assertNotNull( map.get( "E" ) );
    assertNotNull( map.get( "F" ) );
    map.put( "G", "3" );
    map.put( "H", "3" );
    map.put( "I", "3" );
    map.put( "J", "3" );
    map.put( "K", "3" );

    assertEquals( "Size", 11, map.size() );

    assertNotNull( map.get( "C" ) );
    assertNotNull( map.get( "D" ) );
    assertNotNull( map.get( "E" ) );
    assertNotNull( map.get( "F" ) );
    assertNotNull( map.get( "G" ) );
    assertNotNull( map.get( "H" ) );
    assertNotNull( map.get( "I" ) );
    assertNotNull( map.get( "J" ) );
    assertNotNull( map.get( "K" ) );
  }

}
