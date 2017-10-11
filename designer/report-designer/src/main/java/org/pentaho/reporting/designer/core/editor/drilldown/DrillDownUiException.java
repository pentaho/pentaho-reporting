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

package org.pentaho.reporting.designer.core.editor.drilldown;

/**
 * Todo: Document me!
 * <p/>
 * Date: 05.08.2010 Time: 14:10:49
 *
 * @author Thomas Morgner.
 */
public class DrillDownUiException extends Exception {
  /**
   * Constructs a new exception with <code>null</code> as its detail message. The cause is not initialized, and may
   * subsequently be initialized by a call to {@link #initCause}.
   */
  public DrillDownUiException() {
  }

  /**
   * Constructs a new exception with the specified detail message.  The cause is not initialized, and may subsequently
   * be initialized by a call to {@link #initCause}.
   *
   * @param message the detail message. The detail message is saved for later retrieval by the {@link #getMessage()}
   *                method.
   */
  public DrillDownUiException( final String message ) {
    super( message );
  }

  /**
   * Constructs a new exception with the specified detail message and cause.  <p>Note that the detail message associated
   * with <code>cause</code> is <i>not</i> automatically incorporated in this exception's detail message.
   *
   * @param message the detail message (which is saved for later retrieval by the {@link #getMessage()} method).
   * @param cause   the cause (which is saved for later retrieval by the {@link #getCause()} method).  (A <tt>null</tt>
   *                value is permitted, and indicates that the cause is nonexistent or unknown.)
   * @since 1.4
   */
  public DrillDownUiException( final String message, final Throwable cause ) {
    super( message, cause );
  }

  /**
   * Constructs a new exception with the specified cause and a detail message of <tt>(cause==null ? null :
   * cause.toString())</tt> (which typically contains the class and detail message of <tt>cause</tt>). This constructor
   * is useful for exceptions that are little more than wrappers for other throwables (for example, {@link
   * java.security.PrivilegedActionException}).
   *
   * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method).  (A <tt>null</tt>
   *              value is permitted, and indicates that the cause is nonexistent or unknown.)
   * @since 1.4
   */
  public DrillDownUiException( final Throwable cause ) {
    super( cause );
  }
}
