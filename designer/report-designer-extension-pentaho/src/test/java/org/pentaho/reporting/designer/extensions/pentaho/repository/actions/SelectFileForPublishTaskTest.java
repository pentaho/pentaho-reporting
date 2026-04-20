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
import java.awt.Dialog;
import java.awt.Frame;
import java.lang.reflect.Field;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;
import org.pentaho.reporting.designer.extensions.pentaho.repository.dialogs.RepositoryOpenDialog;
import org.pentaho.reporting.designer.extensions.pentaho.repository.dialogs.RepositoryPublishDialog;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

public class SelectFileForPublishTaskTest {

  private Component uiContext;

  @Before
  public void setUp() {
    uiContext = mock( Component.class );
  }

  @Test
  public void testConstructorWithMockComponent() {
    try ( var libSwingMock = mockStatic( LibSwingUtil.class );
          var publishDialogMock = mockConstruction( RepositoryPublishDialog.class ) ) {
      libSwingMock.when( () -> LibSwingUtil.getWindowAncestor( any() ) ).thenReturn( null );
      libSwingMock.when( () -> LibSwingUtil.centerFrameOnScreen( any() ) ).thenAnswer( inv -> null );
      SelectFileForPublishTask task = new SelectFileForPublishTask( uiContext );
      assertNotNull( task );
    }
  }

  @Test
  public void testConstructorWithFrameParent() {
    Frame frame = mock( Frame.class );
    try ( var libSwingMock = mockStatic( LibSwingUtil.class );
          var publishDialogMock = mockConstruction( RepositoryPublishDialog.class ) ) {
      libSwingMock.when( () -> LibSwingUtil.getWindowAncestor( any() ) ).thenReturn( frame );
      libSwingMock.when( () -> LibSwingUtil.centerFrameOnScreen( any() ) ).thenAnswer( inv -> null );
      SelectFileForPublishTask task = new SelectFileForPublishTask( uiContext );
      assertNotNull( task );
    }
  }

  @Test
  public void testConstructorWithDialogParent() {
    Dialog dialog = mock( Dialog.class );
    try ( var libSwingMock = mockStatic( LibSwingUtil.class );
          var publishDialogMock = mockConstruction( RepositoryPublishDialog.class ) ) {
      libSwingMock.when( () -> LibSwingUtil.getWindowAncestor( any() ) ).thenReturn( dialog );
      libSwingMock.when( () -> LibSwingUtil.centerFrameOnScreen( any() ) ).thenAnswer( inv -> null );
      SelectFileForPublishTask task = new SelectFileForPublishTask( uiContext );
      assertNotNull( task );
    }
  }

  @Test
  public void testSetReLoginListener() {
    try ( var libSwingMock = mockStatic( LibSwingUtil.class );
          var publishDialogMock = mockConstruction( RepositoryPublishDialog.class ) ) {
      libSwingMock.when( () -> LibSwingUtil.getWindowAncestor( any() ) ).thenReturn( null );
      libSwingMock.when( () -> LibSwingUtil.centerFrameOnScreen( any() ) ).thenAnswer( inv -> null );
      SelectFileForPublishTask task = new SelectFileForPublishTask( uiContext );
      RepositoryOpenDialog.ReLoginListener listener = mock( RepositoryOpenDialog.ReLoginListener.class );
      task.setReLoginListener( listener );
      assertNotNull( task );
    }
  }

  @Test
  public void testSetReLoginListenerNull() {
    try ( var libSwingMock = mockStatic( LibSwingUtil.class );
          var publishDialogMock = mockConstruction( RepositoryPublishDialog.class ) ) {
      libSwingMock.when( () -> LibSwingUtil.getWindowAncestor( any() ) ).thenReturn( null );
      libSwingMock.when( () -> LibSwingUtil.centerFrameOnScreen( any() ) ).thenAnswer( inv -> null );
      SelectFileForPublishTask task = new SelectFileForPublishTask( uiContext );
      task.setReLoginListener( null );
      assertNotNull( task );
    }
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testSelectFileDelegatesToDialog() throws Exception {
    RepositoryPublishDialog mockDialog = mock( RepositoryPublishDialog.class );
    AuthenticationData loginData = mock( AuthenticationData.class );
    when( mockDialog.performOpen( any(), any() ) ).thenReturn( "/public/test.prpt" );

    try ( var libSwingMock = mockStatic( LibSwingUtil.class );
          var publishDialogMock = mockConstruction( RepositoryPublishDialog.class ) ) {
      libSwingMock.when( () -> LibSwingUtil.getWindowAncestor( any() ) ).thenReturn( null );
      libSwingMock.when( () -> LibSwingUtil.centerFrameOnScreen( any() ) ).thenAnswer( inv -> null );
      SelectFileForPublishTask task = new SelectFileForPublishTask( uiContext );

      // Inject mock dialog
      Field dialogField = SelectFileForPublishTask.class.getDeclaredField( "repositoryBrowserDialog" );
      dialogField.setAccessible( true );
      dialogField.set( task, mockDialog );

      String result = task.selectFile( loginData, "/public/old.prpt" );
      assertEquals( "/public/test.prpt", result );
      verify( mockDialog ).performOpen( loginData, "/public/old.prpt" );
    }
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testSelectFileWithReLoginListener() throws Exception {
    RepositoryPublishDialog mockDialog = mock( RepositoryPublishDialog.class );
    AuthenticationData loginData = mock( AuthenticationData.class );
    RepositoryOpenDialog.ReLoginListener listener = mock( RepositoryOpenDialog.ReLoginListener.class );
    when( mockDialog.performOpen( any(), any() ) ).thenReturn( null );

    try ( var libSwingMock = mockStatic( LibSwingUtil.class );
          var publishDialogMock = mockConstruction( RepositoryPublishDialog.class ) ) {
      libSwingMock.when( () -> LibSwingUtil.getWindowAncestor( any() ) ).thenReturn( null );
      libSwingMock.when( () -> LibSwingUtil.centerFrameOnScreen( any() ) ).thenAnswer( inv -> null );
      SelectFileForPublishTask task = new SelectFileForPublishTask( uiContext );
      task.setReLoginListener( listener );

      Field dialogField = SelectFileForPublishTask.class.getDeclaredField( "repositoryBrowserDialog" );
      dialogField.setAccessible( true );
      dialogField.set( task, mockDialog );

      String result = task.selectFile( loginData, null );
      assertNull( result );
      verify( mockDialog ).setReLoginListener( listener );
    }
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testSetGetExportType() throws Exception {
    RepositoryPublishDialog mockDialog = mock( RepositoryPublishDialog.class );
    when( mockDialog.getExportType() ).thenReturn( "pageable/pdf" );

    try ( var libSwingMock = mockStatic( LibSwingUtil.class );
          var publishDialogMock = mockConstruction( RepositoryPublishDialog.class ) ) {
      libSwingMock.when( () -> LibSwingUtil.getWindowAncestor( any() ) ).thenReturn( null );
      libSwingMock.when( () -> LibSwingUtil.centerFrameOnScreen( any() ) ).thenAnswer( inv -> null );
      SelectFileForPublishTask task = new SelectFileForPublishTask( uiContext );

      Field dialogField = SelectFileForPublishTask.class.getDeclaredField( "repositoryBrowserDialog" );
      dialogField.setAccessible( true );
      dialogField.set( task, mockDialog );

      task.setExportType( "pageable/pdf" );
      verify( mockDialog ).setExportType( "pageable/pdf" );
      assertEquals( "pageable/pdf", task.getExportType() );
    }
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testSetGetDescription() throws Exception {
    RepositoryPublishDialog mockDialog = mock( RepositoryPublishDialog.class );
    when( mockDialog.getDescription() ).thenReturn( "Test Description" );

    try ( var libSwingMock = mockStatic( LibSwingUtil.class );
          var publishDialogMock = mockConstruction( RepositoryPublishDialog.class ) ) {
      libSwingMock.when( () -> LibSwingUtil.getWindowAncestor( any() ) ).thenReturn( null );
      libSwingMock.when( () -> LibSwingUtil.centerFrameOnScreen( any() ) ).thenAnswer( inv -> null );
      SelectFileForPublishTask task = new SelectFileForPublishTask( uiContext );

      Field dialogField = SelectFileForPublishTask.class.getDeclaredField( "repositoryBrowserDialog" );
      dialogField.setAccessible( true );
      dialogField.set( task, mockDialog );

      task.setDescription( "Test Description" );
      verify( mockDialog ).setDescription( "Test Description" );
      assertEquals( "Test Description", task.getDescription() );
    }
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testSetGetReportTitle() throws Exception {
    RepositoryPublishDialog mockDialog = mock( RepositoryPublishDialog.class );
    when( mockDialog.getReportTitle() ).thenReturn( "My Report" );

    try ( var libSwingMock = mockStatic( LibSwingUtil.class );
          var publishDialogMock = mockConstruction( RepositoryPublishDialog.class ) ) {
      libSwingMock.when( () -> LibSwingUtil.getWindowAncestor( any() ) ).thenReturn( null );
      libSwingMock.when( () -> LibSwingUtil.centerFrameOnScreen( any() ) ).thenAnswer( inv -> null );
      SelectFileForPublishTask task = new SelectFileForPublishTask( uiContext );

      Field dialogField = SelectFileForPublishTask.class.getDeclaredField( "repositoryBrowserDialog" );
      dialogField.setAccessible( true );
      dialogField.set( task, mockDialog );

      task.setReportTitle( "My Report" );
      verify( mockDialog ).setReportTitle( "My Report" );
      assertEquals( "My Report", task.getReportTitle() );
    }
  }

  @Test
  @SuppressWarnings( "java:S3011" )
  public void testSetGetLockOutputType() throws Exception {
    RepositoryPublishDialog mockDialog = mock( RepositoryPublishDialog.class );
    when( mockDialog.isLockOutputType() ).thenReturn( true );

    try ( var libSwingMock = mockStatic( LibSwingUtil.class );
          var publishDialogMock = mockConstruction( RepositoryPublishDialog.class ) ) {
      libSwingMock.when( () -> LibSwingUtil.getWindowAncestor( any() ) ).thenReturn( null );
      libSwingMock.when( () -> LibSwingUtil.centerFrameOnScreen( any() ) ).thenAnswer( inv -> null );
      SelectFileForPublishTask task = new SelectFileForPublishTask( uiContext );

      Field dialogField = SelectFileForPublishTask.class.getDeclaredField( "repositoryBrowserDialog" );
      dialogField.setAccessible( true );
      dialogField.set( task, mockDialog );

      task.setLockOutputType( true );
      verify( mockDialog ).setLockOutputType( true );
      assertTrue( task.isLockOutputType() );
    }
  }
}
