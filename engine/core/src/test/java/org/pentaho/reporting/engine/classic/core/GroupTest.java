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

import junit.framework.TestCase;

import java.util.Arrays;

public class GroupTest extends TestCase {
  public GroupTest( final String s ) {
    super( s );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testCreate() throws Exception {
    final RelationalGroup g1 = new RelationalGroup();
    assertNotNull( g1.clone() );
    assertNotNull( g1.getFields() );
    assertNotNull( g1.getFooter() );
    assertNotNull( g1.getHeader() );
    assertNotNull( g1.getName() );
    assertNotNull( g1.toString() );
  }

  public void testMethods() {
    final RelationalGroup g = new RelationalGroup();

    try {
      g.setHeader( null );
      fail();
    } catch ( NullPointerException npe ) {
      // expected, ignored
    }
    try {
      g.setFooter( null );
      fail();
    } catch ( NullPointerException npe ) {
      // expected, ignored
    }
    try {
      g.addField( null );
      fail();
    } catch ( NullPointerException npe ) {
      // expected, ignored
    }

  }

  private static RelationalGroup createGroup( String name, String[] fields ) {
    final RelationalGroup group = new RelationalGroup();
    group.setFields( Arrays.asList( fields ) );
    group.setName( name );
    return group;
  }

  public void testAddToReport() {
    MasterReport report = new MasterReport();
    report.getItemBand().setName( "ItemBand!" );
    report.addGroup( new RelationalGroup() );
    report.addGroup( createGroup( "second", new String[] { "field" } ) );
    report.addGroup( createGroup( "third", new String[] { "field", "field2" } ) );

    assertEquals( 4, report.getGroupCount() );
    assertEquals( "ItemBand!", report.getItemBand().getName() );
  }
}
