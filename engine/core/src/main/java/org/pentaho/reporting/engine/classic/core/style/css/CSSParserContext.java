/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.reporting.engine.classic.core.style.css;

public class CSSParserContext {
  private static final CSSParserContext instance = new CSSParserContext();

  public static CSSParserContext getInstance() {
    return instance;
  }

  public String getDefaultNamespace() {
    return null;
  }

  public String lookupNamespaceURI( String id ) {
    return null;
  }
}
