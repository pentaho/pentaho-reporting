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

package org.pentaho.reporting.libraries.docbundle.metadata;

import java.io.Serializable;

public interface BundleManifest extends Serializable {
  public String getMimeType( String entry );

  public String[] getEntries();

  public String getAttribute( String entryName, String attributeName );

  public String[] getAttributeNames( String entryName );
}
