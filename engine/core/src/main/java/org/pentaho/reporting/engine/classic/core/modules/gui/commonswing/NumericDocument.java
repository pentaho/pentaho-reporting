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
