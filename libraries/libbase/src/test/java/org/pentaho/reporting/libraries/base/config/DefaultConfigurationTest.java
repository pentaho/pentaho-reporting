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