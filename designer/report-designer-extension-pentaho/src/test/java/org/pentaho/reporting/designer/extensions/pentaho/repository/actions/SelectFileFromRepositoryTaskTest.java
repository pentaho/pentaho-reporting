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



package org.pentaho.reporting.designer.extensions.pentaho.repository.actions;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.*;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;
import org.pentaho.reporting.designer.extensions.pentaho.repository.dialogs.RepositoryOpenDialog;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

public class SelectFileFromRepositoryTaskTest {

  Component uiContext;

  @Before
  public void setUp() throws Exception {
    uiContext = mock( Component.class );
  }

  @Test
  public void testSelectFileFromRepositoryTask() {
    SelectFileFromRepositoryTask task = new SelectFileFromRepositoryTask( uiContext );
    assertNotNull( task );
  }

  @Test
  public void testGetSetFilters() {
    SelectFileFromRepositoryTask task = new SelectFileFromRepositoryTask( uiContext );
    assertNotNull( task );
    assertNull( task.getFilters() );
    String[] filters = new String[] { "Manny", "Moe", "Jack" };
    task.setFilters( filters );
    assertArrayEquals( filters, task.getFilters() );
  }

  @Test
  public void testIsSessionExpiredDialogNullReturnsFalse() {
    final SelectFileFromRepositoryTask task = new SelectFileFromRepositoryTask( uiContext );
    assertFalse( task.isSessionExpired() );
  }

  @Test
  public void testIsLoginAgainRequestedDialogNullReturnsFalse() {
    final SelectFileFromRepositoryTask task = new SelectFileFromRepositoryTask( uiContext );
    assertFalse( task.isLoginAgainRequested() );
  }

  @Test
  public void testGetLastBrowsePathDialogNullReturnsNull() {
    final SelectFileFromRepositoryTask task = new SelectFileFromRepositoryTask( uiContext );
    assertNull( task.getLastBrowsePath() );
  }

  @Test
  public void testIsSessionExpiredDialogPresentTrue() {
    final SelectFileFromRepositoryTask task = new SelectFileFromRepositoryTask( uiContext );
    final RepositoryOpenDialog dialogMock = mock( RepositoryOpenDialog.class );
    when( dialogMock.isSessionExpired() ).thenReturn( true );
    setDialog( task, dialogMock );
    assertTrue( task.isSessionExpired() );
  }

  @Test
  public void testIsSessionExpiredDialogPresentFalse() {
    final SelectFileFromRepositoryTask task = new SelectFileFromRepositoryTask( uiContext );
    final RepositoryOpenDialog dialogMock = mock( RepositoryOpenDialog.class );
    when( dialogMock.isSessionExpired() ).thenReturn( false );
    setDialog( task, dialogMock );
    assertFalse( task.isSessionExpired() );
  }

  @Test
  public void testIsLoginAgainRequestedDialogPresentTrue() {
    final SelectFileFromRepositoryTask task = new SelectFileFromRepositoryTask( uiContext );
    final RepositoryOpenDialog dialogMock = mock( RepositoryOpenDialog.class );
    when( dialogMock.isLoginAgainRequested() ).thenReturn( true );
    setDialog( task, dialogMock );
    assertTrue( task.isLoginAgainRequested() );
  }

  @Test
  public void testGetLastBrowsePathDialogPresentReturnsPath() {
    final SelectFileFromRepositoryTask task = new SelectFileFromRepositoryTask( uiContext );
    final RepositoryOpenDialog dialogMock = mock( RepositoryOpenDialog.class );
    when( dialogMock.getLastBrowsePath() ).thenReturn( "/home/admin" );
    setDialog( task, dialogMock );
    assertEquals( "/home/admin", task.getLastBrowsePath() );
  }

  @Test
  public void testSetSessionExpiryHandlingEnabledDialogPresentDelegates() {
    final SelectFileFromRepositoryTask task = new SelectFileFromRepositoryTask( uiContext );
    final RepositoryOpenDialog dialogMock = mock( RepositoryOpenDialog.class );
    setDialog( task, dialogMock );

    task.setSessionExpiryHandlingEnabled( true );

    verify( dialogMock ).setSessionExpiryHandlingEnabled( true );
  }

  @Test
  public void testSelectFileAppliesSessionExpiryHandlingToNewDialog() throws Exception {
    final AuthenticationData loginData = mock( AuthenticationData.class );

    try ( MockedStatic<LibSwingUtil> ls = mockStatic( LibSwingUtil.class );
          MockedConstruction<RepositoryOpenDialog> dialogMc =
              mockConstruction( RepositoryOpenDialog.class,
                  ( m, ctx ) -> when( m.performOpen( any(), any() ) ).thenReturn( null ) ) ) {
      ls.when( () -> LibSwingUtil.getWindowAncestor( any() ) ).thenReturn( null );
      ls.when( () -> LibSwingUtil.centerFrameOnScreen( any() ) ).thenAnswer( i -> null );

      final SelectFileFromRepositoryTask task = new SelectFileFromRepositoryTask( uiContext );
      task.setSessionExpiryHandlingEnabled( true );
      task.selectFile( loginData, null );

      final RepositoryOpenDialog dialog = dialogMc.constructed().get( 0 );
      verify( dialog ).setSessionExpiryHandlingEnabled( true );
    }
  }

  @Test
  public void testSelectFileCreatesDialogDialog() throws Exception {

    AuthenticationData loginData = mock( AuthenticationData.class );
    Dialog dialog = mock( Dialog.class );

    try (
      MockedStatic<LibSwingUtil> ls = mockStatic( LibSwingUtil.class );
      MockedConstruction<RepositoryOpenDialog> dialogMc =
        mockConstruction(
          RepositoryOpenDialog.class,
          ( m, c ) -> when(
            m.performOpen( any(), any() ) )
            .thenReturn( "/test.prpt" ) )
    ) {

      ls.when( () -> LibSwingUtil.getWindowAncestor( any() ) )
        .thenReturn( dialog );

      ls.when( () -> LibSwingUtil.centerFrameOnScreen( any() ) )
        .thenAnswer( i -> null );

      SelectFileFromRepositoryTask task =
        new SelectFileFromRepositoryTask( uiContext );

      task.selectFile( loginData, "/test.prpt" );

      assertEquals( 1, dialogMc.constructed().size() );
    }
  }

  @Test
  public void testSelectFileReturnsPerformOpenResult() throws Exception {

    AuthenticationData loginData = mock( AuthenticationData.class );

    RepositoryOpenDialog dialogMock =
      mock( RepositoryOpenDialog.class );

    when(
      dialogMock.performOpen(
        loginData,
        "/public/report.prpt" ) )
      .thenReturn(
        "/public/report.prpt" );

    SelectFileFromRepositoryTask task =
      new SelectFileFromRepositoryTask( uiContext );

    task.setRepositoryBrowserDialog( dialogMock );

    String result =
      task.selectFile(
        loginData,
        "/public/report.prpt" );

    assertEquals(
      "/public/report.prpt",
      result );
  }

  @Test
  public void testSelectFileAppliesFiltersToNewDialog() throws Exception {

    AuthenticationData loginData =
      mock( AuthenticationData.class );

    String[] filters =
      new String[] { "*.prpt" };

    try (
      MockedStatic<LibSwingUtil> ls =
        mockStatic( LibSwingUtil.class );

      MockedConstruction<RepositoryOpenDialog> dialogMc =
        mockConstruction(
          RepositoryOpenDialog.class,
          ( m, c ) ->
            when(
              m.performOpen(
                any(),
                any() ) )
              .thenReturn( null ) )
    ) {

      ls.when(
          () -> LibSwingUtil.getWindowAncestor(
            any() ) )
        .thenReturn( null );

      ls.when(
          () -> LibSwingUtil.centerFrameOnScreen(
            any() ) )
        .thenAnswer( i -> null );

      SelectFileFromRepositoryTask task =
        new SelectFileFromRepositoryTask(
          uiContext );

      task.setFilters( filters );

      task.selectFile(
        loginData,
        null );

      RepositoryOpenDialog dialog =
        dialogMc.constructed().get( 0 );

      verify( dialog ).setFilters( filters );
    }
  }

  @Test
  public void testIsLoginAgainRequestedDialogPresentFalse() {

    final SelectFileFromRepositoryTask task =
      new SelectFileFromRepositoryTask( uiContext );

    final RepositoryOpenDialog dialogMock =
      mock( RepositoryOpenDialog.class );

    when( dialogMock.isLoginAgainRequested() )
      .thenReturn( false );

    task.setRepositoryBrowserDialog( dialogMock );

    assertFalse( task.isLoginAgainRequested() );
  }

  @Test
  public void testSelectFileCreatesFrameDialog() throws Exception {

    AuthenticationData loginData = mock( AuthenticationData.class );
    Frame frame = mock( Frame.class );

    try (
      MockedStatic<LibSwingUtil> ls = mockStatic( LibSwingUtil.class );
      MockedConstruction<RepositoryOpenDialog> dialogMc =
        mockConstruction(
          RepositoryOpenDialog.class,
          ( m, c ) -> when(
            m.performOpen( any(), any() ) )
            .thenReturn( "/test.prpt" ) )
    ) {

      ls.when( () -> LibSwingUtil.getWindowAncestor( any() ) )
        .thenReturn( frame );

      ls.when( () -> LibSwingUtil.centerFrameOnScreen( any() ) )
        .thenAnswer( i -> null );

      SelectFileFromRepositoryTask task =
        new SelectFileFromRepositoryTask( uiContext );

      task.selectFile( loginData, "/test.prpt" );

      assertEquals( 1, dialogMc.constructed().size() );
    }
  }

  private static void setDialog( final SelectFileFromRepositoryTask task,
                                  final RepositoryOpenDialog dialog ) {
    task.setRepositoryBrowserDialog( dialog );
  }

  @Test
  public void testSelectFileReusesExistingDialog() throws IOException {
    final SelectFileFromRepositoryTask task = new SelectFileFromRepositoryTask( uiContext );
    final RepositoryOpenDialog dialogMock = mock( RepositoryOpenDialog.class );
    final AuthenticationData loginData = mock( AuthenticationData.class );
    when( dialogMock.performOpen( loginData, "/public/a.prpt" ) ).thenReturn( "/public/a.prpt" );
    task.setRepositoryBrowserDialog( dialogMock );

    assertEquals( "/public/a.prpt", task.selectFile( loginData, "/public/a.prpt" ) );
    verify( dialogMock ).performOpen( loginData, "/public/a.prpt" );
  }
}

