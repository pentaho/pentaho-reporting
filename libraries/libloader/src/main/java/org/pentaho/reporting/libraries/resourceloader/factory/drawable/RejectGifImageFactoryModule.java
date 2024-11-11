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


package org.pentaho.reporting.libraries.resourceloader.factory.drawable;

import org.pentaho.reporting.libraries.resourceloader.ContentNotRecognizedException;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.resourceloader.factory.AbstractFactoryModule;

public class RejectGifImageFactoryModule extends AbstractFactoryModule {
  private static final int[] FINGERPRINT = { 'G', 'I', 'F', '8' };

  public RejectGifImageFactoryModule() {
  }

  protected int[] getFingerPrint() {
    return FINGERPRINT;
  }

  protected String[] getMimeTypes() {
    return new String[ 0 ];
  }

  protected String[] getFileExtensions() {
    return new String[ 0 ];
  }

  public Resource create( final ResourceManager caller,
                          final ResourceData data,
                          final ResourceKey context ) throws ResourceCreationException, ResourceLoadingException {
    throw new ContentNotRecognizedException( "Reject GIF as drawable" );
  }
}
