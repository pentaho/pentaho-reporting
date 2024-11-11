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


package org.pentaho.reporting.libraries.css.namespace;

import org.pentaho.reporting.libraries.resourceloader.ResourceKey;

/**
 * Creation-Date: 13.04.2006, 12:31:43
 *
 * @author Thomas Morgner
 */
public interface NamespaceDefinition {
  public String getPreferredPrefix();

  public String getURI();

  public String[] getClassAttribute( String element );

  public String[] getStyleAttribute( String element );

  public ResourceKey getDefaultStyleSheetLocation();
}
