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

package org.pentaho.reporting.libraries.resourceloader.modules.factory.wmf;

import org.pentaho.reporting.libraries.pixie.wmf.WmfFile;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.resourceloader.SimpleResource;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * Creation-Date: 05.04.2006, 17:58:42
 *
 * @author Thomas Morgner
 */
public class WMFImageFactoryModule extends AbstractWMFFactoryModule {
  public WMFImageFactoryModule() {
  }

  public Resource create( final ResourceManager caller,
                          final ResourceData data,
                          final ResourceKey context )
    throws ResourceLoadingException {
    try {
      final long version = data.getVersion( caller );
      final InputStream stream = data.getResourceAsStream( caller );
      try {
        final WmfFile wmfFile = new WmfFile( stream, -1, -1 );
        final Image image = wmfFile.replay();
        return new SimpleResource( data.getKey(), image, Image.class, version );
      } finally {
        stream.close();
      }
    } catch ( IOException e ) {
      throw new ResourceLoadingException( "Failed to process WMF file", e );
    }
  }

}
