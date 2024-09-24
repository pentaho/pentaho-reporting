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
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.libraries.xmlns.parser;

import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * A parse exception. This does the same as the SAXParseException, but it also prints the parent exception.
 *
 * @author Thomas Morgner
 */
public class ParseException extends SAXException {

  /**
   * The line, where the error occured.
   */
  private int line;

  /**
   * The column, where the error occured.
   */
  private int column;

  private boolean noOwnMessage;
  private static final long serialVersionUID = 1188118105409903580L;

  /**
   * Creates a new ParseException with the given message.
   *
   * @param message the message
   */
  public ParseException( final String message ) {
    super( message );
    fillLocation( null );
    noOwnMessage = ( message == null );
  }

  /**
   * Creates a new ParseException with the given root exception.
   *
   * @param e the exception
   */
  public ParseException( final Exception e ) {
    super( e );
    fillLocation( null );
    noOwnMessage = true;
  }

  /**
   * Creates a new ParseException with the given message and root exception.
   *
   * @param message the message
   * @param e       the exception
   */
  public ParseException( final String message, final Exception e ) {
    super( message, e );
    fillLocation( null );
    noOwnMessage = ( message == null );
  }

  /**
   * Creates a new ParseException with the given message and the locator.
   *
   * @param message the message
   * @param locator the locator of the parser
   */
  public ParseException( final String message, final Locator locator ) {
    super( message );
    fillLocation( locator );
    noOwnMessage = ( message == null );
  }

  /**
   * Creates a new ParseException with the given root exception and the locator.
   *
   * @param e       the exception
   * @param locator the locator of the parser
   */
  public ParseException( final Exception e, final Locator locator ) {
    super( e );
    fillLocation( locator );
    noOwnMessage = true;
  }

  /**
   * Creates a new ParseException with the given message, root exception and the locator.
   *
   * @param message the message
   * @param e       the exception
   * @param locator the locator of the parser
   */
  public ParseException( final String message,
                         final Exception e,
                         final Locator locator ) {
    super( message, e );
    fillLocation( locator );
    noOwnMessage = ( message == null );
  }

  /**
   * Modifies the message to give more detailed location information.
   *
   * @return the modified exception message.
   */
  public String getMessage() {
    if ( noOwnMessage ) {
      final Exception parentEx = getException();
      if ( parentEx instanceof ParseException ) {
        return parentEx.getMessage();
      } else {
        final StringBuffer message = new StringBuffer
          ( String.valueOf( parentEx.getMessage() ) );
        message.append( " [Location: Line=" );
        message.append( this.line );
        message.append( " Column=" );
        message.append( this.column );
        message.append( "] " );
        return message.toString();
      }
    } else {
      final StringBuffer message = new StringBuffer
        ( String.valueOf( super.getMessage() ) );
      message.append( " [Location: Line=" );
      message.append( this.line );
      message.append( " Column=" );
      message.append( this.column );
      message.append( "] " );
      return message.toString();
    }
  }

  /**
   * Fills the location with the given locator.
   *
   * @param locator the locator or null.
   */
  protected void fillLocation( final Locator locator ) {
    if ( locator == null ) {
      this.line = -1;
      this.column = -1;
    } else {
      this.line = locator.getLineNumber();
      this.column = locator.getColumnNumber();
    }
  }

  /**
   * Returns the line of the parse position where the error occured.
   *
   * @return the line number or -1 if not known.
   */
  public int getLine() {
    return this.line;
  }

  /**
   * Returns the column of the parse position where the error occured.
   *
   * @return the column number or -1 if not known.
   */
  public int getColumn() {
    return this.column;
  }


  /**
   * Prints the stack trace to the specified stream.
   *
   * @param stream the output stream.
   */
  public void printStackTrace( final PrintStream stream ) {
    super.printStackTrace( stream );
    if ( getException() != null ) {
      stream.println( "ParentException: " );
      getException().printStackTrace( stream );
    }
  }

  /**
   * Override toString to pick up any embedded exception.
   *
   * @return A string representation of this exception.
   */
  public String toString() {
    return getClass().getName() + ": " + getMessage();
  }

  /**
   * Prints the stack trace to the specified writer.
   *
   * @param writer the writer.
   */
  public void printStackTrace( final PrintWriter writer ) {
    super.printStackTrace( writer );
    if ( getException() != null ) {
      writer.println( "ParentException: " );
      getException().printStackTrace( writer );
    }
  }

}
