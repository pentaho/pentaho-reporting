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



package org.pentaho.reporting.libraries.docbundle.metadata;

import java.io.Serializable;

public interface BundleManifest extends Serializable {
  public String getMimeType( String entry );

  public String[] getEntries();

  public String getAttribute( String entryName, String attributeName );

  public String[] getAttributeNames( String entryName );
}
