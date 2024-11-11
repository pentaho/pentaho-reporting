/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.libraries.xmlns.parser;

import org.xml.sax.Locator;

public class NoRootHandlerException extends ParseException {
  /**
   * Creates a new ParseException with the given message.
   *
   * @param message the message
   */
  public NoRootHandlerException( final String message ) {
    super( message );
  }

  /**
   * Creates a new ParseException with the given root exception.
   *
   * @param e the exception
   */
  public NoRootHandlerException( final Exception e ) {
    super( e );
  }

  /**
   * Creates a new ParseException with the given message and root exception.
   *
   * @param message the message
   * @param e       the exception
   */
  public NoRootHandlerException( final String message, final Exception e ) {
    super( message, e );
  }

  /**
   * Creates a new ParseException with the given message and the locator.
   *
   * @param message the message
   * @param locator the locator of the parser
   */
  public NoRootHandlerException( final String message, final Locator locator ) {
    super( message, locator );
  }

  /**
   * Creates a new ParseException with the given root exception and the locator.
   *
   * @param e       the exception
   * @param locator the locator of the parser
   */
  public NoRootHandlerException( final Exception e, final Locator locator ) {
    super( e, locator );
  }

  /**
   * Creates a new ParseException with the given message, root exception and the locator.
   *
   * @param message the message
   * @param e       the exception
   * @param locator the locator of the parser
   */
  public NoRootHandlerException( final String message, final Exception e, final Locator locator ) {
    super( message, e, locator );
  }
}
