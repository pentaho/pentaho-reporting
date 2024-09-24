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

package org.pentaho.reporting.engine.classic.core.style.css;

import org.pentaho.reporting.engine.classic.core.style.css.namespaces.NamespaceCollection;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class DefaultDocumentContext implements DocumentContext {
  private NamespaceCollection namespaces;
  private ResourceManager resourceManager;
  private ResourceKey contextKey;
  private Object styleResourceKey;
  private ElementStyleDefinition styleDefinition;

  public DefaultDocumentContext( final NamespaceCollection namespaces, final ResourceManager resourceManager,
      final ResourceKey contextKey, final Object styleResourceKey, final ElementStyleDefinition styleDefinition ) {
    this.namespaces = namespaces;
    this.resourceManager = resourceManager;
    this.contextKey = contextKey;
    this.styleResourceKey = styleResourceKey;
    this.styleDefinition = styleDefinition;
  }

  public ResourceManager getResourceManager() {
    return resourceManager;
  }

  public ResourceKey getContextKey() {
    return contextKey;
  }

  public NamespaceCollection getNamespaces() {
    return namespaces;
  }

  public Object getStyleResource() {
    return styleResourceKey;
  }

  public ElementStyleDefinition getStyleDefinition() {
    return styleDefinition;
  }

}
