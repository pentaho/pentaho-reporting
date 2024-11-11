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
