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
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyCreationException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public interface WriteableDocumentBundle extends DocumentBundle {
  public void createDirectoryEntry( final String name, final String mimeType ) throws IOException;

  public OutputStream createEntry( final String name, final String mimetype ) throws IOException;

  public boolean removeEntry( final String name ) throws IOException;

  public WriteableDocumentMetaData getWriteableDocumentMetaData();

  public ResourceKey createResourceKey( final String entryName,
                                        final Map factoryParameters ) throws ResourceKeyCreationException;

  public boolean isEmbeddedKey( final ResourceKey resourceKey );
}
