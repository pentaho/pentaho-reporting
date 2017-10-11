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
