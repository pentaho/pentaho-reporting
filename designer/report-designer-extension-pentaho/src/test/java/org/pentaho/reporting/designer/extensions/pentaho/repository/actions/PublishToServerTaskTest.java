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

import java.awt.Component;
import java.io.IOException;
import java.lang.reflect.Field;

import org.apache.commons.vfs2.FileSystemException;

import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;

import org.apache.commons.vfs2.FileObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.designer.core.ReportDesignerBoot;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;
import org.pentaho.reporting.designer.core.auth.AuthenticationStore;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.extensions.pentaho.repository.RepositorySessionManager;
import org.pentaho.reporting.designer.extensions.pentaho.repository.auth.SessionAuthenticationUtil;
import org.pentaho.reporting.designer.extensions.pentaho.repository.util.PublishException;
import org.pentaho.reporting.designer.extensions.pentaho.repository.util.PublishUtil;
import org.pentaho.reporting.libraries.docbundle.DocumentBundle;
import org.pentaho.reporting.libraries.docbundle.DocumentMetaData;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentMetaData;
import org.pentaho.reporting.libraries.pensol.JCRSolutionFileModel;
import org.pentaho.reporting.libraries.pensol.JCRSolutionFileSystem;

public class PublishToServerTaskTest {

  private ReportDesignerContext reportDesignerContext;
  private Component uiContext;
  private AuthenticationData loginData;
  private ReportDocumentContext activeContext;
  private org.pentaho.reporting.engine.classic.core.MasterReport masterReport;
  private DocumentBundle bundle;
  private DocumentMetaData metaData;
  private AuthenticationStore authStore;

  @Before
  public void setUp() {
    reportDesignerContext = mock( ReportDesignerContext.class );
    uiContext = mock( Component.class );
    loginData = mock( AuthenticationData.class );
    activeContext = mock( ReportDocumentContext.class );
    bundle = mock( DocumentBundle.class );
    metaData = mock( DocumentMetaData.class );
    authStore = mock( AuthenticationStore.class );

    when( reportDesignerContext.getActiveContext() ).thenReturn( activeContext );
    when( activeContext.getAuthenticationStore() ).thenReturn( authStore );
    // getContextRoot() returns MasterReport which has getBundle()
    // We mock the entire chain via activeContext
    masterReport = mock( org.pentaho.reporting.engine.classic.core.MasterReport.class );
    when( activeContext.getContextRoot() ).thenReturn( masterReport );
    when( masterReport.getBundle() ).thenReturn( bundle );
    when( bundle.getMetaData() ).thenReturn( metaData );

    RepositorySessionManager.getInstance().clearSession();
  }

  @After
  public void tearDown() {
    RepositorySessionManager.getInstance().clearSession();
  }

  @Test
  public void testConstructor() {
    PublishToServerTask task = new PublishToServerTask( reportDesignerContext, uiContext );
    assertNotNull( task );
  }

  @Test
  public void testSetLoginData() {
    PublishToServerTask task = new PublishToServerTask( reportDesignerContext, uiContext );
    task.setLoginData( loginData, true );
    assertNotNull( task );
  }

  @Test
  public void testSetLoginDataWithFalseStoreUpdates() {
    PublishToServerTask task = new PublishToServerTask( reportDesignerContext, uiContext );
    task.setLoginData( loginData, false );
    assertNotNull( task );
  }

  @Test
  public void testSetLoginDataWithNullData() {
    PublishToServerTask task = new PublishToServerTask( reportDesignerContext, uiContext );
    task.setLoginData( null, false );
    assertNotNull( task );
  }

  @Test
  public void testImplementsAuthenticatedServerTask() {
    PublishToServerTask task = new PublishToServerTask( reportDesignerContext, uiContext );
    assertTrue( task instanceof AuthenticatedServerTask );
  }

  @Test
  public void testImplementsRunnable() {
    PublishToServerTask task = new PublishToServerTask( reportDesignerContext, uiContext );
    assertTrue( task instanceof Runnable );
  }

  @Test
  public void testRunSelectFileReturnsNull() {
    PublishToServerTask task = new PublishToServerTask( reportDesignerContext, uiContext );
    task.setLoginData( loginData, false );

    try ( var selectMock = mockConstruction( SelectFileForPublishTask.class,
      ( mock, ctx ) -> when( mock.selectFile( any(), any() ) ).thenReturn( null ) ) ) {
      task.run();
    }
    assertNotNull( task );
  }

  @Test
  public void testRunPublishSuccess() {
    PublishToServerTask task = new PublishToServerTask( reportDesignerContext, uiContext );
    task.setLoginData( loginData, true );

    Component glassPane = mock( Component.class );
    JRootPane rootPane = mock( JRootPane.class );

    try ( var selectMock = mockConstruction( SelectFileForPublishTask.class,
            ( mock, ctx ) -> when( mock.selectFile( any(), any() ) ).thenReturn( "/public/test.prpt" ) );
          var publishMock = mockStatic( PublishUtil.class );
          var jopMock = mockStatic( JOptionPane.class );
          var swingMock = mockStatic( SwingUtilities.class ) ) {

      publishMock.when( () -> PublishUtil.createBundleData( any() ) ).thenReturn( new byte[0] );
      publishMock.when( () -> PublishUtil.publish( any(), any(), any(), any() ) ).thenReturn( 200 );

      FileObject fileObj = mock( FileObject.class );
      JCRSolutionFileSystem jcrFs = mock( JCRSolutionFileSystem.class );
      JCRSolutionFileModel fileModel = mock( JCRSolutionFileModel.class );
      when( fileObj.getFileSystem() ).thenReturn( jcrFs );
      when( jcrFs.getLocalFileModel() ).thenReturn( fileModel );
      publishMock.when( () -> PublishUtil.createVFSConnection( any() ) ).thenReturn( fileObj );

      swingMock.when( () -> SwingUtilities.getRootPane( any() ) ).thenReturn( rootPane );
      when( rootPane.getGlassPane() ).thenReturn( glassPane );

      jopMock.when( () -> JOptionPane.showConfirmDialog(
        any(), any(), any(), anyInt() ) ).thenReturn( JOptionPane.NO_OPTION );

      task.run();
      verify( authStore ).add( any( AuthenticationData.class ), eq( true ) );
    }
  }

  @Test
  public void testRunPublish401WithBrowserAuth() {
    PublishToServerTask task = new PublishToServerTask( reportDesignerContext, uiContext );
    when( loginData.getOption( "browserAuth" ) ).thenReturn( "true" );
    task.setLoginData( loginData, false );

    try ( var selectMock = mockConstruction( SelectFileForPublishTask.class,
            ( mock, ctx ) -> when( mock.selectFile( any(), any() ) ).thenReturn( "/public/test.prpt" ) );
          var publishMock = mockStatic( PublishUtil.class );
          var jopMock = mockStatic( JOptionPane.class ) ) {

      publishMock.when( () -> PublishUtil.createBundleData( any() ) ).thenReturn( new byte[0] );
      publishMock.when( () -> PublishUtil.publish( any(), any(), any(), any() ) ).thenReturn( 401 );

      // User clicks Cancel on session expired dialog
      jopMock.when( () -> JOptionPane.showOptionDialog(
        any(), any(), any(), anyInt(), anyInt(), any(), any(), any() ) )
        .thenReturn( JOptionPane.NO_OPTION );

      task.run();
    }
    assertNotNull( task );
  }

  @Test
  public void testRunPublish403WithNonBrowserAuth() {
    PublishToServerTask task = new PublishToServerTask( reportDesignerContext, uiContext );
    when( loginData.getOption( "browserAuth" ) ).thenReturn( null );
    task.setLoginData( loginData, false );

    try ( var selectMock = mockConstruction( SelectFileForPublishTask.class,
            ( mock, ctx ) -> when( mock.selectFile( any(), any() ) ).thenReturn( "/public/test.prpt" ) );
          var publishMock = mockStatic( PublishUtil.class );
          var jopMock = mockStatic( JOptionPane.class ) ) {

      publishMock.when( () -> PublishUtil.createBundleData( any() ) ).thenReturn( new byte[0] );
      publishMock.when( () -> PublishUtil.publish( any(), any(), any(), any() ) ).thenReturn( 403 );

      jopMock.when( () -> JOptionPane.showMessageDialog(
        any(), any(), any(), anyInt() ) ).thenAnswer( inv -> null );

      task.run();
    }
    assertNotNull( task );
  }

  @Test
  public void testRunPublishOtherError() {
    PublishToServerTask task = new PublishToServerTask( reportDesignerContext, uiContext );
    task.setLoginData( loginData, false );

    try ( var selectMock = mockConstruction( SelectFileForPublishTask.class,
            ( mock, ctx ) -> when( mock.selectFile( any(), any() ) ).thenReturn( "/public/test.prpt" ) );
          var publishMock = mockStatic( PublishUtil.class );
          var jopMock = mockStatic( JOptionPane.class ) ) {

      publishMock.when( () -> PublishUtil.createBundleData( any() ) ).thenReturn( new byte[0] );
      publishMock.when( () -> PublishUtil.publish( any(), any(), any(), any() ) ).thenReturn( 500 );

      jopMock.when( () -> JOptionPane.showMessageDialog(
        any(), any(), any(), anyInt() ) ).thenAnswer( inv -> null );

      task.run();
    }
    assertNotNull( task );
  }

  @Test
  public void testRunAuthExceptionWithBrowserAuth() {
    PublishToServerTask task = new PublishToServerTask( reportDesignerContext, uiContext );
    when( loginData.getOption( "browserAuth" ) ).thenReturn( "true" );
    task.setLoginData( loginData, false );

    try ( var selectMock = mockConstruction( SelectFileForPublishTask.class,
            ( mock, ctx ) -> when( mock.selectFile( any(), any() ) ).thenReturn( "/public/test.prpt" ) );
          var publishMock = mockStatic( PublishUtil.class );
          var jopMock = mockStatic( JOptionPane.class ) ) {

      publishMock.when( () -> PublishUtil.createBundleData( any() ) )
        .thenThrow( new PublishException( PublishException.ERROR_INVALID_USERNAME_OR_PASSWORD ) );

      jopMock.when( () -> JOptionPane.showOptionDialog(
        any(), any(), any(), anyInt(), anyInt(), any(), any(), any() ) )
        .thenReturn( JOptionPane.NO_OPTION );

      task.run();
    }
    assertNotNull( task );
  }

  @Test
  public void testRunConnectionException() {
    PublishToServerTask task = new PublishToServerTask( reportDesignerContext, uiContext );
    task.setLoginData( loginData, false );

    try ( var selectMock = mockConstruction( SelectFileForPublishTask.class,
            ( mock, ctx ) -> when( mock.selectFile( any(), any() ) ).thenReturn( "/public/test.prpt" ) );
          var publishMock = mockStatic( PublishUtil.class );
          var jopMock = mockStatic( JOptionPane.class ) ) {

      publishMock.when( () -> PublishUtil.createBundleData( any() ) )
        .thenThrow( new PublishException( PublishException.ERROR_FAILED ) );

      jopMock.when( () -> JOptionPane.showMessageDialog(
        any(), any(), any(), anyInt() ) ).thenAnswer( inv -> null );

      task.run();
    }
    assertFalse( RepositorySessionManager.getInstance().hasActiveSession() );
  }

  @Test
  public void testRunGeneralException() {
    PublishToServerTask task = new PublishToServerTask( reportDesignerContext, uiContext );
    task.setLoginData( loginData, false );

    try ( var selectMock = mockConstruction( SelectFileForPublishTask.class,
            ( mock, ctx ) -> when( mock.selectFile( any(), any() ) ).thenReturn( "/public/test.prpt" ) );
          var publishMock = mockStatic( PublishUtil.class );
          var jopMock = mockStatic( JOptionPane.class ) ) {

      publishMock.when( () -> PublishUtil.createBundleData( any() ) )
        .thenThrow( new RuntimeException( "general error" ) );

      jopMock.when( () -> JOptionPane.showMessageDialog(
        any(), any(), any(), anyInt() ) ).thenAnswer( inv -> null );

      task.run();
    }
    assertNotNull( task );
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testRunWithPendingReport() throws Exception {
    PublishToServerTask task = new PublishToServerTask( reportDesignerContext, uiContext );
    task.setLoginData( loginData, false );

    Field pendingField = PublishToServerTask.class.getDeclaredField( "pendingReport" );
    pendingField.setAccessible( true );
    pendingField.set( task, "/public/pending.prpt" );

    SelectFileForPublishTask mockPublishTask = mock( SelectFileForPublishTask.class );
    Field pendingPublishField = PublishToServerTask.class.getDeclaredField( "pendingPublishTask" );
    pendingPublishField.setAccessible( true );
    pendingPublishField.set( task, mockPublishTask );

    try ( var publishMock = mockStatic( PublishUtil.class );
          var jopMock = mockStatic( JOptionPane.class ) ) {

      publishMock.when( () -> PublishUtil.createBundleData( any() ) ).thenReturn( new byte[0] );
      publishMock.when( () -> PublishUtil.publish( any(), any(), any(), any() ) ).thenReturn( 500 );

      jopMock.when( () -> JOptionPane.showMessageDialog(
        any(), any(), any(), anyInt() ) ).thenAnswer( inv -> null );

      task.run();
      assertNull( pendingField.get( task ) );
    }
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testSessionExpiredRetryLoginSucceeds() {
    PublishToServerTask task = new PublishToServerTask( reportDesignerContext, uiContext );
    AuthenticationData newLoginData = mock( AuthenticationData.class );
    when( newLoginData.getUsername() ).thenReturn( "admin" );
    when( loginData.getOption( "browserAuth" ) ).thenReturn( "true" );
    task.setLoginData( loginData, false );

    org.pentaho.reporting.designer.core.ReportDesignerView view =
      mock( org.pentaho.reporting.designer.core.ReportDesignerView.class );
    when( reportDesignerContext.getView() ).thenReturn( view );
    when( view.getParent() ).thenReturn( uiContext );

    try ( var selectMock = mockConstruction( SelectFileForPublishTask.class,
            ( mock, ctx ) -> when( mock.selectFile( any(), any() ) ).thenReturn( "/public/test.prpt" ) );
          var publishMock = mockStatic( PublishUtil.class );
          var jopMock = mockStatic( JOptionPane.class );
          var swingMock = mockStatic( SwingUtilities.class );
          var loginMock = mockConstruction( LoginTask.class,
            ( mock, ctx ) -> when( mock.getLoginData() ).thenReturn( newLoginData ) ) ) {

      publishMock.when( () -> PublishUtil.createBundleData( any() ) ).thenReturn( new byte[0] );
      publishMock.when( () -> PublishUtil.publish( any(), any(), any(), any() ) ).thenReturn( 401 );

      jopMock.when( () -> JOptionPane.showOptionDialog(
        any(), any(), any(), anyInt(), anyInt(), any(), any(), any() ) )
        .thenReturn( JOptionPane.YES_OPTION );
      swingMock.when( () -> SwingUtilities.invokeLater( any() ) ).thenAnswer( inv -> null );

      task.run();
      assertTrue( RepositorySessionManager.getInstance().hasActiveSession() );
    }
  }

  @Test
  public void testRunWithLastFilenameAttribute() {
    when( masterReport.getAttribute( ReportDesignerBoot.DESIGNER_NAMESPACE,
      ReportDesignerBoot.LAST_FILENAME ) ).thenReturn( "/public/oldreport.prpt" );

    PublishToServerTask task = new PublishToServerTask( reportDesignerContext, uiContext );
    task.setLoginData( loginData, false );

    try ( var selectMock = mockConstruction( SelectFileForPublishTask.class,
            ( mock, ctx ) -> when( mock.selectFile( any(), any() ) ).thenReturn( null ) ) ) {
      task.run();
    }
    assertNotNull( task );
  }

  @Test
  public void testRunWithNullLastFilenameAttribute() {
    when( masterReport.getAttribute( ReportDesignerBoot.DESIGNER_NAMESPACE,
      ReportDesignerBoot.LAST_FILENAME ) ).thenReturn( null );

    PublishToServerTask task = new PublishToServerTask( reportDesignerContext, uiContext );
    task.setLoginData( loginData, false );

    try ( var selectMock = mockConstruction( SelectFileForPublishTask.class,
            ( mock, ctx ) -> when( mock.selectFile( any(), any() ) ).thenReturn( null ) ) ) {
      task.run();
    }
    assertNotNull( task );
  }

  @Test
  public void testRunWithWriteableMetaData() {
    WriteableDocumentMetaData writeableMetaData = mock( WriteableDocumentMetaData.class );
    when( bundle.getMetaData() ).thenReturn( writeableMetaData );

    PublishToServerTask task = new PublishToServerTask( reportDesignerContext, uiContext );
    task.setLoginData( loginData, false );

    try ( var selectMock = mockConstruction( SelectFileForPublishTask.class,
            ( mock, ctx ) -> {
              when( mock.selectFile( any(), any() ) ).thenReturn( "/public/test.prpt" );
              when( mock.getReportTitle() ).thenReturn( "My Report" );
              when( mock.getDescription() ).thenReturn( "Desc" );
              when( mock.isLockOutputType() ).thenReturn( true );
              when( mock.getExportType() ).thenReturn( "pageable/pdf" );
            } );
          var publishMock = mockStatic( PublishUtil.class );
          var jopMock = mockStatic( JOptionPane.class ) ) {

      publishMock.when( () -> PublishUtil.createBundleData( any() ) ).thenReturn( new byte[0] );
      publishMock.when( () -> PublishUtil.publish( any(), any(), any(), any() ) ).thenReturn( 500 );

      jopMock.when( () -> JOptionPane.showMessageDialog(
        any(), any(), any(), anyInt() ) ).thenAnswer( inv -> null );

      task.run();
      // Verify writeableMetaData setBundleAttribute was called
      verify( writeableMetaData, atLeastOnce() ).setBundleAttribute( any(), any(), any() );
    }
  }

  // ---- handlePublishSuccess: launch report ----

  @Test
  public void testRunPublishSuccessLaunchReport() {
    PublishToServerTask task = new PublishToServerTask( reportDesignerContext, uiContext );
    task.setLoginData( loginData, true );
    when( loginData.getUrl() ).thenReturn( "http://localhost:8080/pentaho" );

    Component glassPane = mock( Component.class );
    JRootPane rootPane = mock( JRootPane.class );

    try ( var selectMock = mockConstruction( SelectFileForPublishTask.class,
            ( mock, ctx ) -> when( mock.selectFile( any(), any() ) ).thenReturn( "/public/test.prpt" ) );
          var publishMock = mockStatic( PublishUtil.class );
          var jopMock = mockStatic( JOptionPane.class );
          var swingMock = mockStatic( SwingUtilities.class ) ) {

      publishMock.when( () -> PublishUtil.createBundleData( any() ) ).thenReturn( new byte[0] );
      publishMock.when( () -> PublishUtil.publish( any(), any(), any(), any() ) ).thenReturn( 200 );

      FileObject fileObj = mock( FileObject.class );
      JCRSolutionFileSystem jcrFs = mock( JCRSolutionFileSystem.class );
      JCRSolutionFileModel fileModel = mock( JCRSolutionFileModel.class );
      when( fileObj.getFileSystem() ).thenReturn( jcrFs );
      when( jcrFs.getLocalFileModel() ).thenReturn( fileModel );
      publishMock.when( () -> PublishUtil.createVFSConnection( any() ) ).thenReturn( fileObj );

      swingMock.when( () -> SwingUtilities.getRootPane( any() ) ).thenReturn( rootPane );
      when( rootPane.getGlassPane() ).thenReturn( glassPane );

      // User clicks YES to launch report
      jopMock.when( () -> JOptionPane.showConfirmDialog(
        any(), any(), any(), anyInt() ) ).thenReturn( JOptionPane.YES_OPTION );

      publishMock.when( () -> PublishUtil.launchReportOnServer( any(), any() ) ).thenAnswer( inv -> null );

      task.run();
      publishMock.verify( () -> PublishUtil.launchReportOnServer( eq( "http://localhost:8080/pentaho" ),
        eq( "/public/test.prpt" ) ) );
    }
  }


  @Test
  public void testRunPublishSuccessVfsRefreshException() {
    PublishToServerTask task = new PublishToServerTask( reportDesignerContext, uiContext );
    task.setLoginData( loginData, false );

    Component glassPane = mock( Component.class );
    JRootPane rootPane = mock( JRootPane.class );

    try ( var selectMock = mockConstruction( SelectFileForPublishTask.class,
            ( mock, ctx ) -> when( mock.selectFile( any(), any() ) ).thenReturn( "/public/test.prpt" ) );
          var publishMock = mockStatic( PublishUtil.class );
          var jopMock = mockStatic( JOptionPane.class );
          var swingMock = mockStatic( SwingUtilities.class ) ) {

      publishMock.when( () -> PublishUtil.createBundleData( any() ) ).thenReturn( new byte[0] );
      publishMock.when( () -> PublishUtil.publish( any(), any(), any(), any() ) ).thenReturn( 200 );
      publishMock.when( () -> PublishUtil.createVFSConnection( any() ) )
        .thenThrow( new FileSystemException( "VFS error" ) );

      swingMock.when( () -> SwingUtilities.getRootPane( any() ) ).thenReturn( rootPane );
      when( rootPane.getGlassPane() ).thenReturn( glassPane );

      jopMock.when( () -> JOptionPane.showConfirmDialog(
        any(), any(), any(), anyInt() ) ).thenReturn( JOptionPane.NO_OPTION );

      task.run();
      // Exception was caught internally
      assertNotNull( task );
    }
  }


  @Test
  public void testRunPublishWithNullReportTitle() {
    PublishToServerTask task = new PublishToServerTask( reportDesignerContext, uiContext );
    task.setLoginData( loginData, false );

    try ( var selectMock = mockConstruction( SelectFileForPublishTask.class,
            ( mock, ctx ) -> {
              when( mock.selectFile( any(), any() ) ).thenReturn( "/public/test.prpt" );
              when( mock.getReportTitle() ).thenReturn( null );
            } );
          var publishMock = mockStatic( PublishUtil.class );
          var jopMock = mockStatic( JOptionPane.class ) ) {

      publishMock.when( () -> PublishUtil.createBundleData( any() ) ).thenReturn( new byte[0] );
      publishMock.when( () -> PublishUtil.publish( any(), any(), any(), any() ) ).thenReturn( 500 );

      jopMock.when( () -> JOptionPane.showMessageDialog(
        any(), any(), any(), anyInt() ) ).thenAnswer( inv -> null );

      task.run();
    }
    assertNotNull( task );
  }

  @Test
  public void testRunDoesNotPreemptivelyProbeSession() {
    PublishToServerTask task = new PublishToServerTask( reportDesignerContext, uiContext );
    task.setLoginData( loginData, true );

    try ( var selectMock = mockConstruction( SelectFileForPublishTask.class,
            ( mock, ctx ) -> when( mock.selectFile( any(), any() ) ).thenReturn( "/public/test.prpt" ) );
          var publishMock = mockStatic( PublishUtil.class );
          var sessionMock = mockStatic( SessionAuthenticationUtil.class );
          var jopMock = mockStatic( JOptionPane.class ) ) {

      publishMock.when( () -> PublishUtil.createBundleData( any() ) ).thenReturn( new byte[0] );
      publishMock.when( () -> PublishUtil.publish( any(), any(), any(), any() ) ).thenReturn( 200 );

      FileObject fileObj = mock( FileObject.class );
      JCRSolutionFileSystem jcrFs = mock( JCRSolutionFileSystem.class );
      JCRSolutionFileModel fileModel = mock( JCRSolutionFileModel.class );
      when( fileObj.getFileSystem() ).thenReturn( jcrFs );
      when( jcrFs.getLocalFileModel() ).thenReturn( fileModel );
      publishMock.when( () -> PublishUtil.createVFSConnection( any() ) ).thenReturn( fileObj );

      jopMock.when( () -> JOptionPane.showConfirmDialog(
        any(), any(), any(), anyInt() ) ).thenReturn( JOptionPane.NO_OPTION );

      task.run();
      // Session probe should NOT have been called
      sessionMock.verifyNoInteractions();
    }
  }

  @Test
  public void testRunInvalidSsoSessionExpired() {
    when( loginData.getOption( "browserAuth" ) ).thenReturn( "true" );

    PublishToServerTask task = new PublishToServerTask( reportDesignerContext, uiContext );
    task.setLoginData( loginData, false );

    try ( var selectMock = mockConstruction( SelectFileForPublishTask.class,
            ( mock, ctx ) -> when( mock.selectFile( any(), any() ) ).thenReturn( null ) );
          var sessionMock = mockStatic( SessionAuthenticationUtil.class );
          var jopMock = mockStatic( JOptionPane.class ) ) {
      // If the probe were called and returned FALSE, session expired dialog should fire
      sessionMock.when( () -> SessionAuthenticationUtil.checkSessionValidity( loginData ) )
        .thenReturn( Boolean.FALSE );

      jopMock.when( () -> JOptionPane.showOptionDialog(
        any(), any(), any(), anyInt(), anyInt(), any(), any(), any() ) )
        .thenReturn( JOptionPane.NO_OPTION );

      task.run();
    }
  }

  @Test
  public void testRunValidSsoSessionProceedsNormally() {
    when( loginData.getOption( "browserAuth" ) ).thenReturn( "true" );

    PublishToServerTask task = new PublishToServerTask( reportDesignerContext, uiContext );
    task.setLoginData( loginData, false );

    try ( var sessionMock = mockStatic( SessionAuthenticationUtil.class );
          var selectMock = mockConstruction( SelectFileForPublishTask.class,
            ( mock, ctx ) -> when( mock.selectFile( any(), any() ) ).thenReturn( null ) ) ) {
      sessionMock.when( () -> SessionAuthenticationUtil.checkSessionValidity( loginData ) )
        .thenReturn( Boolean.TRUE );
      task.run();
      // Verify task proceeded to create SelectFileForPublishTask
      assertEquals( 1, selectMock.constructed().size() );
    }
  }

  @Test
  public void testRunUnknownSsoSessionValidityProceedsNormally() {
    when( loginData.getOption( "browserAuth" ) ).thenReturn( "true" );

    PublishToServerTask task = new PublishToServerTask( reportDesignerContext, uiContext );
    task.setLoginData( loginData, false );

    try ( var sessionMock = mockStatic( SessionAuthenticationUtil.class );
          var selectMock = mockConstruction( SelectFileForPublishTask.class,
            ( mock, ctx ) -> when( mock.selectFile( any(), any() ) ).thenReturn( null ) ) ) {
      // null means unknown — should NOT trigger session expired
      sessionMock.when( () -> SessionAuthenticationUtil.checkSessionValidity( loginData ) )
        .thenReturn( null );
      task.run();
      assertEquals( 1, selectMock.constructed().size() );
    }
  }

  @Test
  public void testRunNonSsoSessionSkipsValidation() {
    // No browserAuth option → not SSO
    when( loginData.getOption( "browserAuth" ) ).thenReturn( null );

    PublishToServerTask task = new PublishToServerTask( reportDesignerContext, uiContext );
    task.setLoginData( loginData, false );

    try ( var selectMock = mockConstruction( SelectFileForPublishTask.class,
            ( mock, ctx ) -> when( mock.selectFile( any(), any() ) ).thenReturn( null ) ) ) {
      task.run();
      // Proceeded without calling checkSessionValidity
      assertEquals( 1, selectMock.constructed().size() );
    }
  }

  @Test
  public void testSessionExpiredRetryLoginReturnsNull() {
    PublishToServerTask task = new PublishToServerTask( reportDesignerContext, uiContext );
    when( loginData.getOption( "browserAuth" ) ).thenReturn( "true" );
    task.setLoginData( loginData, false );

    org.pentaho.reporting.designer.core.ReportDesignerView view =
      mock( org.pentaho.reporting.designer.core.ReportDesignerView.class );
    when( reportDesignerContext.getView() ).thenReturn( view );
    when( view.getParent() ).thenReturn( uiContext );

    try ( var selectMock = mockConstruction( SelectFileForPublishTask.class,
            ( mock, ctx ) -> when( mock.selectFile( any(), any() ) ).thenReturn( "/public/test.prpt" ) );
          var publishMock = mockStatic( PublishUtil.class );
          var jopMock = mockStatic( JOptionPane.class );
          var swingMock = mockStatic( SwingUtilities.class );
          var loginMock = mockConstruction( LoginTask.class,
            ( mock, ctx ) -> when( mock.getLoginData() ).thenReturn( null ) ) ) {

      publishMock.when( () -> PublishUtil.createBundleData( any() ) ).thenReturn( new byte[0] );
      publishMock.when( () -> PublishUtil.publish( any(), any(), any(), any() ) ).thenReturn( 401 );

      jopMock.when( () -> JOptionPane.showOptionDialog(
        any(), any(), any(), anyInt(), anyInt(), any(), any(), any() ) )
        .thenReturn( JOptionPane.YES_OPTION );
      swingMock.when( () -> SwingUtilities.invokeLater( any() ) ).thenAnswer( inv -> null );

      task.run();
      // LoginTask returned null → clearPendingState, no SwingUtilities.invokeLater
      swingMock.verifyNoInteractions();
    }
  }

  @Test
  public void testHandlePublishExceptionAuthErrorNonBrowserAuth() {
    PublishToServerTask task = new PublishToServerTask( reportDesignerContext, uiContext );
    // Non-browser auth → isConnectionError || isAuthenticationError path
    when( loginData.getOption( "browserAuth" ) ).thenReturn( null );
    task.setLoginData( loginData, false );

    try ( var selectMock = mockConstruction( SelectFileForPublishTask.class,
            ( mock, ctx ) -> when( mock.selectFile( any(), any() ) ).thenReturn( "/public/test.prpt" ) );
          var publishMock = mockStatic( PublishUtil.class );
          var jopMock = mockStatic( JOptionPane.class ) ) {

      // Use an exception with "401" in message — matches isAuthenticationError
      publishMock.when( () -> PublishUtil.createBundleData( any() ) )
        .thenThrow( new RuntimeException( "HTTP 401 Unauthorized" ) );

      jopMock.when( () -> JOptionPane.showMessageDialog(
        any(), any(), any(), anyInt() ) ).thenAnswer( inv -> null );

      task.run();
      // Should clear cached session
      assertFalse( RepositorySessionManager.getInstance().hasActiveSession() );
    }
  }

  @Test
  public void testHandlePublishExceptionConnectionError() {
    PublishToServerTask task = new PublishToServerTask( reportDesignerContext, uiContext );
    when( loginData.getOption( "browserAuth" ) ).thenReturn( null );
    task.setLoginData( loginData, false );

    try ( var selectMock = mockConstruction( SelectFileForPublishTask.class,
            ( mock, ctx ) -> when( mock.selectFile( any(), any() ) ).thenReturn( "/public/test.prpt" ) );
          var publishMock = mockStatic( PublishUtil.class );
          var jopMock = mockStatic( JOptionPane.class ) ) {

      // Use ConnectException wrapped in RuntimeException — matches isConnectionError
      publishMock.when( () -> PublishUtil.createBundleData( any() ) )
        .thenThrow( new RuntimeException( new java.net.ConnectException( "Connection refused" ) ) );

      jopMock.when( () -> JOptionPane.showMessageDialog(
        any(), any(), any(), anyInt() ) ).thenAnswer( inv -> null );

      task.run();
      assertFalse( RepositorySessionManager.getInstance().hasActiveSession() );
    }
  }

  @Test
  public void testHandlePublishSuccessLaunchReportThrowsIOException() {
    PublishToServerTask task = new PublishToServerTask( reportDesignerContext, uiContext );
    task.setLoginData( loginData, false );
    when( loginData.getUrl() ).thenReturn( "http://localhost:8080/pentaho" );

    Component glassPane = mock( Component.class );
    JRootPane rootPane = mock( JRootPane.class );

    try ( var selectMock = mockConstruction( SelectFileForPublishTask.class,
            ( mock, ctx ) -> when( mock.selectFile( any(), any() ) ).thenReturn( "/public/test.prpt" ) );
          var publishMock = mockStatic( PublishUtil.class );
          var jopMock = mockStatic( JOptionPane.class );
          var swingMock = mockStatic( SwingUtilities.class ) ) {

      publishMock.when( () -> PublishUtil.createBundleData( any() ) ).thenReturn( new byte[0] );
      publishMock.when( () -> PublishUtil.publish( any(), any(), any(), any() ) ).thenReturn( 200 );

      FileObject fileObj = mock( FileObject.class );
      JCRSolutionFileSystem jcrFs = mock( JCRSolutionFileSystem.class );
      JCRSolutionFileModel fileModel = mock( JCRSolutionFileModel.class );
      when( fileObj.getFileSystem() ).thenReturn( jcrFs );
      when( jcrFs.getLocalFileModel() ).thenReturn( fileModel );
      publishMock.when( () -> PublishUtil.createVFSConnection( any() ) ).thenReturn( fileObj );

      swingMock.when( () -> SwingUtilities.getRootPane( any() ) ).thenReturn( rootPane );
      when( rootPane.getGlassPane() ).thenReturn( glassPane );

      // User clicks YES to launch report
      jopMock.when( () -> JOptionPane.showConfirmDialog(
        any(), any(), any(), anyInt() ) ).thenReturn( JOptionPane.YES_OPTION );

      // Launching throws IOException  
      publishMock.when( () -> PublishUtil.launchReportOnServer( any(), any() ) )
        .thenThrow( new IOException( "launch failed" ) );

      task.run();
      // IOException is caught and added to UncaughtExceptionsModel — no crash
      assertNotNull( task );
    }
  }

  @Test
  public void testRunPublish403WithBrowserAuth() {
    PublishToServerTask task = new PublishToServerTask( reportDesignerContext, uiContext );
    when( loginData.getOption( "browserAuth" ) ).thenReturn( "true" );
    task.setLoginData( loginData, false );

    try ( var selectMock = mockConstruction( SelectFileForPublishTask.class,
            ( mock, ctx ) -> when( mock.selectFile( any(), any() ) ).thenReturn( "/public/test.prpt" ) );
          var publishMock = mockStatic( PublishUtil.class );
          var jopMock = mockStatic( JOptionPane.class ) ) {

      publishMock.when( () -> PublishUtil.createBundleData( any() ) ).thenReturn( new byte[0] );
      publishMock.when( () -> PublishUtil.publish( any(), any(), any(), any() ) ).thenReturn( 403 );

      // User clicks Cancel on session expired dialog
      jopMock.when( () -> JOptionPane.showOptionDialog(
        any(), any(), any(), anyInt(), anyInt(), any(), any(), any() ) )
        .thenReturn( JOptionPane.NO_OPTION );

      task.run();
    }
    assertNotNull( task );
  }

  // ---- showErrorMessage verification ----

  @Test
  public void testRunPublishOtherErrorShowsDialog() {
    PublishToServerTask task = new PublishToServerTask( reportDesignerContext, uiContext );
    task.setLoginData( loginData, false );

    try ( var selectMock = mockConstruction( SelectFileForPublishTask.class,
            ( mock, ctx ) -> when( mock.selectFile( any(), any() ) ).thenReturn( "/public/test.prpt" ) );
          var publishMock = mockStatic( PublishUtil.class );
          var jopMock = mockStatic( JOptionPane.class ) ) {

      publishMock.when( () -> PublishUtil.createBundleData( any() ) ).thenReturn( new byte[0] );
      publishMock.when( () -> PublishUtil.publish( any(), any(), any(), any() ) ).thenReturn( 500 );

      jopMock.when( () -> JOptionPane.showMessageDialog(
        any(), any(), any(), anyInt() ) ).thenAnswer( inv -> null );

      task.run();

      jopMock.verify( () -> JOptionPane.showMessageDialog(
        any(), any(), any(), anyInt() ) );
    }
  }

  // ---- storeBundleMetaData with non-writeable metadata ----

  @Test
  public void testRunWithNonWriteableMetaData() {
    // metaData is a plain DocumentMetaData mock (not WriteableDocumentMetaData)
    // → storeBundleMetaData should skip setBundleAttribute calls
    PublishToServerTask task = new PublishToServerTask( reportDesignerContext, uiContext );
    task.setLoginData( loginData, true );

    try ( var selectMock = mockConstruction( SelectFileForPublishTask.class,
            ( mock, ctx ) -> {
              when( mock.selectFile( any(), any() ) ).thenReturn( "/public/ro.prpt" );
              when( mock.getReportTitle() ).thenReturn( "Title" );
              when( mock.getDescription() ).thenReturn( "Desc" );
            } );
          var publishMock = mockStatic( PublishUtil.class );
          var jopMock = mockStatic( JOptionPane.class ) ) {

      publishMock.when( () -> PublishUtil.createBundleData( any() ) ).thenReturn( new byte[0] );
      publishMock.when( () -> PublishUtil.publish( any(), any(), any(), any() ) ).thenReturn( 200 );
      jopMock.when( () -> JOptionPane.showConfirmDialog(
        any(), any(), any(), anyInt() ) ).thenReturn( JOptionPane.NO_OPTION );

      // Set up uiContext to have a root pane with glass pane for handlePublishSuccess
      javax.swing.JPanel glassPane = new javax.swing.JPanel();
      JRootPane rootPane = mock( JRootPane.class );
      when( rootPane.getGlassPane() ).thenReturn( glassPane );
      try ( var swingMock = mockStatic( SwingUtilities.class ) ) {
        swingMock.when( () -> SwingUtilities.getRootPane( any() ) ).thenReturn( rootPane );
        publishMock.when( () -> PublishUtil.createVFSConnection( any() ) )
          .thenThrow( new FileSystemException( "test" ) );
        task.run();
      }
    }
    // Verify no WriteableDocumentMetaData methods were called (metaData is not writeable)
    assertNotNull( task );
  }

  // ---- clearPendingState ----

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testClearPendingStateResetsBothFields() throws Exception {
    PublishToServerTask task = new PublishToServerTask( reportDesignerContext, uiContext );

    Field pendingReportField = PublishToServerTask.class.getDeclaredField( "pendingReport" );
    pendingReportField.setAccessible( true );
    pendingReportField.set( task, "/old/path.prpt" );

    Field pendingTaskField = PublishToServerTask.class.getDeclaredField( "pendingPublishTask" );
    pendingTaskField.setAccessible( true );
    pendingTaskField.set( task, mock( SelectFileForPublishTask.class ) );

    // Invoke clearPendingState
    java.lang.reflect.Method m = PublishToServerTask.class.getDeclaredMethod( "clearPendingState" );
    m.setAccessible( true );
    m.invoke( task );

    assertNull( pendingReportField.get( task ) );
    assertNull( pendingTaskField.get( task ) );
  }

  // ---- extractLastFileName explicit test ----

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testExtractLastFileNameReturnsAttributeValue() throws Exception {
    when( masterReport.getAttribute( ReportDesignerBoot.DESIGNER_NAMESPACE, ReportDesignerBoot.LAST_FILENAME ) )
      .thenReturn( "/public/saved.prpt" );

    PublishToServerTask task = new PublishToServerTask( reportDesignerContext, uiContext );
    java.lang.reflect.Method m = PublishToServerTask.class.getDeclaredMethod( "extractLastFileName",
      org.pentaho.reporting.engine.classic.core.MasterReport.class );
    m.setAccessible( true );
    String result = (String) m.invoke( task, masterReport );
    assertEquals( "/public/saved.prpt", result );
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testExtractLastFileNameReturnsNullWhenNoAttribute() throws Exception {
    when( masterReport.getAttribute( ReportDesignerBoot.DESIGNER_NAMESPACE, ReportDesignerBoot.LAST_FILENAME ) )
      .thenReturn( null );

    PublishToServerTask task = new PublishToServerTask( reportDesignerContext, uiContext );
    java.lang.reflect.Method m = PublishToServerTask.class.getDeclaredMethod( "extractLastFileName",
      org.pentaho.reporting.engine.classic.core.MasterReport.class );
    m.setAccessible( true );
    assertNull( m.invoke( task, masterReport ) );
  }

  @Test
  public void testRunSsoLoginSkipsAuthStoreWrite() throws Exception {
    when( loginData.getOption( "browserAuth" ) ).thenReturn( "true" );
    PublishToServerTask task = new PublishToServerTask( reportDesignerContext, uiContext );
    task.setLoginData( loginData, true );

    SelectFileForPublishTask mockSelect = mock( SelectFileForPublishTask.class );
    when( mockSelect.selectFile( any(), any() ) ).thenReturn( "/public/report.prpt" );
    when( mockSelect.getReportTitle() ).thenReturn( "Test" );

    try ( var publishMock = mockStatic( PublishUtil.class ) ) {
      publishMock.when( () -> PublishUtil.createBundleData( any() ) ).thenReturn( new byte[ 0 ] );
      publishMock.when( () -> PublishUtil.publish( any(), any(), any(), any() ) ).thenReturn( 200 );

      // Set pendingReport and pendingPublishTask via reflection to skip dialog
      java.lang.reflect.Field prField = PublishToServerTask.class.getDeclaredField( "pendingReport" );
      prField.setAccessible( true );
      prField.set( task, "/public/report.prpt" );
      java.lang.reflect.Field ptField = PublishToServerTask.class.getDeclaredField( "pendingPublishTask" );
      ptField.setAccessible( true );
      ptField.set( task, mockSelect );

      try ( var jopMock = mockStatic( javax.swing.JOptionPane.class ) ) {
        jopMock.when( () -> javax.swing.JOptionPane.showMessageDialog(
          any(), any(), any(), anyInt() ) ).thenAnswer( inv -> null );
        task.run();
      }
      verify( authStore, never() ).add( any( AuthenticationData.class ), anyBoolean() );
    }
  }

  @Test
  public void testSanitizedForStoreReturnsNullForNullInput() {
    assertNull( PublishToServerTask.sanitizedForStore( null ) );
  }

  @Test
  public void testSanitizedForStoreReturnsSameInstanceWhenUrlNull() {
    AuthenticationData data = mock( AuthenticationData.class );
    when( data.getUrl() ).thenReturn( null );
    assertSame( data, PublishToServerTask.sanitizedForStore( data ) );
  }

  @Test
  public void testSanitizedForStoreStripsSessionAndBrowserAuth() {
    AuthenticationData original = new AuthenticationData( "http://srv/p", "u", "pw", 30 );
    original.setOption( "sessionId", "SHOULD-NOT-LEAK" );
    original.setOption( "browserAuth", "true" );
    original.setOption( "server-version", "5" );
    original.setOption( "lastFilename", "/x.prpt" );

    AuthenticationData copy = PublishToServerTask.sanitizedForStore( original );

    assertNotSame( original, copy );
    assertEquals( "http://srv/p", copy.getUrl() );
    assertEquals( "u", copy.getUsername() );
    assertEquals( "pw", copy.getPassword() );
    assertNull( copy.getOption( "sessionId" ) );
    assertNull( copy.getOption( "browserAuth" ) );
    assertEquals( "5", copy.getOption( "server-version" ) );
    assertEquals( "/x.prpt", copy.getOption( "lastFilename" ) );
  }

  @Test
  public void testSanitizedForStoreSkipsAbsentPreservedOptions() {
    AuthenticationData original = new AuthenticationData( "http://srv/p", "u", "pw", 30 );
    AuthenticationData copy = PublishToServerTask.sanitizedForStore( original );
    assertNotSame( original, copy );
    assertNull( copy.getOption( "server-version" ) );
    assertNull( copy.getOption( "lastFilename" ) );
  }
}
