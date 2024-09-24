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

package org.pentaho.reporting.libraries.resourceloader.factory.drawable;

import org.pentaho.reporting.libraries.resourceloader.ContentNotRecognizedException;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.resourceloader.factory.AbstractFactoryModule;

/**
 * Creation-Date: 05.04.2006, 17:35:12
 *
 * @author Thomas Morgner
 */
public class RejectPNGImageFactoryModule extends AbstractFactoryModule {
  private static final int[] FINGERPRINT = { 137, 80, 78, 71, 13, 10, 26, 10 };

  private static final String[] MIMETYPES =
    {
      "image/png",
      "application/png",
      "application/x-png"
    };

  private static final String[] FILEEXTENSIONS =
    {
      ".png",
    };

  public RejectPNGImageFactoryModule() {
  }

  public int getHeaderFingerprintSize() {
    return FINGERPRINT.length;
  }

  protected int[] getFingerPrint() {
    return FINGERPRINT;
  }

  protected String[] getMimeTypes() {
    return MIMETYPES;
  }

  protected String[] getFileExtensions() {
    return FILEEXTENSIONS;
  }

  public Resource create( final ResourceManager caller,
                          final ResourceData data,
                          final ResourceKey context )
    throws ResourceLoadingException, ResourceCreationException {
    throw new ContentNotRecognizedException();
  }
}
