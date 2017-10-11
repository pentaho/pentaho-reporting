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

@SuppressWarnings( { "AutoBoxing", "HardCodedStringLiteral" } )
public class LFUMapTest extends TestCase {
  public LFUMapTest() {
  }

  public LFUMapTest( final String s ) {
    super( s );
  }

  public void testAdd() {
    final LFUMap<String, String> lfuMap = new LFUMap<String, String>( 10 );
    lfuMap.put( "1", "1" );
    lfuMap.validate();
    lfuMap.put( "2", "2" );
    lfuMap.validate();
    lfuMap.put( "3", "3" );
    lfuMap.validate();
    lfuMap.put( "4", "4" );
    lfuMap.validate();
    lfuMap.put( "1", "5" );
    lfuMap.validate();
    lfuMap.put( "3", "6" );
    lfuMap.validate();
    lfuMap.put( "4", "7" );
    lfuMap.validate();
    lfuMap.put( "2", "8" );
    lfuMap.validate();
  }

  public void testAdd2() {
    final LFUMap<String, String> lfuMap = new LFUMap<String, String>( 10 );
    lfuMap.put( "1", "1" );
    lfuMap.validate();
    lfuMap.put( "2", "2" );
    lfuMap.validate();
    lfuMap.put( "3", "3" );
    lfuMap.validate();
    lfuMap.put( "4", "4" );
    lfuMap.validate();
    lfuMap.put( "1", "5" );
    lfuMap.validate();
    lfuMap.put( "3", "6" );
    lfuMap.validate();
    lfuMap.put( "4", "7" );
    lfuMap.validate();
    lfuMap.put( "a2", "8" );
    lfuMap.validate();
    lfuMap.put( "a4", "4" );
    lfuMap.validate();
    lfuMap.put( "a1", "5" );
    lfuMap.validate();
    lfuMap.put( "a3", "6" );
    lfuMap.validate();
    lfuMap.put( "b4", "7" );
    lfuMap.validate();
    lfuMap.put( "b4", "4" );
    lfuMap.validate();
    lfuMap.put( "b1", "5" );
    lfuMap.validate();
    lfuMap.put( "b3", "6" );
    lfuMap.validate();
    lfuMap.put( "c4", "7" );
    lfuMap.validate();

    lfuMap.get( "a2" );
    lfuMap.validate();
    lfuMap.get( "a4" );
    lfuMap.validate();
    lfuMap.get( "a1" );
    lfuMap.validate();
    lfuMap.get( "a3" );
    lfuMap.validate();
    lfuMap.get( "b4" );
    lfuMap.validate();
    lfuMap.get( "b4" );
    lfuMap.validate();

  }

  public void testClone() throws CloneNotSupportedException {
    final LFUMap<String, String> lfuMap = new LFUMap<String, String>( 10 );
    assertNotNull( lfuMap.clone() );
    lfuMap.put( "1", "1" );
    lfuMap.validate();
    lfuMap.put( "2", "2" );
    lfuMap.validate();
    lfuMap.put( "3", "3" );
    lfuMap.validate();
    lfuMap.put( "4", "4" );
    lfuMap.validate();
    lfuMap.put( "1", "5" );
    lfuMap.validate();
    lfuMap.put( "3", "6" );
    lfuMap.validate();
    lfuMap.put( "4", "7" );
    lfuMap.validate();
    lfuMap.put( "a2", "8" );
    lfuMap.validate();
    lfuMap.put( "a4", "4" );
    lfuMap.validate();
    lfuMap.put( "a1", "5" );
    lfuMap.validate();
    lfuMap.put( "a3", "6" );
    lfuMap.validate();
    lfuMap.put( "b4", "7" );
    lfuMap.validate();
    lfuMap.put( "b4", "4" );
    lfuMap.validate();
    lfuMap.put( "b1", "5" );
    lfuMap.validate();
    lfuMap.put( "b3", "6" );
    lfuMap.validate();
    lfuMap.put( "c4", "7" );
    lfuMap.validate();

    assertNotNull( lfuMap.clone() );
  }

  public void testAddReplace() {
    final LFUMap<Integer, Integer> map = new LFUMap<Integer, Integer>( 10 );
    map.put( 1, 20 );
    assertEquals( (Integer) 20, map.get( 1 ) );
    map.put( 1, 25 );
    assertEquals( (Integer) 25, map.get( 1 ) );
  }
}
