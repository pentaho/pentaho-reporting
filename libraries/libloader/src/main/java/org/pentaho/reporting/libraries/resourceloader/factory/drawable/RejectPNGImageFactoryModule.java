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
