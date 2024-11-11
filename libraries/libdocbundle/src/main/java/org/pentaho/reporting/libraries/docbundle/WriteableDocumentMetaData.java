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

public interface WriteableDocumentMetaData extends DocumentMetaData {
  public void setBundleType( String type );

  public void setEntryMimeType( String entry, String type );

  public void setEntryAttribute( final String entryName, final String attributeName, final String value );

  public void setBundleAttribute( String namespace, String name, Object value );

  public boolean removeEntry( String entry );
}
