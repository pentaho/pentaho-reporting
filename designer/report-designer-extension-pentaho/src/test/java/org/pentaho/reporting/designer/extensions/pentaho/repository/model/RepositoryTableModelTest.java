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


package org.pentaho.reporting.designer.extensions.pentaho.repository.model;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.util.Date;

import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.designer.extensions.pentaho.repository.Messages;

public class RepositoryTableModelTest {

  private static final String FILE1_TXT = "file1.txt";
  private static final String FILE2_TXT = "file2.txt";
  private static final String FILE3_TXT = "file3.txt";
  private static final String LOCALIZED_NAME_ATTR = "localized-name";

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
      doReturn( FILE1_TXT ).when( childFileName1 ).getBaseName();
      doReturn( FILE2_TXT ).when( childFileName2 ).getBaseName();
      doReturn( FILE3_TXT ).when( childFileName3 ).getBaseName();
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
      doReturn( FILE1_TXT ).when( childFileName1 ).getBaseName();
      doReturn( FILE2_TXT ).when( childFileName2 ).getBaseName();
      doReturn( FILE3_TXT ).when( childFileName3 ).getBaseName();
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
      doReturn( FILE1_TXT ).when( childFileName1 ).getBaseName();
      doReturn( FILE2_TXT ).when( childFileName2 ).getBaseName();
      doReturn( FILE3_TXT ).when( childFileName3 ).getBaseName();
      doReturn( childFileContent1 ).when( childFile1 ).getContent();
      doReturn( childFileContent2 ).when( childFile2 ).getContent();
      doReturn( childFileContent3 ).when( childFile3 ).getContent();
      doReturn( localizedName1 ).when( childFileContent1 ).getAttribute( LOCALIZED_NAME_ATTR );
      doReturn( localizedName2 ).when( childFileContent2 ).getAttribute( LOCALIZED_NAME_ATTR );
      doReturn( localizedName3 ).when( childFileContent3 ).getAttribute( LOCALIZED_NAME_ATTR );
      doReturn( description1 ).when( childFileContent1 ).getAttribute( "description" );
      doReturn( description2 ).when( childFileContent2 ).getAttribute( "description" );
      doReturn( description3 ).when( childFileContent3 ).getAttribute( "description" );
      doReturn( modifiedTime ).when( childFileContent1 ).getLastModifiedTime();
      doReturn( modifiedTime ).when( childFileContent2 ).getLastModifiedTime();
      doReturn( modifiedTime ).when( childFileContent3 ).getLastModifiedTime();
      doReturn( childFiles ).when( fileObject ).getChildren();
      assertEquals( localizedName1, repoTableModel.getValueAt( 0, 0 ) );
      assertEquals( FILE1_TXT, repoTableModel.getValueAt( 0, 1 ) );
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
  public void testGetRowCountNullSelectedPath() {
    RepositoryTableModel repoTableModel = new RepositoryTableModel();
    repoTableModel.setSelectedPath( null );
    assertEquals( 0, repoTableModel.getRowCount() );
  }

  @Test
  public void testGetRowCountNotFolder() throws Exception {
    RepositoryTableModel repoTableModel = new RepositoryTableModel();
    doReturn( FileType.FILE ).when( fileObject ).getType();
    repoTableModel.setSelectedPath( fileObject );
    assertEquals( 0, repoTableModel.getRowCount() );
  }

  @Test
  public void testGetRowCountFileSystemException() throws Exception {
    RepositoryTableModel repoTableModel = new RepositoryTableModel();
    doThrow( new FileSystemException( "test error" ) ).when( fileObject ).getType();
    repoTableModel.setSelectedPath( fileObject );
    assertEquals( 0, repoTableModel.getRowCount() );
  }

  @Test
  public void testGetRowCountHiddenFilesFiltered() throws Exception {
    RepositoryTableModel repoTableModel = new RepositoryTableModel();
    repoTableModel.setShowHiddenFiles( false );
    doReturn( FileType.FOLDER ).when( fileObject ).getType();
    FileObject[] childFiles = new FileObject[] { childFile1, childFile2 };
    doReturn( childFiles ).when( fileObject ).getChildren();
    doReturn( childFileName1 ).when( childFile1 ).getName();
    doReturn( childFileName2 ).when( childFile2 ).getName();
    doReturn( FILE1_TXT ).when( childFileName1 ).getBaseName();
    doReturn( FILE2_TXT ).when( childFileName2 ).getBaseName();
    doReturn( true ).when( childFile1 ).isHidden();
    doReturn( false ).when( childFile2 ).isHidden();
    doReturn( FileType.FILE ).when( childFile1 ).getType();
    doReturn( FileType.FILE ).when( childFile2 ).getType();
    repoTableModel.setSelectedPath( fileObject );
    assertEquals( 1, repoTableModel.getRowCount() );
  }

  @Test
  public void testGetRowCountHiddenFilesShown() throws Exception {
    RepositoryTableModel repoTableModel = new RepositoryTableModel();
    repoTableModel.setShowHiddenFiles( true );
    doReturn( FileType.FOLDER ).when( fileObject ).getType();
    FileObject[] childFiles = new FileObject[] { childFile1, childFile2 };
    doReturn( childFiles ).when( fileObject ).getChildren();
    doReturn( childFileName1 ).when( childFile1 ).getName();
    doReturn( childFileName2 ).when( childFile2 ).getName();
    doReturn( FILE1_TXT ).when( childFileName1 ).getBaseName();
    doReturn( FILE2_TXT ).when( childFileName2 ).getBaseName();
    doReturn( true ).when( childFile1 ).isHidden();
    doReturn( false ).when( childFile2 ).isHidden();
    doReturn( FileType.FILE ).when( childFile1 ).getType();
    doReturn( FileType.FILE ).when( childFile2 ).getType();
    repoTableModel.setSelectedPath( fileObject );
    assertEquals( 2, repoTableModel.getRowCount() );
  }

  @Test
  public void testGetRowCountWithFilters() throws Exception {
    RepositoryTableModel repoTableModel = new RepositoryTableModel();
    repoTableModel.setFilters( new String[] { ".prpt" } );
    doReturn( FileType.FOLDER ).when( fileObject ).getType();
    FileObject[] childFiles = new FileObject[] { childFile1, childFile2 };
    doReturn( childFiles ).when( fileObject ).getChildren();
    doReturn( childFileName1 ).when( childFile1 ).getName();
    doReturn( childFileName2 ).when( childFile2 ).getName();
    doReturn( "report.prpt" ).when( childFileName1 ).getBaseName();
    doReturn( "data.csv" ).when( childFileName2 ).getBaseName();
    doReturn( FileType.FILE ).when( childFile1 ).getType();
    doReturn( FileType.FILE ).when( childFile2 ).getType();
    repoTableModel.setSelectedPath( fileObject );
    assertEquals( 1, repoTableModel.getRowCount() );
  }

  @Test
  public void testGetRowCountFolderAlwaysIncluded() throws Exception {
    RepositoryTableModel repoTableModel = new RepositoryTableModel();
    repoTableModel.setFilters( new String[] { ".prpt" } );
    doReturn( FileType.FOLDER ).when( fileObject ).getType();
    FileObject[] childFiles = new FileObject[] { childFile1 };
    doReturn( childFiles ).when( fileObject ).getChildren();
    doReturn( childFileName1 ).when( childFile1 ).getName();
    doReturn( "subfolder" ).when( childFileName1 ).getBaseName();
    doReturn( FileType.FOLDER ).when( childFile1 ).getType();
    repoTableModel.setSelectedPath( fileObject );
    // Folders should always be included regardless of filter
    assertEquals( 1, repoTableModel.getRowCount() );
  }

  @Test
  public void testGetElementForRowNullSelectedPath() {
    RepositoryTableModel repoTableModel = new RepositoryTableModel();
    assertNull( repoTableModel.getElementForRow( 0 ) );
  }

  @Test
  public void testGetElementForRowNotFolder() throws Exception {
    RepositoryTableModel repoTableModel = new RepositoryTableModel();
    doReturn( FileType.FILE ).when( fileObject ).getType();
    repoTableModel.setSelectedPath( fileObject );
    assertNull( repoTableModel.getElementForRow( 0 ) );
  }

  @Test
  public void testGetElementForRowFileSystemException() throws Exception {
    RepositoryTableModel repoTableModel = new RepositoryTableModel();
    doThrow( new FileSystemException( "test error" ) ).when( fileObject ).getType();
    repoTableModel.setSelectedPath( fileObject );
    assertNull( repoTableModel.getElementForRow( 0 ) );
  }

  @Test
  public void testGetElementForRowOutOfBounds() throws Exception {
    RepositoryTableModel repoTableModel = new RepositoryTableModel();
    doReturn( FileType.FOLDER ).when( fileObject ).getType();
    FileObject[] childFiles = new FileObject[] { childFile1 };
    doReturn( childFiles ).when( fileObject ).getChildren();
    doReturn( childFileName1 ).when( childFile1 ).getName();
    doReturn( FILE1_TXT ).when( childFileName1 ).getBaseName();
    doReturn( FileType.FILE ).when( childFile1 ).getType();
    repoTableModel.setSelectedPath( fileObject );
    assertNull( repoTableModel.getElementForRow( 5 ) );
  }

  @Test
  public void testGetValueAtLastModifiedMinusOne() throws Exception {
    RepositoryTableModel repoTableModel = new RepositoryTableModel();
    doReturn( FileType.FOLDER ).when( fileObject ).getType();
    FileObject[] childFiles = new FileObject[] { childFile1 };
    doReturn( childFiles ).when( fileObject ).getChildren();
    doReturn( childFileName1 ).when( childFile1 ).getName();
    doReturn( FILE1_TXT ).when( childFileName1 ).getBaseName();
    doReturn( childFileContent1 ).when( childFile1 ).getContent();
    doReturn( -1L ).when( childFileContent1 ).getLastModifiedTime();
    doReturn( FileType.FILE ).when( childFile1 ).getType();
    repoTableModel.setSelectedPath( fileObject );
    assertNull( repoTableModel.getValueAt( 0, 2 ) );
  }

  @Test
  public void testGetValueAtFileSystemExceptionOnContent() throws Exception {
    RepositoryTableModel repoTableModel = new RepositoryTableModel();
    doReturn( FileType.FOLDER ).when( fileObject ).getType();
    FileObject[] childFiles = new FileObject[] { childFile1 };
    doReturn( childFiles ).when( fileObject ).getChildren();
    doReturn( childFileName1 ).when( childFile1 ).getName();
    doReturn( FILE1_TXT ).when( childFileName1 ).getBaseName();
    doThrow( new FileSystemException( "content error" ) ).when( childFile1 ).getContent();
    doReturn( FileType.FILE ).when( childFile1 ).getType();
    repoTableModel.setSelectedPath( fileObject );
    assertNull( repoTableModel.getValueAt( 0, 0 ) );
  }

  @Test( expected = IndexOutOfBoundsException.class )
  public void testGetColumnNameOutOfBounds() {
    RepositoryTableModel repoTableModel = new RepositoryTableModel();
    repoTableModel.getColumnName( 5 );
  }

  @Test
  public void testGetColumnClassAllColumns() {
    RepositoryTableModel repoTableModel = new RepositoryTableModel();
    assertEquals( String.class, repoTableModel.getColumnClass( 0 ) );
    assertEquals( String.class, repoTableModel.getColumnClass( 1 ) );
    assertEquals( Date.class, repoTableModel.getColumnClass( 2 ) );
    assertEquals( String.class, repoTableModel.getColumnClass( 3 ) );
  }

}
