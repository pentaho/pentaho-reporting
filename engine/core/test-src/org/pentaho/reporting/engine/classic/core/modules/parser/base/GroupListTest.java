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
* Copyright (c) 2000 - 2013 Pentaho Corporation, Simba Management Limited and Contributors...  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.core.modules.parser.base;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;

import java.util.Arrays;

public class GroupListTest extends TestCase {
  public GroupListTest( final String s ) {
    super( s );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testCreate()
    throws CloneNotSupportedException {
    final GroupList gl = new GroupList();
    gl.clear();
    assertNotNull( gl.clone() );
    assertNotNull( gl.iterator() );
    assertNotNull( gl.toString() );
  }

  public void testMethods() {
    final GroupList gl = new GroupList();
    try {
      gl.add( null );
    } catch ( NullPointerException npe ) {
      // expected, ignored
    }

    final RelationalGroup g1 = new RelationalGroup();
    gl.add( g1 );
    gl.add( g1 );
    assertTrue( gl.size() == 1 ); // the old instance gets removed and replaced by the new group
    gl.add( new RelationalGroup() );
    assertTrue( gl.size() == 1 ); // the old instance gets removed and replaced by the new group

    final RelationalGroup g2 = new RelationalGroup();
    g2.addField( "Test" );

    final RelationalGroup g3 = new RelationalGroup();
    g3.addField( "Failure" );

    // group g2 and g3 are unreleated, g2 is no child or parent of g3
    gl.add( g2 );
    try {
      gl.add( g3 );
      fail();
    } catch ( IllegalArgumentException iea ) {
      // expected, ignored
    }
    assertEquals( 2, gl.size() );
  }


  private static RelationalGroup createGroup( String name, String[] fields ) {
    final RelationalGroup group = new RelationalGroup();
    group.setFields( Arrays.asList( fields ) );
    group.setName( name );
    return group;
  }

  public void testAddToReport() throws ParseException {
    MasterReport report = new MasterReport();
    report.getItemBand().setName( "ItemBand!" );

    final GroupList gl = new GroupList();
    gl.add( new RelationalGroup() );
    gl.add( createGroup( "second", new String[] { "field" } ) );
    gl.add( createGroup( "third", new String[] { "field", "field2" } ) );
    gl.installIntoReport( report );

    // Only 3 groups, as the default group is replaced.
    assertEquals( 3, report.getGroupCount() );
    assertEquals( "ItemBand!", report.getItemBand().getName() );

  }
}
