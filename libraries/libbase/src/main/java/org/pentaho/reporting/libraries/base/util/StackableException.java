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
 * A baseclass for exceptions, which could have parent exceptions. These parent exceptions are raised in a subclass and
 * are now wrapped into a subclass of this Exception.
 * <p/>
 * The parents are printed when this exception is printed. This class exists mainly for debugging reasons, as with them
 * it is easier to detect the root cause of an error.
 * <p/>
 * <!-- In a perfect world there would be no need for such a class :)-->
 *
 * @author Thomas Morgner
 * @noinspection UseOfSystemOutOrSystemErr
 * @deprecated Use ordinary exception as your base class.
 */
public class StackableException extends Exception {
  private static final long serialVersionUID = -8649054607849486694L;

  /**
   * The parent exception.
   */
  private Throwable parent;
  private String message;

  /**
   * Creates a StackableRuntimeException with no message and no parent.
   */
  protected StackableException() {
    super();
  }

  /**
   * Creates an exception.
   *
   * @param message the exception message.
   * @param ex      the parent exception.
   */
  protected StackableException( final String message, final Throwable ex ) {
    super();
    this.message = message;
    this.parent = ex;
  }

  /**
   * Creates an exception.
   *
   * @param message the exception message.
   * @param ex      the parent exception.
   * @deprecated use the throwable-version instead.
   */
  protected StackableException( final String message, final Exception ex ) {
    super( message );
    this.parent = ex;
    this.message = message;
  }

  /**
   * Creates an exception.
   *
   * @param message the exception message.
   */
  protected StackableException( final String message ) {
    super();
    this.message = message;
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

  protected void update( final String message, final Throwable parent ) {
    this.message = message;
    this.parent = parent;
  }

  public Throwable getParentThrowable() {
    return parent;
  }

  /**
   * Returns the detail message string of this throwable.
   *
   * @return the detail message string of this <tt>Throwable</tt> instance (which may be <tt>null</tt>).
   */
  public String getMessage() {
    return message;
  }

  /**
   * Returns a short description of this throwable. If this <code>Throwable</code> object was created with a non-null
   * detail message string, then the result is the concatenation of three strings: <ul> <li>The name of the actual class
   * of this object <li>": " (a colon and a space) <li>The result of the {@link #getMessage} method for this object
   * </ul> If this <code>Throwable</code> object was created with a <tt>null</tt> detail message string, then the name
   * of the actual class of this object is returned.
   *
   * @return a string representation of this throwable.
   */
  public String toString() {
    final String s = getClass().getName();
    final String message = getLocalizedMessage();
    return ( message != null ) ? ( s + ": " + message ) : s;
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
   * Prints this <code>Throwable</code> and its backtrace to the standard error stream. This method prints a stack trace
   * for this <code>Throwable</code> object on the error output stream that is the value of the field
   * <code>System.err</code>. The first line of output contains the result of the {@link #toString()} method for this
   * object. Remaining lines represent data previously recorded by the method {@link #fillInStackTrace()}. The format of
   * this information depends on the implementation, but the following example may be regarded as typical:
   * <blockquote><pre>
   * java.lang.NullPointerException
   *         at MyClass.mash(MyClass.java:9)
   *         at MyClass.crunch(MyClass.java:6)
   *         at MyClass.main(MyClass.java:3)
   * </pre></blockquote>
   * This example was produced by running the program:
   * <blockquote><pre>
   * <p/>
   * class MyClass {
   * <p/>
   *     public static void main(String[] argv) {
   *         crunch(null);
   *     }
   *     static void crunch(int[] a) {
   *         mash(a);
   *     }
   * <p/>
   *     static void mash(int[] b) {
   *         System.out.println(b[0]);
   *     }
   * }
   * </pre></blockquote>
   *
   * @see System#err
   */
  public void printStackTrace() {
    synchronized( System.err ) {
      printStackTrace( System.err );
    }
  }
}
