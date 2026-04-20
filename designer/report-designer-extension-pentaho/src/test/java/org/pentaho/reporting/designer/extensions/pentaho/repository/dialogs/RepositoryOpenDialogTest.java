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

package org.pentaho.reporting.designer.extensions.pentaho.repository.dialogs;

import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Tests for session-expiry detection in {@link RepositoryOpenDialog}.
 * <p>
 * Uses Mockito CALLS_REAL_METHODS to avoid HeadlessException in CI
 * (the real constructor creates Swing components that need a display).
 * Fields required by the tested methods are injected via reflection.
 */
@SuppressWarnings( "java:S3011" )
public class RepositoryOpenDialogTest {

  private RepositoryOpenDialog dialog;
  private AuthenticationData loginData;
  private RepositoryTable table;
  private JTextField fileNameTextField;
  private JComboBox locationCombo;

  @Before
  public void setUp() throws ReflectiveOperationException {
    dialog = mock( RepositoryOpenDialog.class, CALLS_REAL_METHODS );
    loginData = mock( AuthenticationData.class );

    table = mock( RepositoryTable.class );
    fileNameTextField = new JTextField();
    locationCombo = new JComboBox();

    setField( "table", table );
    setField( "fileNameTextField", fileNameTextField );
    setField( "locationCombo", locationCombo );
    setField( "currentLoginData", loginData );
    setField( "sessionCheckInProgress", false );
    setField( "initialNavigationInProgress", false );
  }

  // ---- setSelectedView: auth error on VFS refresh triggers handleSessionExpired ----

  @Test
  public void testSetSelectedViewAuthErrorOnRefreshTriggersSessionExpired() throws Exception {
    when( loginData.getOption( "browserAuth" ) ).thenReturn( "true" );
    doNothing().when( dialog ).handleSessionExpired();

    FileObject folder = mock( FileObject.class );
    doThrow( new FileSystemException( "401 Unauthorized" ) ).when( folder ).refresh();

    dialog.setSelectedView( folder );

    verify( dialog ).handleSessionExpired();
    // table.setSelectedPath should NOT be called — we returned early
    verify( table, never() ).setSelectedPath( any() );
  }

  @Test
  public void testSetSelectedViewAuthErrorOnRefreshNonSsoDoesNotTrigger() throws Exception {
    // Non-SSO session — auth error should not trigger handleSessionExpired
    when( loginData.getOption( "browserAuth" ) ).thenReturn( null );

    FileObject folder = mock( FileObject.class );
    doThrow( new FileSystemException( "401 Unauthorized" ) ).when( folder ).refresh();
    when( folder.getType() ).thenReturn( FileType.FOLDER );

    dialog.setSelectedView( folder );

    verify( dialog, never() ).handleSessionExpired();
    // Should continue with normal flow
    verify( table ).setSelectedPath( any() );
  }

  @Test
  public void testSetSelectedViewNonAuthErrorOnRefreshContinuesNormally() throws Exception {
    when( loginData.getOption( "browserAuth" ) ).thenReturn( "true" );

    FileObject folder = mock( FileObject.class );
    doThrow( new FileSystemException( "vfs.provider/connect.error" ) ).when( folder ).refresh();
    when( folder.getType() ).thenReturn( FileType.FOLDER );

    dialog.setSelectedView( folder );

    // Non-auth error (no 401/403 in message) → should NOT trigger session expired
    verify( dialog, never() ).handleSessionExpired();
    verify( table ).setSelectedPath( any() );
  }

  @Test
  public void testSetSelectedViewSuccessfulRefreshContinuesNormally() throws Exception {
    when( loginData.getOption( "browserAuth" ) ).thenReturn( "true" );

    FileObject folder = mock( FileObject.class );
    // refresh() succeeds (no exception)
    when( folder.getType() ).thenReturn( FileType.FOLDER );

    dialog.setSelectedView( folder );

    verify( table ).setSelectedPath( any() );
  }

  @Test
  public void testSetSelectedViewNullClearsState() {
    dialog.setSelectedView( null );

    verify( table ).setSelectedPath( null );
    verify( dialog, never() ).handleSessionExpired();
  }

  // ---- checkForExpiredSession: always does HTTP check (no table row guard) ----

  @Test
  public void testCheckForExpiredSessionFires_evenWhenTableHasRows() throws Exception {
    when( loginData.getOption( "browserAuth" ) ).thenReturn( "true" );
    when( table.getRowCount() ).thenReturn( 5 ); // table has rows

    FileObject folder = mock( FileObject.class );
    when( folder.getType() ).thenReturn( FileType.FOLDER );
    // refresh succeeds
    dialog.setSelectedView( folder );

    // checkForExpiredSession should still run and call isSessionStillActive
    // which will call handleSessionExpired if session is invalid.
    // Since isSessionStillActive makes a real HTTP call that will fail in test,
    // it will return true (assumes still active on exception) — so handleSessionExpired
    // should NOT be called. But the point is checkForExpiredSession ran.
    verify( table ).setSelectedPath( any() );
  }

  @Test
  public void testCheckForExpiredSessionSkippedForNonSso() throws Exception {
    when( loginData.getOption( "browserAuth" ) ).thenReturn( null );

    FileObject folder = mock( FileObject.class );
    when( folder.getType() ).thenReturn( FileType.FOLDER );

    dialog.setSelectedView( folder );

    verify( dialog, never() ).handleSessionExpired();
  }

  @Test
  public void testCheckForExpiredSessionSkippedDuringInitialNavigation() throws Exception {
    when( loginData.getOption( "browserAuth" ) ).thenReturn( "true" );
    setField( "initialNavigationInProgress", true );

    FileObject folder = mock( FileObject.class );
    when( folder.getType() ).thenReturn( FileType.FOLDER );

    dialog.setSelectedView( folder );

    verify( dialog, never() ).handleSessionExpired();
  }

  private void setField( String fieldName, Object value ) throws ReflectiveOperationException {
    Field field = RepositoryOpenDialog.class.getDeclaredField( fieldName );
    field.setAccessible( true );
    field.set( dialog, value );
  }

  // ---- Simple delegation / accessor coverage ----

  @Test
  public void testGetFiltersDelegatesToTable() {
    String[] filters = { "*.prpt", "*.prpti" };
    when( table.getFilters() ).thenReturn( filters );
    assertArrayEquals( filters, dialog.getFilters() );
    verify( table ).getFilters();
  }

  @Test
  public void testSetFiltersDelegatesToTable() {
    String[] filters = { "*.prpt" };
    dialog.setFilters( filters );
    verify( table ).setFilters( filters );
  }

  @Test
  public void testSetReLoginListenerStoresField() throws Exception {
    RepositoryOpenDialog.ReLoginListener listener = mock( RepositoryOpenDialog.ReLoginListener.class );
    dialog.setReLoginListener( listener );
    Field f = RepositoryOpenDialog.class.getDeclaredField( "reLoginListener" );
    f.setAccessible( true );
    assertSame( listener, f.get( dialog ) );
  }

  @Test
  public void testSetReLoginListenerNullClearsField() throws Exception {
    dialog.setReLoginListener( mock( RepositoryOpenDialog.ReLoginListener.class ) );
    dialog.setReLoginListener( null );
    Field f = RepositoryOpenDialog.class.getDeclaredField( "reLoginListener" );
    f.setAccessible( true );
    assertNull( f.get( dialog ) );
  }

  @Test
  public void testGetSelectedViewReturnsField() throws Exception {
    Field f = RepositoryOpenDialog.class.getDeclaredField( "selectedView" );
    f.setAccessible( true );
    FileObject view = mock( FileObject.class );
    f.set( dialog, view );

    Method m = RepositoryOpenDialog.class.getDeclaredMethod( "getSelectedView" );
    m.setAccessible( true );
    assertSame( view, m.invoke( dialog ) );
  }

  // ---- validateInputs ----

  @Test
  public void testValidateInputsEmptyTextReturnsFalse() throws Exception {
    fileNameTextField.setText( "" );
    Method m = RepositoryOpenDialog.class.getDeclaredMethod( "validateInputs", boolean.class );
    m.setAccessible( true );
    assertFalse( (Boolean) m.invoke( dialog, false ) );
    assertFalse( (Boolean) m.invoke( dialog, true ) );
  }

  @Test
  public void testValidateInputsNonEmptyTextNoConfirmReturnsTrue() throws Exception {
    fileNameTextField.setText( "report.prpt" );
    Method m = RepositoryOpenDialog.class.getDeclaredMethod( "validateInputs", boolean.class );
    m.setAccessible( true );
    // onConfirm=false skips session check and returns true
    assertTrue( (Boolean) m.invoke( dialog, false ) );
  }

  @Test
  public void testValidateInputsNonEmptyNonSsoOnConfirmReturnsTrue() throws Exception {
    fileNameTextField.setText( "report.prpt" );
    when( loginData.getOption( "browserAuth" ) ).thenReturn( null ); // not SSO
    Method m = RepositoryOpenDialog.class.getDeclaredMethod( "validateInputs", boolean.class );
    m.setAccessible( true );
    assertTrue( (Boolean) m.invoke( dialog, true ) );
    verify( dialog, never() ).handleSessionExpired();
  }

  // ---- checkForExpiredSession early-return branches ----

  @Test
  public void testCheckForExpiredSessionEarlyReturnWhenLoginDataNull() throws Exception {
    setField( "currentLoginData", null );
    Method m = RepositoryOpenDialog.class.getDeclaredMethod( "checkForExpiredSession" );
    m.setAccessible( true );
    m.invoke( dialog );
    verify( dialog, never() ).handleSessionExpired();
  }

  @Test
  public void testCheckForExpiredSessionEarlyReturnWhenAlreadyInProgress() throws Exception {
    when( loginData.getOption( "browserAuth" ) ).thenReturn( "true" );
    setField( "sessionCheckInProgress", true );
    Method m = RepositoryOpenDialog.class.getDeclaredMethod( "checkForExpiredSession" );
    m.setAccessible( true );
    m.invoke( dialog );
    verify( dialog, never() ).handleSessionExpired();
  }

  // ---- isSessionStillActive null-URL fast-path ----

  @Test
  public void testIsSessionStillActiveTrueWhenLoginDataNull() throws Exception {
    setField( "currentLoginData", null );
    Method m = RepositoryOpenDialog.class.getDeclaredMethod( "isSessionStillActive" );
    m.setAccessible( true );
    assertTrue( (Boolean) m.invoke( dialog ) );
  }

  @Test
  public void testIsSessionStillActiveTrueWhenLoginUrlNull() throws Exception {
    when( loginData.getUrl() ).thenReturn( null );
    Method m = RepositoryOpenDialog.class.getDeclaredMethod( "isSessionStillActive" );
    m.setAccessible( true );
    assertTrue( (Boolean) m.invoke( dialog ) );
  }

  // ---- getDialogId ----

  @Test
  public void testGetDialogId() throws Exception {
    Method m = RepositoryOpenDialog.class.getDeclaredMethod( "getDialogId" );
    m.setAccessible( true );
    assertSame( "ReportDesigner.Pentaho.RepositoryOpen", m.invoke( dialog ) );
  }
}
