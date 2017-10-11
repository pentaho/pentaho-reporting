/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.libraries.base.config;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;

import static org.junit.Assert.*;

/**
 * @author Andrey Khayrutdinov
 */
public class DefaultConfigurationTest {

  private DefaultConfiguration cfg;

  @Before
  public void setUp() throws Exception {
    cfg = new DefaultConfiguration();
  }


  @Test
  public void findPropertyKeys_EmptyIterator_WhenEmptyConfig() {
    assertFalse( cfg.findPropertyKeys( "" ).hasNext() );
  }

  @Test
  public void findPropertyKeys_EmptyIterator_WhenDoesNotMatch() {
    cfg.setConfigProperty( "a", "a" );
    assertFalse( cfg.findPropertyKeys( "q" ).hasNext() );
  }


  @Test
  public void findPropertyKeys_ReturnsInSortedOrder() {
    cfg.setConfigProperty( "b", "b" );
    cfg.setConfigProperty( "a", "a" );
    cfg.setConfigProperty( "c", "c" );

    Iterator<String> keys = cfg.findPropertyKeys( "" );
    assertReturnedSeries( keys, "a", "b", "c" );
  }

  @Test
  public void findPropertyKeys_ReturnsValuesStartedWithPrefix() {
    cfg.setConfigProperty( "a", "a" );
    cfg.setConfigProperty( "b", "b" );
    cfg.setConfigProperty( "aa", "aa" );

    Iterator<String> keys = cfg.findPropertyKeys( "a" );
    assertReturnedSeries( keys, "a", "aa" );
  }

  private void assertReturnedSeries( Iterator<String> iterator, String... expected ) {
    int cnt = 0;
    while ( iterator.hasNext() && cnt < expected.length ) {
      assertEquals( expected[ cnt++ ], iterator.next() );
    }
    assertFalse(
      String.format( "Elements checked: %d\nArray:\n\t%s\n", cnt, Arrays.toString( expected ) ),
      iterator.hasNext()
    );
  }

}