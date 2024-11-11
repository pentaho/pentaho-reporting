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


package org.pentaho.reporting.libraries.resourceloader.modules.factory.svg;

import org.pentaho.reporting.libraries.resourceloader.factory.AbstractFactoryModule;

/**
 * Creation-Date: 05.04.2006, 17:52:57
 *
 * @author Thomas Morgner
 */
public abstract class AbstractSVGFactoryModule extends AbstractFactoryModule {
  private static final int[] FINGERPRINT = new int[ 0 ];

  private static final String[] MIMETYPES =
    {
      "image/svg-xml", "image/svg+xml"
    };

  private static final String[] FILEEXTENSIONS =
    {
      ".svg"
    };

  protected AbstractSVGFactoryModule() {
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
    return FINGERPRINT.length;
  }
}
