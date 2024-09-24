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
 * Copyright (c) 2001 - 2015 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.style;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;

/**
 * This test knows too much about {@linkplain ElementStyleSheet}. Namely, it is aware of internal flag-to-bit packing.
 * The goal of this test is check whether bit operations are done correctly for different amount of input keys.
 *
 * @author Andrey Khayrutdinov
 */
public class ElementStyleSheetKeysVaryingTest {

  @BeforeClass
  public static void init() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  private List<StyleKey> testKeys;
  private int defaultKeysAmount;

  @Before
  public void setUp() {
    testKeys = new ArrayList<>( 4 );
    defaultKeysAmount = StyleKey.getDefinedStyleKeysList().size();
  }

  @After
  public void tearDown() {
    for ( StyleKey testKey : testKeys ) {
      //noinspection deprecation
      StyleKey.removeTestKey( testKey.name );
    }
    testKeys = null;
  }


  @Test
  public void amountMod4Is1() {
    testAmountMod4IsN( 1 );
  }

  @Test
  public void amountMod4Is2() {
    testAmountMod4IsN( 2 );
  }

  @Test
  public void amountMod4Is3() {
    testAmountMod4IsN( 3 );
  }

  @Test
  public void amountMod4Is0() {
    testAmountMod4IsN( 4 );
  }

  private void testAmountMod4IsN( int n ) {
    final int toAdd = 4 - ( defaultKeysAmount % 4 ) + n;
    for ( int i = 0; i < toAdd; i++ ) {
      //noinspection deprecation
      StyleKey syntheticKey = StyleKey.addTestKey( "syntheticKey_" + i, String.class, false, false );
      testKeys.add( syntheticKey );
    }
    assertEquals( "Default key list's size % 4 should be equal " + ( n % 4 ),
      n % 4, StyleKey.getDefinedStyleKeysList().size() % 4 );

    ElementStyleSheet sheet = new ElementStyleSheet();
    for ( StyleKey key : testKeys ) {
      sheet.setStyleProperty( key, key.name );
    }
    for ( StyleKey key : testKeys ) {
      assertEquals( key.name, sheet.getStyleProperty( key ) );
      assertTrue( sheet.isLocalKey( key ) );
    }
  }
}
