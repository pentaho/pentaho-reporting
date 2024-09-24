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
