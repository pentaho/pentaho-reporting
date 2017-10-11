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

package org.pentaho.reporting.libraries.pensol;

import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileSystemException;
import org.pentaho.reporting.libraries.pensol.vfs.FileInfo;
import org.pentaho.reporting.libraries.pensol.vfs.XmlSolutionFileModel;

import java.io.IOException;
import java.io.InputStream;

public class TestSolutionFileModel extends XmlSolutionFileModel {
  public TestSolutionFileModel() {
  }

  public void refresh() throws IOException {
    final InputStream stream = TestSolutionFileModel.class.getResourceAsStream
      ( "/org/pentaho/reporting/libraries/pensol/SolutionRepositoryService.xml" );
    try {
      setRoot( this.performParse( stream ) );
    } finally {
      stream.close();
    }
  }

  public FileInfo performParse( final InputStream postResult ) throws IOException {
    return super.performParse( postResult );
  }

  protected byte[] getDataInternally( final FileInfo fileInfo ) throws FileSystemException {
    return new byte[ 0 ];
  }

  public long getContentSize( final FileName name ) throws FileSystemException {
    return 0;
  }

  protected void setDataInternally( final FileInfo fileInfo, final byte[] data ) throws FileSystemException {
    throw new FileSystemException( "Not implemented" );
  }

  @Override
  public boolean delete( FileName name ) throws FileSystemException {
    throw new FileSystemException( "Not implemented" );
  }
}
