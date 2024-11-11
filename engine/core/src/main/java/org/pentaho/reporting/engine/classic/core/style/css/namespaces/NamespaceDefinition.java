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


package org.pentaho.reporting.engine.classic.core.style.css.namespaces;

import org.pentaho.reporting.libraries.resourceloader.ResourceKey;

public interface NamespaceDefinition {
  public String getPrefix();

  public String getURI();

  public String[] getClassAttribute( String element );

  public String[] getStyleAttribute( String element );

  public ResourceKey getDefaultStyleSheetLocation();
}
