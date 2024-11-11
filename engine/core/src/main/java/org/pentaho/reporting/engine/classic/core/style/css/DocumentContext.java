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


package org.pentaho.reporting.engine.classic.core.style.css;

import org.pentaho.reporting.engine.classic.core.style.css.namespaces.NamespaceCollection;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public interface DocumentContext {
  ResourceManager getResourceManager();

  ResourceKey getContextKey();

  NamespaceCollection getNamespaces();

  Object getStyleResource();

  ElementStyleDefinition getStyleDefinition();
}
