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

package org.pentaho.reporting.engine.classic.core;

import java.io.Serializable;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;

/**
 * A resource bundle factory defines the locale for a report and is used to create resourcebundles.
 *
 * @author Thomas Morgner
 */
public interface ResourceBundleFactory extends Serializable {
  /**
   * A constant containing the configuration key to specify a resource-bundle name for the report.
   * <p/>
   * This property has the value "org.pentaho.reporting.engine.classic.core.ResourceBundle".
   */
  public static final String DEFAULT_RESOURCE_BUNDLE_CONFIG_KEY =
      "org.pentaho.reporting.engine.classic.core.ResourceBundle";

  /**
   * Creates a resource bundle for the given key. How that key is interpreted depends on the used concrete
   * implementation of this interface.
   *
   * @param key
   *          the key that identifies the resource bundle
   * @return the created resource bundle
   * @throws java.util.MissingResourceException
   *           if no resource bundle for the specified base name can be found
   */
  public ResourceBundle getResourceBundle( String key );

  /**
   * Returns the locale that will be used to create the resource bundle. This locale is also used to initialize the
   * java.text.Format instances used by the report.
   *
   * @return the locale.
   */
  public Locale getLocale();

  public TimeZone getTimeZone();
}
