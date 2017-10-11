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

package org.pentaho.reporting.libraries.formula;

/**
 * Creation-Date: 31.10.2006, 14:15:29
 *
 * @author Thomas Morgner
 */
public class EvaluationException extends Exception {
  private static ThreadLocal localInstance = new ThreadLocal();

  private ErrorValue errorValue;
  private static final long serialVersionUID = 5627076786508932648L;

  /**
   * Returns the detail message string of this throwable.
   *
   * @return the detail message string of this <tt>Throwable</tt> instance (which may be <tt>null</tt>).
   */
  public String getMessage() {
    return String.valueOf( errorValue );
  }

  /**
   * Creates a StackableRuntimeException with no message and no parent.
   *
   * @param errorValue the error value that caused this exception.
   */
  public EvaluationException( final ErrorValue errorValue ) {
    this.errorValue = errorValue;
  }


  protected void updateErrorValue( final ErrorValue errorValue ) {
    this.errorValue = errorValue;
  }

  public ErrorValue getErrorValue() {
    return errorValue;
  }

  public static EvaluationException getInstance( final ErrorValue errorValue ) {
    final EvaluationException o = (EvaluationException) localInstance.get();
    if ( o == null ) {
      final EvaluationException retval = new EvaluationException( errorValue );
      localInstance.set( retval );
      return retval;
    }

    o.fillInStackTrace();
    o.updateErrorValue( errorValue );
    return o;
  }
}
