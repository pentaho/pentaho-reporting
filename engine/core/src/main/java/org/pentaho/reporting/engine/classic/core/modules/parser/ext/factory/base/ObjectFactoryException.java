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

package org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base;

/**
 * An exception that is thrown, if the creation of an Object failed in the ObjectFactory implementation.
 *
 * @author Thomas Morgner.
 */
public class ObjectFactoryException extends Exception {

  /**
   * Constructs a new exception with <code>null</code> as its detail message. The cause is not initialized, and may
   * subsequently be initialized by a call to {@link #initCause}.
   */
  public ObjectFactoryException() {
    super();
  }

  /**
   * Constructs a new exception with the specified detail message. The cause is not initialized, and may subsequently be
   * initialized by a call to {@link #initCause}.
   *
   * @param message
   *          the detail message. The detail message is saved for later retrieval by the {@link #getMessage()} method.
   */
  public ObjectFactoryException( final String message ) {
    super( message );
  }

  /**
   * Creates a new exception.
   *
   * @param message
   *          the message.
   * @param cause
   *          the cause of the exception.
   */
  public ObjectFactoryException( final String message, final Exception cause ) {
    super( message, cause );
  }
}
