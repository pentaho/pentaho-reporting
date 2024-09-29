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

import org.pentaho.reporting.libraries.base.config.Configuration;

import java.util.Locale;

/**
 * Creation-Date: 16.11.2006, 17:06:38
 *
 * @author Thomas Morgner
 */
public interface GuiContext {
  public Locale getLocale();

  public IconTheme getIconTheme();

  public Configuration getConfiguration();
}
