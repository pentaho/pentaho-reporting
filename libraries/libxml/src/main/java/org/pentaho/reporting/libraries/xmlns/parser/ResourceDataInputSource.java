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


package org.pentaho.reporting.libraries.xmlns.parser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.xml.sax.InputSource;

import java.io.InputStream;
import java.net.URL;

/**
 * A SAX InputSource implementation that reads its data from a LibLoader ResourceData object.
 *
 * @author Thomas Morgner
 */
public class ResourceDataInputSource extends InputSource {
  private static final Log logger = LogFactory.getLog( ResourceDataInputSource.class );
  private ResourceData data;
  private long version;
  private ResourceManager caller;

  /**
   * Creates a new InputSource using the given resource-data and resource-manager as source.
   *
   * @param data   the resource-data object holding the raw-data.
   * @param caller the resource manager that is loading the current resource.
   * @throws ResourceLoadingException if an error occurs.
   * @see #setPublicId
   * @see #setSystemId
   * @see #setByteStream
   * @see #setCharacterStream
   * @see #setEncoding
   */
  public ResourceDataInputSource( final ResourceData data,
                                  final ResourceManager caller )
    throws ResourceLoadingException {
    if ( data == null ) {
      throw new NullPointerException( "Data must not be null" );
    }
    if ( caller == null ) {
      throw new NullPointerException( "ResourceManager must not be null" );
    }
    this.data = data;
    this.version = data.getVersion( caller );
    this.caller = caller;
    final URL url = caller.toURL( data.getKey() );
    if ( url != null ) {
      setSystemId( url.toExternalForm() );
    }
  }


  /**
   * Set the byte stream for this input source.
   *
   * @param byteStream A byte stream containing an XML document or other entity.
   */
  public void setByteStream( final InputStream byteStream ) {
    throw new UnsupportedOperationException();
  }

  /**
   * Get the byte stream for this input source.
   * <p/>
   * <p>The getEncoding method will return the character encoding for this byte stream, or null if unknown.</p>
   *
   * @return The byte stream, or null if none was supplied.
   * @see #getEncoding
   * @see #setByteStream
   */
  public InputStream getByteStream() {
    try {
      return data.getResourceAsStream( caller );
    } catch ( ResourceLoadingException e ) {
      logger.error( "Unable to create byte-stream: " + data.getKey() );
      return null;
    }
  }

  /**
   * Returns the resource-data object that provides the raw-data.
   *
   * @return the resource-data object.
   */
  public ResourceData getData() {
    return data;
  }

  /**
   * Returns the version (changetracker) of this input source.
   *
   * @return the version (changetracker) of the input source.
   */
  public long getVersion() {
    return version;
  }
}
