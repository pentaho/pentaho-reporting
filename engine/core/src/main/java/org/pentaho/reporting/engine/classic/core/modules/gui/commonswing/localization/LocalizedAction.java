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


package org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.localization;

import java.util.Locale;

import javax.swing.Action;

/**
 * Creation-Date: 30.11.2006, 13:09:37
 *
 * @author Thomas Morgner
 */
public interface LocalizedAction extends Action {
  public void update( Locale locale );
}
