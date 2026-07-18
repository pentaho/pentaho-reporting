/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.reporting.libraries.base.config;

/**
 * A modifiable configuration.
 *
 * @author Thomas Morgner
 */
public interface ModifiableConfiguration extends Configuration {

  /**
   * Sets the value of a configuration property.
   *
   * @param key   the property key.
   * @param value the property value.
   */
  public void setConfigProperty( final String key, final String value );
}
