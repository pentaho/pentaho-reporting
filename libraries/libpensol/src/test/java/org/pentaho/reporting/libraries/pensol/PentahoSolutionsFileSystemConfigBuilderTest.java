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

package org.pentaho.reporting.libraries.pensol;

import static org.junit.Assert.*;

import org.apache.commons.vfs2.FileSystemOptions;
import org.junit.Before;
import org.junit.Test;

public class PentahoSolutionsFileSystemConfigBuilderTest {

  private PentahoSolutionsFileSystemConfigBuilder configBuilder;
  private FileSystemOptions opts;

  @Before
  public void setUp() {
    configBuilder = new PentahoSolutionsFileSystemConfigBuilder();
    opts = new FileSystemOptions();
  }

  @Test
  public void testConstructor() {
    assertNotNull( configBuilder );
  }

  @Test
  public void testSetAndGetTimeOut() {
    configBuilder.setTimeOut( opts, 30000 );
    assertEquals( 30000, configBuilder.getTimeOut( opts ) );
  }

  @Test
  public void testGetTimeOutDefault() {
    assertEquals( 0, configBuilder.getTimeOut( opts ) );
  }

  @Test
  public void testSetAndGetSessionId() {
    configBuilder.setSessionId( opts, "SESS_ABC123" );
    assertEquals( "SESS_ABC123", configBuilder.getSessionId( opts ) );
  }

  @Test
  public void testGetSessionIdDefault() {
    assertNull( configBuilder.getSessionId( opts ) );
  }

  @Test
  public void testSetSessionIdNull() {
    configBuilder.setSessionId( opts, null );
    assertNull( configBuilder.getSessionId( opts ) );
  }

  @Test
  public void testSetSessionIdOverwrite() {
    configBuilder.setSessionId( opts, "FIRST" );
    configBuilder.setSessionId( opts, "SECOND" );
    assertEquals( "SECOND", configBuilder.getSessionId( opts ) );
  }

  @Test
  public void testSetTimeOutOverwrite() {
    configBuilder.setTimeOut( opts, 1000 );
    configBuilder.setTimeOut( opts, 5000 );
    assertEquals( 5000, configBuilder.getTimeOut( opts ) );
  }

  @Test
  public void testDifferentOptionsIndependent() {
    FileSystemOptions opts2 = new FileSystemOptions();
    configBuilder.setSessionId( opts, "SESSION_A" );
    configBuilder.setSessionId( opts2, "SESSION_B" );
    assertEquals( "SESSION_A", configBuilder.getSessionId( opts ) );
    assertEquals( "SESSION_B", configBuilder.getSessionId( opts2 ) );
  }
}
