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

package org.pentaho.reporting.libraries.resourceloader.factory.drawable;

import org.pentaho.reporting.libraries.resourceloader.ContentNotRecognizedException;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.resourceloader.factory.AbstractFactoryModule;

import java.io.IOException;
import java.io.InputStream;

public class RejectJPEGImageFactoryModule extends AbstractFactoryModule {
  private static final int[] FINGERPRINT_1 = { 0xFF, 0xD8 };

  private static final String[] MIMETYPES =
    {
      "image/jpeg",
      "image/jpg",
      "image/jp_",
      "application/jpg",
      "application/x-jpg",
      "image/pjpeg",
      "image/pipeg",
      "image/vnd.swiftview-jpeg",
      "image/x-xbitmap"
    };

  private static final String[] FILEEXTENSIONS =
    {
      ".jpg", ".jpeg"
    };
  private static final int[] EMPTY_ARRAY = new int[ 0 ];

  public RejectJPEGImageFactoryModule() {
  }

  public int getHeaderFingerprintSize() {
    // indicate that we cannot allow the generic fingerprinting. We need to test it by ourselfs.
    return -1;
  }

  protected boolean canHandleResourceByContent( final InputStream data )
    throws IOException {
    final int[] fingerprint = FINGERPRINT_1;
    for ( int i = 0; i < fingerprint.length; i++ ) {
      if ( fingerprint[ i ] != data.read() ) {
        return false;
      }
    }

    if ( data.read() == -1 ) {
      return false;
    }
    if ( data.read() == -1 ) {
      return false;
    }

    //    fingerprint = FINGERPRINT_2;
    //    for (int i = 0; i < fingerprint.length; i++)
    //    {
    //      if (fingerprint[i] != data.read())
    //      {
    //        return false;
    //      }
    //    }
    return true;
  }

  protected int[] getFingerPrint() {
    return EMPTY_ARRAY;
  }

  protected String[] getMimeTypes() {
    return (String[]) RejectJPEGImageFactoryModule.MIMETYPES.clone();
  }

  protected String[] getFileExtensions() {
    return (String[]) RejectJPEGImageFactoryModule.FILEEXTENSIONS.clone();
  }

  public Resource create( final ResourceManager caller,
                          final ResourceData data,
                          final ResourceKey context )
    throws ResourceLoadingException, ResourceCreationException {
    throw new ContentNotRecognizedException();
  }
}
