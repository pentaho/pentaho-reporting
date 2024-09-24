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

import org.pentaho.reporting.libraries.resourceloader.factory.AbstractFactoryModule;

/**
 * Creation-Date: 05.04.2006, 17:52:57
 *
 * @author Thomas Morgner
 */
public abstract class AbstractWMFFactoryModule extends AbstractFactoryModule {
  private static final int[] FINGERPRINT = { 0xD7, 0xCD };

  private static final String[] MIMETYPES =
    {
      "application/x-msmetafile",
      "application/wmf",
      "application/x-wmf",
      "image/wmf",
      "image/x-wmf",
      "image/x-win-metafile",
      "zz-application/zz-winassoc-wmf"
    };

  private static final String[] FILEEXTENSIONS =
    {
      ".wmf"
    };

  protected AbstractWMFFactoryModule() {
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

  public int getHeaderFingerprintSize() {
    return 2;
  }
}
