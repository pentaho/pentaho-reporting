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
