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

package org.pentaho.reporting.libraries.repository;

import junit.framework.TestCase;
import org.pentaho.reporting.libraries.repository.dummy.DummyContentLocation;
import org.pentaho.reporting.libraries.repository.dummy.DummyRepository;

import java.util.HashSet;

@SuppressWarnings( "HardCodedStringLiteral" )
public class PageSequenceNameGeneratorTest extends TestCase {
  private class TestLocation extends DummyContentLocation {
    private HashSet<String> names;

    /**
     * Creates a new root DummyContentLocation with the given repository and name.
     *
     * @param name the name of this location.
     */
    private TestLocation( final String name ) {
      super( new DummyRepository(), name );
      names = new HashSet<String>();
    }

    public void addExistingLocation( final String name ) {
      names.add( name );
    }

    /**
     * A dummy location does not have children, therefore this method always returns false.
     *
     * @param name the name of the item.
     * @return false.
     */
    public boolean exists( final String name ) {
      return names.contains( name );
    }
  }

  public PageSequenceNameGeneratorTest() {
  }

  public void testSequenceCounting() throws ContentIOException {
    final TestLocation testLocation = new TestLocation( "name" );
    final PageSequenceNameGenerator gen = new PageSequenceNameGenerator( testLocation, "test-file", "data" );

    assertEquals( "test-file-0.data", gen.generateName( null, "application/x-binary (not used)" ) );
    assertEquals( "test-file-1.data", gen.generateName( null, "application/x-binary (not used)" ) );
    assertEquals( "test-file-2.data", gen.generateName( null, "application/x-binary (not used)" ) );
  }

  public void testSequenceCountingError() throws ContentIOException {
    final TestLocation testLocation = new TestLocation( "name" );
    testLocation.addExistingLocation( "test-file-2.data" );
    final PageSequenceNameGenerator gen = new PageSequenceNameGenerator( testLocation, "test-file", "data" );

    assertEquals( "test-file-0.data", gen.generateName( null, "application/x-binary (not used)" ) );
    assertEquals( "test-file-1.data", gen.generateName( null, "application/x-binary (not used)" ) );
    try {
      assertEquals( "test-file-2.data", gen.generateName( null, "application/x-binary (not used)" ) );
      fail();
    } catch ( ContentIOException e ) {
      // expected
    }

    try {
      assertEquals( "test-file-2.data", gen.generateName( null, "application/x-binary (not used)" ) );
      fail();
      // continue to fail
    } catch ( ContentIOException e ) {
      // expected
    }
  }
}
