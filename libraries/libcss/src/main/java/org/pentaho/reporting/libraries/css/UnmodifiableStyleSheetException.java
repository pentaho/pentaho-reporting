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

package org.pentaho.reporting.libraries.css;

/**
 * Creation-Date: 01.12.2005, 20:59:34
 *
 * @author Thomas Morgner
 */
public class UnmodifiableStyleSheetException extends RuntimeException {
  /**
   * Constructs a new runtime exception with <code>null</code> as its detail message.  The cause is not initialized, and
   * may subsequently be initialized by a call to {@link #initCause}.
   */
  public UnmodifiableStyleSheetException() {
  }

  /**
   * Constructs a new runtime exception with the specified detail message. The cause is not initialized, and may
   * subsequently be initialized by a call to {@link #initCause}.
   *
   * @param message the detail message. The detail message is saved for later retrieval by the {@link #getMessage()}
   *                method.
   */
  public UnmodifiableStyleSheetException( String message ) {
    super( message );
  }
}
