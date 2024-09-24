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

import java.io.IOException;

public interface SolutionFileModel {
  void refresh() throws IOException;

  String[] getChilds( FileName name ) throws FileSystemException;

  boolean exists( FileName name ) throws FileSystemException;

  boolean isDirectory( FileName name ) throws FileSystemException;

  boolean isVisible( FileName name ) throws FileSystemException;

  long getLastModifiedDate( FileName name ) throws FileSystemException;

  long getContentSize( FileName name ) throws FileSystemException;

  String getDescription( FileName name ) throws FileSystemException;

  String getLocalizedName( FileName name ) throws FileSystemException;

  String getParamServiceUrl( FileName name ) throws FileSystemException;

  String getUrl( FileName name ) throws FileSystemException;

  void setDescription( FileName name, String s ) throws FileSystemException;

  void createFolder( FileName name ) throws FileSystemException;

  byte[] getData( FileName name ) throws FileSystemException;

  void setData( FileName name, byte[] data ) throws FileSystemException;

  boolean delete( FileName name ) throws FileSystemException;
}
