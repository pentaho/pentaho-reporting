/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.reporting.libraries.resourceloader;

public interface ResourceBundleData extends ResourceData {
  public ResourceKey getBundleKey();

  public ResourceBundleData deriveData( final ResourceKey key ) throws ResourceLoadingException;

  public ResourceManager deriveManager( final ResourceManager parent ) throws ResourceLoadingException;
}
