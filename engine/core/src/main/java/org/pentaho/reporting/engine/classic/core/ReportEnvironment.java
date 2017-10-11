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
