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

import org.apache.commons.vfs2.FileSelector;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.provider.AbstractFileName;
import org.apache.commons.vfs2.provider.AbstractFileObject;
import org.apache.commons.vfs2.provider.AbstractFileSystem;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class WebSolutionFileObject extends AbstractFileObject {
  private SolutionFileModel fs;

  public WebSolutionFileObject( final AbstractFileName name,
                                final AbstractFileSystem fileSystem,
                                final SolutionFileModel fs )
  {
    super( name, fileSystem );
    this.fs = fs;
  }

  /**
   * Attaches this file object to its file resource.  This method is called before any of the doBlah() or onBlah()
   * methods.  Sub-classes can use this method to perform lazy initialisation.
   * <p/>
   * This implementation does nothing.
   */
  protected void doAttach() throws Exception {
    super.doAttach();
  }

  protected boolean doIsReadable() throws Exception {
    return true;
  }

  protected boolean doIsWriteable() throws Exception {
    if ( getName().getDepth() < 2 ) {
      return false;
    }
    return true;
  }

  /**
   * Determines the type of this file.  Must not return null.  The return value of this method is cached, so the
   * implementation can be expensive.
   */
  protected FileType doGetType() throws Exception {
    if ( getName().getDepth() < 2 ) {
      return FileType.FOLDER;
    }
    if ( fs.exists( getName() ) == false ) {
      return FileType.IMAGINARY;
    }
    if ( fs.isDirectory( getName() ) ) {
      return FileType.FOLDER;
    }
    return FileType.FILE;
  }

  /**
   * Lists the children of this file.  Is only called if {@link #doGetType}
   * returns {@link org.apache.commons.vfs2.FileType#FOLDER}.  The return value of this method
   * is cached, so the implementation can be expensive.
   */
  protected String[] doListChildren() throws Exception {
    return fs.getChilds( getName() );
  }

  /**
   * Returns the size of the file content (in bytes).  Is only called if
   * {@link #doGetType} returns {@link org.apache.commons.vfs2.FileType#FILE}.
   */
  protected long doGetContentSize() throws Exception {
    return fs.getContentSize( getName() );
  }

  /**
   * Determines if this file is hidden.  Is only called if {@link #doGetType}
   * does not return {@link org.apache.commons.vfs2.FileType#IMAGINARY}.
   * <p/>
   * This implementation always returns false.
   */
  protected boolean doIsHidden() throws Exception {
    return fs.isVisible( getName() ) == false;
  }

  /**
   * Returns the last modified time of this file.  Is only called if
   * {@link #doGetType} does not return {@link org.apache.commons.vfs2.FileType#IMAGINARY}.
   * <p/>
   * This implementation throws an exception.
   */
  protected long doGetLastModifiedTime() throws Exception {
    return fs.getLastModifiedDate( getName() );
  }

  /**
   * Returns the attributes of this file.  Is only called if {@link #doGetType}
   * does not return {@link org.apache.commons.vfs2.FileType#IMAGINARY}.
   * <p/>
   * This implementation always returns an empty map.
   */
  protected Map doGetAttributes() throws Exception {
    final String description = fs.getDescription( getName() );
    final String localizedName = fs.getLocalizedName( getName() );
    final String paramServiceUrl = fs.getParamServiceUrl( getName() );
    final String url = fs.getUrl( getName() );

    final HashMap<String, String> map = new HashMap<String, String>();
    map.put( "description", description );
    map.put( "localized-name", localizedName );
    map.put( "param-service-url", paramServiceUrl );
    map.put( "url", url );
    return map;
  }

  /**
   * Sets an attribute of this file.  Is only called if {@link #doGetType}
   * does not return {@link org.apache.commons.vfs2.FileType#IMAGINARY}.
   * <p/>
   * This implementation throws an exception.
   */
  protected void doSetAttribute( final String atttrName, final Object value ) throws Exception {
    if ( "description".equals( atttrName ) ) {
      if ( value instanceof String ) {
        fs.setDescription( getName(), String.valueOf( value ) );
      } else {
        fs.setDescription( getName(), null );
      }
    }
  }

  /**
   * Creates an input stream to read the file content from.  Is only called
   * if {@link #doGetType} returns {@link org.apache.commons.vfs2.FileType#FILE}.
   * <p/>
   * <p>It is guaranteed that there are no open output streams for this file when this method is called.
   * <p/>
   * <p>The returned stream does not have to be buffered.
   */
  protected InputStream doGetInputStream() throws Exception {
    return new ByteArrayInputStream( fs.getData( getName() ) );
  }

  protected OutputStream doGetOutputStream( final boolean bAppend ) throws Exception {
    final byte[] existingData;
    if ( bAppend ) {
      existingData = fs.getData( getName() );
    } else {
      existingData = new byte[ 0 ];
    }
    return new SolutionFileOutputStream( this, existingData );
  }

  public void writeData( byte[] data ) throws FileSystemException {
    fs.setData( getName(), data );
  }

  /**
   * Creates this file as a folder.  Is only called when:
   * <ul>
   * <li>{@link #doGetType} returns {@link org.apache.commons.vfs2.FileType#IMAGINARY}.
   * <li>The parent folder exists and is writeable, or this file is the
   * root of the file system.
   * </ul>
   * <p/>
   * This implementation throws an exception.
   */
  protected void doCreateFolder() throws Exception {
    fs.createFolder( getName() );
  }

  public String getDescription() throws FileSystemException {
    return fs.getDescription( getName() );
  }

  public void setDescription( final String description ) throws FileSystemException {
    fs.setDescription( getName(), description );
  }

  @Override
  public int delete( final FileSelector selector ) throws FileSystemException {
    return fs.delete( getName() ) ? 1 : 0;
  }
}
