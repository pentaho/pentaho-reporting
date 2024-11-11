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


package org.pentaho.reporting.engine.classic.core;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Provides a simplified interface to access information about the processing environment in a simple way. The report
 * environment system replaces the old parser-config system.
 *
 * @author Thomas Morgner
 */
public interface ReportEnvironment extends Serializable, Cloneable {
  /**
   * Returns a environment property. These properties are usually defined by the outside world, but default values can
   * be specified inside the report-definition.
   *
   * @param key
   * @return
   */
  public Object getEnvironmentProperty( String key );

  /**
   * Returns the text encoding that should be used to encode URLs.
   *
   * @return the encoding for URLs.
   */
  public String getURLEncoding();

  public Locale getLocale();

  public TimeZone getTimeZone();

  public Object clone();

  public Map<String, String[]> getUrlExtraParameter();
}
