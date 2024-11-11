/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


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
