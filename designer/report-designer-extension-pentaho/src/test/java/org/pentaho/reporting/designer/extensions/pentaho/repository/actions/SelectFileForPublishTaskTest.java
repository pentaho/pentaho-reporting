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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.*;
import java.io.IOException;

import org.junit.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;
import org.pentaho.reporting.designer.extensions.pentaho.repository.dialogs.RepositoryPublishDialog;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

public class SelectFileForPublishTaskTest {

  private static SelectFileForPublishTask createTask() {
    try ( MockedStatic<LibSwingUtil> ls = mockStatic( LibSwingUtil.class );
          MockedConstruction<RepositoryPublishDialog> ignored =
              mockConstruction( RepositoryPublishDialog.class ) ) {
      ls.when( () -> LibSwingUtil.getWindowAncestor( any() ) ).thenReturn( null );
      ls.when( () -> LibSwingUtil.centerFrameOnScreen( any() ) ).thenAnswer( i -> null );
      return new SelectFileForPublishTask( mock( Component.class ) );
    }
  }

  @Test
  public void testConstructorNotNull() {
    try ( MockedStatic<LibSwingUtil> ls = mockStatic( LibSwingUtil.class );
          MockedConstruction<RepositoryPublishDialog> ignored =
              mockConstruction( RepositoryPublishDialog.class ) ) {
      ls.when( () -> LibSwingUtil.getWindowAncestor( any() ) ).thenReturn( null );
      ls.when( () -> LibSwingUtil.centerFrameOnScreen( any() ) ).thenAnswer( i -> null );
      assertNotNull( new SelectFileForPublishTask( mock( Component.class ) ) );
    }
  }

  @Test
  public void testIsSessionExpiredDialogNullReturnsFalse() {
    final SelectFileForPublishTask task = createTask();
    // Force null to exercise the null-guard branch
    task.setRepositoryBrowserDialog( null );
    assertFalse( task.isSessionExpired() );
  }

  @Test
  public void testIsLoginAgainRequestedDialogNullReturnsFalse() {
    final SelectFileForPublishTask task = createTask();
    task.setRepositoryBrowserDialog( null );
    assertFalse( task.isLoginAgainRequested() );
  }

  @Test
  public void testGetLastBrowsePathDialogNullReturnsNull() {
    final SelectFileForPublishTask task = createTask();
    task.setRepositoryBrowserDialog( null );
    assertNull( task.getLastBrowsePath() );
  }

  @Test
  public void testIsSessionExpiredDialogPresentDelegatesToDialog() {
    final SelectFileForPublishTask task = createTask();
    final RepositoryPublishDialog dialogMock = mock( RepositoryPublishDialog.class );
    when( dialogMock.isSessionExpired() ).thenReturn( true );
    task.setRepositoryBrowserDialog( dialogMock );

    assertTrue( task.isSessionExpired() );
  }

  @Test
  public void testIsLoginAgainRequestedDialogPresentDelegatesToDialog() {
    final SelectFileForPublishTask task = createTask();
    final RepositoryPublishDialog dialogMock = mock( RepositoryPublishDialog.class );
    when( dialogMock.isLoginAgainRequested() ).thenReturn( true );
    task.setRepositoryBrowserDialog( dialogMock );

    assertTrue( task.isLoginAgainRequested() );
  }

  @Test
  public void testGetLastBrowsePathDialogPresentDelegatesToDialog() {
    final SelectFileForPublishTask task = createTask();
    final RepositoryPublishDialog dialogMock = mock( RepositoryPublishDialog.class );
    when( dialogMock.getLastBrowsePath() ).thenReturn( "/home/admin" );
    task.setRepositoryBrowserDialog( dialogMock );

    assertEquals( "/home/admin", task.getLastBrowsePath() );
  }

  @Test
  public void testSetSessionExpiryHandlingEnabledDelegatesToDialog() {
    final SelectFileForPublishTask task = createTask();
    final RepositoryPublishDialog dialogMock = mock( RepositoryPublishDialog.class );
    task.setRepositoryBrowserDialog( dialogMock );

    task.setSessionExpiryHandlingEnabled( true );

    verify( dialogMock ).setSessionExpiryHandlingEnabled( true );
  }

  @Test
  public void testSelectFileDelegatesToDialog() throws IOException {
    final SelectFileForPublishTask task = createTask();
    final RepositoryPublishDialog dialogMock = mock( RepositoryPublishDialog.class );
    final AuthenticationData loginData = mock( AuthenticationData.class );
    when( dialogMock.performOpen( loginData, "/public/a.prpt" ) ).thenReturn( "/public/a.prpt" );
    task.setRepositoryBrowserDialog( dialogMock );

    assertEquals( "/public/a.prpt", task.selectFile( loginData, "/public/a.prpt" ) );
  }

  @Test
  public void testExportTypeDescriptionTitleAndLockDelegates() {
    final SelectFileForPublishTask task = createTask();
    final RepositoryPublishDialog dialogMock = mock( RepositoryPublishDialog.class );
    task.setRepositoryBrowserDialog( dialogMock );

    task.setExportType( "pdf" );
    when( dialogMock.getExportType() ).thenReturn( "pdf" );
    assertEquals( "pdf", task.getExportType() );

    task.setDescription( "desc" );
    when( dialogMock.getDescription() ).thenReturn( "desc" );
    assertEquals( "desc", task.getDescription() );

    task.setReportTitle( "title" );
    when( dialogMock.getReportTitle() ).thenReturn( "title" );
    assertEquals( "title", task.getReportTitle() );

    task.setLockOutputType( true );
    when( dialogMock.isLockOutputType() ).thenReturn( true );
    assertTrue( task.isLockOutputType() );

    verify( dialogMock ).setExportType( "pdf" );
    verify( dialogMock ).setDescription( "desc" );
    verify( dialogMock ).setReportTitle( "title" );
    verify( dialogMock ).setLockOutputType( true );
  }

  @Test
  public void testConstructorWithFrameParent() {

    Frame frame = mock( Frame.class );

    try (
      MockedStatic<LibSwingUtil> ls = mockStatic( LibSwingUtil.class );
      MockedConstruction<RepositoryPublishDialog> dialogs =
        mockConstruction( RepositoryPublishDialog.class )
    ) {

      ls.when( () -> LibSwingUtil.getWindowAncestor( any() ) )
        .thenReturn( frame );

      ls.when( () -> LibSwingUtil.centerFrameOnScreen( any() ) )
        .thenAnswer( i -> null );

      SelectFileForPublishTask task =
        new SelectFileForPublishTask(
          mock( Component.class ) );

      assertNotNull( task );
      assertEquals( 1, dialogs.constructed().size() );
    }
  }

  @Test
  public void testConstructorWithDialogParent() {

    Dialog dialog = mock( Dialog.class );

    try (
      MockedStatic<LibSwingUtil> ls = mockStatic( LibSwingUtil.class );
      MockedConstruction<RepositoryPublishDialog> dialogs =
        mockConstruction( RepositoryPublishDialog.class )
    ) {

      ls.when( () -> LibSwingUtil.getWindowAncestor( any() ) )
        .thenReturn( dialog );

      ls.when( () -> LibSwingUtil.centerFrameOnScreen( any() ) )
        .thenAnswer( i -> null );

      SelectFileForPublishTask task =
        new SelectFileForPublishTask(
          mock( Component.class ) );

      assertNotNull( task );
      assertEquals( 1, dialogs.constructed().size() );
    }
  }

  @Test
  public void testIsSessionExpiredDialogPresentReturnsFalse() {

    final SelectFileForPublishTask task = createTask();

    final RepositoryPublishDialog dialogMock =
      mock( RepositoryPublishDialog.class );

    when( dialogMock.isSessionExpired() )
      .thenReturn( false );

    task.setRepositoryBrowserDialog( dialogMock );

    assertFalse( task.isSessionExpired() );
  }

  @Test
  public void testIsLoginAgainRequestedDialogPresentReturnsFalse() {

    final SelectFileForPublishTask task = createTask();

    final RepositoryPublishDialog dialogMock =
      mock( RepositoryPublishDialog.class );

    when( dialogMock.isLoginAgainRequested() )
      .thenReturn( false );

    task.setRepositoryBrowserDialog( dialogMock );

    assertFalse( task.isLoginAgainRequested() );
  }
}
