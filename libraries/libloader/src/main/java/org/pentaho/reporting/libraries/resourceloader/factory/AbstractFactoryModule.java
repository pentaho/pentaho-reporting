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
