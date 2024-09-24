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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.resourceloader;

/**
 * This exception is thrown whenever a resource-manager tries to load data using an invalid key.
 *
 * @author Thomas Morgner
 */
public class UnrecognizedLoaderException extends ResourceLoadingException {
  private static final long serialVersionUID = 6319955849184970434L;

  /**
   * Creates a StackableRuntimeException with no message and no parent.
   */
  public UnrecognizedLoaderException() {
  }

  /**
   * Creates an exception.
   *
   * @param message the exception message.
   * @param ex      the parent exception.
   */
  public UnrecognizedLoaderException( final String message, final Exception ex ) {
    super( message, ex );
  }

  /**
   * Creates an exception.
   *
   * @param message the exception message.
   */
  public UnrecognizedLoaderException( final String message ) {
    super( message );
  }
}
