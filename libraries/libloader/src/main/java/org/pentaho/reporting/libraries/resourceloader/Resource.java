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

package org.pentaho.reporting.libraries.resourceloader;

import java.io.Serializable;

/**
 * A resource is a wrapper around the final product. It shall not hold any references to the ResourceData object used to
 * create the resource (to allow efficient 2-stage caching).
 * <p/>
 * Although this interfaces declares to be serializable, this might not be the case for some of the content contained in
 * the resource object. Cache implementors should be aware of that issue and should act accordingly (for instance by not
 * caching that object).
 *
 * @author Thomas Morgner
 */
public interface Resource extends Serializable {
  public Object getResource() throws ResourceException;

  public Class getTargetType();

  public boolean isTemporaryResult();

  public long getVersion( ResourceKey key );

  /**
   * The primary source is also included in this set. The dependencies are given as ResourceKey objects. The keys itself
   * do not hold any state information.
   * <p/>
   * The dependencies do not track deep dependencies. So if Resource A depends on Resource B which depends on Resource
   * C, then A only knows about B, not C.
   *
   * @return
   */
  public ResourceKey[] getDependencies();

  public ResourceKey getSource();
}
