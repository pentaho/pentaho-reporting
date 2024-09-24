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

package org.pentaho.reporting.libraries.pensol.vfs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileSystemException;
import org.pentaho.reporting.libraries.base.util.ArgumentNullException;
import org.pentaho.reporting.libraries.base.util.FastStack;
import org.pentaho.reporting.libraries.pensol.SolutionFileModel;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public abstract class XmlSolutionFileModel implements SolutionFileModel {
  private static final Log logger = LogFactory.getLog( XmlSolutionFileModel.class );
  private FileInfo root;
  private HashMap<FileName, String> descriptionEntries;
  private long refreshTime;
  private String majorVersion;
  private String minorVersion;
  private String releaseVersion;
  private String buildVersion;
  private String milestoneVersion;

  protected XmlSolutionFileModel() {
    descriptionEntries = new HashMap<FileName, String>();
  }

  public FileInfo getRoot() {
    return root;
  }

  public void setRoot( final FileInfo root ) {
    this.root = root;
    this.refreshTime = System.currentTimeMillis();
  }

  public boolean isDirectory( final FileName file ) throws FileSystemException {
    final String[] fileName = computeFileNames( file );
    final FileInfo fileInfo = lookupNode( fileName );
    if ( fileInfo == null ) {
      throw new FileSystemException( "File is not valid." );
    }
    return fileInfo.isDirectory();
  }

  public boolean exists( final FileName file ) throws FileSystemException {
    final String[] fileName = computeFileNames( file );
    final FileInfo fileInfo = lookupNode( fileName );
    return ( fileInfo != null );
  }

  public boolean isVisible( final FileName file ) throws FileSystemException {
    final String[] fileName = computeFileNames( file );
    final FileInfo fileInfo = lookupNode( fileName );
    if ( fileInfo == null ) {
      throw new FileSystemException( "File is not valid." );
    }
    return fileInfo.isVisible();
  }

  public String getName( final FileName file ) throws FileSystemException {
    final String[] fileName = computeFileNames( file );
    final FileInfo fileInfo = lookupNode( fileName );
    if ( fileInfo == null ) {
      throw new FileSystemException( "File is not valid." );
    }
    return fileInfo.getName();
  }

  public String getLocalizedName( final FileName file ) throws FileSystemException {
    final String[] fileName = computeFileNames( file );
    final FileInfo fileInfo = lookupNode( fileName );
    if ( fileInfo == null ) {
      throw new FileSystemException( "File is not valid." );
    }
    return fileInfo.getLocalizedName();
  }

  public void setDescription( final FileName file, final String description ) throws FileSystemException {
    final String[] fileName = computeFileNames( file );
    final FileInfo fileInfo = lookupNode( fileName );
    if ( fileInfo == null ) {
      throw new FileSystemException( "File is not valid." );
    }
    fileInfo.setDescription( description );
  }

  public String getDescription( final FileName file ) throws FileSystemException {
    final String[] fileName = computeFileNames( file );
    final FileInfo fileInfo = lookupNode( fileName );
    if ( fileInfo == null ) {
      throw new FileSystemException( "File is not valid." );
    }
    return fileInfo.getDescription();
  }

  public long getLastModifiedDate( final FileName file ) throws FileSystemException {
    final String[] fileName = computeFileNames( file );
    final FileInfo fileInfo = lookupNode( fileName );
    if ( fileInfo == null ) {
      throw new FileSystemException( "File is not valid." );
    }
    return fileInfo.getLastModifiedDate();
  }

  public String getParamServiceUrl( final FileName file ) throws FileSystemException {
    final String[] fileName = computeFileNames( file );
    final FileInfo fileInfo = lookupNode( fileName );
    if ( fileInfo == null ) {
      throw new FileSystemException( "File is not valid." );
    }
    return fileInfo.getParameterServiceURL();
  }

  public String getTitle( final FileName file ) throws FileSystemException {
    final String[] fileName = computeFileNames( file );
    final FileInfo fileInfo = lookupNode( fileName );
    if ( fileInfo == null ) {
      throw new FileSystemException( "File is not valid." );
    }
    return fileInfo.getTitle();
  }

  public String[] getChilds( final FileName file ) throws FileSystemException {
    final String[] fileName = computeFileNames( file );
    final FileInfo fileInfo = lookupNode( fileName );
    if ( fileInfo == null ) {
      throw new FileSystemException( "File is not valid." );
    }
    final FileInfo[] childs = fileInfo.getChilds();
    final String[] childNames = new String[ childs.length ];
    for ( int i = 0; i < childs.length; i++ ) {
      final FileInfo child = childs[ i ];
      childNames[ i ] = child.getName();
    }
    return childNames;
  }

  public String getUrl( final FileName file ) throws FileSystemException {
    final String[] fileName = computeFileNames( file );
    final FileInfo fileInfo = lookupNode( fileName );
    if ( fileInfo == null ) {
      throw new FileSystemException( "File is not valid." );
    }
    return fileInfo.getUrl();
  }

  protected FileInfo lookupNode( final String[] path ) throws FileSystemException {
    if ( root == null ) {
      try {
        refresh();
      } catch ( IOException e ) {
        throw new FileSystemException( e );
      }
    }
    if ( path.length == 0 ) {
      return root;
    }
    if ( "".equals( path[ 0 ] ) ) {
      if ( path.length == 1 ) {
        return root;
      }
    } else {
      return null;
    }

    FileInfo element = root;
    for ( int i = 1; i < path.length; i++ ) {
      final FileInfo name = element.getChild( path[ i ] );
      if ( name == null ) {
        return null;
      }
      element = name;
    }
    return element;
  }

  protected String[] computeFileNames( FileName file ) {
    final FastStack stack = new FastStack();
    while ( file != null ) {
      final String name = file.getBaseName();
      stack.push( name );
      file = file.getParent();
    }

    final int size = stack.size();
    final String[] result = new String[ size ];
    for ( int i = 0; i < result.length; i++ ) {
      result[ i ] = (String) stack.pop();
    }
    return result;
  }

  protected FileInfo performParse( final InputStream postResult ) throws IOException {
    ArgumentNullException.validate("postResult", postResult);

    try {
      final FileInfoParser contentHandler = new FileInfoParser();
      final SAXParserFactory factory = SAXParserFactory.newInstance();
      final SAXParser parser = factory.newSAXParser();
      final XMLReader reader = parser.getXMLReader();

      try {
        reader.setFeature( "http://xml.org/sax/features/xmlns-uris", false );
      } catch ( SAXException e ) {
        // ignored
      }
      try {
        reader.setFeature( "http://xml.org/sax/features/namespaces", false );
        reader.setFeature( "http://xml.org/sax/features/namespace-prefixes", false );
      } catch ( final SAXException e ) {
        logger.warn( "No Namespace features will be available. (Yes, this is serious)", e );
      }

      reader.setContentHandler( contentHandler );
      reader.parse( new InputSource( postResult ) );

      majorVersion = contentHandler.getMajorVersion();
      minorVersion = contentHandler.getMinorVersion();
      releaseVersion = contentHandler.getReleaseVersion();
      buildVersion = contentHandler.getBuildVersion();
      milestoneVersion = contentHandler.getMilestoneVersion();

      return ( contentHandler.getRoot() );
    } catch ( final ParserConfigurationException e ) {
      throw new FileSystemException( "Failed to init XML system", e );
    } catch ( final SAXException e ) {
      throw new FileSystemException( "Failed to parse document", e );
    }
  }

  public byte[] getData( final FileName file ) throws FileSystemException {
    final String[] fileName = computeFileNames( file );
    final FileInfo fileInfo = lookupNode( fileName );
    if ( fileInfo == null ) {
      throw new FileSystemException( "File is not valid." );
    }

    return getDataInternally( fileInfo );
  }

  public void setData( final FileName file, final byte[] data ) throws FileSystemException {
    final String[] fileName = computeFileNames( file );
    final FileInfo fileInfo = lookupNode( fileName );
    if ( fileInfo == null ) {
      throw new FileSystemException( "File is not valid." );
    }

    setDataInternally( fileInfo, data );
  }

  public void createFolder( final FileName file ) throws FileSystemException {
    throw new FileSystemException( "CreateFolder is not implemented" );
  }

  protected abstract byte[] getDataInternally( final FileInfo fileInfo ) throws FileSystemException;

  protected abstract void setDataInternally( final FileInfo fileInfo, final byte[] data ) throws FileSystemException;

  public long getRefreshTime() {
    return refreshTime;
  }

  public void setRefreshTime( final long refreshTime ) {
    this.refreshTime = refreshTime;
  }

  public HashMap<FileName, String> getDescriptionEntries() {
    return descriptionEntries;
  }

  public String getMajorVersion() {
    return majorVersion;
  }

  public String getMinorVersion() {
    return minorVersion;
  }

  public String getReleaseVersion() {
    return releaseVersion;
  }

  public String getBuildVersion() {
    return buildVersion;
  }

  public String getMilestoneVersion() {
    return milestoneVersion;
  }
}
