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

package org.pentaho.reporting.libraries.fonts.itext;

/**
 * The BaseFontCreateException is thrown if there are problemns while creating iText fonts.
 *
 * @author Thomas Morgner
 */
public class BaseFontCreateException extends RuntimeException {
  /**
   * Creates a new BaseFontCreateException with no message.
   */
  public BaseFontCreateException() {
  }

  /**
   * Creates a new BaseFontCreateException with the given message and base exception.
   *
   * @param s the message for this exception
   * @param e the exception that caused this exception.
   */
  public BaseFontCreateException( final String s, final Exception e ) {
    super( s, e );
  }

  /**
   * Creates a new BaseFontCreateException with the given message.
   *
   * @param s the message for this exception
   */
  public BaseFontCreateException( final String s ) {
    super( s );
  }
}
