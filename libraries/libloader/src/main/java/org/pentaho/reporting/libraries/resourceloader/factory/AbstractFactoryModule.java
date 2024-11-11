/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.libraries.resourceloader.factory;

import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.resourceloader.ResourceCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.IOException;
import java.io.InputStream;

/**
 * Creation-Date: 05.04.2006, 17:44:42
 *
 * @author Thomas Morgner
 */
public abstract class AbstractFactoryModule implements FactoryModule {
  protected AbstractFactoryModule() {
  }

  protected abstract int[] getFingerPrint();

  protected abstract String[] getMimeTypes();

  protected abstract String[] getFileExtensions();


  public int getHeaderFingerprintSize() {
    final int[] fingerPrint = getFingerPrint();
    if ( fingerPrint == null ) {
      return 0;
    }
    return fingerPrint.length;
  }

  public int canHandleResource( final ResourceManager caller,
                                final ResourceData data )
    throws ResourceCreationException, ResourceLoadingException {
    try {
      final InputStream asStream = data.getResourceAsStream( caller );
      try {
        if ( getHeaderFingerprintSize() != 0 ) {
          if ( canHandleResourceByContent( asStream ) ) {
            return RECOGNIZED_FINGERPRINT;
          } else {
            return REJECTED;
          }
        }
        final String mimeType = (String)
          data.getAttribute( ResourceData.CONTENT_TYPE );
        if ( mimeType != null && canHandleResourceByMimeType( mimeType ) ) {
          return RECOGNIZED_CONTENTTYPE;
        }

        final String fileName = (String)
          data.getAttribute( ResourceData.FILENAME );
        if ( fileName != null && canHandleResourceByName( fileName ) ) {
          return RECOGNIZED_FILE;
        }
        if ( getHeaderFingerprintSize() != 0 ) {
          return FEELING_LUCKY;
        }
        return REJECTED;
      } finally {
        asStream.close();
      }
    } catch ( ResourceLoadingException e ) {
      throw e;
    } catch ( Exception e ) {
      throw new ResourceCreationException( "Failed to load or check content", e );
    }
  }

  protected boolean canHandleResourceByContent( final InputStream data )
    throws IOException {
    final int[] fingerprint = getFingerPrint();
    if ( fingerprint.length == 0 ) {
      return false;
    }
    for ( int i = 0; i < fingerprint.length; i++ ) {
      if ( fingerprint[ i ] != data.read() ) {
        return false;
      }
    }
    return true;
  }

  protected boolean canHandleResourceByMimeType( final String name ) {
    final String[] mimes = getMimeTypes();
    for ( int i = 0; i < mimes.length; i++ ) {
      if ( name.equals( mimes[ i ] ) ) {
        return true;
      }
    }

    final int idx = name.indexOf( ';' );
    if ( idx > 0 ) {
      return canHandleResourceByMimeType( name.substring( 0, idx ) );
    }
    return false;
  }

  protected boolean canHandleResourceByName( final String name ) {
    final String[] fexts = getFileExtensions();
    for ( int i = 0; i < fexts.length; i++ ) {
      if ( StringUtils.endsWithIgnoreCase( name, fexts[ i ] ) ) {
        return true;
      }
    }
    return false;
  }

}
