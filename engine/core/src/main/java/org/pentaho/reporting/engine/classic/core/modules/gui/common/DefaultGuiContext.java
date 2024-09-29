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

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.libraries.base.config.Configuration;

import java.util.Locale;

/**
 * Creation-Date: 16.11.2006, 17:05:27
 *
 * @author Thomas Morgner
 */
public class DefaultGuiContext implements GuiContext {
  private Locale locale;
  private IconTheme iconTheme;
  private Configuration configuration;

  public DefaultGuiContext() {
    this.configuration = ClassicEngineBoot.getInstance().getGlobalConfig();
    this.locale = Locale.getDefault();
    this.iconTheme = new DefaultIconTheme();
    this.iconTheme.initialize( configuration );
  }

  public DefaultGuiContext( final Locale locale, final IconTheme iconTheme, final Configuration configuration ) {
    this.locale = locale;
    this.iconTheme = iconTheme;
    this.configuration = configuration;
  }

  public Locale getLocale() {
    return locale;
  }

  public IconTheme getIconTheme() {
    return iconTheme;
  }

  public Configuration getConfiguration() {
    return configuration;
  }
}
