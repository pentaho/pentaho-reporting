/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

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
