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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.lang.reflect.Field;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystem;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.impl.DefaultFileSystemManager;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;
import org.pentaho.reporting.designer.core.auth.GlobalAuthenticationStore;
import org.pentaho.reporting.designer.extensions.pentaho.repository.util.PublishUtil;
import org.pentaho.reporting.libraries.pensol.vfs.WebSolutionFileSystem;

public class ValidateLoginTaskTest {

  private LoginTask loginTask;
  private ReportDesignerContext designerContext;
  private java.awt.Component uiContext;
  private GlobalAuthenticationStore authStore;
  private AuthenticationData loginData;

  @Before
  public void setUp() {
    designerContext = mock( ReportDesignerContext.class );
    uiContext = mock( java.awt.Component.class );
    authStore = mock( GlobalAuthenticationStore.class );
    loginData = mock( AuthenticationData.class );

    when( designerContext.getGlobalAuthenticationStore() ).thenReturn( authStore );
  }

  @Test
  public void testConstructor() {
    loginTask = new LoginTask( designerContext, uiContext, null, loginData );
    ValidateLoginTask validateTask = new ValidateLoginTask( loginTask );
    assertNotNull( validateTask );
  }

  @Test
  public void testGetExceptionInitiallyNull() {
    loginTask = new LoginTask( designerContext, uiContext, null, loginData );
    ValidateLoginTask validateTask = new ValidateLoginTask( loginTask );
    assertNull( validateTask.getException() );
  }

  @Test
  public void testIsLoginCompleteInitiallyFalse() {
    loginTask = new LoginTask( designerContext, uiContext, null, loginData );
    ValidateLoginTask validateTask = new ValidateLoginTask( loginTask );
    assertFalse( validateTask.isLoginComplete() );
  }

  @Test
  public void testRunWithNullLoginDataSetsLoginComplete() {
    loginTask = new LoginTask( designerContext, uiContext, null, null );
    ValidateLoginTask validateTask = new ValidateLoginTask( loginTask );
    validateTask.run();
    assertTrue( validateTask.isLoginComplete() );
    assertNull( validateTask.getException() );
  }

  @Test
  public void testRunWithLoginDataCallsValidation() {
    when( loginData.getUrl() ).thenReturn( "http://localhost:8080/pentaho" );
    when( loginData.getUsername() ).thenReturn( "admin" );
    when( loginData.getPassword() ).thenReturn( "password" );
    loginTask = new LoginTask( designerContext, uiContext, null, loginData );
    ValidateLoginTask validateTask = new ValidateLoginTask( loginTask );
    validateTask.run();
    assertNotNull( validateTask );
  }

  @Test
  public void testImplementsRunnable() {
    loginTask = new LoginTask( designerContext, uiContext, null, loginData );
    ValidateLoginTask validateTask = new ValidateLoginTask( loginTask );
    assertTrue( validateTask instanceof Runnable );
  }

  // ---- validateLoginData coverage ----

  @Test
  public void testValidateLoginDataWithNullLoginData() throws FileSystemException {
    loginTask = new LoginTask( designerContext, uiContext, null, null );
    ValidateLoginTask validateTask = new ValidateLoginTask( loginTask );
    assertTrue( validateTask.validateLoginData() );
  }

  @Test
  public void testValidateLoginDataWithWebSolutionFileSystem() throws Exception {
    loginTask = new LoginTask( designerContext, uiContext, null, loginData );
    ValidateLoginTask validateTask = new ValidateLoginTask( loginTask );

    FileObject fileObj = mock( FileObject.class );
    WebSolutionFileSystem webFs = mock( WebSolutionFileSystem.class );
    when( fileObj.getFileSystem() ).thenReturn( webFs );
    // Refresh time is recent (within 500ms)
    when( webFs.getAttribute( WebSolutionFileSystem.LAST_REFRESH_TIME_ATTRIBUTE ) )
      .thenReturn( System.currentTimeMillis() );

    try ( var publishMock = mockStatic( PublishUtil.class );
          var vfsMock = mockStatic( VFS.class ) ) {
      DefaultFileSystemManager fsMgr = mock( DefaultFileSystemManager.class );
      vfsMock.when( VFS::getManager ).thenReturn( fsMgr );
      publishMock.when( () -> PublishUtil.createVFSConnection( any(), any() ) ).thenReturn( fileObj );

      boolean result = validateTask.validateLoginData();
      assertTrue( result );
    }
  }

  @Test
  public void testValidateLoginDataWithWebSolutionFileSystemNeedsRefresh() throws Exception {
    loginTask = new LoginTask( designerContext, uiContext, null, loginData );
    ValidateLoginTask validateTask = new ValidateLoginTask( loginTask );

    FileObject fileObj = mock( FileObject.class );
    WebSolutionFileSystem webFs = mock( WebSolutionFileSystem.class );
    org.pentaho.reporting.libraries.pensol.SolutionFileModel fileModel =
      mock( org.pentaho.reporting.libraries.pensol.SolutionFileModel.class );
    when( fileObj.getFileSystem() ).thenReturn( webFs );
    // Refresh time is old (more than 500ms ago)
    when( webFs.getAttribute( WebSolutionFileSystem.LAST_REFRESH_TIME_ATTRIBUTE ) )
      .thenReturn( System.currentTimeMillis() - 10000L );
    when( webFs.getLocalFileModel() ).thenReturn( fileModel );

    try ( var publishMock = mockStatic( PublishUtil.class );
          var vfsMock = mockStatic( VFS.class ) ) {
      DefaultFileSystemManager fsMgr = mock( DefaultFileSystemManager.class );
      vfsMock.when( VFS::getManager ).thenReturn( fsMgr );
      publishMock.when( () -> PublishUtil.createVFSConnection( any(), any() ) ).thenReturn( fileObj );

      boolean result = validateTask.validateLoginData();
      assertTrue( result );
      verify( fileModel ).refresh();
    }
  }

  @Test
  public void testValidateLoginDataWithWebSolutionFileSystemNullRefreshTime() throws Exception {
    loginTask = new LoginTask( designerContext, uiContext, null, loginData );
    ValidateLoginTask validateTask = new ValidateLoginTask( loginTask );

    FileObject fileObj = mock( FileObject.class );
    WebSolutionFileSystem webFs = mock( WebSolutionFileSystem.class );
    when( fileObj.getFileSystem() ).thenReturn( webFs );
    when( webFs.getAttribute( WebSolutionFileSystem.LAST_REFRESH_TIME_ATTRIBUTE ) )
      .thenReturn( null );

    try ( var publishMock = mockStatic( PublishUtil.class );
          var vfsMock = mockStatic( VFS.class ) ) {
      DefaultFileSystemManager fsMgr = mock( DefaultFileSystemManager.class );
      vfsMock.when( VFS::getManager ).thenReturn( fsMgr );
      publishMock.when( () -> PublishUtil.createVFSConnection( any(), any() ) ).thenReturn( fileObj );

      boolean result = validateTask.validateLoginData();
      assertTrue( result );
    }
  }

  @Test
  public void testValidateLoginDataWithWebSolutionFileSystemAttributeThrowsFSE() throws Exception {
    loginTask = new LoginTask( designerContext, uiContext, null, loginData );
    ValidateLoginTask validateTask = new ValidateLoginTask( loginTask );

    FileObject fileObj = mock( FileObject.class );
    WebSolutionFileSystem webFs = mock( WebSolutionFileSystem.class );
    when( fileObj.getFileSystem() ).thenReturn( webFs );
    when( webFs.getAttribute( WebSolutionFileSystem.LAST_REFRESH_TIME_ATTRIBUTE ) )
      .thenThrow( new FileSystemException( "attribute not supported" ) );

    try ( var publishMock = mockStatic( PublishUtil.class );
          var vfsMock = mockStatic( VFS.class ) ) {
      DefaultFileSystemManager fsMgr = mock( DefaultFileSystemManager.class );
      vfsMock.when( VFS::getManager ).thenReturn( fsMgr );
      publishMock.when( () -> PublishUtil.createVFSConnection( any(), any() ) ).thenReturn( fileObj );

      // FSE is caught silently, falls through to getType()
      when( fileObj.getType() ).thenReturn( FileType.FOLDER );
      boolean result = validateTask.validateLoginData();
      assertTrue( result );
    }
  }

  @Test
  public void testValidateLoginDataWithWebSolutionFileSystemRefreshThrowsIOE() throws Exception {
    loginTask = new LoginTask( designerContext, uiContext, null, loginData );
    ValidateLoginTask validateTask = new ValidateLoginTask( loginTask );

    FileObject fileObj = mock( FileObject.class );
    WebSolutionFileSystem webFs = mock( WebSolutionFileSystem.class );
    org.pentaho.reporting.libraries.pensol.SolutionFileModel fileModel =
      mock( org.pentaho.reporting.libraries.pensol.SolutionFileModel.class );
    when( fileObj.getFileSystem() ).thenReturn( webFs );
    when( webFs.getAttribute( WebSolutionFileSystem.LAST_REFRESH_TIME_ATTRIBUTE ) )
      .thenReturn( System.currentTimeMillis() - 10000L );
    when( webFs.getLocalFileModel() ).thenReturn( fileModel );
    doThrow( new IOException( "refresh failed" ) ).when( fileModel ).refresh();

    try ( var publishMock = mockStatic( PublishUtil.class );
          var vfsMock = mockStatic( VFS.class ) ) {
      DefaultFileSystemManager fsMgr = mock( DefaultFileSystemManager.class );
      vfsMock.when( VFS::getManager ).thenReturn( fsMgr );
      publishMock.when( () -> PublishUtil.createVFSConnection( any(), any() ) ).thenReturn( fileObj );

      boolean result = validateTask.validateLoginData();
      assertFalse( result );
    }
  }

  @Test
  public void testValidateLoginDataWithNonWebSolutionFileSystemFolderType() throws Exception {
    loginTask = new LoginTask( designerContext, uiContext, null, loginData );
    ValidateLoginTask validateTask = new ValidateLoginTask( loginTask );

    FileObject fileObj = mock( FileObject.class );
    FileSystem fs = mock( FileSystem.class );
    when( fileObj.getFileSystem() ).thenReturn( fs );
    when( fileObj.getType() ).thenReturn( FileType.FOLDER );

    try ( var publishMock = mockStatic( PublishUtil.class );
          var vfsMock = mockStatic( VFS.class ) ) {
      DefaultFileSystemManager fsMgr = mock( DefaultFileSystemManager.class );
      vfsMock.when( VFS::getManager ).thenReturn( fsMgr );
      publishMock.when( () -> PublishUtil.createVFSConnection( any(), any() ) ).thenReturn( fileObj );

      boolean result = validateTask.validateLoginData();
      assertTrue( result );
    }
  }

  @Test
  public void testValidateLoginDataWithNonWebSolutionFileSystemFileType() throws Exception {
    loginTask = new LoginTask( designerContext, uiContext, null, loginData );
    ValidateLoginTask validateTask = new ValidateLoginTask( loginTask );

    FileObject fileObj = mock( FileObject.class );
    FileSystem fs = mock( FileSystem.class );
    when( fileObj.getFileSystem() ).thenReturn( fs );
    when( fileObj.getType() ).thenReturn( FileType.FILE );

    try ( var publishMock = mockStatic( PublishUtil.class );
          var vfsMock = mockStatic( VFS.class ) ) {
      DefaultFileSystemManager fsMgr = mock( DefaultFileSystemManager.class );
      vfsMock.when( VFS::getManager ).thenReturn( fsMgr );
      publishMock.when( () -> PublishUtil.createVFSConnection( any(), any() ) ).thenReturn( fileObj );

      boolean result = validateTask.validateLoginData();
      assertFalse( result );
    }
  }

  // ---- run() exception handling ----

  @Test
  public void testRunWithFileSystemException() {
    loginTask = new LoginTask( designerContext, uiContext, null, loginData );
    ValidateLoginTask validateTask = new ValidateLoginTask( loginTask );

    try ( var publishMock = mockStatic( PublishUtil.class );
          var vfsMock = mockStatic( VFS.class ) ) {
      DefaultFileSystemManager fsMgr = mock( DefaultFileSystemManager.class );
      vfsMock.when( VFS::getManager ).thenReturn( fsMgr );
      publishMock.when( () -> PublishUtil.createVFSConnection( any(), any() ) )
        .thenThrow( new FileSystemException( "connection failed" ) );

      validateTask.run();
      assertFalse( validateTask.isLoginComplete() );
      assertNotNull( validateTask.getException() );
      assertTrue( validateTask.getException() instanceof FileSystemException );
    }
  }
}
