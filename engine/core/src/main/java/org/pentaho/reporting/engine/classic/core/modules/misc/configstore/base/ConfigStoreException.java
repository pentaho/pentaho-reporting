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


package org.pentaho.reporting.engine.classic.core.modules.misc.configstore.base;

/**
 * The config store exception is throwns if an error prevents an operation on the current configuration storage
 * provider.
 *
 * @author Thomas Morgner
 */
public class ConfigStoreException extends Exception {
  /**
   * DefaultConstructor.
   */
  public ConfigStoreException() {
  }

  /**
   * Creates a config store exception with the given message and root exception.
   *
   * @param s
   *          the exception message.
   * @param e
   *          the exception that caused all the trouble.
   */
  public ConfigStoreException( final String s, final Exception e ) {
    super( s, e );
  }

  /**
   * Creates a config store exception with the given message.
   *
   * @param s
   *          the message.
   */
  public ConfigStoreException( final String s ) {
    super( s );
  }
}
