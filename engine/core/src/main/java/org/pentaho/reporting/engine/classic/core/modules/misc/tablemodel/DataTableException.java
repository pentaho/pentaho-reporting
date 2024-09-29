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


package org.pentaho.reporting.engine.classic.core.modules.misc.tablemodel;

/**
 * Creation-Date: 07.09.2007, 12:35:39
 *
 * @author Thomas Morgner
 */
public class DataTableException extends RuntimeException {

  public DataTableException() {
  }

  public DataTableException( final String message, final Exception ex ) {
    super( message, ex );
  }

  public DataTableException( final String message ) {
    super( message );
  }
}
