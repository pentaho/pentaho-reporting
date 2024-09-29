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


package org.pentaho.reporting.libraries.fonts.text.whitespace;

import org.pentaho.reporting.libraries.fonts.text.ClassificationProducer;

/**
 * Creation-Date: 11.06.2006, 20:11:17
 *
 * @author Thomas Morgner
 */
public class DiscardWhiteSpaceFilter implements WhiteSpaceFilter {
  public static final char ZERO_WIDTH = '\u200B';

  private boolean lastWasWhiteSpace;

  public DiscardWhiteSpaceFilter() {
  }

  /**
   * Reset the filter to the same state as if the filter had been constructed but not used yet.
   */
  public void reset() {
    lastWasWhiteSpace = false;
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
    if ( Character.isWhitespace( (char) codepoint ) ) {
      if ( lastWasWhiteSpace == false ) {
        lastWasWhiteSpace = true;
        return DiscardWhiteSpaceFilter.ZERO_WIDTH;
      }
      return WhiteSpaceFilter.STRIP_WHITESPACE;
    }
    if ( codepoint == ClassificationProducer.START_OF_TEXT ) {
      lastWasWhiteSpace = true;
      return WhiteSpaceFilter.STRIP_WHITESPACE;
    } else if ( codepoint == ClassificationProducer.END_OF_TEXT ) {
      // do not modify the whitespace flag ..
      return WhiteSpaceFilter.STRIP_WHITESPACE;
    }

    lastWasWhiteSpace = false;
    return codepoint;
  }


  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

}
