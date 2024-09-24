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

package org.pentaho.reporting.engine.classic.core;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;

/**
 * The ReportEventException is thrown, if there were unrecoverable exceptions during the report processing.
 *
 * @author Thomas Morgner
 */
public class ReportEventException extends ReportProcessingException {
  /**
   * the collected child exceptions.
   */
  private List childExceptions;

  /**
   * Creates an ReportEventException to handle exceptions, that occured during the event dispatching.
   *
   * @param message
   *          the exception message.
   * @param childExceptions
   *          the collected exceptions.
   */
  public ReportEventException( final String message, final List childExceptions ) {
    super( message );
    if ( childExceptions == null ) {
      throw new NullPointerException();
    }
    this.childExceptions = Collections.unmodifiableList( childExceptions );
  }

  /**
   * Gets the collected child exceptions, that occured during the event dispatching.
   *
   * @return the collected child exceptions.
   */
  public List getChildExceptions() {
    return childExceptions;
  }

  /**
   * Returns the errort message string of this throwable object.
   *
   * @return the error message string of this <code>Throwable</code> object if it was created with an error message
   *         string; or <code>null</code> if it was created with no error message.
   */
  public String getMessage() {
    return super.getMessage() + ": " + childExceptions.size() + " exceptions occured.";
  }

  /**
   * Prints the stack trace to the specified writer.
   *
   * @param writer
   *          the writer.
   */
  public void printStackTrace( final PrintWriter writer ) {
    super.printStackTrace( writer );
    for ( int i = 0; i < childExceptions.size(); i++ ) {
      writer.print( "Exception #" );
      writer.println( i );
      final Exception ex = (Exception) childExceptions.get( i );
      if ( ex != null ) {
        ex.printStackTrace( writer );
      } else {
        writer.println( "<not defined>" );
      }
    }
  }

  /**
   * Prints the stack trace to the specified stream.
   *
   * @param stream
   *          the output stream.
   */
  public void printStackTrace( final PrintStream stream ) {
    super.printStackTrace( stream );
    for ( int i = 0; i < childExceptions.size(); i++ ) {
      stream.print( "Exception #" );
      stream.println( i );
      final Exception ex = (Exception) childExceptions.get( i );
      if ( ex != null ) {
        ex.printStackTrace( stream );
      } else {
        stream.println( "<not defined>" );
      }
    }
  }
}
