/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

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
