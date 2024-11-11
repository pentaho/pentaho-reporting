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


package org.pentaho.reporting.libraries.fonts.text.whitespace;

import org.pentaho.reporting.libraries.fonts.text.ClassificationProducer;

/**
 * Creation-Date: 11.06.2006, 20:18:00
 *
 * @author Thomas Morgner
 */
public class PreserveBreaksWhiteSpaceFilter implements WhiteSpaceFilter {
  private boolean collapse;

  public PreserveBreaksWhiteSpaceFilter() {
  }

  /**
   * Reset the filter to the same state as if the filter had been constructed but not used yet.
   */
  public void reset() {
    collapse = false;
  }

  /**
   * Filters the whitespaces. This method returns '-1', if the whitespace should be removed from the stream; otherwise
   * it presents a replacement character. If the codepoint is no whitespace at all, the codepoint is returned
   * unchanged.
   *
   * @param codepoint
   * @return
   */
  public int filter( final int codepoint ) {
    if ( isLinebreak( codepoint ) ) {
      collapse = true;
      return codepoint;
    }

    if ( isWhitespace( codepoint ) ) {
      if ( collapse == true ) {
        return WhiteSpaceFilter.STRIP_WHITESPACE;
      } else {
        collapse = true;
        return ' ';
      }
    }

    if ( codepoint == ClassificationProducer.START_OF_TEXT ) {
      collapse = true;
      return WhiteSpaceFilter.STRIP_WHITESPACE;
    } else if ( codepoint == ClassificationProducer.END_OF_TEXT ) {
      return WhiteSpaceFilter.STRIP_WHITESPACE;
    }

    collapse = false;
    return codepoint;
  }

  private boolean isWhitespace( final int codepoint ) {
    final char ch = (char) ( codepoint & 0xFFFF );
    return Character.isWhitespace( ch );
  }

  protected boolean isLinebreak( final int codepoint ) {
    if ( codepoint == 0xa || codepoint == 0xd ) {
      return true;
    } else {
      return false;
    }
  }


  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

}
