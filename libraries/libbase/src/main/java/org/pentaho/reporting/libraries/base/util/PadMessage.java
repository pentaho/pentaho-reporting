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
