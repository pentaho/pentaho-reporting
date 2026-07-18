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

public class CSSParseException extends Exception {
  public CSSParseException() {
  }

  public CSSParseException( final String message ) {
    super( message );
  }

  public CSSParseException( final String message, final Throwable cause ) {
    super( message, cause );
  }

  public CSSParseException( final Throwable cause ) {
    super( cause );
  }
}
