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

package org.pentaho.reporting.engine.classic.core.modules.gui.common;

/**
 * Creation-Date: 15.05.2007, 16:47:58
 *
 * @author Thomas Morgner
 */
public class StatusType {
  public static final StatusType ERROR = new StatusType( "ERROR" ); //$NON-NLS-1$
  public static final StatusType WARNING = new StatusType( "WARNING" ); //$NON-NLS-1$
  public static final StatusType INFORMATION = new StatusType( "INFORMATION" ); //$NON-NLS-1$
  public static final StatusType NONE = new StatusType( "NONE" ); //$NON-NLS-1$

  private final String myName; // for debug only

  private StatusType( final String name ) {
    myName = name;
  }

  public String toString() {
    return myName;
  }
}
