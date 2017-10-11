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

package org.pentaho.reporting.libraries.css.dom;

import org.pentaho.reporting.libraries.css.model.StyleKeyRegistry;
import org.pentaho.reporting.libraries.css.namespace.NamespaceCollection;
import org.pentaho.reporting.libraries.css.namespace.NamespaceDefinition;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

/**
 * The document context allows LibCSS to interact with the calling implementation. LibCSS tries to minimize the
 * assumptions made on the internal of external systems by providing a minimized set of interfaces which allow LibCSS to
 * retrieve all required information without imposing a specific implementation on the caller.
 *
 * @author : Thomas Morgner
 */
public interface DocumentContext {
  public LayoutOutputMetaData getOutputMetaData();

  public StyleReference[] getStyleReferences();

  /**
   * Returns the resource manager that is used to load externally referenced resources. Such resources can be either
   * images, drawable or other stylesheets. In some cases, this might even reference whole documents.
   * <p/>
   * The implementation should indicate which document types can be loaded using the {@link
   * DocumentContext#getSupportedResourceTypes()} method.
   *
   * @return the resource manager.
   * @see DocumentContext#getSupportedResourceTypes()
   */
  public ResourceManager getResourceManager();

  /**
   * Returns the context key provides the base-key for resolving relative URLs. Usually it is the key that was used to
   * parse the document. Without this key, it would be impossible to resolve non-absolute URLs/paths into a usable URL
   * or path.
   *
   * @return the context key
   * @see ResourceManager#deriveKey(ResourceKey, String)
   */
  public ResourceKey getContextKey();

  /**
   * Returns the style-key registry that holds all known stylekeys that might be encountered during the parsing. As this
   * library might be used in several CSS-enabled systems at the same time (ie. Charting, Classic-Report-Engine,
   * anything else) the library uses this mechanism to separate the stylesheet sets of each application from each
   * other.
   *
   * @return the stylekey registry to use.
   */
  public StyleKeyRegistry getStyleKeyRegistry();

  /**
   * Returns the list of supported resource types that can be loaded as external resources.
   *
   * @return the supported resource types.
   * @see ResourceManager#create(ResourceKey, ResourceKey, Class[])
   */
  public Class[] getSupportedResourceTypes();

  /**
   * Returns information about the known namespaces. This allows the system to recognize 'class' and 'style' attributes
   * for each defined namespace.
   *
   * @return the defines namespaces.
   * @see NamespaceDefinition
   * @see NamespaceCollection#getDefinition(String)
   */
  public NamespaceCollection getNamespaces();
}
