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

package org.pentaho.reporting.libraries.docbundle;

import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.IOException;
import java.io.InputStream;

/**
 * Creation-Date: 16.12.2007, 15:10:34
 *
 * @author Thomas Morgner
 */
public interface DocumentBundle {
  public DocumentMetaData getMetaData();

  public boolean isEntryExists( final String name );

  public boolean isEntryReadable( final String name );

  public InputStream getEntryAsStream( final String name ) throws IOException;

  public String getEntryMimeType( final String name );

  public ResourceKey getBundleKey();

  public ResourceManager getResourceManager();
}
