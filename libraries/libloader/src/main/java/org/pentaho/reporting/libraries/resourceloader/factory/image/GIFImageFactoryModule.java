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

package org.pentaho.reporting.libraries.resourceloader.factory.image;

import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.resourceloader.SimpleResource;
import org.pentaho.reporting.libraries.resourceloader.factory.AbstractFactoryModule;

import java.awt.*;

/**
 * Creation-Date: 05.04.2006, 17:35:12
 *
 * @author Thomas Morgner
 */
public class GIFImageFactoryModule extends AbstractFactoryModule {
  private static final int[] FINGERPRINT = { 'G', 'I', 'F', '8' };
  private static final String[] MIMETYPES =
    {
      "image/gif",
      "image/x-xbitmap",
      "image/gi_"
    };

  private static final String[] FILEEXTENSIONS =
    {
      ".gif"
    };

  public GIFImageFactoryModule() {
  }

  public int getHeaderFingerprintSize() {
    return GIFImageFactoryModule.FINGERPRINT.length;
  }

  protected int[] getFingerPrint() {
    return GIFImageFactoryModule.FINGERPRINT;
  }

  protected String[] getMimeTypes() {
    return GIFImageFactoryModule.MIMETYPES;
  }

  protected String[] getFileExtensions() {
    return GIFImageFactoryModule.FILEEXTENSIONS;
  }

  public Resource create( final ResourceManager caller,
                          final ResourceData data,
                          final ResourceKey context )
    throws ResourceLoadingException {
    final long version = data.getVersion( caller );
    final Image image =
      Toolkit.getDefaultToolkit().createImage( data.getResource( caller ) );
    return new SimpleResource( data.getKey(), image, Image.class, version );
  }
}
