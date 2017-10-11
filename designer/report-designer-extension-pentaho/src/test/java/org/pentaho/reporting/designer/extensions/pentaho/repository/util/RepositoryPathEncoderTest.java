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

package org.pentaho.reporting.designer.extensions.pentaho.repository.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class RepositoryPathEncoderTest {

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void testEncode() {
    String encoded = RepositoryPathEncoder.encode( "http://seylermartialarts.com/index.html" );
    assertEquals( "http%3A%252F%252Fseylermartialarts.com%252Findex.html", encoded );
  }

  @Test
  public void testEncodeURIComponent() {
    String uri = RepositoryPathEncoder.encodeURIComponent( "http://seylermartialarts.com/index.html" );
    assertEquals( "http%3A%2F%2Fseylermartialarts.com%2Findex.html", uri );
  }

  @Test
  public void testEncodeRepositoryPath() {
    final String testPath = "http://seylermartialarts.com/index.html";
    String path = RepositoryPathEncoder.encodeRepositoryPath( testPath );
    assertNotSame( testPath, path );
  }

  @Test
  public void testDecodeRepositoryPath() {
    final String testPath = "http://seylermartialarts.com/index.html";
    final String desiredResult = "http///seylermartialarts.com/index.html";

    String decode = RepositoryPathEncoder.decodeRepositoryPath( testPath );
    assertEquals( desiredResult, decode );
  }

}
