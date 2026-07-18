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



package org.pentaho.reporting.designer.extensions.pentaho.repository.model;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.pentaho.reporting.designer.extensions.pentaho.repository.Messages;
import org.pentaho.reporting.designer.extensions.pentaho.repository.util.PublishUtil;

public class RepositoryTableModelTest {

  FileObject fileObject, childFile1, childFile2, childFile3;
  FileName childFileName1, childFileName2, childFileName3;
  FileContent childFileContent1, childFileContent2, childFileContent3;

  @Before
  public void setUp() throws Exception {
    fileObject = mock( FileObject.class );
    childFile1 = mock( FileObject.class );
    childFile2 = mock( FileObject.class );
    childFile3 = mock( FileObject.class );
    childFileName1 = mock( FileName.class );
    childFileName2 = mock( FileName.class );
    childFileName3 = mock( FileName.class );
    childFileContent1 = mock( FileContent.class );
    childFileContent2 = mock( FileContent.class );
    childFileContent3 = mock( FileContent.class );
  }

  @Test
  public void testRepositoryTableModel() {
    RepositoryTableModel repoTableModel = new RepositoryTableModel();
    assertNotNull( repoTableModel );
  }

  @Test
  public void testIsSetShowHiddenFiles() {
    RepositoryTableModel repoTableModel = new RepositoryTableModel();
    assertNotNull( repoTableModel );
    assertFalse( repoTableModel.isShowHiddenFiles() );

    repoTableModel.setShowHiddenFiles( true );
    assertTrue( repoTableModel.isShowHiddenFiles() );
  }

  @Test
  public void testGetSetFilters() {
    String[] filters = new String[] { "Larry", "Moe", "Curly" };
    RepositoryTableModel repoTableModel = new RepositoryTableModel();
    assertNotNull( repoTableModel );
    assertEquals( 0, repoTableModel.getFilters().length );

    repoTableModel.setFilters( filters );
    assertArrayEquals( filters, repoTableModel.getFilters() );
  }

  @Test
  public void testGetSetSelectedPath() {
    RepositoryTableModel repoTableModel = new RepositoryTableModel();
    assertNotNull( repoTableModel );
    assertNull( repoTableModel.getSelectedPath() );

    repoTableModel.setSelectedPath( fileObject );
    assertEquals( fileObject, repoTableModel.getSelectedPath() );
  }

  @Test
  public void testGetColumnCount() {
    RepositoryTableModel repoTableModel = new RepositoryTableModel();
    assertNotNull( repoTableModel );
    assertEquals( 4, repoTableModel.getColumnCount() );
  }

  @Test
  public void testGetRowCount() {
    RepositoryTableModel repoTableModel = new RepositoryTableModel();
    assertNotNull( repoTableModel );
    assertEquals( 0, repoTableModel.getRowCount() );

    repoTableModel.setSelectedPath( fileObject );
    assertEquals( 0, repoTableModel.getRowCount() );

    try {
      doReturn( FileType.FOLDER ).when( fileObject ).getType();
      FileObject[] childFiles = new FileObject[] { childFile1, childFile2, childFile3 };
      doReturn( childFileName1 ).when( childFile1 ).getName();
      doReturn( childFileName2 ).when( childFile2 ).getName();
      doReturn( childFileName3 ).when( childFile3 ).getName();
      doReturn( "file1.txt" ).when( childFileName1 ).getBaseName();
      doReturn( "file2.txt" ).when( childFileName2 ).getBaseName();
      doReturn( "file3.txt" ).when( childFileName3 ).getBaseName();
      doReturn( childFiles ).when( fileObject ).getChildren();
      assertEquals( 3, repoTableModel.getRowCount() );
    } catch ( FileSystemException e ) {
      e.printStackTrace();
    }
  }

  @Test
  public void testGetColumnNameInt() {
    RepositoryTableModel repoTableModel = new RepositoryTableModel();
    assertNotNull( repoTableModel );

    assertEquals( Messages.getInstance().getString( "SolutionRepositoryTableView.Title" ), repoTableModel
        .getColumnName( 0 ) );
    assertEquals( Messages.getInstance().getString( "SolutionRepositoryTableView.Name" ), repoTableModel
        .getColumnName( 1 ) );
    assertEquals( Messages.getInstance().getString( "SolutionRepositoryTableView.DateModified" ), repoTableModel
        .getColumnName( 2 ) );
    assertEquals( Messages.getInstance().getString( "SolutionRepositoryTableView.Description" ), repoTableModel
        .getColumnName( 3 ) );
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testGetColumnNameInvalidThrowsIndexOutOfBounds() {
    final RepositoryTableModel repoTableModel = new RepositoryTableModel();
    repoTableModel.getColumnName( 99 );
  }

  @Test
  public void testHiddenFileVisibleWhenShowHiddenEnabled() throws Exception {

    RepositoryTableModel model = new RepositoryTableModel();

    model.setSelectedPath( fileObject );
    model.setShowHiddenFiles( true );

    doReturn( FileType.FOLDER ).when( fileObject ).getType();

    doReturn( true ).when( childFile1 ).isHidden();
    doReturn( FileType.FOLDER ).when( childFile1 ).getType();

    doReturn( new FileObject[] { childFile1 } )
      .when( fileObject ).getChildren();

    assertEquals( 1, model.getRowCount() );
  }

  @Test
  public void testFileRejectedByFilter() throws Exception {

    RepositoryTableModel model = new RepositoryTableModel();

    model.setSelectedPath( fileObject );
    model.setShowHiddenFiles( true );

    doReturn( FileType.FOLDER ).when( fileObject ).getType();

    doReturn( false ).when( childFile1 ).isHidden();
    doReturn( FileType.FILE ).when( childFile1 ).getType();

    doReturn( childFileName1 ).when( childFile1 ).getName();
    doReturn( "file1.txt" ).when( childFileName1 ).getBaseName();

    doReturn( new FileObject[] { childFile1 } )
      .when( fileObject ).getChildren();

    try ( MockedStatic<PublishUtil> publishUtil =
            mockStatic( PublishUtil.class ) ) {

      publishUtil.when(
          () -> PublishUtil.acceptFilter(
            any(),
            any() ) )
        .thenReturn( false );

      assertEquals( 0, model.getRowCount() );
    }
  }

  @Test
  public void testFileAcceptedByFilter() throws Exception {

    RepositoryTableModel model = new RepositoryTableModel();

    model.setSelectedPath( fileObject );
    model.setShowHiddenFiles( true );

    doReturn( FileType.FOLDER ).when( fileObject ).getType();

    doReturn( false ).when( childFile1 ).isHidden();
    doReturn( FileType.FILE ).when( childFile1 ).getType();

    doReturn( childFileName1 ).when( childFile1 ).getName();
    doReturn( "file1.prpt" ).when( childFileName1 ).getBaseName();

    doReturn( new FileObject[] { childFile1 } )
      .when( fileObject ).getChildren();

    try ( MockedStatic<PublishUtil> publishUtil =
            mockStatic( PublishUtil.class ) ) {

      publishUtil.when(
          () -> PublishUtil.acceptFilter(
            any(),
            any() ) )
        .thenReturn( true );

      assertEquals( 1, model.getRowCount() );
    }
  }

  @Test
  public void testGetElementForRow() {
    RepositoryTableModel repoTableModel = new RepositoryTableModel();
    assertNotNull( repoTableModel );
    repoTableModel.setSelectedPath( fileObject );

    try {
      doReturn( FileType.FOLDER ).when( fileObject ).getType();
      FileObject[] childFiles = new FileObject[] { childFile1, childFile2, childFile3 };
      doReturn( childFileName1 ).when( childFile1 ).getName();
      doReturn( childFileName2 ).when( childFile2 ).getName();
      doReturn( childFileName3 ).when( childFile3 ).getName();
      doReturn( "file1.txt" ).when( childFileName1 ).getBaseName();
      doReturn( "file2.txt" ).when( childFileName2 ).getBaseName();
      doReturn( "file3.txt" ).when( childFileName3 ).getBaseName();
      doReturn( childFiles ).when( fileObject ).getChildren();
      assertEquals( childFile2, repoTableModel.getElementForRow( 1 ) );
    } catch ( FileSystemException e ) {
      e.printStackTrace();
    }

  }

  @Test
  public void testGetValueAt() {
    final String localizedName1 = "fileName1";
    final String localizedName2 = "fileName2";
    final String localizedName3 = "fileName3";
    final String description1 = "description1";
    final String description2 = "description2";
    final String description3 = "description3";
    final long modifiedTime = System.currentTimeMillis();

    RepositoryTableModel repoTableModel = new RepositoryTableModel();
    assertNotNull( repoTableModel );
    repoTableModel.setSelectedPath( fileObject );

    try {
      doReturn( FileType.FOLDER ).when( fileObject ).getType();
      FileObject[] childFiles = new FileObject[] { childFile1, childFile2, childFile3 };
      doReturn( childFileName1 ).when( childFile1 ).getName();
      doReturn( childFileName2 ).when( childFile2 ).getName();
      doReturn( childFileName3 ).when( childFile3 ).getName();
      doReturn( "file1.txt" ).when( childFileName1 ).getBaseName();
      doReturn( "file2.txt" ).when( childFileName2 ).getBaseName();
      doReturn( "file3.txt" ).when( childFileName3 ).getBaseName();
      doReturn( childFileContent1 ).when( childFile1 ).getContent();
      doReturn( childFileContent2 ).when( childFile2 ).getContent();
      doReturn( childFileContent3 ).when( childFile3 ).getContent();
      doReturn( localizedName1 ).when( childFileContent1 ).getAttribute( "localized-name" );
      doReturn( localizedName2 ).when( childFileContent2 ).getAttribute( "localized-name" );
      doReturn( localizedName3 ).when( childFileContent3 ).getAttribute( "localized-name" );
      doReturn( description1 ).when( childFileContent1 ).getAttribute( "description" );
      doReturn( description2 ).when( childFileContent2 ).getAttribute( "description" );
      doReturn( description3 ).when( childFileContent3 ).getAttribute( "description" );
      doReturn( modifiedTime ).when( childFileContent1 ).getLastModifiedTime();
      doReturn( modifiedTime ).when( childFileContent2 ).getLastModifiedTime();
      doReturn( modifiedTime ).when( childFileContent3 ).getLastModifiedTime();
      doReturn( childFiles ).when( fileObject ).getChildren();
      assertEquals( localizedName1, repoTableModel.getValueAt( 0, 0 ) );
      assertEquals( "file1.txt", repoTableModel.getValueAt( 0, 1 ) );
      assertEquals( new Date( modifiedTime ), repoTableModel.getValueAt( 0, 2 ) );
      assertEquals( description1, repoTableModel.getValueAt( 0, 3 ) );
    } catch ( FileSystemException e ) {
      e.printStackTrace();
    }
  }

  @Test
  public void testGetColumnClassInt() {
    RepositoryTableModel repoTableModel = new RepositoryTableModel();
    assertNotNull( repoTableModel );

    assertEquals( Date.class, repoTableModel.getColumnClass( 2 ) );
    assertEquals( String.class, repoTableModel.getColumnClass( 1 ) );
  }

  @Test
  public void testGetValueAtDateColumnWithUnknownModifiedTimeReturnsNull() throws Exception {
    final RepositoryTableModel repoTableModel = new RepositoryTableModel();
    repoTableModel.setSelectedPath( fileObject );

    doReturn( FileType.FOLDER ).when( fileObject ).getType();
    doReturn( childFileName1 ).when( childFile1 ).getName();
    doReturn( "file1.txt" ).when( childFileName1 ).getBaseName();
    doReturn( childFileContent1 ).when( childFile1 ).getContent();
    doReturn( -1L ).when( childFileContent1 ).getLastModifiedTime();
    doReturn( new FileObject[] { childFile1 } ).when( fileObject ).getChildren();

    assertNull( repoTableModel.getValueAt( 0, 2 ) );
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testGetValueAtInvalidColumnThrowsIndexOutOfBounds() throws Exception {
    final RepositoryTableModel repoTableModel = new RepositoryTableModel();
    repoTableModel.setSelectedPath( fileObject );

    doReturn( FileType.FOLDER ).when( fileObject ).getType();
    doReturn( childFileName1 ).when( childFile1 ).getName();
    doReturn( "file1.txt" ).when( childFileName1 ).getBaseName();
    doReturn( new FileObject[] { childFile1 } ).when( fileObject ).getChildren();
    doReturn( childFileContent1 ).when( childFile1 ).getContent();

    repoTableModel.getValueAt( 0, 99 );
  }

  @Test
  public void testHiddenFileFilteredWhenShowHiddenDisabled() throws Exception {
    final RepositoryTableModel repoTableModel = new RepositoryTableModel();
    repoTableModel.setSelectedPath( fileObject );

    doReturn( FileType.FOLDER ).when( fileObject ).getType();
    doReturn( true ).when( childFile1 ).isHidden();
    doReturn( false ).when( childFile2 ).isHidden();
    doReturn( FileType.FILE ).when( childFile2 ).getType();
    doReturn( childFileName2 ).when( childFile2 ).getName();
    doReturn( "file2.prpt" ).when( childFileName2 ).getBaseName();
    doReturn( new FileObject[] { childFile1, childFile2 } ).when( fileObject ).getChildren();

    repoTableModel.setFilters( new String[] { ".prpt" } );
    repoTableModel.setShowHiddenFiles( false );

    assertEquals( 1, repoTableModel.getRowCount() );
    assertEquals( childFile2, repoTableModel.getElementForRow( 0 ) );
  }

  @Test
  public void testAuthenticationErrorTriggersSessionExpiredListener() throws Exception {
    final RepositoryTableModel repoTableModel = new RepositoryTableModel();
    repoTableModel.setSelectedPath( fileObject );
    doReturn( FileType.FOLDER ).when( fileObject ).getType();
    when( fileObject.getChildren() ).thenThrow( new FileSystemException( "401" ) );

    final AtomicBoolean called = new AtomicBoolean( false );
    repoTableModel.setSessionExpiredListener( ex -> called.set( true ) );

    try ( MockedStatic<PublishUtil> publishUtil = mockStatic( PublishUtil.class ) ) {
      publishUtil.when( () -> PublishUtil.isAuthenticationError( any() ) ).thenReturn( true );

      assertEquals( 0, repoTableModel.getRowCount() );
      assertTrue( called.get() );
    }
  }

  @Test
  public void testNonAuthenticationErrorDoesNotTriggerSessionExpiredListener() throws Exception {
    final RepositoryTableModel repoTableModel = new RepositoryTableModel();
    repoTableModel.setSelectedPath( fileObject );
    doReturn( FileType.FOLDER ).when( fileObject ).getType();
    when( fileObject.getChildren() ).thenThrow( new FileSystemException( "io" ) );

    final AtomicBoolean called = new AtomicBoolean( false );
    repoTableModel.setSessionExpiredListener( ex -> called.set( true ) );

    try ( MockedStatic<PublishUtil> publishUtil = mockStatic( PublishUtil.class ) ) {
      publishUtil.when( () -> PublishUtil.isAuthenticationError( any() ) ).thenReturn( false );
      assertEquals( 0, repoTableModel.getRowCount() );
      assertFalse( called.get() );
    }
  }

  @Test
  public void testGetValueAtHandlesFileSystemException() throws Exception {

    RepositoryTableModel model = new RepositoryTableModel();
    model.setSelectedPath( fileObject );

    doReturn( FileType.FOLDER ).when( fileObject ).getType();
    doReturn( new FileObject[] { childFile1 } ).when( fileObject ).getChildren();
    doReturn( childFileName1 ).when( childFile1 ).getName();
    doReturn( "file1.prpt" ).when( childFileName1 ).getBaseName();

    when( childFile1.getContent() )
      .thenThrow( new FileSystemException( "boom" ) );

    assertNull( model.getValueAt( 0, 0 ) );
  }

  @Test
  public void testGetValueAtUnsupportedEncodingCatch() throws Exception {

    RepositoryTableModel model = new RepositoryTableModel();

    try ( MockedStatic<java.net.URLDecoder> decoder =
            mockStatic( java.net.URLDecoder.class ) ) {

      decoder.when(
          () -> java.net.URLDecoder.decode(
            org.mockito.ArgumentMatchers.anyString(),
            org.mockito.ArgumentMatchers.eq( "UTF-8" ) ) )
        .thenThrow(
          new UnsupportedEncodingException( "forced" ) );

      model.setSelectedPath( fileObject );

      doReturn( FileType.FOLDER ).when( fileObject ).getType();
      doReturn( new FileObject[] { childFile1 } ).when( fileObject ).getChildren();
      doReturn( childFileName1 ).when( childFile1 ).getName();
      doReturn( "file1.prpt" ).when( childFileName1 ).getBaseName();

      assertNull( model.getValueAt( 0, 1 ) );
    }
  }

  @Test
  public void testGetColumnClassAllBranches() {

    RepositoryTableModel model = new RepositoryTableModel();

    assertEquals(
      Date.class,
      model.getColumnClass( 2 ) );

    assertEquals(
      String.class,
      model.getColumnClass( 0 ) );

    assertEquals(
      String.class,
      model.getColumnClass( 1 ) );

    assertEquals(
      String.class,
      model.getColumnClass( 3 ) );
  }

  @Test
  public void testAuthenticationErrorWithNullListener() throws Exception {

    RepositoryTableModel model =
      new RepositoryTableModel();

    model.setSelectedPath( fileObject );

    doReturn( FileType.FOLDER )
      .when( fileObject ).getType();

    when(
        fileObject.getChildren() )
      .thenThrow(
        new FileSystemException( "401" ) );

    try ( MockedStatic<PublishUtil> publishUtil =
            mockStatic( PublishUtil.class ) ) {

      publishUtil.when(
          () -> PublishUtil.isAuthenticationError(
            any() ) )
        .thenReturn( true );

      assertEquals(
        0,
        model.getRowCount() );
    }
  }

  @Test
  public void testGetElementForRowHandlesFileSystemException() throws Exception {

    RepositoryTableModel model =
      new RepositoryTableModel();

    model.setSelectedPath( fileObject );

    doReturn( FileType.FOLDER )
      .when( fileObject ).getType();

    when(
        fileObject.getChildren() )
      .thenThrow(
        new FileSystemException( "boom" ) );

    assertNull(
      model.getElementForRow( 0 ) );
  }

  @Test
  public void testGetElementForRowReturnsNullWhenSelectedPathNull() {

    RepositoryTableModel model =
      new RepositoryTableModel();

    assertNull(
      model.getElementForRow( 0 ) );
  }

  @Test
  public void testGetElementForRowReturnsNullWhenNotFolder()
    throws Exception {

    RepositoryTableModel model =
      new RepositoryTableModel();

    FileObject selected =
      mock( FileObject.class );

    model.setSelectedPath( selected );

    when( selected.getType() )
      .thenReturn( FileType.FILE );

    assertNull(
      model.getElementForRow( 0 ) );
  }

  @Test
  public void testGetElementForRowReturnsNullWhenRowNotFound()
    throws Exception {

    RepositoryTableModel model =
      new RepositoryTableModel();

    FileObject selected =
      mock( FileObject.class );

    FileObject child =
      mock( FileObject.class );

    org.apache.commons.vfs2.FileName name =
      mock( org.apache.commons.vfs2.FileName.class );

    model.setSelectedPath( selected );

    when( selected.getType() )
      .thenReturn( FileType.FOLDER );

    when( selected.getChildren() )
      .thenReturn( new FileObject[] { child } );

    when( child.isHidden() )
      .thenReturn( false );

    when( child.getType() )
      .thenReturn( FileType.FOLDER );

    when( child.getName() )
      .thenReturn( name );

    when( name.getBaseName() )
      .thenReturn( "test.prpt" );

    // only row 0 exists, ask for row 5
    assertNull(
      model.getElementForRow( 5 ) );
  }
}
