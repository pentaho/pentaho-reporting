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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.awt.Component;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.junit.Test;

public class RepositoryTableTest {

  @Test
  public void testConstructorAndBasicDelegates() {
    final RepositoryTable table = new RepositoryTable();
    assertNotNull( table );
    assertFalse( table.isShowHiddenFiles() );

    table.setShowHiddenFiles( true );
    assertTrue( table.isShowHiddenFiles() );

    table.setFilters( new String[] { ".prpt", ".report" } );
    assertArrayEquals( new String[] { ".prpt", ".report" }, table.getFilters() );

    table.refresh();
  }

  @Test
  public void testSelectedPathRoundTrip() {
    final RepositoryTable table = new RepositoryTable();
    final FileObject selectedPath = mock( FileObject.class );

    assertNull( table.getSelectedPath() );
    table.setSelectedPath( selectedPath );
    assertEquals( selectedPath, table.getSelectedPath() );
  }

  @Test
  public void testGetSelectedFileObjectDelegatesToModel() throws Exception {
    final RepositoryTable table = new RepositoryTable();

    final FileObject root = mock( FileObject.class );
    final FileObject child = mock( FileObject.class );
    final FileName name = mock( FileName.class );

    doReturn( FileType.FOLDER ).when( root ).getType();
    doReturn( false ).when( child ).isHidden();
    doReturn( FileType.FILE ).when( child ).getType();
    doReturn( name ).when( child ).getName();
    doReturn( "sample.prpt" ).when( name ).getBaseName();
    doReturn( new FileObject[] { child } ).when( root ).getChildren();

    table.setFilters( new String[] { ".prpt" } );
    table.setSelectedPath( root );

    assertEquals( child, table.getSelectedFileObject( 0 ) );
  }

  @Test
  public void testSetSessionExpiredListenerCanBeRegistered() throws FileSystemException {
    final RepositoryTable table = new RepositoryTable();
    final FileObject root = mock( FileObject.class );

    final AtomicBoolean listenerCalled = new AtomicBoolean( false );
    table.setSessionExpiredListener( cause -> listenerCalled.set( true ) );

    // Non-error path just verifies registration does not break normal refresh flow.
    doReturn( FileType.FOLDER ).when( root ).getType();
    doReturn( new FileObject[0] ).when( root ).getChildren();
    table.setSelectedPath( root );

    assertEquals( 0, table.getRowCount() );
    assertFalse( listenerCalled.get() );
  }

  @Test
  public void testDateCellRendererHandlesDateAndNonDateValues() {
    final RepositoryTable table = new RepositoryTable();
    final javax.swing.table.TableCellRenderer renderer = table.getDefaultRenderer( Date.class );

    final Component c1 = renderer.getTableCellRendererComponent( table, new Date(), false, false, 0, 2 );
    final Component c2 = renderer.getTableCellRendererComponent( table, "plain", false, false, 0, 2 );

    assertNotNull( c1 );
    assertNotNull( c2 );
  }
}
