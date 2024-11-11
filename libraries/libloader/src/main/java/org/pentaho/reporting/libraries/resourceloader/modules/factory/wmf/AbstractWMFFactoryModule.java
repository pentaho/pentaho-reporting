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
