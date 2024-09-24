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

package org.pentaho.reporting.engine.classic.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.libraries.xmlns.common.AttributeMap;

/**
 * @author Andrey Khayrutdinov
 */
public class ReportAttributeMapTest {

  private ReportAttributeMap<String> map;

  @Before
  public void setUp() {
    map = new ReportAttributeMap<String>();
  }

  @After
  public void tearDown() {
    map = null;
  }


  @Test( expected = UnsupportedOperationException.class )
  public void unmodifiable_setAttribute() {
    map.createUnmodifiableMap().setAttribute( "namespace", "attribute", "value" );
  }

  @Test( expected = UnsupportedOperationException.class )
  public void unmodifiable_putAll() {
    map.createUnmodifiableMap().putAll( new AttributeMap<String>() );
  }

  @Test
  public void unmodifiable_isReadOnly() {
    assertTrue( map.createUnmodifiableMap().isReadOnly() );
  }


  @Test
  public void ordinal_isNotReadOnly() {
    assertFalse( map.isReadOnly() );
  }

  @Test
  public void ordinal_setAttribute() {
    assertNull( map.setAttribute( "namespace", "attribute", "value" ) );
    assertEquals( 1, map.getChangeTracker() );
  }

  @Test
  public void ordinal_setAttribute_DoesNotIncrementCounterForSameValues() {
    assertNull( map.setAttribute( "namespace", "attribute", "value" ) );
    assertEquals( "value", map.setAttribute( "namespace", "attribute", "value" ) );
    assertEquals( 1, map.getChangeTracker() );
  }

  @Test
  public void ordinal_putAll() {
    ReportAttributeMap<String> another = new ReportAttributeMap<>();
    another.setAttribute( "namespace1", "attribute1", "value1" );
    another.setAttribute( "namespace1", "attribute2", "value2" );
    another.setAttribute( "namespace2", "attribute1", "value1" );
    another.setAttribute( "namespace2", "attribute2", "value2" );
    map.putAll( another );

    assertEquals( 1, map.getChangeTracker() );
    assertEquals( 4, map.keySet().size() );
  }

  @Test
  public void ordinal_putAll_AlwaysIncrementsCounter() {
    ReportAttributeMap<String> another = new ReportAttributeMap<>();
    another.setAttribute( "namespace", "attribute", "value" );
    map.putAll( another );
    map.putAll( another );

    assertEquals( 2, map.getChangeTracker() );
    assertEquals( 1, map.keySet().size() );
  }

}
