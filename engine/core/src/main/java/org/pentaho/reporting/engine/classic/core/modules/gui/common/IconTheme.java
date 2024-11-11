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

import java.util.Locale;

import javax.swing.Icon;

import org.pentaho.reporting.libraries.base.config.Configuration;

/**
 * An Icon-Theme is an extension point to replace the icons that are used by JFreeReport. Icons provided by the theme
 * must be available in two flavours: Large (24x24) and small (16x16).
 *
 * @author Thomas Morgner
 */
public interface IconTheme {
  public void initialize( Configuration configuration );

  public Icon getSmallIcon( Locale locale, String id );

  public Icon getLargeIcon( Locale locale, String id );
}
