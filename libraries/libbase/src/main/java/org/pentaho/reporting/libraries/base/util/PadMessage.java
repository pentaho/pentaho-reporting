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

import java.util.Arrays;

/**
 * A message object that pads the output if the text is shorter than the given length. This is usefull when concating
 * multiple messages, which should appear in a table like style.
 *
 * @author Thomas Morgner
 */
public class PadMessage {

  /**
   * The message.
   */
  private final Object text;

  /**
   * The padding size.
   */
  private final int length;

  /**
   * Creates a new message.
   *
   * @param message the message.
   * @param length  the padding size.
   */
  public PadMessage( final Object message, final int length ) {
    this.text = message;
    this.length = length;
  }

  /**
   * Returns a string representation of the message.
   *
   * @return the string.
   */
  public String toString() {
    final StringBuilder b = new StringBuilder( length );
    b.append( this.text );
    if ( b.length() < this.length ) {
      final char[] pad = new char[ this.length - b.length() ];
      Arrays.fill( pad, ' ' );
      b.append( pad );
    }
    return b.toString();
  }

}
