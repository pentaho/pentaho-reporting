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

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;
import org.pentaho.reporting.designer.extensions.pentaho.repository.dialogs.RepositoryOpenDialog;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

public class SelectFileFromRepositoryTaskTest {

  private Component uiContext;

  @Before
  public void setUp() {
    uiContext = mock( Component.class );
  }

  @Test
  public void testConstructor() {
    SelectFileFromRepositoryTask task = new SelectFileFromRepositoryTask( uiContext );
    assertNotNull( task );
  }

  @Test
  public void testGetSetFilters() {
    SelectFileFromRepositoryTask task = new SelectFileFromRepositoryTask( uiContext );
    assertNull( task.getFilters() );
    String[] filters = new String[] { "Manny", "Moe", "Jack" };
    task.setFilters( filters );
    assertArrayEquals( filters, task.getFilters() );
  }

  @Test
  public void testSetReLoginListener() {
    SelectFileFromRepositoryTask task = new SelectFileFromRepositoryTask( uiContext );
    RepositoryOpenDialog.ReLoginListener listener = mock( RepositoryOpenDialog.ReLoginListener.class );
    task.setReLoginListener( listener );
    assertNotNull( task );
  }

  @Test
  public void testSetReLoginListenerNull() {
    SelectFileFromRepositoryTask task = new SelectFileFromRepositoryTask( uiContext );
    task.setReLoginListener( null );
    assertNotNull( task );
  }

  @Test
  public void testSetFiltersNull() {
    SelectFileFromRepositoryTask task = new SelectFileFromRepositoryTask( uiContext );
    task.setFilters( null );
    assertNull( task.getFilters() );
  }

  @Test
  public void testSetFiltersEmpty() {
    SelectFileFromRepositoryTask task = new SelectFileFromRepositoryTask( uiContext );
    task.setFilters( new String[0] );
    assertNotNull( task.getFilters() );
    assertEquals( 0, task.getFilters().length );
  }

  // ---- selectFile coverage with mock dialog ----

  @Test
  public void testSelectFileWithFrameParent() throws Exception {
    Frame frame = mock( Frame.class );
    AuthenticationData loginData = mock( AuthenticationData.class );

    try ( var libSwingMock = mockStatic( LibSwingUtil.class );
          var dialogMock = mockConstruction( RepositoryOpenDialog.class,
            ( mock, ctx ) -> when( mock.performOpen( any(), any() ) ).thenReturn( "/public/test.prpt" ) ) ) {
      libSwingMock.when( () -> LibSwingUtil.getWindowAncestor( any() ) ).thenReturn( frame );
      libSwingMock.when( () -> LibSwingUtil.centerFrameOnScreen( any() ) ).thenAnswer( inv -> null );

      SelectFileFromRepositoryTask task = new SelectFileFromRepositoryTask( uiContext );
      String result = task.selectFile( loginData, "/public/old.prpt" );
      assertEquals( "/public/test.prpt", result );
    }
  }

  @Test
  public void testSelectFileWithDialogParent() throws Exception {
    Dialog dialog = mock( Dialog.class );
    AuthenticationData loginData = mock( AuthenticationData.class );

    try ( var libSwingMock = mockStatic( LibSwingUtil.class );
          var dialogMock = mockConstruction( RepositoryOpenDialog.class,
            ( mock, ctx ) -> when( mock.performOpen( any(), any() ) ).thenReturn( null ) ) ) {
      libSwingMock.when( () -> LibSwingUtil.getWindowAncestor( any() ) ).thenReturn( dialog );
      libSwingMock.when( () -> LibSwingUtil.centerFrameOnScreen( any() ) ).thenAnswer( inv -> null );

      SelectFileFromRepositoryTask task = new SelectFileFromRepositoryTask( uiContext );
      String result = task.selectFile( loginData, null );
      assertNull( result );
    }
  }

  @Test
  public void testSelectFileWithNullWindowAncestor() throws Exception {
    AuthenticationData loginData = mock( AuthenticationData.class );

    try ( var libSwingMock = mockStatic( LibSwingUtil.class );
          var dialogMock = mockConstruction( RepositoryOpenDialog.class,
            ( mock, ctx ) -> when( mock.performOpen( any(), any() ) ).thenReturn( "/file.prpt" ) ) ) {
      libSwingMock.when( () -> LibSwingUtil.getWindowAncestor( any() ) ).thenReturn( null );
      libSwingMock.when( () -> LibSwingUtil.centerFrameOnScreen( any() ) ).thenAnswer( inv -> null );

      SelectFileFromRepositoryTask task = new SelectFileFromRepositoryTask( uiContext );
      String result = task.selectFile( loginData, "/initial" );
      assertEquals( "/file.prpt", result );
    }
  }

  @Test
  public void testSelectFileWithFiltersSet() throws Exception {
    AuthenticationData loginData = mock( AuthenticationData.class );
    String[] filters = { ".prpt", ".prpti" };

    try ( var libSwingMock = mockStatic( LibSwingUtil.class );
          var dialogMock = mockConstruction( RepositoryOpenDialog.class,
            ( mock, ctx ) -> when( mock.performOpen( any(), any() ) ).thenReturn( "/f.prpt" ) ) ) {
      libSwingMock.when( () -> LibSwingUtil.getWindowAncestor( any() ) ).thenReturn( null );
      libSwingMock.when( () -> LibSwingUtil.centerFrameOnScreen( any() ) ).thenAnswer( inv -> null );

      SelectFileFromRepositoryTask task = new SelectFileFromRepositoryTask( uiContext );
      task.setFilters( filters );
      String result = task.selectFile( loginData, null );
      assertEquals( "/f.prpt", result );

      assertFalse( dialogMock.constructed().isEmpty() );
      verify( dialogMock.constructed().get( 0 ) ).setFilters( filters );
    }
  }

  @Test
  public void testSelectFileWithReLoginListener() throws Exception {
    AuthenticationData loginData = mock( AuthenticationData.class );
    RepositoryOpenDialog.ReLoginListener listener = mock( RepositoryOpenDialog.ReLoginListener.class );

    try ( var libSwingMock = mockStatic( LibSwingUtil.class );
          var dialogMock = mockConstruction( RepositoryOpenDialog.class,
            ( mock, ctx ) -> when( mock.performOpen( any(), any() ) ).thenReturn( "/file.prpt" ) ) ) {
      libSwingMock.when( () -> LibSwingUtil.getWindowAncestor( any() ) ).thenReturn( null );
      libSwingMock.when( () -> LibSwingUtil.centerFrameOnScreen( any() ) ).thenAnswer( inv -> null );

      SelectFileFromRepositoryTask task = new SelectFileFromRepositoryTask( uiContext );
      task.setReLoginListener( listener );
      task.selectFile( loginData, null );

      verify( dialogMock.constructed().get( 0 ) ).setReLoginListener( listener );
    }
  }

  @Test
  public void testSelectFileCalledTwiceReusesSameDialog() throws Exception {
    AuthenticationData loginData = mock( AuthenticationData.class );

    try ( var libSwingMock = mockStatic( LibSwingUtil.class );
          var dialogMock = mockConstruction( RepositoryOpenDialog.class,
            ( mock, ctx ) -> when( mock.performOpen( any(), any() ) ).thenReturn( "/file.prpt" ) ) ) {
      libSwingMock.when( () -> LibSwingUtil.getWindowAncestor( any() ) ).thenReturn( null );
      libSwingMock.when( () -> LibSwingUtil.centerFrameOnScreen( any() ) ).thenAnswer( inv -> null );

      SelectFileFromRepositoryTask task = new SelectFileFromRepositoryTask( uiContext );
      task.selectFile( loginData, null );
      task.selectFile( loginData, null );

      assertEquals( 1, dialogMock.constructed().size() );
    }
  }

  @Test
  public void testSelectFileWithNullFilters() throws Exception {
    AuthenticationData loginData = mock( AuthenticationData.class );

    try ( var libSwingMock = mockStatic( LibSwingUtil.class );
          var dialogMock = mockConstruction( RepositoryOpenDialog.class,
            ( mock, ctx ) -> when( mock.performOpen( any(), any() ) ).thenReturn( "/file.prpt" ) ) ) {
      libSwingMock.when( () -> LibSwingUtil.getWindowAncestor( any() ) ).thenReturn( null );
      libSwingMock.when( () -> LibSwingUtil.centerFrameOnScreen( any() ) ).thenAnswer( inv -> null );

      SelectFileFromRepositoryTask task = new SelectFileFromRepositoryTask( uiContext );
      task.selectFile( loginData, null );

      assertFalse( dialogMock.constructed().isEmpty() );
      verify( dialogMock.constructed().get( 0 ), never() ).setFilters( any() );
    }
  }

  @Test
  public void testSelectFileWithNullReLoginListener() throws Exception {
    AuthenticationData loginData = mock( AuthenticationData.class );

    try ( var libSwingMock = mockStatic( LibSwingUtil.class );
          var dialogMock = mockConstruction( RepositoryOpenDialog.class,
            ( mock, ctx ) -> when( mock.performOpen( any(), any() ) ).thenReturn( "/file.prpt" ) ) ) {
      libSwingMock.when( () -> LibSwingUtil.getWindowAncestor( any() ) ).thenReturn( null );
      libSwingMock.when( () -> LibSwingUtil.centerFrameOnScreen( any() ) ).thenAnswer( inv -> null );

      SelectFileFromRepositoryTask task = new SelectFileFromRepositoryTask( uiContext );
      task.selectFile( loginData, null );

      assertFalse( dialogMock.constructed().isEmpty() );
      verify( dialogMock.constructed().get( 0 ), never() ).setReLoginListener( any() );
    }
  }
}
