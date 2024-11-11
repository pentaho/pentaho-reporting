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


package org.pentaho.reporting.engine.classic.core.modules.gui.common;

/**
 * Creation-Date: 15.05.2007, 16:47:14
 *
 * @author Thomas Morgner
 */
public interface StatusListener {
  public void setStatus( final StatusType type, final String text, final Throwable cause );
}
