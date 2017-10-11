/*!
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

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
