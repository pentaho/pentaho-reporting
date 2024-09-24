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
