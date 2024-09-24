/*
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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.resourceloader.loader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.resourceloader.LibLoaderBoot;
import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * A generic read handler for URL resources.
 *
 * @author Thomas Morgner
 */
@SuppressWarnings( "UnnecessaryBoxing" )
public class URLResourceData extends AbstractResourceData {
  private static final Log logger = LogFactory.getLog( URLResourceData.class );
  private static final long serialVersionUID = -7183025686032509509L;
  private static Long fixedCacheDelay;
  private long lastDateMetaDataRead;
  private long modificationDate;
  private String filename;
  private Long contentLength;
  private String contentType;
  private boolean metaDataOK;
  private URL url;
  private ResourceKey key;
  private static Boolean fixBrokenWebServiceDateHeader;

  protected static long getFixedCacheDelay() {
    if ( fixedCacheDelay == null ) {
      fixedCacheDelay = new Long( LibLoaderBoot.getInstance().getExtendedConfig().getIntProperty
        ( "org.pentaho.reporting.libraries.resourceloader.config.url.FixedCacheDelay", 5000 ) );
    }
    return fixedCacheDelay.longValue();
  }

  protected static boolean isFixBrokenWebServiceDateHeader() {
    if ( fixBrokenWebServiceDateHeader == null ) {
      fixBrokenWebServiceDateHeader = Boolean.valueOf( LibLoaderBoot.getInstance().getExtendedConfig().getBoolProperty
        ( "org.pentaho.reporting.libraries.resourceloader.config.url.FixBrokenWebServiceDateHeader", false ) );
    }
    return fixBrokenWebServiceDateHeader.booleanValue();
  }

  public URLResourceData( final ResourceKey key ) {
    if ( key == null ) {
      throw new NullPointerException();
    }

    this.modificationDate = -1;
    this.key = key;
    this.url = (URL) key.getIdentifier();
    // for the ease of implementation, we take the file name from the URL.
    // Feel free to add a 'Content-Disposition' parser with all details :)
    this.filename = IOUtils.getInstance().getFileName( url );
  }

  protected void setUrl( final URL url ) {
    this.url = url;
  }

  protected void setKey( final ResourceKey key ) {
    this.key = key;
  }

  protected void setFilename( final String filename ) {
    this.filename = filename;
  }

  protected URL getUrl() {
    return url;
  }

  protected String getFilename() {
    return filename;
  }

  private void readMetaData() throws IOException {
    if ( metaDataOK ) {
      if ( ( System.currentTimeMillis() - lastDateMetaDataRead ) < URLResourceData.getFixedCacheDelay() ) {
        return;
      }
      if ( isFixBrokenWebServiceDateHeader() ) {
        return;
      }

    }

    final URLConnection c = url.openConnection();
    c.setDoOutput( false );
    c.setAllowUserInteraction( false );
    if ( c instanceof HttpURLConnection ) {
      final HttpURLConnection httpURLConnection = (HttpURLConnection) c;
      httpURLConnection.setRequestMethod( "HEAD" );
    }
    c.connect();
    readMetaData( c );
    c.getInputStream().close();
  }

  private void readMetaData( final URLConnection c ) {
    modificationDate = c.getHeaderFieldDate( "last-modified", -1 );
    if ( modificationDate <= 0 ) {
      if ( isFixBrokenWebServiceDateHeader() ) {
        modificationDate = System.currentTimeMillis();
      } else {
        modificationDate = -1;
      }
    }
    contentLength = new Long( c.getContentLength() );
    contentType = c.getHeaderField( "content-type" );
    metaDataOK = true;
    lastDateMetaDataRead = System.currentTimeMillis();
  }

  public InputStream getResourceAsStream( final ResourceManager caller ) throws ResourceLoadingException {
    try {
      final URLConnection c = url.openConnection();
      c.setDoOutput( false );
      c.setAllowUserInteraction( false );
      c.connect();
      if ( isFixBrokenWebServiceDateHeader() == false ) {
        readMetaData( c );
      }
      return c.getInputStream();
    } catch ( IOException e ) {
      throw new ResourceLoadingException( "Failed to open URL connection", e );
    }
  }

  public Object getAttribute( final String key ) {
    if ( key.equals( ResourceData.FILENAME ) ) {
      return filename;
    }
    if ( key.equals( ResourceData.CONTENT_LENGTH ) ) {
      try {
        if ( metaDataOK == false ) {
          readMetaData();
        }
        return contentLength;
      } catch ( IOException e ) {
        if ( logger.isDebugEnabled() ) {
          logger.debug( "No response metadata could be read from the input stream", e );
        }
        return null;
      }
    }
    if ( key.equals( ResourceData.CONTENT_TYPE ) ) {
      try {
        if ( metaDataOK == false ) {
          readMetaData();
        }
        return contentType;
      } catch ( IOException e ) {
        if ( logger.isDebugEnabled() ) {
          logger.debug( "No response metadata could be read from the input stream", e );
        }
        return null;
      }
    }
    return null;
  }

  public long getVersion( final ResourceManager caller )
    throws ResourceLoadingException {
    try {
      // always read the new date .. sorry, this is expensive, but needed here
      // else the cache would not be in sync ...
      readMetaData();
      return modificationDate;
    } catch ( IOException e ) {
      if ( logger.isDebugEnabled() ) {
        logger.debug( "No response metadata could be read from the input stream", e );
      }
      return -1;
    }
  }

  public ResourceKey getKey() {
    return key;
  }
}
