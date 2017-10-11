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

package org.pentaho.reporting.libraries.base.util;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * A baseclass for RuntimeExceptions, which could have parent exceptions. These parent exceptions are raised in a
 * subclass and are now wrapped into a subclass of this Exception.
 * <p/>
 * The parents are printed when this exception is printed. This class exists mainly for debugging reasons, as with them
 * it is easier to detect the root cause of an error.
 *
 * @author Thomas Morgner
 * @deprecated use RuntimeExpression instead.
 */
public class StackableRuntimeException extends RuntimeException {

  /**
   * The parent exception.
   */
  private Throwable parent;
  private static final long serialVersionUID = -4378774171699885841L;

  /**
   * Creates a StackableRuntimeException with no message and no parent.
   */
  public StackableRuntimeException() {
    super();
  }

  /**
   * Creates an exception.
   *
   * @param message the exception message.
   * @param ex      the parent exception.
   */
  public StackableRuntimeException( final String message, final Throwable ex ) {
    super( message );
    this.parent = ex;
  }

  /**
   * Creates an exception.
   *
   * @param message the exception message.
   * @param ex      the parent exception.
   */
  public StackableRuntimeException( final String message, final Exception ex ) {
    super( message );
    this.parent = ex;
  }

  /**
   * Creates an exception.
   *
   * @param message the exception message.
   */
  public StackableRuntimeException( final String message ) {
    super( message );
  }

  /**
   * Returns the parent exception (possibly null).
   *
   * @return the parent exception.
   * @deprecated use the throwable instead.
   */
  public Exception getParent() {
    if ( this.parent instanceof Exception ) {
      return (Exception) this.parent;
    }
    return null;
  }

  public Throwable getParentThrowable() {
    return parent;
  }

  /**
   * Prints the stack trace to the specified stream.
   *
   * @param stream the output stream.
   */
  public void printStackTrace( final PrintStream stream ) {
    super.printStackTrace( stream );
    if ( getParentThrowable() != null ) {
      stream.println( "ParentException: " );
      getParentThrowable().printStackTrace( stream );
    }
  }

  /**
   * Prints the stack trace to the specified writer.
   *
   * @param writer the writer.
   */
  public void printStackTrace( final PrintWriter writer ) {
    super.printStackTrace( writer );
    if ( getParentThrowable() != null ) {
      writer.println( "ParentException: " );
      getParentThrowable().printStackTrace( writer );
    }
  }

  /**
   * Prints the stack trace to System.err.
   *
   * @noinspection UseOfSystemOutOrSystemErr
   */
  public void printStackTrace() {
    synchronized( System.err ) {
      printStackTrace( System.err );
    }
  }
}
