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


package org.pentaho.reporting.libraries.resourceloader.cache;

import org.pentaho.reporting.libraries.resourceloader.ResourceBundleData;

import java.io.Serializable;

/**
 * Creation-Date: 06.04.2006, 09:44:05
 *
 * @author Thomas Morgner
 */
public interface ResourceBundleDataCacheEntry extends Serializable {
  public ResourceBundleData getData();

  public long getStoredVersion();
}
