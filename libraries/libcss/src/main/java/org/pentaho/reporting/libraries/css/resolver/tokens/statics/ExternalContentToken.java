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



package org.pentaho.reporting.libraries.css.resolver.tokens.statics;

import org.pentaho.reporting.libraries.css.resolver.tokens.types.GenericType;

public class ExternalContentToken
  extends StaticToken implements GenericType {
  private Object data;

  public ExternalContentToken( final Object data ) {
    this.data = data;
  }

  public Object getRaw() {
    return data;
  }
}
