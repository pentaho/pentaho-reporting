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
 * Copyright (c) 2000 - 2017 Hitachi Vantara, Simba Management Limited and Contributors...  All rights reserved.
 */

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
