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

package org.pentaho.reporting.libraries.resourceloader.cache;

import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;

/**
 * Creation-Date: 06.04.2006, 10:10:18
 *
 * @author Thomas Morgner
 */
public interface ResourceFactoryCache {
  public Resource get( ResourceKey key, Class[] target );

  public void put( final Resource resource );

  public void remove( final Resource resource );

  public void clear();

  public void shutdown();
}
