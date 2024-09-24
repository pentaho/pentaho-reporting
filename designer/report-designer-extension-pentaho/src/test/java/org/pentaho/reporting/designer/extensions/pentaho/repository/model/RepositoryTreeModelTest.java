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

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.doReturn;

import javax.swing.tree.TreePath;

import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.junit.Before;
import org.junit.Test;

public class RepositoryTreeModelTest {

  FileObject repositoryRoot, childFile1, childFile2, childFile3, childFile4;
  FileName childFileName1, childFileName2, childFileName3;

  @Before
  public void setUp() throws Exception {
    repositoryRoot = mock( FileObject.class );
    childFile1 = mock( FileObject.class );
    childFile2 = mock( FileObject.class );
    childFile3 = mock( FileObject.class );
    childFile4 = mock( FileObject.class );
    childFileName1 = mock( FileName.class );
    childFileName2 = mock( FileName.class );
    childFileName3 = mock( FileName.class );
  }

  @Test
  public void testRepositoryTreeModel() {
    RepositoryTreeModel treeModel = new RepositoryTreeModel();
    assertNotNull( treeModel );
  }

  @Test
  public void testRepositoryTreeModelFileObjectStringArrayBoolean() {
    String[] filters = new String[] { "manny", "moe", "jack" };
    RepositoryTreeModel treeModel = new RepositoryTreeModel( repositoryRoot, filters, true );
    assertNotNull( treeModel );
    assertArrayEquals( filters, treeModel.getFilters() );
    assertNotNull( treeModel.getRoot() );
    assertTrue( treeModel.isShowFoldersOnly() );
  }

  @Test
  public void testIsSetShowFoldersOnly() {
    RepositoryTreeModel treeModel = new RepositoryTreeModel();
    assertNotNull( treeModel );
    assertTrue( treeModel.isShowFoldersOnly() );
    treeModel.setShowFoldersOnly( false );
    assertFalse( treeModel.isShowFoldersOnly() );
  }

  @Test
  public void testIsSetShowHiddenFiles() {
    RepositoryTreeModel treeModel = new RepositoryTreeModel();
    assertNotNull( treeModel );
    assertFalse( treeModel.isShowHiddenFiles() );
    treeModel.setShowHiddenFiles( true );
    assertTrue( treeModel.isShowHiddenFiles() );
  }

  @Test
  public void testGetSetFilters() {
    String[] filters = new String[] { "manny", "moe", "jack" };
    RepositoryTreeModel treeModel = new RepositoryTreeModel();
    assertNotNull( treeModel );
    assertEquals( 0, treeModel.getFilters().length );
    treeModel.setFilters( filters );
    assertArrayEquals( filters, treeModel.getFilters() );
  }

  @Test
  public void testGetSetFileSystemRoot() {
    RepositoryTreeModel treeModel = new RepositoryTreeModel();
    assertNotNull( treeModel );
    assertNull( treeModel.getFileSystemRoot() );
    treeModel.setFileSystemRoot( repositoryRoot );
    assertEquals( repositoryRoot, treeModel.getFileSystemRoot() );
  }

  @Test
  public void testGetChild() {
    RepositoryTreeModel treeModel = new RepositoryTreeModel();
    assertNotNull( treeModel );
    assertNull( treeModel.getFileSystemRoot() );
    FileObject[] childFiles = new FileObject[] { childFile1, childFile2, childFile3 };
    try {
      doReturn( childFiles ).when( repositoryRoot ).getChildren();
    } catch ( FileSystemException e ) {
      e.printStackTrace();
    }
    treeModel.setFileSystemRoot( repositoryRoot );
    Object value = treeModel.getChild( repositoryRoot, 1 );
    assertEquals( childFile2, value );

    treeModel.setShowFoldersOnly( true );
    try {
      doReturn( FileType.FILE ).when( childFile1 ).getType();
      doReturn( FileType.FILE ).when( childFile2 ).getType();
      doReturn( FileType.FILE ).when( childFile3 ).getType();
    } catch ( FileSystemException e ) {
      e.printStackTrace();
    }
    value = treeModel.getChild( repositoryRoot, 0 );
    assertEquals( childFile1, value );

    treeModel.setShowHiddenFiles( false );
    try {
      doReturn( FileType.FOLDER ).when( childFile1 ).getType();
      doReturn( FileType.FOLDER ).when( childFile2 ).getType();
      doReturn( FileType.FOLDER ).when( childFile3 ).getType();
      doReturn( true ).when( childFile1 ).isHidden();
      doReturn( true ).when( childFile2 ).isHidden();
      doReturn( true ).when( childFile3 ).isHidden();
    } catch ( FileSystemException e ) {
      e.printStackTrace();
    }
    value = treeModel.getChild( repositoryRoot, 2 );
    assertEquals( childFile3, value );

  }

  @Test
  public void testGetChildCount() {
    RepositoryTreeModel treeModel = new RepositoryTreeModel();
    assertNotNull( treeModel );
    treeModel.setFileSystemRoot( repositoryRoot );
    assertEquals( 0, treeModel.getChildCount( repositoryRoot ) );

    FileObject[] childFiles = new FileObject[] { childFile1, childFile2, childFile3 };
    try {
      doReturn( childFiles ).when( repositoryRoot ).getChildren();
      doReturn( FileType.FOLDER ).when( repositoryRoot ).getType();
      doReturn( FileType.FOLDER ).when( childFile1 ).getType();
      doReturn( FileType.FOLDER ).when( childFile2 ).getType();
      doReturn( FileType.FOLDER ).when( childFile3 ).getType();
    } catch ( FileSystemException e ) {
      e.printStackTrace();
    }
    assertEquals( 3, treeModel.getChildCount( repositoryRoot ) );
  }

  @Test
  public void testIsLeaf() {
    RepositoryTreeModel treeModel = new RepositoryTreeModel();
    assertNotNull( treeModel );
    treeModel.setFileSystemRoot( repositoryRoot );
    assertTrue( treeModel.isLeaf( repositoryRoot ) );
  }

  @Test
  public void testValueForPathChanged() {
    RepositoryTreeModel treeModel = new RepositoryTreeModel();
    assertNotNull( treeModel );
    treeModel.valueForPathChanged( null, null );
  }

  @Test
  public void testGetIndexOfChild() {
    RepositoryTreeModel treeModel = new RepositoryTreeModel();
    assertNotNull( treeModel );
    treeModel.setFileSystemRoot( repositoryRoot );
    treeModel.setShowFoldersOnly( false );
    FileObject[] childFiles = new FileObject[] { childFile1, childFile2, childFile3 };
    try {
      doReturn( childFiles ).when( repositoryRoot ).getChildren();
      doReturn( childFileName1 ).when( childFile1 ).getName();
      doReturn( childFileName2 ).when( childFile2 ).getName();
      doReturn( childFileName3 ).when( childFile3 ).getName();
      doReturn( "BaseName1" ).when( childFileName1 ).getBaseName();
      doReturn( "BaseName2" ).when( childFileName2 ).getBaseName();
      doReturn( "BaseName3" ).when( childFileName3 ).getBaseName();
    } catch ( FileSystemException e ) {
      e.printStackTrace();
    }
    assertEquals( 1, treeModel.getIndexOfChild( repositoryRoot, childFile2 ) );
    assertEquals( -1, treeModel.getIndexOfChild( repositoryRoot, childFile4 ) );
  }

  @Test
  public void testGetTreePathForSelection() {
    RepositoryTreeModel treeModel = new RepositoryTreeModel();
    assertNotNull( treeModel );
    treeModel.setFileSystemRoot( repositoryRoot );
    FileObject[] childFiles = new FileObject[] { childFile1, childFile2, childFile3 };
    try {
      doReturn( childFiles ).when( repositoryRoot ).getChildren();
      doReturn( childFileName1 ).when( childFile1 ).getName();
      doReturn( childFileName2 ).when( childFile2 ).getName();
      doReturn( childFileName3 ).when( childFile3 ).getName();
      doReturn( repositoryRoot ).when( childFile1 ).getParent();
      doReturn( repositoryRoot ).when( childFile2 ).getParent();
      doReturn( repositoryRoot ).when( childFile3 ).getParent();
      TreePath path = treeModel.getTreePathForSelection( childFile2, null );
      assertEquals( 2, path.getPath().length );
      assertEquals( childFile2, path.getLastPathComponent() );
    } catch ( FileSystemException e ) {
      e.printStackTrace();
    }

  }

  @Test
  public void testFindNodeByName() {
    FileObject[] childFiles = new FileObject[] { childFile1, childFile2, childFile3 };
    try {
      doReturn( FileType.FOLDER ).when( repositoryRoot ).getType();
      doReturn( childFiles ).when( repositoryRoot ).getChildren();
      doReturn( childFileName1 ).when( childFile1 ).getName();
      doReturn( childFileName2 ).when( childFile2 ).getName();
      doReturn( childFileName3 ).when( childFile3 ).getName();
      doReturn( "BaseName1" ).when( childFileName1 ).getBaseName();
      doReturn( "BaseName2" ).when( childFileName2 ).getBaseName();
      doReturn( "BaseName3" ).when( childFileName3 ).getBaseName();
      doReturn( childFile2 ).when( repositoryRoot ).getChild( "BaseName2" );
      assertEquals( childFile2, RepositoryTreeModel.findNodeByName( repositoryRoot, "BaseName2" ) );
    } catch ( FileSystemException e ) {
      e.printStackTrace();
    }
  }

}
