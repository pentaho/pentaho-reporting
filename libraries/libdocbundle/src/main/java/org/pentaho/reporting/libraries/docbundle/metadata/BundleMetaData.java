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


package org.pentaho.reporting.libraries.docbundle.metadata;

import java.io.Serializable;

public interface BundleMetaData extends Serializable, Cloneable {
  public Object getBundleAttribute( String namespace, String attributeName );

  public String[] getNamespaces();

  public String[] getNames( String namespace );

  public Object clone() throws CloneNotSupportedException;
}
