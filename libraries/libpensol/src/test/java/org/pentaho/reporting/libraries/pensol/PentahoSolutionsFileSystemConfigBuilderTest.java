/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/


package org.pentaho.reporting.libraries.pensol;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.apache.commons.vfs2.FileSystemOptions;
import org.junit.Test;

public class PentahoSolutionsFileSystemConfigBuilderTest {

  @Test
  public void testGetSessionIdNotSetReturnsNull() {
    final PentahoSolutionsFileSystemConfigBuilder builder =
        new PentahoSolutionsFileSystemConfigBuilder();
    final FileSystemOptions opts = new FileSystemOptions();
    assertNull( builder.getSessionId( opts ) );
  }

  @Test
  public void testSetAndGetSessionIdReturnsValue() {
    final PentahoSolutionsFileSystemConfigBuilder builder =
        new PentahoSolutionsFileSystemConfigBuilder();
    final FileSystemOptions opts = new FileSystemOptions();
    builder.setSessionId( opts, "my-session-id" );
    assertEquals( "my-session-id", builder.getSessionId( opts ) );
  }

  @Test
  public void testSetSessionIdNullReturnsNull() {
    final PentahoSolutionsFileSystemConfigBuilder builder =
        new PentahoSolutionsFileSystemConfigBuilder();
    final FileSystemOptions opts = new FileSystemOptions();
    builder.setSessionId( opts, null );
    assertNull( builder.getSessionId( opts ) );
  }

  @Test
  public void testGetTimeOutNotSetReturnsZero() {
    final PentahoSolutionsFileSystemConfigBuilder builder =
        new PentahoSolutionsFileSystemConfigBuilder();
    final FileSystemOptions opts = new FileSystemOptions();
    assertEquals( 0, builder.getTimeOut( opts ) );
  }

  @Test
  public void testSetAndGetTimeOutReturnsConfiguredValue() {
    final PentahoSolutionsFileSystemConfigBuilder builder =
        new PentahoSolutionsFileSystemConfigBuilder();
    final FileSystemOptions opts = new FileSystemOptions();
    builder.setTimeOut( opts, 12345 );
    assertEquals( 12345, builder.getTimeOut( opts ) );
  }
}
