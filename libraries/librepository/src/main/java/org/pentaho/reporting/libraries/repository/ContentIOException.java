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

package org.pentaho.reporting.libraries.repository;

/**
 * A Exception that indicates repository related errors.
 *
 * @author Thomas Morgner
 */
public class ContentIOException extends Exception {
  private static final long serialVersionUID = 2947843470210463760L;

  /**
   * Creates a ContentIOException with no message and no parent.
   */
  public ContentIOException() {
  }

  /**
   * Creates an ContentIOException.
   *
   * @param message the exception message.
   * @param ex      the parent exception.
   */
  public ContentIOException( final String message, final Exception ex ) {
    super( message, ex );
  }

  /**
   * Creates an ContentIOException.
   *
   * @param message the exception message.
   */
  public ContentIOException( final String message ) {
    super( message );
  }
}
