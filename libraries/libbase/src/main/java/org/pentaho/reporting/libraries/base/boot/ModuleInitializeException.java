/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.libraries.base.boot;

/**
 * This exception is thrown when the module initialization encountered an unrecoverable error which prevents the module
 * from being used.
 *
 * @author Thomas Morgner
 */
public class ModuleInitializeException extends Exception {
  /**
   * A serialization related constant.
   */
  private static final long serialVersionUID = -8742325619631583144L;

  /**
   * Creates a ModuleInitializeException with no message and no base exception.
   */
  public ModuleInitializeException() {
    // nothing required
  }

  /**
   * Creates a ModuleInitializeException with the given message and base exception.
   *
   * @param s the message
   * @param e the root exception
   */
  public ModuleInitializeException( final String s, final Throwable e ) {
    super( s, e );
  }

  /**
   * Creates a ModuleInitializeException with the given message and no base exception.
   *
   * @param s the exception message
   */
  public ModuleInitializeException( final String s ) {
    super( s );
  }

}
