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
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package org.pentaho.reporting.libraries.pensol;

import junit.framework.TestCase;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.VFS;

import java.io.IOException;
import java.io.InputStream;

public class VfsTest extends TestCase {
  public VfsTest() {
  }

  public VfsTest( final String name ) {
    super( name );
  }

  protected void setUp() throws Exception {
    LibPensolBoot.getInstance().start();
  }

  public void testParse() throws IOException {
    final InputStream stream = TestSolutionFileModel.class.getResourceAsStream
      ( "/org/pentaho/reporting/libraries/pensol/ee-service.xml" );
    try {
      TestSolutionFileModel model = new TestSolutionFileModel();
      model.performParse( stream );
    } finally {
      if ( stream != null ) {
        stream.close();
      }
    }

  }

  public void testInitialLoading() throws FileSystemException {
    final FileObject nonExistent = VFS.getManager().resolveFile( "test-solution://localhost/non-existent" );
    assertFalse( nonExistent.exists() );
    assertEquals( FileType.IMAGINARY, nonExistent.getType() );
    assertEquals( "non-existent", nonExistent.getName().getBaseName() );
    final FileObject directory = VFS.getManager().resolveFile( "test-solution://localhost/bi-developers" );
    assertTrue( directory.exists() );
    assertEquals( FileType.FOLDER, directory.getType() );
    assertEquals( "bi-developers", directory.getName().getBaseName() );
    final FileObject file =
      VFS.getManager().resolveFile( "test-solution://localhost/bi-developers/analysis/query1.xaction" );
    assertTrue( file.exists() );
    assertEquals( FileType.FILE, file.getType() );
    assertEquals( "query1.xaction", file.getName().getBaseName() );
  }
}
