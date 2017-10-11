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

package org.pentaho.reporting.engine.classic.core.modules.gui.commonswing;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * Creation-Date: 29.10.2007, 18:44:24
 *
 * @author Thomas Morgner
 */
public class NumericDocument extends PlainDocument {
  private StringBuffer buffer;

  public NumericDocument() {
    this.buffer = new StringBuffer();
  }

  public void insertString( final int offs, final String str, final AttributeSet a ) throws BadLocationException {
    buffer.delete( 0, buffer.length() );
    final char[] chars = str.toCharArray();
    for ( int i = 0; i < chars.length; i++ ) {
      final char aChar = chars[i];
      if ( Character.isDigit( aChar ) ) {
        buffer.append( aChar );
      }
    }
    if ( buffer.length() > 0 ) {
      super.insertString( offs, buffer.toString(), a );
    }
  }

  public void replace( final int offset, final int length, final String text, final AttributeSet attrs )
    throws BadLocationException {
    buffer.delete( 0, buffer.length() );
    final char[] chars = text.toCharArray();
    for ( int i = 0; i < chars.length; i++ ) {
      final char aChar = chars[i];
      if ( Character.isDigit( aChar ) ) {
        buffer.append( aChar );
      }
    }
    if ( buffer.length() > 0 ) {
      // for JDK 1.2 compatiblity we cannot do this as a single operation, as the replace method did
      // not exist until JDK 1.4
      super.remove( offset, length );
      super.insertString( offset, buffer.toString(), attrs );
    }
  }
}
