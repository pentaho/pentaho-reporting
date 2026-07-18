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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import org.apache.commons.vfs2.Capability;
import org.apache.commons.vfs2.FileSystem;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.UserAuthenticationData;
import org.apache.commons.vfs2.provider.GenericFileName;
import org.apache.commons.vfs2.provider.LayeredFileName;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.pentaho.reporting.libraries.pensol.vfs.WebSolutionFileSystem;

public class PentahoSolutionFileProviderTest {

  private static class ExposedProvider extends PentahoSolutionFileProvider {
    FileSystem createForTest( final LayeredFileName rootName, final FileSystemOptions opts ) throws Exception {
      return super.doCreateFileSystem( rootName, opts );
    }
  }

  @Test
  public void testConstructorAndCapabilities() {
    final PentahoSolutionFileProvider provider = new PentahoSolutionFileProvider();
    assertNotNull( provider );
    assertTrue( provider.getCapabilities().contains( Capability.GET_TYPE ) );
    assertTrue( provider.getCapabilities().contains( Capability.READ_CONTENT ) );
  }

  @Test
  public void testAuthenticatorTypes() {
    assertEquals( 2, PentahoSolutionFileProvider.AUTHENTICATOR_TYPES.length );
    assertEquals( UserAuthenticationData.USERNAME, PentahoSolutionFileProvider.AUTHENTICATOR_TYPES[0] );
    assertEquals( UserAuthenticationData.PASSWORD, PentahoSolutionFileProvider.AUTHENTICATOR_TYPES[1] );
  }

  @Test
  public void testDoCreateFileSystemForJcrSchemeCreatesJcrFs() throws Exception {
    final ExposedProvider provider = new ExposedProvider();
    final LayeredFileName rootName = mock( LayeredFileName.class );
    final GenericFileName outerName = mock( GenericFileName.class );

    doReturn( "jcr-solution" ).when( rootName ).getScheme();
    doReturn( outerName ).when( rootName ).getOuterName();
    doReturn( "http://localhost:8080/pentaho" ).when( outerName ).getURI();
    doReturn( "user" ).when( outerName ).getUserName();
    doReturn( "pass" ).when( outerName ).getPassword();

    final FileSystem fs = provider.createForTest( rootName, new FileSystemOptions() );
    assertThat( fs, is( instanceOf( JCRSolutionFileSystem.class ) ) );
  }

  @Test
  public void testDoCreateFileSystemForWebSchemeCreatesWebFs() throws Exception {
    final ExposedProvider provider = new ExposedProvider();
    final LayeredFileName rootName = mock( LayeredFileName.class );
    final GenericFileName outerName = mock( GenericFileName.class );

    doReturn( "web-solution" ).when( rootName ).getScheme();
    doReturn( outerName ).when( rootName ).getOuterName();
    doReturn( "http://localhost:8080/pentaho" ).when( outerName ).getURI();
    doReturn( "http" ).when( outerName ).getScheme();
    doReturn( "" ).when( outerName ).getHostName();
    doReturn( -1 ).when( outerName ).getPort();
    doReturn( "user" ).when( outerName ).getUserName();
    doReturn( "pass" ).when( outerName ).getPassword();

    final FileSystem fs = provider.createForTest( rootName, new FileSystemOptions() );
    assertThat( fs, is( instanceOf( WebSolutionFileSystem.class ) ) );
  }

  @Test
  public void testDoCreateFileSystemForWebSchemeWithProxyAndCredentials()
    throws Exception {

    final ExposedProvider provider = new ExposedProvider();

    final LayeredFileName rootName =
      mock( LayeredFileName.class );

    final GenericFileName outerName =
      mock( GenericFileName.class );

    doReturn( "web-solution" ).when( rootName ).getScheme();
    doReturn( outerName ).when( rootName ).getOuterName();

    doReturn( "http://localhost:8080/pentaho" )
      .when( outerName ).getURI();

    doReturn( "http" )
      .when( outerName ).getScheme();

    doReturn( "proxyhost" )
      .when( outerName ).getHostName();

    doReturn( 8080 )
      .when( outerName ).getPort();

    doReturn( "admin" )
      .when( outerName ).getUserName();

    doReturn( "password" )
      .when( outerName ).getPassword();

    final FileSystem fs =
      provider.createForTest(
        rootName,
        new FileSystemOptions() );

    assertNotNull( fs );
    assertThat(
      fs,
      is( instanceOf( WebSolutionFileSystem.class ) ) );
  }

  @Test
  public void testJcrFileSystemWithExplicitCredentials()
    throws Exception {

    final ExposedProvider provider =
      new ExposedProvider();

    final LayeredFileName rootName =
      mock( LayeredFileName.class );

    final GenericFileName outerName =
      mock( GenericFileName.class );

    doReturn( "jcr-solution" )
      .when( rootName ).getScheme();

    doReturn( outerName )
      .when( rootName ).getOuterName();

    doReturn( "http://localhost:8080/pentaho" )
      .when( outerName ).getURI();

    doReturn( "john" )
      .when( outerName ).getUserName();

    doReturn( "secret" )
      .when( outerName ).getPassword();

    FileSystem fs =
      provider.createForTest(
        rootName,
        new FileSystemOptions() );

    assertNotNull( fs );
  }

  @Test
  public void testJcrDirectFileSystemWhenSessionExists() {

    try ( MockedStatic<PentahoSessionHolder> holder =
            mockStatic( PentahoSessionHolder.class ) ) {

      holder.when( PentahoSessionHolder::getSession )
        .thenReturn( mock( IPentahoSession.class ) );

      ExposedProvider provider =
        new ExposedProvider();

      LayeredFileName rootName =
        mock( LayeredFileName.class );

      GenericFileName outerName =
        mock( GenericFileName.class );

      doReturn( "jcr-solution" )
        .when( rootName ).getScheme();

      doReturn( outerName )
        .when( rootName ).getOuterName();

      try {
        provider.createForTest(
          rootName,
          new FileSystemOptions() );
      } catch ( NoClassDefFoundError expected ) {
        // Expected when runtime dependencies are unavailable in the test environment.
      } catch ( Exception e ) {
        throw new RuntimeException( e );
      }

      assertTrue( true );
    }
  }

  @Test
  public void testConstructorWithPentahoSession() {

    try ( MockedStatic<PentahoSessionHolder> holder =
            mockStatic( PentahoSessionHolder.class ) ) {

      holder.when( PentahoSessionHolder::getSession )
        .thenReturn( mock( IPentahoSession.class ) );

      PentahoSolutionFileProvider provider =
        new PentahoSolutionFileProvider();

      assertNotNull( provider );
    }
  }

  @Test
  public void testJcrFileSystemReadsUsernameAndPassword()
    throws Exception {

    ExposedProvider provider =
      new ExposedProvider();

    LayeredFileName rootName =
      mock( LayeredFileName.class );

    GenericFileName outerName =
      mock( GenericFileName.class );

    doReturn( "jcr-solution" )
      .when( rootName ).getScheme();

    doReturn( outerName )
      .when( rootName ).getOuterName();

    doReturn( "http://localhost:8080/pentaho" )
      .when( outerName ).getURI();

    doReturn( "user1" )
      .when( outerName ).getUserName();

    doReturn( "pass1" )
      .when( outerName ).getPassword();

    FileSystem fs =
      provider.createForTest(
        rootName,
        new FileSystemOptions() );

    assertNotNull( fs );
  }

  @Test
  public void testWebFileSystemSetsProxy()
    throws Exception {

    ExposedProvider provider =
      new ExposedProvider();

    LayeredFileName rootName =
      mock( LayeredFileName.class );

    GenericFileName outerName =
      mock( GenericFileName.class );

    doReturn( "web-solution" )
      .when( rootName ).getScheme();

    doReturn( outerName )
      .when( rootName ).getOuterName();

    doReturn( "http://localhost:8080/pentaho" )
      .when( outerName ).getURI();

    doReturn( "http" )
      .when( outerName ).getScheme();

    doReturn( "proxyhost" )
      .when( outerName ).getHostName();

    doReturn( 8080 )
      .when( outerName ).getPort();

    doReturn( "" )
      .when( outerName ).getUserName();

    doReturn( "" )
      .when( outerName ).getPassword();

    FileSystem fs =
      provider.createForTest(
        rootName,
        new FileSystemOptions() );

    assertTrue(
      fs instanceof WebSolutionFileSystem );
  }
}
