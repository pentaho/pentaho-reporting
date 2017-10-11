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

package org.pentaho.reporting.libraries.css.parser;

import org.pentaho.reporting.libraries.css.model.StyleKeyRegistry;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;

import java.util.Map;

/**
 * Creation-Date: 25.11.2005, 17:47:10
 *
 * @author Thomas Morgner
 */
public class CSSParserContext {
  private static class ThreadContextVar extends ThreadLocal {
    public ThreadContextVar() {
    }

    public Object initialValue() {
      return new CSSParserContext();
    }
  }

  private static ThreadContextVar contextVar = new ThreadContextVar();

  private StyleKeyRegistry styleKeyRegistry;
  private CSSValueFactory valueFactory;
  private ResourceKey source;
  private Map namespaces;
  private String defaultNamespace;

  public static CSSParserContext getContext() {
    return (CSSParserContext) contextVar.get();
  }

  private CSSParserContext() {
  }

  public void setStyleKeyRegistry( final StyleKeyRegistry styleKeyRegistry ) {
    if ( styleKeyRegistry == null ) {
      this.styleKeyRegistry = null;
      this.valueFactory = null;
    } else {
      this.styleKeyRegistry = styleKeyRegistry;
      this.valueFactory = new CSSValueFactory( styleKeyRegistry );
    }
  }

  public StyleKeyRegistry getStyleKeyRegistry() {
    return styleKeyRegistry;
  }

  public CSSValueFactory getValueFactory() {
    return valueFactory;
  }

  public ResourceKey getSource() {
    return source;
  }

  public void setSource( final ResourceKey source ) {
    this.source = source;
  }

  public Map getNamespaces() {
    return namespaces;
  }

  public void setNamespaces( final Map namespaces ) {
    this.namespaces = namespaces;
  }

  public String getDefaultNamespace() {
    return defaultNamespace;
  }

  public void setDefaultNamespace( final String defaultNamespace ) {
    this.defaultNamespace = defaultNamespace;
  }

  public void destroy() {
    this.defaultNamespace = null;
    this.namespaces = null;
    this.source = null;
    this.styleKeyRegistry = null;
    this.valueFactory = null;
  }
}
