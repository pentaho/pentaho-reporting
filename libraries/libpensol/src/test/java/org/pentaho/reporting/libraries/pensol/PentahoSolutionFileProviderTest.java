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
import static org.mockito.Mockito.mock;

import java.lang.reflect.Field;
import java.util.Collection;

import org.apache.commons.vfs2.Capability;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.impl.DefaultFileSystemManager;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;

public class PentahoSolutionFileProviderTest {

  @BeforeClass
  public static void initBoot() {
    LibPensolBoot.getInstance().start();
  }

  @After
  public void tearDown() {
    PentahoSessionHolder.removeSession();
  }

  @Test
  public void testGetCapabilities() {
    Collection<?> capabilities = PentahoSolutionFileProvider.capabilities;
    assertNotNull( capabilities );
    assertFalse( capabilities.isEmpty() );
    assertTrue( capabilities.contains( Capability.GET_TYPE ) );
    assertTrue( capabilities.contains( Capability.LIST_CHILDREN ) );
    assertTrue( capabilities.contains( Capability.READ_CONTENT ) );
    assertTrue( capabilities.contains( Capability.WRITE_CONTENT ) );
    assertTrue( capabilities.contains( Capability.CREATE ) );
    assertTrue( capabilities.contains( Capability.FS_ATTRIBUTES ) );
    assertTrue( capabilities.contains( Capability.URI ) );
    assertTrue( capabilities.contains( Capability.GET_LAST_MODIFIED ) );
  }

  @Test
  public void testAuthenticatorTypes() {
    assertNotNull( PentahoSolutionFileProvider.AUTHENTICATOR_TYPES );
    assertEquals( 2, PentahoSolutionFileProvider.AUTHENTICATOR_TYPES.length );
  }

  @Test
  public void testCapabilitiesAreUnmodifiable() {
    Collection<?> capabilities = PentahoSolutionFileProvider.capabilities;
    try {
      capabilities.clear();
      fail( "Expected UnsupportedOperationException" );
    } catch ( UnsupportedOperationException e ) {
      // Expected - collection is unmodifiable
    }
  }

  @Test
  public void testGetCapabilitiesInstanceMethod() {
    PentahoSolutionFileProvider provider = new PentahoSolutionFileProvider();
    Collection<?> caps = provider.getCapabilities();
    assertSame( PentahoSolutionFileProvider.capabilities, caps );
  }

  @Test
  public void testConstructorWithoutPentahoSessionDoesNotBypass() throws Exception {
    PentahoSessionHolder.removeSession();
    PentahoSolutionFileProvider provider = new PentahoSolutionFileProvider();
    assertFalse( readBypassFlag( provider ) );
  }

  @Test
  public void testConstructorWithPentahoSessionEnablesBypass() throws Exception {
    AutoCloseable session = () -> PentahoSessionHolder.removeSession();
    PentahoSessionHolder.setSession( mock( IPentahoSession.class ) );
    try ( session ) {
      PentahoSolutionFileProvider provider = new PentahoSolutionFileProvider();
      assertTrue( readBypassFlag( provider ) );
    }
  }

  // ---- doCreateFileSystem coverage via DefaultFileSystemManager ----

  @Test
  public void testDoCreateFileSystemJcrSchemeUsernamePass() throws Exception {
    try ( DefaultFileSystemManager mgr = newManager() ) {
      // Use a port that is not listening; the FileSystem is created lazily on first
      // operation, so simply *resolving* exercises doCreateFileSystem successfully.
      FileObject fo = mgr.resolveFile( "jcr-solution:http://u:p@127.0.0.1:1/pentaho/!/" );
      assertNotNull( fo );
    }
  }

  // Note: the JCR-direct (bypass) branch instantiates JCRSolutionDirectFileModel
  // which has a runtime dependency on Kettle's org.pentaho.di.core.bowl.Bowl that
  // is not present on this module's test classpath; covered by integration tests.

  @Test
  public void testDoCreateFileSystemWebScheme() throws Exception {
    try ( DefaultFileSystemManager mgr = newManager() ) {
      FileObject fo = mgr.resolveFile( "web-solution:http://u:p@127.0.0.1:1/pentaho/!/" );
      assertNotNull( fo );
    }
  }

  @Test
  public void testDoCreateFileSystemJcrWithSessionIdOption() throws Exception {
    try ( DefaultFileSystemManager mgr = newManager() ) {
      FileSystemOptions opts = new FileSystemOptions();
      new PentahoSolutionsFileSystemConfigBuilder().setSessionId( opts, "SESS_ABC" );
      FileObject fo = mgr.resolveFile( "jcr-solution:http://127.0.0.1:1/pentaho/!/", opts );
      assertNotNull( fo );
    }
  }

  @Test
  public void testDoCreateFileSystemWebWithSessionIdOption() throws Exception {
    try ( DefaultFileSystemManager mgr = newManager() ) {
      FileSystemOptions opts = new FileSystemOptions();
      new PentahoSolutionsFileSystemConfigBuilder().setSessionId( opts, "SESS_ABC" );
      FileObject fo = mgr.resolveFile( "web-solution:http://127.0.0.1:1/pentaho/!/", opts );
      assertNotNull( fo );
    }
  }

  // ---- direct private method coverage ----

  // Note: createJCRDirectFileSystem similarly requires Kettle's Bowl class and
  // is therefore not unit-testable from this module.

  // ---- createWebFileSystem with credentials (no session ID) ----

  @Test
  public void testDoCreateFileSystemWebWithCredentials() throws Exception {
    try ( DefaultFileSystemManager mgr = newManager() ) {
      FileObject fo = mgr.resolveFile( "web-solution:http://admin:secret@127.0.0.1:1/pentaho/!/" );
      assertNotNull( fo );
    }
  }

  @Test
  public void testDoCreateFileSystemWebNoCredentials() throws Exception {
    try ( DefaultFileSystemManager mgr = newManager() ) {
      // No username/password in the URL → empty credentials path
      FileObject fo = mgr.resolveFile( "web-solution:http://127.0.0.1:1/pentaho/!/" );
      assertNotNull( fo );
    }
  }

  @Test
  public void testDoCreateFileSystemJcrWithEmptySessionId() throws Exception {
    try ( DefaultFileSystemManager mgr = newManager() ) {
      FileSystemOptions opts = new FileSystemOptions();
      new PentahoSolutionsFileSystemConfigBuilder().setSessionId( opts, "" );
      // Empty sessionId → falls back to username/password path
      FileObject fo = mgr.resolveFile( "jcr-solution:http://u:p@127.0.0.1:1/pentaho/!/", opts );
      assertNotNull( fo );
    }
  }

  @Test
  public void testDoCreateFileSystemWebWithEmptySessionId() throws Exception {
    try ( DefaultFileSystemManager mgr = newManager() ) {
      FileSystemOptions opts = new FileSystemOptions();
      new PentahoSolutionsFileSystemConfigBuilder().setSessionId( opts, "" );
      FileObject fo = mgr.resolveFile( "web-solution:http://u:p@127.0.0.1:1/pentaho/!/", opts );
      assertNotNull( fo );
    }
  }

  // ---- helpers ----

  private static boolean readBypassFlag( PentahoSolutionFileProvider provider ) throws Exception {
    Field f = PentahoSolutionFileProvider.class.getDeclaredField( "bypassAuthentication" );
    f.setAccessible( true );
    return (Boolean) f.get( provider );
  }

  private static DefaultFileSystemManager newManager() throws org.apache.commons.vfs2.FileSystemException {
    DefaultFileSystemManager mgr = new DefaultFileSystemManager();
    PentahoSolutionFileProvider provider = new PentahoSolutionFileProvider();
    mgr.addProvider( "jcr-solution", provider );
    mgr.addProvider( "web-solution", provider );
    @SuppressWarnings( { "deprecation", "java:S1874" } )
    org.apache.commons.vfs2.provider.FileProvider httpProvider =
      new org.apache.commons.vfs2.provider.http.HttpFileProvider();
    mgr.addProvider( "http", httpProvider );
    mgr.init();
    return mgr;
  }
}
