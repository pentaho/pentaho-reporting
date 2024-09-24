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

package org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper;

/**
 * Creation-Date: 30.08.2007, 17:14:39
 *
 * @author Thomas Morgner
 */
public class HtmlOutputProcessingException extends RuntimeException {
  public HtmlOutputProcessingException() {
  }

  public HtmlOutputProcessingException( final String message, final Exception ex ) {
    super( message, ex );
  }

  public HtmlOutputProcessingException( final String message ) {
    super( message );
  }
}
