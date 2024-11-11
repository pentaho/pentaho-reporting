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


package org.pentaho.reporting.engine.classic.core.style;

/**
 * The interface that must be supported by objects that wish to receive notification of style change events.
 *
 * @author Thomas Morgner
 */
public interface StyleChangeListener {
  /**
   * Receives notification that a style has changed.
   *
   * @param source
   *          the source of the change.
   * @param key
   *          the style key.
   * @param value
   *          the value.
   */
  public void styleChanged( ElementStyleSheet source, StyleKey key, Object value );

  /**
   * Receives notification that a style has been removed.
   *
   * @param source
   *          the source of the change.
   * @param key
   *          the style key.
   */
  public void styleRemoved( ElementStyleSheet source, StyleKey key );
}
