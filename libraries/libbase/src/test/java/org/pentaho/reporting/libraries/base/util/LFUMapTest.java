/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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
