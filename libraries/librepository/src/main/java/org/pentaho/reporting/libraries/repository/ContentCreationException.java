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
 * A exception that indicates that a new ContentEntry could not be created in the repository.
 *
 * @author Thomas Morgner
 */
public class ContentCreationException extends ContentIOException {
  private static final long serialVersionUID = 8817127426694197164L;

  /**
   * Creates a ContentCreationException with no message and no parent.
   */
  public ContentCreationException() {
  }

  /**
   * Creates an ContentCreationException.
   *
   * @param message the exception message.
   * @param ex      the parent exception.
   */
  public ContentCreationException( final String message, final Exception ex ) {
    super( message, ex );
  }

  /**
   * Creates an ContentCreationException.
   *
   * @param message the exception message.
   */
  public ContentCreationException( final String message ) {
    super( message );
  }
}
