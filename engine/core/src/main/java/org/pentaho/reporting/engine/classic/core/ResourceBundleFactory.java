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
