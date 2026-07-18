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



package org.pentaho.reporting.libraries.docbundle;

public interface WriteableDocumentMetaData extends DocumentMetaData {
  public void setBundleType( String type );

  public void setEntryMimeType( String entry, String type );

  public void setEntryAttribute( final String entryName, final String attributeName, final String value );

  public void setBundleAttribute( String namespace, String name, Object value );

  public boolean removeEntry( String entry );
}
