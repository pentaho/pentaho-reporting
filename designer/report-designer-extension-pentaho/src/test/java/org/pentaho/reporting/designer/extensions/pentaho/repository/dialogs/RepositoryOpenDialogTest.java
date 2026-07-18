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


package org.pentaho.reporting.designer.extensions.pentaho.repository.dialogs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileObject;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.designer.extensions.pentaho.repository.util.PublishUtil;

import javax.swing.JOptionPane;

public class RepositoryOpenDialogTest {

  @Test
  public void testGetLastBrowsePathWithNullSelectedViewReturnsNull() {
    final RepositoryOpenDialog dialog = mock( RepositoryOpenDialog.class, CALLS_REAL_METHODS );
    assertNull( dialog.getLastBrowsePath() );
  }

  @Test
  public void testGetLastBrowsePathReturnsDecodedPath() throws Exception {
    final RepositoryOpenDialog dialog = mock( RepositoryOpenDialog.class, CALLS_REAL_METHODS );
    doNothing().when( dialog ).pack();

    final FileObject selectedView = mock( FileObject.class );
    final FileName name = mock( FileName.class );
    doReturn( name ).when( selectedView ).getName();
    doReturn( "/public/reports" ).when( name ).getPathDecoded();

    setField( dialog, "selectedView", selectedView );

    assertEquals( "/public/reports", dialog.getLastBrowsePath() );
  }

  @Test
  public void testGetLastBrowsePathExceptionReturnsNull() throws Exception {
    final RepositoryOpenDialog dialog = mock( RepositoryOpenDialog.class, CALLS_REAL_METHODS );
    final FileObject selectedView = mock( FileObject.class );
    org.mockito.Mockito.when( selectedView.getName() ).thenThrow( new RuntimeException( "boom" ) );

    setField( dialog, "selectedView", selectedView );

    assertNull( dialog.getLastBrowsePath() );
  }

  @Test
  public void testSessionFlagsDefaultFalseAndCanBeUpdatedByReflection() throws Exception {
    final RepositoryOpenDialog dialog = mock( RepositoryOpenDialog.class, CALLS_REAL_METHODS );

    assertFalse( dialog.isSessionExpired() );
    assertFalse( dialog.isLoginAgainRequested() );

    setField( dialog, "sessionExpired", true );
    setField( dialog, "loginAgainRequested", true );

    assertTrue( dialog.isSessionExpired() );
    assertTrue( dialog.isLoginAgainRequested() );
  }

  @Test
  public void testSetSessionExpiryHandlingEnabledUpdatesField() throws Exception {
    final RepositoryOpenDialog dialog = mock( RepositoryOpenDialog.class, CALLS_REAL_METHODS );

    dialog.setSessionExpiryHandlingEnabled( true );
    assertTrue( (Boolean) getField( dialog, "sessionExpiryHandlingEnabled" ) );

    dialog.setSessionExpiryHandlingEnabled( false );
    assertFalse( (Boolean) getField( dialog, "sessionExpiryHandlingEnabled" ) );
  }

  @Test
  public void testShowSessionExpiredDialogLoginAgainSelected() throws Exception {
    final RepositoryOpenDialog dialog = mock( RepositoryOpenDialog.class, CALLS_REAL_METHODS );
    doNothing().when( dialog ).setVisible( false );

    try ( MockedStatic<JOptionPane> optionPane = org.mockito.Mockito.mockStatic( JOptionPane.class ) ) {
      optionPane.when( () -> JOptionPane.showOptionDialog( any(), any(), any(), anyInt(), anyInt(), any(), any(), any() ) )
          .thenReturn( 0 );
      invokePrivate( dialog, "showSessionExpiredDialog" );
    }

    assertTrue( dialog.isLoginAgainRequested() );
  }

  @Test
  public void testShowSessionExpiredDialogCancelSelected() throws Exception {
    final RepositoryOpenDialog dialog = mock( RepositoryOpenDialog.class, CALLS_REAL_METHODS );
    doNothing().when( dialog ).setVisible( false );

    try ( MockedStatic<JOptionPane> optionPane = org.mockito.Mockito.mockStatic( JOptionPane.class ) ) {
      optionPane.when( () -> JOptionPane.showOptionDialog( any(), any(), any(), anyInt(), anyInt(), any(), any(), any() ) )
          .thenReturn( 1 );
      invokePrivate( dialog, "showSessionExpiredDialog" );
    }

    assertFalse( dialog.isLoginAgainRequested() );
  }

  @Test
  public void testHandleFileSystemExceptionDisabledAddsToUncaughtModel() throws Exception {
    final RepositoryOpenDialog dialog = mock( RepositoryOpenDialog.class, CALLS_REAL_METHODS );
    final FileSystemException fse = new FileSystemException( "boom" );

    try ( MockedStatic<UncaughtExceptionsModel> uem = org.mockito.Mockito.mockStatic( UncaughtExceptionsModel.class ) ) {
      final UncaughtExceptionsModel model = mock( UncaughtExceptionsModel.class );
      uem.when( UncaughtExceptionsModel::getInstance ).thenReturn( model );
      invokePrivate( dialog, "handleFileSystemException", new Class<?>[] { FileSystemException.class }, fse );
      org.mockito.Mockito.verify( model ).addException( fse );
    }
  }

  @Test
  public void testHandleFileSystemExceptionEnabledNonAuthAddsToUncaughtModel() throws Exception {
    final RepositoryOpenDialog dialog = mock( RepositoryOpenDialog.class, CALLS_REAL_METHODS );
    final FileSystemException fse = new FileSystemException( "not-auth" );
    dialog.setSessionExpiryHandlingEnabled( true );

    try ( MockedStatic<PublishUtil> pu = org.mockito.Mockito.mockStatic( PublishUtil.class );
          MockedStatic<UncaughtExceptionsModel> uem = org.mockito.Mockito.mockStatic( UncaughtExceptionsModel.class ) ) {
      pu.when( () -> PublishUtil.isAuthenticationError( fse ) ).thenReturn( false );
      final UncaughtExceptionsModel model = mock( UncaughtExceptionsModel.class );
      uem.when( UncaughtExceptionsModel::getInstance ).thenReturn( model );
      invokePrivate( dialog, "handleFileSystemException", new Class<?>[] { FileSystemException.class }, fse );
      org.mockito.Mockito.verify( model ).addException( fse );
    }
  }

  @Test
  public void testHandleFileSystemExceptionEnabledAuthAlreadyExpiredReturnsEarly() throws Exception {
    final RepositoryOpenDialog dialog = mock( RepositoryOpenDialog.class, CALLS_REAL_METHODS );
    final FileSystemException fse = new FileSystemException( "auth" );
    dialog.setSessionExpiryHandlingEnabled( true );
    setField( dialog, "sessionExpired", true );

    try ( MockedStatic<PublishUtil> pu = org.mockito.Mockito.mockStatic( PublishUtil.class ) ) {
      pu.when( () -> PublishUtil.isAuthenticationError( fse ) ).thenReturn( true );
      invokePrivate( dialog, "handleFileSystemException", new Class<?>[] { FileSystemException.class }, fse );
    }

    assertTrue( dialog.isSessionExpired() );
  }

  private static Object invokePrivate( final Object target, final String methodName ) throws Exception {
    return invokePrivate( target, methodName, new Class<?>[ 0 ] );
  }

  private static Object invokePrivate( final Object target,
                                       final String methodName,
                                       final Class<?>[] paramTypes,
                                       final Object... args ) throws Exception {
    final Method m = RepositoryOpenDialog.class.getDeclaredMethod( methodName, paramTypes );
    m.setAccessible( true );
    return m.invoke( target, args );
  }

  private static void setField( final Object target, final String name, final Object value ) throws Exception {
    final Field f = RepositoryOpenDialog.class.getDeclaredField( name );
    f.setAccessible( true );
    f.set( target, value );
  }

  private static Object getField( final Object target, final String name ) throws Exception {
    final Field f = RepositoryOpenDialog.class.getDeclaredField( name );
    f.setAccessible( true );
    return f.get( target );
  }
}
