/*!
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.designer.extensions.pentaho.repository.model;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
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

}
