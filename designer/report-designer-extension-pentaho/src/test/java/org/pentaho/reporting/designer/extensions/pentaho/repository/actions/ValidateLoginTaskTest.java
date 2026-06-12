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

package org.pentaho.reporting.designer.extensions.pentaho.repository.actions;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystem;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.VFS;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;
import org.pentaho.reporting.designer.extensions.pentaho.repository.util.PublishUtil;
import org.pentaho.reporting.libraries.pensol.JCRSolutionFileModel;
import org.pentaho.reporting.libraries.pensol.JCRSolutionFileSystem;
import org.pentaho.reporting.libraries.pensol.SolutionFileModel;
import org.pentaho.reporting.libraries.pensol.vfs.WebSolutionFileSystem;

import java.io.IOException;

public class ValidateLoginTaskTest {

  private static LoginTask mockLoginTask( final AuthenticationData loginData ) {
    final LoginTask task = mock( LoginTask.class );
    when( task.getLoginData() ).thenReturn( loginData );
    return task;
  }

  @Test
  public void testConstructorGetsLoginDataFromLoginTask() {
    final AuthenticationData data = mock( AuthenticationData.class );
    final ValidateLoginTask task = new ValidateLoginTask( mockLoginTask( data ) );
    assertNull( task.getException() );
    assertFalse( task.isLoginComplete() );
  }

  @Test
  public void testValidateLoginDataNullLoginDataReturnsTrue() throws Exception {
    final ValidateLoginTask task = new ValidateLoginTask( mockLoginTask( null ) );
    assertTrue( task.validateLoginData() );
  }

  @Test
  public void testRunNullLoginDataSetsLoginCompleteTrue() {
    final ValidateLoginTask task = new ValidateLoginTask( mockLoginTask( null ) );
    task.run();
    assertTrue( task.isLoginComplete() );
    assertNull( task.getException() );
  }

  @Test
  public void testValidateLoginDataWebFsRecentRefreshDoesNotCallRefresh() throws Exception {
    final AuthenticationData loginData = mock( AuthenticationData.class );
    final SolutionFileModel fileModel = mock( SolutionFileModel.class );
    final WebSolutionFileSystem wfs = mock( WebSolutionFileSystem.class );
    final FileObject vfsConn = mock( FileObject.class );

    when( vfsConn.getFileSystem() ).thenReturn( wfs );
    when( wfs.getLocalFileModel() ).thenReturn( fileModel );
    when( wfs.getAttribute( WebSolutionFileSystem.LAST_REFRESH_TIME_ATTRIBUTE ) )
        .thenReturn( System.currentTimeMillis() );

    try ( MockedStatic<VFS> vfsMock = mockStatic( VFS.class );
          MockedStatic<PublishUtil> puMock = mockStatic( PublishUtil.class ) ) {
      vfsMock.when( VFS::getManager ).thenReturn( mock( FileSystemManager.class ) );
      puMock.when( () -> PublishUtil.createVFSConnection( any(), any() ) ).thenReturn( vfsConn );

      final ValidateLoginTask task = new ValidateLoginTask( mockLoginTask( loginData ) );
      assertTrue( task.validateLoginData() );
      verify( fileModel, never() ).refresh();
    }
  }

  @Test
  public void testValidateLoginDataRefreshThrowsIOExceptionReturnsFalse()
    throws Exception {

    AuthenticationData loginData = mock( AuthenticationData.class );

    SolutionFileModel fileModel =
      mock( SolutionFileModel.class );

    WebSolutionFileSystem wfs =
      mock( WebSolutionFileSystem.class );

    FileObject vfsConn =
      mock( FileObject.class );

    when( vfsConn.getFileSystem() )
      .thenReturn( wfs );

    when( wfs.getLocalFileModel() )
      .thenReturn( fileModel );

    when( wfs.getAttribute( any() ) )
      .thenReturn( null ); // force refresh

    doThrow( new IOException( "io failure" ) )
      .when( fileModel )
      .refresh();

    try ( MockedStatic<VFS> vfsMock =
            mockStatic( VFS.class );
          MockedStatic<PublishUtil> puMock =
            mockStatic( PublishUtil.class ) ) {

      vfsMock.when( VFS::getManager )
        .thenReturn( mock( FileSystemManager.class ) );

      puMock.when(
          () -> PublishUtil.createVFSConnection(
            any(), any() ) )
        .thenReturn( vfsConn );

      ValidateLoginTask task =
        new ValidateLoginTask(
          mockLoginTask( loginData ) );

      assertFalse(
        task.validateLoginData() );
    }
  }

  @Test
  public void testValidateLoginDataJcrFsStaleRefreshCallsRefresh() throws Exception {

    AuthenticationData loginData = mock( AuthenticationData.class );

    JCRSolutionFileModel fileModel =
      mock( JCRSolutionFileModel.class );

    JCRSolutionFileSystem jcrFs =
      mock( JCRSolutionFileSystem.class );

    FileObject vfsConn =
      mock( FileObject.class );

    when( vfsConn.getFileSystem() )
      .thenReturn( jcrFs );

    when( jcrFs.getLocalFileModel() )
      .thenReturn( fileModel );

    when(
      jcrFs.getAttribute(
        JCRSolutionFileSystem.LAST_REFRESH_TIME_ATTRIBUTE ) )
      .thenReturn(
        System.currentTimeMillis() - 10000L );

    try ( MockedStatic<VFS> vfsMock =
            mockStatic( VFS.class );
          MockedStatic<PublishUtil> puMock =
            mockStatic( PublishUtil.class ) ) {

      vfsMock.when( VFS::getManager )
        .thenReturn( mock( FileSystemManager.class ) );

      puMock.when(
          () -> PublishUtil.createVFSConnection(
            any(), any() ) )
        .thenReturn( vfsConn );

      ValidateLoginTask task =
        new ValidateLoginTask(
          mockLoginTask( loginData ) );

      assertTrue( task.validateLoginData() );

      verify( fileModel ).refresh();
    }
  }

  @Test
  public void testValidateLoginDataWebFsStaleRefreshCallsRefresh() throws Exception {
    final AuthenticationData loginData = mock( AuthenticationData.class );
    final SolutionFileModel fileModel = mock( SolutionFileModel.class );
    final WebSolutionFileSystem wfs = mock( WebSolutionFileSystem.class );
    final FileObject vfsConn = mock( FileObject.class );

    when( vfsConn.getFileSystem() ).thenReturn( wfs );
    when( wfs.getLocalFileModel() ).thenReturn( fileModel );
    // Return a very old timestamp so refresh IS triggered
    when( wfs.getAttribute( WebSolutionFileSystem.LAST_REFRESH_TIME_ATTRIBUTE ) )
        .thenReturn( System.currentTimeMillis() - 10_000L );

    try ( MockedStatic<VFS> vfsMock = mockStatic( VFS.class );
          MockedStatic<PublishUtil> puMock = mockStatic( PublishUtil.class ) ) {
      vfsMock.when( VFS::getManager ).thenReturn( mock( FileSystemManager.class ) );
      puMock.when( () -> PublishUtil.createVFSConnection( any(), any() ) ).thenReturn( vfsConn );

      final ValidateLoginTask task = new ValidateLoginTask( mockLoginTask( loginData ) );
      assertTrue( task.validateLoginData() );
      verify( fileModel ).refresh();
    }
  }

  @Test
  public void testValidateLoginDataWebFsNullLastRefreshCallsRefresh() throws Exception {
    final AuthenticationData loginData = mock( AuthenticationData.class );
    final SolutionFileModel fileModel = mock( SolutionFileModel.class );
    final WebSolutionFileSystem wfs = mock( WebSolutionFileSystem.class );
    final FileObject vfsConn = mock( FileObject.class );

    when( vfsConn.getFileSystem() ).thenReturn( wfs );
    when( wfs.getLocalFileModel() ).thenReturn( fileModel );
    // getAttribute returns null → force refresh
    when( wfs.getAttribute( any() ) ).thenReturn( null );

    try ( MockedStatic<VFS> vfsMock = mockStatic( VFS.class );
          MockedStatic<PublishUtil> puMock = mockStatic( PublishUtil.class ) ) {
      vfsMock.when( VFS::getManager ).thenReturn( mock( FileSystemManager.class ) );
      puMock.when( () -> PublishUtil.createVFSConnection( any(), any() ) ).thenReturn( vfsConn );

      final ValidateLoginTask task = new ValidateLoginTask( mockLoginTask( loginData ) );
      assertTrue( task.validateLoginData() );
      verify( fileModel ).refresh();
    }
  }

  @Test
  public void testValidateLoginDataWebFsgetAttributeThrowsFseforcesRefresh() throws Exception {
    final AuthenticationData loginData = mock( AuthenticationData.class );
    final SolutionFileModel fileModel = mock( SolutionFileModel.class );
    final WebSolutionFileSystem wfs = mock( WebSolutionFileSystem.class );
    final FileObject vfsConn = mock( FileObject.class );

    when( vfsConn.getFileSystem() ).thenReturn( wfs );
    when( wfs.getLocalFileModel() ).thenReturn( fileModel );
    when( wfs.getAttribute( any() ) ).thenThrow( new FileSystemException( "unsupported" ) );

    try ( MockedStatic<VFS> vfsMock = mockStatic( VFS.class );
          MockedStatic<PublishUtil> puMock = mockStatic( PublishUtil.class ) ) {
      vfsMock.when( VFS::getManager ).thenReturn( mock( FileSystemManager.class ) );
      puMock.when( () -> PublishUtil.createVFSConnection( any(), any() ) ).thenReturn( vfsConn );

      final ValidateLoginTask task = new ValidateLoginTask( mockLoginTask( loginData ) );
      assertTrue( task.validateLoginData() );
      verify( fileModel ).refresh();
    }
  }

  @Test
  public void testValidateLoginDataWebFsRefreshThrowsRuntimeExceptionWrapsInFse() throws Exception {
    final AuthenticationData loginData = mock( AuthenticationData.class );
    final SolutionFileModel fileModel = mock( SolutionFileModel.class );
    final WebSolutionFileSystem wfs = mock( WebSolutionFileSystem.class );
    final FileObject vfsConn = mock( FileObject.class );

    when( vfsConn.getFileSystem() ).thenReturn( wfs );
    when( wfs.getLocalFileModel() ).thenReturn( fileModel );
    when( wfs.getAttribute( any() ) ).thenReturn( null ); // force refresh
    doThrow( new RuntimeException( "HTTP 401" ) ).when( fileModel ).refresh();

    try ( MockedStatic<VFS> vfsMock = mockStatic( VFS.class );
          MockedStatic<PublishUtil> puMock = mockStatic( PublishUtil.class ) ) {
      vfsMock.when( VFS::getManager ).thenReturn( mock( FileSystemManager.class ) );
      puMock.when( () -> PublishUtil.createVFSConnection( any(), any() ) ).thenReturn( vfsConn );

      final ValidateLoginTask task = new ValidateLoginTask( mockLoginTask( loginData ) );
      try {
        task.validateLoginData();
        // Should have thrown FileSystemException
        assertFalse( "Expected FileSystemException", true );
      } catch ( FileSystemException expected ) {
        assertNotNull( expected.getCause() );
      }
    }
  }

  @Test
  public void testValidateLoginDatajcrFsnullAttributecallsRefresh() throws Exception {
    final AuthenticationData loginData = mock( AuthenticationData.class );
    final JCRSolutionFileModel fileModel = mock( JCRSolutionFileModel.class );
    final JCRSolutionFileSystem jcrFs = mock( JCRSolutionFileSystem.class );
    final FileObject vfsConn = mock( FileObject.class );

    when( vfsConn.getFileSystem() ).thenReturn( jcrFs );
    when( jcrFs.getLocalFileModel() ).thenReturn( fileModel );
    when( jcrFs.getAttribute( any() ) ).thenReturn( null );

    try ( MockedStatic<VFS> vfsMock = mockStatic( VFS.class );
          MockedStatic<PublishUtil> puMock = mockStatic( PublishUtil.class ) ) {
      vfsMock.when( VFS::getManager ).thenReturn( mock( FileSystemManager.class ) );
      puMock.when( () -> PublishUtil.createVFSConnection( any(), any() ) ).thenReturn( vfsConn );

      final ValidateLoginTask task = new ValidateLoginTask( mockLoginTask( loginData ) );
      assertTrue( task.validateLoginData() );
      verify( fileModel ).refresh();
    }
  }

  @Test
  public void testValidateLoginDatajcrFsrecentTimestampdoesNotRefresh() throws Exception {
    final AuthenticationData loginData = mock( AuthenticationData.class );
    final JCRSolutionFileModel fileModel = mock( JCRSolutionFileModel.class );
    final JCRSolutionFileSystem jcrFs = mock( JCRSolutionFileSystem.class );
    final FileObject vfsConn = mock( FileObject.class );

    when( vfsConn.getFileSystem() ).thenReturn( jcrFs );
    when( jcrFs.getLocalFileModel() ).thenReturn( fileModel );
    when( jcrFs.getAttribute( JCRSolutionFileSystem.LAST_REFRESH_TIME_ATTRIBUTE ) )
        .thenReturn( System.currentTimeMillis() );

    try ( MockedStatic<VFS> vfsMock = mockStatic( VFS.class );
          MockedStatic<PublishUtil> puMock = mockStatic( PublishUtil.class ) ) {
      vfsMock.when( VFS::getManager ).thenReturn( mock( FileSystemManager.class ) );
      puMock.when( () -> PublishUtil.createVFSConnection( any(), any() ) ).thenReturn( vfsConn );

      final ValidateLoginTask task = new ValidateLoginTask( mockLoginTask( loginData ) );
      assertTrue( task.validateLoginData() );
      verify( fileModel, never() ).refresh();
    }
  }

  @Test
  public void testValidateLoginDataUnknownFsFolderTypeReturnsTrue() throws Exception {
    final AuthenticationData loginData = mock( AuthenticationData.class );
    final FileSystem unknownFs = mock( FileSystem.class );
    final FileObject vfsConn = mock( FileObject.class );

    when( vfsConn.getFileSystem() ).thenReturn( unknownFs );
    when( vfsConn.getType() ).thenReturn( FileType.FOLDER );

    try ( MockedStatic<VFS> vfsMock = mockStatic( VFS.class );
          MockedStatic<PublishUtil> puMock = mockStatic( PublishUtil.class ) ) {
      vfsMock.when( VFS::getManager ).thenReturn( mock( FileSystemManager.class ) );
      puMock.when( () -> PublishUtil.createVFSConnection( any(), any() ) ).thenReturn( vfsConn );

      final ValidateLoginTask task = new ValidateLoginTask( mockLoginTask( loginData ) );
      assertTrue( task.validateLoginData() );
    }
  }

  @Test
  public void testValidateLoginDataunknownFsfileTypereturnsFalse() throws Exception {
    final AuthenticationData loginData = mock( AuthenticationData.class );
    final FileSystem unknownFs = mock( FileSystem.class );
    final FileObject vfsConn = mock( FileObject.class );

    when( vfsConn.getFileSystem() ).thenReturn( unknownFs );
    when( vfsConn.getType() ).thenReturn( FileType.FILE );

    try ( MockedStatic<VFS> vfsMock = mockStatic( VFS.class );
          MockedStatic<PublishUtil> puMock = mockStatic( PublishUtil.class ) ) {
      vfsMock.when( VFS::getManager ).thenReturn( mock( FileSystemManager.class ) );
      puMock.when( () -> PublishUtil.createVFSConnection( any(), any() ) ).thenReturn( vfsConn );

      final ValidateLoginTask task = new ValidateLoginTask( mockLoginTask( loginData ) );
      assertFalse( task.validateLoginData() );
    }
  }

  @Test
  public void testRunfileSystemExceptionsetsException() {
    final AuthenticationData loginData = mock( AuthenticationData.class );

    try ( MockedStatic<VFS> vfsMock = mockStatic( VFS.class );
          MockedStatic<PublishUtil> puMock = mockStatic( PublishUtil.class ) ) {
      vfsMock.when( VFS::getManager ).thenReturn( mock( FileSystemManager.class ) );
      puMock.when( () -> PublishUtil.createVFSConnection( any(), any() ) )
          .thenThrow( new FileSystemException( "connect failed" ) );

      final ValidateLoginTask task = new ValidateLoginTask( mockLoginTask( loginData ) );
      task.run();

      assertFalse( task.isLoginComplete() );
      assertNotNull( task.getException() );
    }
  }
}
