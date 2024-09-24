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

package org.pentaho.reporting.libraries.resourceloader.factory;

import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

/**
 * Creation-Date: 05.04.2006, 17:00:33
 *
 * @author Thomas Morgner
 */
public interface FactoryModule {
  public static final int RECOGNIZED_FINGERPRINT = 4000;
  public static final int RECOGNIZED_CONTENTTYPE = 2000;
  public static final int RECOGNIZED_FILE = 1000;
  /**
   * A default handler does not reject the content.
   */
  public static final int FEELING_LUCKY = 0;
  public static final int REJECTED = -1;

  public int canHandleResource( final ResourceManager caller,
                                final ResourceData data )
    throws ResourceCreationException, ResourceLoadingException;

  public int getHeaderFingerprintSize();

  public Resource create( final ResourceManager caller,
                          final ResourceData data,
                          final ResourceKey context )
    throws ResourceCreationException, ResourceLoadingException;
}
