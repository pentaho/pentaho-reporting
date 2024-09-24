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
